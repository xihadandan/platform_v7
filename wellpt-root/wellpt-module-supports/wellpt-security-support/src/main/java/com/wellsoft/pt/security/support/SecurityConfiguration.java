/*
 * @(#)2017-01-17 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.support;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 安全配置项信息
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
public class SecurityConfiguration implements InitializingBean {

    private AuthenticationUrlConfiguration defaultAuthenticationUrlConfiguration;

    // <登录类型，认证URL配置>映射
    private Map<String, AuthenticationUrlConfiguration> authenticationUrlMappings = new HashMap<String, AuthenticationUrlConfiguration>(
            0);


    /**
     * @return the defaultAuthenticationUrlConfiguration
     */
    public AuthenticationUrlConfiguration getDefaultAuthenticationUrlConfiguration() {
        return defaultAuthenticationUrlConfiguration;
    }

    /**
     * @param defaultAuthenticationUrlConfiguration 要设置的defaultAuthenticationUrlConfiguration
     */
    public void setDefaultAuthenticationUrlConfiguration(
            AuthenticationUrlConfiguration defaultAuthenticationUrlConfiguration) {
        this.defaultAuthenticationUrlConfiguration = defaultAuthenticationUrlConfiguration;
    }

    /**
     * @return the authenticationUrlMappings
     */
    public Map<String, AuthenticationUrlConfiguration> getAuthenticationUrlMappings() {
        return authenticationUrlMappings;
    }

    /**
     * @param authenticationUrlMappings 要设置的authenticationUrlMappings
     */
    public void setAuthenticationUrlMappings(Map<String, AuthenticationUrlConfiguration> authenticationUrlMappings) {
        this.authenticationUrlMappings = authenticationUrlMappings;
    }

    /**
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {
        for (Entry<String, AuthenticationUrlConfiguration> entry : this.authenticationUrlMappings.entrySet()) {
            AuthenticationUrlConfiguration value = entry.getValue();
            // 登录成功跳转地址
            if (StringUtils.isBlank(value.getAuthenticationSuccessUrl())) {
                value.setAuthenticationSuccessUrl(this.defaultAuthenticationUrlConfiguration
                        .getAuthenticationSuccessUrl());
            }
            // 登录失败跳转地址
            if (StringUtils.isBlank(value.getAuthenticationFailureUrl())) {
                value.setAuthenticationFailureUrl(this.defaultAuthenticationUrlConfiguration
                        .getAuthenticationFailureUrl());
            }
            // 登出处理地址
            if (StringUtils.isBlank(value.getLogoutFilterProcessesUrl())) {
                value.setLogoutFilterProcessesUrl(this.defaultAuthenticationUrlConfiguration
                        .getLogoutFilterProcessesUrl());
            }
            // 登出成功跳转地址
            if (StringUtils.isBlank(value.getLogoutSuccessUrl())) {
                value.setLogoutSuccessUrl(this.defaultAuthenticationUrlConfiguration.getLogoutSuccessUrl());
            }
            // 手机端登录失败跳转地址
            if (StringUtils.isBlank(value.getMobileAuthenticationFailureUrl())) {
                value.setMobileAuthenticationFailureUrl(this.defaultAuthenticationUrlConfiguration
                        .getMobileAuthenticationFailureUrl());
            }
            // 手机端登出成功跳转地址
            if (StringUtils.isBlank(value.getMobileLogoutSuccessUrl())) {
                value.setMobileLogoutSuccessUrl(this.defaultAuthenticationUrlConfiguration.getMobileLogoutSuccessUrl());
            }
        }
    }

    /**
     * @param loginType
     * @return
     */
    public AuthenticationUrlConfiguration getAuthenticationUrlConfiguration(String loginType) {
        if (this.authenticationUrlMappings.containsKey(loginType)) {
            return this.authenticationUrlMappings.get(loginType);
        }
        return this.defaultAuthenticationUrlConfiguration;
    }

}
