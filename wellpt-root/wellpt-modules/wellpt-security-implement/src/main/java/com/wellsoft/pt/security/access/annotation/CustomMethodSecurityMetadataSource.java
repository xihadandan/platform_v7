/*
 * @(#)2013-1-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access.annotation;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.Jsr250MethodSecurityMetadataSource;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-24.1	zhulh		2013-1-24		Create
 * </pre>
 * @date 2013-1-24
 */
public class CustomMethodSecurityMetadataSource extends Jsr250MethodSecurityMetadataSource {

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.access.SecurityMetadataSource#getAllConfigAttributes()
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
//				org.springframework.security.core.
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.access.method.AbstractFallbackMethodSecurityMetadataSource#findAttributes(java.lang.reflect.Method, java.lang.Class)
     */
    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method, Class<?> targetClass) {
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.access.method.AbstractFallbackMethodSecurityMetadataSource#findAttributes(java.lang.Class)
     */
    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return null;
    }

}
