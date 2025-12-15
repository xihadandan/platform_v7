/*
 * @(#)2014-1-15 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.authentication;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.util.web.AjaxUtils;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.support.SecurityConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-15.1	zhulh		2014-1-15		Create
 * </pre>
 * @date 2014-1-15
 */
public class CustomSaveRequestAwareAuthenticationSuccessHandler extends
        SavedRequestAwareAuthenticationSuccessHandler {

    static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";
    private SecurityConfiguration securityConfiguration;
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    public CustomSaveRequestAwareAuthenticationSuccessHandler() {
    }

    public CustomSaveRequestAwareAuthenticationSuccessHandler(
            SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
    }

    public CustomSaveRequestAwareAuthenticationSuccessHandler(
            SecurityConfiguration securityConfiguration,
            AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.securityConfiguration = securityConfiguration;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public CustomSaveRequestAwareAuthenticationSuccessHandler(
            AuthenticationSuccessHandler authenticationSuccessHandler, String defaultTargetUrl) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.setDefaultTargetUrl(defaultTargetUrl);
    }


    /**
     * @return the authenticationSuccessHandler
     */
    public AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return authenticationSuccessHandler;
    }

    /**
     * @param authenticationSuccessHandler 要设置的authenticationSuccessHandler
     */
    public void setAuthenticationSuccessHandler(
            AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        if (authenticationSuccessHandler != null) {
            authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        }
        SavedRequest savedRequest = (SavedRequest) request.getSession().getAttribute(SAVED_REQUEST);
        if (savedRequest != null && AjaxUtils.isAjaxRequest(savedRequest)) {
            request.getSession().removeAttribute(SAVED_REQUEST);
        }
        String source = request.getParameter("source");
        if (StringUtils.isBlank(source) || (!StringUtils.isBlank(source) && !source.equals(
                "mobile"))) {// 非手机登陆时重定向
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    /**
     * Builds the target URL according to the logic defined in the main class Javadoc.
     */
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String targetUrl = super.determineTargetUrl(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object princial = null;
        if (auth != null) {
            princial = auth.getPrincipal();
        }
        String loginType = LoginType.USER;
        if (princial instanceof UserDetails) {
            loginType = ((UserDetails) princial).getLoginType();
        }
        if (securityConfiguration != null) {
            targetUrl = securityConfiguration.getAuthenticationUrlConfiguration(loginType)
                    .getAuthenticationSuccessUrl();
        }
        return targetUrl;
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
