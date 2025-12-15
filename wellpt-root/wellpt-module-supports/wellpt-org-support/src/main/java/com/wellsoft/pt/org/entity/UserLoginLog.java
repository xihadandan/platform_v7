/*
 * @(#)2012-12-31 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 用户登录日记
 *
 * @author
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-13	liuzq		2013-10-13
 * </pre>
 * @date 2013-10-13
 */
//@Entity
//@Table(name = "org_user_login_log")
@Deprecated
public class UserLoginLog extends IdEntity {

    private static final long serialVersionUID = 2627861934930043189L;

    // private User user;

    private String userUuid;

    private String loginIp;

    private Integer loginSource;

    // 登录时间
    private Date loginTime;

    // 注销时间
    private Date logoutTime;
    private String tenantId;

    @ApiModelProperty("客户端操作系统")
    private String userOs;
    @ApiModelProperty("客户端浏览器")
    private String browser;

    @ApiModelProperty("客户端浏览器版本")
    private String browserVersion;

    public String getBrowserVersion() {
        return this.browserVersion;
    }

    public void setBrowserVersion(final String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getUserOs() {
        return this.userOs;
    }

    public void setUserOs(final String userOs) {
        this.userOs = userOs;
    }

    public String getBrowser() {
        return this.browser;
    }

    public void setBrowser(final String browser) {
        this.browser = browser;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the user
     */
    // @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "user_uuid")
    // public User getUser() {
    // return user;
    // }
    //
    // /**
    // * @param user 要设置的user
    // */
    // public void setUser(User user) {
    // this.user = user;
    // }
    @Column(name = "login_ip")
    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    /**
     * @return the userUuid
     */
    public String getUserUuid() {
        return userUuid;
    }

    /**
     * @param userUuid 要设置的userUuid
     */
    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    @Column(name = "login_time")
    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    @Column(name = "logout_time")
    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public Integer getLoginSource() {
        return loginSource;
    }

    public void setLoginSource(Integer loginSource) {
        this.loginSource = loginSource;
    }
}
