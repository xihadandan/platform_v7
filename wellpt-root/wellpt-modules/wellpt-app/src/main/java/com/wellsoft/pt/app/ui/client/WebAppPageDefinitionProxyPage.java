/*

 * @(#)2016年5月16日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.support.RequireJsHelper;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.app.theme.Theme;
import com.wellsoft.pt.app.transform.AppPageDefinitionJson2HtmlTransformer;
import com.wellsoft.pt.app.transform.Page2HtmlTransformer;
import com.wellsoft.pt.app.ui.AbstractPage;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.Widget;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 默认的页面解析代理
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
public final class WebAppPageDefinitionProxyPage extends AbstractPage {

    private String jsModule;
    private List<Widget> views = new ArrayList<Widget>();
    private List<View> renderViews = new ArrayList<View>(0);

    private Page2HtmlTransformer page2HtmlTransformer;

    /**
     * @param definitionJson
     * @throws Exception
     */
    public WebAppPageDefinitionProxyPage(String definitionJson) throws Exception {
        super(definitionJson);
    }

    /**
     * @param id
     * @param title
     * @param definitionJson
     * @throws Exception
     */
    public WebAppPageDefinitionProxyPage(String id, String title, String theme, String jsModule, String definitionJson)
            throws Exception {
        super(id, title, theme, definitionJson);
        this.jsModule = jsModule;
        this.views.addAll(WidgetDefinitionUtils.extractWidgets(this));
        for (Widget widget : this.views) {
            if (!widget.useDefinitionHtml()) {
                this.renderViews.add(widget);
            }
        }
    }

    public WebAppPageDefinitionProxyPage(String id, String title, String theme, boolean parseJSON, String definitionJson)
            throws Exception {
        super(id, title, theme, definitionJson, parseJSON);
        this.views.addAll(WidgetDefinitionUtils.extractWidgets(this));
        for (Widget widget : this.views) {
            if (!widget.useDefinitionHtml()) {
                this.renderViews.add(widget);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractPage#getRenderCssFiles(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public List<CssFile> getRenderCssFiles(AppContext appContext, HttpServletRequest request,
                                           HttpServletResponse response) {
        // 加载CSS
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        // 1、页面定义组件解析时的CSS
        for (View view : views) {
            UIDesignComponent component = appContext.getComponent(((Widget) view).getWtype());
            if (component != null) {
                cssFiles.addAll(component.getExplainCssFiles(appContext, view, request, response));
            }
        }

        // 2、页面主题的CSS
        // 页面主题
        Theme theme = this.getTheme(appContext, request, response);
        if (theme != null && theme.getCssFiles() != null) {
            cssFiles.addAll(theme.getCssFiles());
        }
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractPage#getRequireJavaScripts(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public List<JavaScriptModule> getRequireJavaScripts(AppContext appContext, HttpServletRequest request,
                                                        HttpServletResponse response) {
        // 加载JS模块
        List<JavaScriptModule> javaScriptModules = new ArrayList<JavaScriptModule>();
        // 1、JS模块中加载的JS
        if (StringUtils.isNotBlank(jsModule)) {
            String[] jsModules = StringUtils.split(jsModule, Separator.SEMICOLON.getValue());
            for (String module : jsModules) {
                javaScriptModules.add(AppContextHolder.getContext().getJavaScriptModule(module));
            }
        }

        // 2、页面定义组件的JS模块、组件解析依赖的JS模块
        for (View view : views) {
            String wtype = ((Widget) view).getWtype();
            // 页面定义组件的JS模块
            String wtypeJavaScriptModule = WidgetDefinitionUtils.getJavaScriptModuleByWtype(wtype);
            JavaScriptModule javaScriptModule = appContext.getJavaScriptModule(wtypeJavaScriptModule);
            // 组件解析依赖的JS模块
            UIDesignComponent component = appContext.getComponent(wtype);
            if (component != null) {
                List<JavaScriptModule> dependencies = component.getExplainJavaScriptModules(appContext, view, request,
                        response);
                if (CollectionUtils.isNotEmpty(dependencies)) {
                    javaScriptModule = RequireJsHelper.clone(javaScriptModule);
                    javaScriptModule.getDependencies().addAll(RequireJsHelper.getJavaScriptModuleIds(dependencies));
                    javaScriptModules.addAll(dependencies);
                }
            }
            javaScriptModules.add(javaScriptModule);
        }

        // 3、页面主题的JS模块
        // 页面主题
        Theme theme = this.getTheme(appContext, request, response);
        if (theme != null && theme.getJavaScriptModules() != null) {
            javaScriptModules.addAll(theme.getJavaScriptModules());
        }
        return javaScriptModules;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#getViews()
     */
    @Override
    public List<View> getViews() {
        List<View> views = new ArrayList<View>();
        views.addAll(this.views);
        return views;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractPage#getRenderViews()
     */
    @Override
    public List<View> getRenderViews() {
        return renderViews;
    }

    /**
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.app.ui.View#render(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String render(AppContext appContext, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (this.page2HtmlTransformer == null) {
            this.page2HtmlTransformer = new AppPageDefinitionJson2HtmlTransformer(this);
        }
        return page2HtmlTransformer.transform(this, appContext, request, response);
    }

}
