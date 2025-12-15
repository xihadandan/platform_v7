package com.wellsoft.pt.dms.config.component;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 文档交换器组件定义
 *
 * @author chenq
 */
@Component
public class DocExchangerComponent extends UIDesignComponent {


    /**
     * 组件名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "文档交换器";
    }


    @Override
    public String getType() {
        return "wDocExchanger";
    }


    @Override
    public String getPreviewHtml() {
        String panel = "<div class=\"panel panel-default\"><div class=\"panel-body ui-sortable\"></div></div>";
        return panel;
    }


    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.widget_doc_exchanger.id", "widget_doc_exchanger");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
    }


    @Override
    public List<CssFile> getDefineCssFiles() {
        return Collections.emptyList();
    }


    @Override
    public int getOrder() {
        return 101;
    }


    @Override
    public ComponentCategory getCategory() {
        return SimpleComponentCategory.APP;
    }
}
