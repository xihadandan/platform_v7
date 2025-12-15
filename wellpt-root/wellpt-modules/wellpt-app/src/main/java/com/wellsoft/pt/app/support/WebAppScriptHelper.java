/*
 * @(#)2016-10-26 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.context.support.UserAppData;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.js.JavaScriptTemplate;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-10-26.1	zhulh		2016-10-26		Create
 * </pre>
 * @date 2016-10-26
 */
public class WebAppScriptHelper {

    private static final String MSIE = "msie";
    private static final String MSIE_11 = "rv:11";

    private static final String JS_MODULE_HTML5SHIV = "html5shiv";
    private static final String JS_MODULE_RESPOND = "respond";

    private static final String APP_VERSION_UPGRADE_TIP = "app_version_upgrade_tip";
    private static String appVersionUpgradeTip = null;

    /**
     * IE8兼容性处理代码
     *
     * @return
     */
    public static String getHtml5SupportScript(AppContext appContext, HttpServletRequest request) {
        if (!isUserAgentOfMsie(request)) {
            return StringUtils.EMPTY;
        }
        String ctx = request.getContextPath();
        String html5shivPath = appContext.getJavaScriptModule(JS_MODULE_HTML5SHIV).getConfusePath();
        String respondPath = appContext.getJavaScriptModule(JS_MODULE_RESPOND).getConfusePath();
        StringBuilder sb = new StringBuilder();
        sb.append("<!--[if lt IE 9]>");
        sb.append("<script src=\"" + ctx + html5shivPath + ".js\"></script>");
        sb.append("<script src=\"" + ctx + respondPath + ".js\"></script>");
        sb.append("<![endif]-->");
        return sb.toString();
    }

    /**
     * @param request
     * @return
     */
    public static boolean isUserAgentOfMsie(HttpServletRequest request) {
        String userAgent = ServletUtils.getUserAgent(request);
        if (StringUtils.isBlank(userAgent) || !(StringUtils.lowerCase(userAgent).contains(MSIE)
                || StringUtils.contains(userAgent, MSIE_11))) {
            return false;
        }
        return true;
    }

    /**
     * @return
     * @throws Exception
     */
    public static String generateInitScript(HttpServletRequest request, Map<String, Object> environment,
                                            String pageDefinitionJson, List<CssFile> cssFiles, List<JavaScriptTemplate> javaScriptTemplates,
                                            UserAppData currentUserAppData) throws Exception {
        return generateInitScript(request, environment, pageDefinitionJson, cssFiles, javaScriptTemplates,
                currentUserAppData, true, false);
    }

    /**
     * @return
     * @throws Exception
     */
    public static String generateInitScript(HttpServletRequest request, Map<String, Object> environment,
                                            String pageDefinitionJson, List<CssFile> cssFiles, List<JavaScriptTemplate> javaScriptTemplates,
                                            UserAppData currentUserAppData, boolean loadUpgradeTip, boolean loadPace) throws Exception {
        String ctx = request.getContextPath();
        if (environment == null) {
            environment = new HashMap<String, Object>();
        }
        Map<String, String> loadedCssFiles = Collections.emptyMap();
        if (cssFiles != null) {
            loadedCssFiles = new HashMap<String, String>(0);
            for (CssFile cssFile : cssFiles) {
                if (cssFile != null) {
                    loadedCssFiles.put(cssFile.getId(), cssFile.getId());
                }
            }
        }
        if (javaScriptTemplates == null) {
            javaScriptTemplates = new ArrayList<JavaScriptTemplate>();
        }
        environment.put(AppConstants.KEY_WEB_APP_VERSION, AppConstants.WEB_APP_VERSION);
        String environmentJson = environment == null ? AppConstants.JSON_EMPTY : JsonUtils.object2Json(environment);
        String definitionJson = pageDefinitionJson == null ? AppConstants.JSON_EMPTY : pageDefinitionJson;
        String currentUserAppDataJson = currentUserAppData == null ? AppConstants.JSON_EMPTY : currentUserAppData
                .toJsonString();
        StringBuilder sb = new StringBuilder();
        sb.append("window.ctx = \"" + ctx + "\";");
        sb.append("var WebApp = WebApp || {};");
        sb.append("WebApp.environment = " + environmentJson + ";");
        sb.append("WebApp.pageDefinition = WebApp.pageDefiniton = " + definitionJson + ";");
        sb.append("WebApp.loadedCssFiles = " + JsonUtils.object2Json(loadedCssFiles) + ";");
        sb.append("WebApp.jsTemplates = "
                + JsonUtils.object2Json(ConvertUtils.convertElementToMap(javaScriptTemplates, AppConstants.KEY_ID))
                + ";");
        sb.append("WebApp.currentUserAppData = " + currentUserAppDataJson + ";");

        // 请求信息
        Map<String, Object> requestInfo = new HashMap<String, Object>();
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        requestInfo.put("scheme", request.getScheme());
        requestInfo.put("serverPort", serverPort);
        requestInfo.put("serverName", serverName);
        requestInfo.put("contextPath", contextPath);
        requestInfo.put("basePath", scheme + "://" + serverName + ":" + serverPort + contextPath);
        sb.append("WebApp.requestInfo = " + JsonUtils.object2Json(requestInfo) + ";");

        // 客户端版本检测提示更新
        if (loadUpgradeTip) {
            if (appVersionUpgradeTip == null) {
                appVersionUpgradeTip = HtmlTemplateHelper.getJavaScript(APP_VERSION_UPGRADE_TIP, null, request);
            }
            sb.append(appVersionUpgradeTip);
        }

        // 浏览器加载进度条
        if (loadPace) {
            List<JavaScriptModule> paceModules = new ArrayList<JavaScriptModule>();
            StringBuilder paceCallback = new StringBuilder();
            paceModules.add(AppContextHolder.getContext().getJavaScriptModule("pace"));
            paceModules.add(AppContextHolder.getContext().getJavaScriptModule("constant"));
            paceCallback.append("var pace = require('pace');");
            paceCallback.append("pace.start();");
            sb.append(RequireJsHelper.getRequireScript(paceModules, paceCallback.toString()));
        }
        return sb.toString();
    }
}
