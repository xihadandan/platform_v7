/*
 * @(#)2016年6月21日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context;

import com.wellsoft.pt.app.context.support.UserAppData;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.js.JavaScriptTemplate;
import com.wellsoft.pt.app.support.PiApplication;
import com.wellsoft.pt.app.support.PiFunction;
import com.wellsoft.pt.app.support.PiModule;
import com.wellsoft.pt.app.support.PiSystem;
import com.wellsoft.pt.app.theme.Theme;

import java.io.Serializable;
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
 * 2016年6月21日.1	zhulh		2016年6月21日		Create
 * </pre>
 * @date 2016年6月21日
 */
public interface AppContext extends Serializable {

    /**
     * 获取当前用户的应用数据
     *
     * @return
     */
    UserAppData getCurrentUserAppData();

    /**
     * 设置当前用户的应用数据
     *
     * @return
     */
    void setCurrentUserAppData(UserAppData userAppData);

    // 固化数据 系统相关

    /**
     * 所有主题
     *
     * @return
     */
    List<Theme> getAllThemes();

    /**
     * 所有样式
     *
     * @return
     */
    List<CssFile> getAllCssFile();

    /**
     * 所有JS模块
     *
     * @return
     */
    List<JavaScriptModule> getAllJavaScriptModules();

    /**
     * 所有JS模板
     *
     * @return
     */
    List<JavaScriptTemplate> getAllJavaScriptTemplates();

    /**
     * 所有组件
     *
     * @return
     */
    List<UIDesignComponent> getAllComponents();

    /**
     * 所有系统
     *
     * @return
     */
    List<PiSystem> getAllSystems();

    /**
     * 所有模块
     *
     * @return
     */
    List<PiModule> getAllModules();

    /**
     * 所有应用
     *
     * @return
     */
    List<PiApplication> getAllApplications();

    /**
     * 获取主题
     *
     * @param id
     * @return
     */
    Theme getTheme(String id);

    /**
     * 获取样式
     *
     * @param id
     * @return
     */
    CssFile getCssFile(String id);

    /**
     * 获取JS模块
     *
     * @param id
     * @return
     */
    JavaScriptModule getJavaScriptModule(String id);

    /**
     * 获取JS模板
     *
     * @param id
     * @return
     */
    JavaScriptTemplate getJavaScriptTemplate(String id);

    /**
     * 获取组件
     *
     * @param wtype
     * @return
     */
    UIDesignComponent getComponent(String wtype);

    /**
     * 根据集成信息UUID，获取系统
     *
     * @param piUuid
     * @return
     */
    PiSystem getSystem(String piUuid);

    /**
     * 根据集成信息UUID，获取模块
     *
     * @param piUuid
     * @return
     */
    PiModule getModule(String piUuid);

    /**
     * 根据集成信息UUID，获取应用
     *
     * @param piUuid
     * @return
     */
    PiApplication getApplication(String piUuid);

    /**
     * 获取功能
     *
     * @param id
     * @return
     */
    PiFunction getFunction(String dataPath);

    /**
     * 是否手机应用
     *
     * @return
     */
    boolean isMobileApp();

    /**
     * 是否手机应用
     *
     * @return
     */
    void setMobileApp(boolean isMobileApp);

    /**
     * 解析导出到手机端JavaScript模块
     *
     * @param jsModuleId
     * @return
     */
    JavaScriptModule resolveExportsJavaScriptModule(String jsModuleId);

    void debug();

    boolean isDebug();
}
