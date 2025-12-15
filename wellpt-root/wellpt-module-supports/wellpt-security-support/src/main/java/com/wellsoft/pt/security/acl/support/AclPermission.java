/*
 * @(#)2012-10-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.support;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

/**
 * Description: ACL权限定义类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-30.1	zhulh		2012-10-30		Create
 * </pre>
 * @date 2012-10-30
 */
public class AclPermission extends BasePermission {
    /**
     * 草稿
     */
    public static final Permission DRAFT = new AclPermission(1 << 5, 'D'); // 32
    /**
     * 待办
     */
    public static final Permission TODO = new AclPermission(1 << 6, 'T'); // 64
    /**
     * 已办
     */
    public static final Permission DONE = new AclPermission(1 << 7, 'D'); // 128
    /**
     * 关注
     */
    public static final Permission ATTENTION = new AclPermission(1 << 8, 'A'); // 256
    /**
     * 未阅
     */
    public static final Permission UNREAD = new AclPermission(1 << 9, 'U'); // 512
    /**
     * 已阅
     */
    public static final Permission FLAG_READ = new AclPermission(1 << 10, 'R'); // 1024
    /**
     * 督办
     */
    public static final Permission SUPERVISE = new AclPermission(1 << 11, 'S'); // 2048
    /**
     * 监控
     */
    public static final Permission MONITOR = new AclPermission(1 << 12, 'M'); // 4096
    /**
     * 委托
     */
    public static final Permission DELEGATION = new AclPermission(1 << 13, 'D'); // 8192
    /**
     * 收件
     */
    public static final Permission INTBOX = new AclPermission(1 << 14, 'I'); // 8192
    /**
     * 发件
     */
    public static final Permission OUTBOX = new AclPermission(1 << 15, 'O'); // 8192
    /**
     * 拒收
     */
    public static final Permission REFUSE = new AclPermission(1 << 28, 'R');
    /**
     * 查阅
     */
    public static final Permission CONSULT = new AclPermission(1, 'C');// 1
    private static final long serialVersionUID = 6433269798814028362L;

    /**
     * @param mask
     */
    public AclPermission(int mask) {
        super(mask);
    }

    /**
     * @param mask
     * @param code
     */
    public AclPermission(int mask, char code) {
        super(mask, code);
    }

}
