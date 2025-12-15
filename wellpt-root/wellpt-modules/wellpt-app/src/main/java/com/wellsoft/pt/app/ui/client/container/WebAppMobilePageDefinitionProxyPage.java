/*
 * @(#)2016-09-21 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.container;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.html.Tag;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.ui.AbstractPage;
import com.wellsoft.pt.app.ui.client.WebAppPageDefinitionProxyPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 手机App页面解析代理
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-21.1	zhulh		2016-09-21		Create
 * </pre>
 * @date 2016-09-21
 */
public final class WebAppMobilePageDefinitionProxyPage extends AbstractPage {

    private static final String JS_MODULE_JQUERY = "jquery";
    private static final String JS_MODULE_MOBILE_APP = "mobileApp";

    private WebAppPageDefinitionProxyPage proxyPage;

    /**
     * @param definitionJson
     * @throws Exception
     */
    public WebAppMobilePageDefinitionProxyPage(String definitionJson) throws Exception {
        super(definitionJson);
    }

    public WebAppMobilePageDefinitionProxyPage(String id, String title, String theme, boolean parseJSON, String definitionJson)
            throws Exception {
        proxyPage = new WebAppPageDefinitionProxyPage(id, title, theme, parseJSON, definitionJson);
    }

    /**
     * @param id
     * @param title
     * @param theme
     * @param jsModule
     * @param definitionJson
     * @throws Exception
     */
    public WebAppMobilePageDefinitionProxyPage(String id, String title, String theme, String jsModule,
                                               String definitionJson) throws Exception {
        // 装饰填充页面JSON定义
        proxyPage = new WebAppPageDefinitionProxyPage(id, title, theme, jsModule, definitionJson);
    }

    @Override
    public String getId() {
        return proxyPage.getId();
    }

    @Override
    public String getTitle() {
        return proxyPage.getTitle();
    }

    @Override
    public String getUuid() {
        return proxyPage.getUuid();
    }

    @Override
    public String getDefinitionJson() throws Exception {
        return proxyPage.getDefinitionJson();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractPage#getEnvironment()
     */
    @Override
    public Map<String, Object> getEnvironment() {
        Map<String, Object> evn = super.getEnvironment();
        // 窗口模式，手机窗口
        evn.put(AppConstants.KEY_IS_MOBILE_APP, true);
        evn.put(AppConstants.KEY_WINDOW_MODEL, AppConstants.WINDOW_MODEL_MOBILE);
        return evn;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractPage#getRenderCssFiles(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public List<CssFile> getRenderCssFiles(AppContext appContext, HttpServletRequest request,
                                           HttpServletResponse response) {
        String muiId = Config.getValue("pt.css.mui.id", "mui");
        CssFile mui = AppContextHolder.getContext().getCssFile(muiId);
        String muiAppId = Config.getValue("pt.css.mui-app.id", "mui-app");
        CssFile muiApp = AppContextHolder.getContext().getCssFile(muiAppId);
        String muiAppUxdId = Config.getValue("pt.css.mui-app-uxd.id", "mui-app-uxd");
        CssFile muiUxdApp = AppContextHolder.getContext().getCssFile(muiAppUxdId);
        String muiDtpickerId = Config.getValue("pt.css.mui-dtpicker.id", "mui-dtpicker");
        CssFile muiDtpicker = AppContextHolder.getContext().getCssFile(muiDtpickerId);
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(mui);
        cssFiles.add(muiApp);
        cssFiles.add(muiUxdApp);
        cssFiles.add(muiDtpicker);
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.client.WebAppPageDefinitionProxyPage#render(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String render(AppContext appContext, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        boolean isMobileApp = appContext.isMobileApp();
        try {
            appContext.setMobileApp(true);
            // 标题
            Map<String, Object> root = new HashMap<String, Object>();
            root.put(AppConstants.KEY_CTX, request.getContextPath());
            root.put(AppConstants.KEY_TITLE, this.proxyPage.getTitle());

            // html内容
            root.put(AppConstants.KEY_HTML, this.proxyPage.getAttribute(AppConstants.KEY_HTML));

            // 加载CSS
            List<CssFile> cssFiles = this.getRenderCssFiles(appContext, request, response);
            root.put("mobileCssFiles", CssHelper.getCssImport(request, cssFiles));

            // 加载require config js
            List<JavaScriptModule> javaScriptModules = this.proxyPage.getRequireJavaScripts(appContext, request,
                    response);
            javaScriptModules = RequireJsHelper.sortWithResolveDependency(javaScriptModules);
            // javaScriptModules.remove(appContext.getJavaScriptModule(JS_MODULE_JQUERY));
            List<JavaScriptModule> configScriptModules = new ArrayList<JavaScriptModule>();
            configScriptModules.addAll(javaScriptModules);
            // 加载系统下配置的JS模块
            PiSystem piSystem = appContext.getCurrentUserAppData().getSystem();
            configScriptModules.addAll(RequireJsHelper.getJavaScriptModules(AppCacheUtils
                    .getJavaScriptModuleFunctionBySystem(piSystem)));
            StringBuilder sb = new StringBuilder();
            sb.append(Tag.SCRIPT_START);
            sb.append(RequireJsHelper.getConfigScript(request, configScriptModules, false));
            sb.append(Tag.SCRIPT_END);
            root.put("configScript", sb.toString());

            // 应用
            // 加载JS
            sb = new StringBuilder();
            sb.append(Tag.SCRIPT_START);
            String webAppInitScript = WebAppScriptHelper.generateInitScript(request, this.getEnvironment(),
                    this.proxyPage.getDefinitionJson(), cssFiles, RequireJsHelper.getJavaScriptTemplates(AppCacheUtils
                            .getJavaScriptTemplateFunctionBySystem(piSystem)), appContext.getCurrentUserAppData(),
                    false, false);
            sb.append(webAppInitScript);
            // 回调JS脚本
            StringBuilder callbackScript = new StringBuilder();// "require([ \"/resources/pt/js/app/app.js\" ]);";
            // callbackScript.append("mui('body').wMobilePage({})");
            callbackScript.append("appContext.init(WebApp.currentUserAppData);");
            callbackScript.append("require([ctx + \"" + appContext.getJavaScriptModule(JS_MODULE_MOBILE_APP).getPath()
                    + ".js\" ]);");
            // 加载JS模块
            sb.append("require([\"jquery\"], function($) {");
            sb.append(RequireJsHelper.getRequireScript(javaScriptModules, callbackScript.toString(), false));
            sb.append("});");
            sb.append(Tag.SCRIPT_END);
            root.put("requireScript", sb.toString());
            String template = HtmlTemplateHelper.getHtml("html_mobile", root, request);
            String responseBody = this.proxyPage.renderViews(template, appContext, request, response);
            // 页面缓存
            if (this.proxyPage.isCacheable()) {
                AppCacheUtils.setAppPage(appContext.getCurrentUserAppData(), responseBody);
            }
            return responseBody;
        } finally {
            appContext.setMobileApp(isMobileApp);
        }
    }
}
