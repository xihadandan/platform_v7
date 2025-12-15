/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.pt.security.support.SecurityConfiguration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-26.1	zhulh		2012-12-26		Create
 * </pre>
 * @date 2012-12-26
 */
public class DomainSimpleUrlAuthenticationFailureHandler extends
        SimpleUrlAuthenticationFailureHandler {

    private SecurityConfiguration securityConfiguration;

    public DomainSimpleUrlAuthenticationFailureHandler() {
    }

    public DomainSimpleUrlAuthenticationFailureHandler(
            SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler#onAuthenticationFailure(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        try {
            // Object tenant =
            // request.getAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_KEY);
            String loginType = request
                    .getParameter(
                            LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_LOGIN_TYPE);
            if (exception.getCause() != null && "OAuth2AccessDeniedException".equals(exception.getCause().getClass().getSimpleName())) {
                // 统一认证拒绝访问
                loginType = LoginType.INTERNET_USER;
                exception = new BadCredentialsException(SpringSecurityMessageSource.getAccessor().getMessage(
                        "AbstractUserDetailsAuthenticationProvider.badCredentials",
                        "Bad credentials"));
                try {
                    Cookie errorLoginCookie = new Cookie("SPRING_SECURITY_LAST_EXCEPTION", URLEncoder.encode(exception.getMessage()));
                    errorLoginCookie.setPath(request.getContextPath().length() > 0 ? request.getContextPath() : "/");
                    errorLoginCookie.setMaxAge(5);
                    response.addCookie(errorLoginCookie);
                } catch (Exception e) {
                }
            }
            String authenticationFailureUrl = securityConfiguration.getAuthenticationUrlConfiguration(
                    loginType)
                    .getAuthenticationFailureUrl(request, response);
            this.setDefaultFailureUrl(authenticationFailureUrl);
            super.onAuthenticationFailure(request, response, exception);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
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
