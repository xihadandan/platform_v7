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
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.theme.Theme;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
@Component
public class AppContextImpl extends AbstractAppContext implements InitializingBean {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -921768291723525676L;
    private static final ThreadLocal<Boolean> isMobileAppHolder = new NamedThreadLocal<Boolean>("Is mobile app context");
    private static Map<String, JavaScriptModule> shareJavaScriptModuleMap = new ConcurrentHashMap<String, JavaScriptModule>();
    private List<Theme> themes;
    private Map<String, Theme> themeMap;
    private List<JavaScriptModule> javaScriptModules;
    private Map<String, JavaScriptModule> javaScriptModuleMap;
    private Map<String, JavaScriptTemplate> javaScriptTemplateMaps;
    private List<CssFile> cssFiles;
    private Map<String, CssFile> cssFileMap;
    @Autowired
    private AppContextConfiguration appContextConfiguration;
    @Autowired(required = false)
    private List<JavaScriptTemplate> javaScriptTemplates = new ArrayList<JavaScriptTemplate>();
    @Autowired
    private List<UIDesignComponent> components;
    private Map<String, UIDesignComponent> componentMap;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getCurrentUserAppData()
     */
    @Override
    public UserAppData getCurrentUserAppData() {
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#setCurrentUserAppData(com.wellsoft.pt.app.context.support.UserAppData)
     */
    @Override
    public void setCurrentUserAppData(UserAppData userAppData) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getTheme(java.lang.String)
     */
    @Override
    public Theme getTheme(String id) {
        return themeMap.get(id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllThemes()
     */
    @Override
    public List<Theme> getAllThemes() {
        return themes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getJavaScriptModule(java.lang.String)
     */
    @Override
    public JavaScriptModule getJavaScriptModule(String id) {
        JavaScriptModule result = null;
        if (isMobileApp() && (result = resolveExportsJavaScriptModule(id)) != null) {
            return result;
        }
        return javaScriptModuleMap.get(id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getJavaScriptTemplate(java.lang.String)
     */
    @Override
    public JavaScriptTemplate getJavaScriptTemplate(String id) {
        return javaScriptTemplateMaps.get(id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllJavaScriptModules()
     */
    @Override
    public List<JavaScriptModule> getAllJavaScriptModules() {
        return javaScriptModules;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllJavaScriptTemplates()
     */
    @Override
    public List<JavaScriptTemplate> getAllJavaScriptTemplates() {
        return javaScriptTemplates;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getCssFile(java.lang.String)
     */
    @Override
    public CssFile getCssFile(String name) {
        return cssFileMap.get(name);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllCssFile()
     */
    @Override
    public List<CssFile> getAllCssFile() {
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getComponent(java.lang.String)
     */
    @Override
    public UIDesignComponent getComponent(String wtype) {
        return componentMap != null ? componentMap.get(wtype) : null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllComponents()
     */
    @Override
    public List<UIDesignComponent> getAllComponents() {
        return components;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
//        appContextConfiguration.configure();
//        themes = appContextConfiguration.getAllThemes();
//        javaScriptModules = appContextConfiguration.getAllJavaScriptModules();
//        cssFiles = appContextConfiguration.getAllCssFile();
//        javaScriptTemplates.addAll(appContextConfiguration.getAllJavaScriptTemplats());
//        themeMap = ConvertUtils.convertElementToMap(themes, AppConstants.KEY_ID);
//        javaScriptModuleMap = ConvertUtils.convertElementToMap(javaScriptModules, AppConstants.KEY_ID);
//        cssFileMap = ConvertUtils.convertElementToMap(cssFiles, AppConstants.KEY_ID);
//        javaScriptTemplateMaps = ConvertUtils.convertElementToMap(javaScriptTemplates, AppConstants.KEY_ID);
//        componentMap = ConvertUtils.convertElementToMap(components, "type");
//        // 设置模块映射关系
//        List<JavaScriptModule> javaScriptModules = getAllJavaScriptModules();
//        for (JavaScriptModule javaScriptModule : javaScriptModules) {
//            String id = javaScriptModule.getId();
//            String exports = javaScriptModule.getExports();
//            if (StringUtils.equals(id, exports)) {
//                continue;
//            }
//            shareJavaScriptModuleMap.put(exports, javaScriptModule);
//        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllSystems()
     */
    @Override
    public List<PiSystem> getAllSystems() {
        return AppCacheUtils.getAllPiSystems();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllModules()
     */
    @Override
    public List<PiModule> getAllModules() {
        return AppCacheUtils.getAllPiModules();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllApplications()
     */
    @Override
    public List<PiApplication> getAllApplications() {
        return AppCacheUtils.getAllApplications();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getSystem(java.lang.String)
     */
    @Override
    public PiSystem getSystem(String piUuid) {
        return AppCacheUtils.getPiSystem(piUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getModule(java.lang.String)
     */
    @Override
    public PiModule getModule(String piUuid) {
        return AppCacheUtils.getPiModule(piUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getApplication(java.lang.String)
     */
    @Override
    public PiApplication getApplication(String piUuid) {
        return AppCacheUtils.getPiApplication(piUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getFunction(java.lang.String)
     */
    @Override
    public PiFunction getFunction(String piUuid) {
        return AppCacheUtils.getPiFunction(piUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#isMobileApp()
     */
    @Override
    public boolean isMobileApp() {
        return Boolean.TRUE.equals(isMobileAppHolder.get());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#setMobileApp(boolean)
     */
    @Override
    public void setMobileApp(boolean isMobileApp) {
        isMobileAppHolder.set(isMobileApp);
    }

    @Override
    public JavaScriptModule resolveExportsJavaScriptModule(String jsModuleId) {
        return shareJavaScriptModuleMap.get(jsModuleId);
    }


}
