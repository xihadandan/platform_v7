/*
 * @(#)2021-12-02 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.layoutdocument.dto;

import java.io.Serializable;


/**
 * Description: 数据库表LAYOUT_DOCUMENT_SERVICE_CONF的对应的DTO类
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-12-02.1	shenhb		2021-12-02		Create
 * </pre>
 * @date 2021-12-02
 */
public class LayoutDocumentServiceConfDto implements Serializable {

    private static final long serialVersionUID = 1638425347674L;

    // uuid
    private String uuid;
    // 编号
    private String code;
    // 服务唯一标识符
    private String serverUniqueCode;
    // 支持的文件扩展名
    private String fileExtensions;
    // 服务地址
    private String serverUrl;
    // 服务名称
    private String serverName;
    // 状态
    private String status;

    /**
     * @return the code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the serverUniqueCode
     */
    public String getServerUniqueCode() {
        return this.serverUniqueCode;
    }

    /**
     * @param serverUniqueCode
     */
    public void setServerUniqueCode(String serverUniqueCode) {
        this.serverUniqueCode = serverUniqueCode;
    }

    /**
     * @return the fileExtensions
     */
    public String getFileExtensions() {
        return this.fileExtensions;
    }

    /**
     * @param fileExtensions
     */
    public void setFileExtensions(String fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    /**
     * @return the serverUrl
     */
    public String getServerUrl() {
        return this.serverUrl;
    }

    /**
     * @param serverUrl
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return this.serverName;
    }

    /**
     * @param serverName
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
