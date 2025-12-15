/*
 * @(#)2012-11-15 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.context.util.collection.List2GroupMap;
import com.wellsoft.pt.basicdata.datadict.bean.DataDictionaryAttributeBean;
import com.wellsoft.pt.basicdata.datadict.bean.DataDictionaryBean;
import com.wellsoft.pt.basicdata.datadict.bean.DataDictionaryDto;
import com.wellsoft.pt.basicdata.datadict.dao.DataDictionaryDao;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionaryAttribute;
import com.wellsoft.pt.basicdata.datadict.facade.DataDictionarySelectiveDataProvider;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryAttributeService;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.dyview.support.TreeNodeForView;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Description: 数据字典服务层实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-15.1	zhulh		2012-11-15		Create
 * </pre>
 * @date 2012-11-15
 */
@Service
public class DataDictionaryServiceImpl extends AbstractJpaServiceImpl<DataDictionary, DataDictionaryDao, String>
        implements DataDictionaryService, Select2QueryApi {

    public static final String DATA_DICT_CACHE_NAME = "Basic Data";

    // @Autowired
    // private AclService aclService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DataDictionaryAttributeService dataDictionaryAttributeService;

    @Autowired
    private CommonValidateService commonValidateService;

    @Autowired
    private DataDictionarySelectiveDataProvider dataDictionarySelectiveDataProvider;

    public static void buildChildNodes(TreeNode node, Map<String, List<DataDictionary>> ddMap, boolean data) {
        String key = node.getId();
        List<DataDictionary> ddList = ddMap.get(key);
        if (ddList == null) {
            return;
        }
        for (DataDictionary dd : ddList) {
            TreeNode child = new TreeNode();
            child.setId(dd.getUuid());
            child.setName(dd.getName());
            if (data) {
                DataDictionaryDto dto = new DataDictionaryDto();
                org.springframework.beans.BeanUtils.copyProperties(dd, dto);
                child.setData(dto);
            } else {
                child.setData(dd.getCode());
            }
            node.getChildren().add(child);
            buildChildNodes(child, ddMap, data);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadic.service.DataDictionaryService#get(java.lang.String)
     */
    @Override
    public DataDictionary get(String uid) {
        return dao.getOne(uid);
    }

    /**
     * 返回数据字典所有者在ACL中的SID
     *
     * @param dataDictionary
     * @return
     */
    private List<String> getAclSid(DataDictionary dataDictionary) {
        List<String> newOwners = new ArrayList<String>();
        if (dataDictionary.getOwners().isEmpty()) {
            // "ROLE_DATA_DIC"
            dataDictionary.getOwners().add(ACL_SID);
            return dataDictionary.getOwners();
        } else {
            List<String> owners = dataDictionary.getOwners();
            for (String owner : owners) {
                if (owner.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                    owner = "ROLE_" + owner;
                }
                newOwners.add(owner);
            }
        }
        // 返回组织部门中选择的角色作为SID
        return newOwners;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadic.service.DataDictionaryService#removeByPk(java.lang.String)
     */
    @Override
    @Transactional
    @CacheEvict(value = DATA_DICT_CACHE_NAME, allEntries = true)
    public void removeByPk(String uuid) {
        // 清理缓存
        cacheManager.getCache(ModuleID.BASIC_DATA).clear();
        this.dao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadic.service.DataDictionaryService#removeAllByPk(java.util.Collection)
     */
    @Override
    @Transactional
    @CacheEvict(value = DATA_DICT_CACHE_NAME, allEntries = true)
    public void removeAllByPk(Collection<String> uuids) {
        // 清理缓存
        cacheManager.getCache(ModuleID.BASIC_DATA).clear();
        for (String uuid : uuids) {
            this.dao.delete(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getAsTreeAsync(java.lang.String)
     */
    @Override
    public List<TreeNode> getAsTreeAsync(String uuid) {
        List<DataDictionary> list = new ArrayList<DataDictionary>();
        TreeNode node = new TreeNode();
        // 查询所有根结点
        if (TreeNode.ROOT_ID.equals(uuid)) {
            list = this.dao.getTopLevel();
        } else {
            // 查询指定结点的下一级子结点
            DataDictionary dataDictionary = this.dao.getOne(uuid);
            if (dataDictionary == null) {
                dataDictionary = dao.getByType(uuid);
            }
            if (dataDictionary != null) {
                list = Arrays.asList(dataDictionary.getChildren().toArray(new DataDictionary[0]));
            }
        }

        List<TreeNode> children = node.getChildren();
        for (DataDictionary dataDictionary : list) {
            TreeNode child = new TreeNode();
            child.setId(dataDictionary.getUuid());
            child.setName(dataDictionary.getName());
            child.setData(dataDictionary.getCode());
            child.setIsParent(dataDictionary.getChildren().size() > 0);
            child.setNocheck(dataDictionary.getChildren().size() > 0);
            children.add(child);
        }
        return children;
    }

    @Override
    public TreeNode getAllDataDicAsTree(String uuid) {
        TreeNode rootNode = new TreeNode();
        List<DataDictionary> allList = dao.listAllByOrderPage(null, "parent.uuid asc,seq asc");
        List<DataDictionary> topList = new ArrayList<DataDictionary>();
        // UUID为空返回null
        if (StringUtils.isBlank(uuid) || StringUtils.equals("undefined", uuid)) {
            return null;
        }
        if (TreeNode.ROOT_ID.equals(uuid)) {
            // 从根节点开始获取
            topList = this.dao.getTopLevel();
            rootNode.setName("数据字典");
            rootNode.setId(TreeNode.ROOT_ID);
            rootNode.setNocheck(true);
        } else {// 获取指定节点的数据
            DataDictionary obj = this.dao.getOne(uuid);
            if (null == obj) {
                obj = getByType(uuid);
            }
            rootNode.setName(obj.getName());
            rootNode.setId(obj.getUuid());
            DataDictionaryDto dto = new DataDictionaryDto();
            org.springframework.beans.BeanUtils.copyProperties(obj, dto);
            rootNode.setData(dto);
            rootNode.setNocheck(true);
            topList = this.dao.getByParent(obj);
        }

        // 将所有节点数据按上下级关系分组
        Map<String, List<DataDictionary>> ddMap = new List2GroupMap<DataDictionary>() {
            @Override
            protected String getGroupUuid(DataDictionary dd) {
                if (dd.getParent() != null) {
                    return dd.getParent().getUuid();
                }
                return null;
            }
        }.convert(allList);

        for (DataDictionary dd : topList) {
            TreeNode node = new TreeNode();
            node.setId(dd.getUuid());
            node.setName(dd.getName());
            DataDictionaryDto dto = new DataDictionaryDto();
            org.springframework.beans.BeanUtils.copyProperties(dd, dto);
            node.setData(dto);
            // 生成子结点
            buildChildNodes(node, ddMap, true);
            rootNode.getChildren().add(node);
        }
        return rootNode;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getAllAsTree(java.lang.String)
     */
    @Override
    public TreeNode getAllAsTree(String uuid) {
        TreeNode rootNode = new TreeNode();
        List<DataDictionary> allList = dao.listAllByOrderPage(null, "parent.uuid asc,seq asc");
        List<DataDictionary> topList = new ArrayList<DataDictionary>();
        // 从根节点开始获取
        if (TreeNode.ROOT_ID.equals(uuid)) {
            topList = this.dao.getTopLevel();
            rootNode.setName("数据字典");
            rootNode.setId(TreeNode.ROOT_ID);
            rootNode.setNocheck(true);
        } else {// 获取指定节点的数据
            DataDictionary obj = this.dao.getOne(uuid);
            rootNode.setName(obj.getName());
            rootNode.setId(obj.getUuid());
            rootNode.setData(obj.getCode());
            rootNode.setNocheck(true);
            topList = this.dao.getByParent(obj);
        }

        // 将所有节点数据按上下级关系分组
        Map<String, List<DataDictionary>> ddMap = new List2GroupMap<DataDictionary>() {
            @Override
            protected String getGroupUuid(DataDictionary dd) {
                if (dd.getParent() != null) {
                    return dd.getParent().getUuid();
                }
                return null;
            }
        }.convert(allList);

        for (DataDictionary dd : topList) {
            TreeNode node = new TreeNode();
            node.setId(dd.getUuid());
            node.setName(dd.getName());
            node.setData(dd.getCode());
            // 生成子结点
            buildChildNodes(node, ddMap, false);
            rootNode.getChildren().add(node);
        }
        return rootNode;
    }

    /**
     * 异步加载树形结点，维护时不需要考虑ACL权限(视图专用)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getAsTreeAsyncForView(java.lang.String)
     */
    @Override
    public List<TreeNode> getAsTreeAsyncForView(String uuid) {
        List<DataDictionary> list = new ArrayList<DataDictionary>();
        TreeNodeForView node = new TreeNodeForView();
        // 查询所有根结点
        if (TreeNode.ROOT_ID.equals(uuid)) {
            list = this.dao.getTopLevel();
        } else {
            // 查询指定结点的下一级子结点
            DataDictionary dataDictionary = this.dao.getOne(uuid);
            if (dataDictionary != null) {
                list = Arrays.asList(dataDictionary.getChildren().toArray(new DataDictionary[0]));
            }
        }
        List<TreeNode> children = node.getChildren();
        for (DataDictionary dataDictionary : list) {
            TreeNodeForView child = new TreeNodeForView();
            List<DataDictionaryAttribute> attributes = dataDictionary.getAttributes();
            for (DataDictionaryAttribute a : attributes) {
                child.setAttribute(a.getValue());
            }
            child.setId(dataDictionary.getUuid());
            child.setName(dataDictionary.getName());
            child.setData(dataDictionary.getCode());
            child.setIsParent(dataDictionary.getChildren().size() > 0);
            child.setNocheck(dataDictionary.getChildren().size() > 0);
            children.add(child);
        }
        return children;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getFromTypeAsTreeAsync(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getFromTypeAsTreeAsync(String uuid, String type) {
        String rootUuid = uuid;
        if (TreeNode.ROOT_ID.equals(uuid)) {
            DataDictionary dataDictionary = this.getByType(type);
            if (dataDictionary != null) {
                rootUuid = dataDictionary.getUuid();
            } else {
                TreeNode node = new TreeNode();
                return node.getChildren();
            }
        }
        return getAsTreeAsync(rootUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getFromTypeAsTreeAsync(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getViewTypeAsTreeAsync(String uuid, String type) {
        String rootUuid = uuid;
        if (TreeNode.ROOT_ID.equals(uuid)) {
            DataDictionary dataDictionary = this.getByType(type);
            if (dataDictionary != null) {
                rootUuid = dataDictionary.getUuid();
            } else {
                TreeNode node = new TreeNode();
                return node.getChildren();
            }
        }
        return getAsTreeAsyncForView(rootUuid);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getBean
     * (java.lang.String)
     */
    @Override
    public DataDictionaryBean getBean(String uuid) {
        DataDictionary dataDictionary = this.dao.getOne(uuid);
        if (dataDictionary == null) {
            return null;
        }
        DataDictionaryBean bean = new DataDictionaryBean();
        BeanUtils.copyProperties(dataDictionary, bean);

        // 1、设置父节点
        DataDictionary parent = dataDictionary.getParent();
        if (parent != null) {
            bean.setParentName(parent.getName());
            bean.setParentUuid(parent.getUuid());
        }

        // 2、设置子结点
        List<DataDictionary> children = dataDictionary.getChildren();
        List<DataDictionary> beanChildren = BeanUtils.convertCollection(children, DataDictionary.class);
        bean.setChildren(beanChildren);

        // 3、设置字典属性
        List<DataDictionaryAttribute> attributes = dataDictionary.getAttributes();
        List<DataDictionaryAttribute> beanAttributes = BeanUtils.convertCollection(attributes,
                DataDictionaryAttribute.class);
        bean.setAttributes(beanAttributes);

        // 4、设置所有者
        // List<AclSid> aclSids = aclService.getSid(dataDictionary);
        // List<String> sids = new ArrayList<String>();
        // for (AclSid sid : aclSids) {
        // if (ACL_SID.equals(sid.getSid())) {
        // continue;
        // }
        // sids.add(sid.getSid());
        // }
        // StringBuilder ownerIds = new StringBuilder();
        // StringBuilder ownerNames = new StringBuilder();
        // Iterator<String> it = sids.iterator();
        // while (it.hasNext()) {
        // String sid = it.next();
        // if (sid.startsWith(IdPrefix.USER.getValue())) {
        // User user = orgApiFacade.getUserById(sid);
        // ownerIds.append(user.getId());
        // ownerNames.append(user.getUserName());
        // } else if (sid.startsWith(IdPrefix.ROLE.getValue())) {
        // sid = sid.substring(IdPrefix.ROLE.getName().length() + 1);
        // Department department = orgApiFacade.getDepartmentById(sid);
        // if (department != null) {
        // ownerIds.append(department.getId());
        // ownerNames.append(department.getName());
        // }
        // }
        // if (it.hasNext()) {
        // ownerIds.append(Separator.SEMICOLON.getValue());
        // ownerNames.append(Separator.SEMICOLON.getValue());
        // }
        // }
        // bean.setOwnerIds(ownerIds.toString());
        // bean.setOwnerNames(ownerNames.toString());

        return bean;
    }

    private boolean isModify(String oldValue, String newValue) {
        if (StringUtils.isNotBlank(oldValue) && !oldValue.equals(newValue)) {
            return true;
        }
        if (StringUtils.isBlank(oldValue) && StringUtils.isNotBlank(newValue)) {
            return true;
        }
        return false;

    }

    /*
     * 保存数据字典
     *
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#saveBean
     * (com.wellsoft.pt.basicdata.datadict.bean.DataDictionaryBean)
     */
    @Override
    @Transactional
    @CacheEvict(value = DATA_DICT_CACHE_NAME, allEntries = true)
    public String saveBean(DataDictionaryBean bean) {
        DataDictionary dataDictionary = new DataDictionary();

        if (StringUtils.isNotBlank(bean.getUuid())) {
            dataDictionary = this.dao.getOne(bean.getUuid());
            // 类型非空唯一性判断
            if (StringUtils.isNotBlank(bean.getType())
                    && !commonValidateService.checkUnique(bean.getUuid(), "dataDictionary", "type", bean.getType())) {
                throw new RuntimeException("已经存在类型为[" + bean.getType() + "]的字典!");
            }
        } else if (StringUtils.isNotBlank(bean.getType())
                && commonValidateService.checkExists("dataDictionary", "type", bean.getType())) {
            // 类型非空唯一性判断
            throw new RuntimeException("已经存在类型为[" + bean.getType() + "]的字典!");
        }

        BeanUtils.copyProperties(bean, dataDictionary);
        // 1、设置父节点
        if (StringUtils.isNotBlank(bean.getParentUuid())) {
            dataDictionary.setParent(this.dao.getOne(bean.getParentUuid()));
        }

        if (!dataDictionary.isEditable()) {
            throw new RuntimeException("该字典不允许修改!");
        }
        if (dataDictionary.getParent() != null && !dataDictionary.getParent().isChildEditable()) {
            throw new RuntimeException("字典[" + dataDictionary.getParent().getName() + "]的子项不允许变更!");
        }

        // 2、设置所有者
        if (StringUtils.isNotBlank(bean.getOwnerIds())) {
            String[] ownerIds = StringUtils.split(bean.getOwnerIds(), Separator.SEMICOLON.getValue());
            dataDictionary.setOwners(Arrays.asList(ownerIds));
        }

        this.dao.save(dataDictionary);
        // 清理缓存
        this.dataDictionarySelectiveDataProvider.clear(dataDictionary.getType());

        // 3、保存子结点
        for (DataDictionaryBean child : bean.getDeletedChildren()) {
            if (StringUtils.isNotBlank(child.getUuid())) {
                if (!dataDictionary.isChildEditable()) {
                    throw new RuntimeException("不允许变更该字典的子项!");
                }
                this.remove(child.getUuid());
            }
        }
        List<DataDictionaryBean> children = bean.getChangedChildren();
        for (DataDictionaryBean child : children) {
            if (StringUtils.isNotBlank(child.getUuid())) {
                // 类型非空唯一性判断
                if (StringUtils.isNotBlank(child.getType())
                        && !commonValidateService.checkUnique(child.getUuid(), "dataDictionary", "type",
                        child.getType())) {
                    throw new RuntimeException("已经存在类型为[" + child.getType() + "]的字典!");
                }
                DataDictionary datadict = this.dao.getOne(child.getUuid());
                if (isModify(datadict.getType(), child.getType()) || isModify(datadict.getCode(), child.getCode())
                        || isModify(datadict.getName(), child.getName()) || datadict.getSeq() != child.getSeq()) {
                    if (!dataDictionary.isChildEditable()) {
                        throw new RuntimeException("不允许变更该字典的子项!");
                    }
                    if (!datadict.isEditable()) {
                        throw new RuntimeException("该字典列表项[" + child.getName() + "]不允许修改!");
                    }
                    BeanUtils.copyProperties(child, datadict);
                    this.dao.save(datadict);
                }
                // 清理缓存
                this.dataDictionarySelectiveDataProvider.clear(datadict.getType());
            } else {
                // 类型非空唯一性判断
                if (StringUtils.isNotBlank(child.getType())
                        && commonValidateService.checkExists("dataDictionary", "type", child.getType())) {
                    throw new RuntimeException("已经存在类型为[" + child.getType() + "]的字典!");
                }
                if (!dataDictionary.isChildEditable()) {
                    throw new RuntimeException("不允许变更该字典的子项!");
                }
                DataDictionary dataDictionaryModel = new DataDictionary();
                BeanUtils.copyProperties(child, dataDictionaryModel);
                dataDictionaryModel.setUuid(null);
                dataDictionaryModel.setParent(dataDictionary);
                this.dao.save(dataDictionaryModel);
            }
        }

        // 4、保存字典属性
        for (DataDictionaryAttribute attribute : bean.getDeletedAttributes()) {
            if (StringUtils.isNotBlank(attribute.getUuid())) {
                dataDictionaryAttributeService.delete(attribute.getUuid());
            }
        }
        List<DataDictionaryAttributeBean> attributeBeans = bean.getChangedAttributes();
        for (DataDictionaryAttributeBean attribute : attributeBeans) {
            if (StringUtils.isNotBlank(attribute.getUuid())) {
                DataDictionaryAttribute attributeModel = this.dataDictionaryAttributeService
                        .getOne(attribute.getUuid());
                BeanUtils.copyProperties(attribute, attributeModel);
                this.dataDictionaryAttributeService.save(attributeModel);
            } else {
                DataDictionaryAttribute attributeModel = new DataDictionaryAttribute();
                BeanUtils.copyProperties(attribute, attributeModel);
                attributeModel.setUuid(null);
                attributeModel.setDataDictionary(dataDictionary);
                this.dataDictionaryAttributeService.save(attributeModel);
            }
        }

        // 5、清理缓存
        cacheManager.getCache(ModuleID.BASIC_DATA).clear();
        return dataDictionary.getUuid();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#remove
     * (java.lang.String)
     */
    @Override
    @Transactional
    @CacheEvict(value = DATA_DICT_CACHE_NAME, allEntries = true)
    public void remove(String uuid) {
        DataDictionary datadict = this.dao.getOne(uuid);
        if (!datadict.isDeletable()) {
            throw new RuntimeException("该字典[" + datadict.getName() + "]不允许删除!");
        }
        if (datadict.getParent() != null && !datadict.getParent().isChildEditable()) {
            throw new RuntimeException("字典[" + datadict.getParent().getName() + "]不允许变更子项!");
        }
        // 清理缓存
        cacheManager.getCache(ModuleID.BASIC_DATA).clear();
        this.dao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getByType(java.lang.String)
     */
    @Override
    @Cacheable(value = DATA_DICT_CACHE_NAME)
    @Transactional
    public DataDictionary getByType(String type) {
        // 直接从数据库查，以获取所有者
        DataDictionary dataDictionary = dao.getByType(type);
        if (dataDictionary == null) {
            return null;
        }

        // 如果没有权限访问返回null
        // if (!hasPermission(dataDictionary)) {
        // return null;
        // }

        for (DataDictionaryAttribute attribute : dataDictionary.getAttributes()) {
            this.dataDictionaryAttributeService.evict(attribute);
        }
        // 转换字典属性对象
        dataDictionary.setAttributes(BeanUtils.convertCollection(dataDictionary.getAttributes(),
                DataDictionaryAttribute.class));

        return dataDictionary;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getDataDictionariesByType(java.lang.String)
     */
    @Override
    @Cacheable(value = DATA_DICT_CACHE_NAME)
    public List<DataDictionary> getDataDictionariesByType(String type) {
        DataDictionary dataDictionary = getByType(type);
        if (dataDictionary == null) {
            return new ArrayList<DataDictionary>();
        }
        return getChildrenDictionaries(dataDictionary);
    }

    @Override
    @Cacheable(value = DATA_DICT_CACHE_NAME)
    public List<DataDictionary> getDataDictionariesByTypeCode(String type, String code) {
        DataDictionary dataDictionary = null;
        if (StringUtils.isBlank(code)) {
            String[] types = type.split(";");
            if (types.length > 1) {
                List<DataDictionary> dictionaries = getDataDictionariesByTypeCode(types[0], code);
                for (int i = 1; i < types.length; i++) {
                    dictionaries.addAll(getDataDictionariesByTypeCode(types[i], code));
                }
                return dictionaries;
            } else {
                dataDictionary = getByType(type);
            }
        } else {
            List<DataDictionary> dicts = dao.getDataDictionaries(type, code);
            dataDictionary = CollectionUtils.isEmpty(dicts) ? null : dicts.get(0);
        }
        if (dataDictionary == null) {
            return new ArrayList<DataDictionary>();
        }
        return getChildrenDictionaries(dataDictionary);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getDataDictionariesByParentUuid(java.lang.String)
     */
    @Override
    @Cacheable(value = DATA_DICT_CACHE_NAME)
    public List<DataDictionary> getDataDictionariesByParentUuid(String uuid) {
        DataDictionary dataDictionary = this.get(uuid);
        if (dataDictionary == null) {
            return new ArrayList<DataDictionary>();
        }
        return getChildrenDictionaries(dataDictionary);
    }

    /**
     * 如何描述该方法
     *
     * @param list
     * @param dataDictionary
     * @return
     */
    private List<DataDictionary> getChildrenDictionaries(DataDictionary dataDictionary) {
        List<DataDictionary> list = new ArrayList<DataDictionary>();
        // 获取与判断子结点是否有访问权限
        List<DataDictionary> children = dataDictionary.getChildren();
        for (DataDictionary child : children) {
            // if (hasPermission(child)) {
            DataDictionary dictionary = new DataDictionary();
            BeanUtils.copyProperties(child, dictionary);
            for (DataDictionaryAttribute attribute : child.getAttributes()) {
                this.dataDictionaryAttributeService.evict(attribute);
            }
            // 转换字典属性对象
            dictionary.setAttributes(BeanUtils.convertCollection(child.getAttributes(), DataDictionaryAttribute.class));
            list.add(dictionary);
            // }
        }
        return list;
    }

    @Override
    public Map<String, Object> getDataDictionariesByType4View(String type) {
        List<DataDictionary> list = this.getDataDictionariesByType(type);
        Map<String, Object> map = new HashMap<String, Object>();
        for (DataDictionary dataDictionary : list) {
            map.put(dataDictionary.getCode(), dataDictionary.getName());
        }
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getKeyValuePair(java.lang.String, java.lang.String)
     */
    @Override
    public QueryItem getKeyValuePair(String sourceType, String targetCode) {
        QueryItem item = new QueryItem();
        item.put("label", sourceType);
        item.put("value", targetCode);
        if (StringUtils.isNotBlank(sourceType) && StringUtils.isNotBlank(targetCode)) {
            DataDictionary dataDictionary = this.getByType(sourceType);
            if (dataDictionary == null) {
                dataDictionary = this.get(sourceType);
            }
            if (dataDictionary != null && StringUtils.equals(dataDictionary.getCode(), targetCode)) {
                item.put("label", dataDictionary.getName());
                item.put("value", targetCode);
            } else if (dataDictionary != null) {
                String[] codes = targetCode.split(Separator.SEMICOLON.getValue());
                item.clear();
                for (int index = 0; index < codes.length; index++) {
                    if (Config.DATABASE_TYPE.startsWith("Oracle")) {
                        traverseChildren4Oracle(dataDictionary, codes[index], item, "");
                    } else {
                        traverseChildren(dataDictionary, codes[index], item, "");
                    }
                }
            }
        }
        return item;
    }

    /**
     * oracle的connect by prior实现
     *
     * @param parent
     * @param code
     * @param item
     */
    private boolean traverseChildren4Oracle(DataDictionary parent, String code, QueryItem item, String path) {
        String sql = "select t.* from cd_data_dict t where t.code = :dictCode start with t.uuid = :startWithUuid connect by nocycle prior t.uuid = t.parent_uuid";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dictCode", code);
        params.put("startWithUuid", parent.getUuid());
        List<DataDictionary> children = dao.listBySQL(sql, params);
        if (children != null && false == children.isEmpty()) {
            for (DataDictionary child : children) {
                path = child.getName();
                // 递归路径
                String parentUuid = child.getParentUuid();
                while (false == StringUtils.equals(parentUuid, parent.getUuid())) {
                    child = dao.getOne(parentUuid);
                    parentUuid = child.getParent().getUuid();
                    path = child.getName() + Separator.SLASH.getValue() + path;
                }

                if (item.get("value") == null) {
                    item.put("label", path);
                    item.put("value", code);
                } else {
                    item.put("label", item.get("label") + Separator.SEMICOLON.getValue() + path);
                    item.put("value", item.get("value") + Separator.SEMICOLON.getValue() + code);
                }
            }
        }
        return false;
    }

    /**
     * @param parent
     * @param code
     * @param item
     */
    private boolean traverseChildren(DataDictionary parent, String code, QueryItem item, String path) {
        List<DataDictionary> children = parent.getChildren();
        if (children != null && false == children.isEmpty()) {
            for (DataDictionary child : children) {
                if (StringUtils.equals(child.getCode(), code)) {
                    if (item.get("value") == null) {
                        item.put("label", path + child.getName());
                        item.put("value", code);
                    } else {
                        item.put("label", item.get("label") + Separator.SEMICOLON.getValue() + path + child.getName());
                        item.put("value", item.get("value") + Separator.SEMICOLON.getValue() + code);
                    }
                    return true;
                }
            }
            for (DataDictionary child : children) {
                if (traverseChildren(child, code, item, path + child.getName() + Separator.SLASH.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getDataDictionaries(java.lang.String, java.lang.String)
     */
    @Override
    public List<DataDictionary> getDataDictionaries(String type, String code) {
        List<DataDictionary> list = new ArrayList<DataDictionary>();
        List<DataDictionary> dataDictionaries = dao.getDataDictionaries(type, code);
        for (DataDictionary dataDictionary : dataDictionaries) {
            // if (hasPermission(dataDictionary)) {
            DataDictionary dictionary = new DataDictionary();
            BeanUtils.copyProperties(dataDictionary, dictionary);
            for (DataDictionaryAttribute attribute : dataDictionary.getAttributes()) {
                this.dataDictionaryAttributeService.evict(attribute);
            }
            // 转换字典属性对象
            dictionary.setAttributes(BeanUtils.convertCollection(dataDictionary.getAttributes(),
                    DataDictionaryAttribute.class));
            list.add(dictionary);
            // }
        }
        return list;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getDataDictionaryName(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, Object> getDataDictionaryName(String type, String code) {
        Map<String, Object> map = new HashMap<String, Object>();
        DataDictionary dataDictionary = new DataDictionary();
        List<DataDictionary> dataDictionaries = dao.getDataDictionaries(type, code);
        if (dataDictionaries.size() > 0) {
            dataDictionary = dataDictionaries.get(0);
            // if (hasPermission(dataDictionary)) {
            DataDictionary dictionary = new DataDictionary();
            BeanUtils.copyProperties(dataDictionary, dictionary);
            for (DataDictionaryAttribute attribute : dataDictionary.getAttributes()) {
                this.dataDictionaryAttributeService.evict(attribute);
            }
            // 转换字典属性对象
            dictionary.setAttributes(BeanUtils.convertCollection(dataDictionary.getAttributes(),
                    DataDictionaryAttribute.class));
            map.put("label", dictionary.getName());
            // }
        } else {
            map.put("label", code);
        }
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getAsTreeAsyncForControl(java.lang.String)
     */
    @Override
    public List<TreeNode> getAsTreeAsyncForControl(String uuid) {
        List<DataDictionary> list = new ArrayList<DataDictionary>();
        TreeNode node = new TreeNode();
        // 查询所有根结点
        if (TreeNode.ROOT_ID.equals(uuid)) {
            list = this.dao.getTopLevel();
        } else {
            // 查询指定结点的下一级子结点
            DataDictionary dataDictionary = this.dao.getOne(uuid);
            if (dataDictionary != null) {
                list = Arrays.asList(dataDictionary.getChildren().toArray(new DataDictionary[0]));
            }
        }

        List<TreeNode> children = node.getChildren();
        for (DataDictionary dataDictionary : list) {
            TreeNode child = new TreeNode();
            child.setId(dataDictionary.getUuid());
            child.setName(dataDictionary.getName());
            child.setData(dataDictionary.getType());// 此处返回的是type
            child.setIsParent(dataDictionary.getChildren().size() > 0);
            child.setNocheck(dataDictionary.getChildren().size() > 0);
            children.add(child);
        }
        return children;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getAsTreeAsyncForUuid(java.lang.String)
     */
    @Override
    public List<TreeNode> getAsTreeAsyncForUuid(String uuid, String type) {
        List<DataDictionary> list = new ArrayList<DataDictionary>();
        DataDictionary parentdataDictionary = this.getByType(type);
        if (TreeNode.ROOT_ID.equals(uuid)) {
            list = Arrays.asList(parentdataDictionary.getChildren().toArray(new DataDictionary[0]));
        } else {
            // 查询指定结点的下一级子结点
            DataDictionary dataDictionary = this.dao.getOne(uuid);
            if (dataDictionary != null) {
                list = Arrays.asList(dataDictionary.getChildren().toArray(new DataDictionary[0]));
            }
        }

        TreeNode node = new TreeNode();
        List<TreeNode> children = node.getChildren();
        for (DataDictionary dataDictionary : list) {
            TreeNode child = new TreeNode();
            child.setId(dataDictionary.getUuid());
            child.setName(dataDictionary.getName());
            child.setData(dataDictionary.getUuid());// 此处返回的是uuid
            child.setIsParent(dataDictionary.getChildren().size() > 0);
            child.setNocheck(dataDictionary.getChildren().size() > 0);
            children.add(child);
        }
        return children;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getAsTreeAsyncByCheckAll(java.lang.String)
     */
    @Override
    public List<TreeNode> getAsTreeAsyncByCheckAll(String id) {

        List<DataDictionary> list = new ArrayList<DataDictionary>();
        TreeNode node = new TreeNode();
        // 查询所有根结点
        if (TreeNode.ROOT_ID.equals(id)) {
            list = this.dao.getTopLevel();
        } else {
            // 查询指定结点的下一级子结点
            DataDictionary dataDictionary = this.dao.getOne(id);
            if (dataDictionary != null) {
                list = Arrays.asList(dataDictionary.getChildren().toArray(new DataDictionary[0]));
            }
        }

        List<TreeNode> children = node.getChildren();
        for (DataDictionary dataDictionary : list) {
            TreeNode child = new TreeNode();
            child.setId(dataDictionary.getUuid());
            child.setName(dataDictionary.getName());
            child.setData(dataDictionary.getType());
            child.setIsParent(dataDictionary.getChildren().size() > 0);
            child.setNocheck(false);
            children.add(child);
        }
        return children;
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        PagingInfo pagingInfo = queryInfo.getPagingInfo();
        String parentType = queryInfo.getOtherParams("type");
        List<DataDictionary> definitions = dao.getDataDictionariesByParentTypeAndName(parentType,
                queryInfo.getSearchValue(), pagingInfo);
        String idProperty = queryInfo.getOtherParams("idKey", "code");
        String textProperty = queryInfo.getOtherParams("textKey", "name");
        return new Select2QueryData(definitions, idProperty, textProperty, pagingInfo);
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        return null;
    }

    @Override
    public DataDictionary getByParentTypeAndCode(String parentType, String code) {
        return dao.getByParentTypeAndCode(parentType, code);
    }

    @Override
    public String getCnAbsolutePath(String uuid, int level) {
        String sql = "select get_cn_absolute_path(:uuid,:level) from dual";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uuid", uuid);
        params.put("level", level);
        List<String> paths = this.dao.listCharSequenceBySQL(sql, params);
        if (paths != null && paths.size() > 0) {
            return paths.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#isExistsByCodeWithInType(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isExistsByCodeWithInType(String code, String type) {
        DataDictionary dataDictionary = this.getByCodeWithInType(code, type);
        return dataDictionary != null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService#getByCodeWithInType(java.lang.String, java.lang.String)
     */
    @Override
    public DataDictionary getByCodeWithInType(String code, String type) {
        DataDictionary dataDictionary = this.dao.getOneByFieldEq("type", type);
        if (dataDictionary == null) {
            return null;
        }
        if (StringUtils.equals(dataDictionary.getCode(), code)) {
            return dataDictionary;
        }
        return this.findCode(dataDictionary.getChildren(), code);
    }

    public DataDictionary findCode(List<DataDictionary> dictionaryList, String code) {
        if (CollectionUtils.isNotEmpty(dictionaryList)) {
            for (DataDictionary child : dictionaryList) {
                if (StringUtils.equals(child.getCode(), code)) {
                    return child;
                }
                if (CollectionUtils.isNotEmpty(child.getChildren())) {
                    return this.findCode(child.getChildren(), code);
                }
            }
        }
        return null;
    }


    @Override
    @Transactional
    public void save(DataDictionary dataDictionary) {
        this.dao.save(dataDictionary);
    }

    @Override
    public List<DataDictionary> getAll() {
        return listAll();
    }

    @Override
    public List<QueryItem> query(String qUERY_FOR_CATEGORYPRIVILEGE_TREE, HashMap<String, Object> hashMap,
                                 Class<QueryItem> class1) {
        return listQueryItemBySQL(qUERY_FOR_CATEGORYPRIVILEGE_TREE, hashMap, null);
    }

    @Override
    public List<DataDictionary> findBy(String propertyName, String value) {
        return this.dao.listByFieldEqValue(propertyName, value);
    }

    @Override
    public String getDataDictionaryJsonKvByType(String type) {

        DataDictionary dataDictionary = this.getByType(type);
        if (dataDictionary != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(dataDictionary.getCode(), dataDictionary.getName());
            Map<String, String> kv = Maps.newHashMap();
            kv.put(dataDictionary.getCode(), dataDictionary.getName());
            return jsonObject.toString();

        }
        return "";
    }

    @Override
    public List<DataDictionary> queryModuleDataDics(Map<String, Object> params) {
        return this.dao.listByNameSQLQuery("appModuleDataDicDefManagerQuery", params);
    }

    @Override
    @Transactional
    public void updateSeqByUuids(List<String> uuids) {
        for (int i = 0; i < uuids.size(); i++) {
            DataDictionary dataDictionary = this.getOne(uuids.get(i));
            dataDictionary.setSeq(i + 1);
            this.save(dataDictionary);
        }
    }

    @Override
    @Transactional
    public List<TreeNode> getTreeNodeByType(String type, Integer maxLevel) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        DataDictionary dataDictionary = dao.getByType(type);
        traverseTreeNode(0, maxLevel, dataDictionary, treeNodes, null);
        return treeNodes;
    }

    @Override
    @Transactional
    public void moveDataDicAfterOther(String uuid, String afterUuid) {
        DataDictionary dataDictionary = dao.getOne(uuid);
        //获取该父节点下的所有同级节点，排序号统一刷一遍
        DataDictionary parent = dataDictionary.getParent();
        List<DataDictionary> sameLevelDataDics = parent == null ? dao.getTopLevel() : dao.getByParent(parent);
        List<String> uuids = Lists.transform(sameLevelDataDics, new Function<DataDictionary, String>() {

            @Nullable
            @Override
            public String apply(@Nullable DataDictionary dataDictionary) {
                return dataDictionary.getUuid();
            }
        });
        List<String> uuidList = Lists.newArrayList(uuids);
        int uuidIndex = uuidList.indexOf(uuid);
        if (StringUtils.isNotBlank(afterUuid)) {
            uuidList.remove(uuidIndex);
            int otherUuidIndex = uuidList.indexOf(afterUuid);
            uuidList.add(otherUuidIndex + 1, uuid);
        } else {
            uuidList.remove(uuidIndex);
            uuidList.add(0, uuid);
        }

        int seq = 1;
        for (String uid : uuidList) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("seq", seq++);
            params.put("uuid", uid);
            dao.updateByHQL("update DataDictionary set seq=:seq where uuid=:uuid", params);
        }
    }

    @Override
    @Transactional
    public DataDictionaryDto quickAddDataDic(String name, String afterUuid, String parentUuid) {
        DataDictionary parent = null;
        if (StringUtils.isNotBlank(parentUuid)) {
            parent = dao.getOne(parentUuid);
        } else if (StringUtils.isNotBlank(afterUuid)) {
            parent = dao.getOne(afterUuid).getParent();
        }
        String type = "", ctype;
        if (parent != null) {
            type = parent.getType();
            // 清理缓存
            this.dataDictionarySelectiveDataProvider.clear(type);
        }
        DataDictionary addOne = new DataDictionary();
        addOne.setName(name);
        int seq = this.dao.countMaxSeqUnderParentDataDdic(parent);
        addOne.setCode(ctype = (type + "_" + (seq + 1)));
        addOne.setType(addOne.getCode());
        addOne.setSeq(10000000);
        addOne.setParent(parent);
        // 已经存在，重新生成
        while (null != dao.getByType(ctype)) {
            addOne.setCode(ctype = (type + "_" + (seq++)));
            addOne.setType(addOne.getCode());
        }
        save(addOne);
        //重排序
        moveDataDicAfterOther(addOne.getUuid(), afterUuid);
        DataDictionaryDto dto = new DataDictionaryDto();
        org.springframework.beans.BeanUtils.copyProperties(addOne, dto);
        // 清理缓存
        cacheManager.getCache(ModuleID.BASIC_DATA).clear();
        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = DATA_DICT_CACHE_NAME, allEntries = true)
    public void quickDeleteDataDic(String uuid) {
        this.dao.delete(uuid);
    }

    @Override
    public List<DataDictionary> getByNameLikes(String name) {
        return this.dao.listByFieldAnyLike("name", name);
    }

    @Override
    public int countChildren(String uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        return (int) this.dao.countBySQL("select count(1) from cd_data_dict where parent_uuid = :uuid", params);
    }

    private void traverseTreeNode(Integer currentLevel, Integer maxLevel, DataDictionary dataDictionary,
                                  List<TreeNode> treeNodes, TreeNode pTreeNode) {
        if (dataDictionary == null || dataDictionary.getChildren().isEmpty()) {
            return;
        } else if (currentLevel >= maxLevel) {
            return;
        }
        for (DataDictionary childDataDictionary : dataDictionary.getChildren()) {
            TreeNode treeNode = new TreeNode();
            treeNodes.add(treeNode);
            treeNode.setId(childDataDictionary.getCode());
            treeNode.setName(childDataDictionary.getName());
            treeNode.setPath(pTreeNode == null ? childDataDictionary.getName() : pTreeNode.getPath() + "/"
                    + childDataDictionary.getName());
            traverseTreeNode(currentLevel + 1, maxLevel, childDataDictionary, treeNode.getChildren(), treeNode);
        }
    }

}
