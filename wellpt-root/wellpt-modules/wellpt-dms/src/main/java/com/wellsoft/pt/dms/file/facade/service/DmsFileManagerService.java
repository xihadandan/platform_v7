/*
 * @(#)Jan 19, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.basicdata.selective.support.DataItem;
import com.wellsoft.pt.dms.bean.DmsRoleBean;
import com.wellsoft.pt.dms.file.view.FileTypeInfos;
import com.wellsoft.pt.dms.file.view.ViewFileInfo;
import com.wellsoft.pt.dms.model.*;

import java.util.Collection;
import java.util.List;

/**
 * Description: 文件管理服务类
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
public interface DmsFileManagerService extends BaseService {

    /**
     * 判断是否配置夹文档
     *
     * @param folderUuid
     * @return
     */
    boolean isConfigDocumentForCreate(String folderUuid);

    /**
     * 获得配置文件夹
     *
     * @param folderUuid
     * @return
     */
    public DmsFolderDyformDefinition getFolderDyformDefinitionByFolderUuid(String folderUuid);

    /**
     * 根据夹UUID获取夹及子夹配置的表单定义信息，如果找不到，取上级夹
     *
     * @param folderUuid
     * @return
     */
    List<DmsFolderDyformDefinition> getAllFolderDyformDefinitionByFolderUuid(String folderUuid);

    /**
     * 判断是否为有效的文件名
     *
     * @param fileName
     * @return
     */
    boolean isValidFileName(String fileName);

    /**
     * 根据夹UUID获取夹配置的视图列表ID
     *
     * @param folderUuid
     * @return
     */
    String getFolderListViewId(String folderUuid);

    /**
     * 根据夹UUID获取夹配置
     *
     * @param folderUuid
     * @return
     */
    DmsFolderConfiguration getFolderConfiguration(String folderUuid);

    /**
     * 根据夹UUID获取夹视图配置
     *
     * @param folderUuid
     * @return
     */
    DmsFolderDataViewConfiguration getFolderDataViewConfiguration(String folderUuid);

    /**
     * 保存夹配置
     *
     * @param dmsFolderConfiguration
     */
    void saveFolderConfiguration(DmsFolderConfiguration dmsFolderConfiguration);

    /**
     * 创建夹
     *
     * @param folderUuid
     * @param folderName
     * @param parentFolderUuid
     * @return
     */
    String createFolder(String folderUuid, String folderName, String parentFolderUuid);

    /**
     * 重命名夹
     *
     * @param folderUuid
     * @param newFolderName
     */
    void renameFolder(String folderUuid, String newFolderName);

    /**
     * 删除夹
     *
     * @param folderUuid
     */
    void deleteFolder(String folderUuid);

    /**
     * 重命名文件
     *
     * @param fileUuid
     * @param newFileName
     */
    void renameFile(String fileUuid, String newFileName);

    /**
     * 读取文件内容
     *
     * @param fileUuid
     * @return
     */
    String readFileContentAsString(String fileUuid);

    /**
     * 获取夹属性
     *
     * @param dmsFile
     * @return
     */
    DmsFileAttributes getAttributes(DmsFile dmsFile);

    /**
     * 获取查看文件的信息
     *
     * @param dmsFile
     * @return
     */
    ViewFileInfo getViewFileInfo(DmsFile dmsFile);

    /**
     * 获取文件类型的信息
     *
     * @return
     */
    FileTypeInfos getFileTypeInfos();

    /**
     * 如何描述该方法
     *
     * @param category
     * @return
     */
    List<DmsRoleBean> getRolesByCategory(String category);

    /**
     * 如何描述该方法
     *
     * @param category
     * @return
     */
    List<DataItem> getRoleCategories();

    /**
     * 分享文件
     *
     * @param dmsFiles
     * @param shareOrgId
     * @param shareOrgName
     */
    void shareFile(List<DmsFile> dmsFiles, String shareOrgId, String shareOrgName);

    /**
     * 取消分享文件
     *
     * @param dmsFiles
     */
    void cancelShareFile(List<DmsFileShareInfo> dmsFiles);

    /**
     * 移动夹、文件
     *
     * @param sourceDmsFiles
     * @param destFolderUuid
     */
    void moveFile(List<DmsFile> sourceDmsFiles, String destFolderUuid);

    /**
     * 复制夹、文件
     *
     * @param sourceDmsFiles
     * @param destFolderUuid
     */
    List<String> copyFile(List<DmsFile> sourceDmsFiles, String destFolderUuid);

    /**
     * 删除文件
     *
     * @param dmsFiles
     */
    void deleteFile(List<DmsFile> dmsFiles);

    /**
     * 恢复文件
     *
     * @param dmsFiles
     */
    void restoreFile(List<DmsFile> dmsFiles);

    /**
     * 物理删除文件
     *
     * @param dmsFiles
     */
    void physicalDeleteFile(List<DmsFile> dmsFiles);

    /**
     * 检查同一目录下文件名是否重复
     *
     * @param folderName
     * @param parentFolderUuid
     * @return
     */
    boolean checkTheSameNameForCreateFolder(String folderName, String parentFolderUuid);

    /**
     * 检查同一目录下文件夹名是否重复
     *
     * @param folderName
     * @param parentFolderUuid
     * @return
     */
    boolean existsFolderNameByParentFolderUuid(String folderName, String parentFolderUuid);

    /**
     * 检查同一目录下文件名是否重复
     *
     * @param fileName
     * @param folderUuid
     * @return
     */
    boolean existsFileNameByFolderUuid(String fileName, String folderUuid);

    /**
     * 检查同一目录下文件夹名是否重复
     *
     * @param folderName
     * @param folderUuid
     * @return
     */
    boolean existsTheSameFolderNameWidthFolderUuid(String folderName, String folderUuid);

    /**
     * 检查同一目录下文件名是否重复
     *
     * @param fileName
     * @param fileUuid
     * @return
     */
    boolean existsTheSameFileNameWidthFileUuid(String fileName, String fileUuid);

    /**
     * 检查文件对应的MONGO数据是否存在
     *
     * @param fileUuids
     * @return
     */
    boolean checkFileDataExists(Collection<String> fileUuids);

    /**
     * 送审批流程
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getApproveFlowSelectData(Select2QueryInfo queryInfo);

    /**
     * 送审批流程选中
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getApproveFlowSelectDataByIds(Select2QueryInfo queryInfo);

    /**
     * 异步获取树
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getFolderTreeAsync(String treeNodeId);

    /**
     * 判断是否文件库的数据源
     *
     * @param dataStoreId
     * @return
     */
    boolean isFileManagerDataStore(String dataStoreId);


    Select2QueryData getDmsFolderSelectData(Select2QueryInfo queryInfo);

}
