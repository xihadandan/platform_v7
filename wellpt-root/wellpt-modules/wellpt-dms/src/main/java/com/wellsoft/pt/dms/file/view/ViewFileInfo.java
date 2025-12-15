/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.view;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月26日.1	zhulh		2018年9月26日		Create
 * </pre>
 * @date 2018年9月26日
 */
public class ViewFileInfo extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8512751803264666627L;

    // 文件类型
    private String contentType;
    // 查看文件的UUID
    private String fileUuid;
    // 查看文件的URL
    private String viewUrl;

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType 要设置的contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the fileUuid
     */
    public String getFileUuid() {
        return fileUuid;
    }

    /**
     * @param fileUuid 要设置的fileUuid
     */
    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    /**
     * @return the viewUrl
     */
    public String getViewUrl() {
        return viewUrl;
    }

    /**
     * @param viewUrl 要设置的viewUrl
     */
    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }

}
