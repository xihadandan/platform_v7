/*
 * @(#)Dec 29, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.action;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 29, 2017.1	zhulh		Dec 29, 2017		Create
 * </pre>
 * @date Dec 29, 2017
 */
public interface FileActions {
    // 创建夹
    String CREATE_FOLDER = "createFolder";
    // 读取夹
    String READ_FOLDER = "readFolder";
    // 列出当前夹下的子夹
    String LIST_FOLDER = "listFolder";
    // 列出当前夹下的所有子夹(包含子夹)
    String LIST_ALL_FOLDER = "listAllFolder";
    // 列出当前夹下的文件
    String LIST_FILES = "listFiles";
    // 列出当前夹下的文件(包含子夹)
    String LIST_ALL_FILES = "listAllFiles";
    // 列出当前夹下的子夹及文件
    String LIST_FOLDER_AND_FILES = "listFolderAndFiles";
    // 列出当前夹下的子夹及文件(包含子夹)
    String LIST_ALL_FOLDER_AND_FILES = "listAllFolderAndFiles";
    // 分享夹
    String SHARE_FOLDER = "shareFolder";
    // 取消分享夹
    String CANCEL_SHARE_FOLDER = "cancelShareFolder";
    // 查看夹属性
    String VIEW_FOLDER_ATTRIBUTES = "viewFolderAttributes";
    // 编辑夹属性
    String EDIT_FOLDER_ATTRIBUTES = "editFolderAttributes";
    // 获取夹的操作权限
    String GET_FOLDER_ACTIONS = "getFolderActions";
    // 重命名夹
    String RENAME_FOLDER = "renameFolder";
    // 移动夹
    String MOVE_FOLDER = "moveFolder";
    // 复制夹
    String COPY_FOLDER = "copyFolder";
    // 删除夹(有子夹、文件不可删除)
    String DELETE_FOLDER = "deleteFolder";
    // 删除夹(有子夹、文件一起删除)
    String FORCE_DELETE_FOLDER = "forceDeleteFolder";
    // 恢复夹
    String RESTORE_FOLDER = "restoreFolder";
    // 彻底删除夹(有子夹、文件一起删除)
    String PHYSICAL_DELETE_FOLDER = "physicalDeleteFolder";

    // 创建文件
    String CREATE_FILE = "createFile";
    // 创建文档
    String CREATE_DOCUMENT = "createDocument";
    // 读取文件
    String READ_FILE = "readFile";
    // 打开文件
    String OPEN_FILE = "openFile";
    // 预览文件
    String PREVIEW_FILE = "previewFile";
    // 下载文件
    String DOWNLOAD_FILE = "downloadFile";
    // 收藏文件
    String ATTENTION_FILE = "attentionFile";
    // 取消收藏文件
    String UNFOLLOW_FILE = "unfollowFile";
    // 分享文件
    String SHARE_FILE = "shareFile";
    // 取消分享文件
    String CANCEL_SHARE_FILE = "cancelShareFile";
    // 查看文件属性
    String VIEW_FILE_ATTRIBUTES = "viewFileAttributes";
    // 获取文件的操作权限
    String GET_FILE_ACTIONS = "getFileActions";
    // 重命名文件
    String RENAME_FILE = "renameFile";
    // 签出文件
    String CHECK_OUT_FILE = "checkOutFile";
    // 签入文件
    String CHECK_IN_FILE = "checkInFile";
    // 编辑文件
    String EDIT_FILE = "editFile";
    // 移动文件
    String MOVE_FILE = "moveFile";
    // 复制文件
    String COPY_FILE = "copyFile";
    // 删除文件
    String DELETE_FILE = "deleteFile";
    // 恢复文件
    String RESTORE_FILE = "restoreFile";
    // 彻底删除文件
    String PHYSICAL_DELETE_FILE = "physicalDeleteFile";
}
