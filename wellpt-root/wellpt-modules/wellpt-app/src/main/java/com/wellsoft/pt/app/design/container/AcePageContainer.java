/*
 * @(#)2016-09-20 V1.0
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
import com.wellsoft.pt.app.ui.client.container.WebAppAcePageDefinitionProxyPage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: ACE界面模板
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-20.1	zhulh		2016-09-20		Create
 * </pre>
 * @date 2016-09-20
 */
@Component
public class AcePageContainer extends AbstractContainer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.container.Container#getName()
     */
    @Override
    public String getName() {
        return "ACE界面模板";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getViewClass()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends View> getViewClass() {
        return WebAppAcePageDefinitionProxyPage.class;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wAcePage";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getCategory()
     */
    @Override
    public ComponentCategory getCategory() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 1;
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

        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(bootstrap);
        cssFiles.add(jqueryUI);
        cssFiles.add(ztree);
        cssFiles.add(select2);
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
        String fontAwesomeId = Config.getValue("pt.css.font-awesome.id", "font-awesome");
        CssFile fontAwesome = AppContextHolder.getContext().getCssFile(fontAwesomeId);
        String animateId = Config.getValue("pt.css.animate.id", "animate");
        CssFile animate = AppContextHolder.getContext().getCssFile(animateId);
        String aceStyleId = Config.getValue("pt.css.ace-style.id", "ace-style");
        CssFile aceStyle = AppContextHolder.getContext().getCssFile(aceStyleId);
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(bootstrap);
        cssFiles.add(fontAwesome);
        cssFiles.add(animate);
        cssFiles.add(aceStyle);
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.container_ace_page.id", "container_ace_page");
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

}
