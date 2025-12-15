/*
 * @(#)2016年8月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.context.support.UserAppData;
import com.wellsoft.pt.app.entity.AppSystem;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.theme.SimpleTheme;
import com.wellsoft.pt.app.theme.Theme;
import com.wellsoft.pt.app.web.interceptor.WebAppFacadeInterceptor;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月13日.1	zhulh		2016年8月13日		Create
 * </pre>
 * @date 2016年8月13日
 */
@Component
public class AppContextCurrentUserAppDataRepositoryImpl implements
        AppContextCurrentUserAppDataRepository {

    private static final String PI_PATH_REQUEST_ATTRIBUTE_NAME = WebAppFacadeInterceptor.PI_PATH_REQUEST_ATTRIBUTE_NAME;

    @Autowired
    private SecurityAuditFacadeService securityAuditFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextCurrentUserAppDataRepository#update(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.wellsoft.pt.security.core.userdetails.UserDetails, com.wellsoft.pt.app.context.AppContext)
     */
    @Override
    public void update(HttpServletRequest request, HttpServletResponse response, UserDetails user,
                       AppContext appContext) {
        UserAppData userAppData = appContext.getCurrentUserAppData();
        // 1、主题
        Theme theme = null;
        // 2、当前访问的应用路径
        String appPath = (String) request.getAttribute(PI_PATH_REQUEST_ATTRIBUTE_NAME);
        // 3、当前访问的应用集成UUID
        String piUuid = AppCacheUtils.getPiUuidByPath(appPath);
        // 3.1、当前访问的页面UUID
        String pageUuid = request.getParameter("pageUuid");
        // 4、当前使用的系统
        PiSystem system = null;
        // 5、当前使用的模块
        PiModule module = null;
        // 5.1、当前模块加载的应用
        List<PiApplication> moduleApps = null;
        // 6、当前使用的应用
        PiApplication application = null;
        // 6.1、当前应用加载的功能
        List<PiFunction> appFunctions = null;

        WebUtils.setSessionAttribute(request, "portalPageUuid", pageUuid);

        Object preferences = AppCacheUtils.getPreferences(SpringSecurityUtils.getCurrentUserId());

        // 集成信息项
        PiItem piItem = AppCacheUtils.getPiItem(piUuid);
        if (piItem == null) {
            piItem = AppCacheUtils.updatePiItem(piUuid);
        }

        // 系统UUID
        Integer appType = Integer.valueOf(piItem.getType());

        if (AppType.SYSTEM.equals(appType)) {
            system = getSystem(piItem, true);
        } else if (AppType.MODULE.equals(appType)) {
            system = getSystem(piItem, false);
            module = getModule(piItem);
            moduleApps = getModuleApps(module, piItem);
        } else if (AppType.APPLICATION.equals(appType)) {
            system = getSystem(piItem, false);
            PiItem modulePiItem = AppCacheUtils.getPiItem(piItem.getParentUuid());
            if (modulePiItem == null) {
                modulePiItem = AppCacheUtils.updatePiItem(piItem.getParentUuid());
            }
            // 应用挂在模块下，而不是系统下
            if (AppType.MODULE.equals(Integer.valueOf(modulePiItem.getType()))) {
                module = getModule(modulePiItem);
                moduleApps = getModuleApps(module, modulePiItem);
            }
            application = getApplication(piItem);
            appFunctions = getAppFunctions(application, piItem);
        }
        HttpSession httpSession = request.getSession();
        AppSystem appSystem = AppCacheUtils.getAppSystemById(system.getId());
        if (!Config.isBackendServer()) {
            theme = ThemeHelper.getTheme(request, appContext, appSystem.getTheme());
            userAppData.setTheme(theme);
        } else {
            // 返回前端服务解析相关样式
            userAppData.setTheme(new SimpleTheme(appSystem.getTheme(), null, null, null, 0));
        }
        userAppData.setTheme(theme);
        userAppData.setAppPath(appPath);
        userAppData.setPiUuid(piUuid);
        userAppData.setPageUuid(pageUuid);
        userAppData.setSystem(system);
        userAppData.setModule(module);
        userAppData.setModuleApps(moduleApps);
        userAppData.setPreferences(preferences);
        userAppData.setApplication(application);
        userAppData.setAppFunctions(appFunctions);
        userAppData.setSessionId(httpSession.getId());
    }

    /**
     * @param piItem
     * @return
     */
    private PiSystem getSystem(PiItem piItem, boolean isSystemPi) {
        if (isSystemPi) {
            return AppCacheUtils.getPiSystem(piItem.getUuid());
        }

        String sysPiPath = piItem.getPath();
        String[] paths = StringUtils.split(sysPiPath, Separator.SLASH.getValue());
        sysPiPath = Separator.SLASH.getValue() + paths[0];
        return AppCacheUtils.getPiSystem(AppCacheUtils.getPiUuidByPath(sysPiPath));
    }

    /**
     * @param piItem
     * @return
     */
    private PiModule getModule(PiItem piItem) {
        return AppCacheUtils.getPiModule(piItem.getUuid());
    }

    /**
     * @param module
     * @param modulePiItem
     * @return
     */
    private List<PiApplication> getModuleApps(PiModule module, PiItem piItem) {
        List<PiApplication> apps = AppCacheUtils.getPiAppsByModule(piItem.getUuid());
        List<PiApplication> grantedApps = new ArrayList<PiApplication>();
        // 权限过滤
        for (PiApplication piApplication : apps) {
            if (securityAuditFacadeService.isGranted(piApplication.getPiUuid(),
                    AppFunctionType.AppProductIntegration)) {
                grantedApps.add(piApplication);
            }
        }
        return grantedApps;
    }

    /**
     * @param piItem
     * @return
     */
    private PiApplication getApplication(PiItem piItem) {
        return AppCacheUtils.getPiApplication(piItem.getUuid());
    }

    /**
     * @param application
     * @param piItem
     * @return
     */
    private List<PiFunction> getAppFunctions(PiApplication application, PiItem piItem) {
        List<PiFunction> functions = AppCacheUtils.getPiFunctionsByApp(piItem.getUuid());
        List<PiFunction> grantedFunctions = new ArrayList<PiFunction>();
        // 权限过滤
        for (PiFunction piFunction : functions) {
            if (securityAuditFacadeService.isGranted(
                    AppCacheUtils.getPiUuidByPath(piFunction.getPath()),
                    AppFunctionType.AppProductIntegration)) {
                grantedFunctions.add(piFunction);
            }
        }
        return grantedFunctions;
    }

}
