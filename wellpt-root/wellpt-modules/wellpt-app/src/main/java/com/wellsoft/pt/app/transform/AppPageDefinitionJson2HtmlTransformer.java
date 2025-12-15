/*
 * @(#)2016年5月16日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.transform;

import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.support.UserAppData;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.html.Tag;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.js.JavaScriptTemplate;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.ui.AbstractPage;
import com.wellsoft.pt.app.ui.Page;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * 2016年5月16日.1	zhulh		2016年5月16日		Create
 * </pre>
 * @date 2016年5月16日
 */
public class AppPageDefinitionJson2HtmlTransformer implements Page2HtmlTransformer {
    private static final String APP_JS_MODULE = "pt/js/app/app";
    private String pageDefinitionJson;
    private String html;

    /**
     * @param definitionJson
     */
    public AppPageDefinitionJson2HtmlTransformer(Page page) {
        try {
            PageTransformerHolder.page(page);
            parseJson(page);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            PageTransformerHolder.clear();
        }
    }

    /**
     * @param definitionJson
     * @throws Exception
     */
    private void parseJson(Page page) throws Exception {
        if (page instanceof AbstractPage) {
            String pageUuid = ((AbstractPage) page).getUuid();
        }
        JSONObject jsonObject = new JSONObject(page.getDefinitionJson());
        this.html = jsonObject.getString(AppConstants.KEY_HTML);
        jsonObject.remove(AppConstants.KEY_HTML);
        this.pageDefinitionJson = jsonObject.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.app.transform.Page2HtmlTransformer#transform(com.wellsoft.pt.app.ui.Page, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String transform(Page page, AppContext appContext, HttpServletRequest request,
                            HttpServletResponse response)
            throws Exception {
        String ctx = request.getContextPath();
        UserAppData userAppData = appContext.getCurrentUserAppData();
        StringBuilder sb = new StringBuilder();
        sb.append(page.getDocType());
        sb.append(Tag.HTML_START);
        // meta
        sb.append(Tag.HEAD_START);
        for (String meta : page.getMetas()) {
            sb.append(meta);
        }
        // title
        sb.append(Tag.TITLE_START);
        sb.append(page.getTitle());
        sb.append(Tag.TITLE_END);

        // 加载require js
        List<JavaScriptModule> javaScriptModules = page.getRequireJavaScripts(appContext, request,
                response);
        List<JavaScriptModule> configScriptModules = new ArrayList<JavaScriptModule>();
        configScriptModules.addAll(javaScriptModules);
        // 加载系统下配置的JS模块
        PiSystem piSystem = userAppData.getSystem();
        configScriptModules.addAll(RequireJsHelper.getJavaScriptModules(AppCacheUtils
                .getJavaScriptModuleFunctionBySystem(piSystem)));
        configScriptModules.add(appContext.getJavaScriptModule("pace"));

        // 加载系统下配置的JS模板
        List<JavaScriptTemplate> javaScriptTemplates = RequireJsHelper.getJavaScriptTemplates(
                AppCacheUtils
                        .getJavaScriptTemplateFunctionBySystem(piSystem));

        // 加载CSS的模块
        List<CssFile> cssFiles = page.getRenderCssFiles(appContext, request, response);
        // 加载JS模块要加载的CSS的模块
        configScriptModules = RequireJsHelper.sortWithResolveDependency(configScriptModules);
        CssHelper.addJavaScriptModuleCssFiles(configScriptModules, cssFiles);
        // 加载系统下配置的CSS模块
        cssFiles.addAll(CssHelper.getCssFiles(
                AppCacheUtils.getFunctionsBySystem(AppFunctionType.CssFile, piSystem)));

        // 生成加载CSS的模块HTML
        sb.append(CssHelper.getCssImport(request, cssFiles));
        // IE8兼容性处理代码
        sb.append(WebAppScriptHelper.getHtml5SupportScript(appContext, request));
        // 加载require.js
        sb.append("<script src=\"" + ctx
                + appContext.getJavaScriptModule(
                AppConstants.REQUIRE_JS_MODULE).getConfusePath() + ".js\"></script>");
        // 加载require config js
        sb.append(Tag.SCRIPT_START);
        sb.append(RequireJsHelper.getConfigScript(request, configScriptModules, false));
        sb.append(Tag.SCRIPT_END);
        sb.append(Tag.HEAD_END);

        // 添加集成路径的的最后一部分(系统、模块、应用)ID作为页面全局样式类
        String cssClass = userAppData.getFinalPartOfAppPath();
        sb.append("<body class='" + cssClass + "'>");
        // sb.append(Tag.BODY_START);
        // 加载组件页面
        sb.append(html);
        // 加载JS
        String appJsModule = APP_JS_MODULE;
        sb.append(getReadyScript(page, appContext, cssFiles, javaScriptModules, javaScriptTemplates,
                appJsModule,
                request, response));
        sb.append(Tag.BODY_END);
        sb.append(Tag.HTML_END);

        String responseBody = page.renderViews(sb.toString(), appContext, request, response);
        // 页面缓存
        if (page.isCacheable()) {
            AppCacheUtils.setAppPage(userAppData, responseBody);
        }
        return responseBody;
    }

    /**
     * @param page
     * @param appContext
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    private String getReadyScript(Page page, AppContext appContext, List<CssFile> cssFiles,
                                  List<JavaScriptModule> javaScriptModules,
                                  List<JavaScriptTemplate> javaScriptTemplates, String appJsModule,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        UserAppData userAppData = appContext.getCurrentUserAppData();
        StringBuilder sb = new StringBuilder();
        sb.append(Tag.SCRIPT_START);
        String webAppInitScript = WebAppScriptHelper.generateInitScript(request,
                page.getEnvironment(),
                pageDefinitionJson, cssFiles, javaScriptTemplates, userAppData);
        sb.append(webAppInitScript);
        // 回调JS脚本
        StringBuilder callbackScript = new StringBuilder();// "require([ \"/resources/pt/js/app/app.js\" ]);";
        callbackScript.append("appContext.init(WebApp.currentUserAppData);");
        callbackScript.append("require([ \"" + appJsModule + "\" ]);");
        // 加载JS模块
        sb.append(RequireJsHelper.getRequireScript(javaScriptModules, callbackScript.toString()));
        sb.append(Tag.SCRIPT_END);
        return sb.toString();
    }

}
