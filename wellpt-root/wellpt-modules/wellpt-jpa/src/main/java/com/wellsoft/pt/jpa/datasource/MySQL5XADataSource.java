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
 * mysql数据源
 */
public class MySQL5XADataSource extends AbstractXADataSource {

    private String url;

    private String user;

    private String password;

    @Override
    public String getDriverClass() {
        return com.mysql.jdbc.Driver.class.getCanonicalName();
    }


    @Override
    public String getXaDatasourceClass() {
        return com.mysql.jdbc.jdbc2.optional.MysqlXADataSource.class.getCanonicalName();
    }


    @Override
    public String getDialect() {
        return CustomOracle10gDialect.class.getCanonicalName();
    }

    @Override
    public String getType() {
        return DatabaseType.MySQL5.getName();
    }


    @Override
    public void extractDriverProperties(Properties properties) {
        driverProperties.put(XADriverProperty.URL, properties.get(XADriverProperty.URL));
        driverProperties.put("user", properties.get(XADriverProperty.USER));
        driverProperties.put("password", properties.get(XADriverProperty.PASSWORD));
        driverProperties.put("pinGlobalTxToPhysicalConnection", true);
        driverProperties.put("useSSL", true);

    }


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
            url = "jdbc:mysql://" + server + ":" + port + "/" + databaseName + "?useUnicode=true&characterEncoding=utf-8&useSSL=true"; // 兼容旧方式
        }
        driverProperties.put(XADriverProperty.URL, url);
        driverProperties.put("user", user);
        driverProperties.put("password", password);
        return this;
    }

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
