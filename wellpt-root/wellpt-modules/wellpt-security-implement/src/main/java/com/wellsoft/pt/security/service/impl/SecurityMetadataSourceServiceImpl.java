/*
 * @(#)2013-1-16 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.service.impl;

import com.wellsoft.pt.security.access.intercept.provider.SecurityMetadataSourceProviderFactory;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Description: 用于加载SecurityMetadataSource数据的服务
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
@Service
public class SecurityMetadataSourceServiceImpl implements SecurityMetadataSourceService {

    @Autowired
    private SecurityMetadataSourceProviderFactory securityMetadataSourceProviderFactory;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.service.SecurityMetadataSourceService#loadSecurityMetadataSource()
     */
    @Override
    public void loadSecurityMetadataSource() {
        // 重新加载URL资源
        securityMetadataSourceProviderFactory.getSecurityMetadataSourceProvider().reload();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.service.SecurityMetadataSourceService#loadSecurityMetadataSource(java.lang.String, java.lang.String)
     */
    @Override
    public void loadSecurityMetadataSource(String configUuid, String configType) {
        // 重新加载URL资源
        securityMetadataSourceProviderFactory.getSecurityMetadataSourceProvider().reload(configUuid,
                configType);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.service.SecurityMetadataSourceService#getAttributes(java.lang.Object, java.lang.String)
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object, String functionType) {
        Map<Object, Collection<ConfigAttribute>> map = securityMetadataSourceProviderFactory
                .getSecurityMetadataSourceProvider().getConfigAttributeMap(functionType);
        if (map == null) {
            return Collections.emptyList();
        }

        Collection<ConfigAttribute> configAttributes = map.get(object);
        if (configAttributes == null) {
            return Collections.emptyList();
        }

        return configAttributes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.service.SecurityMetadataSourceService#addAnonymousAttributes(java.util.Collection, java.lang.String)
     */
    @Override
    public void addAnonymousAttributes(String resouceId, Collection<Object> objects,
                                       String functionType) {
        securityMetadataSourceProviderFactory.getSecurityMetadataSourceProvider().addAnonymousAttributes(
                resouceId,
                objects, functionType);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.service.SecurityMetadataSourceService#removeResourceAttributes(java.lang.String)
     */
    @Override
    public void removeResourceAttributes(String resourceId) {
        securityMetadataSourceProviderFactory.getSecurityMetadataSourceProvider().removeResourceAttributes(
                resourceId);
    }

    @Override
    public void removeAnonymousAttributes(String resourceId, Collection<Object> value, String key) {
        securityMetadataSourceProviderFactory.getSecurityMetadataSourceProvider().removeAnonymousAttributes(
                resourceId);
    }

    @Override
    public Set<GrantedAuthority> getGrantedAuthority(String roleUuid) {
        return securityMetadataSourceProviderFactory.getSecurityMetadataSourceProvider().getGrantedAuthority(roleUuid);
    }

    @Override
    public Set<GrantedAuthority> getUserGrantedAuthority(String roleUuid) {
        return securityMetadataSourceProviderFactory.getDefaultTenantSecurityMetadataSourceProvider().getGrantedAuthority(roleUuid);
    }

    @Override
    public boolean isSecurityCacheExpired() {
        return securityMetadataSourceProviderFactory.getSecurityMetadataSourceProvider().isSecurityCacheExpired();
    }

}
