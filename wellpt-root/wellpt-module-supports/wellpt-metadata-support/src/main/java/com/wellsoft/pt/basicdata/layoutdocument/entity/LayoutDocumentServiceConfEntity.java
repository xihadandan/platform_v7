/*
 * @(#)2021-12-02 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.layoutdocument.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 数据库表LAYOUT_DOCUMENT_SERVICE_CONF的实体类
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
@Entity
@Table(name = "LAYOUT_DOCUMENT_SERVICE_CONF")
@DynamicUpdate
@DynamicInsert
public class LayoutDocumentServiceConfEntity extends TenantEntity {

    private static final long serialVersionUID = 1638425347674L;

    // 编号
    private String code;
    // 服务唯一标识符
    @NotBlank
    private String serverUniqueCode;
    // 支持的文件扩展名
    @NotBlank
    private String fileExtensions;
    // 服务地址
    @NotBlank
    private String serverUrl;
    // 服务名称
    @NotBlank
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
