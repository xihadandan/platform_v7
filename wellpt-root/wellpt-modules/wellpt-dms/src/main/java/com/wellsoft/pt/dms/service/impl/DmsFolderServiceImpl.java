/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.dms.bean.DmsFolderAssignRoleBean;
import com.wellsoft.pt.dms.bean.DmsFolderConfigurationBean;
import com.wellsoft.pt.dms.dao.DmsFolderDao;
import com.wellsoft.pt.dms.entity.*;
import com.wellsoft.pt.dms.model.DmsFolder;
import com.wellsoft.pt.dms.service.*;
import com.wellsoft.pt.dms.support.FileStatus;
import com.wellsoft.pt.dms.support.FolderType;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
@Service
public class DmsFolderServiceImpl extends AbstractJpaServiceImpl<DmsFolderEntity, DmsFolderDao, String> implements DmsFolderService {

    private static final String COUNT_BY_PARENT_UUIDS = "select t1.parent_uuid as folder_uuid, count(t1.uuid) as child_count "
            + "from dms_folder t1 where t1.status = :status and t1.parent_uuid in(:folderUuids) group by t1.parent_uuid";

    @Autowired
    private DmsRoleService dmsRoleService;

    @Autowired
    private DmsFileService dmsFileService;

    @Autowired
    private DmsObjectIdentityService dmsObjectIdentityService;

    @Autowired
    private DmsObjectAssignRoleService dmsObjectAssignRoleService;

    @Autowired
    private DmsObjectAssignRoleItemService dmsObjectAssignRoleItemService;

    @Autowired
    private DmsFolderConfigurationService dmsFolderConfigurationService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#get(java.lang.String)
     */
    @Override
    public DmsFolderEntity get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * 获取所有数据
     *
     * @return
     */
    @Override
    public List<DmsFolderEntity> getAll() {
        return this.dao.listByEntity(new DmsFolderEntity());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#findByExample(DmsFolder)
     */
    @Override
    public List<DmsFolderEntity> findByExample(DmsFolderEntity example) {
        return this.dao.listByEntity(example);
    }

    @Override
    public List<QueryItem> listByParams(Map<String, Object> params, PagingInfo pagingInfo) {
        return this.dao.listQueryItemByNameHQLQuery("dmsFolderQuery", params, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#getRootFolders()
     */
    @Override
    public List<DmsFolderEntity> getRootFolders() {
        String hql = "from DmsFolderEntity t where t.status = 1 and t.parentUuid is null";
        return this.dao.listByHQL(hql, null);
    }

    @Override
    public List<DmsFolderEntity> listLibraryBySystem(String system) {
        if (StringUtils.isBlank(system)) {
            return Collections.emptyList();
        }
        String hql = "from DmsFolderEntity t where t.status = 1 and t.parentUuid is null";
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        return this.dao.listByHQL(hql, params);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#save(com.wellsoft.pt.dms.entity.DmsFolder)
     */
    @Override
    @Transactional
    public void save(DmsFolderEntity entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#saveAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void saveAll(Collection<DmsFolderEntity> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsFolderService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<DmsFolderEntity> entities) {
        this.dao.deleteByEntities(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#remove(DmsFolder)
     */
    @Override
    @Transactional
    public void remove(DmsFolderEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#removeAllByPk(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#countByParentUuid(java.lang.String)
     */
    @Override
    public Long countByParentUuid(String parentUuid) {
        Assert.hasLength(parentUuid, "上级夹UUID不能为空！");

        String hql = "from DmsFolderEntity t where t.parentUuid = :parentUuid and t.status <> :deleteStatus";
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentUuid", parentUuid);
        params.put("deleteStatus", FileStatus.DELETE);
        return this.dao.countByHQL(hql, params);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#countByParentUuids(java.util.List)
     */
    @Override
    public Map<String, Long> countByParentUuids(List<String> parentUuids) {
        Map<String, Long> countMap = Maps.newHashMap();
        Set<String> tmpFolderUuids = new HashSet<String>();
        int num = 0;
        for (String parentUuid : parentUuids) {
            num++;
            tmpFolderUuids.add(parentUuid);
            if (num % 1000 == 0 || num == parentUuids.size()) {
                Map<String, Object> values = Maps.newHashMap();
                values.put("folderUuids", tmpFolderUuids);
                values.put("status", FileStatus.NORMAL);
                List<QueryItem> queryItems = this.dao.listQueryItemBySQL(COUNT_BY_PARENT_UUIDS, values, new PagingInfo(1, Integer.MAX_VALUE));
                for (QueryItem queryItem : queryItems) {
                    countMap.put(queryItem.getString("folderUuid"), queryItem.getLong("childCount"));
                }
                tmpFolderUuids.clear();
            }
        }
        return countMap;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#addUserPrivateAdminPermission(java.lang.String)
     */
    @Override
    @Transactional
    public void addUserPrivateAdminPermission(String folderUuid) {
        DmsFolderEntity dmsFolderEntity = this.get(folderUuid);
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String userId = userDetails.getUserId();
        if (!StringUtils.equals(userDetails.getUserId(), dmsFolderEntity.getCreator())) {
            throw new RuntimeException("当前用户不是夹的创建者，不能设置私有权限！");
        }
        // 私有的管理员权限
        DmsRoleEntity dmsRoleEntity = dmsRoleService.getAdminRole();

        // 获取夹授权对象
        DmsObjectIdentityEntity dmsObjectIdentity = dmsObjectIdentityService.getOrCreate(dmsFolderEntity);
        String objectIdentityUuid = dmsObjectIdentity.getUuid();

        // 夹配置
        DmsFolderConfigurationEntity dmsFolderConfiguration = dmsFolderConfigurationService.getByFolderUuid(folderUuid);

        DmsFolderConfigurationBean dmsFolderConfigurationBean = JsonUtils.json2Object(
                dmsFolderConfiguration.getConfiguration(), DmsFolderConfigurationBean.class);
        dmsFolderConfigurationBean.setIsInheritFolderRole(0);
        // 添加管理员权限到夹中
        List<DmsFolderAssignRoleBean> dmsObjectAssignRoleBeans = dmsFolderConfigurationBean.getAssignRoles();
        dmsObjectAssignRoleBeans.clear();
        DmsFolderAssignRoleBean dmsFolderAssignRoleBean = new DmsFolderAssignRoleBean();
        dmsFolderAssignRoleBean.setRoleUuid(dmsRoleEntity.getUuid());
        dmsFolderAssignRoleBean.setRoleName(dmsRoleEntity.getName());
        dmsFolderAssignRoleBean.setPermit("Y");
        dmsFolderAssignRoleBean.setOrgIds(userId);
        dmsFolderAssignRoleBean.setOrgNames(userDetails.getUserName());
        dmsObjectAssignRoleBeans.add(dmsFolderAssignRoleBean);

        // 更新夹配置
        dmsFolderConfiguration.setIsInheritFolderRole(0);
        dmsFolderConfiguration.setRefObjectIdentityUuid(objectIdentityUuid);
        dmsFolderConfiguration.setConfiguration(JsonUtils.object2Json(dmsFolderConfigurationBean));
        dmsFolderConfigurationService.save(dmsFolderConfiguration);

        // 夹权限配置
        dmsObjectAssignRoleItemService.removeByObjectIdentityUuid(objectIdentityUuid);
        dmsObjectAssignRoleService.removeByObjectIdentityUuid(objectIdentityUuid);
        List<DmsObjectAssignRoleEntity> assignRoleEntities = BeanUtils.copyCollection(dmsObjectAssignRoleBeans,
                DmsObjectAssignRoleEntity.class);
        for (DmsObjectAssignRoleEntity assignRoleEntity : assignRoleEntities) {
            assignRoleEntity.setObjectIdentityUuid(objectIdentityUuid);
        }
        dmsObjectAssignRoleService.saveAll(assignRoleEntities);
        // 夹权限项配置
        for (DmsObjectAssignRoleEntity assignRoleEntity : assignRoleEntities) {
            List<String> orgIdArray = Arrays.asList(StringUtils.split(assignRoleEntity.getOrgIds(),
                    Separator.COMMA.getValue()));
            String assignRoleUuid = assignRoleEntity.getUuid();
            for (String orgId : orgIdArray) {
                DmsObjectAssignRoleItemEntity roleItemEntity = new DmsObjectAssignRoleItemEntity();
                roleItemEntity.setAssignRoleUuid(assignRoleUuid);
                roleItemEntity.setOrgId(orgId);
                dmsObjectAssignRoleItemService.save(roleItemEntity);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#isExists(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isExists(String childFolderName, String parentFolderUuid) {
        DmsFolderEntity example = new DmsFolderEntity();
        example.setName(childFolderName);
        example.setParentUuid(parentFolderUuid);
        example.setStatus(FileStatus.NORMAL);
        return this.dao.countByEntity(example) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#getByFolderNameAndParentFolderUuid(java.lang.String, java.lang.String)
     */
    @Override
    public DmsFolderEntity getByFolderNameAndParentFolderUuid(String childFolderName, String parentFolderUuid) {
        DmsFolderEntity example = new DmsFolderEntity();
        example.setName(childFolderName);
        example.setParentUuid(parentFolderUuid);
        example.setStatus(FileStatus.NORMAL);
        List<DmsFolderEntity> folderEntities = this.dao.listByEntity(example);
        if (!folderEntities.isEmpty()) {
            return folderEntities.get(0);
        }
        return null;
    }

    @Override
    public boolean existsFileUnderFolerIncludeChildFolder(String folderUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", folderUuid);
        return this.dao.countByNamedSQLQuery("existsFileUnderFolerIncludeChildFolder", params) > 0;
    }

    @Override
    public Map<String, Object> existsFolerChildFile(Collection<String> folderUuids) {
        List<String> tips = new ArrayList<>();
        for (String folderUuid : folderUuids) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("uuid", "%" + folderUuid + "%");
            String hql = "from DmsFolderEntity where uuidPath like :uuid order by createTime ";
            List<DmsFolderEntity> dmsFolderEntities = this.dao.listByHQL(hql, params);
            for (DmsFolderEntity dmsFolderEntity : dmsFolderEntities) {
                Long count = this.dmsFileService.countByFolderUuid(dmsFolderEntity.getUuid());
                if (count != null && count.longValue() > 0l) {
                    tips.add(dmsFolderEntity.getName());
                }
            }
        }

        Map<String, Object> returnMap = new HashMap<>();
        if (tips.size() > 0) {
            returnMap.put("flg", false);
            returnMap.put("tips", tips);
        } else {
            returnMap.put("flg", true);
        }
        return returnMap;
    }


    @Override
    @Transactional
    public void removeAllUuids(Collection<String> folderUuids) {
        Map<String, Object> map = this.existsFolerChildFile(folderUuids);
        if (!(Boolean) map.get("flg")) {
            throw new RuntimeException(String.format("子文件夹：[%s],有内容不能删除", StringUtils.join((List) map.get("tips"), ",")));
        }
        for (String folderUuid : folderUuids) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("uuid", "%" + folderUuid + "%");
            String hql = "from DmsFolderEntity where uuidPath like :uuid order by createTime ";
            List<DmsFolderEntity> dmsFolderEntities = this.dao.listByHQL(hql, params);
            this.dao.deleteByEntities(dmsFolderEntities);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderService#getByFileUuid(java.lang.String)
     */
    @Override
    public DmsFolderEntity getByFileUuid(String fileUuid) {
        String hql = "from DmsFolderEntity t1 where t1.uuid in(select t2.folderUuid from DmsFileEntity t2 where t2.uuid = :fileUuid)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("fileUuid", fileUuid);
        List<DmsFolderEntity> dmsFolderEntities = this.dao.listByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(dmsFolderEntities)) {
            return dmsFolderEntities.get(0);
        }
        return null;
    }

    /**
     * 列出文件库
     *
     * @return
     */
    @Override
    public List<DmsFolderEntity> listFileLibrary() {
        DmsFolderEntity entity = new DmsFolderEntity();

        String hql = "from DmsFolderEntity t where t.type = :type and t.status = :status order by t.code";
        Map<String, Object> params = Maps.newHashMap();
        params.put("status", FileStatus.NORMAL);
        params.put("type", FolderType.LIBRARY);
        return this.dao.listByHQL(hql, params);
    }

    /**
     * 更新夹编号
     *
     * @param folderCodes
     */
    @Override
    @Transactional
    public void updateFolderCodes(Map<String, String> folderCodes) {
        String hql = "update DmsFolderEntity t set t.code = :code where t.uuid = :uuid";
        for (Map.Entry<String, String> entry : folderCodes.entrySet()) {
            Map<String, Object> values = Maps.newHashMap();
            values.put("uuid", entry.getKey());
            values.put("code", entry.getValue());
            this.dao.updateByHQL(hql, values);
        }
    }

    /**
     * 根据UUID夹路径获取继承夹权限的子夹
     *
     * @param uuidPath
     * @return
     */
    @Override
    public List<DmsFolderEntity> listChildrenOfInheritFolderRoleByUuidPath(String uuidPath) {
        String hql = "from DmsFolderEntity t where t.uuidPath like '' || :uuidPath || '/%' and exists(from DmsFolderConfigurationEntity c where c.isInheritFolderRole = 1 and c.folderUuid = t.uuid) order by t.uuidPath asc";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuidPath", uuidPath);
        return this.dao.listByHQL(hql, values);
    }

    /**
     * 检测夹下是否存在指定文件夹名的夹
     *
     * @param folderName
     * @param parentFolderUuid
     * @return
     */
    @Override
    public boolean existsFolderNameByParentFolderUuid(String folderName, String parentFolderUuid) {
        Assert.hasLength(folderName, "文件夹名不能为空！");
        Assert.hasLength(parentFolderUuid, "上级夹UUID不能为空！");

        String hql = "from DmsFolderEntity t where t.name = :name and t.parentUuid = :parentUuid and t.status <> :deleteStatus";
        Map<String, Object> params = Maps.newHashMap();
        params.put("name", folderName);
        params.put("parentUuid", parentFolderUuid);
        params.put("deleteStatus", FileStatus.DELETE);
        return this.dao.countByHQL(hql, params) > 0;
    }

    /**
     * 检查同一目录下文件夹名是否重复
     *
     * @param folderName
     * @param folderUuid
     * @return
     */
    @Override
    public boolean existsTheSameFolderNameWidthFolderUuid(String folderName, String folderUuid) {
        Assert.hasLength(folderName, "文件夹名不能为空！");
        Assert.hasLength(folderUuid, "夹UUID不能为空！");

        String hql = "from DmsFolderEntity t1 where t1.name = :folderName and t1.uuid <> :folderUuid and t1.status <> :deleteStatus and t1.parentUuid = (select t2.parentUuid from DmsFolderEntity t2 where t2.uuid = :folderUuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("folderName", folderName);
        params.put("folderUuid", folderUuid);
        params.put("deleteStatus", FileStatus.DELETE);
        return this.dao.countByHQL(hql, params) > 0;
    }

    @Override
    public List<String> listFolderUuidByLibraryUuid(String libraryUuid) {
        String hql = "select t.uuid from DmsFolderEntity t where t.status = :status and t.libraryUuid = :libraryUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("libraryUuid", libraryUuid);
        params.put("status", FileStatus.NORMAL);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public Map<String, String> listFolderPathNameAsMap(Collection<String> folderUuids) {
        if (CollectionUtils.isEmpty(folderUuids)) {
            return Collections.emptyMap();
        }

        Map<String, String> folderPathNameMap = Maps.newHashMap();
        List<DmsFolderEntity> folderEntities = this.listByUuids(Lists.newArrayList(folderUuids));

        Set<String> uuidPaths = Sets.newHashSet();
        folderEntities.forEach(dmsFolderEntity -> {
            folderPathNameMap.put(dmsFolderEntity.getUuid(), Separator.SLASH.getValue() + dmsFolderEntity.getName());
            uuidPaths.addAll(Arrays.asList(StringUtils.split(dmsFolderEntity.getUuidPath(), Separator.SLASH.getValue())));
            uuidPaths.remove(dmsFolderEntity.getUuid());
        });
        if (CollectionUtils.isEmpty(uuidPaths)) {
            return folderPathNameMap;
        }

        Map<String, Object> values = Maps.newHashMap();
        values.put("folderUuids", uuidPaths);
        List<DmsFolderEntity> pathFolderEntities = Lists.newArrayList(this.listByUuids(Lists.newArrayList(uuidPaths)));
        Map<String, DmsFolderEntity> pathFolderMap = pathFolderEntities.stream().collect(Collectors.toMap(DmsFolderEntity::getUuid, Function.identity()));
        folderEntities.forEach(dmsFolderEntity -> {
            if (!pathFolderMap.containsKey(dmsFolderEntity.getUuid())) {
                pathFolderMap.put(dmsFolderEntity.getUuid(), dmsFolderEntity);
            }
        });
        folderEntities.forEach(dmsFolderEntity -> {
            List<String> pathNames = Lists.newArrayList();
            List<String> pathUuids = Arrays.asList(StringUtils.split(dmsFolderEntity.getUuidPath(), Separator.SLASH.getValue()));
            pathUuids.forEach(pathUuid -> {
                DmsFolderEntity folderEntity = pathFolderMap.get(pathUuid);
                if (folderEntity != null) {
                    pathNames.add(folderEntity.getName());
                } else {
                    pathNames.add(pathUuid);
                }
            });
            folderPathNameMap.put(dmsFolderEntity.getUuid(), Separator.SLASH.getValue() + String.join(Separator.SLASH.getValue(), pathNames));
        });
        return folderPathNameMap;
    }

}
