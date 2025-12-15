/*
 * @(#)2016年5月10日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.component;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.html.Tag;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.ui.Component;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 页面组件定义抽象类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月10日.1	zhulh		2016年5月10日		Create
 * </pre>
 * @date 2016年5月10日
 */
public abstract class UIDesignComponent implements Component, Ordered {


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getViewClass()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends View> getViewClass() {
        return DefaultWidgetDefinitionProxyView.class;
    }

    /**
     * 组件定义加载的js模块
     *
     * @return
     */
    public abstract JavaScriptModule getJavaScriptModule();

    /**
     * 页面设计嚣添加组件时的预览html
     *
     * @return
     */
    public String getPreviewHtml() {
        return Tag.DIV_START + Tag.DIV_END;
    }

    /**
     * 加载的CSS文件，针对设计页面
     *
     * @return
     */
    public List<CssFile> getDefineCssFiles() {
        return Collections.emptyList();
    }

    /**
     * 获取依赖的JS文件，针对解析页面
     *
     * @return
     */
    public List<JavaScriptModule> getExplainJavaScriptModules(AppContext appContext, View view,
                                                              HttpServletRequest request, HttpServletResponse response) {
        return Collections.emptyList();
    }

    /**
     * 加载的CSS文件，针对解析页面
     * 已过时，使用getExplainCssFiles(View view, AppContext appContext, HttpServletRequest request, HttpServletResponse response)
     *
     * @return
     */
    @Deprecated
    public List<CssFile> getExplainCssFiles() {
        return Collections.emptyList();
    }

    /**
     * 加载的CSS文件，针对解析页面
     *
     * @return
     */
    public List<CssFile> getExplainCssFiles(AppContext appContext, View view, HttpServletRequest request,
                                            HttpServletResponse response) {
        List<CssFile> cssFiles = getExplainCssFiles();
        return cssFiles;
    }

    /**
     * 是否可配置属性
     *
     * @return
     */
    public boolean isConfigurable() {
        return true;
    }

    /**
     * 默认配置项
     *
     * @return
     */
    public Map<String, Object> getDefaultOptions() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(AppConstants.KEY_WTYPE, getType());
        return options;
    }

    /**
     * 通过Key加载CssFile
     *
     * @param key
     * @param defaultValue
     * @return
     */
    protected CssFile getCssFile(String key, String defaultValue) {
        String cssId = Config.getValue(key, defaultValue);
        return AppContextHolder.getContext().getCssFile(cssId);
    }

    /**
     * 通过Key加载JavaScriptModule
     *
     * @param key
     * @param defaultValue
     * @return
     */
    protected JavaScriptModule getJavaScriptModule(String key, String defaultValue) {
        String id = Config.getValue(key, defaultValue);
        return AppContextHolder.getContext().getJavaScriptModule(id);
    }

}
