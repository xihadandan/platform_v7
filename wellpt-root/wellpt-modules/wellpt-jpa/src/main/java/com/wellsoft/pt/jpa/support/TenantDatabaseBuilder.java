/*
 * @(#)2013-4-22 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.support;

import com.wellsoft.pt.mt.entity.Tenant;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-22.1	zhulh		2013-4-22		Create
 * </pre>
 * @date 2013-4-22
 */
public interface TenantDatabaseBuilder {
    /**
     * 初始化数据库
     *
     * @param tenant
     */
    void build(Tenant tenant);

    /**
     * 生成删除用户及分离数据库脚本
     *
     * @param tenant
     */
    void drop(Tenant tenant);
}
