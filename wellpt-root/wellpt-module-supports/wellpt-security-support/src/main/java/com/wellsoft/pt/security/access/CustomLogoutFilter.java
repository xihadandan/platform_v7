/*
 * @(#)2014-1-17 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access;

import com.wellsoft.pt.security.core.userdetails.SuperAdminDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.support.CasLoginUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

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

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-17.1	zhulh		2014-1-17		Create
 * </pre>
 * @date 2014-1-17
 */
public class CustomLogoutFilter extends GenericFilterBean {

    // ~ Instance fields
    // ================================================================================================

    // public static String DEFAULT_LOGOUT_URL = "/security_logout";
    public static final String CAS_LOGOUT_URL = "/j_spring_cas_security_logout"; // add
    // by
    // zky
    // cas的logout请求
    public static final String DEFAULT_LOGOUT_URL = "/j_spring_security_logout";
    private final List<LogoutHandler> handlers;
    private final SimpleUrlLogoutSuccessHandler defaultLogoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
    private final SimpleUrlLogoutSuccessHandler superAdminLogoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
    private final Map<String, SimpleUrlLogoutSuccessHandler> logoutSuccessHandlerMap = new HashMap<String, SimpleUrlLogoutSuccessHandler>();
    private RequestMatcher logoutRequestMatcher;
    private String logoutSuccessUrl = "/";
    private String superAdminLogoutSuccessUrl = "/superadmin/login";
    private LogoutSuccessHandler logoutSuccessHandler;

    // ~ Constructors
    // ===================================================================================================

    /**
     * Constructor which takes a <tt>LogoutSuccessHandler</tt> instance to determine the target destination
     * after logging out. The list of <tt>LogoutHandler</tt>s are intended to perform the actual logout functionality
     * (such as clearing the security context, invalidating the session, etc.).
     */
    public CustomLogoutFilter(LogoutSuccessHandler logoutSuccessHandler, LogoutHandler... handlers) {
        this.logoutSuccessHandler = logoutSuccessHandler;
        Assert.notEmpty(handlers, "LogoutHandlers are required");
        this.handlers = Arrays.asList(handlers);
        Assert.notNull(logoutSuccessHandler, "logoutSuccessHandler cannot be null");
    }

    public CustomLogoutFilter(String logoutSuccessUrl, LogoutHandler... handlers) {
        Assert.notEmpty(handlers, "LogoutHandlers are required");
        this.handlers = Arrays.asList(handlers);
        Assert.isTrue(!StringUtils.hasLength(logoutSuccessUrl) || UrlUtils.isValidRedirectUrl(logoutSuccessUrl),
                logoutSuccessUrl + " isn't a valid redirect URL");
        SimpleUrlLogoutSuccessHandler urlLogoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
        if (StringUtils.hasText(logoutSuccessUrl)) {
            urlLogoutSuccessHandler.setDefaultTargetUrl(logoutSuccessUrl);
        }
    }

    // ~ Methods
    // ========================================================================================================

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (requiresLogout(request, response)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (logger.isDebugEnabled()) {
                logger.debug("Logging out user '" + auth + "' and transferring to logout destination");
            }

            for (LogoutHandler handler : handlers) {
                handler.logout(request, response, auth);
            }

            SimpleUrlLogoutSuccessHandler tempLogoutSuccessHandler = null;
            Object princial = null;
            if (auth != null) {
                princial = auth.getPrincipal();
            }
            if (princial instanceof SuperAdminDetails) {
                tempLogoutSuccessHandler = superAdminLogoutSuccessHandler;
                tempLogoutSuccessHandler.setDefaultTargetUrl(superAdminLogoutSuccessUrl);
            } else if (princial instanceof UserDetails) {
                if (CasLoginUtils.isUseCas()) {
                    tempLogoutSuccessHandler = (SimpleUrlLogoutSuccessHandler) logoutSuccessHandler;
                } else {
                    String tenant = ((UserDetails) princial).getTenant();
                    if (!logoutSuccessHandlerMap.containsKey(tenant)) {
                        SimpleUrlLogoutSuccessHandler urlLogoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
                        logoutSuccessHandlerMap.put(tenant, urlLogoutSuccessHandler);
                    }
                    tempLogoutSuccessHandler = logoutSuccessHandlerMap.get(tenant);
                    if ("6".equals(((UserDetails) princial).getLoginType())) {
                        tempLogoutSuccessHandler.setDefaultTargetUrl("/xxgs/index");
                    } else {
                        tempLogoutSuccessHandler.setDefaultTargetUrl("/tenant/" + ((UserDetails) princial).getTenant());
                    }
                }
            } else {
                tempLogoutSuccessHandler = defaultLogoutSuccessHandler;
                tempLogoutSuccessHandler.setDefaultTargetUrl(logoutSuccessUrl);
            }

            String redirectUrl = request.getParameter("redirectUrl");
            if (StringUtils.hasText(redirectUrl)) {
                tempLogoutSuccessHandler.setDefaultTargetUrl(redirectUrl);
            }

            tempLogoutSuccessHandler.onLogoutSuccess(request, response, auth);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * @return the superAdminLogoutSuccessUrl
     */
    public String getSuperAdminLogoutSuccessUrl() {
        return superAdminLogoutSuccessUrl;
    }

    /**
     * @param superAdminLogoutSuccessUrl 要设置的superAdminLogoutSuccessUrl
     */
    public void setSuperAdminLogoutSuccessUrl(String superAdminLogoutSuccessUrl) {
        this.superAdminLogoutSuccessUrl = superAdminLogoutSuccessUrl;
    }

    /**
     * @return the logoutRequestMatcher
     */
    public RequestMatcher getLogoutRequestMatcher() {
        return logoutRequestMatcher;
    }

    public void setLogoutRequestMatcher(RequestMatcher logoutRequestMatcher) {
        Assert.notNull(logoutRequestMatcher, "logoutRequestMatcher cannot be null");
        this.logoutRequestMatcher = logoutRequestMatcher;
    }

    /**
     * Allow subclasses to modify when a logout should take place.
     *
     * @param request  the request
     * @param response the response
     * @return <code>true</code> if logout should occur, <code>false</code> otherwise
     */
    protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
        return logoutRequestMatcher.matches(request);
    }

}
