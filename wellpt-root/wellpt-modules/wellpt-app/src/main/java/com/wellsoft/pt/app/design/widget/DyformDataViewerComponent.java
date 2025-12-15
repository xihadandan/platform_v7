/*
 * @(#)2016年5月10日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.widget;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.client.widget.DyformDataViewerWidget;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 组织树组件
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
@Component
public class DyformDataViewerComponent extends UIDesignComponent {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wDyformDataViewer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getName()
     */
    @Override
    public String getName() {
        return "表单数据查看器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getViewClass()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends View> getViewClass() {
        return DyformDataViewerWidget.class;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.widget_dyform_data_viewer.id", "widget_dyform_data_viewer");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
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
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(getCssFile("pt.css.ztree.id", "ztree"));
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getPreviewHtml()
     */
    @Override
    public String getPreviewHtml() {
        return "<div></div>";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#isConfigurable()
     */
    @Override
    public boolean isConfigurable() {
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
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
