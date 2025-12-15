/*
 * @(#)Dec 6, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.widget.mobile;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.client.widget.mobile.MobileListViewWidget;
import org.springframework.stereotype.Component;

/**
 * Description: 数据列表
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 6, 2016.1	zhulh		Dec 6, 2016		Create
 * </pre>
 * @date Dec 6, 2016
 */
@Component
public class MobileListViewComponent extends UIDesignComponent {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wMobileListView";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getName()
     */
    @Override
    public String getName() {
        return "数据列表";
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

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 31;
    }

    @Override
    public Class<? extends View> getViewClass() {
        return MobileListViewWidget.class;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.widget_mobile_list_view.id", "widget_mobile_list_view");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
    }

}
