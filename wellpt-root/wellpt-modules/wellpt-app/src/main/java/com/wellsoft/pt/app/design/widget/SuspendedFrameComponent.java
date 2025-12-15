/*
 * @(#)2016年8月29日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.widget;

import com.google.common.collect.Lists;
import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.client.widget.SuspendedFrameWidget;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年3月4日.1	wangrf		2020年3月4日		Create
 * </pre>
 * @date 2020年3月4日
 */
@Component
public class SuspendedFrameComponent extends UIDesignComponent {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wSuspendedFrame";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getName()
     */
    @Override
    public String getName() {
        return "悬浮框组件";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getCategory()
     */
    @Override
    public ComponentCategory getCategory() {
        return SimpleComponentCategory.APP;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 45;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        // return getJavaScriptModule("pt.js.module.widget_suspended_frame.id", "widget_suspended_frame");
        String id = Config.getValue("pt.js.module.widget_suspended_frame.id", "widget_suspended_frame");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getDefaultOptions()
     */
    @Override
    public Map<String, Object> getDefaultOptions() {
        // TODO
        Map<String, Object> options = super.getDefaultOptions();
        options.put("configuration", new HashMap<String, Object>());
        return options;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends View> getViewClass() {
        return SuspendedFrameWidget.class;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getDefineCssFiles()
     */
    @Override
    public List<CssFile> getDefineCssFiles() {
        // TODO
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        return cssFiles;
    }

    /**
     * 根据组件定义，加载解析时需要的JS模块
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getExplainJavaScriptModules(com.wellsoft.pt.app.context.AppContext, com.wellsoft.pt.app.ui.View, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public List<JavaScriptModule> getExplainJavaScriptModules(AppContext appContext, View view,
                                                              HttpServletRequest request, HttpServletResponse response) {
        // TODO
        List<JavaScriptModule> javaScriptModules = Lists.newArrayList();
        return javaScriptModules;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getExplainCssFiles()
     */
    @Override
    public List<CssFile> getExplainCssFiles() {
        // TODO
        return getDefineCssFiles();
    }
}
