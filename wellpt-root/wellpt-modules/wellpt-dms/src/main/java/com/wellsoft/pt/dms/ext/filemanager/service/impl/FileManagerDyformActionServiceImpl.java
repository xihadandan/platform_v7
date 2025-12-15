/*
 * @(#)Jan 23, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.filemanager.service.impl;

import com.wellsoft.pt.dms.config.support.Configuration;
import com.wellsoft.pt.dms.config.support.DmsFileManagerConfiguration;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.enums.FileTypeEnum;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService;
import com.wellsoft.pt.dms.ext.filemanager.service.FileManagerDyformActionService;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dms.service.DmsFolderService;
import com.wellsoft.pt.dms.support.FileStatus;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 23, 2018.1	zhulh		Jan 23, 2018		Create
 * </pre>
 * @date Jan 23, 2018
 */
@Service
@Transactional
public class FileManagerDyformActionServiceImpl extends BaseServiceImpl implements FileManagerDyformActionService {

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private DyFormActionService dyFormActionService;

    @Autowired
    private DmsFileService dmsFileService;

    @Autowired
    private DmsFolderService dmsFolderService;

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.filemanager.service.FileManagerDyformActionService#saveAsDraft(com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData, com.wellsoft.pt.dms.core.context.ActionContext)
     */
    @Override
    public DmsFile saveAsDraft(FileManagerDyFormActionData dyFormActionData, ActionContext actionContext) {
        String folderUuid = dyFormActionData.getFolderUuid();
        String fileUuid = dyFormActionData.getFileUuid();
        DyFormData dyFormData = dyFormActionData.getDyFormData();
        String dataUuid = null;

        // 设置草稿状态到表单中
        setFileStatusToDyformData(folderUuid, FileStatus.DRAFT, dyFormData, actionContext);

        // 保存表单信息
        boolean isVersioning = actionContext.getConfiguration().isEnableVersioning();
        if (isVersioning) {
            dataUuid = dyFormActionService.saveVersion(actionContext, dyFormActionData);
        } else {
            dataUuid = dyFormActionService.save(dyFormData);
        }

        // 文档名称
        String fileName = getFileName(folderUuid, dyFormData, actionContext);

        // 保存或更新文件信息
        DmsFileEntity dmsFileEntity = null;
        if (StringUtils.isNotBlank(fileUuid)) {
            dmsFileEntity = dmsFileService.get(fileUuid);
        } else {
            dmsFileEntity = dmsFileService.getByFolderUuidAndDataUuid(folderUuid, dataUuid);
        }
        if (dmsFileEntity == null) {
            dmsFileEntity = new DmsFileEntity();
        }
        dmsFileEntity.setFileName(fileName);
        dmsFileEntity.setContentType(FileTypeEnum.DYFORM.getType());
        dmsFileEntity.setDataDefUuid(dyFormData.getFormUuid());
        dmsFileEntity.setDataUuid(dataUuid);
        dmsFileEntity.setFolderUuid(folderUuid);
        dmsFileEntity.setLibraryUuid(dmsFolderService.getOne(folderUuid).getLibraryUuid());
        dmsFileEntity.setStatus(FileStatus.DRAFT);
        dmsFileService.save(dmsFileEntity);

        // 返回文件信息
        DmsFile dmsFile = new DmsFile();
        BeanUtils.copyProperties(dmsFileEntity, dmsFile);
        dmsFile.setName(dmsFileEntity.getFileName());
        dmsFile.setFileSize(dmsFileEntity.getFileSize());
        return dmsFile;
    }

    /**
     * @param folderUuid
     * @param fileStatus
     * @param dyFormData
     * @param actionContext
     */
    private void setFileStatusToDyformData(String folderUuid, String fileStatus, DyFormData dyFormData,
                                           ActionContext actionContext) {
        Configuration configuration = actionContext.getConfiguration();
        if (configuration instanceof DmsFileManagerConfiguration) {
            String fileStatusField = ((DmsFileManagerConfiguration) configuration).getFileStatusField(folderUuid);
            if (StringUtils.isNotBlank(fileStatusField) && dyFormData.isFieldExist(fileStatusField)) {
                dyFormData.setFieldValue(fileStatusField, fileStatus);
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param actionContext
     * @param dyFormData
     * @return
     */
    private String getFileName(String folderUuid, DyFormData dyFormData, ActionContext actionContext) {
        String fileName = dyFormData.getDataUuid();
        Configuration configuration = actionContext.getConfiguration();
        if (configuration instanceof DmsFileManagerConfiguration) {
            String fileNameField = ((DmsFileManagerConfiguration) configuration).getFileNameField(folderUuid);
            if (StringUtils.isNotBlank(fileNameField) && dyFormData.isFieldExist(fileNameField)) {
                Object fieldValue = dyFormData.getFieldValue(fileNameField);
                if (fieldValue != null) {
                    fileName = fieldValue.toString();
                }
            }
        }
        return fileName;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.filemanager.service.FileManagerDyformActionService#saveAsNormal(com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData, com.wellsoft.pt.dms.core.context.ActionContext)
     */
    @Override
    public DmsFile saveAsNormal(FileManagerDyFormActionData dyFormActionData, ActionContext actionContext) {
        String folderUuid = dyFormActionData.getFolderUuid();
        String fileUuid = dyFormActionData.getFileUuid();
        DyFormData dyFormData = dyFormActionData.getDyFormData();
        String dataUuid = null;

        // 设置草稿状态到表单中
        setFileStatusToDyformData(folderUuid, FileStatus.NORMAL, dyFormData, actionContext);

        // 保存表单信息
        boolean isVersioning = actionContext.getConfiguration().isEnableVersioning();
        if (isVersioning) {
            dataUuid = dyFormActionService.saveVersion(actionContext, dyFormActionData);
        } else {
            dataUuid = dyFormActionService.save(dyFormData);
        }

        // 文档名称
        String fileName = getFileName(folderUuid, dyFormData, actionContext);

        // 保存或更新文件信息
        DmsFileEntity dmsFileEntity = null;
        if (StringUtils.isNotBlank(fileUuid)) {
            dmsFileEntity = dmsFileService.get(fileUuid);
        } else {
            dmsFileEntity = dmsFileService.getByFolderUuidAndDataUuid(folderUuid, dataUuid);
        }
        if (dmsFileEntity == null) {
            dmsFileEntity = new DmsFileEntity();
        }
        dmsFileEntity.setFileName(fileName);
        dmsFileEntity.setContentType(FileTypeEnum.DYFORM.getType());

        String dataDefUuid = dyFormData.getFormUuid();

        DyFormFormDefinition dyFormFormDefinition = dyFormApiFacade.getFormDefinition(dataDefUuid);
        dataDefUuid = dyFormFormDefinition.doGetPFormUuid();
        //		if ("M".equals(dyFormFormDefinition.getFormType())
        //				&& StringUtils.isNotBlank(dyFormFormDefinition.getpFormUuid())) {
        //			dataDefUuid = dyFormFormDefinition.getpFormUuid();
        //		}

        // int index = 0; // 递归计数
        // while (true) {
        // DyFormFormDefinition dyFormFormDefinition =
        // dyFormApiFacade.getFormDefinition(dataDefUuid);
        // if (index < 100 && !"P".equals(dyFormFormDefinition.getFormType())
        // && StringUtils.isNotBlank(dyFormFormDefinition.getpFormUuid())) {
        // dataDefUuid = dyFormFormDefinition.getpFormUuid();
        // index = index + 1;
        // } else {
        // break;
        // }
        // }

        dmsFileEntity.setDataDefUuid(dataDefUuid);
        dmsFileEntity.setDataUuid(dataUuid);
        dmsFileEntity.setFolderUuid(folderUuid);
        dmsFileEntity.setStatus(FileStatus.NORMAL);
        dmsFileEntity.setLibraryUuid(dmsFolderService.getOne(folderUuid).getLibraryUuid());
        dmsFileService.save(dmsFileEntity);

        // 返回文件信息
        DmsFile dmsFile = new DmsFile();
        BeanUtils.copyProperties(dmsFileEntity, dmsFile);
        dmsFile.setName(dmsFileEntity.getFileName());
        dmsFile.setFileSize(dmsFileEntity.getFileSize());
        return dmsFile;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.filemanager.service.FileManagerDyformActionService#delete(com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData)
     */
    @Override
    public void delete(FileManagerDyFormActionData dyFormActionData) {
        String fileUuid = dyFormActionData.getFileUuid();
        String folderUuid = dyFormActionData.getFolderUuid();
        String dataUuid = dyFormActionData.getDataUuid();
        if (StringUtils.isNotBlank(fileUuid)) {
            dmsFileServiceFacade.deleteFile(fileUuid);
        } else {
            dmsFileServiceFacade.deleteFileByFolderUuidAndDataUuid(folderUuid, dataUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.filemanager.service.FileManagerDyformActionService#restore(com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData)
     */
    @Override
    public void restore(FileManagerDyFormActionData dyFormActionData) {
        String fileUuid = dyFormActionData.getFileUuid();
        String folderUuid = dyFormActionData.getFolderUuid();
        String dataUuid = dyFormActionData.getDataUuid();
        if (StringUtils.isNotBlank(fileUuid)) {
            dmsFileServiceFacade.restoreFile(fileUuid);
        } else {
            dmsFileServiceFacade.restoreFileByFolderUuidAndDataUuid(folderUuid, dataUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.filemanager.service.FileManagerDyformActionService#physicalDelete(com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData)
     */
    @Override
    public void physicalDelete(FileManagerDyFormActionData dyFormActionData) {
        String fileUuid = dyFormActionData.getFileUuid();
        String folderUuid = dyFormActionData.getFolderUuid();
        String dataDefUuid = dyFormActionData.getFormUuid();
        String dataUuid = dyFormActionData.getDataUuid();
        if (StringUtils.isNotBlank(fileUuid)) {
            dmsFileServiceFacade.physicalDeleteFile(fileUuid);
        } else {
            dmsFileServiceFacade.physicalDeleteFileByFolderUuidAndDataUuid(folderUuid, dataUuid, dataDefUuid);
        }
    }

}
