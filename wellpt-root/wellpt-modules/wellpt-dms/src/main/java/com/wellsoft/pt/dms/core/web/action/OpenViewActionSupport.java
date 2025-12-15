/*
 * @(#)Feb 28, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.action;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.html.Tag;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.dms.config.support.Configuration;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.View;
import com.wellsoft.pt.dms.core.web.ViewResolver;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * Feb 28, 2017.1	zhulh		Feb 28, 2017		Create
 * </pre>
 * @date Feb 28, 2017
 */
@Component
public class OpenViewActionSupport extends ActionSupport implements InitializingBean {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8055916639589998252L;

    @Autowired
    private List<ViewResolver> viewResolvers;

    /**
     * @param model
     * @param request
     * @param response
     */
    protected void mergeOutputModel(Model model, ActionContext actionContext, HttpServletRequest request,
                                    HttpServletResponse response) {
        // 标题
        String title = request.getParameter("documentTitle");
        if (StringUtils.isBlank(title)) {
            title = actionContext.getDocumentTitle(model.asMap().get("documentData"));
        }
        if (StringUtils.isNotBlank(title)) {
            model.addAttribute("title", title);
        }

        // 单据二开模块
        Configuration configuration = getActionContext().getConfiguration();
        String jsModule = configuration.getDocument(actionContext, request).getJsModule();
        if (StringUtils.isNotBlank(jsModule)) {
            model.addAttribute("documentViewModule", jsModule);
        }
        if (!model.containsAttribute("documentViewModule")) {
            model.addAttribute("documentViewModule", "DmsDocumentView");
        }
        model.addAttribute("documentViewCss", getDocumentViewCss((String) model.asMap().get("documentViewModule")));
        model.addAttribute("dms_id", getActionContext().getDmsId());

        // 应用初始化脚本
        StringBuilder sb = new StringBuilder();
        sb.append(Tag.SCRIPT_START);
        sb.append("var WebApp = WebApp || {};");
        sb.append("WebApp.widgetDefinitions = {};");
        sb.append("WebApp.widgetDefinitions['" + getActionContext().getDmsId() + "'] = "
                + JsonUtils.object2Json(configuration) + ";");
        model.addAttribute("widgetConfigurationJSON", JsonUtils.object2Json(configuration));

        // 附加参数
        Map<String, String[]> paramMap = request.getParameterMap();
        Map<String, Object> extraParams = new HashMap<String, Object>();
        for (String paramName : paramMap.keySet()) {
            if (paramName.startsWith("ep_")) {
                String paramValue = request.getParameter(paramName);
                extraParams.put(paramName, paramValue);
            }
        }
        getExtraParams(model).putAll(extraParams);
        // model.addAttribute("extraParams", extraParams);

        sb.append("WebApp.extraParams = " + JsonUtils.object2Json(extraParams) + ";");
        model.addAttribute("extraParamsJSON", JsonUtils.object2Json(extraParams));
        sb.append(Tag.SCRIPT_END);
        model.addAttribute("dmsAppInitScript", sb.toString());
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getExtraParams(Model model) {
        Map<String, Object> extraParams = new HashMap<String, Object>(0);
        if (model.containsAttribute("extraParams")) {
            extraParams = (Map<String, Object>) model.asMap().get("extraParams");
        } else {
            model.addAttribute("extraParams", extraParams);
        }
        return extraParams;
    }

    /**
     * 如何描述该方法
     *
     * @param jsModule
     */
    private String getDocumentViewCss(String jsModule) {
        List<String> cssModules = new ArrayList<String>();
        cssModules.add("animate");
        JavaScriptModule javaScriptModule = AppContextHolder.getContext().getJavaScriptModule(jsModule);
        if (javaScriptModule != null) {
            Set<CssFile> cssFiles = javaScriptModule.getCssFiles();
            for (CssFile cssFile : cssFiles) {
                cssModules.add(cssFile.getId());
            }
        }
        return StringUtils.join(cssModules, Separator.COMMA.getValue());
    }

    /**
     * @param request
     * @return
     */
    protected View resolveView(HttpServletRequest request) {
        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveView(getActionContext(), request);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        OrderComparator.sort(this.viewResolvers);
    }

}
