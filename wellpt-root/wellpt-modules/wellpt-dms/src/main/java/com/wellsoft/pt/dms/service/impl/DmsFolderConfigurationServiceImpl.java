/*
 * @(#)2017-12-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.design.config.BootstrapTableConfiguration;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreDefinitionBean;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateCategoryService;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.dms.bean.DmsFolderAssignRoleBean;
import com.wellsoft.pt.dms.bean.DmsRoleBean;
import com.wellsoft.pt.dms.core.support.DataType;
import com.wellsoft.pt.dms.dao.DmsFolderConfigurationDao;
import com.wellsoft.pt.dms.entity.*;
import com.wellsoft.pt.dms.facade.service.DmsRoleMgr;
import com.wellsoft.pt.dms.file.store.DmsFileQueryInterface;
import com.wellsoft.pt.dms.model.DmsFolderConfiguration;
import com.wellsoft.pt.dms.model.DmsFolderDataViewConfiguration;
import com.wellsoft.pt.dms.model.DmsFolderDyformDefinition;
import com.wellsoft.pt.dms.service.*;
import com.wellsoft.pt.jpa.datasource.DatabaseType;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.audit.bean.RoleDto;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-12-21.1	zhulh		2017-12-21		Create
 * </pre>
 * @date 2017-12-21
 */
@Service
public class DmsFolderConfigurationServiceImpl extends AbstractJpaServiceImpl<DmsFolderConfigurationEntity, DmsFolderConfigurationDao, String>
        implements DmsFolderConfigurationService {

    private String GET_FOLDER_DATA_DEFINITION_BY_FOLDER_UUID = "select t2.configuration from dms_folder t1 inner join dms_folder_configuration t2 on t1.uuid = t2.folder_uuid start with t1.uuid = :folderUuid connect by prior t1.parent_uuid = t1.uuid order by t1.uuid_path desc";

    private String GET_FOLDER_DATA_DEFINITION_BY_FILE_UUID = "select t2.configuration from dms_folder t1 inner join dms_folder_configuration t2 on t1.uuid = t2.folder_uuid start with t1.uuid = (select t3.folder_uuid from dms_file t3 where t3.uuid = :fileUuid) connect by prior t1.parent_uuid = t1.uuid order by t1.uuid_path desc";

    @Autowired
    private DmsFolderService dmsFolderService;

    @Autowired
    private DmsObjectIdentityService dmsObjectIdentityService;

    @Autowired
    private DmsObjectAssignRoleService dmsObjectAssignRoleService;

    @Autowired
    private DmsObjectAssignRoleItemService dmsObjectAssignRoleItemService;

    @Autowired
    private CdDataStoreDefinitionService cdDataStoreDefinitionService;
    @Autowired
    private PrintTemplateCategoryService printTemplateCategoryService;
    @Autowired
    private PrintTemplateService printTemplateService;
    @Autowired
    private List<DmsFileQueryInterface> dmsFileQueryInterfaceList;

    @Autowired
    private DmsRoleMgr dmsRoleMgr;
    @Autowired
    private DmsRoleService dmsRoleService;
    @Autowired
    private RoleService roleService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#get(java.lang.String)
     */
    @Override
    public DmsFolderConfigurationEntity get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#getByFolderUuid(java.lang.String)
     */
    @Override
    public DmsFolderConfigurationEntity getByFolderUuid(String folderUuid) {
        return this.dao.getOneByFieldEq("folderUuid", folderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#findByExample(DmsFolderConfiguration)
     */
    @Override
    public List<DmsFolderConfigurationEntity> findByExample(DmsFolderConfigurationEntity example) {
        return this.dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#save(com.wellsoft.pt.dms.entity.DmsFolderConfiguration)
     */
    @Override
    @Transactional
    public void save(DmsFolderConfigurationEntity entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#saveAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void saveAll(Collection<DmsFolderConfigurationEntity> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#saveFolderConfiguration(com.wellsoft.pt.dms.model.DmsFolderConfiguration)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    @Transactional
    public void saveFolderConfiguration(DmsFolderConfiguration folderConfiguration) {
        String folderUuid = folderConfiguration.getFolderUuid();
        DmsFolderEntity entity = dmsFolderService.get(folderUuid);
        // 获取夹授权对象
        DmsObjectIdentityEntity dmsObjectIdentity = dmsObjectIdentityService.getOrCreate(entity);
        String objectIdentityUuid = dmsObjectIdentity.getUuid();

        // 夹配置
        DmsFolderConfigurationEntity folderConfigurationEntity = this.getByFolderUuid(folderUuid);
        if (folderConfigurationEntity == null) {
            folderConfigurationEntity = new DmsFolderConfigurationEntity();
            folderConfigurationEntity.setUuid(SnowFlake.getId() + StringUtils.EMPTY);
        }

        // 判断是否变更权限继承方式
        Integer currentIsInheritFolderRole = folderConfigurationEntity.getIsInheritFolderRole();
        Integer newIsInheritFolderRole = folderConfiguration.getIsInheritFolderRole();
        boolean isChangeInheritFolderRole = currentIsInheritFolderRole == null ? true
                : !currentIsInheritFolderRole.equals(newIsInheritFolderRole);

        // 夹实体信息
        // 夹UUID
        folderConfigurationEntity.setFolderUuid(folderUuid);
        // 阅读人员字段
        folderConfigurationEntity.setReadFileField(folderConfiguration.getReadFileField());
        // 编辑人员字段
        folderConfigurationEntity.setEditFileField(folderConfiguration.getEditFileField());
        // 是否继承夹的权限配置，1是，0否
        Integer isInheritFolderRole = folderConfiguration.getIsInheritFolderRole();
        folderConfigurationEntity.setIsInheritFolderRole(isInheritFolderRole);
        // 引用的授权对象UUID
        if (Integer.valueOf(1).equals(isInheritFolderRole)) {
            folderConfigurationEntity
                    .setRefObjectIdentityUuid(getRefObjectIdentityUuidForInheritFolderRole(entity, objectIdentityUuid));
        } else {
            folderConfigurationEntity.setRefObjectIdentityUuid(objectIdentityUuid);
        }
        // 配置JSON信息
        folderConfigurationEntity.setConfiguration(JsonUtils.object2Json(folderConfiguration));
        this.save(folderConfigurationEntity);

        // 保存管理者、编辑者、阅读者
        saveFolderBuildRoles(entity, folderConfiguration);

        // 更新子夹的权限变更方式
        if (isChangeInheritFolderRole) {
            String refObjectIdentityUuid = folderConfigurationEntity.getRefObjectIdentityUuid();
            updateChildrenRefObjectIdentityOfInheritFolderRole(entity, refObjectIdentityUuid);
        }

        // 夹权限配置
        dmsObjectAssignRoleItemService.removeByObjectIdentityUuid(objectIdentityUuid);
        dmsObjectAssignRoleService.removeByObjectIdentityUuid(objectIdentityUuid);
        List<DmsFolderAssignRoleBean> dmsObjectAssignRoleBeans = folderConfiguration.getAssignRoles();
        List<DmsObjectAssignRoleEntity> assignRoleEntities = BeanUtils.copyCollection(dmsObjectAssignRoleBeans,
                DmsObjectAssignRoleEntity.class);
        for (DmsObjectAssignRoleEntity assignRoleEntity : assignRoleEntities) {
            assignRoleEntity.setObjectIdentityUuid(objectIdentityUuid);
            if (StringUtils.isBlank(assignRoleEntity.getPermit())) {
                assignRoleEntity.setPermit("Y");
            }
            if (StringUtils.isBlank(assignRoleEntity.getDeny())) {
                assignRoleEntity.setDeny("N");
            }
        }
        // 继承上级夹，清空当前配置的权限
        if (Integer.valueOf(1).equals(isInheritFolderRole)) {
            assignRoleEntities.clear();
        }
        dmsObjectAssignRoleService.saveAll(assignRoleEntities);
        // 夹权限项配置
        for (DmsObjectAssignRoleEntity assignRoleEntity : assignRoleEntities) {
            List<String> orgIdArray = Arrays
                    .asList(StringUtils.split(assignRoleEntity.getOrgIds(), Separator.COMMA.getValue() + Separator.SEMICOLON.getValue()));
            String assignRoleUuid = assignRoleEntity.getUuid();
            List<DmsObjectAssignRoleItemEntity> roleItemEntities = Lists.newArrayList();
            for (String orgId : orgIdArray) {
                DmsObjectAssignRoleItemEntity roleItemEntity = new DmsObjectAssignRoleItemEntity();
                roleItemEntity.setAssignRoleUuid(assignRoleUuid);
                roleItemEntity.setOrgId(orgId);
                if (!IdPrefix.hasPrefix(orgId)) {
                    Role role = roleService.get(orgId);
                    if (role != null) {
                        roleItemEntity.setOrgId(role.getId());
                    }
                }
                roleItemEntities.add(roleItemEntity);
            }
            // 添加默认的租户管理员角色
            DmsObjectAssignRoleItemEntity roleItemEntity = new DmsObjectAssignRoleItemEntity();
            roleItemEntity.setAssignRoleUuid(assignRoleUuid);
            roleItemEntity.setOrgId(BuildInRole.ROLE_TENANT_ADMIN.name());
            roleItemEntities.add(roleItemEntity);
            dmsObjectAssignRoleItemService.saveAll(roleItemEntities);
        }
    }

    /**
     * 更新源文件夹的继承的上级夹权限引用为目标夹
     *
     * @param folderUuid
     * @param refObjectIdentityUuidOfFolderUuid
     */
    @Override
    @Transactional
    public void updateInheritFolderRole(String folderUuid, String refObjectIdentityUuidOfFolderUuid) {
        DmsFolderConfigurationEntity configurationEntity = this.getByFolderUuid(folderUuid);
        if (configurationEntity == null || Integer.valueOf(0).equals(configurationEntity.getIsInheritFolderRole())) {
            return;
        }
        DmsFolderEntity folderEntity = dmsFolderService.get(folderUuid);
        DmsFolderConfigurationEntity destConfigurationEntity = this.getByFolderUuid(refObjectIdentityUuidOfFolderUuid);
        String refObjectIdentityUuid = destConfigurationEntity.getRefObjectIdentityUuid();

        // 更新当前夹
        configurationEntity.setRefObjectIdentityUuid(refObjectIdentityUuid);
        this.save(configurationEntity);
        // 更新子夹
        updateChildrenRefObjectIdentityOfInheritFolderRole(folderEntity, refObjectIdentityUuid);
    }

    /**
     * @param folderConfiguration
     */
    private void saveFolderBuildRoles(DmsFolderEntity folderEntity, DmsFolderConfiguration folderConfiguration) {
        String administratorRoleId = "administrator_" + folderConfiguration.getFolderUuid();
        String editorRoleId = "editor_" + folderConfiguration.getFolderUuid();
        String readerRoleId = "reader_" + folderConfiguration.getFolderUuid();
        String administrator = Objects.toString(folderConfiguration.getAdministrator(), StringUtils.EMPTY);
        List<String> administratorActions = folderConfiguration.getAdministratorActions();
        String editor = Objects.toString(folderConfiguration.getEditor(), StringUtils.EMPTY);
        List<String> editorActions = folderConfiguration.getEditorActions();
        String reader = Objects.toString(folderConfiguration.getReader(), StringUtils.EMPTY);
        List<String> readActions = folderConfiguration.getReaderActions();

        if (StringUtils.isNotBlank(administrator) && CollectionUtils.isNotEmpty(administratorActions)) {
            updateAssignRole(administratorRoleId, "管理者", administrator, administratorActions, folderEntity,
                    folderConfiguration, BuildInRole.ROLE_TENANT_ADMIN.name());
        }

        if (StringUtils.isNotBlank(editor) && CollectionUtils.isNotEmpty(editorActions)) {
            updateAssignRole(editorRoleId, "编辑者", editor, editorActions, folderEntity, folderConfiguration);
        }

        if (StringUtils.isNotBlank(reader) && CollectionUtils.isNotEmpty(readActions)) {
            updateAssignRole(readerRoleId, "阅读者", reader, readActions, folderEntity, folderConfiguration);
        }
    }

    /**
     * @param dmsRoleId
     * @param dmsRoleNameSuffix
     * @param orgIds
     * @param folderEntity
     * @param folderConfiguration
     */
    private void updateAssignRole(String dmsRoleId, String dmsRoleNameSuffix, String orgIds, List<String> actions, DmsFolderEntity folderEntity,
                                  DmsFolderConfiguration folderConfiguration, String... appendRoleIds) {
        DmsRoleEntity roleEntity = dmsRoleService.getById(dmsRoleId);
        DmsRoleBean roleBean = null;
        if (roleEntity == null) {
            roleBean = new DmsRoleBean();
            roleBean.setName(folderEntity.getName() + "_" + dmsRoleNameSuffix);
            roleBean.setId(dmsRoleId);
            roleBean.setCode(dmsRoleId);
            if (CollectionUtils.isNotEmpty(actions)) {
                roleBean.setActions(StringUtils.join(actions, Separator.COMMA.getValue()));
            }
            String dmsRoleUuid = dmsRoleMgr.saveBean(roleBean);
            roleEntity = dmsRoleService.get(dmsRoleUuid);
        } else {
            roleBean = dmsRoleMgr.getBean(roleEntity.getUuid());
            roleBean.setName(folderEntity.getName() + "_" + dmsRoleNameSuffix);
            if (CollectionUtils.isNotEmpty(actions)) {
                roleBean.setActions(StringUtils.join(actions, Separator.COMMA.getValue()));
            } else {
                roleBean.setActions(StringUtils.EMPTY);
            }
            String dmsRoleUuid = dmsRoleMgr.saveBean(roleBean);
            roleEntity = dmsRoleService.get(dmsRoleUuid);
        }

        List<String> orgIdList = Lists.newArrayList(StringUtils.split(orgIds, Separator.SEMICOLON.getValue()));
        // 附加的角色
        if (appendRoleIds != null && appendRoleIds.length > 0) {
            orgIdList.addAll(Arrays.asList(appendRoleIds));
        }
        // 组织角色UUID转换为角色ID
        if (CollectionUtils.isNotEmpty(orgIdList)) {
            List<RoleDto> roleDtos = roleService.getRolesByUuids(orgIdList);
            roleDtos.forEach(roleDto -> {
                if (orgIdList.contains(roleDto.getUuid())) {
                    orgIdList.remove(roleDto.getUuid());
                    orgIdList.add(roleDto.getId());
                }
            });
        }

        String roleUuid = roleEntity.getUuid();
        List<DmsFolderAssignRoleBean> assignRoleBeans = Lists.newArrayList(folderConfiguration.getAssignRoles());
        DmsFolderAssignRoleBean assignRoleBean = assignRoleBeans.stream().filter(item -> StringUtils.equals(item.getRoleUuid(), roleUuid))
                .findFirst().orElse(null);
        if (assignRoleBean == null) {
            assignRoleBean = new DmsFolderAssignRoleBean();
            assignRoleBeans.add(assignRoleBean);
        }
        assignRoleBean.setRoleUuid(roleEntity.getUuid());
        assignRoleBean.setRoleName(roleEntity.getName());
        assignRoleBean.setOrgIds(StringUtils.join(orgIdList, Separator.COMMA.getValue()));
        assignRoleBean.setPermit("Y");
        assignRoleBean.setDeny("N");
        folderConfiguration.setAssignRoles(assignRoleBeans);
    }

    /**
     * 更新子夹的权限配置为继承上级夹的配置引用
     *
     * @param entity
     * @param refObjectIdentityUuid
     */
    private void updateChildrenRefObjectIdentityOfInheritFolderRole(DmsFolderEntity entity,
                                                                    String refObjectIdentityUuid) {
        String uuidPath = entity.getUuidPath();
        List<DmsFolderEntity> dmsFolderEntities = this.dmsFolderService.listChildrenOfInheritFolderRoleByUuidPath(uuidPath);
        Map<String, String> uuidPathMap = new HashMap<String, String>();
        uuidPathMap.put(uuidPath, uuidPath);
        // 获取连继的直接下级夹配置，根据uuidPath去过滤匹配
        List<String> directChildrenUuids = new ArrayList<String>();
        for (DmsFolderEntity dmsFolderEntity : dmsFolderEntities) {
            String childUuidPath = dmsFolderEntity.getUuidPath();
            String parentUuidPath = StringUtils.substring(childUuidPath, 0,
                    StringUtils.lastIndexOf(childUuidPath, "/"));
            if (uuidPathMap.containsKey(parentUuidPath)) {
                uuidPathMap.put(childUuidPath, childUuidPath);
                directChildrenUuids.add(dmsFolderEntity.getUuid());
            }
        }
        if (!directChildrenUuids.isEmpty()) {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("refObjectIdentityUuid", refObjectIdentityUuid);
            values.put("directChildrenUuids", directChildrenUuids);
            String updateHql = "update DmsFolderConfigurationEntity t set t.refObjectIdentityUuid = :refObjectIdentityUuid where t.isInheritFolderRole = 1 and t.folderUuid in(:directChildrenUuids)";
            this.dao.updateByHQL(updateHql, values);
        }
    }

    /**
     * @param entity
     * @return
     */
    private String getRefObjectIdentityUuidForInheritFolderRole(DmsFolderEntity entity,
                                                                String defaultRefObjectIdentityUuid) {
        String parentUuid = entity.getParentUuid();
        if (StringUtils.isBlank(parentUuid)) {
            return defaultRefObjectIdentityUuid;
        }
        DmsFolderConfigurationEntity dmsFolderConfiguration = this.getByFolderUuid(parentUuid);
        return dmsFolderConfiguration.getRefObjectIdentityUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsFolderConfigurationService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<DmsFolderConfigurationEntity> entities) {
        this.dao.deleteByEntities(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#remove(DmsFolderConfiguration)
     */
    @Override
    @Transactional
    public void remove(DmsFolderConfigurationEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#removeAllByPk(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#getFolderDataDefinitionByFolderUuid(java.lang.String)
     */
    @Override
    public DmsFolderDyformDefinition getFolderDyformDefinitionByFolderUuid(String folderUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        List<DmsFolderConfigurationEntity> configurations = null;
        String dataBaseType = Config.getValue("database.type");
        if (DatabaseType.MySQL5.getName().equalsIgnoreCase(dataBaseType)) {
            configurations = listWithAllParentFolderConfigurationByFolderUuid(folderUuid);
        } else {
            configurations = this.dao.listBySQL(GET_FOLDER_DATA_DEFINITION_BY_FOLDER_UUID, values);
        }
        return conver2FolderDyformDefinition(configurations);
    }

    /**
     * @param folderUuid
     * @return
     */
    private List<DmsFolderConfigurationEntity> listWithAllParentFolderConfigurationByFolderUuid(String folderUuid) {
        List<DmsFolderConfigurationEntity> configurationEntities = Lists.newArrayList();
        DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
        if (dmsFolderEntity != null) {
            String[] uuidPaths = StringUtils.split(dmsFolderEntity.getUuidPath(), Separator.SLASH.getValue());
            for (int index = uuidPaths.length - 1; index >= 0; index--) {
                DmsFolderConfigurationEntity configurationEntity = getByFolderUuid(uuidPaths[index]);
                configurationEntities.add(configurationEntity);
            }
        }

        return configurationEntities;
    }

    /**
     * @param fileUuid
     * @return
     */
    private List<DmsFolderConfigurationEntity> listWithAllParentFolderConfigurationByFileUuid(String fileUuid) {
        List<DmsFolderConfigurationEntity> configurationEntities = Lists.newArrayList();
        DmsFolderEntity dmsFolderEntity = dmsFolderService.getByFileUuid(fileUuid);
        String[] uuidPaths = StringUtils.split(dmsFolderEntity.getUuidPath(), Separator.SLASH.getValue());
        for (int index = uuidPaths.length - 1; index >= 0; index--) {
            DmsFolderConfigurationEntity configurationEntity = getByFolderUuid(uuidPaths[index]);
            configurationEntities.add(configurationEntity);
        }
        return configurationEntities;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#getAllFolderDyformDefinitionsByFolderUuid(java.lang.String)
     */
    @Override
    public List<DmsFolderDyformDefinition> getAllFolderDyformDefinitionsByFolderUuid(String folderUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        List<DmsFolderConfigurationEntity> configurations = this.dao
                .listByNameSQLQuery("getAllFolderDyformDefinitionsByFolderUuid", values);
        List<DmsFolderDyformDefinition> folderDyformDefinitions = conver2FolderDyformDefinitions(configurations);
        Set<DmsFolderDyformDefinition> folderDyformDefinitionSet = Sets.newLinkedHashSet();
        folderDyformDefinitionSet.addAll(folderDyformDefinitions);
        if (CollectionUtils.isEmpty(folderDyformDefinitionSet)) {
            DmsFolderDyformDefinition dmsFolderDyformDefinition = getFolderDyformDefinitionByFolderUuid(folderUuid);
            if (dmsFolderDyformDefinition != null) {
                folderDyformDefinitionSet.add(dmsFolderDyformDefinition);
            }
        }
        return Arrays.asList(folderDyformDefinitionSet.toArray(new DmsFolderDyformDefinition[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#getFolderDyformDefinitionByFileUuid(java.lang.String)
     */
    @Override
    public DmsFolderDyformDefinition getFolderDyformDefinitionByFileUuid(String fileUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("fileUuid", fileUuid);
        List<DmsFolderConfigurationEntity> configurations = null;
        String dataBaseType = Config.getValue("database.type");
        if (DatabaseType.MySQL5.getName().equalsIgnoreCase(dataBaseType)) {
            configurations = listWithAllParentFolderConfigurationByFileUuid(fileUuid);
        } else {
            configurations = this.dao.listBySQL(GET_FOLDER_DATA_DEFINITION_BY_FILE_UUID, values);
        }
        return conver2FolderDyformDefinition(configurations);
    }

    /**
     * 如何描述该方法
     *
     * @param configurations
     */
    private DmsFolderDyformDefinition conver2FolderDyformDefinition(List<DmsFolderConfigurationEntity> configurations) {
        DmsFolderDyformDefinition dmsFolderDyformDefinition = null;
        for (DmsFolderConfigurationEntity config : configurations) {
            DmsFolderConfiguration configuration = JsonUtils.json2Object(config.getConfiguration(),
                    DmsFolderConfiguration.class);
            String dataType = configuration.getDataType();
            String formUuid = configuration.getFormUuid();
            if ((DataType.DYFORM.getId().equals(dataType) || DataType.MIXTURE.getId().equals(dataType))
                    && StringUtils.isNotBlank(formUuid)) {
                dmsFolderDyformDefinition = convert2FolderDyformDefinition(configuration);
                break;
            }
        }
        return dmsFolderDyformDefinition;
    }

    /**
     * @param configurations
     * @return
     */
    private List<DmsFolderDyformDefinition> conver2FolderDyformDefinitions(
            List<DmsFolderConfigurationEntity> configurations) {
        List<DmsFolderDyformDefinition> dmsFolderDyformDefinitions = Lists.newArrayList();
        for (DmsFolderConfigurationEntity config : configurations) {
            DmsFolderConfiguration configuration = JsonUtils.json2Object(config.getConfiguration(),
                    DmsFolderConfiguration.class);
            String dataType = configuration.getDataType();
            String formUuid = configuration.getFormUuid();
            if ((DataType.DYFORM.getId().equals(dataType) || DataType.MIXTURE.getId().equals(dataType))
                    && StringUtils.isNotBlank(formUuid)) {
                dmsFolderDyformDefinitions.add(convert2FolderDyformDefinition(configuration));
            }
        }
        return dmsFolderDyformDefinitions;
    }

    /**
     * @param configuration
     * @return
     */
    private DmsFolderDyformDefinition convert2FolderDyformDefinition(DmsFolderConfiguration configuration) {
        DmsFolderDyformDefinition dmsFolderDyformDefinition = new DmsFolderDyformDefinition();
        dmsFolderDyformDefinition.setDataType(configuration.getDataType());
        dmsFolderDyformDefinition.setFormUuid(configuration.getFormUuid());
        dmsFolderDyformDefinition.setFormName(configuration.getFormName());
        dmsFolderDyformDefinition.setDisplayFormUuid(configuration.getDisplayFormUuid());
        dmsFolderDyformDefinition.setDisplayFormName(configuration.getDisplayFormName());
        dmsFolderDyformDefinition.setDisplayMFormUuid(configuration.getDisplayMFormUuid());
        dmsFolderDyformDefinition.setDisplayMFormName(configuration.getDisplayMFormName());
        dmsFolderDyformDefinition.setFileNameField(configuration.getFileNameField());
        dmsFolderDyformDefinition.setFileStatusField(configuration.getFileStatusField());
        dmsFolderDyformDefinition.setEditFileField(configuration.getEditFileField());
        dmsFolderDyformDefinition.setReadFileField(configuration.getReadFileField());
        dmsFolderDyformDefinition.setStick(configuration.isStick());
        dmsFolderDyformDefinition.setStickStatusField(configuration.getStickStatusField());
        dmsFolderDyformDefinition.setStickTimeField(configuration.getStickTimeField());
        dmsFolderDyformDefinition.setReadRecord(configuration.isReadRecord());
        dmsFolderDyformDefinition.setReadRecordField(configuration.getReadRecordField());
        return dmsFolderDyformDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#getFolderDataViewConfigurationByFolderUuid(java.lang.String)
     */
    @Override
    public DmsFolderDataViewConfiguration getFolderDataViewConfigurationByFolderUuid(String folderUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        List<DmsFolderConfigurationEntity> configurations = null;
        String dataBaseType = Config.getValue("database.type");
        if (DatabaseType.MySQL5.getName().equalsIgnoreCase(dataBaseType)) {
            configurations = listWithAllParentFolderConfigurationByFolderUuid(folderUuid);
        } else {
            configurations = this.dao.listBySQL(GET_FOLDER_DATA_DEFINITION_BY_FOLDER_UUID, values);
        }
        return conver2FolderDataViewConfiguration(configurations);
    }

    /**
     * @param configurations
     * @return
     */
    private DmsFolderDataViewConfiguration conver2FolderDataViewConfiguration(
            List<DmsFolderConfigurationEntity> configurations) {
        DmsFolderDataViewConfiguration dmsFolderDataViewConfiguration = new DmsFolderDataViewConfiguration();
        int index = 0;
        for (DmsFolderConfigurationEntity config : configurations) {
            DmsFolderConfiguration configuration = JsonUtils.json2Object(config.getConfiguration(),
                    DmsFolderConfiguration.class);
            String listViewId = configuration.getListViewId();
            if (StringUtils.isNotBlank(listViewId)) {
                dmsFolderDataViewConfiguration.setListViewId(listViewId);
                dmsFolderDataViewConfiguration.setInherit(index > 0);
                // 判断是否为文件库数据源
                BootstrapTableConfiguration bootstrapTableConfiguration = WidgetDefinitionUtils
                        .getWidgetConfigurationById(BootstrapTableConfiguration.class, listViewId);
                String dataStoreId = bootstrapTableConfiguration.getDataStoreId();
                CdDataStoreDefinitionBean cdDataStoreDefinitionBean = cdDataStoreDefinitionService
                        .getBeanById(dataStoreId);
                String dataInterfaceName = cdDataStoreDefinitionBean.getDataInterfaceName();
                boolean isFileManagerDataStore = false;
                for (DmsFileQueryInterface dmsFileQueryInterface : dmsFileQueryInterfaceList) {
                    if (StringUtils.equals(dmsFileQueryInterface.getClass().getCanonicalName(), dataInterfaceName)) {
                        isFileManagerDataStore = true;
                        break;
                    }
                }
                dmsFolderDataViewConfiguration.setFileManagerDataStore(isFileManagerDataStore);
                break;
            }
            index++;
        }
        return dmsFolderDataViewConfiguration;
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#getFolderDataTypeByFolderUuid(java.lang.String)
     */
    @Override
    public List<String> getFolderDataTypeByFolderUuid(String folderUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        List<DmsFolderConfigurationEntity> configurations = null;
        String dataBaseType = Config.getValue("database.type");
        if (DatabaseType.MySQL5.getName().equalsIgnoreCase(dataBaseType)) {
            configurations = listWithAllParentFolderConfigurationByFolderUuid(folderUuid);
        } else {
            configurations = this.dao.listBySQL(GET_FOLDER_DATA_DEFINITION_BY_FOLDER_UUID, values);
        }
        return extractDataType(configurations);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFolderConfigurationService#getFolderDataTypeByFileUuid(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getFolderDataTypeByFileUuid(String fileUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("fileUuid", fileUuid);
        List<DmsFolderConfigurationEntity> configurations = null;
        String dataBaseType = Config.getValue("database.type");
        if (DatabaseType.MySQL5.getName().equalsIgnoreCase(dataBaseType)) {
            configurations = listWithAllParentFolderConfigurationByFileUuid(fileUuid);
        } else {
            configurations = this.dao.listBySQL(GET_FOLDER_DATA_DEFINITION_BY_FILE_UUID, values);
        }
        return extractDataType(configurations);
    }

    @Override
    public List<TreeNode> getPrintTemplateTreeByUser() {
        List<PrintTemplateCategory> printTemplateCategoryList = null;
        List<PrintTemplate> printTemplateList = null;
        final String superAdminName = "admin";
        if (superAdminName.equals(SpringSecurityUtils.getCurrentLoginName())) {
            // 1、超管用户：可见超管和全部系统单位定义的打印模板
            printTemplateCategoryList = printTemplateCategoryService.getAllBySystemUnitIds();
            printTemplateList = printTemplateService.findAll();

        } else {
            // 2、单位管理员：可见超管和当前系统单位中创建的打印模板
            List<String> systemUnitIds = Lists.newArrayList();
            final String PT_UNIT_ID = "S0000000000";
            systemUnitIds.add(PT_UNIT_ID);
            systemUnitIds.add(SpringSecurityUtils.getCurrentUserUnitId());
            printTemplateCategoryList = printTemplateCategoryService.getAllBySystemUnitIds(systemUnitIds);
            printTemplateList = getAllBySystemUnitIds(systemUnitIds);
        }
        return printTemplateService.getPrintTemplateTree(printTemplateCategoryList, printTemplateList);
    }

    @Override
    public List<TreeNode> getPrintTemplateTreeByFolderUuids(List<String> folderUuids) {
        List<TreeNode> treeNodeList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(folderUuids)) {
            Boolean existParentTemplateUuidListNullFlag = Boolean.FALSE;
            Set<String> printTemplateUuidList = Sets.newHashSet();
            for (String folderUuid : folderUuids) {
                Set<String> printTemplateUuidListTemp = getPrintTemplateUuidListByFolderUuid(folderUuid);
                if (printTemplateUuidListTemp.size() == 0) {
                    existParentTemplateUuidListNullFlag = Boolean.TRUE;
                    break;
                }
                printTemplateUuidList.addAll(printTemplateUuidListTemp);
            }
            // 3、当文档所在文件夹及其所有父级夹都未设置打印模板时，获取全部打印模板（全部打印模板：目前是超管和当前系统单位的）
            if (existParentTemplateUuidListNullFlag) {
                return getPrintTemplateTreeByUser();
            }

            List<PrintTemplate> printTemplates = getPrintTemplateList(printTemplateUuidList);

            List<PrintTemplateCategory> printTemplateCategories = getPrintTemplateCategorieList(printTemplates);
            treeNodeList.addAll(printTemplateService.getPrintTemplateTree(printTemplateCategories, printTemplates));
        }

        return treeNodeList;
    }

    @Override
    public String getRefObjectIdentityUuidByFileUuid(String fileUuid) {
        String hql = "select t.refObjectIdentityUuid from DmsFolderConfigurationEntity t where t.folderUuid = (select folderUuid from DmsFileEntity f where f.uuid = :fileUuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("fileUuid", fileUuid);
        List<String> refObjectIdentityUuids = this.dao.listCharSequenceByHQL(hql, params);
        return CollectionUtils.isNotEmpty(refObjectIdentityUuids) ? refObjectIdentityUuids.get(0) : null;
    }

    /**
     * 获取打印模板对象集合
     *
     * @param printTemplateUuidList 打印模板ID集合和分类id集合
     * @return java.util.List<com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate>
     **/
    private List<PrintTemplate> getPrintTemplateList(Set<String> printTemplateUuidList) {
        if (CollectionUtils.isEmpty(printTemplateUuidList)) {
            return Lists.newArrayList();
        }
        Set<String> categoryUuids = Sets.newHashSet();
        for (String uuid : printTemplateUuidList) {
            categoryUuids.add(uuid);
        }
        // 可能存在子分类
        List<PrintTemplateCategory> printTemplateCategories = printTemplateCategoryService
                .getChildNodeListByUuids(categoryUuids);
        for (PrintTemplateCategory printTemplateCategory : printTemplateCategories) {
            categoryUuids.add(printTemplateCategory.getUuid());
        }
        List<PrintTemplate> printTemplates = printTemplateService.getTemplateListByTypes(categoryUuids);
        for (PrintTemplate printTemplate : printTemplates) {
            printTemplateUuidList.add(printTemplate.getUuid());
        }
        return printTemplateService.getListByUuids(printTemplateUuidList);
    }

    /**
     * 获取打印模板分类对象集合
     *
     * @param printTemplates 打印模板集合
     * @return java.util.List<com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory>
     **/
    private List<PrintTemplateCategory> getPrintTemplateCategorieList(List<PrintTemplate> printTemplates) {
        if (CollectionUtils.isEmpty(printTemplates)) {
            return Lists.newArrayList();
        }
        Set<String> categoryUuids = Sets.newHashSet();
        for (PrintTemplate printTemplate : printTemplates) {
            if (StringUtils.isNotBlank(printTemplate.getCategory())) {
                categoryUuids.add(printTemplate.getCategory());
            }
        }
        return printTemplateCategoryService.getListByUuids(categoryUuids);
    }

    /**
     * 获取打印模板uuid集合通过文件夹ID
     *
     * @param folderUuid
     * @return java.util.Set<java.lang.String> 分类uuid也可能是printTemplateUuid
     **/
    private Set<String> getPrintTemplateUuidListByFolderUuid(String folderUuid) {
        Set<String> printTemplateUuidSets = Sets.newHashSet();
        // 1、读取该文件夹的“打印模板”配置
        DmsFolderConfigurationEntity configurationEntity = getByFolderUuid(folderUuid);
        if (configurationEntity == null) {
            throw new BusinessException("请检查参数：folderUuid是无效的：" + folderUuid);
        }
        Map<String, Object> conf = new Gson().fromJson(configurationEntity.getConfiguration(), HashMap.class);
        // conf.get("printTemplateUuids") 可能是分类uuid也可能是printTemplateUuid
        if (conf.get("printTemplateUuids") != null) {
            String printTemplateUuids = String.valueOf(conf.get("printTemplateUuids"));
            String[] printTemplateUuidStrs = printTemplateUuids.split(";");
            if (printTemplateUuidStrs.length > 0 && StringUtils.isNotBlank(printTemplateUuids)) {
                for (String printTemplateUuidStr : printTemplateUuidStrs) {
                    printTemplateUuidSets.add(printTemplateUuidStr);
                }
            } else {
                // 2、如果该文件夹没有配置“打印模板”，读取其父级夹，父级还没有，找父级的父级，有查到即终止
                DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
                if (dmsFolderEntity != null && StringUtils.isNotBlank(dmsFolderEntity.getParentUuid())) {
                    printTemplateUuidSets = getPrintTemplateUuidListByFolderUuid(dmsFolderEntity.getParentUuid());
                }
            }
        } else {
            // 2、如果该文件夹没有配置“打印模板”，读取其父级夹，父级还没有，找父级的父级，有查到即终止
            DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
            if (dmsFolderEntity != null && StringUtils.isNotBlank(dmsFolderEntity.getParentUuid())) {
                printTemplateUuidSets = getPrintTemplateUuidListByFolderUuid(dmsFolderEntity.getParentUuid());
            }
        }
        return printTemplateUuidSets;
    }

    private List<PrintTemplate> getAllBySystemUnitIds(List<String> systemUnitIds) {
        Map<String, Object> values = new HashMap<String, Object>();
        String hql = "from PrintTemplate t where 1=1";
        if (CollectionUtils.isNotEmpty(systemUnitIds)) {
            values.put("systemUnitIds", systemUnitIds);
            hql += " and  t.systemUnitId in(:systemUnitIds) ";
        }
        hql += " order by code asc, name asc ";
        List<PrintTemplate> printTemplates = printTemplateService.listByHQL(hql, values);
        return BeanUtils.convertCollection(printTemplates, PrintTemplate.class);
    }

    /**
     * @param configurations
     * @return
     */
    private List<String> extractDataType(List<DmsFolderConfigurationEntity> configurations) {
        for (DmsFolderConfigurationEntity config : configurations) {
            DmsFolderConfiguration configuration = JsonUtils.json2Object(config.getConfiguration(),
                    DmsFolderConfiguration.class);
            String dataType = configuration.getDataType();
            if (StringUtils.isNotBlank(dataType)) {
                if (DataType.MIXTURE.getId().equals(dataType)) {
                    return Lists.newArrayList(DataType.FILE.getId(), DataType.DYFORM.getId());
                }
                return Lists.newArrayList(dataType);
            }
            List<String> dataTypes = configuration.getDataTypes();
            if (CollectionUtils.isNotEmpty(dataTypes)) {
                return dataTypes;
            }
        }
        return Collections.emptyList();
    }

}
