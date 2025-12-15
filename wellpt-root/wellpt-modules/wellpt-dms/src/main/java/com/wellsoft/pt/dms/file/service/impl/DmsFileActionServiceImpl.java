/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.dms.bean.DmsFolderAssignRoleBean;
import com.wellsoft.pt.dms.bean.DmsFolderConfigurationBean;
import com.wellsoft.pt.dms.core.support.DataType;
import com.wellsoft.pt.dms.entity.*;
import com.wellsoft.pt.dms.enums.FileTypeEnum;
import com.wellsoft.pt.dms.event.DmsFileSavedEvent;
import com.wellsoft.pt.dms.facade.api.DmsDataVersionFacade;
import com.wellsoft.pt.dms.file.action.FileAction;
import com.wellsoft.pt.dms.file.action.FileActionManager;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.service.DmsFileActionService;
import com.wellsoft.pt.dms.model.*;
import com.wellsoft.pt.dms.service.*;
import com.wellsoft.pt.dms.support.*;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
 * Jan 3, 2018.1	zhulh		Jan 3, 2018		Create
 * </pre>
 * @date Jan 3, 2018
 */
@Service
@Transactional
public class DmsFileActionServiceImpl extends BaseServiceImpl implements DmsFileActionService {

    @Autowired
    private DmsFolderService dmsFolderService;

    @Autowired
    private DmsFolderConfigurationService dmsFolderConfigurationService;

    @Autowired
    private DmsObjectAssignRoleService dmsObjectAssignRoleService;

    @Autowired
    private DmsObjectAssignRoleItemService dmsObjectAssignRoleItemService;

    @Autowired
    private DmsFileService dmsFileService;

    @Autowired
    private DmsRoleActionService dmsRoleActionService;

    @Autowired
    private DmsObjectIdentityService dmsObjectIdentityService;

    @Autowired
    private DmsRecycleBinService dmsRecycleBinService;

    @Autowired
    private DmsShareInfoService dmsShareInfoService;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private FileActionManager fileActionManager;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private OrgFacadeService orgApiFacade;

    @Autowired
    private DmsDataVersionFacade dmsDataVersionFacade;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DmsFileDoucmentIndexServiceImpl dmsFileDoucmentIndexService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#createFolder(java.lang.String, java.lang.String)
     */
    @Override
    public String createFolder(String childFolderName, String parentFolderUuid) {
        String childFolderUuid = UUID.randomUUID().toString();
        return createFolder(childFolderUuid, childFolderName, parentFolderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#createFolder(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public String createFolder(String childFolderUuid, String childFolderName, String parentFolderUuid) {
        if (!FileHelper.isValidFileName(childFolderName)) {
            throw new RuntimeException(FileHelper.getInValidFolderNameTip(childFolderName));
        }
        String folderUuid = childFolderUuid;
        if (StringUtils.isBlank(folderUuid)) {
            folderUuid = SnowFlake.getId() + StringUtils.EMPTY;//UUID.randomUUID().toString();
        }
        DmsFolderEntity parntFolder = null;
        if (StringUtils.isNotBlank(parentFolderUuid)) {
            parntFolder = dmsFolderService.get(parentFolderUuid);
        }
        String code = StringUtils.EMPTY;
        String uuidPath = null;
        // 上级夹不为空
        if (parntFolder != null) {
            uuidPath = parntFolder.getUuidPath() + Separator.SLASH.getValue() + folderUuid;
            code = generateFolderCode(parntFolder);
        } else {
            // 上级夹为空
            // uuidPath = "/" + folderUuid;
            throw new BusinessException("上级夹不能为空！");
        }
        // 创建夹基本信息
        DmsFolderEntity folderEntity = new DmsFolderEntity();
        folderEntity.setUuid(folderUuid);
        folderEntity.setName(childFolderName);
        folderEntity.setCode(code);
        folderEntity.setUuidPath(uuidPath);
        folderEntity.setStatus(FileStatus.NORMAL);
        folderEntity.setParentUuid(parentFolderUuid);
        folderEntity.setLibraryUuid(parntFolder.getLibraryUuid());
        folderEntity.setType(FolderType.FOLDER);
        folderEntity.setSystem(RequestSystemContextPathResolver.system());
        folderEntity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        dmsFolderService.save(folderEntity);

        DmsFolderConfigurationEntity parentConfigurationEntity = null;
        String refObjectIdentityUuid = null;
        // 上级夹不为空
        if (StringUtils.isNotBlank(parentFolderUuid)) {
            parentConfigurationEntity = dmsFolderConfigurationService.getByFolderUuid(parentFolderUuid);
            refObjectIdentityUuid = parentConfigurationEntity.getRefObjectIdentityUuid();
        } else {
            // 上级夹为空
            refObjectIdentityUuid = dmsObjectIdentityService.getOrCreate(folderEntity).getUuid();
        }

        // 创建夹配置信息，继承上级夹
        DmsFolderConfigurationBean configuration = new DmsFolderConfigurationBean();
        configuration.setFolderUuid(folderUuid);
        configuration.setIsInheritFolderRole(1);

        DmsFolderConfigurationEntity folderConfigurationEntity = new DmsFolderConfigurationEntity();
        folderConfigurationEntity.setFolderUuid(folderUuid);
        folderConfigurationEntity.setIsInheritFolderRole(1);
        folderConfigurationEntity.setRefObjectIdentityUuid(refObjectIdentityUuid);
        folderConfigurationEntity.setConfiguration(JsonUtils.object2Json(configuration));
        dmsFolderConfigurationService.save(folderConfigurationEntity);

        this.dao.getSession().flush();
        this.dao.getSession().clear();
        return folderUuid;
    }

    /**
     * @param parentFolder
     * @return
     */
    private String generateFolderCode(DmsFolderEntity parentFolder) {
        long childrenCount = dmsFolderService.countByParentUuid(parentFolder.getUuid());
        // DecimalFormat decimalFormat = new DecimalFormat("000");
        String parentCode = "00";
        if (StringUtils.isNotBlank(parentFolder.getParentUuid())) {
            parentCode = parentFolder.getCode();
        }
        return parentCode + childrenCount;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#createFolderIfNotExists(java.lang.String, java.lang.String)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public String createFolderIfNotExists(String childFolderPath, String parentFolderUuid) {
        String childFolderUuid = null;
        String tmpParentFolderUuid = parentFolderUuid;
        String[] childFolderNames = StringUtils.split(childFolderPath, Separator.SLASH.getValue());
        for (String childFolderName : childFolderNames) {
            String newFolderName = StringUtils.trim(childFolderName);
            DmsFolderEntity dmsFolderEntity = dmsFolderService.getByFolderNameAndParentFolderUuid(newFolderName,
                    tmpParentFolderUuid);
            if (dmsFolderEntity != null) {
                childFolderUuid = dmsFolderEntity.getUuid();
            } else {
                childFolderUuid = UUID.randomUUID().toString();
                createFolder(childFolderUuid, newFolderName, tmpParentFolderUuid);
            }
            tmpParentFolderUuid = childFolderUuid;
        }
        return childFolderUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#readFolder(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public DmsFolder readFolder(String folderUuid) {
        // 权限检测
        if (!hasFolderPermission(folderUuid, FileActions.READ_FOLDER)) {
            return null;
        }
        return readFolderWithoutPermission(folderUuid);
    }

    /**
     * @param folderUuid
     * @return
     */
    public DmsFolder readFolderWithoutPermission(String folderUuid) {
        DmsFolderEntity folderEntity = dmsFolderService.get(folderUuid);
        if (folderEntity == null) {
            return null;
        }
        DmsFolder dmsFolder = new DmsFolder();
        dmsFolder.setUuid(folderEntity.getUuid());
        dmsFolder.setName(folderEntity.getName());
        dmsFolder.setContentType(FileMediaType.APPLICATION_FOLDER);
        return dmsFolder;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#hasFolderPermission(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasFolderPermission(String folderUuid, String... actions) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        List<String> actionArray = new ArrayList<String>();
        actionArray.addAll(Arrays.asList(actions));
        values.put("folderUuid", folderUuid);
        values.put("actions", actions);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        Long count = this.nativeDao.countByNamedQuery("dmsCheckFolderPermissionQuery", values);
        return count.intValue() > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#hasFolderRole(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasFolderRole(String userId, String folderUuid, String... roleIds) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        values.put("dmsRoleIds", roleIds);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        Long count = this.nativeDao.countByNamedQuery("dmsCheckFolderRoleQuery", values);
        return count.intValue() > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#getFolderAttributes(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public DmsFolderAttributes getFolderAttributes(String folderUuid) {
        DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
        DmsFolderAttributes attributes = new DmsFolderAttributes();
        attributes.setName(dmsFolderEntity.getName());
        attributes.setContentType(FileMediaType.APPLICATION_FOLDER);
        attributes.setContentTypeName(FileTypeEnum.FOLDER.getName());
        attributes.setLocation(getFolderPathName(dmsFolderEntity.getParentUuid()));
        attributes.setSize(null);
        attributes.setCreateTime(dmsFolderEntity.getCreateTime());
        attributes.setModifyTime(dmsFolderEntity.getModifyTime());
        // 夹配置信息
        DmsFolderConfigurationEntity folderConfigurationEntity = dmsFolderConfigurationService
                .getByFolderUuid(folderUuid);
        if (folderConfigurationEntity != null && StringUtils.isNotBlank(folderConfigurationEntity.getConfiguration())) {
            attributes.setFolderConfiguration(JsonUtils.json2Object(folderConfigurationEntity.getConfiguration(),
                    DmsFolderConfiguration.class));
        }
        return attributes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#hasFilePermission(java.lang.String, java.lang.String[])
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasFilePermission(String fileUuid, String... actions) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
        // 创建者完全控制
        if (StringUtils.equals(dmsFileEntity.getCreator(), currUserId)) {
            return true;
        }
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        List<String> actionArray = new ArrayList<String>();
        actionArray.addAll(Arrays.asList(actions));
        values.put("fileUuid", fileUuid);
        values.put("actions", actions);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        Long count = this.nativeDao.countByNamedQuery("dmsCheckFilePermissionQuery", values);
        return count.intValue() > 0;
    }

    /**
     * 文件权限过滤
     *
     * @param fileUuids
     * @param actions
     * @return
     */
    @Override
    public List<String> filterFileWithPermission(Collection<String> fileUuids, String... actions) {
        if (CollectionUtils.isEmpty(fileUuids)) {
            return Collections.emptyList();
        }

        List<String> retFileUuids = Lists.newArrayList();
        List<String> initFileUuids = Lists.newArrayList(fileUuids);
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        List<DmsFileEntity> fileEntities = dmsFileService.listByUuids(initFileUuids);
        // 创建者完全控制
        List<String> adminFileUuids = fileEntities.stream().filter(dmsFileEntity -> StringUtils.equals(dmsFileEntity.getCreator(), currUserId))
                .map(dmsFileEntity -> dmsFileEntity.getUuid()).collect(Collectors.toList());

        retFileUuids.addAll(adminFileUuids);
        initFileUuids.removeAll(adminFileUuids);
        if (CollectionUtils.isNotEmpty(initFileUuids)) {
            FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
            String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
            Map<String, Object> values = new HashMap<String, Object>();
            List<String> actionArray = new ArrayList<String>();
            actionArray.addAll(Arrays.asList(actions));
            values.put("fileUuids", initFileUuids);
            values.put("actions", actions);
            values.put("unit_in_expression_org_id", currUserId);
            values.put("userOrgIdsTemplate", userOrgIdsTemplate);
            values.put("orgIds", fileQueryTemplate.getOrgIds());
            values.put("roleIds", fileQueryTemplate.getRoleIds());
            values.put("sids", fileQueryTemplate.getSids());
            List<String> fileUuidList = this.nativeDao.namedQuery("dmsFilterFileWithPermissionQuery", values, String.class);
            retFileUuids.addAll(fileUuidList);
        }

        return retFileUuids;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listRootFolder()
     */
    @Override
    public List<DmsFolder> listRootFolder() {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        values.put("systemId", RequestSystemContextPathResolver.system());
        List<DmsFolder> folders = this.nativeDao.namedQuery("dmsListRootFolderQuery", values, DmsFolder.class);
        return folders;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listFolder(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DmsFolder> listFolder(String folderUuid) {
        // 权限检测
        if (!hasFolderPermission(folderUuid, FileActions.LIST_FOLDER, FileActions.LIST_ALL_FOLDER,
                FileActions.LIST_FOLDER_AND_FILES, FileActions.LIST_ALL_FOLDER_AND_FILES)) {
            return Collections.emptyList();
        }
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        List<DmsFolder> folders = this.nativeDao.namedQuery("dmsListFolderQuery", values, DmsFolder.class);
        return folders;
    }

    /**
     * @param folderUuid
     * @return
     */
    @Transactional(readOnly = true)
    public List<DmsFolder> listFolderWithoutPermission(String folderUuid) {
        // String currUserId = SpringSecurityUtils.getCurrentUserId();
        // String userOrgIdsTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        // values.put("unit_in_expression_org_id", currUserId);
        // values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        List<DmsFolder> folders = this.nativeDao.namedQuery("dmsListFolderQueryWithoutPermission", values, DmsFolder.class);
        return folders;
    }

    /**
     * @param folderUuid
     * @return
     */
    @Transactional(readOnly = true)
    public List<DmsFolder> listDeletedFolderWithoutPermission(String folderUuid) {
        // String currUserId = SpringSecurityUtils.getCurrentUserId();
        // String userOrgIdsTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        // values.put("unit_in_expression_org_id", currUserId);
        // values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        List<DmsFolder> folders = this.nativeDao.namedQuery("dmsListDeletedFolderQueryWithoutPermission", values, DmsFolder.class);
        return folders;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listFolder(java.lang.String, boolean)
     */
    @Override
    public List<DmsFolder> listFolder(String folderUuid, boolean listNearestIfNotFound) {
        List<DmsFolder> folders = listFolder(folderUuid);
        if (folders.isEmpty() && listNearestIfNotFound) {
            // TODO
        }
        return folders;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listAllFolder(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DmsFolder> listAllFolder(String folderUuid) {
        // 权限检测
        if (!hasFolderPermission(folderUuid, FileActions.LIST_ALL_FOLDER)) {
            return Collections.emptyList();
        }
        DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        values.put("uuidPath", dmsFolderEntity.getUuidPath());
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        PagingInfo pagingInfo = new PagingInfo(1, Integer.MAX_VALUE);
        List<DmsFolder> folders = this.nativeDao.namedQuery("dmsListAllFolderQuery", values, DmsFolder.class,
                pagingInfo);
        return folders;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listFolderCount(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public long listFolderCount(String folderUuid) {
        DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        values.put("uuidPath", dmsFolderEntity.getUuidPath());
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        Long count = this.nativeDao.countByNamedQuery("dmsListAllFolderQuery", values);
        return count.longValue();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listFiles(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DmsFile> listFiles(String folderUuid) {
        // 权限检测
        if (!hasFolderPermission(folderUuid, FileActions.LIST_FILES)) {
            return Collections.emptyList();
        }
        return listFilesWithoutPermission(folderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listFiles(java.lang.String)
     */
    @Transactional(readOnly = true)
    public List<DmsFile> listFilesWithoutPermission(String folderUuid) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        List<DmsFile> files = this.nativeDao.namedQuery("dmsListFilesQuery", values, DmsFile.class);
        return files;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listAllFiles(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DmsFile> listAllFiles(String folderUuid) {
        // 权限检测
        if (!hasFolderPermission(folderUuid, FileActions.LIST_ALL_FILES)) {
            return Collections.emptyList();
        }
        DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        values.put("uuidPath", dmsFolderEntity.getUuidPath());
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        PagingInfo pagingInfo = new PagingInfo(1, Integer.MAX_VALUE);
        List<DmsFile> files = this.nativeDao.namedQuery("dmsListAllFilesQuery", values, DmsFile.class, pagingInfo);
        return files;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listFolderAndFiles(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DmsFile> listFolderAndFiles(String folderUuid) {
        // 权限检测
        if (!hasFolderPermission(folderUuid, FileActions.LIST_FOLDER_AND_FILES, FileActions.LIST_ALL_FOLDER_AND_FILES)) {
            return Collections.emptyList();
        }
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        List<DmsFile> files = this.nativeDao.namedQuery("dmsListFolderAndFilesQuery", values, DmsFile.class);
        return files;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listAllFolderAndFiles(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DmsFile> listAllFolderAndFiles(String folderUuid) {
        // 权限检测
        if (!hasFolderPermission(folderUuid, FileActions.LIST_ALL_FOLDER_AND_FILES)) {
            return Collections.emptyList();
        }
        DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        values.put("uuidPath", dmsFolderEntity.getUuidPath());
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        PagingInfo pagingInfo = new PagingInfo(1, Integer.MAX_VALUE);
        List<DmsFile> files = this.nativeDao.namedQuery("dmsListAllFolderAndFilesQuery", values, DmsFile.class,
                pagingInfo);
        return files;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#shareFolder(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String shareFolder(String folderUuid, String shareOrgId, String shareOrgName) {
        DmsShareInfoEntity dmsShareInfoEntity = new DmsShareInfoEntity();
        dmsShareInfoEntity.setDataType(DmsFolderEntity.class.getCanonicalName());
        dmsShareInfoEntity.setDataUuid(folderUuid);
        dmsShareInfoEntity.setOwnerId(SpringSecurityUtils.getCurrentUserId());
        dmsShareInfoEntity.setShareOrgId(shareOrgId);
        dmsShareInfoEntity.setShareOrgName(shareOrgName);
        dmsShareInfoEntity.setShareTime(Calendar.getInstance().getTime());
        dmsShareInfoService.save(dmsShareInfoEntity);
        return dmsShareInfoEntity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#cancelShareFolder(java.lang.String, java.lang.String)
     */
    @Override
    public void cancelShareFolder(String folderUuid, String shareUuid) {
        String hql = "delete from DmsShareInfoEntity t where t.dataType = :dataType and t.dataUuid = :dataUuid and t.uuid = :shareUuid and t.ownerId = :ownerId";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", DmsFolderEntity.class.getCanonicalName());
        values.put("dataUuid", folderUuid);
        values.put("shareUuid", shareUuid);
        values.put("ownerId", SpringSecurityUtils.getCurrentUserId());
        this.dao.batchExecute(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#renameFolder(java.lang.String, java.lang.String)
     */
    @Override
    public void renameFolder(String folderUuid, String newFolderName) {
        // 权限检测
        if (!hasFolderPermission(folderUuid, FileActions.RENAME_FOLDER)) {
            throw new RuntimeException("没有权限重命名夹！");
        }
        renameFolderWithoutPermission(folderUuid, newFolderName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#renameFolder(java.lang.String, java.lang.String)
     */
    @Override
    public void renameFolderWithoutPermission(String folderUuid, String newFolderName) {
        if (!FileHelper.isValidFileName(newFolderName)) {
            throw new RuntimeException(FileHelper.getInValidFolderNameTip(newFolderName));
        }
        DmsFolderEntity folderEntity = dmsFolderService.get(folderUuid);
        folderEntity.setName(newFolderName);
        dmsFolderService.save(folderEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#moveFolder(java.lang.String, java.lang.String)
     */
    @Override
    public void moveFolder(String sourceFolderUuid, String destFolderUuid) {
        // 权限检测
        if (!hasFolderPermission(sourceFolderUuid, FileActions.MOVE_FOLDER)) {
            throw new RuntimeException("没有权限移动夹！");
        }
        if (!hasFolderPermission(destFolderUuid, FileActions.CREATE_FOLDER)) {
            throw new RuntimeException("没有权限创建移动的夹！");
        }

        // 目标夹检测，不能移动到源夹的子夹
        DmsFolderEntity sourceFolderEntity = dmsFolderService.get(sourceFolderUuid);
        DmsFolderEntity targetFolderEntity = dmsFolderService.get(destFolderUuid);
        // 夹内移动直接返回
        if (StringUtils.equals(sourceFolderEntity.getParentUuid(), targetFolderEntity.getUuid())) {
            return;
        }
        String sourceUuidPath = sourceFolderEntity.getUuidPath();
        String targetUuidPath = targetFolderEntity.getUuidPath();
        if (StringUtils.startsWith(targetUuidPath, sourceUuidPath)) {
            throw new RuntimeException("不能移到源夹的子夹");
        }

        // 更新源夹的PARENT_UUID、UUID_PATH
        String newSourceUuidPath = targetFolderEntity.getUuidPath() + Separator.SLASH.getValue() + sourceFolderUuid;
        sourceFolderEntity.setParentUuid(destFolderUuid);
        sourceFolderEntity.setUuidPath(newSourceUuidPath);
        dmsFolderService.save(sourceFolderEntity);

        // 更新源夹的子夹UUID_PATH
        updateUuidPathOfChildrenFolder(sourceFolderEntity.getUuid(), sourceFolderEntity.getUuidPath());

        // 更新权限引用
        dmsFolderConfigurationService.updateInheritFolderRole(sourceFolderUuid, destFolderUuid);
    }

    /**
     * @param folderUuid
     * @param folderUuidPath
     */
    private void updateUuidPathOfChildrenFolder(String folderUuid, String folderUuidPath) {
        DmsFolderEntity example = new DmsFolderEntity();
        example.setParentUuid(folderUuid);
        List<DmsFolderEntity> entities = dmsFolderService.findByExample(example);
        for (DmsFolderEntity entity : entities) {
            String uuidPath = folderUuidPath + Separator.SLASH.getValue() + entity.getUuid();
            entity.setUuidPath(uuidPath);
            dmsFolderService.save(entity);

            // 递归更新子结点
            updateUuidPathOfChildrenFolder(entity.getUuid(), entity.getUuidPath());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#copyFolder(java.lang.String, java.lang.String)
     */
    @Override
    public String copyFolder(String sourceFolderUuid, String destFolderUuid) {
        // 权限检测
        if (!hasFolderPermission(sourceFolderUuid, FileActions.COPY_FOLDER)) {
            throw new RuntimeException("没有权限复制夹！");
        }
        if (!hasFolderPermission(destFolderUuid, FileActions.CREATE_FOLDER)) {
            throw new RuntimeException("没有权限创建复制的夹！");
        }

        return copyFolderAndFiles(sourceFolderUuid, destFolderUuid);
    }

    /**
     * @param sourceFolderUuid
     * @param destFolderUuid
     * @return
     */
    private String copyFolderAndFiles(String sourceFolderUuid, String destFolderUuid) {
        // 目标夹检测，不能复制到源夹的子夹
        DmsFolderEntity sourceFolderEntity = dmsFolderService.get(sourceFolderUuid);
        DmsFolderEntity targetFolderEntity = dmsFolderService.get(destFolderUuid);
        String sourceUuidPath = sourceFolderEntity.getUuidPath();
        String targetUuidPath = targetFolderEntity.getUuidPath();
        if (StringUtils.startsWith(targetUuidPath, sourceUuidPath)) {
            throw new RuntimeException("不能复制到源夹的子夹");
        }

        String newFolderUuid = SnowFlake.getId() + StringUtils.EMPTY;
        String newUuidPath = targetFolderEntity.getUuidPath() + Separator.SLASH.getValue() + newFolderUuid;
        // 夹复制处理
        DmsFolderEntity newFolderEntity = new DmsFolderEntity();
        BeanUtils.copyProperties(sourceFolderEntity, newFolderEntity, IdEntity.BASE_FIELDS);
        newFolderEntity.setUuid(newFolderUuid);
        newFolderEntity.setUuidPath(newUuidPath);
        newFolderEntity.setParentUuid(targetFolderEntity.getUuid());
        newFolderEntity.setLibraryUuid(targetFolderEntity.getLibraryUuid());
        dmsFolderService.save(newFolderEntity);

        // 创建夹配置信息
        // 1、继承上级夹
        DmsFolderConfigurationEntity sourceFolderConfigurationEntity = dmsFolderConfigurationService
                .getByFolderUuid(sourceFolderUuid);
        if (Integer.valueOf("1").equals(sourceFolderConfigurationEntity.getIsInheritFolderRole())) {
            // 获取夹授权对象
            DmsFolderConfigurationEntity parentConfigurationEntity = dmsFolderConfigurationService
                    .getByFolderUuid(destFolderUuid);
            String objectIdentityUuid = parentConfigurationEntity.getRefObjectIdentityUuid();
            // 夹JOSN配置信息
            DmsFolderConfigurationBean configuration = new DmsFolderConfigurationBean();
            configuration.setFolderUuid(newFolderUuid);
            configuration.setIsInheritFolderRole(1);
            // 夹配置
            DmsFolderConfigurationEntity newFolderConfigurationEntity = new DmsFolderConfigurationEntity();
            newFolderConfigurationEntity.setFolderUuid(newFolderUuid);
            newFolderConfigurationEntity.setReadFileField(sourceFolderConfigurationEntity.getReadFileField());
            newFolderConfigurationEntity.setEditFileField(sourceFolderConfigurationEntity.getEditFileField());
            newFolderConfigurationEntity.setIsInheritFolderRole(1);
            newFolderConfigurationEntity.setRefObjectIdentityUuid(objectIdentityUuid);
            newFolderConfigurationEntity.setConfiguration(JsonUtils.object2Json(configuration));
            dmsFolderConfigurationService.save(newFolderConfigurationEntity);
        } else {
            // 2、自定义
            // 获取夹授权对象
            DmsObjectIdentityEntity dmsObjectIdentity = dmsObjectIdentityService.getOrCreate(newFolderEntity);
            String objectIdentityUuid = dmsObjectIdentity.getUuid();
            // 夹JOSN配置信息
            DmsFolderConfigurationBean configuration = JsonUtils.json2Object(
                    sourceFolderConfigurationEntity.getConfiguration(), DmsFolderConfigurationBean.class);
            configuration.setFolderUuid(newFolderUuid);
            // 夹配置
            DmsFolderConfigurationEntity newFolderConfigurationEntity = new DmsFolderConfigurationEntity();
            BeanUtils.copyProperties(sourceFolderConfigurationEntity, newFolderConfigurationEntity,
                    IdEntity.BASE_FIELDS);
            newFolderConfigurationEntity.setUuid(null);
            newFolderConfigurationEntity.setFolderUuid(newFolderUuid);
            newFolderConfigurationEntity.setReadFileField(configuration.getReadFileField());
            newFolderConfigurationEntity.setEditFileField(configuration.getEditFileField());
            newFolderConfigurationEntity.setIsInheritFolderRole(configuration.getIsInheritFolderRole());
            newFolderConfigurationEntity.setRefObjectIdentityUuid(objectIdentityUuid);
            newFolderConfigurationEntity.setConfiguration(JsonUtils.object2Json(configuration));
            dmsFolderConfigurationService.save(newFolderConfigurationEntity);
            // 夹权限配置
            List<DmsFolderAssignRoleBean> dmsObjectAssignRoleBeans = configuration.getAssignRoles();
            if (dmsObjectAssignRoleBeans == null) {
                dmsObjectAssignRoleBeans = Collections.emptyList();
            }
            List<DmsObjectAssignRoleEntity> assignRoleEntities = BeanUtils.copyCollection(dmsObjectAssignRoleBeans,
                    DmsObjectAssignRoleEntity.class);
            for (DmsObjectAssignRoleEntity assignRoleEntity : assignRoleEntities) {
                assignRoleEntity.setUuid(null);
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
        // 夹、夹配置保存进数据库
        this.dao.getSession().flush();

        // 复制源夹下的文件
        List<DmsFile> sourceFolderFiles = listFiles(sourceFolderUuid);
        for (DmsFile folderFile : sourceFolderFiles) {
            copyFile(folderFile.getUuid(), newFolderUuid);
        }

        // 复制源夹下的子夹
        List<DmsFolder> sourceFolderSubFolders = listFolder(sourceFolderUuid);
        for (DmsFolder subFolder : sourceFolderSubFolders) {
            copyFolderAndFiles(subFolder.getUuid(), newFolderUuid);
        }
        return newFolderUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#deleteFolder(java.lang.String)
     */
    @Override
    public void deleteFolder(String folderUuid) {
        // 子夹检测
        DmsFolderEntity folderExample = new DmsFolderEntity();
        folderExample.setParentUuid(folderUuid);
        folderExample.setStatus(FileStatus.NORMAL);
        Long folderCount = this.dao.countByExample(folderExample);
        if (folderCount.intValue() > 0) {
            throw new RuntimeException("存在子夹不能删除");
        }

        // 夹文件检测
        DmsFileEntity fileExample = new DmsFileEntity();
        fileExample.setFolderUuid(folderUuid);
        fileExample.setStatus(FileStatus.NORMAL);
        Long fileCount = this.dao.countByExample(fileExample);
        if (fileCount.intValue() > 0) {
            throw new RuntimeException("存在文件不能删除");
        }

        String hql = "update DmsFolderEntity t set t.status = :folderStatus where t.uuid = :folderUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        values.put("folderStatus", FileStatus.DELETE);
        this.dao.batchExecute(hql, values);

        // 生成回收站记录
        dmsRecycleBinService.addData(DmsFolderEntity.class.getCanonicalName(), folderUuid,
                SpringSecurityUtils.getCurrentUserId());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#deleteFolder(java.util.List)
     */
    @Override
    public void deleteFolder(List<String> folderUuids) {
        for (String folderUuid : folderUuids) {
            this.deleteFolder(folderUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#forceDeleteFolder(java.lang.String)
     */
    @Override
    public void forceDeleteFolder(String folderUuid) {
        DmsFolderEntity folderEntity = dmsFolderService.get(folderUuid);
        String folderUuidPath = folderEntity.getUuidPath();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuidPath", folderUuidPath);
        values.put("fileStatus", FileStatus.DELETE);
        values.put("folderStatus", FileStatus.DELETE);
        // 删除夹文件
        String deleteFileHql = "update DmsFileEntity t1 set t1.status = :fileStatus where exists "
                + "(select t2.uuid from DmsFolderEntity t2 where t2.uuidPath like :folderUuidPath || '%' "
                + "and t1.folderUuid = t2.uuid)";
        this.dao.batchExecute(deleteFileHql, values);
        // 删除夹(包括子夹)
        String deleteChildFolderHql = "update DmsFolderEntity t set t.status = :folderStatus where t.uuidPath like :folderUuidPath || '%'";
        this.dao.batchExecute(deleteChildFolderHql, values);

        // 生成回收站记录
        dmsRecycleBinService.addData(DmsFolderEntity.class.getCanonicalName(), folderUuid,
                SpringSecurityUtils.getCurrentUserId());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#restoreFolder(java.lang.String)
     */
    @Override
    public void restoreFolder(String folderUuid) {
        DmsFolderEntity folderEntity = dmsFolderService.get(folderUuid);
        String folderUuidPath = folderEntity.getUuidPath();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuidPath", folderUuidPath);
        values.put("fileStatus", FileStatus.NORMAL);
        values.put("folderStatus", FileStatus.NORMAL);
        // 删除夹文件
        String deleteFileHql = "update DmsFileEntity t1 set t1.status = :fileStatus where exists "
                + "(select t2.uuid from DmsFolderEntity t2 where t2.uuidPath like :folderUuidPath || '%' "
                + "and t1.folderUuid = t2.uuid)";
        this.dao.batchExecute(deleteFileHql, values);
        // 删除夹(包括子夹)
        String deleteChildFolderHql = "update DmsFolderEntity t set t.status = :folderStatus where t.uuidPath like :folderUuidPath || '%'";
        this.dao.batchExecute(deleteChildFolderHql, values);

        // 删除回收站
        dmsRecycleBinService.removeByDataUuid(folderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#physicalDeleteFolder(java.lang.String)
     */
    @Override
    public void physicalDeleteFolder(String folderUuid) {
        DmsFolderEntity folderEntity = dmsFolderService.get(folderUuid);
        String folderUuidPath = folderEntity.getUuidPath();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuidPath", folderUuidPath);
        values.put("fileStatus", FileStatus.DELETE);
        values.put("folderStatus", FileStatus.DELETE);
        // 删除夹文件
        String deleteFileHql = "delete from DmsFileEntity t1 where t1.status = :fileStatus and exists "
                + "(select t2.uuid from DmsFolderEntity t2 where t2.uuidPath like :folderUuidPath || '%' "
                + "and t1.folderUuid = t2.uuid)";
        this.dao.batchExecute(deleteFileHql, values);
        // 删除夹(包括子夹)
        String deleteChildFolderHql = "delete from DmsFolderEntity t where t.status = :folderStatus and t.uuidPath like :folderUuidPath || '%'";
        this.dao.batchExecute(deleteChildFolderHql, values);

        // 删除回收站
        dmsRecycleBinService.removeByDataUuid(folderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#physicalDeleteFolder(java.util.List)
     */
    @Override
    public void physicalDeleteFolder(List<String> folderUuids) {
        for (String folderUuid : folderUuids) {
            this.physicalDeleteFolder(folderUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#createFile(java.lang.String, com.wellsoft.pt.dms.model.DmsFile)
     */
    @Override
    public String createFile(String parentFolderUuid, DmsFile data) {
        if (!FileHelper.isValidFileName(data.getName())) {
            throw new RuntimeException(FileHelper.getInValidFileNameTip(data.getName()));
        }
        DmsFileEntity dmsFileEntity = new DmsFileEntity();
        dmsFileEntity.setFileName(data.getName());
        dmsFileEntity.setDataDefUuid(data.getDataDefUuid());
        dmsFileEntity.setDataUuid(data.getDataUuid());
        dmsFileEntity.setFolderUuid(parentFolderUuid);
        dmsFileEntity.setLibraryUuid(dmsFolderService.getOne(parentFolderUuid).getLibraryUuid());
        dmsFileService.save(dmsFileEntity);
        return dmsFileEntity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#uploadFile(java.lang.String, com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity)
     */
    @Override
    public String uploadFile(String folderUuid, MongoFileEntity file) {
        if (!hasFolderPermission(folderUuid, FileActions.CREATE_FILE)) {
            throw new RuntimeException("没有权限上传文件！");
        }

        String uuid = SnowFlake.getId() + StringUtils.EMPTY;
        DmsFileEntity dmsFileEntity = new DmsFileEntity();
        dmsFileEntity.setUuid(uuid);
        dmsFileEntity.setFileName(null == file.getLogicFileInfo() ? file.getFileName() : file.getLogicFileInfo()
                .getFileName());
        dmsFileEntity.setContentType(file.getContentType());
        dmsFileEntity.setFileSize(file.getLogicFileInfo().getFileSize());
        dmsFileEntity.setDataDefUuid(uuid);
        dmsFileEntity.setDataUuid(file.getFileID());
        dmsFileEntity.setFolderUuid(folderUuid);
        dmsFileEntity.setLibraryUuid(dmsFolderService.getOne(folderUuid).getLibraryUuid());
        dmsFileService.save(dmsFileEntity);

        // 放入MONGO文件夹
        mongoFileService.pushFileToFolder(uuid, file.getFileID(), null);

        // 建立全文索引
        // dmsFileDoucmentIndexService.index(dmsFileEntity, file.getFileID());
        ApplicationContextHolder.publishEvent(new DmsFileSavedEvent(dmsFileEntity, file.getFileID()));
        return dmsFileEntity.getUuid();
    }

    @Override
    public String uploadFileAsNewVersion(String folderUuid, String fileUuid, String remark, MongoFileEntity file) {
        if (!hasFolderPermission(folderUuid, FileActions.CREATE_FILE)) {
            throw new RuntimeException("没有权限上传文件！");
        }
        return uploadFileAsNewVersionWithoutPermission(folderUuid, fileUuid, remark, file);
    }

    /**
     * @param folderUuid
     * @param fileUuid
     * @param remark
     * @param file
     * @return
     */
    @Override
    public String uploadFileAsNewVersionWithoutPermission(String folderUuid, String fileUuid, String remark, MongoFileEntity file) {
        DmsFileEntity dmsFileEntity = StringUtils.isBlank(fileUuid) ? null : dmsFileService.get(fileUuid);
        if (null == dmsFileEntity) {
            String uuid = UUID.randomUUID().toString();
            dmsFileEntity = new DmsFileEntity();
            dmsFileEntity.setUuid(uuid);
            dmsFileEntity.setFileName(null == file.getLogicFileInfo() ? file.getFileName() : file.getLogicFileInfo()
                    .getFileName());
            dmsFileEntity.setContentType(file.getContentType());
            dmsFileEntity.setFileSize(file.getLogicFileInfo().getFileSize());
            // dmsFileEntity.setDataDefUuid(uuid);
            dmsFileEntity.setDataUuid(file.getFileID());
            dmsFileEntity.setFolderUuid(folderUuid);
            dmsFileEntity.setLibraryUuid(dmsFolderService.getOne(folderUuid).getLibraryUuid());
            dmsFileService.save(dmsFileEntity);
            // 放入MONGO文件夹
            mongoFileService.pushFileToFolder(uuid, file.getFileID(), null);
            dmsDataVersionFacade.saveVersion(FileTypeEnum.FILE.getType(), file.getFileName(), file.getFileID(), uuid,
                    1, 0.1, null == remark ? "初始版本" : remark);
            return dmsFileEntity.getUuid();
        } else {
            DmsFileEntity newDmsFileEntity = new DmsFileEntity();
            BeanUtils.copyProperties(dmsFileEntity, newDmsFileEntity);
            dmsFileEntity.setStatus(FileStatus.DELETE);
            dmsFileService.save(dmsFileEntity);// 标记为删除
            String uuid = UUID.randomUUID().toString();
            newDmsFileEntity.setRecVer(null);
            newDmsFileEntity.setUuid(uuid);
            newDmsFileEntity.setFileName(null == file.getLogicFileInfo() ? file.getFileName() : file.getLogicFileInfo()
                    .getFileName());
            newDmsFileEntity.setContentType(file.getContentType());
            newDmsFileEntity.setFileSize(file.getLogicFileInfo().getFileSize());
            // newDmsFileEntity.setDataDefUuid(uuid);
            newDmsFileEntity.setDataUuid(file.getFileID());
            newDmsFileEntity.setFolderUuid(folderUuid);
            dmsFileService.save(newDmsFileEntity);
            // 放入MONGO文件夹
            mongoFileService.pushFileToFolder(uuid, file.getFileID(), null);
            dmsDataVersionFacade.saveNewVersion(FileTypeEnum.FILE.getType(), file.getFileName(),
                    dmsFileEntity.getDataUuid(), dmsFileEntity.getUuid(), file.getFileID(), uuid, 1, 0.1,
                    null == remark ? "初始版本" : remark);
            return newDmsFileEntity.getUuid();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#readFile(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public DmsFile readFile(String fileUuid) {
        // 权限检测
        if (!hasFilePermission(fileUuid, FileActions.READ_FILE)) {
            return null;
        }
        return readFileWithoutPermission(fileUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#readFileWithWithoutPermission(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public DmsFile readFileWithoutPermission(String fileUuid) {
        DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
        DmsFile dmsFile = new DmsFile();
        BeanUtils.copyProperties(dmsFileEntity, dmsFile);
        dmsFile.setName(dmsFileEntity.getFileName());
        return dmsFile;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#getFileAttributes(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public DmsFileAttributes getFileAttributes(String fileUuid) {
        DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
        DmsFileAttributes attributes = new DmsFileAttributes();
        attributes.setName(dmsFileEntity.getFileName());
        attributes.setContentType(dmsFileEntity.getContentType());
        attributes.setContentTypeName(FileHelper.getFileTypeAsString(dmsFileEntity.getFileName(),
                dmsFileEntity.getContentType()));
        attributes.setLocation(getFolderPathName(dmsFileEntity.getFolderUuid()));
        attributes.setSize(dmsFileEntity.getFileSize());
        attributes.setSizeString(FileHelper.getFileSizeAsString(dmsFileEntity.getFileSize(),
                dmsFileEntity.getContentType()));
        attributes.setCreateTime(dmsFileEntity.getCreateTime());
        attributes.setModifyTime(dmsFileEntity.getModifyTime());
        // 夹配置信息
        DmsFolderConfigurationEntity folderConfigurationEntity = dmsFolderConfigurationService
                .getByFolderUuid(dmsFileEntity.getFolderUuid());
        if (folderConfigurationEntity != null && StringUtils.isNotBlank(folderConfigurationEntity.getConfiguration())) {
            attributes.setFolderConfiguration(JsonUtils.json2Object(folderConfigurationEntity.getConfiguration(),
                    DmsFolderConfiguration.class));
        }
        return attributes;
    }

    /**
     * @param folderUuid
     * @return
     */
    private String getFolderPathName(String folderUuid) {
        DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
        String uuidPath = dmsFolderEntity.getUuidPath();
        List<String> uuidPaths = new ArrayList<String>();
        uuidPaths.addAll(Arrays.asList(StringUtils.split(uuidPath, Separator.SLASH.getValue())));
        uuidPaths.remove(dmsFolderEntity.getUuid());
        if (uuidPaths.isEmpty()) {
            return Separator.SLASH.getValue() + dmsFolderEntity.getName();
        }
        String hql = "select name from DmsFolderEntity t where t.uuid in(:folderUuids) order by t.uuidPath asc";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuids", uuidPaths);
        List<String> dmsFileNames = this.dao.query(hql, values, String.class);
        dmsFileNames.add(dmsFolderEntity.getName());
        StringBuilder sb = new StringBuilder();
        for (String dmsFileName : dmsFileNames) {
            sb.append(Separator.SLASH.getValue() + dmsFileName);
        }
        return sb.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#renameFile(java.lang.String, java.lang.String)
     */
    @Override
    public void renameFile(String fileUuid, String newFileName) {
        DmsFileEntity fileEntity = dmsFileService.get(fileUuid);
        // 权限检测
        if (!hasFolderPermission(fileEntity.getFolderUuid(), FileActions.RENAME_FILE)) {
            throw new RuntimeException("没有权限重命名文件！");
        }
        if (!FileHelper.isDyform(fileEntity) && !FileHelper.isValidFileName(newFileName)) {
            throw new RuntimeException(FileHelper.getInValidFileNameTip(newFileName));
        }
        fileEntity.setFileName(newFileName);
        dmsFileService.save(fileEntity);
        // 如果是表单，更新对应的标题映射字段
        if (FileHelper.isDyform(fileEntity)) {
            String folderUuid = fileEntity.getFolderUuid();
            DmsFolderDyformDefinition dmsFolderDyformDefinition = dmsFolderConfigurationService
                    .getFolderDyformDefinitionByFolderUuid(folderUuid);
            if (dmsFolderDyformDefinition != null) {
                String fileNameField = dmsFolderDyformDefinition.getFileNameField();
                if (StringUtils.isNotBlank(fileNameField)) {
                    DyFormData dyFormData = dyFormApiFacade.getDyFormData(fileEntity.getDataDefUuid(),
                            fileEntity.getDataUuid());
                    if (dyFormData.isFieldExist(fileNameField)) {
                        dyFormData.setFieldValue(fileNameField, newFileName);
                        dyFormApiFacade.saveFormData(dyFormData);
                    }
                }
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#shareFile(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String shareFile(String fileUuid, String shareOrgId, String shareOrgName) {
        DmsShareInfoEntity dmsShareInfoEntity = new DmsShareInfoEntity();
        dmsShareInfoEntity.setDataType(DmsFileEntity.class.getCanonicalName());
        dmsShareInfoEntity.setDataUuid(fileUuid);
        dmsShareInfoEntity.setOwnerId(SpringSecurityUtils.getCurrentUserId());
        dmsShareInfoEntity.setShareOrgId(shareOrgId);
        dmsShareInfoEntity.setShareOrgName(shareOrgName);
        dmsShareInfoEntity.setShareTime(Calendar.getInstance().getTime());
        dmsShareInfoService.save(dmsShareInfoEntity);
        return dmsShareInfoEntity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#cancelShareFile(java.lang.String, java.lang.String)
     */
    @Override
    public void cancelShareFile(String fileUuid, String shareUuid) {
        String hql = "delete from DmsShareInfoEntity t where t.dataType = :dataType and t.dataUuid = :dataUuid and t.uuid = :shareUuid and t.ownerId = :ownerId";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", DmsFileEntity.class.getCanonicalName());
        values.put("dataUuid", fileUuid);
        values.put("shareUuid", shareUuid);
        values.put("ownerId", SpringSecurityUtils.getCurrentUserId());
        this.dao.batchExecute(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#attentionFile(java.lang.String)
     */
    @Override
    public void attentionFile(String fileUuid) {
        // TODO Auto-generated method stub

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#checkOutFile(java.lang.String)
     */
    @Override
    public void checkOutFile(String fileUuid) {
        // TODO Auto-generated method stub

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#checkInFile(java.lang.String)
     */
    @Override
    public void checkInFile(String fileUuid) {
        // TODO Auto-generated method stub

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#moveFile(java.lang.String, java.lang.String)
     */
    @Override
    public void moveFile(String sourceFileUuid, String destFolderUuid) {
        // 权限检测
        if (!hasFilePermission(sourceFileUuid, FileActions.MOVE_FILE)) {
            throw new RuntimeException("没有权限移动文件！");
        }
        if (!(hasFolderPermission(destFolderUuid, FileActions.CREATE_FILE) || hasFolderPermission(destFolderUuid,
                FileActions.CREATE_DOCUMENT))) {
            throw new RuntimeException("没有权限创建移动的文件！");
        }

        String hql = "update DmsFileEntity t set t.folderUuid = :folderUuid where t.uuid = :uuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", sourceFileUuid);
        values.put("folderUuid", destFolderUuid);
        this.dao.batchExecute(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#copyFile(java.lang.String, java.lang.String)
     */
    @Override
    public String copyFile(String sourceFileUuid, String destFolderUuid) {
        // 权限检测
        if (!hasFilePermission(sourceFileUuid, FileActions.COPY_FILE)) {
            throw new RuntimeException("没有权限复制文件！");
        }
        if (!(hasFolderPermission(destFolderUuid, FileActions.CREATE_FILE) || hasFolderPermission(destFolderUuid,
                FileActions.CREATE_DOCUMENT))) {
            throw new RuntimeException("没有权限创建复制的文件！");
        }

        DmsFileEntity fileEntity = this.dmsFileService.get(sourceFileUuid);
        DmsFileEntity newFileEntity = new DmsFileEntity();
        BeanUtils.copyProperties(fileEntity, newFileEntity, IdEntity.BASE_FIELDS);
        String newFileUuid = UUID.randomUUID().toString();
        newFileEntity.setUuid(newFileUuid);
        newFileEntity.setFolderUuid(destFolderUuid);
        newFileEntity.setLibraryUuid(dmsFolderService.getOne(destFolderUuid).getLibraryUuid());

        // 通过不同类型的文件实现接口去处理
        // TODO
        if (FileHelper.isDyform(fileEntity)) {
            String sourceFormUuid = fileEntity.getDataDefUuid();
            String sourceDataUuid = fileEntity.getDataUuid();
            String targetFormUuid = sourceFormUuid;
            String targetDataUuid = dyFormApiFacade.copyFormData(sourceFormUuid, sourceDataUuid, targetFormUuid);
            newFileEntity.setDataUuid(targetDataUuid);
        } else {
            // 放入MONGO文件夹
            mongoFileService.pushFileToFolder(newFileUuid, newFileEntity.getDataUuid(), null);
        }

        this.dmsFileService.save(newFileEntity);

        return newFileEntity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#deleteFile(java.lang.String)
     */
    @Override
    public void deleteFile(String fileUuid) {
        // 权限检测
        if (!hasFilePermission(fileUuid, FileActions.DELETE_FILE)) {
            throw new RuntimeException("没有权限删除文件！");
        }

        DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
        dmsFileEntity.setStatus(FileStatus.DELETE);
        this.dmsFileService.save(dmsFileEntity);

        // 将表单的配置的状态字段更新为已删除，通过不同类型的文件实现接口去处理
        // TODO
        if (FileHelper.isDyform(dmsFileEntity)) {
            DmsFolderDyformDefinition dmsFolderDyformDefinition = dmsFolderConfigurationService
                    .getFolderDyformDefinitionByFolderUuid(dmsFileEntity.getFolderUuid());
            if (dmsFolderDyformDefinition != null) {
                String fileStatusField = dmsFolderDyformDefinition.getFileStatusField();
                if (StringUtils.isNotBlank(fileStatusField)) {
                    DyFormData dyFormData = dyFormApiFacade.getDyFormData(dmsFileEntity.getDataDefUuid(),
                            dmsFileEntity.getDataUuid());
                    if (dyFormData.isFieldExist(fileStatusField)) {
                        dyFormData.setFieldValue(fileStatusField, FileStatus.DELETE);
                        dyFormApiFacade.saveFormData(dyFormData);
                    }
                }
            }
        }

        // 生成回收站记录
        dmsRecycleBinService.addData(DmsFileEntity.class.getCanonicalName(), fileUuid,
                SpringSecurityUtils.getCurrentUserId());

        // 逻辑删除索引
        dmsFileDoucmentIndexService.logicDelete(dmsFileEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#deleteFileByFolderUuidAndDataUuid(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteFileByFolderUuidAndDataUuid(String folderUuid, String dataUuid) {
        DmsFileEntity dmsFileEntity = dmsFileService.getByFolderUuidAndDataUuid(folderUuid, dataUuid);
        deleteFile(dmsFileEntity.getUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#restoreFile(java.lang.String)
     */
    @Override
    public void restoreFile(String fileUuid) {
        String hql = "update DmsFileEntity t set t.status = :fileStatus where t.uuid = :fileUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("fileUuid", fileUuid);
        values.put("fileStatus", FileStatus.NORMAL);
        this.dao.batchExecute(hql, values);

        // 将表单的配置的状态字段更新为已删除，通过不同类型的文件实现接口去处理
        // TODO
        DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
        if (FileHelper.isDyform(dmsFileEntity)) {
            DmsFolderDyformDefinition dmsFolderDyformDefinition = dmsFolderConfigurationService
                    .getFolderDyformDefinitionByFolderUuid(dmsFileEntity.getFolderUuid());
            String fileStatusField = dmsFolderDyformDefinition.getFileStatusField();
            if (StringUtils.isNotBlank(fileStatusField)) {
                DyFormData dyFormData = dyFormApiFacade.getDyFormData(dmsFileEntity.getDataDefUuid(),
                        dmsFileEntity.getDataUuid());
                if (dyFormData.isFieldExist(fileStatusField)) {
                    dyFormData.setFieldValue(fileStatusField, FileStatus.NORMAL);
                    dyFormApiFacade.saveFormData(dyFormData);
                }
            }
        }

        // 删除回收站
        dmsRecycleBinService.removeByDataUuid(fileUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#restoreFileByFolderUuidAndDataUuid(java.lang.String, java.lang.String)
     */
    @Override
    public void restoreFileByFolderUuidAndDataUuid(String folderUuid, String dataUuid) {
        DmsFileEntity dmsFileEntity = dmsFileService.getByFolderUuidAndDataUuid(folderUuid, dataUuid);
        restoreFile(dmsFileEntity.getUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#physicalDeleteFile(java.lang.String)
     */
    @Override
    public void physicalDeleteFile(String fileUuid) {
        // 权限检测
        if (!hasFilePermission(fileUuid, FileActions.DELETE_FILE)) {
            throw new RuntimeException("没有权限删除文件！");
        }
        DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
        if (FileHelper.isDyform(dmsFileEntity)) {
            if (dmsFileService.countByDataUuid(dmsFileEntity.getDataUuid()) == 1) {
                dyFormApiFacade.delFormData(dmsFileEntity.getDataDefUuid(), dmsFileEntity.getDataUuid());
            }
        }
        dmsFileService.remove(dmsFileEntity);

        // 删除回收站
        dmsRecycleBinService.removeByDataUuid(fileUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#physicalDeleteFileByFolderUuidAndDataUuid(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void physicalDeleteFileByFolderUuidAndDataUuid(String folderUuid, String dataUuid, String dataDefUuid) {
        DmsFileEntity dmsFileEntity = dmsFileService.getByFolderUuidAndDataUuid(folderUuid, dataUuid);
        if (dmsFileEntity != null) {
            physicalDeleteFile(dmsFileEntity.getUuid());
            if (FileHelper.isDyform(dmsFileEntity)) {
                dyFormApiFacade.delFormData(dmsFileEntity.getDataDefUuid(), dataUuid);
            }
        } else {
            dyFormApiFacade.delFormData(dataDefUuid, dataUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#getFolderActions(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DmsFileAction> getFolderActions(String folderUuid) {
        // 角色
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        values.put("folderStatus", FileStatus.NORMAL);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        List<DmsFolderAssignRoleQueryItem> items = this.nativeDao.namedQuery("dmsFolderAssignRoleQuery", values,
                DmsFolderAssignRoleQueryItem.class);
        List<String> permitRoles = new ArrayList<String>();
        List<String> denyRoles = new ArrayList<String>();
        for (DmsFolderAssignRoleQueryItem item : items) {
            String permit = item.getPermit();
            String deny = item.getDeny();
            String roleUuid = item.getRoleUuid();
            if ("Y".equalsIgnoreCase(permit) && !"Y".equalsIgnoreCase(deny)) {
                permitRoles.add(roleUuid);
            }
            if ("Y".equalsIgnoreCase(deny)) {
                denyRoles.add(roleUuid);
            }
        }
        permitRoles.removeAll(denyRoles);

        // 文件夹数据类型定义
        List<String> folderDataTypes = dmsFolderConfigurationService.getFolderDataTypeByFolderUuid(folderUuid);

        // 角色操作
        List<String> permitRoleActions = dmsRoleActionService.getActionByRoleUuids(permitRoles);
        List<String> denyRoleActions = dmsRoleActionService.getActionByRoleUuids(denyRoles);
        permitRoleActions.removeAll(denyRoleActions);

        List<DmsFileAction> dmsFileActions = new ArrayList<DmsFileAction>();
        for (String action : permitRoleActions) {
            FileAction fileAction = fileActionManager.getFileAction(action);
            if (fileAction == null) {
                continue;
            }
            String fileActionId = fileAction.getId();
            // 夹数据类型为空，不能上传文件、新建文档
            if (CollectionUtils.isEmpty(folderDataTypes)) {
                if (FileActions.CREATE_FILE.equals(fileActionId) || FileActions.CREATE_DOCUMENT.equals(fileActionId)) {
                    continue;
                }
            } else {
                // 文件类型只能上传文件
                if (!folderDataTypes.contains(DataType.DYFORM.getId()) && FileActions.CREATE_DOCUMENT.equals(fileActionId)) {
                    continue;
                }
                // 表单类型只能新建文档
                if (!folderDataTypes.contains(DataType.FILE.getId()) && FileActions.CREATE_FILE.equals(fileActionId)) {
                    continue;
                }
            }
            DmsFileAction dmsFileAction = new DmsFileAction();
            BeanUtils.copyProperties(fileAction, dmsFileAction);
            dmsFileActions.add(dmsFileAction);
        }
        return dmsFileActions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#getFileActions(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DmsFileAction> getFileActions(String fileUuid) {
        DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
        List<DmsFileAction> dmsFileActions = getFolderActions(dmsFileEntity.getFolderUuid());
        // 文件分配的权限
        dmsFileActions.addAll(getFileAssignActions(fileUuid));
        String userId = SpringSecurityUtils.getCurrentUserId();
        // 文件创建者可删除
        if (StringUtils.equals(dmsFileEntity.getCreator(), userId)) {
            FileAction deleteFileAction = fileActionManager.getFileAction(FileActions.DELETE_FILE);
            DmsFileAction dmsDeleteFileAction = new DmsFileAction();
            dmsDeleteFileAction.setIsCreator(true);
            BeanUtils.copyProperties(deleteFileAction, dmsDeleteFileAction);
            if (!dmsFileActions.contains(dmsDeleteFileAction)) {
                dmsFileActions.add(dmsDeleteFileAction);
            }
        }
        return dmsFileActions;
    }

    /**
     * @param fileUuid
     * @return
     */
    private List<DmsFileAction> getFileAssignActions(String fileUuid) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("fileUuid", fileUuid);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        List<DmsFileAssignActionQueryItem> items = this.nativeDao.namedQuery("dmsFileAssignActionQuery", values,
                DmsFileAssignActionQueryItem.class);

        List<DmsFileAction> fileActions = Lists.newArrayList();
        for (DmsFileAssignActionQueryItem item : items) {
            FileAction fileAction = fileActionManager.getFileAction(item.getAction());
            if (fileAction == null) {
                continue;
            }
            DmsFileAction dmsFileAction = new DmsFileAction();
            BeanUtils.copyProperties(fileAction, dmsFileAction);
            fileActions.add(dmsFileAction);
        }
        return fileActions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listReadFolderUuid(java.lang.String, java.util.Map)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> listReadFolderUuid(String folderUuid, Map<String, Object> queryParams) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.putAll(queryParams);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        List<String> folderUuids = getReadFolderUuidsFromCache(folderUuid, "listReadFolderUuid");
        if (folderUuids == null) {
            // folderUuids = this.nativeDao.namedQuery("dmsListReadFolderUuidQuery", values);
            folderUuids = this.nativeDao.namedQuery("dmsAllowListReadFolderUuidQuery", values);
            if (folderUuids == null) {
                folderUuids = Lists.newArrayList();
            }
            folderUuids.removeAll(this.nativeDao.namedQuery("dmsDenyListReadFolderUuidQuery", values));
            putReadFolderUuidsToCache(folderUuid, folderUuids, "listReadFolderUuid");
        }
        return folderUuids;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listReadAllFolderUuid(java.lang.String, java.util.Map)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> listReadAllFolderUuid(String folderUuid, Map<String, Object> queryParams) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.putAll(queryParams);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        List<String> folderUuids = getReadFolderUuidsFromCache(folderUuid, "listReadAllFolderUuid");
        if (folderUuids == null) {
            // folderUuids = this.nativeDao.namedQuery("dmsListReadAllFolderUuidQuery", values);
            folderUuids = this.nativeDao.namedQuery("dmsAllowListReadAllFolderUuidQuery", values);
            if (folderUuids == null) {
                folderUuids = Lists.newArrayList();
            }
            folderUuids.removeAll(this.nativeDao.namedQuery("dmsDenyListReadAllFolderUuidQuery", values));
            putReadFolderUuidsToCache(folderUuid, folderUuids, "listReadAllFolderUuid");
        }
        return folderUuids;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listReadAllFileFolderUuid(java.lang.String, java.util.Map)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> listReadAllFileFolderUuid(String folderUuid, Map<String, Object> queryParams) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.putAll(queryParams);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        List<String> folderUuids = getReadFolderUuidsFromCache(folderUuid, "listReadAllFileFolderUuid");
        if (folderUuids == null) {
            // folderUuids = this.nativeDao.namedQuery("dmsListReadAllFileFolderUuidQuery", values);
            folderUuids = this.nativeDao.namedQuery("dmsAllowListReadAllFileFolderUuidQuery", values);
            if (folderUuids == null) {
                folderUuids = Lists.newArrayList();
            }
            folderUuids.removeAll(this.nativeDao.namedQuery("dmsDenyListReadAllFileFolderUuidQuery", values));
            putReadFolderUuidsToCache(folderUuid, folderUuids, "listReadAllFileFolderUuid");
        }
        return folderUuids;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileActionService#listReadAllFolderAndFileFolderUuid(java.lang.String, java.util.Map)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, List<String>> listReadAllFolderAndFileFolderUuid(String folderUuid,
                                                                        Map<String, Object> queryParams) {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.putAll(queryParams);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        List<String> allFolders = getReadFolderUuidsFromCache(folderUuid, "listReadAllFolderUuid");
        List<String> allFileFolders = getReadFolderUuidsFromCache(folderUuid, "listReadAllFileFolderUuid");
        if (allFolders == null || allFileFolders == null) {
            allFolders = Lists.newArrayList();
            allFileFolders = Lists.newArrayList();
            // List<QueryItem> folderItems = this.nativeDao.namedQuery("dmsListReadAllFolderAndFileFolderUuidQuery", values, QueryItem.class);
            List<QueryItem> allowFolderItems = this.nativeDao.namedQuery("dmsAllowListReadAllFolderAndFileFolderUuidQuery", values, QueryItem.class);
            List<QueryItem> denyFolderItems = this.nativeDao.namedQuery("dmsDenyListReadAllFolderAndFileFolderUuidQuery", values, QueryItem.class);
            for (QueryItem queryItem : allowFolderItems) {
                if (StringUtils.equals("1", ObjectUtils.toString(queryItem.get("fileType")))) {
                    allFolders.add(queryItem.getString("folderUuid"));
                } else {
                    allFileFolders.add(queryItem.getString("folderUuid"));
                }
            }
            for (QueryItem queryItem : denyFolderItems) {
                if (StringUtils.equals("1", ObjectUtils.toString(queryItem.get("fileType")))) {
                    allFolders.remove(queryItem.getString("folderUuid"));
                } else {
                    allFileFolders.remove(queryItem.getString("folderUuid"));
                }
            }
            putReadFolderUuidsToCache(folderUuid, allFolders, "listReadAllFolderUuid");
            putReadFolderUuidsToCache(folderUuid, allFileFolders, "listReadAllFileFolderUuid");
        }
        Map<String, List<String>> map = Maps.newHashMap();
        map.put("folder", allFolders);
        map.put("file", allFileFolders);
        return map;
    }

    /**
     * @param folderUuid
     * @param string
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<String> getReadFolderUuidsFromCache(String folderUuid, String keyPrefix) {
        Cache cache = cacheManager.getCache(CacheName.DEFAULT);
        List<String> readFolderUuids = null;
        if (cache != null) {
            ValueWrapper valueWrapper = cache.get(keyPrefix + folderUuid + SpringSecurityUtils.getCurrentUserId());
            if (valueWrapper != null) {
                readFolderUuids = (List<String>) valueWrapper.get();
            }
        }
        return readFolderUuids;
    }

    /**
     * @param folderUuid
     * @param folderUuids
     * @param string
     */
    private void putReadFolderUuidsToCache(String folderUuid, List<String> folderUuids, String keyPrefix) {
        Cache cache = cacheManager.getCache(CacheName.DEFAULT);
        if (cache != null) {
            cache.put(keyPrefix + folderUuid + SpringSecurityUtils.getCurrentUserId(), folderUuids);
        }
    }

}
