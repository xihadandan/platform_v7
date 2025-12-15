/*
 * @(#)Jan 23, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.support;

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
public class FileStore extends Store {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4998191187853452201L;

    private String folderUuid;

    private String fileNameField;

    /**
     * @return the folderUuid
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * @param folderUuid 要设置的folderUuid
     */
    public void setFolderUuid(String folderUuid) {
        this.folderUuid = folderUuid;
    }

    /**
     * @return the fileNameField
     */
    public String getFileNameField() {
        return fileNameField;
    }

    /**
     * @param fileNameField 要设置的fileNameField
     */
    public void setFileNameField(String fileNameField) {
        this.fileNameField = fileNameField;
    }

}
