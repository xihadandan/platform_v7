/*
 * @(#)2013-2-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.util.dyform;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.tool.hbm2ddl.ConnectionHelper;
import org.jboss.logging.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

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
class DatabaseExporter implements Exporter {
    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
            DatabaseExporter.class.getName());

    private final ConnectionHelper connectionHelper;
    private final SqlExceptionHelper sqlExceptionHelper;

    private final Connection connection;
    private final Statement statement;
    private String uuid = null;

    public DatabaseExporter(ConnectionHelper connectionHelper,
                            SqlExceptionHelper sqlExceptionHelper, String uuid)
            throws SQLException {
        this.connectionHelper = connectionHelper;
        this.sqlExceptionHelper = sqlExceptionHelper;

        connectionHelper.prepare(true);
        connection = connectionHelper.getConnection();
        statement = connection.createStatement();
        this.uuid = uuid;
    }

    @Override
    public boolean acceptsImportScripts() {
        return true;
    }

    @Override
    public void export(String formatted) throws Exception {
        statement.executeUpdate(formatted);
        try {
            String presevedTableKey = "table";
            int index = formatted.indexOf(presevedTableKey);
            String subFormatted = formatted.substring(index + presevedTableKey.length()).trim();
            String qualifiedTblName = subFormatted.substring(0, subFormatted.indexOf(" "));
            String tblName = qualifiedTblName.substring(qualifiedTblName.indexOf(".") + 1);
            formatted = formatted.replace(qualifiedTblName, tblName);
			/*String sql = "insert into dyform_sql_log" + "(UUID,CREATE_TIME, TABLE_NAME, SQL_SCRIPT  , form_uuid)"
					+ "values" + "('" + UuidUtils.createUuid() + "', sysdate, '" + tblName + "', '" + formatted
					+ "', '" + this.uuid + "' )";
			statement.executeUpdate(sql);*/
            // ApplicationContextHolder.getBean(DyformSqlLogService.class).saveSqlLog(formatted, tblName, this.uuid);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        try {
            SQLWarning warnings = statement.getWarnings();
            if (warnings != null) {
                sqlExceptionHelper.logAndClearWarnings(connection);
            }
        } catch (SQLException e) {
            LOG.unableToLogSqlWarnings(e);
        }
    }

    @Override
    public void release() throws Exception {
        try {
            statement.close();
        } finally {
            connectionHelper.release();
        }
    }
}
