/*
 * @(#)2013-10-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.log.OperationLog;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-14.1	zhulh		2013-10-14		Create
 * </pre>
 * @date 2013-10-14
 */
@Entity
@Table(name = "log_user_operation")
public class UserOperationLog extends TenantEntity implements OperationLog {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 9140758443948554838L;

    private String moduleId;

    private String moduleName;

    private String content;

    private String operation;

    private String details;

    private String userName;

    private String clientIp;

    private String clientBrowser;

    private String system;
    private String tenant;

    /**
     * @return the moduleId
     */
    @Override
    public String getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId 要设置的moduleId
     */
    @Override
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the moduleName
     */
    @Override
    public String getModuleName() {
        return moduleName;
    }

    /**
     * @param moduleName 要设置的moduleName
     */
    @Override
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the content
     */
    @Override
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    @Override
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the operation
     */
    @Override
    public String getOperation() {
        return operation;
    }

    /**
     * @param operation 要设置的operation
     */
    @Override
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * @return the details
     */
    @Override
    public String getDetails() {
        return details;
    }

    /**
     * @param details 要设置的details
     */
    @Override
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName 要设置的userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the clientIp
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * @param clientIp 要设置的clientIp
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * @return the clientBrowser
     */
    public String getClientBrowser() {
        return clientBrowser;
    }

    /**
     * @param clientBrowser 要设置的clientBrowser
     */
    public void setClientBrowser(String clientBrowser) {
        this.clientBrowser = clientBrowser;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
