/*
 * @(#)2013-2-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.util.dyform;

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
class SuppliedConnectionHelper implements ConnectionHelper {
    private static Logger LOG = LoggerFactory.getLogger(SuppliedConnectionHelper.class);
    private Connection connection;
    private boolean toggleAutoCommit;

    public SuppliedConnectionHelper(Connection connection) {
        this.connection = connection;
    }

    public void prepare(boolean needsAutoCommit) throws SQLException {
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

    public Connection getConnection() {
        return connection;
    }

    public void release() throws SQLException {
        new SqlExceptionHelper().logAndClearWarnings(connection);
        if (toggleAutoCommit) {
            connection.setAutoCommit(false);
        }
        connection = null;
    }
}
