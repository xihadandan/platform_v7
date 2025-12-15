/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: AdminAuthenticationProcessingFilter.java
 *
 * @author zhulh
 * @date 2012-12-25
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-25.1	zhulh		2012-12-25		Create
 * </pre>
 */
public class AdminAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        TenantContextHolder.reset();
        TenantContextHolder.setLoginType(LoginType.ADMIN);
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_KEY, obtainTenant(request));
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_ID_KEY,
                obtainTenantId(request));
        return super.attemptAuthentication(request, response);
    }

    private Object obtainTenantId(HttpServletRequest request) {
        return request.getParameter(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_ID_KEY);
    }

    private String obtainTenant(HttpServletRequest request) {
        return request.getParameter(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_KEY);
    }

}
