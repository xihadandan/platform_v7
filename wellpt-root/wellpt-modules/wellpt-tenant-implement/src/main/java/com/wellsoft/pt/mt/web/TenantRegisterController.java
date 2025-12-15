/*
 * @(#)2013-4-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.mt.bean.RegisterBean;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-18.1	zhulh		2013-4-18		Create
 * </pre>
 * @date 2013-4-18
 */
@Controller
@RequestMapping("/security/tenant")
public class TenantRegisterController extends BaseController {

    @Autowired
    private TenantManagerService tenantManagerService;

    /**
     * 跳转到租户注册页面
     *
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm() {
        return forward("/security/tenant_register");
    }

    /**
     * 跳转到租户注册页面
     *
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@Valid RegisterBean registerBean) {
        Tenant tenant = new Tenant();
        tenant.setName(registerBean.getCompanyName());
        tenant.setAccount(registerBean.getAccount());
        tenant.setPassword(registerBean.getPassword());
        String email = registerBean.getEmail();
        tenant.setEmail(email);

        boolean result = false;
        try {
            result = tenantManagerService.register(tenant);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        // 注册成功跳转到注册成功界面
        if (result) {
            return forward("/security/tenant_register_success");
        }

        return forward("/security/tenant_register_failure");
    }

    /**
     * 跳转到注册成功的页面
     *
     * @return
     */
    @RequestMapping(value = "/register/success", method = RequestMethod.GET)
    public String registerSuccess() {
        return forward("/security/tenant_register_success");
    }

    /**
     * 跳转到注册失败的页面
     *
     * @return
     */
    @RequestMapping(value = "/register/failure", method = RequestMethod.GET)
    public String registerFailure() {
        return forward("/security/tenant_register_failure");
    }
}
