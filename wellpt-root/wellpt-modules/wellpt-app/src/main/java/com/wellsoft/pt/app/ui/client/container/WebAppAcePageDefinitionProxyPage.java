/*
 * @(#)2016-09-21 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.container;

import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.html.Tag;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.ui.AbstractPage;
import com.wellsoft.pt.app.ui.client.WebAppPageDefinitionProxyPage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: Ace页面解析代理
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
public final class WebAppAcePageDefinitionProxyPage extends AbstractPage {

    private String wAcePageId;

    private String wHeaderId;
    private String wHeaderHtml;

    private String wLeftSidebarId;
    private String wLeftSidebarHtml;

    private String wDashboardId;
    private String wDashboardHtml;

    private String wFooterId;
    private String wFooterHtml;

    private WebAppPageDefinitionProxyPage proxyPage;

    /**
     * @param definitionJson
     * @throws Exception
     */
    public WebAppAcePageDefinitionProxyPage(String definitionJson) throws Exception {
        super(definitionJson);
    }

    /**
     * @param id
     * @param title
     * @param theme
     * @param jsModule
     * @param definitionJson
     * @throws Exception
     */
    public WebAppAcePageDefinitionProxyPage(String id, String title, String theme, String jsModule,
                                            String definitionJson) throws Exception {
        // 装饰填充页面JSON定义
        proxyPage = new WebAppPageDefinitionProxyPage(id, title, theme, jsModule,
                decorateJsonDefinition(definitionJson));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractPage#getEnvironment()
     */
    @Override
    public Map<String, Object> getEnvironment() {
        Map<String, Object> evn = super.getEnvironment();
        // 窗口模式，模态窗口
        evn.put(AppConstants.KEY_WINDOW_MODEL, AppConstants.WINDOW_MODEL_MODAL);
        return evn;
    }

    /**
     * @throws JSONException
     */
    private String decorateJsonDefinition(String definitionJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(definitionJson);
        wAcePageId = jsonObject.getString("id");
        // 头部
        JSONObject header = jsonObject.getJSONObject("header");
        String headerWidgetDefUuid = header.getString("uuid");
        AppWidgetDefinition headerWidgetDefinition = AppCacheUtils.getAppWidgetDefinition(headerWidgetDefUuid);
        JSONObject headerWidget = new JSONObject(headerWidgetDefinition.getDefinitionJson());
        wHeaderId = headerWidget.getString("id");
        wHeaderHtml = headerWidgetDefinition.getHtml();
        header.put("id", wHeaderId);

        // 左导航
        JSONObject leftSidebar = jsonObject.getJSONObject("leftSidebar");
        String leftSidebarWidgetDefUuid = leftSidebar.getString("uuid");
        AppWidgetDefinition leftSidebarWidgetDefinition = AppCacheUtils
                .getAppWidgetDefinition(leftSidebarWidgetDefUuid);
        JSONObject leftSidebarWidget = new JSONObject(leftSidebarWidgetDefinition.getDefinitionJson());
        wLeftSidebarId = leftSidebarWidgetDefinition.getId();
        wLeftSidebarHtml = leftSidebarWidgetDefinition.getHtml();
        leftSidebar.put("id", wLeftSidebarId);

        // 仪表盘
        JSONObject dashboard = jsonObject.getJSONObject("dashboard");
        String dashboardWidgetDefUuid = dashboard.getString("uuid");
        AppWidgetDefinition dashboardWidgetDefinition = AppCacheUtils.getAppWidgetDefinition(dashboardWidgetDefUuid);
        JSONObject dashboardWidget = new JSONObject(dashboardWidgetDefinition.getDefinitionJson());
        wDashboardId = dashboardWidgetDefinition.getId();
        wDashboardHtml = dashboardWidgetDefinition.getHtml();
        dashboard.put("id", wDashboardId);

        // 底部
        JSONObject footer = jsonObject.getJSONObject("footer");
        String footerWidgetDefUuid = footer.getString("uuid");
        AppWidgetDefinition footerWidgetDefinition = AppCacheUtils.getAppWidgetDefinition(footerWidgetDefUuid);
        JSONObject footerWidget = new JSONObject(footerWidgetDefinition.getDefinitionJson());
        wFooterId = footerWidgetDefinition.getId();
        wFooterHtml = footerWidgetDefinition.getHtml();
        footer.put("id", wFooterId);

        // 组成页面的组件
        JSONArray items = new JSONArray();
        items.put(headerWidget);
        items.put(leftSidebarWidget);
        items.put(dashboardWidget);
        items.put(footerWidget);
        jsonObject.put("items", items);
        return jsonObject.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.client.WebAppPageDefinitionProxyPage#render(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String render(AppContext appContext, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // 加载CSS
        List<CssFile> cssFiles = this.proxyPage.getRenderCssFiles(appContext, request, response);
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("aceCssFiles", CssHelper.getCssImport(request, cssFiles));

        // 加载require config js
        List<JavaScriptModule> javaScriptModules = this.proxyPage.getRequireJavaScripts(appContext, request, response);
        List<JavaScriptModule> configScriptModules = new ArrayList<JavaScriptModule>();
        configScriptModules.addAll(javaScriptModules);
        // 加载系统下配置的JS模块
        PiSystem piSystem = appContext.getCurrentUserAppData().getSystem();
        configScriptModules.addAll(RequireJsHelper.getJavaScriptModules(AppCacheUtils
                .getJavaScriptModuleFunctionBySystem(piSystem)));
        StringBuilder sb = new StringBuilder();
        sb.append(Tag.SCRIPT_START);
        sb.append(RequireJsHelper.getConfigScript(request, configScriptModules));
        sb.append(Tag.SCRIPT_END);
        root.put("configScript", sb.toString());

        // 应用
        // 加载JS
        sb = new StringBuilder();
        sb.append(Tag.SCRIPT_START);
        String webAppInitScript = WebAppScriptHelper.generateInitScript(request, this.getEnvironment(),
                this.proxyPage.getDefinitionJson(), cssFiles,
                RequireJsHelper.getJavaScriptTemplates(AppCacheUtils.getJavaScriptTemplateFunctionBySystem(piSystem)),
                appContext.getCurrentUserAppData());
        sb.append(webAppInitScript);
        // 回调JS脚本
        StringBuilder callbackScript = new StringBuilder();// "require([ \"/resources/pt/js/app/app.js\" ]);";
        callbackScript.append("appContext.init(WebApp.currentUserAppData);");
        callbackScript.append("require([ \"pt/js/app/app\" ]);");
        // 加载JS模块
        sb.append("require([\"jquery\", \"jquery-migrate\"], function($) {");
        sb.append(RequireJsHelper.getRequireScript(javaScriptModules, callbackScript.toString()));
        sb.append("});");
        sb.append(Tag.SCRIPT_END);
        root.put("requireScript", sb.toString());

        root.put("wAcePageId", wAcePageId);
        root.put("wHeaderId", wHeaderId);
        root.put("wHeaderHtml", wHeaderHtml);
        root.put("wLeftSidebarId", wLeftSidebarId);
        root.put("wLeftSidebarHtml", wLeftSidebarHtml);
        root.put("wDashboardId", wDashboardId);
        root.put("wDashboardHtml", wDashboardHtml);
        root.put("wFooterId", wFooterId);
        root.put("wFooterHtml", wFooterHtml);
        String template = HtmlTemplateHelper.getHtml("html_ace", root, request);
        return this.proxyPage.renderViews(template, appContext, request, response);
    }

}
