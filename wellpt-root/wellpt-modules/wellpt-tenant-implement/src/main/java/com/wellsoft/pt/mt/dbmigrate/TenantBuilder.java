/*
 * @(#)2013-4-22 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dbmigrate;

import com.wellsoft.pt.mt.entity.Tenant;

import java.util.List;
import java.util.Properties;

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
public interface TenantBuilder {

    /**
     * 用DBA查询数据库是否存在
     *
     * @param tenant
     * @param dbaPorp
     * @param callback
     * @return
     */
    public boolean isDbExist(Tenant tenant, Properties dbaPorp, ExecuteCallback callback);

    /**
     * 创建租户库,不初始化数据
     *
     * @param tenant
     * @param dbaPorp
     * @param callback
     */
    public void buildDb(Tenant tenant, Properties dbaPorp, ExecuteCallback callback);

    /**
     * 初始化数据库
     *
     * @param tenant
     * @param dbaPorp
     * @param callback
     */
    public void migrateDb(Tenant tenant, Properties dbaPorp, ExecuteCallback callback);

    /**
     * 按模块初始化数据库
     *
     * @param tenant
     * @param modules
     * @param dbaPorp
     * @param callback
     */
    public void migrateDb(Tenant tenant, List<String> modules, Properties dbaPorp, ExecuteCallback callback);

    /**
     * 删除对应租户,TODO:ORG数据
     *
     * @param tenant
     * @param dbaPorp
     */
    public void drop(Tenant tenant, Properties dbaPorp);
}
