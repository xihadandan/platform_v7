/*
 * @(#)2014-9-26 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.sso.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.sso.entity.Accounts;
import com.wellsoft.pt.basicdata.sso.entity.SsoDetails;
import com.wellsoft.pt.basicdata.sso.entity.SsoParams;
import com.wellsoft.pt.basicdata.sso.service.SsoDetailsService;
import com.wellsoft.pt.basicdata.sso.service.SsoParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Description: SSO（Single Sign-On）单点登录控制
 *
 * @author FashionSUN
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-26.1	FashionSUN		2014-9-26		Create
 * </pre>
 * @date 2014-9-26
 */
@Controller
@RequestMapping("/xzsp/sso")
public class SsoController extends BaseController {

    @Autowired
    private SsoDetailsService ssoDetailsService;

    @Autowired
    private SsoParamsService ssoParamsService;

    /**
     * SSO配置
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/config")
    public String ssoConfig(Model model) {
        return forward("/pt/sso/sso_config");
    }

    /**
     * SSO登录实现
     *
     * @param sysId
     * @param model
     * @return
     */
    @RequestMapping(value = "/main")
    public String ssoMain(@RequestParam(value = "sysId") String sysId, Model model) {
        SsoDetails ssoDetails = ssoDetailsService.getBySYSID(sysId);
        List<SsoParams> paramsList = ssoParamsService.queryBySYSID(sysId);
        Accounts accounts = ssoDetailsService.ssoAuth(sysId);
        model.addAttribute("ssoDetail", ssoDetails);
        model.addAttribute("paramsList", paramsList);
        model.addAttribute("account", accounts);
        return forward("/pt/sso/sso_main");
    }

    @RequestMapping(value = "/proxy")
    @ResponseBody
    public String proxyHttp(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> params = request.getParameterMap();
        return "";
    }
}
