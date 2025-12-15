/*
 * @(#)2013-12-4 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.datasource;

import com.wellsoft.pt.jpa.support.CustomOracle10gDialect;
import com.wellsoft.pt.mt.entity.Tenant;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
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
public class Oracle10gXADataSource extends AbstractXADataSource {

    private String url;

    private String user;

    private String password;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.jdbc.datasource.DataSource#getDriverClass()
     */
    @Override
    public String getDriverClass() {
        return oracle.jdbc.driver.OracleDriver.class.getCanonicalName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.datasource.XADataSource#getXaDatasourceClass()
     */
    @Override
    public String getXaDatasourceClass() {
        return oracle.jdbc.xa.client.OracleXADataSource.class.getCanonicalName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.datasource.DataSource#getDialect()
     */
    @Override
    public String getDialect() {
        return CustomOracle10gDialect.class.getCanonicalName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.jdbc.datasource.DataSource#getType()
     */
    @Override
    public String getType() {
        return DatabaseType.Oracle10g.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.jdbc.datasource.DataSource#extractDriverProperties(java.util.Properties)
     */
    @Override
    public void extractDriverProperties(Properties properties) {
        driverProperties.put(XADriverProperty.URL, properties.get(XADriverProperty.URL));
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
        String port = tenant.getJdbcPort();
        String databaseName = tenant.getJdbcDatabaseName();
        this.user = tenant.getJdbcUsername();
        this.password = tenant.getJdbcPassword();
        this.jndiName = tenant.getId();
        if (StringUtils.isNotBlank(tenant.getJdbcUrlFormat())) {
            url = tenant.getJdbcUrlFormat();
            url = url.replace("${jdbcPort}", port);
            url = url.replace("${jdbcServer}", server);
            url = url.replace("${jdbcDatabaseName}", databaseName);
        } else {
            url = "jdbc:oracle:thin:@" + server + ":" + port + ":" + databaseName; // 兼容旧方式
        }
        // jdbc:oracle:thin:@192.168.0.249:1521:ORCL
        driverProperties.put(XADriverProperty.URL, url);
        driverProperties.put("user", user);
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
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public boolean isEnableJdbc4ConnectionTest() {
        return false;
    }

    @Override
    public boolean isTestQueryEnable() {
        return true;
    }

    @Override
    public String getTestQuery() {
        return "select 1 from dual";
    }

    @Override
    public int getMaxIdleTime() {
        return 30 * 60;
    }

}
