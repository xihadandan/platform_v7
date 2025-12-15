/*
 * @(#)Jan 31, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.api;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dms.bean.DmsFileVo;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.executor.CancelShareFileActionExecutor.CancelShareFileActionParam;
import com.wellsoft.pt.dms.file.executor.CancelShareFileActionExecutor.CancelShareFileActionResult;
import com.wellsoft.pt.dms.file.executor.CancelShareFolderActionExecutor.CancelShareFolderActionParam;
import com.wellsoft.pt.dms.file.executor.CancelShareFolderActionExecutor.CancelShareFolderActionResult;
import com.wellsoft.pt.dms.file.executor.CopyFileActionExecutor.CopyFileActionParam;
import com.wellsoft.pt.dms.file.executor.CopyFileActionExecutor.CopyFileActionResult;
import com.wellsoft.pt.dms.file.executor.CopyFolderActionExecutor.CopyFolderActionParam;
import com.wellsoft.pt.dms.file.executor.CopyFolderActionExecutor.CopyFolderActionResult;
import com.wellsoft.pt.dms.file.executor.CreateFolderActionExecutor.CreateFolderActionParam;
import com.wellsoft.pt.dms.file.executor.CreateFolderActionExecutor.CreateFolderActionResult;
import com.wellsoft.pt.dms.file.executor.DeleteFileActionExecutor.DeleteFileActionParam;
import com.wellsoft.pt.dms.file.executor.DeleteFileActionExecutor.DeleteFileActionResult;
import com.wellsoft.pt.dms.file.executor.DeleteFolderActionExecutor.DeleteFolderActionParam;
import com.wellsoft.pt.dms.file.executor.DeleteFolderActionExecutor.DeleteFolderActionResult;
import com.wellsoft.pt.dms.file.executor.FileActionExecutorMethod;
import com.wellsoft.pt.dms.file.executor.FileActionExecutorMethodFactory;
import com.wellsoft.pt.dms.file.executor.ForceDeleteFolderActionExecutor.ForceDeleteFolderActionParam;
import com.wellsoft.pt.dms.file.executor.ForceDeleteFolderActionExecutor.ForceDeleteFolderActionResult;
import com.wellsoft.pt.dms.file.executor.MoveFileActionExecutor.MoveFileActionParam;
import com.wellsoft.pt.dms.file.executor.MoveFileActionExecutor.MoveFileActionResult;
import com.wellsoft.pt.dms.file.executor.MoveFolderActionExecutor.MoveFolderActionParam;
import com.wellsoft.pt.dms.file.executor.MoveFolderActionExecutor.MoveFolderActionResult;
import com.wellsoft.pt.dms.file.executor.PhysicalDeleteFileActionExecutor.PhysicalDeleteFileActionParam;
import com.wellsoft.pt.dms.file.executor.PhysicalDeleteFileActionExecutor.PhysicalDeleteFileActionResult;
import com.wellsoft.pt.dms.file.executor.PhysicalDeleteFolderActionExecutor.PhysicalDeleteFolderActionParam;
import com.wellsoft.pt.dms.file.executor.PhysicalDeleteFolderActionExecutor.PhysicalDeleteFolderActionResult;
import com.wellsoft.pt.dms.file.executor.ReadFileActionExecutor.ReadFileActionParam;
import com.wellsoft.pt.dms.file.executor.ReadFileActionExecutor.ReadFileActionResult;
import com.wellsoft.pt.dms.file.executor.ReadFolderActionExecutor.ReadFolderActionParam;
import com.wellsoft.pt.dms.file.executor.ReadFolderActionExecutor.ReadFolderActionResult;
import com.wellsoft.pt.dms.file.executor.RenameFileActionExecutor.RenameFileActionParam;
import com.wellsoft.pt.dms.file.executor.RenameFolderActionExecutor.RenameFolderActionParam;
import com.wellsoft.pt.dms.file.executor.RestoreFileActionExecutor.RestoreFileActionParam;
import com.wellsoft.pt.dms.file.executor.RestoreFileActionExecutor.RestoreFileActionResult;
import com.wellsoft.pt.dms.file.executor.RestoreFolderActionExecutor.RestoreFolderActionParam;
import com.wellsoft.pt.dms.file.executor.RestoreFolderActionExecutor.RestoreFolderActionResult;
import com.wellsoft.pt.dms.file.executor.ShareFileActionExecutor.ShareFileActionParam;
import com.wellsoft.pt.dms.file.executor.ShareFileActionExecutor.ShareFileActionResult;
import com.wellsoft.pt.dms.file.executor.ShareFolderActionExecutor.ShareFolderActionParam;
import com.wellsoft.pt.dms.file.executor.ShareFolderActionExecutor.ShareFolderActionResult;
import com.wellsoft.pt.dms.file.executor.ViewFileAttributesActionExecutor.ViewFileAttributesActionParam;
import com.wellsoft.pt.dms.file.executor.ViewFileAttributesActionExecutor.ViewFileAttributesActionResult;
import com.wellsoft.pt.dms.file.executor.ViewFolderAttributesActionExecutor.ViewFolderAttributesActionParam;
import com.wellsoft.pt.dms.file.executor.ViewFolderAttributesActionExecutor.ViewFolderAttributesActionResult;
import com.wellsoft.pt.dms.file.query.api.*;
import com.wellsoft.pt.dms.file.service.DmsFileActionService;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dms.model.DmsFileAction;
import com.wellsoft.pt.dms.model.DmsFileAttributes;
import com.wellsoft.pt.dms.model.DmsFolder;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dms.service.DmsFolderService;
import com.wellsoft.pt.dms.support.FileMediaType;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.service.MultiOrgUserAccountService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 31, 2018.1	zhulh		Jan 31, 2018		Create
 * </pre>
 * @date Jan 31, 2018
 */
@Component
public class DmsFileServiceFacade extends AbstractApiFacade {

    @Autowired
    private DmsFolderService dmsFolderService;

    @Autowired
    private DmsFileService dmsFileService;

    @Autowired
    private DmsFileActionService dmsFileActionService;

    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;

    /**
     * 创建文件查询
     *
     * @return
     */
    public synchronized DmsFileQuery createFileQuery() {
        return ApplicationContextHolder.getBean(DmsFileQuery.class);
    }

    /**
     * 创建文件查询
     *
     * @return
     */
    public DmsFileRecycleBinQuery createFileRecycleBinQuery() {
        return ApplicationContextHolder.getBean(DmsFileRecycleBinQuery.class);
    }

    /**
     * 创建最近访问文件查询
     *
     * @return
     */
    public DmsFileRecentVisitQuery createFileRecentVisitQuery() {
        return ApplicationContextHolder.getBean(DmsFileRecentVisitQuery.class);
    }

    /**
     * 创建我的标签文件查询
     *
     * @return
     */
    public DmsFileMyTagDataQuery createFileMyTagDataQuery() {
        return ApplicationContextHolder.getBean(DmsFileMyTagDataQuery.class);
    }

    /**
     * 创建我的分享文件查询
     *
     * @return
     */
    public DmsFileMyShareInfoQuery createFileMyShareInfoQuery() {
        return ApplicationContextHolder.getBean(DmsFileMyShareInfoQuery.class);
    }

    /**
     * 创建与我分享文件查询
     *
     * @return
     */
    public DmsFileShareWithMeQuery createFileShareWithMeQuery() {
        return ApplicationContextHolder.getBean(DmsFileShareWithMeQuery.class);
    }

    /**
     * 表单数据归档
     *
     * @param dyFormData
     * @param folderUuid
     * @param readerIds
     * @return
     */
    public String archive(DyFormData dyFormData, String folderUuid, List<String> readerIds) {
        return dmsFileService.archive(dyFormData, folderUuid, readerIds);
    }

    /**
     * 表单数据归档
     *
     * @param fileName
     * @param dyFormData
     * @param folderUuid
     * @param readerIds
     * @return
     */
    public String archive(String fileName, DyFormData dyFormData, String folderUuid, List<String> readerIds) {
        return dmsFileService.archive(fileName, dyFormData, folderUuid, readerIds);
    }

    /**
     * 表单数据归档
     *
     * @param fileName
     * @param contentType
     * @param dyFormData
     * @param folderUuid
     * @return
     */
    public String archive(String fileName, String contentType, DyFormData dyFormData, String folderUuid, List<String> readerIds) {
        return dmsFileService.archive(fileName, contentType, dyFormData, folderUuid, readerIds);
    }

    /**
     * 文件数据归档
     *
     * @param dmsFile
     * @param readerIds
     * @return
     */
    public String archive(DmsFile dmsFile, List<String> readerIds) {
        return dmsFileService.archive(dmsFile, readerIds);
    }

    /**
     * 上传mongo文件
     *
     * @param folderUuid
     * @param file
     * @return
     */
    public String uploadFile(String folderUuid, MongoFileEntity file) {
        return dmsFileActionService.uploadFile(folderUuid, file);
    }

    /**
     * 根据文件UUID获取文件
     *
     * @param fileUuid
     * @return
     */
    public DmsFile getFile(String fileUuid) {
        ReadFileActionParam readFileActionParam = new ReadFileActionParam();
        readFileActionParam.setFileUuid(fileUuid);
        ReadFileActionResult readFileActionResult = FileActionExecutorMethodFactory.getFileActionExecutorMethod(
                FileActions.READ_FILE).execute(readFileActionParam);
        return readFileActionResult.getData();
    }

    /**
     * 根据文件UUID获取文件
     *
     * @param fileUuid
     * @return
     */
    public DmsFile getFileWithoutPermission(String fileUuid) {
        DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
        if (dmsFileEntity == null) {
            return null;
        }
        DmsFile dmsFile = new DmsFile();
        BeanUtils.copyProperties(dmsFileEntity, dmsFile);
        dmsFile.setName(dmsFileEntity.getFileName());
        return dmsFile;
    }

    /**
     * @param folderUuid
     * @param dataUuid
     * @return
     */
    public DmsFile getFileWithoutPermission(String folderUuid, String dataUuid) {
        DmsFileEntity dmsFileEntity = dmsFileService.getByFolderUuidAndDataUuid(folderUuid, dataUuid);
        if (dmsFileEntity == null) {
            return null;
        }
        DmsFile dmsFile = new DmsFile();
        BeanUtils.copyProperties(dmsFileEntity, dmsFile);
        dmsFile.setName(dmsFileEntity.getFileName());
        return dmsFile;
    }

    /**
     * 根据夹UUID获取夹
     *
     * @param folderUuid
     * @return
     */
    public DmsFolder getFolder(String folderUuid) {
        ReadFolderActionParam readFolderActionParam = new ReadFolderActionParam();
        readFolderActionParam.setFolderUuid(folderUuid);
        ReadFolderActionResult readFolderActionResult = FileActionExecutorMethodFactory.getFileActionExecutorMethod(
                FileActions.READ_FOLDER).execute(readFolderActionParam);
        return readFolderActionResult.getData();
    }

    /**
     * 根据夹UUID获取夹，不需要权限
     *
     * @param folderUuid
     * @return
     */
    public DmsFolder getFolderWithoutPermission(String folderUuid) {
        DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
        if (dmsFolderEntity == null) {
            return null;
        }
        DmsFolder dmsFolder = new DmsFolder();
        BeanUtils.copyProperties(dmsFolderEntity, dmsFolder);
        return dmsFolder;
    }

    /**
     * @param folderUuid
     * @return
     */
    public DmsFileAttributes getFolderAttributes(String folderUuid) {
        ViewFolderAttributesActionParam viewFolderAttributesActionParam = new ViewFolderAttributesActionParam();
        viewFolderAttributesActionParam.setFolderUuid(folderUuid);
        ViewFolderAttributesActionResult viewFolderAttributesActionResult = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.VIEW_FOLDER_ATTRIBUTES).execute(
                        viewFolderAttributesActionParam);
        return viewFolderAttributesActionResult.getData();
    }

    /**
     * @param fileUuid
     * @return
     */
    public DmsFileAttributes getFileAttributes(String fileUuid) {
        ViewFileAttributesActionParam viewFileAttributesActionParam = new ViewFileAttributesActionParam();
        viewFileAttributesActionParam.setFileUuid(fileUuid);
        ViewFileAttributesActionResult viewFileAttributesActionResult = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.VIEW_FILE_ATTRIBUTES).execute(viewFileAttributesActionParam);
        return viewFileAttributesActionResult.getData();
    }

    /**
     * @param folderUuid
     * @param folderName
     * @return
     */
    public String createFolder(String folderUuid, String folderName) {
        return createFolder(folderUuid, folderName, null);
    }

    /**
     * @param folderUuid
     * @param folderName
     * @param parentFolderUuid
     * @return
     */
    public String createFolder(String folderUuid, String folderName, String parentFolderUuid) {
        CreateFolderActionParam createFolderActionParam = new CreateFolderActionParam();
        DmsFolder dmsFolder = new DmsFolder();
        dmsFolder.setUuid(folderUuid);
        dmsFolder.setName(folderName);
        dmsFolder.setContentType(FileMediaType.APPLICATION_FOLDER);
        createFolderActionParam.setFolderUuid(parentFolderUuid);
        createFolderActionParam.setData(dmsFolder);
        CreateFolderActionResult createFolderActionResult = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.CREATE_FOLDER).execute(createFolderActionParam);
        return createFolderActionResult.getFolderUuid();
    }

    /**
     * @param childFolderPath
     * @param parentFolderUuid
     * @return
     */
    public String createFolderIfNotExists(String childFolderPath, String parentFolderUuid) {
        return dmsFileActionService.createFolderIfNotExists(childFolderPath, parentFolderUuid);
    }

    /**
     * 添加用户私有的管理员权限
     *
     * @param folderUuid
     */
    public void addUserPrivateAdminPermission(String folderUuid) {
        dmsFolderService.addUserPrivateAdminPermission(folderUuid);
    }

    /**
     * 重命名夹
     *
     * @param folderUuid
     * @param newFolderName
     */
    public void renameFolder(String folderUuid, String newFolderName) {
        RenameFolderActionParam renameFolderActionParam = new RenameFolderActionParam();
        renameFolderActionParam.setFolderUuid(folderUuid);
        renameFolderActionParam.setNewFolderName(newFolderName);
        FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.RENAME_FOLDER);
        executorMethod.execute(renameFolderActionParam);
    }

    public void renameFolderWithoutPermission(String folderUuid, String newFolderName) {
        dmsFileActionService.renameFolderWithoutPermission(folderUuid, newFolderName);
    }

    /**
     * 重命名文件
     *
     * @param fileUuid
     * @param newFileName
     */
    public void renameFile(String fileUuid, String newFileName) {
        RenameFileActionParam renameFileActionParam = new RenameFileActionParam();
        renameFileActionParam.setFileUuid(fileUuid);
        renameFileActionParam.setNewFileName(newFileName);
        FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.RENAME_FILE);
        executorMethod.execute(renameFileActionParam);
    }

    /**
     * 分享夹
     *
     * @param folderUuid
     * @param shareOrgId
     * @param shareOrgName
     */
    public String shareFolder(String folderUuid, String shareOrgId, String shareOrgName) {
        ShareFolderActionParam shareFolderActionParam = new ShareFolderActionParam();
        shareFolderActionParam.setFolderUuid(folderUuid);
        shareFolderActionParam.setShareOrgId(shareOrgId);
        shareFolderActionParam.setShareOrgName(shareOrgName);
        FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.SHARE_FOLDER);
        ShareFolderActionResult shareFolderActionResult = executorMethod.execute(shareFolderActionParam);
        return shareFolderActionResult.getShareUuid();
    }

    /**
     * 分享文件
     *
     * @param fileUuid
     * @param shareOrgId
     * @param shareOrgName
     */
    public String shareFile(String fileUuid, String shareOrgId, String shareOrgName) {
        ShareFileActionParam shareFileActionParam = new ShareFileActionParam();
        shareFileActionParam.setFileUuid(fileUuid);
        shareFileActionParam.setShareOrgId(shareOrgId);
        shareFileActionParam.setShareOrgName(shareOrgName);
        FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.SHARE_FILE);
        ShareFileActionResult shareFileActionResult = executorMethod.execute(shareFileActionParam);
        return shareFileActionResult.getShareUuid();
    }

    /**
     * 取消夹分享
     *
     * @param folderUuid
     * @param shareUuid
     */
    public void cancelShareFolder(String folderUuid, String shareUuid) {
        CancelShareFolderActionParam cancelShareFolderActionParam = new CancelShareFolderActionParam();
        cancelShareFolderActionParam.setFolderUuid(folderUuid);
        cancelShareFolderActionParam.setShareUuid(shareUuid);
        CancelShareFolderActionResult cancelShareFolderActionResult = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.CANCEL_SHARE_FOLDER).execute(cancelShareFolderActionParam);
    }

    /**
     * 取消文件分享
     *
     * @param fileUuid
     * @param shareUuid
     */
    public void cancelShareFile(String fileUuid, String shareUuid) {
        CancelShareFileActionParam cancelShareFileActionParam = new CancelShareFileActionParam();
        cancelShareFileActionParam.setFileUuid(fileUuid);
        cancelShareFileActionParam.setShareUuid(shareUuid);
        CancelShareFileActionResult cancelShareFileActionResult = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.CANCEL_SHARE_FILE).execute(cancelShareFileActionParam);
    }

    /**
     * 移动夹
     *
     * @param sourceFolderUuid
     * @param destFolderUuid
     */
    public void moveFolder(String sourceFolderUuid, String destFolderUuid) {
        MoveFolderActionParam moveFolderActionParam = new MoveFolderActionParam();
        moveFolderActionParam.setSourceFolderUuid(sourceFolderUuid);
        moveFolderActionParam.setDestFolderUuid(destFolderUuid);
        FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.MOVE_FOLDER);
        MoveFolderActionResult moveFolderActionResult = executorMethod.execute(moveFolderActionParam);
    }

    /**
     * 移动文件
     *
     * @param sourceFileUuid
     * @param destFolderUuid
     */
    public void moveFile(String sourceFileUuid, String destFolderUuid) {
        MoveFileActionParam moveFileActionParam = new MoveFileActionParam();
        moveFileActionParam.setSourceFileUuid(sourceFileUuid);
        moveFileActionParam.setDestFolderUuid(destFolderUuid);
        FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.MOVE_FILE);
        MoveFileActionResult moveFolderActionResult = executorMethod.execute(moveFileActionParam);
    }

    /**
     * 复制夹
     *
     * @param sourceFolderUuid
     * @param destFolderUuid
     * @return
     */
    public String copyFolder(String sourceFolderUuid, String destFolderUuid) {
        CopyFolderActionParam copyFolderActionParam = new CopyFolderActionParam();
        copyFolderActionParam.setSourceFolderUuid(sourceFolderUuid);
        copyFolderActionParam.setDestFolderUuid(destFolderUuid);
        FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.COPY_FOLDER);
        CopyFolderActionResult copyFolderActionResult = executorMethod.execute(copyFolderActionParam);
        return copyFolderActionResult.getFolderUuid();
    }

    /**
     * 复制文件
     *
     * @param sourceFileUuid
     * @param destFolderUuid
     * @return
     */
    public String copyFile(String sourceFileUuid, String destFolderUuid) {
        CopyFileActionParam copyFileActionParam = new CopyFileActionParam();
        copyFileActionParam.setSourceFileUuid(sourceFileUuid);
        copyFileActionParam.setDestFolderUuid(destFolderUuid);
        FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.COPY_FILE);
        CopyFileActionResult copyFileActionResult = executorMethod.execute(copyFileActionParam);
        return copyFileActionResult.getFileUuid();
    }

    /**
     * 删除夹
     *
     * @param folderUuid
     */
    public void deleteFolder(String folderUuid) {
        List<DmsFileAction> dmsFileActions = dmsFileActionService.getFolderActions(folderUuid);
        // 强制删除
        if (hasActionPermission(FileActions.FORCE_DELETE_FOLDER, dmsFileActions)) {
            FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                    .getFileActionExecutorMethod(FileActions.FORCE_DELETE_FOLDER);
            ForceDeleteFolderActionParam forceDeleteFolderActionParam = new ForceDeleteFolderActionParam();
            forceDeleteFolderActionParam.setFolderUuid(folderUuid);
            ForceDeleteFolderActionResult forceDeleteFolderActionResult = executorMethod
                    .execute(forceDeleteFolderActionParam);
        } else if (hasActionPermission(FileActions.DELETE_FOLDER, dmsFileActions)) {
            // 删除空夹
            FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                    .getFileActionExecutorMethod(FileActions.DELETE_FOLDER);
            DeleteFolderActionParam deleteFolderActionParam = new DeleteFolderActionParam();
            deleteFolderActionParam.setFolderUuid(folderUuid);
            DeleteFolderActionResult deleteFolderActionResult = executorMethod.execute(deleteFolderActionParam);
        } else {
            throw new RuntimeException("无权限删除文件夹！");
        }
    }

    /**
     * 删除夹
     *
     * @param folderUuid
     */
    public void forceDeleteFolder(String folderUuid) {
        dmsFileActionService.forceDeleteFolder(folderUuid);
    }

    /**
     * @param dmsFileActions
     * @param dmsFileActions
     * @return
     */
    private boolean hasActionPermission(String fileAction, List<DmsFileAction> dmsFileActions) {
        for (DmsFileAction dmsFileAction : dmsFileActions) {
            if (StringUtils.equals(fileAction, dmsFileAction.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param fileUuid
     */
    public void deleteFile(String fileUuid) {
        DeleteFileActionParam deleteFileActionParam = new DeleteFileActionParam();
        deleteFileActionParam.setFileUuid(fileUuid);
        FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.DELETE_FILE);
        DeleteFileActionResult deleteFileActionResult = executorMethod.execute(deleteFileActionParam);
    }

    /**
     * 根据数据UUID，删除文件
     *
     * @param dataUuid
     */
    public void deleteFileByFolderUuidAndDataUuid(String folderUuid, String dataUuid) {
        DeleteFileActionParam deleteFileActionParam = new DeleteFileActionParam();
        deleteFileActionParam.setFolderUuid(folderUuid);
        deleteFileActionParam.setDataUuid(dataUuid);
        FileActionExecutorMethod executorMethod = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.DELETE_FILE);
        DeleteFileActionResult deleteFileActionResult = executorMethod.execute(deleteFileActionParam);
    }

    /**
     * 恢复夹
     *
     * @param folderUuid
     */
    public void restoreFolder(String folderUuid) {
        RestoreFolderActionParam restoreFolderActionParam = new RestoreFolderActionParam();
        restoreFolderActionParam.setFolderUuid(folderUuid);
        RestoreFolderActionResult restoreFolderActionResult = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.RESTORE_FOLDER).execute(restoreFolderActionParam);
    }

    /**
     * 恢复文件
     *
     * @param fileUuid
     */
    public void restoreFile(String fileUuid) {
        RestoreFileActionParam restoreFileActionParam = new RestoreFileActionParam();
        restoreFileActionParam.setFileUuid(fileUuid);
        RestoreFileActionResult restoreFileActionResult = FileActionExecutorMethodFactory.getFileActionExecutorMethod(
                FileActions.RESTORE_FILE).execute(restoreFileActionParam);
    }

    /**
     * 根据数据UUID，恢复文件
     *
     * @param dataUuid
     */
    public void restoreFileByFolderUuidAndDataUuid(String folderUuid, String dataUuid) {
        RestoreFileActionParam restoreFileActionParam = new RestoreFileActionParam();
        restoreFileActionParam.setFolderUuid(folderUuid);
        restoreFileActionParam.setDataUuid(dataUuid);
        RestoreFileActionResult restoreFileActionResult = FileActionExecutorMethodFactory.getFileActionExecutorMethod(
                FileActions.RESTORE_FILE).execute(restoreFileActionParam);
    }

    /**
     * 物理删除夹
     *
     * @param folderUuid
     */
    public void physicalDeleteFolder(String folderUuid) {
        PhysicalDeleteFolderActionParam physicalDeleteFolderActionParam = new PhysicalDeleteFolderActionParam();
        physicalDeleteFolderActionParam.setFolderUuid(folderUuid);
        PhysicalDeleteFolderActionResult physicalDeleteFolderActionResult = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.PHYSICAL_DELETE_FOLDER).execute(
                        physicalDeleteFolderActionParam);
    }

    /**
     * 物理删除文件
     *
     * @param fileUuid
     */
    public void physicalDeleteFile(String fileUuid) {
        PhysicalDeleteFileActionParam physicalDeleteFileActionParam = new PhysicalDeleteFileActionParam();
        physicalDeleteFileActionParam.setFileUuid(fileUuid);
        PhysicalDeleteFileActionResult physicalDeleteFileActionResult = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.PHYSICAL_DELETE_FILE).execute(physicalDeleteFileActionParam);
    }

    /**
     * 物理删除文件，不带权限
     *
     * @param fileUuid
     */
    public void physicalDeleteFileWithoutPermission(String fileUuid) {
        dmsFileService.remove(fileUuid);
    }

    /**
     * @param dataUuid
     */
    public void physicalDeleteFileByFolderUuidAndDataUuid(String folderUuid, String dataUuid, String dataDefUuid) {
        PhysicalDeleteFileActionParam physicalDeleteFileActionParam = new PhysicalDeleteFileActionParam();
        physicalDeleteFileActionParam.setFolderUuid(folderUuid);
        physicalDeleteFileActionParam.setDataUuid(dataUuid);
        physicalDeleteFileActionParam.setDataDefUuid(dataDefUuid);
        PhysicalDeleteFileActionResult physicalDeleteFileActionResult = FileActionExecutorMethodFactory
                .getFileActionExecutorMethod(FileActions.PHYSICAL_DELETE_FILE).execute(physicalDeleteFileActionParam);
    }

    /**
     * 判断指定的用户ID对夹是否有指定的角色ID
     *
     * @param userId
     * @param folderUuid
     * @param dmsRoleId
     * @return
     */
    public boolean hasRole(String userId, String folderUuid, String dmsRoleId) {
        return dmsFileActionService.hasFolderRole(userId, folderUuid, dmsRoleId);
    }

    /**
     * 判断当前用户对夹是否有指定的操作权限
     *
     * @param folderUuid
     * @param actions
     * @return
     */
    public boolean hasFolderPermission(String folderUuid, String... actions) {
        return dmsFileActionService.hasFolderPermission(folderUuid, actions);
    }

    /**
     * 判断当前用户对文件是否有指定的操作权限
     *
     * @param fileUuid
     * @param actions
     * @return
     */
    public boolean hasFilePermission(String fileUuid, String... actions) {
        return dmsFileActionService.hasFilePermission(fileUuid, actions);
    }

    /**
     * 根据权限过滤文件
     *
     * @param fileUuids
     * @param actions
     * @return
     */
    public List<String> filterFileWithPermission(Collection<String> fileUuids, String... actions) {
        return dmsFileActionService.filterFileWithPermission(fileUuids, actions);
    }

    /**
     * 检查同一目录下文件名是否重复
     *
     * @param folderName
     * @param parentFolderUuid
     * @return
     */
    public boolean checkTheSameNameForCreateFolder(final String folderName, String parentFolderUuid) {
        return dmsFolderService.existsFolderNameByParentFolderUuid(folderName, parentFolderUuid);
    }

    /**
     * @return
     */
    public List<DmsFolder> listRootFolder() {
        return dmsFileActionService.listRootFolder();
    }

    /**
     * @param folderUuid
     * @return
     */
    public List<DmsFolder> listFolder(String folderUuid) {
        return dmsFileActionService.listFolder(folderUuid);
    }

    /**
     * @param folderUuid
     * @return
     */
    public List<DmsFolder> listFolderWithoutPermission(String folderUuid) {
        return dmsFileActionService.listFolderWithoutPermission(folderUuid);
    }

    /**
     * @param folderUuid
     * @return
     */
    public List<DmsFolder> listDeletedFolderWithoutPermission(String folderUuid) {
        return dmsFileActionService.listDeletedFolderWithoutPermission(folderUuid);
    }

    /**
     * @param folderUuid
     * @return
     */
    public List<DmsFolder> listAllFolder(String folderUuid) {
        return dmsFileActionService.listAllFolder(folderUuid);
    }

    /**
     * @param folderUuid
     * @return
     */
    public List<DmsFile> listFilesWithoutPermission(String folderUuid) {
        return dmsFileActionService.listFilesWithoutPermission(folderUuid);
    }

    /**
     * @param folderUuid
     * @param queryParams
     * @return
     */
    public List<String> listReadFolderUuid(String folderUuid, Map<String, Object> queryParams) {
        return dmsFileActionService.listReadFolderUuid(folderUuid, queryParams);
    }

    /**
     * @param folderUuid
     * @param queryParams
     * @return
     */
    public List<String> listReadAllFolderUuid(String folderUuid, Map<String, Object> queryParams) {
        return dmsFileActionService.listReadAllFolderUuid(folderUuid, queryParams);
    }

    /**
     * @param folderUuid
     * @param queryParams
     * @return
     */
    public List<String> listReadAllFileFolderUuid(String folderUuid, Map<String, Object> queryParams) {
        return dmsFileActionService.listReadAllFileFolderUuid(folderUuid, queryParams);
    }

    /**
     * @param folderUuid
     * @param queryParams
     * @return
     */
    public Map<String, List<String>> listReadAllFolderAndFileFolderUuid(String folderUuid, Map<String, Object> queryParams) {
        return dmsFileActionService.listReadAllFolderAndFileFolderUuid(folderUuid, queryParams);
    }

    /**
     * 根据 归档文件实体 查询符合条件的uuid
     *
     * @param fileEntity
     * @return
     */
    public List<String> queryDmsFileUuids(DmsFileEntity fileEntity) {
        List<DmsFileEntity> fileList = dmsFileService.list(fileEntity);
        List<String> uuids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fileList)) {
            for (DmsFileEntity dmsFileEntity : fileList) {
                uuids.add(dmsFileEntity.getUuid());
            }
        }
        return uuids;
    }

    /**
     * 根据 uuid集合更新创建人
     *
     * @param dmsFileVo
     */
    public int updateDmsFileCreator(DmsFileVo dmsFileVo) {

        return dmsFileService.updateDmsFileCreator(dmsFileVo);
    }

    /**
     * 更新夹编号
     *
     * @param folderCodes
     */
    public void updateFolderCodes(Map<String, String> folderCodes) {
        if (MapUtils.isEmpty(folderCodes)) {
            return;
        }
        this.dmsFolderService.updateFolderCodes(folderCodes);
    }

    /**
     * 根据文件UUID，添加文件阅读者
     *
     * @param fileUuid
     * @param readerIds
     * @return
     */
    public boolean addFileReader(String fileUuid, List<String> readerIds) {
        return dmsFileService.addFileReader(fileUuid, readerIds);
    }

    /**
     * 根据文件数据UUID，添加文件阅读者
     *
     * @param dataUuid
     * @param readerIds
     * @return
     */
    public boolean addFileReaderByDataUuid(String dataUuid, List<String> readerIds) {
        return dmsFileService.addFileReaderByDataUuid(dataUuid, readerIds);
    }

    /**
     * 根据文件UUID，删除文件阅读者
     *
     * @param fileUuid
     * @param readerIds
     * @return
     */
    public boolean deleteFileReader(String fileUuid, List<String> readerIds) {
        return dmsFileService.deleteFileReader(fileUuid, readerIds);
    }

    /**
     * 根据文件数据UUID，删除文件阅读者
     *
     * @param dataUuid
     * @param readerIds
     * @return
     */
    public boolean deleteFileReaderByDataUuid(String dataUuid, List<String> readerIds) {
        return dmsFileService.deleteFileReaderByDataUuid(dataUuid, readerIds);
    }

    public List<String> getFileReaders(String fileUuid) {
        return dmsFileService.getFileReaders(fileUuid);
    }
}
