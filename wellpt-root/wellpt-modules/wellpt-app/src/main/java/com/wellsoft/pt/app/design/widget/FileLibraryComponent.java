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
import com.wellsoft.pt.app.support.WebAppScriptHelper;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.client.widget.BootstrapTableWidget;
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
 * @author liuting
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年3月17日.1	liuting		2020年3月17日		Create
 * </pre>
 * @date 2020年3月17日
 */
@Component
public class FileLibraryComponent extends UIDesignComponent {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wFileLibrary";
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
        return "文件夹目录维护组件";
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
        return 55;
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
        String id = Config.getValue("pt.js.module.widget_file_library.id", "widget_file_library");
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
        return BootstrapTableWidget.class;
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
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(getCssFile("pt.css.select2.id", "select2"));
        cssFiles.add(getCssFile("pt.css.bootstrapTable.id", "bootstrapTable"));
        cssFiles.add(getCssFile("pt.css.bootstrap-editable.id", "bootstrap-editable"));
        cssFiles.add(getCssFile("pt.css.bootstrap-datetimepicker.id", "bootstrap-datetimepicker"));
        cssFiles.add(getCssFile("pt.css.bootstrap-table-label-mark.id", "bootstrap-table-label-mark"));
        cssFiles.add(getCssFile("pt.css.bootstrap-table-fixed-columns.id", "bootstrap-table-fixed-columns"));
        cssFiles.add(getCssFile("pt.css.wellBtnLib.id", "wellBtnLib"));
//		cssFiles.add(getCssFile("pt.css.bootstrap-table-page-jump-to.id", "bootstrap-table-page-jump-to"));
        cssFiles.add(getCssFile("pt.css.minicolors.id", "minicolors"));
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
        List<JavaScriptModule> javaScriptModules = Lists.newArrayList();
        BootstrapTableWidget bootstrapTableWidget = (BootstrapTableWidget) view;
        boolean msie = WebAppScriptHelper.isUserAgentOfMsie(request);
        if (msie || bootstrapTableWidget.isEnableTreegrid()) {
            javaScriptModules.add(appContext.getJavaScriptModule("bootstrap-table-treegrid"));
        }
        if (msie || bootstrapTableWidget.isEnableCardgrid()) {
            javaScriptModules.add(appContext.getJavaScriptModule("bootstrap-table-cardgrid"));
        }
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
