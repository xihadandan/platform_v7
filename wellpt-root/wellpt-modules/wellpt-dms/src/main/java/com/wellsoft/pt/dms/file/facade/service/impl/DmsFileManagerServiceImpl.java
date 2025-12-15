/*
 * @(#)Jan 19, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.web.JsonDataServicesContextHolder;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreDefinitionBean;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.basicdata.selective.support.DataItem;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQueryItem;
import com.wellsoft.pt.dms.bean.DmsRoleBean;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.entity.DmsFolderConfigurationEntity;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService;
import com.wellsoft.pt.dms.file.registry.DmsFileTypeRegistry;
import com.wellsoft.pt.dms.file.store.DmsFileQueryInterface;
import com.wellsoft.pt.dms.file.view.FileTypeInfos;
import com.wellsoft.pt.dms.file.view.FileViewer;
import com.wellsoft.pt.dms.file.view.ViewFileInfo;
import com.wellsoft.pt.dms.model.*;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dms.service.DmsFolderConfigurationService;
import com.wellsoft.pt.dms.service.DmsFolderService;
import com.wellsoft.pt.dms.service.DmsRoleService;
import com.wellsoft.pt.dms.support.EncodingDetect;
import com.wellsoft.pt.dms.support.FileHelper;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 19, 2018.1	zhulh		Jan 19, 2018		Create
 * </pre>
 * @date Jan 19, 2018
 */
@Service
public class DmsFileManagerServiceImpl implements DmsFileManagerService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DmsFolderConfigurationService dmsFolderConfigurationService;

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    @Autowired
    private DmsFileService dmsFileService;

    @Autowired
    private DmsRoleService dmsRoleService;

    @Autowired
    private DmsFolderService dmsFolderService;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired(required = false)
    private List<FileViewer> fileViewers;

    @Autowired
    private DmsFileTypeRegistry dmsFileTypeRegistry;

    @Autowired
    private CdDataStoreDefinitionService cdDataStoreDefinitionService;

    @Autowired
    private List<DmsFileQueryInterface> dmsFileQueryInterfaceList;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#isConfigDocumentForCreate(java.lang.String)
     */
    @Override
    public boolean isConfigDocumentForCreate(String folderUuid) {
        DmsFolderDyformDefinition dmsFolderDyformDefinition = dmsFolderConfigurationService
                .getFolderDyformDefinitionByFolderUuid(folderUuid);
        return dmsFolderDyformDefinition != null;
    }

    @Override
    public DmsFolderDyformDefinition getFolderDyformDefinitionByFolderUuid(String folderUuid) {
        return dmsFolderConfigurationService.getFolderDyformDefinitionByFolderUuid(folderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getAllFolderDyformDefinitionByFolderUuid(java.lang.String)
     */
    @Override
    public List<DmsFolderDyformDefinition> getAllFolderDyformDefinitionByFolderUuid(String folderUuid) {
        return dmsFolderConfigurationService.getAllFolderDyformDefinitionsByFolderUuid(folderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#isValidFileName(java.lang.String)
     */
    @Override
    public boolean isValidFileName(String fileName) {
        return FileHelper.isValidFileName(fileName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getFolderListViewId(java.lang.String)
     */
    @Override
    public String getFolderListViewId(String folderUuid) {
        DmsFolderConfigurationEntity config = dmsFolderConfigurationService.getByFolderUuid(folderUuid);
        DmsFolderConfiguration configuration = JsonUtils.json2Object(config.getConfiguration(),
                DmsFolderConfiguration.class);
        return configuration.getListViewId();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getFolderConfiguration(java.lang.String)
     */
    @Override
    public DmsFolderConfiguration getFolderConfiguration(String folderUuid) {
        DmsFolderConfigurationEntity config = dmsFolderConfigurationService.getByFolderUuid(folderUuid);
        if (config == null || StringUtils.isBlank(config.getConfiguration())) {
            return new DmsFolderConfiguration();
        }
        DmsFolderConfiguration configuration = JsonUtils.json2Object(config.getConfiguration(),
                DmsFolderConfiguration.class);
        return configuration;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getFolderDataViewConfiguration(java.lang.String)
     */
    @Override
    public DmsFolderDataViewConfiguration getFolderDataViewConfiguration(String folderUuid) {
        return dmsFolderConfigurationService.getFolderDataViewConfigurationByFolderUuid(folderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#saveFolderConfiguration(com.wellsoft.pt.dms.model.DmsFolderConfiguration)
     */
    @Override
    public void saveFolderConfiguration(DmsFolderConfiguration dmsFolderConfiguration) {
        dmsFolderConfigurationService.saveFolderConfiguration(dmsFolderConfiguration);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#createFolder(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String createFolder(String folderUuid, String folderName, String parentFolderUuid) {
        return dmsFileServiceFacade.createFolder(folderUuid, folderName, parentFolderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#renameFolder(java.lang.String, java.lang.String)
     */
    @Override
    public void renameFolder(String folderUuid, String newFolderName) {
        dmsFileServiceFacade.renameFolder(folderUuid, newFolderName);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#deleteFolder(java.lang.String)
     */
    @Override
    public void deleteFolder(String folderUuid) {
        dmsFileServiceFacade.deleteFolder(folderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#renameFile(java.lang.String, java.lang.String)
     */
    @Override
    public void renameFile(String fileUuid, String newFileName) {
        dmsFileServiceFacade.renameFile(fileUuid, newFileName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#readFileContentAsString(java.lang.String)
     */
    @Override
    public String readFileContentAsString(String fileUuid) {
        String fileContent = StringUtils.EMPTY;
        DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
        if (dmsFileEntity == null) {
            return fileContent;
        }
        String fileId = dmsFileEntity.getDataUuid();
        MongoFileEntity file = mongoFileService.getFile(fileId);
        if (file == null) {
            return fileContent;
        }

        File tmpFile = createTempFile(file);
        Writer writer = null;
        try {
            String encoding = EncodingDetect.getJavaEncode(tmpFile.getAbsolutePath());
            fileContent = FileUtils.readFileToString(tmpFile.getAbsoluteFile(), encoding);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            tmpFile.delete();
            IOUtils.closeQuietly(writer);
        }
        return fileContent;
    }

    /**
     * @param file
     * @return
     */
    private File createTempFile(MongoFileEntity file) {
        File downloadDir = new File(Config.APP_DATA_DIR, "dmsfiles");
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        String fileName = UUID.randomUUID().toString();
        File tmpFile = new File(downloadDir, fileName);
        InputStream input = null;
        OutputStream output = null;
        try {
            input = file.getInputstream();
            output = new FileOutputStream(tmpFile);
            IOUtils.copy(input, output);
        } catch (Exception e) {
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
        return tmpFile;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getAttributes(com.wellsoft.pt.dms.model.DmsFile)
     */
    @Override
    public DmsFileAttributes getAttributes(DmsFile dmsFile) {
        DmsFileAttributes attributes = null;
        if (FileHelper.isFolder(dmsFile)) {
            attributes = getFolderAttributes(dmsFile);
        } else {
            attributes = getFileAttributes(dmsFile);
        }
        return attributes;
    }

    /**
     * @param dmsFile
     * @return
     */
    private DmsFileAttributes getFolderAttributes(DmsFile dmsFile) {
        return dmsFileServiceFacade.getFolderAttributes(dmsFile.getUuid());
    }

    /**
     * @param dmsFile
     * @return
     */
    private DmsFileAttributes getFileAttributes(DmsFile dmsFile) {
        return dmsFileServiceFacade.getFileAttributes(dmsFile.getUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getViewFileInfo(com.wellsoft.pt.dms.model.DmsFile)
     */
    @Override
    public ViewFileInfo getViewFileInfo(DmsFile dmsFile) {
        ViewFileInfo viewFileInfo = null;
        if (CollectionUtils.isEmpty(fileViewers)) {
            return viewFileInfo;
        }
        for (FileViewer fileView : fileViewers) {
            if (fileView.matches(dmsFile.getContentType())) {
                String viewFileUrl = fileView.getViewUrl(dmsFile, JsonDataServicesContextHolder.getRequest(),
                        JsonDataServicesContextHolder.getResponse());
                viewFileInfo = new ViewFileInfo();
                viewFileInfo.setContentType(dmsFile.getContentType());
                viewFileInfo.setFileUuid(dmsFile.getUuid());
                viewFileInfo.setViewUrl(viewFileUrl);
                break;
            }
        }
        return viewFileInfo;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getFileTypeInfos()
     */
    @Override
    public FileTypeInfos getFileTypeInfos() {
        FileTypeInfos fileTypeInfos = new FileTypeInfos();
        fileTypeInfos.setTypeNameMap(dmsFileTypeRegistry.getTypeNameAsMap());
        fileTypeInfos.setDownloadableMap(dmsFileTypeRegistry.getDownloadableAsMap());
        return fileTypeInfos;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getRolesByCategory(java.lang.String)
     */
    @Override
    public List<DmsRoleBean> getRolesByCategory(String category) {
        List<DmsRoleEntity> dmsRoleEntities = dmsRoleService.getByCategory(category);
        return BeanUtils.copyCollection(dmsRoleEntities, DmsRoleBean.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getRoleCategories()
     */
    @Override
    public List<DataItem> getRoleCategories() {
        return dmsRoleService.getRoleCategories();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#shareFile(java.util.List, java.lang.String, java.lang.String)
     */
    @Override
    public void shareFile(List<DmsFile> dmsFiles, String shareOrgId, String shareOrgName) {
        for (DmsFile dmsFile : dmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                shareFolder(dmsFile, shareOrgId, shareOrgName);
            } else {
                shareFile(dmsFile, shareOrgId, shareOrgName);
            }
        }
    }

    /**
     * @param dmsFile
     * @param shareOrgId
     * @param shareOrgName
     */
    private void shareFolder(DmsFile dmsFile, String shareOrgId, String shareOrgName) {
        dmsFileServiceFacade.shareFolder(dmsFile.getUuid(), shareOrgId, shareOrgName);
    }

    /**
     * @param dmsFile
     * @param shareOrgId
     * @param shareOrgName
     */
    private void shareFile(DmsFile dmsFile, String shareOrgId, String shareOrgName) {
        dmsFileServiceFacade.shareFile(dmsFile.getUuid(), shareOrgId, shareOrgName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#cancelShareFile(java.util.List)
     */
    @Override
    public void cancelShareFile(List<DmsFileShareInfo> dmsFiles) {
        for (DmsFileShareInfo dmsFile : dmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                cancelShareFolder(dmsFile);
            } else {
                cancelShareFile(dmsFile);
            }
        }
    }

    /**
     * @param dmsFile
     */
    private void cancelShareFolder(DmsFileShareInfo dmsFile) {
        dmsFileServiceFacade.cancelShareFolder(dmsFile.getUuid(), dmsFile.getShareUuid());
    }

    /**
     * @param dmsFile
     */
    private void cancelShareFile(DmsFileShareInfo dmsFile) {
        dmsFileServiceFacade.cancelShareFile(dmsFile.getUuid(), dmsFile.getShareUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#moveFile(java.util.List, java.lang.String)
     */
    @Override
    public void moveFile(List<DmsFile> sourceDmsFiles, String destFolderUuid) {
        for (DmsFile dmsFile : sourceDmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                if (existsFolderNameByParentFolderUuid(dmsFile.getName(), destFolderUuid)) {
                    throw new BusinessException(String.format("夹[%s]已存在，不能移入夹！", dmsFile.getName()));
                }
            } else {
//                if (existsFileNameByFolderUuid(dmsFile.getName(), destFolderUuid)) {
//                    throw new BusinessException(String.format("文件[%s]已存在，不能移入件！", dmsFile.getName()));
//                }
            }
        }

        for (DmsFile dmsFile : sourceDmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                moveFolder(dmsFile, destFolderUuid);
            } else {
                moveFile(dmsFile, destFolderUuid);
            }
        }
    }

    /**
     * @param dmsFile
     * @param destFolderUuid
     */
    private void moveFolder(DmsFile dmsFile, String destFolderUuid) {
        dmsFileServiceFacade.moveFolder(dmsFile.getUuid(), destFolderUuid);
    }

    /**
     * @param dmsFile
     * @param destFolderUuid
     */
    private void moveFile(DmsFile dmsFile, String destFolderUuid) {
        dmsFileServiceFacade.moveFile(dmsFile.getUuid(), destFolderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#copyFile(java.util.List, java.lang.String)
     */
    @Override
    public List<String> copyFile(List<DmsFile> sourceDmsFiles, String destFolderUuid) {
        for (DmsFile dmsFile : sourceDmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                if (existsFolderNameByParentFolderUuid(dmsFile.getName(), destFolderUuid)) {
                    throw new BusinessException(String.format("夹[%s]已存在，不能复制夹！", dmsFile.getName()));
                }
            } else {
//                if (existsFileNameByFolderUuid(dmsFile.getName(), destFolderUuid)) {
//                    throw new BusinessException(String.format("文件[%s]已存在，不能复制文件！", dmsFile.getName()));
//                }
            }
        }

        List<String> fileUuids = Lists.newArrayList();
        for (DmsFile dmsFile : sourceDmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                fileUuids.add(copyFolder(dmsFile, destFolderUuid));
            } else {
                fileUuids.add(copyFile(dmsFile, destFolderUuid));
            }
        }
        return fileUuids;
    }

    /**
     * @param dmsFile
     * @param destFolderUuid
     */
    private String copyFolder(DmsFile dmsFile, String destFolderUuid) {
        return dmsFileServiceFacade.copyFolder(dmsFile.getUuid(), destFolderUuid);
    }

    /**
     * @param dmsFile
     * @param destFolderUuid
     */
    private String copyFile(DmsFile dmsFile, String destFolderUuid) {
        return dmsFileServiceFacade.copyFile(dmsFile.getUuid(), destFolderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#deleteFile(java.util.List)
     */
    @Override
    public void deleteFile(List<DmsFile> dmsFiles) {
        for (DmsFile dmsFile : dmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                deleteFolder(dmsFile);
            } else {
                deleteFile(dmsFile);
            }
        }
    }

    /**
     * @param dmsFile
     */
    private void deleteFolder(DmsFile dmsFile) {
        dmsFileServiceFacade.deleteFolder(dmsFile.getUuid());
    }

    /**
     * @param dmsFile
     */
    private void deleteFile(DmsFile dmsFile) {
        dmsFileServiceFacade.deleteFile(dmsFile.getUuid());
    }

    @Override
    public void restoreFile(List<DmsFile> dmsFiles) {
        for (DmsFile dmsFile : dmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                dmsFileServiceFacade.restoreFolder(dmsFile.getUuid());
            } else {
                dmsFileServiceFacade.restoreFile(dmsFile.getUuid());
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#physicalDeleteFile(java.util.List)
     */
    @Override
    public void physicalDeleteFile(List<DmsFile> dmsFiles) {
        for (DmsFile dmsFile : dmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                physicalDeleteFolder(dmsFile);
            } else {
                physicalDeleteFile(dmsFile);
            }
        }
    }

    /**
     * @param dmsFile
     */
    private void physicalDeleteFolder(DmsFile dmsFile) {
        dmsFileServiceFacade.physicalDeleteFolder(dmsFile.getUuid());
    }

    /**
     * @param dmsFile
     */
    private void physicalDeleteFile(DmsFile dmsFile) {
        dmsFileServiceFacade.physicalDeleteFile(dmsFile.getUuid());
    }

    /**
     * @param folderName
     * @param parentFolderUuid
     */
    @Override
    public boolean checkTheSameNameForCreateFolder(String folderName, String parentFolderUuid) {
        return dmsFileServiceFacade.checkTheSameNameForCreateFolder(folderName, parentFolderUuid);
    }

    /**
     * 检查同一目录下文件名是否重复
     *
     * @param folderName
     * @param parentFolderUuid
     * @return
     */
    @Override
    public boolean existsFolderNameByParentFolderUuid(String folderName, String parentFolderUuid) {
        return dmsFolderService.existsFolderNameByParentFolderUuid(folderName, parentFolderUuid);
    }

    /**
     * 检查同一目录下文件名是否重复
     *
     * @param fileName
     * @param folderUuid
     * @return
     */
    @Override
    public boolean existsFileNameByFolderUuid(String fileName, String folderUuid) {
        return dmsFileService.existsFileNameByFolderUuid(fileName, folderUuid);
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
        return dmsFolderService.existsTheSameFolderNameWidthFolderUuid(folderName, folderUuid);
    }

    /**
     * 检查同一目录下文件名是否重复
     *
     * @param fileName
     * @param fileUuid
     * @return
     */
    @Override
    public boolean existsTheSameFileNameWidthFileUuid(String fileName, String fileUuid) {
        return dmsFileService.existsTheSameFileNameWidthFileUuid(fileName, fileUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#checkFileDataExists(java.util.Collection)
     */
    @Override
    public boolean checkFileDataExists(Collection<String> fileUuids) {
        for (String fileUuid : fileUuids) {
            DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
            String fileId = dmsFileEntity.getDataUuid();
            MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileId);
            if (mongoFileEntity == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getApproveFlowSelectData(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData getApproveFlowSelectData(Select2QueryInfo queryInfo) {
        Select2QueryData select2Data = new Select2QueryData();
        String searchValue = queryInfo.getSearchValue();
        FlowDefinitionQuery flowDefinitionQuery = FlowEngine.getInstance().createQuery(FlowDefinitionQuery.class);
        if (StringUtils.isNotBlank(searchValue)) {
            flowDefinitionQuery.idLike(searchValue);
            flowDefinitionQuery.nameLike(searchValue);
        }
        flowDefinitionQuery.distinctVersion();
        List<FlowDefinitionQueryItem> flowDefinitionQueryItems = flowDefinitionQuery.list();
        for (FlowDefinitionQueryItem flowDefinitionQueryItem : flowDefinitionQueryItems) {
            String text = flowDefinitionQueryItem.getName();
            select2Data.addResultData(new Select2DataBean(flowDefinitionQueryItem.getId(), text));
        }
        return select2Data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getApproveFlowSelectDataByIds(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData getApproveFlowSelectDataByIds(Select2QueryInfo queryInfo) {
        String[] flowDefIds = queryInfo.getIds();
        FlowDefinitionQuery flowDefinitionQuery = FlowEngine.getInstance().createQuery(FlowDefinitionQuery.class);
        flowDefinitionQuery.ids(Arrays.asList(flowDefIds));
        flowDefinitionQuery.distinctVersion();
        List<FlowDefinitionQueryItem> flowDefinitionQueryItems = flowDefinitionQuery.list();
        return new Select2QueryData(flowDefinitionQueryItems, "id", "name", queryInfo.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#getFolderTreeAsync(java.lang.String)
     */
    @Override
    public List<TreeNode> getFolderTreeAsync(String treeNodeId) {
        List<DmsFolder> dmsFolders = null;
        if (TreeNode.ROOT_ID.equals(treeNodeId)) {
            dmsFolders = dmsFileServiceFacade.listRootFolder();
        } else {
            dmsFolders = dmsFileServiceFacade.listFolder(treeNodeId);
        }
        return converToTreeNodes(dmsFolders);
    }

    /**
     * @param dmsFolders
     * @return
     */
    private List<TreeNode> converToTreeNodes(List<DmsFolder> dmsFolders) {
        TreeNode treeNode = new TreeNode();
        for (DmsFolder dmsFolder : dmsFolders) {
            TreeNode folderNode = new TreeNode();
            folderNode.setId(dmsFolder.getUuid());
            folderNode.setName(dmsFolder.getName());
            folderNode.setIsParent(true);
            treeNode.getChildren().add(folderNode);
        }
        return treeNode.getChildren();
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService#isFileManagerDataStore(java.lang.String)
     */
    @Override
    public boolean isFileManagerDataStore(String dataStoreId) {
        CdDataStoreDefinitionBean cdDataStoreDefinitionBean = cdDataStoreDefinitionService.getBeanById(dataStoreId);
        if (cdDataStoreDefinitionBean != null) {
            String dataInterfaceName = cdDataStoreDefinitionBean.getDataInterfaceName();
            for (DmsFileQueryInterface dmsFileQueryInterface : dmsFileQueryInterfaceList) {
                if (StringUtils.equals(dmsFileQueryInterface.getClass().getCanonicalName(), dataInterfaceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Select2QueryData getDmsFolderSelectData(Select2QueryInfo queryInfo) {
        String unitid = SpringSecurityUtils.getCurrentUserUnitId();
        List<DmsFolderEntity> dmsFolderEntities = Lists.newArrayList();
        if (MultiOrgSystemUnit.PT_ID.equalsIgnoreCase(unitid)) {
            dmsFolderEntities = dmsFolderService.getAll();
        } else {
            DmsFolderEntity dmsFolder = new DmsFolderEntity();
            dmsFolder.setSystemUnitId(MultiOrgSystemUnit.PT_ID);
            dmsFolderEntities.addAll(dmsFolderService.findByExample(dmsFolder));

            dmsFolder.setSystemUnitId(unitid);
            dmsFolderEntities.addAll(dmsFolderService.findByExample(dmsFolder));
        }

        return new Select2QueryData(dmsFolderEntities, "uuid", "name");
    }

}
