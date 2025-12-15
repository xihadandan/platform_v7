/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails;

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
 * 2012-12-26.1	zhulh		2012-12-26		Create
 * </pre>
 * @date 2012-12-26
 */
public interface UserDetailsServiceProvider {
    public static final String KEY_FJCA_SERVER_URL = "fjca.server.url";

    public String getLoginType();

    UserDetails getUserDetails(String username);

    /**
     * 获取一个用户的所有授权的权限集
     *
     * @return
     */
    Set<GrantedAuthority> queryAllGrantedAuthoritiesByUserId(String userId);
}
