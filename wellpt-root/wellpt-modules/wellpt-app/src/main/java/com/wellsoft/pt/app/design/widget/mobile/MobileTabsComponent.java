package com.wellsoft.pt.app.design.widget.mobile;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.client.widget.mobile.MobileTabsWidget;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Component
public class MobileTabsComponent extends UIDesignComponent {

    @Override
    public String getType() {
        return "wMobileTabs";
    }

    @Override
    public String getName() {
        return "Tabs组件";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends View> getViewClass() {
        return MobileTabsWidget.class;
    }

    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.widget_mobile_tabs.id", "widget_mobile_tabs");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
    }

    @Override
    public List<CssFile> getExplainCssFiles() {
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        return cssFiles;
    }

    @Override
    public String getPreviewHtml() {
        String panel = "<div class=\"panel panel-default\"><div class=\"panel-body ui-sortable\"></div></div>";
        return panel;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public ComponentCategory getCategory() {
        return SimpleComponentCategory.LAYOUT;
    }

}
