/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.model.*;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;

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
 * Jan 3, 2018.1	zhulh		Jan 3, 2018		Create
 * </pre>
 * @date Jan 3, 2018
 */
public interface DmsFileActionService extends BaseService {

    /**
     * 创建夹
     *
     * @param childFolderName
     * @param parentFolderUuid
     * @return
     */
    String createFolder(String childFolderName, String parentFolderUuid);

    /**
     * 创建夹
     *
     * @param childFolderUuid
     * @param childFolderName
     * @param parentFolderUuid
     * @return
     */
    String createFolder(String childFolderUuid, String childFolderName, String parentFolderUuid);

    /**
     * 创建夹，返回创建的夹UUID
     *
     * @param childFolderPath
     * @param parentFolderUuid
     * @return
     */
    String createFolderIfNotExists(String childFolderPath, String parentFolderUuid);

    /**
     * 读取夹
     *
     * @param folderUuid
     * @return
     */
    DmsFolder readFolder(String folderUuid);

    DmsFolder readFolderWithoutPermission(String folderUuid);

    /**
     * 夹权限判断
     *
     * @param folderUuid
     * @param action
     * @return
     */
    boolean hasFolderPermission(String folderUuid, String... actions);

    /**
     * 判断指定的用户ID对夹是否有指定的角色ID
     *
     * @param userId
     * @param folderUuid
     * @param dmsRoleId
     * @return
     */
    boolean hasFolderRole(String userId, String folderUuid, String... roleIds);

    /**
     * 查看夹属性
     *
     * @param folderUuid
     * @return
     */
    DmsFolderAttributes getFolderAttributes(String folderUuid);

    /**
     * 文件权限判断
     *
     * @param fileUuid
     * @param actions
     * @return
     */
    boolean hasFilePermission(String fileUuid, String... actions);

    /**
     * 文件权限过滤
     *
     * @param fileUuids
     * @param actions
     * @return
     */
    List<String> filterFileWithPermission(Collection<String> fileUuids, String... actions);

    /**
     * 列出根夹
     *
     * @return
     */
    List<DmsFolder> listRootFolder();

    /**
     * 列出当前夹下的子夹
     *
     * @param folderUuid
     * @return
     */
    List<DmsFolder> listFolder(String folderUuid);

    List<DmsFolder> listFolderWithoutPermission(String folderUuid);

    List<DmsFolder> listDeletedFolderWithoutPermission(String folderUuid);

    /**
     * 列出当前夹下的子夹
     *
     * @param folderUuid
     * @param listNearestIfNotFound
     * @return
     */
    List<DmsFolder> listFolder(String folderUuid, boolean listNearestIfNotFound);

    /**
     * 列出当前夹下的所有子夹(包含子夹)
     *
     * @param folderUuid
     * @return
     */
    List<DmsFolder> listAllFolder(String folderUuid);

    /**
     * 列出当前夹下的子夹数量
     *
     * @param folderUuid
     * @return
     */
    long listFolderCount(String folderUuid);

    /**
     * 列出当前夹下的文件
     *
     * @param folderUuid
     * @return
     */
    List<DmsFile> listFiles(String folderUuid);

    /**
     * 列出当前夹下的文件
     *
     * @param folderUuid
     * @return
     */
    List<DmsFile> listFilesWithoutPermission(String folderUuid);

    /**
     * 列出当前夹下的文件(包含子夹)
     *
     * @param folderUuid
     * @return
     */
    List<DmsFile> listAllFiles(String folderUuid);

    /**
     * 列出当前夹下的子夹及文件
     *
     * @param folderUuid
     * @return
     */
    List<DmsFile> listFolderAndFiles(String folderUuid);

    /**
     * 列出所有夹、文件(包含子夹)
     *
     * @param folderUuid
     * @return
     */
    List<DmsFile> listAllFolderAndFiles(String folderUuid);

    /**
     * 分享夹
     *
     * @param folderUuid
     * @param shareOrgId
     * @param shareOrgName
     * @return
     */
    String shareFolder(String folderUuid, String shareOrgId, String shareOrgName);

    /**
     * 取消分享夹
     *
     * @param folderUuid
     * @param shareUuid
     */
    void cancelShareFolder(String folderUuid, String shareUuid);

    /**
     * 重命名夹
     *
     * @param folderUuid
     * @param newFolderName
     */
    void renameFolder(String folderUuid, String newFolderName);

    /**
     * 重命名夹
     *
     * @param folderUuid
     * @param newFolderName
     */
    void renameFolderWithoutPermission(String folderUuid, String newFolderName);

    /**
     * 移动夹
     *
     * @param sourceFolderUuid
     * @param destFolderUuid
     */
    void moveFolder(String sourceFolderUuid, String destFolderUuid);

    /**
     * 复制夹
     *
     * @param sourceFolderUuid
     * @param destFolderUuid
     * @return
     */
    String copyFolder(String sourceFolderUuid, String destFolderUuid);

    /**
     * 删除夹(有子夹、文件不可删除)
     *
     * @param folderUuid
     */
    void deleteFolder(String folderUuid);

    /**
     * 删除夹(有子夹、文件不可删除)
     *
     * @param folderUuids
     */
    void deleteFolder(List<String> folderUuids);

    /**
     * 删除夹(有子夹、文件一起删除)
     *
     * @param folderUuid
     */
    void forceDeleteFolder(String folderUuid);

    /**
     * 恢复夹
     *
     * @param folderUuid
     */
    void restoreFolder(String folderUuid);

    /**
     * 彻底删除夹(有子夹、文件不可删除)
     *
     * @param folderUuid
     */
    void physicalDeleteFolder(String folderUuid);

    /**
     * 彻底删除夹(有子夹、文件不可删除)
     *
     * @param folderUuids
     */
    void physicalDeleteFolder(List<String> folderUuids);

    /**
     * 新建文件
     *
     * @param parentFolderUuid
     * @param data
     * @return
     */
    String createFile(String parentFolderUuid, DmsFile data);

    /**
     * 上传文件
     *
     * @param folderUuid
     * @param file
     */
    String uploadFile(String folderUuid, MongoFileEntity file);

    /**
     * 上传新文件
     *
     * @param folderUuid
     * @param fileUuid
     * @param file
     */
    String uploadFileAsNewVersion(String folderUuid, String fileUuid, String remark, MongoFileEntity file);

    String uploadFileAsNewVersionWithoutPermission(String folderUuid, String fileUuid, String remark, MongoFileEntity file);

    /**
     * 读取文件
     *
     * @param fileUuid
     * @return
     */
    DmsFile readFile(String fileUuid);

    /**
     * 读取文件(不判断权限)
     *
     * @param fileUuid
     * @return
     */
    DmsFile readFileWithoutPermission(String fileUuid);

    /**
     * 查看文件属性
     *
     * @param fileUuid
     * @return
     */
    DmsFileAttributes getFileAttributes(String fileUuid);

    /**
     * 分享文件
     *
     * @param fileUuid
     * @param shareOrgId
     * @param shareOrgName
     * @return
     */
    String shareFile(String fileUuid, String shareOrgId, String shareOrgName);

    /**
     * 取消分享文件
     *
     * @param fileUuid
     * @param shareUuid
     * @return
     */
    void cancelShareFile(String fileUuid, String shareUuid);

    /**
     * 收藏文件
     *
     * @param fileUuid
     */
    void attentionFile(String fileUuid);

    /**
     * 重命名文件
     *
     * @param fileUuid
     * @param newFileName
     */
    void renameFile(String fileUuid, String newFileName);

    /**
     * 签出文件
     *
     * @param fileUuid
     */
    void checkOutFile(String fileUuid);

    /**
     * 签入文件
     *
     * @param fileUuid
     */
    void checkInFile(String fileUuid);

    /**
     * 移动文件
     *
     * @param sourceFileUuid
     * @param destFolderUuid
     */
    void moveFile(String sourceFileUuid, String destFolderUuid);

    /**
     * 复制文件
     *
     * @param sourceFileUuid
     * @param destFolderUuid
     */
    String copyFile(String sourceFileUuid, String destFolderUuid);

    /**
     * 删除文件
     *
     * @param fileUuid
     */
    void deleteFile(String fileUuid);

    /**
     * 根据夹UUID、数据UUID，删除文件
     *
     * @param folderUuid
     * @param dataUuid
     */
    void deleteFileByFolderUuidAndDataUuid(String folderUuid, String dataUuid);

    /**
     * 恢复文件
     *
     * @param fileUuid
     */
    void restoreFile(String fileUuid);

    /**
     * 根据夹UUID、数据UUID恢复文件
     *
     * @param folderUuid
     * @param dataUuid
     */
    void restoreFileByFolderUuidAndDataUuid(String folderUuid, String dataUuid);

    /**
     * 彻底删除文件
     *
     * @param fileUuid
     */
    void physicalDeleteFile(String fileUuid);

    /**
     * 根据夹UUID、数据UUID，彻底删除文件
     *
     * @param folderUuid
     * @param dataUuid
     * @param dataDefUuid
     */
    void physicalDeleteFileByFolderUuidAndDataUuid(String folderUuid, String dataUuid, String dataDefUuid);

    /**
     * 获取当前用户对指定夹的操作
     *
     * @param folderUuid
     * @return
     */
    List<DmsFileAction> getFolderActions(String folderUuid);

    /**
     * 获取当前用户对指定文件的操作
     *
     * @param dmsFile
     * @return
     */
    List<DmsFileAction> getFileActions(String fileUuid);

    /**
     * 获取可访问当前夹下的夹UUID列表参数
     *
     * @param folderUuid
     * @param queryParams
     * @return
     */
    List<String> listReadFolderUuid(String folderUuid, Map<String, Object> queryParams);

    /**
     * 获取可读取所有夹UUID，包含所有子夹
     *
     * @param folderUuid
     * @param queryParams
     * @return
     */
    List<String> listReadAllFolderUuid(String folderUuid, Map<String, Object> queryParams);

    /**
     * 获取可读取夹文件的夹UUID，包含所有子夹
     *
     * @param folderUuid
     * @param queryParams
     * @return
     */
    List<String> listReadAllFileFolderUuid(String folderUuid, Map<String, Object> queryParams);


    /**
     * 获取可读取所有夹、文件的夹UUID，包含所有子夹
     *
     * @param folderUuid
     * @param queryParams
     * @return Map<folder / file, < 夹UUID列表>>
     */
    Map<String, List<String>> listReadAllFolderAndFileFolderUuid(String folderUuid, Map<String, Object> queryParams);

}
