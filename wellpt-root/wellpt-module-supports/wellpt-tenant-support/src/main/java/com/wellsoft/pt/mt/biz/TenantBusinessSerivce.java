/*
 * @(#)2013-12-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.biz;

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
 * 2013-12-6.1	zhulh		2013-12-6		Create
 * </pre>
 * @date 2013-12-6
 */
public interface TenantBusinessSerivce {

    /**
     * 根据租户信息创建租户数据库
     *
     * @param tenant
     */
    void createTenantDatabase(Tenant tenant);

    /**
     * 根据租户信息创建租户库内的管理员
     *
     * @param tenant
     */
    void createTenantAdminUser(Tenant tenant);

    boolean checkDatasourceConnectionStatus(String tenantUuid);
}
