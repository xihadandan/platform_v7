/*
 * @(#)2013-4-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.superadmin.dao.DatabaseConfigDao;
import com.wellsoft.pt.security.superadmin.entity.DatabaseConfig;

import java.util.Collection;
import java.util.List;

/**
 * Description: 数据库管理员服务类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-24.1	rzhu		2013-4-24		Create
 * </pre>
 * @date 2013-4-24
 */
public interface DatabaseConfigService extends JpaService<DatabaseConfig, DatabaseConfigDao, String> {
    /**
     * 根据UUID获取数据库配置
     *
     * @param uuid
     * @return
     */
    DatabaseConfig get(String uuid);

    /**
     * 获取所有的数据库管理员
     *
     * @return
     */
    List<DatabaseConfig> getAll();

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
}
