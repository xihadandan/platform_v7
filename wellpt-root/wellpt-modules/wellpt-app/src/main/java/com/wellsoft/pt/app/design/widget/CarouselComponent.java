/*
 * @(#)2019年8月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.widget;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年08月12日  	Create
 * </pre>
 * @date 2019年09月19日
 */
@Component
public class CarouselComponent extends UIDesignComponent {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wCarousel";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getName()
     */
    @Override
    public String getName() {
        return "轮播图组件";
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.widget_carousel.id", "widget_carousel");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.component.UIDesignComponent#getOrder()
     */
    @Override
    public int getOrder() {
        return 38;
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

    @Override
    public List<CssFile> getDefineCssFiles() {
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(getCssFile("pt.css.wCarousel.id", "wCarousel"));
        return cssFiles;
    }

    @Override
    public List<CssFile> getExplainCssFiles() {
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(getCssFile("pt.css.wCarousel.id", "wCarousel"));
        return cssFiles;
    }

}
