/*
 * @(#)Jan 17, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.event.LogoutSuccessEvent;
import com.wellsoft.pt.security.support.AuthenticationUrlConfiguration;
import com.wellsoft.pt.security.support.SecurityConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 17, 2017.1	zhulh		Jan 17, 2017		Create
 * </pre>
 * @date Jan 17, 2017
 */
public class MutableLogoutFilter extends LogoutFilter {

    private final List<LogoutHandler> handlers;
    private SecurityConfiguration securityConfiguration;
    private Map<String, RequestMatcher> logoutRequestMatcherMap = new HashMap<String, RequestMatcher>(0);
    private Map<String, LogoutSuccessHandler> logoutSuccessHandlerMap = new HashMap<String, LogoutSuccessHandler>(0);

    /**
     * @param logoutSuccessUrl
     * @param handlers
     */
    public MutableLogoutFilter(String logoutSuccessUrl, LogoutHandler[] handlers) {
        super(logoutSuccessUrl, handlers);
        this.handlers = Arrays.asList(handlers);
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (requiresLogout(request, response)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            for (LogoutHandler handler : handlers) {
                handler.logout(request, response, auth);
            }

            Object princial = null;
            if (auth != null) {
                princial = auth.getPrincipal();
            }
            String loginType = LoginType.USER;
            if (princial instanceof UserDetails) {
                loginType = ((UserDetails) princial).getLoginType();
                ApplicationContextHolder.getApplicationContext().publishEvent(new LogoutSuccessEvent(((UserDetails) princial)));
            }
            LogoutSuccessHandler logoutSuccessHandler = getLogoutSuccessHandler(loginType, request, response);
            logoutSuccessHandler.onLogoutSuccess(request, response, auth);

            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * @param loginType
     * @return
     */
    private LogoutSuccessHandler getLogoutSuccessHandler(String loginType, HttpServletRequest request,
                                                         HttpServletResponse response) {
        String logoutSuccessUrl = securityConfiguration.getAuthenticationUrlConfiguration(loginType)
                .getLogoutSuccessUrl(request, response);
        if (!logoutSuccessHandlerMap.containsKey(logoutSuccessUrl)) {
            SimpleUrlLogoutSuccessHandler urlLogoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
            urlLogoutSuccessHandler.setDefaultTargetUrl(logoutSuccessUrl);
            logoutSuccessHandlerMap.put(logoutSuccessUrl, urlLogoutSuccessHandler);
        }
        return logoutSuccessHandlerMap.get(logoutSuccessUrl);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.filter.GenericFilterBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        setSecurityConfiguration(securityConfiguration);
    }

    protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
        for (Entry<String, RequestMatcher> entry : logoutRequestMatcherMap.entrySet()) {
            if (entry.getValue().matches(request)) {
                return true;
            }
        }
        return false;
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
        if (securityConfiguration == null || securityConfiguration.getAuthenticationUrlMappings() == null) {
            return;
        }
        for (Entry<String, AuthenticationUrlConfiguration> entry : securityConfiguration.getAuthenticationUrlMappings()
                .entrySet()) {
            AuthenticationUrlConfiguration value = entry.getValue();
            String logoutFilterProcessesUrl = value.getLogoutFilterProcessesUrl();
            logoutRequestMatcherMap.put(logoutFilterProcessesUrl,
                    new FilterProcessUrlRequestMatcher(logoutFilterProcessesUrl));
        }
    }

    private static final class FilterProcessUrlRequestMatcher implements RequestMatcher {
        private final String filterProcessesUrl;

        private FilterProcessUrlRequestMatcher(String filterProcessesUrl) {
            Assert.hasLength(filterProcessesUrl, "filterProcessesUrl must be specified");
            Assert.isTrue(UrlUtils.isValidRedirectUrl(filterProcessesUrl),
                    filterProcessesUrl + " isn't a valid redirect URL");
            this.filterProcessesUrl = filterProcessesUrl;
        }

        public boolean matches(HttpServletRequest request) {
            String uri = request.getRequestURI();
            int pathParamIndex = uri.indexOf(';');

            if (pathParamIndex > 0) {
                // strip everything from the first semi-colon
                uri = uri.substring(0, pathParamIndex);
            }

            int queryParamIndex = uri.indexOf('?');

            if (queryParamIndex > 0) {
                // strip everything from the first question mark
                uri = uri.substring(0, queryParamIndex);
            }

            if ("".equals(request.getContextPath())) {
                return uri.endsWith(filterProcessesUrl);
            }

            return uri.endsWith(request.getContextPath() + filterProcessesUrl);
        }
    }
}
