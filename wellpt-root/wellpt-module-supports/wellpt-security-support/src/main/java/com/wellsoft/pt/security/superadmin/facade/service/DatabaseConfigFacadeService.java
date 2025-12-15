/*
 * @(#)2018年1月11日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.security.superadmin.entity.DatabaseConfig;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年1月11日.1	chenqiong		2018年1月11日		Create
 * </pre>
 * @date 2018年1月11日
 */
public interface DatabaseConfigFacadeService extends Facade {

    DatabaseConfig getDatabaseConfigByUuid(String uuid);

    /**
     * 保存数据库管理员配置
     *
     * @param databaseAdmin
     */
    DatabaseConfig saveDatabaseConfig(DatabaseConfig databaseAdmin);

    /**
     * 测试数据库的连接性
     *
     * @return
     */
    boolean checkConnectionStatus(String uuid) throws Exception;

    /**
     * 删除数据库配置
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 批量删除数据库配置
     *
     * @param uuid
     */
    void removeAll(Collection<String> uuids);

    /**
     * 如何描述该方法
     *
     * @return
     */
    Object getAll();

}
