/*
 * @(#)2013-1-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.enums;

/**
 * Description: 内置角色枚举类
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
public enum BuildInRole {
    //代表超级管理员URL资源的内置角色，由类SuperAdminSecurityMetadataProvider提供URL映射
    ROLE_ADMIN,
    //代表系统单位管理员
    ROLE_UNIT_ADMIN,
    //代表租户管理员
    ROLE_TENANT_ADMIN,
    //代表所有登录用户URL资源的内置角色，由类UserSecurityMetadataProvider提供URL映射
    ROLE_USER,
    //代表所有未登录用户URL资源的内置角色，由类AnonymousSecurityMetadataProvider提供URL
    ROLE_ANONYMOUS,
    //代表互联网登录用户的内置角色
    ROLE_INTERNET_USER,
    //代表匿名token的内置角色
    ROLE_ANONYMOUS_TOKEN_VALID,
    //代表可信任的客户端角色
    ROLE_TRUSTED_CLIENT;
}
