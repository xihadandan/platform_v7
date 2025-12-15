/*
 * @(#)2013-5-29 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.provider.impl;

import com.wellsoft.pt.jpa.datasource.XADataSource;
import com.wellsoft.pt.jpa.datasource.XADataSourceFactory;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.security.superadmin.entity.DatabaseConfig;
import com.wellsoft.pt.security.superadmin.provider.DatabaseConfigProvider;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-29.1	zhulh		2013-5-29		Create
 * </pre>
 * @date 2013-5-29
 */
public class Oracle10gConfigProvider extends DatabaseConfigProvider {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.provider.DatabaseConfigProvider#obtainConnection(com.wellsoft.pt.security.superadmin.entity.DatabaseConfig)
     */
    @Override
    protected Connection obtainConnection(DatabaseConfig databaseConfig) throws SQLException {
        XADataSource xaDataSource = XADataSourceFactory.getXADataSource(databaseConfig.getType());

        Tenant tenant = new Tenant();
        tenant.setJdbcServer(databaseConfig.getHost());
        tenant.setJdbcPort(databaseConfig.getPort());
        tenant.setJdbcDatabaseName(databaseConfig.getDatabaseName());
        tenant.setJdbcUsername(databaseConfig.getLoginName());
        tenant.setJdbcPassword(databaseConfig.getPassword());
        xaDataSource.buildInternal(tenant);

        return xaDataSource.getConnection();
    }

}
