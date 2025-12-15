/*
 * @(#)2016年8月12日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.support.CurrentUserAppData;
import com.wellsoft.pt.app.context.support.UserAppData;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月12日.1	zhulh		2016年8月12日		Create
 * </pre>
 * @date 2016年8月12日
 */
@Component
public class AppContextRepositoryImpl implements AppContextRepository {

    public static final String APP_CONTEXT_KEY = "APP_CONTEXT";

    public static final String USER_APP_DATA_CONTEXT_KEY = "USER_APP_DATA_CONTEXT";

    @Autowired
    private AppContextCurrentUserAppDataRepository appContextCurrentUserAppDataRepository;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextRepository#loadContext(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public AppContext loadContext(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession(false);

        AppContext appContext = Config.isBackendServer() ? null : readAppContextFromSession(httpSession);
        if (appContext == null) {
            appContext = AppContextHolder.getContext();
        }
        UserAppData userAppData = Config.isBackendServer() ? null : readUserAppDataFromSession(httpSession);
        if (userAppData == null) {
            userAppData = generateNewUserAppData();
        }
        appContext.setCurrentUserAppData(userAppData);
        if ("true".equalsIgnoreCase(request.getHeader("debug"))) {
            appContext.debug();
        }

        UserDetails user = SpringSecurityUtils.getCurrentUser();
        // 更新当前用户的应用数据
        appContextCurrentUserAppDataRepository.update(request, response, user, appContext);

        return appContext;
    }

    /**
     * 从session中读取应用上下文集
     *
     * @param httpSession
     * @return
     */
    private AppContext readAppContextFromSession(HttpSession httpSession) {
        if (httpSession == null) {
            return null;
        }

        // 从session中读取上下文数据
        Object appContext = httpSession.getAttribute(APP_CONTEXT_KEY);

        if (appContext == null) {
            return null;
        }

        // 类型判断
        if (!(appContext instanceof AppContext)) {
            return null;
        }

        return (AppContext) appContext;
    }

    /**
     * @param httpSession
     * @return
     */
    private UserAppData readUserAppDataFromSession(HttpSession httpSession) {
        if (httpSession == null) {
            return null;
        }

        // 从session中读取上下文数据
        Object userAppData = httpSession.getAttribute(USER_APP_DATA_CONTEXT_KEY);

        if (userAppData == null) {
            return generateNewUserAppData();
        }

        // 类型判断
        if (!(userAppData instanceof UserAppData)) {
            return null;
        }

        return (UserAppData) userAppData;
    }

    /**
     * @return
     */
    private UserAppData generateNewUserAppData() {
        return new CurrentUserAppData();
    }

    /**
     * @return
     */
    private AppContext generateNewContext() {
        return AppContextHolder.createEmptyContext();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextRepository#saveContext(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void saveContext(AppContext appContext, HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession(false);
        Authentication authentication = SpringSecurityUtils.getAuthentication();

        // 用户未登录
        if (authentication == null) {
            if (httpSession != null) {
                httpSession.removeAttribute(APP_CONTEXT_KEY);
                httpSession.removeAttribute(USER_APP_DATA_CONTEXT_KEY);
            }
            return;
        }

        // 用户已登录
        if (httpSession != null) {
            UserAppData userAppData = appContext.getCurrentUserAppData();
            if (httpSession.getAttribute(APP_CONTEXT_KEY) == null) {
                httpSession.setAttribute(APP_CONTEXT_KEY, appContext);
            }
            if (httpSession.getAttribute(USER_APP_DATA_CONTEXT_KEY) == null) {
                httpSession.setAttribute(USER_APP_DATA_CONTEXT_KEY, userAppData);
            }
        }
    }

}
