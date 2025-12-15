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
public class OneRowThreeColumnLayoutComponent extends UIDesignComponent {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wBootgrid_444";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getName()
     */
    @Override
    public String getName() {
        return "一行三列布局";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.layout_bootstrap_grid.id", "layout_bootstrap_grid");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#isConfigurable()
     */
    @Override
    public boolean isConfigurable() {
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getDefaultOptions()
     */
    @Override
    public Map<String, Object> getDefaultOptions() {
        Map<String, Object> values = super.getDefaultOptions();
        Map<String, Object> configuration = new HashMap<String, Object>();
        List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
        Map<String, Object> column41 = new HashMap<String, Object>();
        Map<String, Object> column42 = new HashMap<String, Object>();
        Map<String, Object> column43 = new HashMap<String, Object>();
        column41.put("index", "1");
        column41.put("colspan", "4");
        column42.put("index", "2");
        column42.put("colspan", "4");
        column43.put("index", "3");
        column43.put("colspan", "4");
        columns.add(column41);
        columns.add(column42);
        columns.add(column43);
        configuration.put("columns", columns);
        values.put(AppConstants.KEY_CONFIGURATION, configuration);
        return values;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getPreviewHtml()
     */
    @Override
    public String getPreviewHtml() {
        Map<String, Object> root = new HashMap<String, Object>();
        return PreviewHtmlUtils.getPreviewHtml("bootstrap_grid_444.ftl", root);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getOrder()
     */
    @Override
    public int getOrder() {
        return 13;
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
