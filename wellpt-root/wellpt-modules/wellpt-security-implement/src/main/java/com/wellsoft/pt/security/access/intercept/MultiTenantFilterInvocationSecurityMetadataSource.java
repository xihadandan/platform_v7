/*
 * @(#)2013-1-16 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access.intercept;

import com.wellsoft.pt.security.access.intercept.provider.SecurityMetadataSourceProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import java.util.Collection;
import java.util.Collections;

/**
 * Description: 资源数据定义，将所有的资源和权限对应关系建立起来，即定义某一资源可以被哪些角色访问
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-16.1	zhulh		2013-1-16		Create
 * </pre>
 * @date 2013-1-16
 */
public class MultiTenantFilterInvocationSecurityMetadataSource implements
        FilterInvocationSecurityMetadataSource {

    private final static Logger LOGGER = LoggerFactory.getLogger(
            MultiTenantFilterInvocationSecurityMetadataSource.class);


    @Autowired
    private SecurityMetadataSourceProviderFactory securityMetadataProviderFactory;

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.access.SecurityMetadataSource#getAttributes(java.lang.Object)
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(
            Object object) throws IllegalArgumentException {
        return securityMetadataProviderFactory.getSecurityMetadataSourceProvider().getRequestAttributes(
                object);

    }

    public Collection<ConfigAttribute> getRequestURLAttributes(
            String url) throws IllegalArgumentException {
        return securityMetadataProviderFactory.getSecurityMetadataSourceProvider().getRequestURLAttributes(
                url);

    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.access.SecurityMetadataSource#getAllConfigAttributes()
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.access.SecurityMetadataSource#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

}
