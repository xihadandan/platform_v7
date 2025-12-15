/*
 * @(#)2017-01-17 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.web.HttpRequestDeviceUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

/**
 * Description: 认证相关URL配置
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-17.1	zhulh		2017-01-17		Create
 * </pre>
 * @date 2017-01-17
 */
public class AuthenticationUrlConfiguration {
    // 登录成功跳转地址
    private String authenticationSuccessUrl;
    // 登录失败跳转地址
    private String authenticationFailureUrl;

    // 登出处理地址
    private String logoutFilterProcessesUrl;
    // 登出成功跳转地址
    private String logoutSuccessUrl;

    // 手机端登录失败跳转地址
    private String mobileAuthenticationFailureUrl;
    // 手机端登出成功跳转地址
    private String mobileLogoutSuccessUrl;

    private Map<String, Set<String>> logoutSuccessUrlMapping = Maps.newHashMap();

    /**
     * @return the authenticationSuccessUrl
     */
    public String getAuthenticationSuccessUrl() {
        return authenticationSuccessUrl;
    }

    /**
     * @param authenticationSuccessUrl 要设置的authenticationSuccessUrl
     */
    public void setAuthenticationSuccessUrl(String authenticationSuccessUrl) {
        this.authenticationSuccessUrl = authenticationSuccessUrl;
    }

    /**
     * @return the authenticationFailureUrl
     */
    public String getAuthenticationFailureUrl() {
        return authenticationFailureUrl;
    }

    /**
     * @param authenticationFailureUrl 要设置的authenticationFailureUrl
     */
    public void setAuthenticationFailureUrl(String authenticationFailureUrl) {
        this.authenticationFailureUrl = authenticationFailureUrl;
    }

    /**
     * @return the logoutFilterProcessesUrl
     */
    public String getLogoutFilterProcessesUrl() {
        return logoutFilterProcessesUrl;
    }

    /**
     * @param logoutFilterProcessesUrl 要设置的logoutFilterProcessesUrl
     */
    public void setLogoutFilterProcessesUrl(String logoutFilterProcessesUrl) {
        this.logoutFilterProcessesUrl = logoutFilterProcessesUrl;
    }

    /**
     * @return the logoutSuccessUrl
     */
    public String getLogoutSuccessUrl() {
        return logoutSuccessUrl;
    }

    /**
     * @param logoutSuccessUrl 要设置的logoutSuccessUrl
     */
    public void setLogoutSuccessUrl(String logoutSuccessUrl) {
        this.logoutSuccessUrl = logoutSuccessUrl;
    }

    /**
     * @return the mobileAuthenticationFailureUrl
     */
    public String getMobileAuthenticationFailureUrl() {
        return mobileAuthenticationFailureUrl;
    }

    /**
     * @param mobileAuthenticationFailureUrl 要设置的mobileAuthenticationFailureUrl
     */
    public void setMobileAuthenticationFailureUrl(String mobileAuthenticationFailureUrl) {
        this.mobileAuthenticationFailureUrl = mobileAuthenticationFailureUrl;
    }

    /**
     * @return the mobileLogoutSuccessUrl
     */
    public String getMobileLogoutSuccessUrl() {
        return mobileLogoutSuccessUrl;
    }

    /**
     * @param mobileLogoutSuccessUrl 要设置的mobileLogoutSuccessUrl
     */
    public void setMobileLogoutSuccessUrl(String mobileLogoutSuccessUrl) {
        this.mobileLogoutSuccessUrl = mobileLogoutSuccessUrl;
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public String getAuthenticationFailureUrl(HttpServletRequest request, HttpServletResponse response) {
        if ((HttpRequestDeviceUtils.isAndroidDevice(request) || HttpRequestDeviceUtils.isIOSDevice(request) || HttpRequestDeviceUtils
                .isMobileDevice(request)) && StringUtils.isNotBlank(this.mobileAuthenticationFailureUrl)) {
            return this.mobileAuthenticationFailureUrl;
        }
        return this.authenticationFailureUrl;
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public String getLogoutSuccessUrl(HttpServletRequest request, HttpServletResponse response) {
        if ((HttpRequestDeviceUtils.isAndroidDevice(request) || HttpRequestDeviceUtils.isIOSDevice(request) || HttpRequestDeviceUtils
                .isMobileDevice(request)) && StringUtils.isNotBlank(this.mobileLogoutSuccessUrl)) {
            return this.mobileLogoutSuccessUrl;
        }
        // TODO：匹配对应地址响应的退出地址
        return this.logoutSuccessUrl;
    }

}
