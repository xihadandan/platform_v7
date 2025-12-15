/*
 * @(#)2013-2-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.util.dyform;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.tool.hbm2ddl.ConnectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 2013-2-8.1	zhulh		2013-2-8		Create
 * </pre>
 * @date 2013-2-8
 */
class SuppliedConnectionProviderConnectionHelper implements ConnectionHelper {
    private static Logger LOG = LoggerFactory.getLogger(SuppliedConnectionProviderConnectionHelper.class);
    private ConnectionProvider provider;
    private Connection connection;
    private boolean toggleAutoCommit;

    public SuppliedConnectionProviderConnectionHelper(ConnectionProvider provider) {
        this.provider = provider;
    }

    public void prepare(boolean needsAutoCommit) throws SQLException {
        connection = provider.getConnection();
        toggleAutoCommit = needsAutoCommit && !connection.getAutoCommit();
        if (toggleAutoCommit) {
            try {
                connection.commit();
            } catch (Exception ignore) {
                // might happen with a managed connection
                LOG.error(ignore.getMessage(), ignore);
            }
            connection.setAutoCommit(true);
        }
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public void release() throws SQLException {
        // we only release the connection
        if (connection != null) {
            new SqlExceptionHelper().logAndClearWarnings(connection);
            if (toggleAutoCommit) {
                connection.setAutoCommit(false);
            }
            provider.closeConnection(connection);
            connection = null;
        }
    }
}