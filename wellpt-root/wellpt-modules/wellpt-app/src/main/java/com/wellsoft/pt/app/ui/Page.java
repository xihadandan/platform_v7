/*
 * @(#)2016年3月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui;

import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.theme.Theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Description: 页面解析接口
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
public interface Page extends View {

    /**
     * 获取页面文档类型
     *
     * @return
     */
    String getDocType();

    /**
     * 获取页面文档元数据
     *
     * @return
     */
    List<String> getMetas();

    /**
     * 获取页面的应用环境
     *
     * @return
     */
    Map<String, Object> getEnvironment();

    /**
     * 页面使用的主题
     *
     * @return
     */
    Theme getTheme(AppContext appContext, HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取要加载的CSS文件
     *
     * @return
     */
    List<CssFile> getRenderCssFiles(AppContext appContext, HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取依赖的JS文件
     *
     * @return
     */
    List<JavaScriptModule> getRequireJavaScripts(AppContext appContext, HttpServletRequest request,
                                                 HttpServletResponse response);

    /**
     * 获取页面包含的组件
     *
     * @return
     */
    List<View> getViews();

    /**
     * 获取页面需要渲染的组件
     *
     * @return
     */
    List<View> getRenderViews();

    /**
     * 渲染页面需要渲染的组件
     *
     * @param template
     * @param appContext
     * @param request
     * @param response
     * @return
     */
    String renderViews(String template, AppContext appContext, HttpServletRequest request, HttpServletResponse response);

    /**
     * 页面是否可缓存
     *
     * @return
     */
    boolean isCacheable();

}
