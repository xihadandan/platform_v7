/*
 * @(#)2013-5-29 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.enums;

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
public enum Database {
    // Oracle (any version)
    Oracle("", "", ""),
    // Oracle 9i
    Oracle9i("", "", ""),
    // Oracle 10g
    Oracle10g("", "", ""),
    // Oracle 11g
    Oracle11g("", "", ""),
    // Microsoft SQL Server 2000
    SQLServer2000("com.microsoft.sqlserver.jdbc.SQLServerDriver", "com.microsoft.sqlserver.jdbc.SQLServerXADataSource",
            "org.hibernate.dialect.SQLServerDialect"),
    // Microsoft SQL Server 2005
    SQLServer2005("com.microsoft.sqlserver.jdbc.SQLServerDriver", "com.microsoft.sqlserver.jdbc.SQLServerXADataSource",
            "org.hibernate.dialect.SQLServer2005Dialect"),
    // Microsoft SQL Server 2008
    SQLServer2008("com.microsoft.sqlserver.jdbc.SQLServerDriver", "com.microsoft.sqlserver.jdbc.SQLServerXADataSource",
            "org.hibernate.dialect.SQLServer2008Dialect"),
    // H2 Database
    H2("org.h2.Driver", "org.h2.jdbcx.JdbcDataSource", "org.hibernate.dialect.H2Dialect");

    // 驱动
    private String driver;
    // 分布式事务驱动
    private String xaDataSource;
    // 方言
    private String dialect;

    private Database(String driver, String xaDataSource, String dialect) {
        this.driver = driver;
        this.xaDataSource = xaDataSource;
        this.dialect = dialect;
    }

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver 要设置的driver
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @return the xaDataSource
     */
    public String getXaDataSource() {
        return xaDataSource;
    }

    /**
     * @param xaDataSource 要设置的xaDataSource
     */
    public void setXaDataSource(String xaDataSource) {
        this.xaDataSource = xaDataSource;
    }

    /**
     * @return the dialect
     */
    public String getDialect() {
        return dialect;
    }

    /**
     * @param dialect 要设置的dialect
     */
    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

}
