/*
 * @(#)2016-09-28 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.container;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.client.widget.mobile.MobilePanelWidget;
import org.springframework.stereotype.Component;

/**
 * Description: 面板组件
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-28.1	zhulh		2016-09-28		Create
 * </pre>
 * @date 2016-09-28
 */
@Component
public class MobilePanelContainer extends AbstractContainer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wMobilePanel";
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

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.container.Container#getName()
     */
    @Override
    public String getName() {
        return "面板";
    }

    @Override
    public Class<? extends View> getViewClass() {
        return MobilePanelWidget.class;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getPreviewHtml()
     */
    @Override
    public String getPreviewHtml() {
        String panel = "<div class=\"panel panel-default\"><div class=\"panel-body ui-sortable\"></div></div>";
        return panel;
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
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.container_mobile_panel.id", "container_mobile_panel");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
    }

}
