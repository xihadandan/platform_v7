/*
 * @(#)2013-1-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access.intercept.provider;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

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
//@Component
public class SuperAdminSecurityMetadataSourceProvider extends AnonymousSecurityMetadataSourceProvider {
    @Override
    public void removeAnonymousAttributes(String resourceId) {

    }

    @Override
    public Set<GrantedAuthority> getGrantedAuthority(String roleUuid) {
        return null;
    }

    @Override
    public boolean isSecurityCacheExpired() {
        return false;
    }
}
