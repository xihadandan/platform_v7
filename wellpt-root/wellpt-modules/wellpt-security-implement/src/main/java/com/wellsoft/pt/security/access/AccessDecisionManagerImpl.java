/*
 * @(#)2013-1-16 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access;

import com.wellsoft.pt.security.core.userdetails.SuperAdminDetails;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Description: 访问决策器，决定某个用户具有的角色，是否有足够的权限去访问某个资源
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
public class AccessDecisionManagerImpl implements AccessDecisionManager {

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.access.AccessDecisionManager#decide(org.springframework.security.core.Authentication, java.lang.Object, java.util.Collection)
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        int deny = 0;
        int result = vote(authentication, object, configAttributes);
        switch (result) {
            case AccessDecisionVoter.ACCESS_GRANTED:
                return;
            case AccessDecisionVoter.ACCESS_DENIED:
                deny++;
                break;
            default:
                break;
        }

        if (deny > 0) {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * @param authentication
     * @param object
     * @param configAttributes
     * @return
     */
    private int vote(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) {
        // 判断用户如果是个超级管理员，则放开所有权限的检查
        Object userDetail = authentication.getPrincipal();
        if (userDetail instanceof SuperAdminDetails) {
            return AccessDecisionVoter.ACCESS_GRANTED;
        } else {
            int result = AccessDecisionVoter.ACCESS_ABSTAIN;
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            for (ConfigAttribute attribute : configAttributes) {
                if (this.supports(attribute)) {
                    result = AccessDecisionVoter.ACCESS_DENIED;

                    // Attempt to find a matching granted authority
                    for (GrantedAuthority authority : authorities) {
                        if (attribute.getAttribute().equals(authority.getAuthority())) {
                            return AccessDecisionVoter.ACCESS_GRANTED;
                        }
                    }
                }
            }
            return result;
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.access.AccessDecisionManager#supports(org.springframework.security.access.ConfigAttribute)
     */
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute.getAttribute() != null;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.access.AccessDecisionManager#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}
