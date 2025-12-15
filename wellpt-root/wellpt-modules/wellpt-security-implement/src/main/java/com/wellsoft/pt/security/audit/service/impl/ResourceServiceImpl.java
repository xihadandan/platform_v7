/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */

package com.wellsoft.pt.security.audit.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.facade.api.AppFunctionFacade;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.audit.bean.ResourceBean;
import com.wellsoft.pt.security.audit.bean.ResourceDto;
import com.wellsoft.pt.security.audit.dao.ResourceDao;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.service.PrivilegeResourceService;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import com.wellsoft.pt.security.audit.service.ResourceService;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.audit.support.ResourceDataSource;
import com.wellsoft.pt.security.audit.support.SecurityConfigType;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.enums.ResourceType;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.security.support.SecurityResourceConfig;
import com.wellsoft.pt.security.util.SecurityConfigUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-17.1	zhulh		2013-1-17		Create
 * </pre>
 * @date 2013-1-17
 */
@Service
public class ResourceServiceImpl extends
        AbstractJpaServiceImpl<Resource, ResourceDao, String> implements
        ResourceService {
    private static final String SECURITY_CACHE_NAME = "Security";
    private static final String PUSHRESOURCEPREFIX = "audit.pushResource.prefix";
    private static final String ISPUSHRESOURCE = "audit.ispushResource";
    private static final String SOURCETYPE = "RESOURCE";
    @Autowired
    AppFunctionFacade appFunctionFacade;
    private String QUERY_FOR_RESOURCE_BUTTON_TREE = "select resource.uuid as uuid,resource.remark as remark, resource.name as name, resource.code as code,resource.type as type, resource.parent.uuid as parentUuid "
            + " from Resource resource where resource.type = 'MENU' or resource.type = 'BUTTON' order by resource.code asc";
    @Autowired
    private PrivilegeResourceService privilegeResourceService;
    @Autowired
    private SecurityMetadataSourceService securityMetadataSourceService;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private CommonValidateService commonValidateService;
    @Autowired
    private DataDictionaryService dataDictionaryService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private RoleService roleService;
    // <type, ResourceDataSource>
    private Map<String, ResourceDataSource> resourceDataSourceMap = new HashMap<String, ResourceDataSource>();

    /**
     * @param resourceId
     * @param configAttributeMap
     * @param object
     * @param config
     */
    private static void addSecurityConfigAttribute(String resourceId,
                                                   Map<Object, Collection<ConfigAttribute>> configAttributeMap,
                                                   Object object, String config) {
        if (!configAttributeMap.containsKey(object)) {
            configAttributeMap.put(object, new ArrayList<ConfigAttribute>());
        }
        Collection<ConfigAttribute> securityConfigs = configAttributeMap.get(object);
        SecurityConfig securityConfig = new SecurityResourceConfig(resourceId, config);
        if (!securityConfigs.contains(securityConfig)) {
            securityConfigs.add(securityConfig);
        }
    }

    /**
     * @param resourceId
     * @param configAttributeMap
     * @param object
     */
    private static void addAnonymousConfigAttribute(String resourceId,
                                                    Map<Object, Collection<ConfigAttribute>> configAttributeMap,
                                                    Object object) {
        if (!configAttributeMap.containsKey(object)) {
            configAttributeMap.put(object, new ArrayList<ConfigAttribute>());
        }
        Collection<ConfigAttribute> securityConfigs = configAttributeMap.get(object);
        SecurityConfig securityConfig = new SecurityResourceConfig(resourceId,
                BuildInRole.ROLE_ANONYMOUS.name());
        if (!securityConfigs.contains(securityConfig)) {
            securityConfigs.add(securityConfig);
        }
    }

    /**
     * 根据UUID获取资源
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#getBean(java.lang.String)
     */
    @Override
    public ResourceBean getBean(String uuid) {
        Resource resource = this.dao.getOne(uuid);
        ResourceBean bean = new ResourceBean();
        BeanUtils.copyProperties(resource, bean);

        // 1、获取父节点
        if (resource.getParent() != null) {
            bean.setParentUuid(resource.getParent().getUuid());
            bean.setParentName(resource.getParent().getName());
        }

        // 获取子结点为类型为按钮的资源
        // 按钮子结点
        Set<ResourceBean> buttons = new LinkedHashSet<ResourceBean>();
        // 方法子结点
        Set<ResourceBean> methods = new LinkedHashSet<ResourceBean>();
        Set<Resource> children = resource.getChildren();
        for (Resource child : children) {
            if (ResourceType.BUTTON.getValue().equals(child.getType())) {
                ResourceBean res = new ResourceBean();
                BeanUtils.copyProperties(child, res);
                buttons.add(res);
            } else if (ResourceType.METHOD.getValue().equals(child.getType())) {
                ResourceBean res = new ResourceBean();
                BeanUtils.copyProperties(child, res);
                methods.add(res);
            }
        }
        // 2、设置按钮子结点
        bean.setButtons(buttons);
        // 3、设置方法子结点
        bean.setMethods(methods);

        return bean;
    }

    private void singleRowToCategory(String prefix, Resource resource) {
        DataDictionary existDataDictionary = dataDictionaryService.getByType(
                prefix + resource.getCode());
        if (existDataDictionary != null) {
            return;
        }
        if (!ResourceType.MENU.getValue().equals(resource.getType())) {
            return;
        }
        Resource parentResource = resource.getParent();
        // 最顶级的
        if (parentResource == null) {
            DataDictionary dataDictionary = new DataDictionary();
            dataDictionary.setName(resource.getName());
            dataDictionary.setType(prefix + resource.getCode());
            dataDictionary.setCode(prefix + resource.getCode());
            dataDictionary.setSourceType(SOURCETYPE);
            dataDictionary.setSourceUuid(resource.getUuid());
            String categoryType = Config.getValue("audit.security.category.type");
            DataDictionary parent = dataDictionaryService.getByType(categoryType);
            dataDictionary.setParent(parent);
            dataDictionaryService.save(dataDictionary);
        } else {

            DataDictionary dataDictionary = new DataDictionary();
            dataDictionary.setName(resource.getName());
            dataDictionary.setType(prefix + resource.getCode());
            dataDictionary.setCode(prefix + resource.getCode());
            dataDictionary.setSourceType(SOURCETYPE);
            dataDictionary.setSourceUuid(resource.getUuid());
            DataDictionary parentDataDictionary = dataDictionaryService.getByType(
                    prefix + parentResource.getCode());
            if (parentDataDictionary == null) {
                return;
            }
            if (parentDataDictionary.getName().equals(parentResource.getName())) {
                dataDictionary.setParent(parentDataDictionary);
            }
            dataDictionaryService.save(dataDictionary);
        }
    }

    // TODO
    protected void pushResourceToCategory(Resource resource) {
        String prefix = Config.getValue(PUSHRESOURCEPREFIX);
        // 单条数据同步到资源

        Set<Resource> children = resource.getChildren();
        String categoryType = Config.getValue("audit.security.category.type");
        DataDictionary parent = dataDictionaryService.getByType(categoryType);

        List<DataDictionary> dataDictionarySet = parent.getChildren();

        HashMap<String, DataDictionary> categorymap = new HashMap<String, DataDictionary>();
        // 父级大类map
        for (DataDictionary dataDictionary : dataDictionarySet) {
            // 二级目录
            categorymap.put(dataDictionary.getCode(), dataDictionary);
        }
        // 是一级目录 resource.getParent() == null
        if (resource.getCode().equals("006002005") && !categorymap.containsKey(
                resource.getCode())) {
            DataDictionary dataDictionary = new DataDictionary();
            dataDictionary.setName(resource.getName());
            dataDictionary.setType(prefix + resource.getCode());
            dataDictionary.setCode(prefix + resource.getCode());
            dataDictionary.setParent(parent);
            dataDictionary.setSourceType(SOURCETYPE);
            dataDictionary.setSourceUuid(resource.getUuid());
            dataDictionaryService.save(dataDictionary);
        }

        HashMap<String, Resource> resourceMap = new HashMap<String, Resource>();
        for (Resource child : children) {
            if (ResourceType.MENU.getValue().equals(child.getType())) {
                resourceMap.put(child.getCode(), child);
                createSubResourceMap(resourceMap, child);
            }
        }

        HashMap<String, DataDictionary> dataDictionaryMap = new HashMap<String, DataDictionary>();
        // 推式资源到字典
        for (String code : resourceMap.keySet()) {
            Resource rs = resourceMap.get(code);
            DataDictionary existDataDictionary = dataDictionaryService.getByType(
                    prefix + rs.getCode());
            if (existDataDictionary != null) {
                dataDictionaryMap.put(code, existDataDictionary);
                continue;
            }
            DataDictionary dataDictionary = new DataDictionary();
            dataDictionary.setName(rs.getName());
            dataDictionary.setType(prefix + rs.getCode());
            dataDictionary.setCode(prefix + rs.getCode());
            dataDictionary.setSourceType(SOURCETYPE);
            dataDictionary.setSourceUuid(rs.getUuid());
            // dataDictionary.setParent(parent);
            dataDictionaryService.save(dataDictionary);
            dataDictionaryMap.put(code, dataDictionary);
        }

        // 设置主子关系
        for (String code : resourceMap.keySet()) {
            Resource rs = resourceMap.get(code);
            Resource parentResource = rs.getParent();
            DataDictionary parentDataDictionary = dataDictionaryService.getByType(
                    prefix + parentResource.getCode());
            if (parentDataDictionary.getName().equals(parentResource.getName())) {
                DataDictionary updateDatadic = dataDictionaryMap.get(code);
                updateDatadic.setParent(parentDataDictionary);
                dataDictionaryService.save(updateDatadic);
            }
        }

    }

    private void createSubResourceMap(HashMap<String, Resource> resourceMap, Resource resource) {
        Set<Resource> children = resource.getChildren();
        for (Resource child : children) {
            if (ResourceType.MENU.getValue().equals(child.getType())) {

                resourceMap.put(child.getCode(), child);

                createSubResourceMap(resourceMap, child);
            }
        }
    }

    /**
     * 保存资源
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#saveBean(com.wellsoft.pt.security.audit.bean.ResourceBean)
     */
    @Override
    @Transactional
    @CacheEvict(value = SECURITY_CACHE_NAME, allEntries = true)
    public String saveBean(ResourceBean bean) {
        Resource resource = new Resource();
        if (StringUtils.isBlank(bean.getName())) {
            throw new RuntimeException("名称不能为空！");
        }
        // modifiy by xujm 增加角色编号
        String code = bean.getCode();
        if (StringUtils.isBlank(code)) {
            throw new RuntimeException("编号不能为空！");
        }
        // modifiy by zhangyh 添加编号规则校验
        if (code.startsWith("B")) {
            throw new RuntimeException("编号不能以字母B开头！");
        }
        Set<ResourceBean> buttons = bean.getChangedButtons();
        for (Resource button : buttons) {
            String buttonCode = button.getCode();
            if (!buttonCode.startsWith("B")) {
                throw new RuntimeException("按钮编号请以字母B开头！");
            }
        }
        if (StringUtils.isBlank(bean.getUuid())) {
            // 资源编号唯一性验证
            resource.setCode(bean.getCode());
            checkIsExists(resource, bean.getCode());
            bean.setUuid(null);
            if (StringUtils.isBlank(bean.getType())) {
                bean.setType(ResourceType.MENU.getValue());
            }
        } else {
            // 资源编号唯一性验证
            checkIsUnique(bean, bean.getCode());
            resource = this.dao.getOne(bean.getUuid());
        }
        BeanUtils.copyProperties(bean, resource);

        this.dao.save(resource);

        if (StringUtils.isNotBlank(resource.getModuleId())) {
            appFunctionFacade.synchronizeFunction2ModuleProductIntegrate(resource.getUuid(),
                    AppFunctionType.MENU, resource.getModuleId(), false);
        }

        // 1、设置父节点
        if (StringUtils.isNotBlank(bean.getParentUuid())) {
            resource.setParent(this.dao.getOne(bean.getParentUuid()));
        } else {
            resource.setParent(null);
        }

        // 2、删除已册的按钮、方法
        for (Resource child : bean.getDeletedButtons()) {
            appFunctionFacade.delete(child.getUuid());
            this.remove(child.getUuid());
        }
        for (Resource child : bean.getDeletedMethods()) {
            this.remove(child.getUuid());
        }

        // 3、保存按钮
        for (Resource button : buttons) {
            Resource btn = new Resource();
            if (StringUtils.isNotBlank(button.getUuid())) {
                // 资源编号唯一性验证
                checkIsUnique(button, button.getCode());

                btn = this.dao.getOne(button.getUuid());
                BeanUtils.copyProperties(button, btn);
                btn.setType(ResourceType.BUTTON.getValue());
                this.dao.save(btn);
            } else {

                // 资源编号唯一性验证
                btn.setCode(button.getCode());
                checkIsExists(btn, button.getCode());
                BeanUtils.copyProperties(button, btn);
                btn.setUuid(null);
                btn.setType(ResourceType.BUTTON.getValue());
                btn.setParent(resource);
                this.dao.save(btn);
            }

            if (StringUtils.isNotBlank(btn.getModuleId())) {
                appFunctionFacade.synchronizeFunction2ModuleProductIntegrate(btn.getUuid(),
                        AppFunctionType.BUTTON, btn.getModuleId(), false);
            }
        }

        // 4、保存方法
        Set<ResourceBean> methods = bean.getChangedMethods();
        for (Resource method : methods) {
            if (StringUtils.isNotBlank(method.getUuid())) {
                // 资源编号唯一性验证
                checkIsUnique(method, method.getCode());

                Resource mth = this.dao.getOne(method.getUuid());
                BeanUtils.copyProperties(method, mth);
                mth.setType(ResourceType.METHOD.getValue());
                this.dao.save(mth);
            } else {
                Resource mth = new Resource();
                // 资源编号唯一性验证
                mth.setCode(method.getCode());
                checkIsExists(mth, method.getCode());

                BeanUtils.copyProperties(method, mth);
                mth.setUuid(null);
                mth.setType(ResourceType.METHOD.getValue());
                mth.setParent(resource);
                this.dao.save(mth);
            }
        }
        if ("true".equals(Config.getValue(ISPUSHRESOURCE))) {
            // pushResourceToCategory(resource);
            String prefix = Config.getValue(PUSHRESOURCEPREFIX);
            singleRowToCategory(prefix, resource);
        }

        // 发布安全配置变更事件
        SecurityConfigUtils.publishSecurityConfigUpdatedEvent(resource);
        AppCacheUtils.clear();
        return resource.getUuid();
    }

    /**
     * 如何描述该方法
     *
     * @param bean
     * @param resource
     */
    private void checkIsExists(Resource resource, String code) {
        if (commonValidateService.checkExists("resource", "code", code)) {
            throw new RuntimeException("已经存在编号为[" + code + "]的资源!");
        }
    }

    /**
     * @param resource
     * @param code
     */
    private void checkIsUnique(Resource resource, String code) {
        if (!commonValidateService.checkUnique(resource.getUuid(), "resource", "code", code)) {
            throw new RuntimeException("已经存在编号为[" + code + "]的资源!");
        }
    }

    /**
     * (non-Javadoc)
     * @see com.wellsoft.pt.security.audit.service.ResourceService#getDataDictionaryByType(java.lang.String)
     */
    /*@Override
    public List<DataDictionary> getDataDictionariesByType(String type) {
    	return basicDataApiFacade.getDataDictionariesByType(type);
    }*/

    /**
     * 根据UUID删除资源
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#remove(java.lang.String)
     */
    @Override
    @Transactional
    @CacheEvict(value = SECURITY_CACHE_NAME, allEntries = true)
    public void remove(String uuid) {
        Resource resource = this.dao.getOne(uuid);
        // 嵌套删除资源
        removeNested(resource);
        // 触发权限资源改变事件
//        this.securityMetadataSourceService.loadSecurityMetadataSource();
    }

    /**
     * 嵌套删除资源
     *
     * @param resource
     */
    private void removeNested(Resource resource) {
        Set<Resource> resources = resource.getChildren();
        for (Resource res : resources) {
            removeNested(res);
        }

        // 资源-权限关系，资源是被控方，多对多 被控方删除(可以删除中间表记录)
        Set<Privilege> privileges = resource.getPrivileges();
        for (Privilege privilege : privileges) {
            privilege.getResources().remove(resource);
        }

        this.dao.delete(resource);
    }

    /**
     * 获取资源的树形结构
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#getMenuAsTree(java.lang.String)
     */
    @Override
    public TreeNode getMenuAsTree(String excludeUuid) {
        List<Resource> resources = this.dao.getToLevel();
        TreeNode treeNode = new TreeNode();
        treeNode.setId(TreeNode.ROOT_ID);
        treeNode.setName("资源");
        buildMenuTree(treeNode, resources, excludeUuid);
        return treeNode;
    }

    /**
     * @param treeNode
     * @param resources
     * @param excludeUuid
     */
    private void buildMenuTree(TreeNode treeNode, List<Resource> resources, String excludeUuid) {
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Resource resource : resources) {
            if (!ResourceType.MENU.getValue().equals(
                    resource.getType()) || resource.getUuid().equals(excludeUuid)) {
                continue;
            }

            TreeNode child = new TreeNode();
            child.setId(resource.getUuid());
            child.setName(resource.getName());

            /* lmw 2015-5-26 20:40 begin */
            child.setData(resource.getUuid());
            /* lmw 2015-5-26 20:40 end */
            children.add(child);

            if (resource.getChildren().size() != 0) {
                buildMenuTree(child, Arrays.asList(resource.getChildren().toArray(new Resource[0])),
                        excludeUuid);
            }

        }
        treeNode.setChildren(children);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#getResourceMenuTree(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getResourceMenuTree(String treeNodeId, String excludeUuid) {
        TreeNode treeNode = new TreeNode();
        treeNode.setId(TreeNode.ROOT_ID);
        if (TreeNode.ROOT_ID.equals(treeNodeId)) {
            for (Resource resource : this.dao.getToLevel()) {
                buildMenuTree(treeNode, resource, resource.getName(), excludeUuid);
            }
        } else {
            Resource resource = this.dao.getOne(treeNodeId);
            for (Resource res : resource.getChildren()) {
                buildMenuTree(treeNode, res, resource.getName(), excludeUuid);
            }
        }
        return treeNode.getChildren();
    }

    /**
     * 如何描述该方法
     *
     * @param treeNode
     * @param res
     * @param name
     * @param excludeUuid
     */
    private void buildMenuTree(TreeNode treeNode, Resource resource, String path,
                               String excludeUuid) {
        String type = resource.getType();
        if (!(ResourceType.MENU.getValue().equals(type)) || resource.getUuid().equals(
                excludeUuid)) {
            return;
        }

        TreeNode child = new TreeNode();
        child.setId(resource.getUuid());
        child.setName(resource.getName());
        child.setData(resource.getCode());
        child.setPath(path);

        for (Resource res : resource.getChildren()) {
            buildMenuTree(child, res, path + "/" + res.getName(), excludeUuid);
        }
        treeNode.getChildren().add(child);

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#getResourceButtonTree(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getResourceButtonTree(String treeNodeId, String excludeUuid) {
        if (!TreeNode.ROOT_ID.equals(treeNodeId)) {
            return new ArrayList<TreeNode>(0);
        }
        // 构建权限资源树
        List<QueryItem> allResources = listQueryItemByHQL(QUERY_FOR_RESOURCE_BUTTON_TREE, null,
                null);
        TreeNode treeNode = new TreeNode();
        treeNode.setId(TreeNode.ROOT_ID);
        treeNode.setNocheck(true);
        List<QueryItem> topResources = new ArrayList<QueryItem>();
        Map<String, List<QueryItem>> parentResourceMap = new HashMap<String, List<QueryItem>>();

        for (QueryItem queryItem : allResources) {
            String resourceName = queryItem.getString("name");
            String remark = queryItem.getString("remark");
            if (StringUtils.isNotBlank(remark) && !remark.equals(resourceName)) {
                queryItem.put("name", resourceName + "[" + remark + "]");
            }
            String parentUuid = queryItem.getString("parentUuid");
            if (StringUtils.isNotBlank(parentUuid)) {
                if (!parentResourceMap.containsKey(parentUuid)) {
                    parentResourceMap.put(parentUuid, new ArrayList<QueryItem>());
                }
                parentResourceMap.get(parentUuid).add(queryItem);
            } else {
                topResources.add(queryItem);
            }
        }
        for (QueryItem queryItem : topResources) {
            TreeNode node = new TreeNode();
            String resourceUuid = queryItem.getString("uuid");
            String resourceName = queryItem.getString("name");
            String resourceCode = queryItem.getString("code");
            node.setId(resourceUuid);
            node.setName(resourceName);
            node.setData(resourceCode);
            node.setPath(resourceName);
            node.setIsParent(true);
            node.setNocheck(true);

            // 生成子结点
            buildChildNodes(node, resourceName, parentResourceMap);

            treeNode.getChildren().add(node);
        }
        return treeNode.getChildren();
    }

    /**
     * @param node
     * @param parentMap
     */
    private void buildChildNodes(TreeNode node, String path,
                                 Map<String, List<QueryItem>> parentResourceMap) {
        String key = node.getId();
        List<QueryItem> queryItems = parentResourceMap.get(key);
        if (queryItems == null) {
            return;
        }

        for (QueryItem queryItem : queryItems) {
            TreeNode child = new TreeNode();
            String id = queryItem.getString("uuid");
            String name = queryItem.getString("name");
            String code = queryItem.getString("code");
            String type = queryItem.getString("type");

            child.setId(id);
            child.setName(name);
            child.setData(code);
            child.setPath(path + "/" + name);
            if (ResourceType.MENU.getValue().equals(type)) {
                child.setIsParent(true);
                child.setNocheck(true);
            }
            node.getChildren().add(child);

            buildChildNodes(child, path + "/" + name, parentResourceMap);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#getDynamicButtonResourcesByCode(java.lang.String)
     */
    @Override
    public List<Resource> getDynamicButtonResourcesByCode(String code) {
        List<Resource> dybtns = this.dao.getDynamicButtonsByCode(code);
        List<Resource> resources = BeanUtils.convertCollection(dybtns, Resource.class);
        return resources;
    }

    /**
     * @param requestMap
     * @param rawUrl
     * @param rolename
     */
    private void addUrlSecurityConfigAttribute(Map<Object, Collection<ConfigAttribute>> requestMap,
                                               String rawUrl,
                                               String rolename) {
        String[] urls = null;
        if (rawUrl.indexOf(";") != -1) {
            urls = StringUtils.split(rawUrl, ";");
        } else {
            urls = new String[]{rawUrl};
        }

        for (String requestUrl : urls) {
            if (!requestMap.containsKey(requestUrl)) {
                requestMap.put(requestUrl, new ArrayList<ConfigAttribute>());
            }
            Collection<ConfigAttribute> securityConfigs = requestMap.get(requestUrl);
            SecurityConfig securityConfig = new SecurityConfig(rolename);
            if (!securityConfigs.contains(securityConfig)) {
                securityConfigs.add(securityConfig);
            }
        }
    }

    /**
     * @param type
     * @return
     */
    private ResourceDataSource getResourceDataSource(String type) {
        if (resourceDataSourceMap.containsKey(type)) {
            return resourceDataSourceMap.get(type);
        }
        Map<String, ResourceDataSource> map = ApplicationContextHolder.getApplicationContext().getBeansOfType(
                ResourceDataSource.class);
        for (Entry<String, ResourceDataSource> entry : map.entrySet()) {
            ResourceDataSource source = entry.getValue();
            if (StringUtils.equals(type, source.getId())) {
                resourceDataSourceMap.put(type, source);
            }
        }
        return resourceDataSourceMap.get(type);
    }

    /**
     * @param type
     * @return
     */
    private Collection<ResourceDataSource> getAllResourceDataSources() {
        Map<String, ResourceDataSource> map = ApplicationContextHolder.getApplicationContext().getBeansOfType(
                ResourceDataSource.class);
        if (MapUtils.isEmpty(map)) {
            return Collections.emptyList();
        }
        return map.values();
    }

    @Override
    public Map<String, Resource> loadResourceMap() {
        Map<String, Resource> resourceMap = new HashMap<String, Resource>();
        // 获取所有的资源
        List<Resource> resources = listAll();
        for (Resource resource : resources) {
            String code = resource.getCode();
            if (StringUtils.isBlank(code)) {
                continue;
            }
            addResource(resourceMap, StringUtils.trim(code), resource);
        }
        return resourceMap;
    }

    /**
     * @param code
     * @param resource
     */
    private void addResource(Map<String, Resource> resourceMap, String code, Resource resource) {
        Resource button = new Resource();
        BeanUtils.copyProperties(resource, button);
        resourceMap.put(code, button);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#getUrlResourcesByUserUuid(java.lang.String)
     */
    @Override
    public List<Resource> getUrlResourcesByUserUuid(String userUuid) {
        List<Resource> resources = new ArrayList<Resource>();
        // 1、用户自身拥有的角色权限
        String hql1 = "select resource from User as user join user.roles as role join role.privileges as privilege join privilege.resources as resource "
                + " where user.uuid = :userUuid and resource.type = 'MENU' and resource.url is not null";
        Map<String, Object> values1 = new HashMap<String, Object>();
        values1.put("userUuid", userUuid);
        List<Resource> resources1 = this.listByHQL(hql1, values1);
        resources.addAll(resources1);

        // 2、用户所在群组拥有的角色权限
        String hql2 = "select resource from Group as g1 join g1.roles as role join role.privileges as privilege join privilege.resources as resource "
                + " where resource.url is not null and exists (select user.uuid from User as user join user.groups as g2 where g1 = g2 and user.uuid = :userUuid)";
        Map<String, Object> values2 = new HashMap<String, Object>();
        values2.put("userUuid", userUuid);
        List<Resource> resources2 = this.dao.listByHQL(hql2, values2);
        resources.addAll(resources2);

        // 3、用户所在部门拥有的角色权限
        String hql3 = "select resource from Department as department join department.roles as role join role.privileges as privilege join privilege.resources as resource "
                + " where resource.url is not null and exists (select departmentUserJob.uuid from DepartmentUserJob as departmentUserJob where departmentUserJob.department = department and departmentUserJob.user.uuid = :userUuid)";
        Map<String, Object> values3 = new HashMap<String, Object>();
        values3.put("userUuid", userUuid);
        List<Resource> resources3 = this.dao.listByHQL(hql3, values3);
        resources.addAll(resources3);

        return BeanUtils.convertCollection(resources, Resource.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#getResourceByCode(java.lang.String)
     */
    @Override
    @Cacheable(value = SECURITY_CACHE_NAME)
    public Resource getResourceByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        Resource example = new Resource();
        example.setCode(code);
        List<Resource> resources = this.dao.listByEntity(example);
        if (resources.size() != 1) {
            return null;
        }
        Resource resource = resources.get(0);
        Resource res = new Resource();
        BeanUtils.copyProperties(resource, res);
        return res;
    }

    @Override
    @Transactional
    public void updateParent(String childCode, String toParentCode) {
        if (StringUtils.isNotBlank(childCode) && StringUtils.isNotBlank(toParentCode)) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("code", childCode);
            Resource resource = this.dao.getOneByHQL("from Resource where code=:code", params);
            if (null != resource) {
                // 更改资源父类
                params.put("code", toParentCode);
                Resource newParentResource = dao.getOneByHQL("code", params);
                Resource oldParentResource = resource.getParent();
                if (null != newParentResource) {
                    resource.setParent(newParentResource);
                    dao.save(resource);
                }
                // 修改相应的权限
                Set<Privilege> privileges = resource.getPrivileges();
                if (null != privileges && !privileges.isEmpty()) {
                    for (Privilege privilege : privileges) {
                        if (null != privilege) {
                            Set<Resource> childResources = oldParentResource.getChildren();
                            // 当该权限旧父权限下其他资源时不删除 没有是删除资源权限
                            if (null == childResources || childResources.isEmpty()) {
                                privilege.getResources().remove(oldParentResource);
                            } else {
                                boolean hasChilds = false;
                                for (Resource childResource : childResources) {
                                    if (!resource.equals(childResource)
                                            && privilege.getResources().contains(childResource)) {
                                        hasChilds = true;
                                        break;
                                    }
                                }
                                if (!hasChilds) {
                                    privilege.getResources().remove(oldParentResource);
                                }
                            }
                            // 添加新父资源权限
                            if (null != newParentResource) {
                                privilege.getResources().add(newParentResource);
                            }
                            privilegeService.save(privilege);
                        }
                    }
                }
                // 更该数据字典
                DataDictionary dictionary = dataDictionaryService.getByType(
                        Config.getValue(PUSHRESOURCEPREFIX)
                                + childCode);
                DataDictionary newParentdictionary = dataDictionaryService.getByType(Config
                        .getValue(PUSHRESOURCEPREFIX) + toParentCode);
                if (null != dictionary) {
                    dictionary.setParent(newParentdictionary);
                    dataDictionaryService.save(dictionary);
                }
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#loadConfigAttribute()
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Map<Object, Collection<ConfigAttribute>>> loadConfigAttribute() {
        Map<String, Map<Object, Collection<ConfigAttribute>>> map = new HashMap<String, Map<Object, Collection<ConfigAttribute>>>();

        // 获取类型为菜单URL的资源
        StopWatch watch = new StopWatch("loadConfigAttribute");
        watch.start("getAllResource");
        List<Resource> resources = listAll();
        watch.stop();
        watch.start("getAllPrivilege");
        List<Privilege> privileges = this.privilegeService.getAll();
        watch.stop();
        watch.start("getAllRole");
        List<Role> roles = this.roleService.getAll();
        watch.stop();
        watch.start("extractResourceConfigAttribute");
        extractResourceConfigAttribute(map, resources, privileges, roles);
        watch.stop();
        watch.start("getAllPrivilegeResource");
        // 获取类型为其他权限的资源
        List<PrivilegeResource> privilegeResources = this.privilegeResourceService.listAll();
        watch.stop();
        watch.start("extractPrivilegeResourceConfigAttribute");
        extractPrivilegeResourceConfigAttribute(map, privilegeResources, privileges, roles);
        watch.stop();
        logger.info("加载权限资源数据耗时: {}", watch.prettyPrint());
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#loadConfigAttribute(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Map<Object, Collection<ConfigAttribute>>> loadConfigAttribute(
            String configUuid,
            String configType) {
        if (SecurityConfigType.ROLE.equals(configType)) {
            return loadRoleConfigAttribute(configUuid);
        } else if (SecurityConfigType.PRIVILEGE.equals(configType)) {
            return loadPrivilegeConfigAttribute(configUuid);
        }
        return loadConfigAttribute();
    }

    /**
     * @param roleUuid
     * @return
     */
    public Map<String, Map<Object, Collection<ConfigAttribute>>> loadRoleConfigAttribute(
            String roleUuid) {
        Map<String, Map<Object, Collection<ConfigAttribute>>> map = new HashMap<String, Map<Object, Collection<ConfigAttribute>>>();

        List<Resource> resources = new ArrayList<Resource>();
        List<PrivilegeResource> privilegeResources = new ArrayList<PrivilegeResource>();
        Role role = this.roleService.get(roleUuid);
        Set<Privilege> privileges = role.getPrivileges();
        for (Privilege privilege : privileges) {
            resources.addAll(privilege.getResources());
            privilegeResources.addAll(
                    this.privilegeResourceService.getByPrivilegeUuid(privilege.getUuid()));
        }

        // 获取类型为菜单URL的资源
        extractResourceConfigAttribute(map, resources, null, null);

        // 获取类型为其他权限的资源
        extractPrivilegeResourceConfigAttribute(map, privilegeResources, null, null);

        return map;
    }

    /**
     * @param privilegeUuid
     * @return
     */
    public Map<String, Map<Object, Collection<ConfigAttribute>>> loadPrivilegeConfigAttribute(
            String privilegeUuid) {
        final Map<String, Map<Object, Collection<ConfigAttribute>>> map = Maps.newConcurrentMap();

        Privilege privilege = this.privilegeService.get(privilegeUuid);
        List<Resource> resources = Arrays.asList(privilege.getResources().toArray(new Resource[0]));
        final List<PrivilegeResource> privilegeResources = this.privilegeResourceService.getByPrivilegeUuid(
                privilegeUuid);

        // 获取类型为菜单URL的资源
        extractResourceConfigAttribute(map, resources, null, null);

        //FIXME:权限资源太多，解析太慢
        // 获取类型为其他权限的资源
        extractPrivilegeResourceConfigAttribute(map, privilegeResources, null, null);
        return map;
    }

    /**
     * 获取类型为菜单URL的资源
     *
     * @param map
     * @param resources
     */
    public void extractResourceConfigAttribute(
            Map<String, Map<Object, Collection<ConfigAttribute>>> map,
            List<Resource> resources, List<Privilege> allPrivileges, List<Role> allRoles) {
        for (Resource resource : resources) {
            String uuid = resource.getUuid();
            String url = resource.getUrl();
            String code = resource.getCode();
            if (StringUtils.isBlank(url) && StringUtils.isBlank(code)) {
                continue;
            }
            String functionType = resource.getType();
            if (!map.containsKey(functionType)) {
                map.put(functionType, new LinkedHashMap<Object, Collection<ConfigAttribute>>());
            }
            Map<Object, Collection<ConfigAttribute>> functionMap = map.get(functionType);

            Set<Privilege> privileges = resource.getPrivileges();
            for (Privilege privilege : privileges) {
                Set<Role> roles = privilege.getRoles();
                for (Role role : roles) {
                    String roleId = role.getId();
                    if (StringUtils.isNotBlank(url)) {
                        addUrlSecurityConfigAttribute(functionMap, url, roleId);
                    }
                    if (StringUtils.isNotBlank(code)) {
                        addSecurityConfigAttribute(uuid, functionMap, StringUtils.trim(code),
                                roleId);
                    }
                }
            }
        }
    }

    /**
     * 获取类型为其他权限的资源
     *
     * @param map
     * @param privilegeResources
     */
    public void extractPrivilegeResourceConfigAttribute(
            Map<String, Map<Object, Collection<ConfigAttribute>>> map,
            List<PrivilegeResource> privilegeResources, List<Privilege> allPrivileges,
            List<Role> allRoles) {
        // 受保护的资源对象映射表
        Map<String, Map<String, Collection<Object>>> resourceObjectIdentityMap = Maps.newHashMap();
        StopWatch stopWatch = new StopWatch("extractPrivilegeResourceConfigAttribute");
        stopWatch.start("privilegeResourceConfigAttribute");
        // 1、受保护的资源
        for (PrivilegeResource resource : privilegeResources) {
            String privilegeUuid = resource.getPrivilegeUuid();
            String resourceUuid = resource.getResourceUuid();
            Privilege privilege = this.privilegeService.get(privilegeUuid);
            if (privilege == null) {
                logger.warn("UUID为{}的权限不存在!", privilegeUuid);
                continue;
            }
            Set<Role> roles = privilege.getRoles();
            if (roles.isEmpty()) {
                continue;
            }
            // 新版页面定义、页面组件以及组件上的元素直接控制权限
            if (AppFunctionType.AppPageDefinition.equalsIgnoreCase(resource.getType())
                    || AppFunctionType.AppWidgetFunctionElement.equalsIgnoreCase(resource.getType())
                    || AppFunctionType.AppModule.equalsIgnoreCase(resource.getType())) {
                if (!map.containsKey(resource.getType())) {
                    map.put(resource.getType(), new LinkedHashMap<Object, Collection<ConfigAttribute>>());
                }
                for (Role role : roles) {
                    SecurityConfig securityConfig = new SecurityResourceConfig(resourceUuid, role.getId());
                    if (!map.get(resource.getType()).containsKey(resourceUuid)) {
                        map.get(resource.getType()).put(resourceUuid, Lists.newArrayList());
                    }
                    map.get(resource.getType()).get(resourceUuid).add(securityConfig);
                }
                continue;
            }
            // FIXME: 7.0 不再使用6.2产品集成树，跳过其权限资源
            if (AppFunctionType.AppProductIntegration.equalsIgnoreCase(resource.getType())) {
                continue;
            }
            ResourceDataSource resourceDataSource = getResourceDataSource(resource.getType());
            if (StringUtils.isBlank(resourceUuid) || resourceDataSource == null) {
                continue;
            }
            Map<String, Collection<Object>> objectIdentityMap = resourceObjectIdentityMap.get(
                    resourceUuid);
            if (objectIdentityMap == null) {
                objectIdentityMap = resourceDataSource.getObjectIdentities(resourceUuid);
                resourceObjectIdentityMap.put(resourceUuid, objectIdentityMap);
            }

            for (Entry<String, Collection<Object>> entry : objectIdentityMap.entrySet()) {
                String functionType = entry.getKey();
                Collection<Object> objectIdentities = entry.getValue();
                if (!map.containsKey(functionType)) {
                    map.put(functionType, new LinkedHashMap<Object, Collection<ConfigAttribute>>());
                }
                Map<Object, Collection<ConfigAttribute>> functionMap = map.get(functionType);
                for (Object objectIdentity : objectIdentities) {
                    for (Role role : roles) {
                        String roleId = role.getId();
                        addSecurityConfigAttribute(resourceUuid, functionMap,
                                StringUtils.trim(objectIdentity.toString()), roleId);
                    }
                }
            }
        }
        stopWatch.stop();
        stopWatch.start("getAllResourceDataSources");
        // 2、不需要受保护的资源
        Collection<ResourceDataSource> resourceDataSources = getAllResourceDataSources();
        for (ResourceDataSource resourceDataSource : resourceDataSources) {
            if (AppFunctionType.AppProductIntegration.equalsIgnoreCase(resourceDataSource.getId())) {
                // FIXME: 7.0 不再使用6.2产品集成树，跳过其权限资源
                continue;
            }
            List<String> anonymousResourceIds = resourceDataSource.getAnonymousResources();
            for (String anonymousResourceId : anonymousResourceIds) {
                Map<String, Collection<Object>> anonymousObjectIdentityMap = resourceDataSource
                        .getObjectIdentities(anonymousResourceId);
                for (Entry<String, Collection<Object>> entry : anonymousObjectIdentityMap.entrySet()) {
                    String functionType = entry.getKey();
                    Collection<Object> anonymousObjectIdentities = entry.getValue();
                    if (!map.containsKey(functionType)) {
                        map.put(functionType,
                                new LinkedHashMap<Object, Collection<ConfigAttribute>>());
                    }
                    Map<Object, Collection<ConfigAttribute>> functionMap = map.get(functionType);
                    for (Object anonymousObjectIdentity : anonymousObjectIdentities) {
                        addAnonymousConfigAttribute(anonymousResourceId, functionMap,
                                StringUtils.trim(anonymousObjectIdentity.toString()));
                    }
                }
            }
        }
        stopWatch.stop();
        logger.info("extractPrivilegeResourceConfigAttribute 耗时 : {}", stopWatch.prettyPrint());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#get(java.lang.String)
     */
    @Override
    public Resource get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.ResourceService#getByCode(java.lang.String)
     */
    @Override
    public List<Resource> getByCode(String code) {
        return dao.listByFieldEqValue("code", code);
    }

    @Override
    public List<Resource> queryModuleSecurityMenuResource(Map<String, Object> params) {
        return dao.listByNameSQLQuery("appModuleMenuResManagerQuery", params);
    }

    @Override
    public long coutModuleMenus(String moduleId) {
        Resource resource = new Resource();
        resource.setModuleId(moduleId);
        return dao.countByEntity(resource);
    }

    @Override
    public void updateConfigAttributeByPrivilege(String privilegeUuid) {
        loadPrivilegeConfigAttribute(privilegeUuid);
    }

    @Override
    public List<ResourceDto> getModuleMenuResources(String moduleId) {
        List<Resource> resources = dao.listByFieldEqValue("moduleId", moduleId);
        if (CollectionUtils.isNotEmpty(resources)) {
            List<ResourceDto> resourceDtos = Lists.newArrayListWithCapacity(resources.size());
            for (Resource rs : resources) {
                ResourceDto dto = new ResourceDto(rs.getUuid(), rs.getCode(), rs.getName(), rs.getUrl(), rs.getApplyTo(), rs.getRemark());
                resourceDtos.add(dto);
            }
            return resourceDtos;
        }
        return null;
    }

    @Override
    public List<Resource> listModuleMenuResources(String moduleId) {
        return dao.listByFieldEqValue("moduleId", moduleId);
    }


}
