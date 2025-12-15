/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.domain;

import com.wellsoft.pt.api.WellptObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public class RequestParam extends WellptObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4665264840516891760L;

    // 租户ID
    private String tenantId;
    // 用户名
    private String username;
    // 密码
    private String password;

    private String json;

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
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password 要设置的password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the json
     */
    public String getJson() {
        return json;
    }

    /**
     * @param json 要设置的json
     */
    public void setJson(String json) {
        this.json = json;
    }

}
