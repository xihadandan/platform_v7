/*
 * @(#)2016年5月10日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.layout;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.design.support.PreviewHtmlUtils;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.ui.ComponentCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongds
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月17日	zhongds		2019年12月17日		Create
 * </pre>
 * @date 2019年12月17日
 */
@Component
public class PageCustomLayoutComponent extends UIDesignComponent {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wLayoutCustomPage";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getName()
     */
    @Override
    public String getName() {
        return "自定义页面布局";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.layout_custom_page.id", "layout_custom_page");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
    }

    /**
     * (non-Javadoc)
     *
     * @see UIDesignComponent#isConfigurable()
     */
    @Override
    public boolean isConfigurable() {
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see UIDesignComponent#getDefaultOptions()
     */
    @Override
    public Map<String, Object> getDefaultOptions() {
        Map<String, Object> values = super.getDefaultOptions();
        Map<String, Object> configuration = new HashMap<String, Object>();
        List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
        Map<String, Object> wellHeader = new HashMap<String, Object>();
        Map<String, Object> wellLeftNav = new HashMap<String, Object>();
        Map<String, Object> wellContent = new HashMap<String, Object>();
        Map<String, Object> wellFooter = new HashMap<String, Object>();
        wellHeader.put("index", "0");
        wellHeader.put("colspan", "12");
        wellHeader.put("class", "well-header");
        wellLeftNav.put("index", "1");
        wellLeftNav.put("colspan", "3");
        wellLeftNav.put("width", "260");
        wellLeftNav.put("class", "well-left-nav");
        wellContent.put("index", "2");
        wellContent.put("colspan", "9");
        wellContent.put("class", "well-content");
        wellFooter.put("index", "3");
        wellFooter.put("colspan", "12");
        wellFooter.put("class", "well-footer");
        columns.add(wellHeader);
        columns.add(wellLeftNav);
        columns.add(wellContent);
        columns.add(wellFooter);
        configuration.put("columns", columns);
        values.put(AppConstants.KEY_CONFIGURATION, configuration);
        return values;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getPreviewHtml()
     */
    @Override
    public String getPreviewHtml() {
        Map<String, Object> root = new HashMap<String, Object>();
        return PreviewHtmlUtils.getPreviewHtml("bootstrap_grid_12.ftl", root);
    }

    /**
     * (non-Javadoc)
     *
     * @see UIDesignComponent#getOrder()
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
        return SimpleComponentCategory.LAYOUT;
    }

}
