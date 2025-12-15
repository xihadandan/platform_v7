/*
 * @(#)2016年8月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context.support;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.theme.Theme;
import org.apache.commons.lang.StringUtils;

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
 * 2016年8月13日.1	zhulh		2016年8月13日		Create
 * </pre>
 * @date 2016年8月13日
 */
public class CurrentUserAppData implements UserAppData {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1003090832527455239L;
    // 当前模块加载的应用
    List<PiApplication> moduleApps;
    // 当前应用加载的功能
    List<PiFunction> appFunctions;
    // 当前主题
    private Theme theme;
    // 当前集成信息路径
    private String appPath;
    // 当前集成信息UUID
    private String piUuid;
    // 当前访问的页面UUID
    private String pageUuid;
    // 当前系统
    private PiSystem system;
    // 当前模块
    private PiModule module;
    // 当前应用
    private PiApplication application;
    // 当前用户的配置数据
    private Object preferences;
    // 当前用户的会话Id
    private String sessionId;

    // 可访问的所有系统
    private List<PiSystem> systems;
    // 可访问的所有系统->模块
    private Map<String, List<PiModule>> moduleMap;
    // 可访问的所有系统->应用
    private Map<String, List<PiApplication>> applicationMap;
    // 可访问的所有系统->功能
    private Map<String, List<PiFunction>> functionMap;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#getTheme()
     */
    @Override
    public Theme getTheme() {
        return this.theme;
    }

    /**
     * @param theme 要设置的theme
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    /**
     * @return the appPath
     */
    public String getAppPath() {
        return appPath;
    }

    /**
     * @param appPath 要设置的appPath
     */
    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#getFinalPartOfAppPath()
     */
    @Override
    public String getFinalPartOfAppPath() {
        if (StringUtils.isBlank(appPath)) {
            return StringUtils.EMPTY;
        }
        String[] paths = StringUtils.split(appPath, Separator.SLASH.getValue());
        if (paths.length > 0) {
            return paths[paths.length - 1];
        }
        return StringUtils.EMPTY;
    }

    /**
     * @return the piUuid
     */
    public String getPiUuid() {
        return piUuid;
    }

    /**
     * @param piUuid 要设置的piUuid
     */
    public void setPiUuid(String piUuid) {
        this.piUuid = piUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#getPageUuid()
     */
    @Override
    public String getPageUuid() {
        return this.pageUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#setPageUuid(java.lang.String)
     */
    @Override
    public void setPageUuid(String pageUuid) {
        this.pageUuid = pageUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.UserAppData#getSystem()
     */
    @Override
    public PiSystem getSystem() {
        return this.system;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#setSystem(com.wellsoft.pt.app.support.PiSystem)
     */
    @Override
    public void setSystem(PiSystem system) {
        this.system = system;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.UserAppData#getModule()
     */
    @Override
    public PiModule getModule() {
        return this.module;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#setModule(com.wellsoft.pt.app.support.PiModule)
     */
    @Override
    public void setModule(PiModule module) {
        this.module = module;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#getModuleApps()
     */
    @Override
    public List<PiApplication> getModuleApps() {
        return this.moduleApps;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#setModuleApps(java.util.List)
     */
    @Override
    public void setModuleApps(List<PiApplication> moduleApps) {
        this.moduleApps = moduleApps;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.UserAppData#getApplication()
     */
    @Override
    public PiApplication getApplication() {
        return this.application;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#setApplication(com.wellsoft.pt.app.support.PiApplication)
     */
    @Override
    public void setApplication(PiApplication application) {
        this.application = application;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#getAppFunctions()
     */
    @Override
    public List<PiFunction> getAppFunctions() {
        return this.appFunctions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#setAppFunctions(java.util.List)
     */
    @Override
    public void setAppFunctions(List<PiFunction> appFunctions) {
        this.appFunctions = appFunctions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#getPiItems(java.lang.String)
     */
    @Override
    public List<PiItem> getPiItems(String systemId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.UserAppData#getSystems()
     */
    @Override
    public List<PiSystem> getSystems() {
        return this.systems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.UserAppData#getModules(java.lang.String)
     */
    @Override
    public List<PiModule> getModules(String systemId) {
        return moduleMap.get(systemId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.UserAppData#getApplications(java.lang.String)
     */
    @Override
    public List<PiApplication> getApplications(String systemId) {
        return applicationMap.get(systemId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.UserAppData#getFunctions(java.lang.String)
     */
    @Override
    public List<PiFunction> getFunctions(String systemId) {
        return functionMap.get(systemId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#getPreferences()
     */
    @Override
    public Object getPreferences() {
        return preferences;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#setPreferences(java.lang.Object)
     */
    @Override
    public void setPreferences(Object preferences) {
        this.preferences = preferences;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#toJsonString()
     */
    @Override
    public String toJsonString() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appPath", appPath);
        values.put("piUuid", piUuid);
        values.put("pageUuid", pageUuid);
        values.put("system", system);
        values.put("module", module);
        values.put("moduleApps", moduleApps);
        values.put("preferences", preferences);
        values.put("application", application);
        values.put("appFunctions", appFunctions);
        return JsonUtils.object2Json(values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#getSessionId()
     */
    @Override
    public String getSessionId() {
        return sessionId;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.support.UserAppData#setSessionId(java.lang.String)
     */
    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
