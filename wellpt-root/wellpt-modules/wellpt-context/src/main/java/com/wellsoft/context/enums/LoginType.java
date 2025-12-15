/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.enums;

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
public class LoginType {
    // 普通用户登录
    public static final String USER = "1";
    // 管理员登录
    public static final String ADMIN = "2";
    // 超级管理员登录
    public static final String SUPER_ADMIN = "3";
    // FJCA证书登录
    public static final String CERTIFICATE = "4";
    // X509证书登录
    public static final String X509Certificate = "5";
    // RESTful登录
    public static final String RESTful = "6";
    // 互联网用户登录
    public static final String INTERNET_USER = "7";
}
