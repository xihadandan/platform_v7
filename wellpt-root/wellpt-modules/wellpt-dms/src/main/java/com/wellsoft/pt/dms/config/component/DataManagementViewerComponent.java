/*
 * @(#)2017-02-14 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.component;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.dms.config.widget.DataManagementViewerWidget;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 数据管理页面组件定义类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-02-14.1	zhulh		2017-02-14		Create
 * </pre>
 * @date 2017-02-14
 */
@Component
public class DataManagementViewerComponent extends UIDesignComponent {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.container.Container#getName()
     */
    @Override
    public String getName() {
        return "数据管理查看器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wDataManagementViewer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getViewClass()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends View> getViewClass() {
        return DataManagementViewerWidget.class;
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
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.widget_data_management_viewer.id", "widget_data_management_viewer");
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
        String summernoteId = Config.getValue("pt.css.summernote.id", "summernote");
        CssFile summernote = AppContextHolder.getContext().getCssFile(summernoteId);

        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(summernote);
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getOrder()
     */
    @Override
    public int getOrder() {
        return 100;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getCategory()
     */
    @Override
    public ComponentCategory getCategory() {
        return SimpleComponentCategory.APP;
    }

}
