/*
 * @(#)2015年8月14日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.entity;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
 * 2015年8月14日.1	zhulh		2015年8月14日		Create
 * </pre>
 * @date 2015年8月14日
 */
@Entity
@CommonEntity
@Table(name = "MT_TENANT_GZ_USER")
@DynamicUpdate
@DynamicInsert
public class TenantGzUser extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4867013008092574974L;
    // 用户名
    private String userName;
    // 用户名登录名
    private String userLoginName;
    // 用户ID
    private String userId;
    // 主职租户名称
    private String zzTenantName;
    // 主职租户ID
    private String zzTenantId;
    // 挂职租户名称
    private String gzTenantName;
    // 挂职租户ID
    private String gzTenantId;

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
     * @return the userLoginName
     */
    public String getUserLoginName() {
        return userLoginName;
    }

    /**
     * @param userLoginName 要设置的userLoginName
     */
    public void setUserLoginName(String userLoginName) {
        this.userLoginName = userLoginName;
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
     * @return the zzTenantName
     */
    public String getZzTenantName() {
        return zzTenantName;
    }

    /**
     * @param zzTenantName 要设置的zzTenantName
     */
    public void setZzTenantName(String zzTenantName) {
        this.zzTenantName = zzTenantName;
    }

    /**
     * @return the zzTenantId
     */
    public String getZzTenantId() {
        return zzTenantId;
    }

    /**
     * @param zzTenantId 要设置的zzTenantId
     */
    public void setZzTenantId(String zzTenantId) {
        this.zzTenantId = zzTenantId;
    }

    /**
     * @return the gzTenantName
     */
    public String getGzTenantName() {
        return gzTenantName;
    }

    /**
     * @param gzTenantName 要设置的gzTenantName
     */
    public void setGzTenantName(String gzTenantName) {
        this.gzTenantName = gzTenantName;
    }

    /**
     * @return the gzTenantId
     */
    public String getGzTenantId() {
        return gzTenantId;
    }

    /**
     * @param gzTenantId 要设置的gzTenantId
     */
    public void setGzTenantId(String gzTenantId) {
        this.gzTenantId = gzTenantId;
    }

}
