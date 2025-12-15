/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.pt.security.util.DesCipherUtil;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: SuperAdminAuthenticationProcessingFilter.java
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
public class SuperAdminAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        TenantContextHolder.reset();
        TenantContextHolder.setLoginType(LoginType.SUPER_ADMIN);
        return super.attemptAuthentication(request, response);
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String encryptKey = request.getParameter(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_PWD_ECRYPT_KEY);
        String password = super.obtainPassword(request);
        String encryptType = request.getParameter(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_PWD_ECRYPT_TYPE);
        if (StringUtils.isBlank(encryptType)) {
            return password;
        } else if ("1".equalsIgnoreCase(encryptType)) {
            return new String(Base64.decodeBase64(password));
        } else if ("2".equalsIgnoreCase(encryptType)) {
            return DesCipherUtil.decrypt(password, encryptKey);
        }
        return password;
    }
}
