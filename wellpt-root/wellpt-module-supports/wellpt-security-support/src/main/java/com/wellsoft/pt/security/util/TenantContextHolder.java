/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.util;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.mt.service.Tenantable;
import com.wellsoft.pt.security.interfaces.filter.LoginAuthenticationProcessingFilter;
import com.wellsoft.pt.security.interfaces.filter.SwitchTenantUserFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
public class TenantContextHolder {
    private static final ThreadLocal<String> loginTypeContextHolder = new NamedThreadLocal<String>("Login type context");
    private static Logger logger = LoggerFactory.getLogger(TenantContextHolder.class);

    /**
     * 如何描述该方法
     *
     * @return
     */
    public static String getTenantId() {
        String tenantId = null;
        // 如果正在进行忽略登录，返回登录的ID
        if (IgnoreLoginUtils.getLoginingTenantId() != null) {
            return IgnoreLoginUtils.getLoginingTenantId();
        }
        // 如果是忽略登录则返回忽略登录的tenant
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            tenantId = IgnoreLoginUtils.getUserDetails().getTenantId();
            return tenantId == null ? Config.DEFAULT_TENANT : tenantId;
        }

        // 切换租户账号
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            Object tenantIdValue = request.getAttribute(SwitchTenantUserFilter.SPRING_SECURITY_SWITCH_TENANT_KEY);
            if (tenantIdValue != null) {
                return tenantIdValue.toString();
            }
        }

        if (StringUtils.isBlank(tenantId)) {
            UserDetails user = SpringSecurityUtils.getCurrentUser();
            if (user != null && user instanceof Tenantable) {
                tenantId = ((Tenantable) user).getTenantId();
                return tenantId == null ? Config.DEFAULT_TENANT : tenantId;
            }
        }

        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            Object tenantIdValue = request
                    .getAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_ID_KEY);
            if (tenantIdValue != null) {
                tenantId = tenantIdValue.toString();
            } else {
                tenantIdValue = request
                        .getAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_KEY);
                if (tenantIdValue != null) {
                    tenantId = tenantIdValue.toString();
                }
            }
        }

        return tenantId == null ? Config.DEFAULT_TENANT : tenantId;
    }

    /**
     * Description how to use this method
     */
    public static String getLoginType() {
        return loginTypeContextHolder.get();
    }

    /**
     * Description how to use this method
     */
    public static void setLoginType(String loginType) {
        loginTypeContextHolder.set(loginType);
    }

    /**
     * Description how to use this method
     */
    public static void reset() {
        loginTypeContextHolder.remove();
    }

}
