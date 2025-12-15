/*
 * @(#)2019年7月3日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.portal.web;

import com.wellsoft.context.annotation.Description;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.design.container.AbstractContainer;
import com.wellsoft.pt.app.design.container.Container;
import com.wellsoft.pt.app.design.web.WebAppDesignerController;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年7月3日.1	zhulh		2019年7月3日		Create
 * </pre>
 * @date 2019年7月3日
 */
@Controller
@RequestMapping("/web/app/portal")
public class WebAppPortalConfigurerController extends WebAppDesignerController {

    @Autowired
    private AppPageDefinitionService appPageDefinitionService;

    /**
     * 打开前台门户配置页面
     *
     * @return
     */
    @RequestMapping(value = "/config/page/{pageUuid}")
    @Description("门户配置")
    public String portalConfig(@PathVariable(value = "pageUuid") String pageUuid, HttpServletRequest request,
                               HttpServletResponse response, Model model) {
        AppPageDefinition appPageDefinition = appPageDefinitionService.get(pageUuid);
        String piUuid = appPageDefinition.getAppPiUuid();
        String pageWtype = appPageDefinition.getWtype();
        // 配置页面信息
        config(piUuid, pageUuid, pageWtype, request, response, model);
        String appPortalConfigCss = "<link href=\"" + request.getContextPath()
                + "/resources/pt/css/app/portal/app_portal_config.css\" rel=\"stylesheet\">";
        String sourcePageUuid = appPageDefinition.getCorrelativePageUuid();
        if (StringUtils.isBlank(sourcePageUuid)) {
            sourcePageUuid = appPageDefinition.getUuid();
        }
        model.addAttribute("appPortalConfigCss", appPortalConfigCss);
        model.addAttribute("sourcePageUuid", sourcePageUuid);
        return "app/portal/app_portal_config";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.web.WebAppDesignerController#getAppDesignerJavaScriptModule(com.wellsoft.pt.app.design.container.AbstractContainer, com.wellsoft.pt.app.context.AppContext)
     */
    @Override
    protected JavaScriptModule getAppDesignerJavaScriptModule(AbstractContainer container, AppContext appContext) {
        return appContext.getJavaScriptModule("AppPortalConfigurer");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.web.WebAppDesignerController#getClientConfigJavaScriptModule(com.wellsoft.pt.app.design.container.Container, com.wellsoft.pt.app.context.AppContext)
     */
    @Override
    protected JavaScriptModule getClientConfigJavaScriptModule(Container container, AppContext appContext) {
        return appContext.getJavaScriptModule("app_portal_config");
    }

    /**
     * 预览
     *
     * @return
     */
    @RequestMapping(value = "/preview/{piUuid}")
    @Description("门户预览")
    public String preview(@PathVariable("piUuid") String piUuid,
                          @RequestParam(value = "pageUuid", required = false) String pageUuid, HttpServletRequest request,
                          HttpServletResponse response, Model model) {
        return super.preview(piUuid, pageUuid, request, response, model);
    }

}
