/*
 * @(#)2015年9月10日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.entity;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月10日.1	zhulh		2015年9月10日		Create
 * </pre>
 * @date 2015年9月10日
 */
@Entity
@CommonEntity
@Table(name = "api_access_log")
@DynamicUpdate
@DynamicInsert
public class ApiAccessLog extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5320945542498062861L;

    // 租户ID
    private String tenantId;
    // 用户名
    private String username;
    // 用户ID
    private String userId;
    // 调用的接口服务
    private String apiServiceName;
    // 请求的JSON数据
    private String requestJson;
    // 响应的数据
    private String responseBody;
    // 客户端访问IP
    private String clientIp;
    // 客户端访问浏览器
    private String clientBrowser;
    // 记录时间
    private Date logTime;

    /**
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId 要设置的tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username 要设置的username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the apiServiceName
     */
    public String getApiServiceName() {
        return apiServiceName;
    }

    /**
     * @param apiServiceName 要设置的apiServiceName
     */
    public void setApiServiceName(String apiServiceName) {
        this.apiServiceName = apiServiceName;
    }

    /**
     * @return the requestJson
     */
    public String getRequestJson() {
        return requestJson;
    }

    /**
     * @param requestJson 要设置的requestJson
     */
    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    /**
     * @return the responseBody
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * @param responseBody 要设置的responseBody
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
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
     * @return the logTime
     */
    public Date getLogTime() {
        return logTime;
    }

    /**
     * @param logTime 要设置的logTime
     */
    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

}
