/*
 * @(#)2016年5月11日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.container;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.client.WebAppPageDefinitionProxyPage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
 * 2016年10月12日.1	zhongzh		2016年5月11日		Create
 * </pre>
 * @date 2016年10月12日
 */
@Component
public class LayoutitPageContainer extends AbstractContainer {

    public static final String PAGE_TYPE = "wLayoutit";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return PAGE_TYPE;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.container.Container#getName()
     */
    @Override
    public String getName() {
        return "Layoutit页面容器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getViewClass()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends View> getViewClass() {
        return WebAppPageDefinitionProxyPage.class;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getDefineCssFiles()
     */
    @Override
    public List<CssFile> getDefineCssFiles() {
        String bootstrapId = Config.getValue("pt.css.bootstrap.id", "bootstrap");
        CssFile bootstrap = AppContextHolder.getContext().getCssFile(bootstrapId);
        String jqueryUIId = Config.getValue("pt.css.jquery-ui.id", "jquery-ui");
        CssFile jqueryUI = AppContextHolder.getContext().getCssFile(jqueryUIId);
        String ztreeId = Config.getValue("pt.css.ztree.id", "ztree");
        CssFile ztree = AppContextHolder.getContext().getCssFile(ztreeId);
        String select2Id = Config.getValue("pt.css.select2.id", "select2");
        CssFile select2 = AppContextHolder.getContext().getCssFile(select2Id);
        String fontAwesome2Id = Config.getValue("pt.css.font-awesome.id", "font-awesome");
        CssFile fontAwesome = AppContextHolder.getContext().getCssFile(fontAwesome2Id);

        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(bootstrap);
        cssFiles.add(jqueryUI);
        cssFiles.add(ztree);
        cssFiles.add(select2);
        cssFiles.add(fontAwesome);
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getExplainCssFiles()
     */
    @Override
    public List<CssFile> getExplainCssFiles() {
        String bootstrapId = Config.getValue("pt.css.bootstrap.id", "bootstrap");
        CssFile bootstrap = AppContextHolder.getContext().getCssFile(bootstrapId);
        String jqueryUIId = Config.getValue("pt.css.jquery-ui.id", "jquery-ui");
        CssFile jqueryUI = AppContextHolder.getContext().getCssFile(jqueryUIId);
        String appBaseId = Config.getValue("pt.css.app-base.id", "app-base");
        CssFile appBase = AppContextHolder.getContext().getCssFile(appBaseId);
        String iconFontId = Config.getValue("pt.css.iconfont.id", "iconfont");
        CssFile iconFont = AppContextHolder.getContext().getCssFile(iconFontId);

        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(bootstrap);
        cssFiles.add(jqueryUI);
        cssFiles.add(appBase);
        cssFiles.add(iconFont);
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.config_component.id", "config_component");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getDefaultOptions()
     */
    @Override
    public Map<String, Object> getDefaultOptions() {
        Map<String, Object> defaults = super.getDefaultOptions();
        defaults.put("title", getName());
        return defaults;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getCategory()
     */
    @Override
    public ComponentCategory getCategory() {
        return null;
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

}
