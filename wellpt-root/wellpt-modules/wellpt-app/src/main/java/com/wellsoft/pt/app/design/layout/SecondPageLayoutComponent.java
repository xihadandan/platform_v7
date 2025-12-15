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
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年9月20日	zhongzh		2016年9月20日		Create
 * </pre>
 * @date 2016年9月20日
 */
@Component
public class SecondPageLayoutComponent extends UIDesignComponent {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wSecondPageLayout";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getName()
     */
    @Override
    public String getName() {
        return "二级页面容器布局";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.layout_secondPage.id", "layout_secondPage");
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
        Map<String, Object> column = new HashMap<String, Object>();
        Map<String, Object> column1 = new HashMap<String, Object>();
        column.put("index", "0");
        column.put("colspan", "3");
        column.put("width", "260");
        column1.put("index", "1");
        column1.put("colspan", "9");
        columns.add(column);
        columns.add(column1);
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
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getOrder()
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
