/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-26.1	zhulh		2012-12-26		Create
 * </pre>
 * @date 2012-12-26
 */
@Controller
public class HomeController extends BaseController {
    @Autowired
    private TenantFacadeService tenantService;

    @RequestMapping(value = {"/", "/index", "/index.jsp"})
    public String index(Model model) {
        String account = Config.getValue("home.tenant.account");
        String redirectPage = Config.getValue("home.tenant.page");

        if (StringUtils.isNotBlank(account) && StringUtils.isNotBlank(redirectPage)) {
            Tenant tenant = tenantService.getByAccount(account);
            if (tenant != null) {
                return redirect(redirectPage);
            }
        }

        List<Tenant> tenants = tenantService.getActiveTenants();
        model.addAttribute("tenants", tenants);

        return forward("index");
    }
}
