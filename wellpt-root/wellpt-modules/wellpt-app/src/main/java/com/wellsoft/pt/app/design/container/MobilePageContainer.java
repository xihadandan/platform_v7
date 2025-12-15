/*
 * @(#)2016-12-05 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.container;

import com.google.common.collect.Lists;
import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.ui.ComponentCategory;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.app.ui.client.container.WebAppMobilePageDefinitionProxyPage;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 手机页面设计器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-12-05.1	zhulh		2016-12-05		Create
 * </pre>
 * @date 2016-12-05
 */
@Component
public class MobilePageContainer extends AbstractContainer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.container.Container#getName()
     */
    @Override
    public String getName() {
        return "手机页面设计器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Component#getType()
     */
    @Override
    public String getType() {
        return "wMobilePage";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getViewClass()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends View> getViewClass() {
        return WebAppMobilePageDefinitionProxyPage.class;
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
        return 10;
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
        String bootstrapTableId = Config.getValue("pt.css.bootstrapTable.id", "bootstrapTable");
        CssFile bootstrapTable = AppContextHolder.getContext().getCssFile(bootstrapTableId);
        String bootstrapEditableId = Config.getValue("pt.css.bootstrap-editable.id", "bootstrap-editable");
        CssFile bootstrapEditable = AppContextHolder.getContext().getCssFile(bootstrapEditableId);
        String bootstrapDatetimepickerId = Config.getValue("pt.css.bootstrap-datetimepicker.id",
                "bootstrap-datetimepicker");
        CssFile bootstrapDatetimepicker = AppContextHolder.getContext().getCssFile(bootstrapDatetimepickerId);
        String fontAwesomeId = Config.getValue("pt.css.font-awesome.id", "font-awesome");
        CssFile fontAwesome = AppContextHolder.getContext().getCssFile(fontAwesomeId);
        String jsonviewId = Config.getValue("pt.css.jsonview.id", "jsonview");
        CssFile jsonview = AppContextHolder.getContext().getCssFile(jsonviewId);
        String baseId = Config.getValue("pt.css.app-base.id", "app-base");
        CssFile base = AppContextHolder.getContext().getCssFile(baseId);
        String muiId = Config.getValue("pt.css.mui.id", "mui");
        CssFile mui = AppContextHolder.getContext().getCssFile(muiId);

        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(bootstrap);
        cssFiles.add(jqueryUI);
        cssFiles.add(ztree);
        cssFiles.add(select2);
        cssFiles.add(bootstrapTable);
        cssFiles.add(bootstrapEditable);
        cssFiles.add(bootstrapDatetimepicker);
        cssFiles.add(fontAwesome);
        cssFiles.add(jsonview);
        cssFiles.add(base);
        cssFiles.add(mui);
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getExplainCssFiles()
     */
    @Override
    public List<CssFile> getExplainCssFiles() {
        List<CssFile> cssFiles = new ArrayList<CssFile>();
        cssFiles.add(getCssFile("pt.css.mui.id", "mui"));
        cssFiles.add(getCssFile("pt.css.mui-app.id", "mui-app"));
        return cssFiles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.component.UIDesignComponent#getJavaScriptModule()
     */
    @Override
    public JavaScriptModule getJavaScriptModule() {
        String id = Config.getValue("pt.js.module.container_mobile_page.id", "container_mobile_page");
        JavaScriptModule jsModule = AppContextHolder.getContext().getJavaScriptModule(id);
        return jsModule;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.container.AbstractContainer#supportsWysiwyg()
     */
    @Override
    public boolean supportsWysiwyg() {
        return false;
    }

    @Override
    public List<JavaScriptModule> getExplainJavaScriptModules(AppContext appContext, View view,
                                                              HttpServletRequest request, HttpServletResponse response) {
        List<JavaScriptModule> javaScriptModules = Lists.newArrayList();
        javaScriptModules.add(appContext.getJavaScriptModule("mui"));
        return javaScriptModules;
    }

}
