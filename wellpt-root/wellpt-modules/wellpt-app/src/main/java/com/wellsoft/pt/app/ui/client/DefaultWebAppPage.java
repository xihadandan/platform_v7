/*
 * @(#)2016年3月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.css.SimpleCssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.theme.Theme;
import com.wellsoft.pt.app.transform.DefaultPage2HtmlTransformer;
import com.wellsoft.pt.app.transform.Page2HtmlTransformer;
import com.wellsoft.pt.app.ui.AbstractPage;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.Widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月30日.1	zhulh		2016年3月30日		Create
 * </pre>
 * @date 2016年3月30日
 */
public class DefaultWebAppPage extends AbstractPage {

    private Page2HtmlTransformer transformer = new DefaultPage2HtmlTransformer();
    private List<View> views = new ArrayList<View>();

    /**
     * @param definitionJson
     * @throws Exception
     */
    public DefaultWebAppPage(String definitionJson) throws Exception {
        super(definitionJson);
    }

    public DefaultWebAppPage addView(View view) {
        views.add(view);
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#getViews()
     */
    @Override
    public List<View> getViews() {
        return views;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.View#render(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String render(AppContext appContext, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return transformer.transform(this, appContext, request, response);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#getTheme(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public Theme getTheme(AppContext appContext, HttpServletRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractPage#getRenderCssFiles(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public List<CssFile> getRenderCssFiles(AppContext appContext, HttpServletRequest request,
                                           HttpServletResponse response) {
        // bootstrap
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        CssFile bootstrap = new SimpleCssFile("/resources/bootstrap/3.3.5/css/bootstrap.min.css", 1);
        CssFile jqueryui = new SimpleCssFile("/resources/jqueryui/1.11.4/jquery-ui.min.css", 2);
        CssFile datetimepicker = new SimpleCssFile("/resources/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css", 3);
        cssFiles.add(bootstrap);
        cssFiles.add(jqueryui);
        cssFiles.add(datetimepicker);
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractPage#getRequireJavaScripts()
     */
    @Override
    public List<JavaScriptModule> getRequireJavaScripts(AppContext appContext, HttpServletRequest request,
                                                        HttpServletResponse response) {
        return super.getRequireJavaScripts(appContext, request, response);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.View#getDefinitionJson()
     */
    @Override
    public String getDefinitionJson() {
        Map<String, Object> definitions = new HashMap<String, Object>();
        definitions.put("id", "home");
        definitions.put("title", "我的主页");
        definitions.put("theme", "theme");
        definitions.put("wtype", "wPage");
        definitions.put("layout", "layout");
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        Map<String, Object> header = new HashMap<String, Object>();
        header.put("id", "wHeader");
        header.put("wtype", "wHeader");
        Map<String, Object> dashboard = new HashMap<String, Object>();
        dashboard.put("id", "wDashboard");
        dashboard.put("wtype", "wDashboard");
        Map<String, Object> footer = new HashMap<String, Object>();
        footer.put("id", "wHtml");
        footer.put("wtype", "wHtml");
        items.add(header);
        items.add(dashboard);
        items.add(footer);
        definitions.put("items", items);
        return JsonUtils.object2Json(definitions);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Widget#getItems()
     */
    @Override
    public List<Widget> getItems() {
        // TODO Auto-generated method stub
        return null;
    }

}
