/*
 * @(#)Jan 4, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.component;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.dms.config.widget.FileManagerWidget;
import org.springframework.stereotype.Component;

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
 * Jan 4, 2018.1	zhulh		Jan 4, 2018		Create
 * </pre>
 * @date Jan 4, 2018
 */
@Component
public class FileManagerComponent extends UIDesignComponent {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getCategory()
     */
    @Override
    public ComponentCategory getCategory() {
        return SimpleComponentCategory.APP;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getName()
     */
    @Override
    public String getName() {
        return "文件管理";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wFileManager";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getViewClass()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends View> getViewClass() {
        return FileManagerWidget.class;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getPreviewHtml()
     */
    @Override
    public String getPreviewHtml() {
        String panel = "<div class=\"panel panel-default\"><div class=\"panel-body ui-sortable\"></div></div>";
        return panel;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 110;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.widget_file_manager.id", "widget_file_manager");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getDefineCssFiles()
     */
    @Override
    public List<CssFile> getDefineCssFiles() {
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(getCssFile("pt.css.wFileManager.id", "wFileManager"));
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getExplainCssFiles(com.wellsoft.pt.app.context.AppContext, com.wellsoft.pt.app.ui.View, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public List<CssFile> getExplainCssFiles(AppContext appContext, View view, HttpServletRequest request,
                                            HttpServletResponse response) {
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(getCssFile("pt.css.wFileManager.id", "wFileManager"));
        return cssFiles;
    }

}
