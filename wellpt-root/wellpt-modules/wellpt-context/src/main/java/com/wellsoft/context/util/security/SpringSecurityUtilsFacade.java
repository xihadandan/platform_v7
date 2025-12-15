/*
 * @(#)2018年1月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * SpringSecurityUtils(具体实现在wellpt-security)
 */
public interface SpringSecurityUtilsFacade {

    UserDetails getCurrentUser();
}
