/*
 * @(#)2016年3月8日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dbmigrate;

import com.wellsoft.pt.mt.entity.Tenant;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月8日.1	zhongzh		2016年3月8日		Create
 * </pre>
 * @date 2016年3月8日
 */
public interface TenantSqlExecutor {

    /**
     * 尝试获取租户库的数据源执行脚本
     *
     * @param tenant
     * @param sqlScript
     * @param callback
     * @param holderObj
     */
    public void execute(Tenant tenant, String sqlScript, ExecuteCallback callback, Object... holderObj);

    /**
     * 尝试获取模块的数据源执行脚本,如果模块数据源不存在,则尝试获取租户库的数据源执行脚本
     *
     * @param module
     * @param tenant
     * @param sqlScript
     * @param callback
     * @param holderObj
     */
    public void execute(String module, Tenant tenant, String sqlScript, ExecuteCallback callback, Object... holderObj);
}
