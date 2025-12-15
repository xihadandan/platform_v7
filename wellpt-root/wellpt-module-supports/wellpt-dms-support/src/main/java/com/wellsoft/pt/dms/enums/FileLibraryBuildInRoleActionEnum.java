/*
 * @(#)5/20/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 5/20/24.1	zhulh		5/20/24		Create
 * </pre>
 * @date 5/20/24
 */
public enum FileLibraryBuildInRoleActionEnum {
    ADMINISTRATOR("管理者", "createFolder,readFolder,listFolder,listAllFolder,listFiles,listAllFiles,listFolderAndFiles,listAllFolderAndFiles,shareFolder,viewFolderAttributes,renameFolder,moveFolder,copyFolder,deleteFolder,forceDeleteFolder,createFile,createDocument,readFile,openFile,downloadFile,shareFile,viewFileAttributes,renameFile,editFile,moveFile,copyFile,deleteFile"),
    EDITOR("编辑者", "readFolder,listFolder,listAllFolder,listFiles,listAllFiles,listFolderAndFiles,listAllFolderAndFiles,viewFolderAttributes,editFolderAttributes,renameFolder,moveFolder,readFile,openFile,viewFileAttributes,renameFile,editFile,moveFile"),
    READER("阅读者", "readFolder,listFolder,listAllFolder,listFiles,listAllFiles,listFolderAndFiles,listAllFolderAndFiles,viewFolderAttributes,readFile,openFile,viewFileAttributes");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private FileLibraryBuildInRoleActionEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
