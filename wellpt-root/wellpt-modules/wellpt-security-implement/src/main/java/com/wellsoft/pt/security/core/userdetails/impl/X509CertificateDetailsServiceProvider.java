/*
 * @(#)2013-12-4 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails.impl;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.pt.security.access.LoginAuthenticationProcessingFilter;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-4.1	zhulh		2013-12-4		Create
 * </pre>
 * @date 2013-12-4
 */
@Service
@Transactional(readOnly = true)
public class X509CertificateDetailsServiceProvider extends UserDetailsServiceProviderImpl {
    private Pattern tenantPattern = Pattern.compile("/tenant/(.*?(?=/))", Pattern.CASE_INSENSITIVE);

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.core.userdetails.impl.UserDetailsServiceProviderImpl#getLoginType()
     */
    @Override
    public String getLoginType() {
        return LoginType.X509Certificate;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.core.userdetails.impl.UserDetailsServiceProviderImpl#getUserDetails(java.lang.String)
     */
    @Override
    public UserDetails getUserDetails(String username) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr.getRequest();

        String requestUrl = request.getRequestURI();
        Matcher matcher = tenantPattern.matcher(requestUrl);
        if (matcher.find()) {
            String tenantAccount = matcher.group(1);
            request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_KEY, tenantAccount);
        }

        return super.getUserDetails(username);
    }

}
