/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.web;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.config.entity.AppLoginPageConfigEntity;
import com.wellsoft.pt.security.config.service.AppLoginPageConfigService;
import com.wellsoft.pt.security.core.userdetails.SuperAdminDetails;
import com.wellsoft.pt.security.support.CasLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-25.1	zhulh		2012-12-25		Create
 * </pre>
 * @date 2012-12-25
 */
@Api(tags = "超管登录管理")
@RestController
@RequestMapping("/superadmin")
public class SuperAdminLoginController extends BaseController {

    private final static String NEW_SUPERADMIN_PAGE = "/web/app/pt-mgr.html?pageUuid=2f852b9e-4564-4f5b-bc7e-bb57f639cbe3";

    @Autowired
    AppLoginPageConfigService appLoginPageConfigService;

    @PostMapping("/login/security_check")
    @ApiOperation(value = "登录安全检查", notes = "登录安全检查")
    public String loginSecurityCheck(HttpServletRequest request) {
        request.setAttribute("loginType", LoginType.SUPER_ADMIN);
        return forward("/passport/user/login/security_check");
    }

    @GetMapping("/login")
    @ApiOperation(value = "登录", notes = "登录")
    public String login(Model model, HttpServletRequest request) {
        if (CasLoginUtils.isUseCas()) {
            return redirect("/superadmin/login/success");
        } else {
            AppLoginPageConfigEntity pageConfig = appLoginPageConfigService.getBySystemUnitId(MultiOrgSystemUnit.PT_ID);
            if (pageConfig == null) {
                // 默认的背景图、logo
                pageConfig = appLoginPageConfigService.saveInitPageConfig(MultiOrgSystemUnit.PT_ID);
            }
            model.addAttribute("config", pageConfig);
            model.addAttribute("loginType", 3);
            // 背景图转为base64编码输出
            try {
                String imageBase64 = IOUtils.toString(pageConfig.getPageBackgroundImage().getCharacterStream());
                if (StringUtils.isNotBlank(imageBase64)) {
                    model.addAttribute("backgroundImage",
                            "url(data:image/png;base64," + imageBase64.replaceAll("\r|\n", "") + ")");
                }

                imageBase64 = IOUtils.toString(pageConfig.getPageLogo().getCharacterStream());
                if (StringUtils.isNotBlank(imageBase64)) {
                    if ("_right".equalsIgnoreCase(pageConfig.getPageStyle())) {
                        model.addAttribute("headerImage",
                                "data:image/png;base64," + imageBase64.replaceAll("\r|\n", ""));
                    } else {
                        model.addAttribute("headerImage",
                                "url(data:image/png;base64," + imageBase64.replaceAll("\r|\n", "") + ")");
                    }
                }
            } catch (Exception e) {

            }

            return forward("/user/domain_login3" + StringUtils.trimToEmpty(pageConfig.getPageStyle()));
        }
    }

    @GetMapping("/login/success")
    @ApiOperation(value = "登录", notes = "登录")
    public String loginSuccess(HttpServletRequest request) {
        SuperAdminDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (userDetails == null) {
            redirect("/security_logout");
        }
        if (CasLoginUtils.isUseCas()) {
            request.setAttribute("logintype", LoginType.SUPER_ADMIN);
        }
        return redirect("/superadmin/main?version=2.0");
    }

    @GetMapping("/main")
    @ApiOperation(value = "登录主页", notes = "登录主页")
    public String main(@RequestParam(required = false) String version) {
        SuperAdminDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (userDetails == null) {
            redirect("/security_logout");
        }
        return (StringUtils.isBlank(version) || "1.0".equals(version)) ?
                // 旧版管理后台
                forward("/superadmin/main")
                // 新版管理后台
                : redirect(NEW_SUPERADMIN_PAGE);
    }
}
