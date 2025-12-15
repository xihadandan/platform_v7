/*
 * @(#)2016年8月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context;

import com.wellsoft.context.util.ApplicationContextHolder;
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
import org.apache.commons.lang.BooleanUtils;

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
public class AppContextCurrentUserProxyImpl extends AbstractAppContext {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2381719919838150315L;

    private UserAppData userAppData;


    private ThreadLocal<Boolean> DEBUG_HOLDER = new ThreadLocal<Boolean>();

    /**
     * @param appContext
     */
    public AppContextCurrentUserProxyImpl() {
        super();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getCurrentUserAppData()
     */
    @Override
    public UserAppData getCurrentUserAppData() {
        return userAppData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#setCurrentUserAppData(com.wellsoft.pt.app.context.support.UserAppData)
     */
    @Override
    public void setCurrentUserAppData(UserAppData userAppData) {
        this.userAppData = userAppData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllThemes()
     */
    @Override
    public List<Theme> getAllThemes() {
        return ApplicationContextHolder.getBean(AppContext.class).getAllThemes();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllCssFile()
     */
    @Override
    public List<CssFile> getAllCssFile() {
        return ApplicationContextHolder.getBean(AppContext.class).getAllCssFile();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllJavaScriptModules()
     */
    @Override
    public List<JavaScriptModule> getAllJavaScriptModules() {
        return ApplicationContextHolder.getBean(AppContext.class).getAllJavaScriptModules();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllJavaScriptTemplates()
     */
    @Override
    public List<JavaScriptTemplate> getAllJavaScriptTemplates() {
        return ApplicationContextHolder.getBean(AppContext.class).getAllJavaScriptTemplates();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllComponents()
     */
    @Override
    public List<UIDesignComponent> getAllComponents() {
        return ApplicationContextHolder.getBean(AppContext.class).getAllComponents();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllSystems()
     */
    @Override
    public List<PiSystem> getAllSystems() {
        return ApplicationContextHolder.getBean(AppContext.class).getAllSystems();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllModules()
     */
    @Override
    public List<PiModule> getAllModules() {
        return ApplicationContextHolder.getBean(AppContext.class).getAllModules();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getAllApplications()
     */
    @Override
    public List<PiApplication> getAllApplications() {
        return ApplicationContextHolder.getBean(AppContext.class).getAllApplications();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getTheme(java.lang.String)
     */
    @Override
    public Theme getTheme(String id) {
        return ApplicationContextHolder.getBean(AppContext.class).getTheme(id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getCssFile(java.lang.String)
     */
    @Override
    public CssFile getCssFile(String id) {
        return ApplicationContextHolder.getBean(AppContext.class).getCssFile(id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getJavaScriptModule(java.lang.String)
     */
    @Override
    public JavaScriptModule getJavaScriptModule(String id) {
        return ApplicationContextHolder.getBean(AppContext.class).getJavaScriptModule(id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getJavaScriptTemplate(java.lang.String)
     */
    @Override
    public JavaScriptTemplate getJavaScriptTemplate(String id) {
        return ApplicationContextHolder.getBean(AppContext.class).getJavaScriptTemplate(id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getComponent(java.lang.String)
     */
    @Override
    public UIDesignComponent getComponent(String wtype) {
        return ApplicationContextHolder.getBean(AppContext.class).getComponent(wtype);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getSystem(java.lang.String)
     */
    @Override
    public PiSystem getSystem(String piUuid) {
        return ApplicationContextHolder.getBean(AppContext.class).getSystem(piUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getModule(java.lang.String)
     */
    @Override
    public PiModule getModule(String piUuid) {
        return ApplicationContextHolder.getBean(AppContext.class).getModule(piUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getApplication(java.lang.String)
     */
    @Override
    public PiApplication getApplication(String piUuid) {
        return ApplicationContextHolder.getBean(AppContext.class).getApplication(piUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#getFunction(java.lang.String)
     */
    @Override
    public PiFunction getFunction(String piUuid) {
        return ApplicationContextHolder.getBean(AppContext.class).getFunction(piUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#isMobileApp()
     */
    @Override
    public boolean isMobileApp() {
        return ApplicationContextHolder.getBean(AppContext.class).isMobileApp();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#setMobileApp(boolean)
     */
    @Override
    public void setMobileApp(boolean isMobileApp) {
        ApplicationContextHolder.getBean(AppContext.class).setMobileApp(isMobileApp);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContext#resolveExportsJavaScriptModule(java.lang.String)
     */
    @Override
    public JavaScriptModule resolveExportsJavaScriptModule(String jsModuleId) {
        return ApplicationContextHolder.getBean(AppContext.class).resolveExportsJavaScriptModule(jsModuleId);
    }

    @Override
    public void debug() {
        DEBUG_HOLDER.set(true);
    }

    @Override
    public boolean isDebug() {
        return BooleanUtils.isTrue(DEBUG_HOLDER.get());
    }
}
