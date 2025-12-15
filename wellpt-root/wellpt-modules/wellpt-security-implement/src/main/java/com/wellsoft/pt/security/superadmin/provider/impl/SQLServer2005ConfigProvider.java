/*
 * @(#)2013-5-29 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.provider.impl;

import com.wellsoft.pt.security.superadmin.entity.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
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
public class SQLServer2005ConfigProvider extends SQLServer2000ConfigProvider {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.provider.DatabaseConfigProvider#obtainConnection(com.wellsoft.pt.security.superadmin.entity.DatabaseConfig)
     */
    @Override
    protected Connection obtainConnection(DatabaseConfig databaseConfig) throws SQLException {
        String host = databaseConfig.getHost();
        String port = databaseConfig.getPort();
        String loginName = databaseConfig.getLoginName();
        String password = databaseConfig.getPassword();
        String url = "jdbc:sqlserver://" + host + ":" + port + ";databaseName=master";
        return DriverManager.getConnection(url, loginName, password);
    }

}
