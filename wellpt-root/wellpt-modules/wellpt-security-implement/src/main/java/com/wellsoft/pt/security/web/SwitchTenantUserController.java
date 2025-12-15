/*
 * @(#)2015-7-14 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.web;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.access.SwitchTenantUserFilter;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-14.1	zhulh		2015-7-14		Create
 * </pre>
 * @date 2015-7-14
 */
@Controller
@RequestMapping(value = "/security/switch/tenant/user")
public class SwitchTenantUserController extends BaseController {

    @Autowired
    private OrgApiFacade orgApiFacade;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String switchTenantUser(@RequestParam(value = "toTenant") String toTenant,
                                   RedirectAttributes redirectAttributes) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        // 跨租户用户检测
        String userId = userDetails.getUserId();
        try {
            IgnoreLoginUtils.login(toTenant, userId);
            MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(userId);
            if (user == null) {
                throw new RuntimeException("租户[" + toTenant + "]中不存在ID为[" + userId + "]的用户!");
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            IgnoreLoginUtils.logout();
        }

        // 重定向到租户用户切换过滤器
        SwitchTenantUserFilter switchTenantUserFilter = ApplicationContextHolder.getBean(SwitchTenantUserFilter.class);
        String switchUserUrl = switchTenantUserFilter.getSwitchUserUrl();
        String username = userDetails.getLoginName();
        redirectAttributes.addAttribute(SwitchTenantUserFilter.SPRING_SECURITY_SWITCH_USERNAME_KEY, username);
        redirectAttributes.addAttribute(SwitchTenantUserFilter.SPRING_SECURITY_SWITCH_TENANT_KEY, toTenant);

        return redirect(switchUserUrl);
    }

}
