/*
 * @(#)2012-12-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.support.CasLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 多租户管理员控制器 /tenant/{tenantId}/main 多租户管理员主页
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-19.1	zhulh		2012-12-19		Create
 * </pre>
 * @date 2012-12-19
 */
@Api(tags = "多租户管理员控制器 /tenant/{tenantId}/main 多租户管理员主页")
@RestController
@RequestMapping("/passport/admin")
public class AdminLoginController extends BaseController {

    private final static String NEW_ADMIN_PAGE_URL = "/web/app/pt-mgr.html?pageUuid=ac525dcd-50b7-42e9-95b7-658b117ac19b";

    @ApiOperation(value = "跳转工作台", notes = "跳转工作台", tags = {"单位管理员登录", "超管登录"})
    @GetMapping(value = "/main")
    public String main(HttpServletRequest request, @RequestParam(required = false) String version) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (userDetails == null) {
            if (CasLoginUtils.isUseCas()) {
                return redirect("/j_spring_cas_security_logout");
            } else {
                return redirect("/security_logout");
            }
        }
        // 只有单点情况下，才需要判断是否管理员
        // modify200619单位管理员跳转到工作台
        if (CasLoginUtils.isUseCas()/* && !userDetails.isAdmin() */) {
            return redirect("/passport/user/login/success");
        }
        return (StringUtils.isBlank(version) || "1.0".equals(version)) ? forward("/admin/main")
                : redirect(NEW_ADMIN_PAGE_URL);
    }

    @ApiOperation(value = "跳转到我的主页", notes = "跳转到我的主页", tags = {"单位管理员登录", "超管登录"})
    @GetMapping(value = "/home")
    public String home() {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (userDetails == null) {
            if (CasLoginUtils.isUseCas()) {
                return redirect("/j_spring_cas_security_logout");
            } else {
                return redirect("/security_logout");
            }
        }
        return forward("/admin/home");
    }

}
