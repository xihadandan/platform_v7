/*
 * @(#)3/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.model;

import com.wellsoft.pt.app.feishu.support.AbstractMessage;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 3/25/25.1	    zhulh		3/25/25		    Create
 * </pre>
 * @date 3/25/25
 */
public class FileMessage extends AbstractMessage {

    private static final long serialVersionUID = 4103698842829691229L;

    private String fileKey;
    private String fileName;
    private String type;

    /**
     * @return the fileKey
     */
    public String getFileKey() {
        return fileKey;
    }

    /**
     * @param fileKey 要设置的fileKey
     */
    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName 要设置的fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getTextMessage() {
        return this.fileName;
    }
}
