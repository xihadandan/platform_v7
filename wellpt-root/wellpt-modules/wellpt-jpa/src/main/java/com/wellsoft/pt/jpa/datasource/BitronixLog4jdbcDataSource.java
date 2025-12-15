/*
 * @(#)2013-1-16 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.datasource;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import net.sf.log4jdbc.ConnectionSpy;
import net.sf.log4jdbc.SpyLogFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Description: 封装Bitronix的log4jdbc数据源
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-16.1	zhulh		2013-1-16		Create
 * </pre>
 * @date 2013-1-16
 */
public class BitronixLog4jdbcDataSource extends PoolingDataSource {

    private static final long serialVersionUID = -669035606898026786L;

    /**
     * (non-Javadoc)
     *
     * @see bitronix.tm.resource.jdbc.PoolingDataSource#getConnection()
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (SpyLogFactory.getSpyLogDelegator().isJdbcLoggingEnabled()) {
            return new ConnectionSpy(super.getConnection());
        }
        return super.getConnection();
    }

    /**
     * (non-Javadoc)
     *
     * @see bitronix.tm.resource.jdbc.PoolingDataSource#getConnection(java.lang.String, java.lang.String)
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if (SpyLogFactory.getSpyLogDelegator().isJdbcLoggingEnabled()) {
            return new ConnectionSpy(super.getConnection(username, password));
        }
        return super.getConnection(username, password);
    }

}
