/*
 * @(#)2016年8月29日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.widget;

import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author liut
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年4月23日.1	liut		2020年4月23日		Create
 * </pre>
 * @date 2020年4月23日
 */
@Component
public class UnifiedPortalComponent extends UIDesignComponent {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wUnifiedPortal";
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
        return "统一门户组件";
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
        return 66;
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
        return getJavaScriptModule("pt.js.module.widget_unified_portal.id", "widget_unified_portal");
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
        Map<String, Object> options = super.getDefaultOptions();
        options.put("configuration", new HashMap<String, Object>());
        return options;
    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public Class<? extends View> getViewClass() {
//        return UnifiedPortalWidget.class;
//    }

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
        cssFiles.add(getCssFile("pt.css.minicolors.id", "minicolors"));
        cssFiles.add(getCssFile("pt.css.wUnifiedPortal.id", "wUnifiedPortal"));
        return cssFiles;
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
        cssFiles.add(getCssFile("pt.css.select2.id", "select2"));
        cssFiles.add(getCssFile("pt.css.minicolors.id", "minicolors"));
        cssFiles.add(getCssFile("pt.css.wUnifiedPortal.id", "wUnifiedPortal"));
        return cssFiles;
    }
}
