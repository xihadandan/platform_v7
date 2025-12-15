/*
 * @(#)2013-1-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access.intercept.provider;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.access.SecurityMetadataSourceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.stereotype.Component;

import java.util.*;

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
@Component
public class AnonymousSecurityMetadataSourceProvider implements SecurityMetadataSourceProvider {

    private static final Collection<ConfigAttribute> randomConfigAttribute = new ArrayList<ConfigAttribute>();

    static {
        String randomRole = UUID.randomUUID().toString();
        randomConfigAttribute.add(new SecurityConfig(randomRole));
    }

    private Map<Object, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<Object, Collection<ConfigAttribute>>();


    @Autowired
    private SecurityMetadataSourceProviderFactory securityMetadataSourceProviderFactory;


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#getAllConfigAttributeMap()
     */
    @Override
    public Map<Object, Collection<ConfigAttribute>> getAllConfigAttributeMap() {
        return requestMap;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#getConfigAttributeMap(String)
     */
    @Override
    public Map<Object, Collection<ConfigAttribute>> getConfigAttributeMap(String functionType) {
        SecurityMetadataSourceProvider defaultTenantSecurityMetadataSourceProvider = securityMetadataSourceProviderFactory
                .getDefaultTenantSecurityMetadataSourceProvider();
        return defaultTenantSecurityMetadataSourceProvider.getConfigAttributeMap(functionType);
        // return requestMap;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#getAllConfigAttributes()
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return filterSecurityInterceptor().obtainSecurityMetadataSource().getAllConfigAttributes();
    }

    private FilterSecurityInterceptor filterSecurityInterceptor() {
        return ApplicationContextHolder.getBean(FilterSecurityInterceptor.class);
    }

    @Override
    public void appStartLoadConfigAttribute() {

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#getRequestAttributes(Object)
     */
    @Override
    public Collection<ConfigAttribute> getRequestAttributes(Object object) {
        Collection<ConfigAttribute> attributes = filterSecurityInterceptor().obtainSecurityMetadataSource()
                .getAttributes(object);
        if (attributes == null || attributes.isEmpty()) {
            SecurityMetadataSourceProvider defaultTenantSecurityMetadataSourceProvider = securityMetadataSourceProviderFactory
                    .getDefaultTenantSecurityMetadataSourceProvider();
            Collection<ConfigAttribute> defaultTenantConfigAttributes = defaultTenantSecurityMetadataSourceProvider
                    .getRequestAttributes(object);
            return defaultTenantConfigAttributes;
            // return randomConfigAttribute;
        }
        return attributes;
    }

    @Override
    public Collection<ConfigAttribute> getRequestURLAttributes(String object) {
        return this.getAllConfigAttributes();
    }

    @Override
    public Set<GrantedAuthority> getGrantedAuthority(String roleUuid) {
        return null;
    }

    @Override
    public boolean isSecurityCacheExpired() {
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#reload()
     */
    @Override
    public void reload() {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#reload(String, String)
     */
    @Override
    public void reload(String configUuid, String configType) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#addAnonymousAttributes(java.lang.String, java.util.Collection, java.lang.String)
     */
    @Override
    public void addAnonymousAttributes(String resouceId, Collection<Object> objects,
                                       String functionType) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#removeResourceAttributes(java.lang.String)
     */
    @Override
    public void removeResourceAttributes(String resourceId) {
    }

    @Override
    public void removeAnonymousAttributes(String resourceId) {

    }


}
