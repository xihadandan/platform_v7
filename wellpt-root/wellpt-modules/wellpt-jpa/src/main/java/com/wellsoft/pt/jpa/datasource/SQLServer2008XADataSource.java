/*
 * @(#)2013-12-4 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.datasource;

import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import com.wellsoft.pt.jpa.support.CustomSQLServer2008Dialect;
import com.wellsoft.pt.mt.entity.Tenant;

import java.sql.Connection;
import java.sql.SQLException;
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
 * 2013-12-4.1	zhulh		2013-12-4		Create
 * </pre>
 * @date 2013-12-4
 */
public class SQLServer2008XADataSource extends AbstractXADataSource {
    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.jdbc.datasource.DataSource#getDriverClass()
     */
    @Override
    public String getDriverClass() {
        return com.microsoft.sqlserver.jdbc.SQLServerDriver.class.getCanonicalName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.datasource.XADataSource#getXaDatasourceClass()
     */
    @Override
    public String getXaDatasourceClass() {
        return SQLServerXADataSource.class.getCanonicalName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.datasource.DataSource#getDialect()
     */
    @Override
    public String getDialect() {
        return CustomSQLServer2008Dialect.class.getCanonicalName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.jdbc.datasource.DataSource#getType()
     */
    @Override
    public String getType() {
        return DatabaseType.SQLServer2008.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.jdbc.datasource.DataSource#extractDriverProperties(java.util.Properties)
     */
    @Override
    public void extractDriverProperties(Properties properties) {
        driverProperties.put(XADriverProperty.SERVER_NAME, properties.get(XADriverProperty.SERVER_NAME));
        driverProperties.put(XADriverProperty.DATABASE_NAME, properties.get(XADriverProperty.DATABASE_NAME));
        driverProperties.put(XADriverProperty.SELECT_METHOD, properties.get(XADriverProperty.SELECT_METHOD));
        driverProperties.put("user", properties.get(XADriverProperty.USER));
        driverProperties.put("password", properties.get(XADriverProperty.PASSWORD));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.jdbc.datasource.XADataSource#buildInternal(com.wellsoft.TenantEntity.mt.entity.Tenant)
     */
    @Override
    public XADataSource buildInternal(Tenant tenant) {
        String server = tenant.getJdbcServer();
        // String port = tenant.getJdbcPort();
        String databaseName = tenant.getJdbcDatabaseName();
        String username = tenant.getJdbcUsername();
        String password = tenant.getJdbcPassword();

        this.jndiName = databaseName;

        driverProperties.put(XADriverProperty.SERVER_NAME, server);
        driverProperties.put(XADriverProperty.DATABASE_NAME, databaseName);
        driverProperties.put(XADriverProperty.SELECT_METHOD, "cursor");
        driverProperties.put("user", username);
        driverProperties.put("password", password);

        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.jdbc.datasource.DataSource#getConnection()
     */
    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public boolean isEnableJdbc4ConnectionTest() {
        return true;
    }

    @Override
    public boolean isTestQueryEnable() {
        return true;
    }

    @Override
    public String getTestQuery() {
        return "select 1";
    }

    @Override
    public int getMaxIdleTime() {
        return 30 * 60;
    }
}
