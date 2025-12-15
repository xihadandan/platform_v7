/*
 * @(#)2013-4-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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
 * 2013-4-30.1	zhulh		2013-4-30		Create
 * </pre>
 * @date 2013-4-30
 */
@Entity
@Table(name = "SYS_IP_CONFIG")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entity")
@DynamicUpdate
@DynamicInsert
public class IpSecurityConfig extends IdEntity {

    // 登录验证码
    public static final String APPLY_TO_LOGIN_VERIFY_CODE = "1";
    // 限制用户登录
    public static final String APPLY_TO_LOGIN_LIMIT = "2";
    // 手机短信二次验证
    public static final String APPLY_TO_SMS_VERIFY_CODE = "3";
    // 限制用户域登录
    public static final String APPLY_TO_DOMAIN_LOGIN_LIMIT = "4";
    private static final long serialVersionUID = 7460312831689281823L;
    // 需要验证、允许登录的IP地址
    private String ipAddress1;

    // 不需要验证、禁止登录的IP地址
    private String ipAddress2;

    // 应用于登录验证码、限制用户登录、手机短信二次验证
    private String applyTo;

    // 有效期限(以秒为单位)
    private Integer validPeriod;

    // 需要验证、允许登录的域地址
    private String domainAddress1;

    // 不需要验证、禁止登录的域地址
    private String domainAddress2;

    // 要限制的用户、部门ID
    private String sid;

    // 要限制的用户、部门名称
    private String sidName;

    private String tenant;

    private String system;

    /**
     * @return the ipAddress1
     */
    public String getIpAddress1() {
        return ipAddress1;
    }

    /**
     * @param ipAddress1 要设置的ipAddress1
     */
    public void setIpAddress1(String ipAddress1) {
        this.ipAddress1 = ipAddress1;
    }

    /**
     * @return the ipAddress2
     */
    public String getIpAddress2() {
        return ipAddress2;
    }

    /**
     * @param ipAddress2 要设置的ipAddress2
     */
    public void setIpAddress2(String ipAddress2) {
        this.ipAddress2 = ipAddress2;
    }

    /**
     * @return the applyTo
     */
    public String getApplyTo() {
        return applyTo;
    }

    /**
     * @param applyTo 要设置的applyTo
     */
    public void setApplyTo(String applyTo) {
        this.applyTo = applyTo;
    }

    /**
     * @return the validPeriod
     */
    public Integer getValidPeriod() {
        return validPeriod;
    }

    /**
     * @param validPeriod 要设置的validPeriod
     */
    public void setValidPeriod(Integer validPeriod) {
        this.validPeriod = validPeriod;
    }

    public String getDomainAddress1() {
        return domainAddress1;
    }

    public void setDomainAddress1(String domainAddress1) {
        this.domainAddress1 = domainAddress1;
    }

    public String getDomainAddress2() {
        return domainAddress2;
    }

    public void setDomainAddress2(String domainAddress2) {
        this.domainAddress2 = domainAddress2;
    }

    /**
     * @return the sid
     */
    public String getSid() {
        return sid;
    }

    /**
     * @param sid 要设置的sid
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * @return the sidName
     */
    public String getSidName() {
        return sidName;
    }

    /**
     * @param sidName 要设置的sidName
     */
    public void setSidName(String sidName) {
        this.sidName = sidName;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
