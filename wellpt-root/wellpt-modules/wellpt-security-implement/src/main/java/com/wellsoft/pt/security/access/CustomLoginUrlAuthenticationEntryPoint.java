/*
 * @(#)2017-01-18 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access;

import com.wellsoft.pt.security.support.SecurityConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 自定义返回http登录验证入口URL
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-18.1	zhulh		2017-01-18		Create
 * </pre>
 * @date 2017-01-18
 */
@SuppressWarnings("deprecation")
public class CustomLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private SecurityConfiguration securityConfiguration;

    public CustomLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint#determineUrlToUseForThisRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
     */
    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
                                                     AuthenticationException exception) {
        String loginUrl = securityConfiguration.getAuthenticationUrlConfiguration(request.getParameter("loginType")).getLogoutSuccessUrl(request,
                response);
//		String loginUrl = securityConfiguration.getDefaultAuthenticationUrlConfiguration().getLogoutSuccessUrl(request,
//				response);
        if (StringUtils.isNotBlank(loginUrl)) {
            return loginUrl;
        }
        return super.determineUrlToUseForThisRequest(request, response, exception);
    }

    /**
     * @return the securityConfiguration
     */
    public SecurityConfiguration getSecurityConfiguration() {
        return securityConfiguration;
    }

    /**
     * @param securityConfiguration 要设置的securityConfiguration
     */
    public void setSecurityConfiguration(SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
    }

}
