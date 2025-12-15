/*
 * @(#)2016年8月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context.support;

import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.theme.Theme;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 动态数据 用户相关
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
public interface UserAppData extends Serializable {

    /**
     * 获取当前用户使用的主题
     *
     * @return
     */
    Theme getTheme();

    /**
     * 设置当前用户使用的主题
     *
     * @param theme
     */
    void setTheme(Theme theme);

    /**
     * 获取用户当前访问的集成信息路径
     *
     * @return
     */
    String getAppPath();

    /**
     * 设置用户当前访问的集成信息路径
     *
     * @return
     */
    void setAppPath(String appPath);

    /**
     * 获取用户当前访问的集成信息路径
     *
     * @return
     */
    String getFinalPartOfAppPath();

    /**
     * 获取用户当前访问的集成信息UUID
     *
     * @return
     */
    String getPiUuid();

    /**
     * 设置用户当前访问的集成信息UUID
     *
     * @return
     */
    void setPiUuid(String piUuid);

    /**
     * 获取用户当前访问的页面UUID
     *
     * @return
     */
    String getPageUuid();

    /**
     * 设置用户当前访问的页面UUID
     *
     * @return
     */
    void setPageUuid(String pageUuid);

    /**
     * 获取用户当前访问的系统
     *
     * @return
     */
    PiSystem getSystem();

    /**
     * 设置当前用户当前访问的系统
     *
     * @param system
     */
    void setSystem(PiSystem system);

    /**
     * 获取当前用户访问的模块
     *
     * @return
     */
    PiModule getModule();

    /**
     * 设置当前用户访问的模块
     *
     * @param module
     */
    void setModule(PiModule module);

    /**
     * 获取当前模块加载的应用
     *
     * @return
     */
    List<PiApplication> getModuleApps();

    /**
     * 设置当前模块加载的应用
     *
     * @param moduleApps
     */
    void setModuleApps(List<PiApplication> moduleApps);

    /**
     * 获取当前用户访问的应用
     *
     * @return
     */
    PiApplication getApplication();

    /**
     * 设置当前用户访问的应用
     *
     * @param application
     */
    void setApplication(PiApplication application);

    /**
     * 获取当前应用加载的功能
     *
     * @return
     */
    List<PiFunction> getAppFunctions();

    /**
     * 设置当前应用加载的功能
     *
     * @param appFunctions
     */
    void setAppFunctions(List<PiFunction> appFunctions);

    /**
     * 根据系统ID获取对应的集成信息项
     *
     * @return
     */
    List<PiItem> getPiItems(String systemId);

    /**
     * 获取当前用户可访问的所有系统
     *
     * @return
     */
    List<PiSystem> getSystems();

    /**
     * 获取当前用户可访问某个系统的所有模块
     *
     * @param systemId
     * @return
     */
    List<PiModule> getModules(String systemId);

    /**
     * 获取当前用户可访问某个系统的所有应用
     *
     * @param systemId
     * @return
     */
    List<PiApplication> getApplications(String systemId);

    /**
     * 获取当前用户可访问某个系统的所有功能
     *
     * @param systemId
     * @return
     */
    List<PiFunction> getFunctions(String systemId);

    /**
     * 获取用户配置数据,path存在则
     *
     * @param path
     * @return
     */
    Object getPreferences();

    /**
     * 设置用户配置数据
     *
     * @param preferences
     */
    void setPreferences(Object preferences);

    /**
     * 转化为JSON字符串
     *
     * @return
     */
    String toJsonString();

    String getSessionId();

    void setSessionId(String sessionId);

}
