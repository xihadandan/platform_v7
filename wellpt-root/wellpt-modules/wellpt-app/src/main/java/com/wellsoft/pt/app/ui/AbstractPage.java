/*
 * @(#)2016年3月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui;

import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.html.DocType;
import com.wellsoft.pt.app.html.Meta;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.theme.Theme;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

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
 * 2016年3月30日.1	zhulh		2016年3月30日		Create
 * </pre>
 * @date 2016年3月30日
 */
public abstract class AbstractPage extends DefaultWidgetDefinitionProxyView implements Page {

    private static List<String> metas = new ArrayList<String>();

    static {
        metas.add(Meta.CONTENT_TYPE);
        metas.add(Meta.VIEWPORT);
        metas.add(Meta.X_UA_COMPATIBLE);
        metas.add(Meta.RENDERER);
    }

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private String id;

    private String title;

    private String theme;

    private String uuid;

    /**
     * @throws Exception
     */
    public AbstractPage() throws Exception {
        this(AppConstants.JSON_EMPTY);
    }

    /**
     * @param definitionJson
     * @throws Exception
     */
    public AbstractPage(String definitionJson) throws Exception {
        super(definitionJson);
        if (super.jsonObject.has("uuid")) {
            this.uuid = super.jsonObject.getString("uuid");
        }
    }

    public AbstractPage(String id, String title, String theme,
                        String definitionJson, boolean parseJSON) throws Exception {
        super(definitionJson, parseJSON);
        this.id = id;
        this.title = title;
        this.theme = theme;
        if (super.jsonObject.has("uuid")) {
            this.uuid = super.jsonObject.getString("uuid");
        }
    }

    /**
     * @param id
     * @param title
     */
    public AbstractPage(String id, String title, String theme,
                        String definitionJson) throws Exception {
        super(definitionJson);
        this.id = id;
        this.title = title;
        this.theme = theme;
        if (super.jsonObject.has("uuid")) {
            this.uuid = super.jsonObject.getString("uuid");
        }
    }

    public String getUuid() {
        return this.uuid;
    }

    /**
     * @return the id
     */
    public String getId() {
        if (StringUtils.isBlank(this.id)) {
            return super.getId();
        }
        return this.id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        if (StringUtils.isBlank(this.title)) {
            return super.getTitle();
        }
        return this.title;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.View#useDefinitionHtml()
     */
    @Override
    public boolean useDefinitionHtml() {
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#getDocType()
     */
    @Override
    public String getDocType() {
        return DocType.HTML5;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#getMetas()
     */
    @Override
    public List<String> getMetas() {
        return metas;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#getEnvironment()
     */
    @Override
    public Map<String, Object> getEnvironment() {
        return new HashMap<String, Object>();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#getTheme(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public Theme getTheme(AppContext appContext, HttpServletRequest request,
                          HttpServletResponse response) {
        // 页面主题
        Theme currentTheme = appContext.getCurrentUserAppData().getTheme();
        if (currentTheme != null) {
            return currentTheme;
        }
        if (StringUtils.isBlank(this.theme)) {
            this.theme = AppConstants.THEME_DEFAULT;
        }
        // 页面设置的主题添加到Session
        WebUtils.setSessionAttribute(request, AppConstants.KEY_APP_THEME, this.theme);
        return AppContextHolder.getContext().getTheme(this.theme);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#getRenderCssFiles(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public List<CssFile> getRenderCssFiles(AppContext appContext, HttpServletRequest request,
                                           HttpServletResponse response) {
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#getRequireJavaScripts(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public List<JavaScriptModule> getRequireJavaScripts(AppContext appContext,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#getViews()
     */
    @Override
    public List<View> getViews() {
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#getRenderViews()
     */
    @Override
    public List<View> getRenderViews() {
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#renderViews(java.lang.String, com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String renderViews(String template, AppContext appContext, HttpServletRequest request,
                              HttpServletResponse response) {
        List<View> renderViews = getRenderViews();
        // 不需要渲染组件直接返回生成的HTML
        if (renderViews.isEmpty()) {
            return template;
        }

        // 渲染组件
        Document doc = Jsoup.parse(template);
        for (View view : renderViews) {
            try {
                Element element = doc.getElementById(view.getId());
                if (element == null) {
                    logger.error("组件ID为" + view.getId() + "的页面元素不存在，忽略掉！");
                    continue;
                }
                String renderHtml = view.render(appContext, request, response);
                renderHtml = StringUtils.isBlank(renderHtml) ? StringUtils.EMPTY : renderHtml;
                element.html(renderHtml);
            } catch (Exception e) {
                String errorMsg = "组件" + view.getTitle() + "(" + view.getId() + ")" + "渲染报错！";
                logger.error(errorMsg, e);
            }
        }
        return doc.html();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Page#isCacheable()
     */
    public boolean isCacheable() {
        return getRenderViews().isEmpty();
    }

}
