/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package com.wellsoft.pt.dm.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.ConnectionHelper;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.hibernate.tool.hbm2ddl.Target;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A commandline tool to update a database schema. May also be called from inside an application.
 *
 * （重写：增加日志打印，后续可以考虑语义通俗化解释发生异常的script）
 *
 * @author Christoph Sturm
 * @author Steve Ebersole
 * @modifyBy chenq
 */
public class SchemaUpdate {
    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaUpdate.class.getName());
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SchemaUpdate.class);

    private final Configuration configuration;
    private final ConnectionHelper connectionHelper;
    private final SqlStatementLogger sqlStatementLogger;
    private final SqlExceptionHelper sqlExceptionHelper;
    private final Dialect dialect;

    private final List<Exception> exceptions = new ArrayList<Exception>();

    private Formatter formatter;

    private boolean haltOnError;
    private boolean format = true;
    private String outputFile;
    private String delimiter;

    public SchemaUpdate(Configuration cfg) throws HibernateException {
        this(cfg, cfg.getProperties());
    }

    public SchemaUpdate(Configuration configuration, Properties properties) throws HibernateException {
        this.configuration = configuration;
        this.dialect = Dialect.getDialect(properties);

        Properties props = new Properties();
        props.putAll(dialect.getDefaultProperties());
        props.putAll(properties);
        this.connectionHelper = new ManagedProviderConnectionHelper(props);

        this.sqlExceptionHelper = new SqlExceptionHelper();
        this.sqlStatementLogger = new SqlStatementLogger(false, true);
        this.formatter = FormatStyle.DDL.getFormatter();
    }

    public SchemaUpdate(ServiceRegistry serviceRegistry, Configuration cfg) throws HibernateException {
        this.configuration = cfg;

        final JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
        this.dialect = jdbcServices.getDialect();
        this.connectionHelper = new SuppliedConnectionProviderConnectionHelper(jdbcServices.getConnectionProvider());

        this.sqlExceptionHelper = new SqlExceptionHelper();
        this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
        this.formatter = (sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE).getFormatter();
    }

    private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
        Environment.verifyProperties(properties);
        ConfigurationHelper.resolvePlaceHolders(properties);
        return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings(properties).build();
    }


    /**
     * Execute the schema updates
     *
     * @param script print all DDL to the console
     */
    public void execute(boolean script, boolean doUpdate) {
        execute(Target.interpret(script, doUpdate));
    }

    public void execute(Target target) {
        LOG.runningHbm2ddlSchemaUpdate();

        Connection connection = null;
        Statement stmt = null;
        Writer outputFileWriter = null;

        exceptions.clear();

        try {
            DatabaseMetadata meta;
            try {
                LOG.fetchingDatabaseMetadata();
                connectionHelper.prepare(true);
                connection = connectionHelper.getConnection();
                meta = new DatabaseMetadata(connection, dialect, configuration);
                stmt = connection.createStatement();
            } catch (SQLException sqle) {
                exceptions.add(sqle);
                LOG.unableToGetDatabaseMetadata(sqle);
                throw sqle;
            }

            LOG.updatingSchema();

            if (outputFile != null) {
                LOG.writingGeneratedSchemaToFile(outputFile);
                outputFileWriter = new FileWriter(outputFile);
            }

            List<SchemaUpdateScript> scripts = configuration.generateSchemaUpdateScriptList(dialect, meta);
            for (SchemaUpdateScript script : scripts) {
                String formatted = formatter.format(script.getScript());
                try {
                    if (delimiter != null) {
                        formatted += delimiter;
                    }
//                    if (target.doScript()) {
//                        System.out.println(formatted);
//                     }
                    if (outputFile != null) {
                        outputFileWriter.write(formatted + "\n");
                    }
                    if (target.doExport()) {
                        LOG.debug(script.getScript());
                        stmt.executeUpdate(formatted);
                        logger.info("执行DDL语句成功 : {}", formatted);
                    }
                } catch (SQLException e) {
                    if (!script.isQuiet()) {
                        if (haltOnError) {
                            throw new JDBCException("Error during DDL export", e);
                        }
                        exceptions.add(e);
                        LOG.unsuccessful(script.getScript());
                        logger.error("执行DDL语句异常 : {}", formatted);
                        LOG.error(e.getMessage());
                    }
                }
            }

            LOG.schemaUpdateComplete();

        } catch (Exception e) {
            exceptions.add(e);
            LOG.unableToCompleteSchemaUpdate(e);
        } finally {

            try {
                if (stmt != null) {
                    stmt.close();
                }
                connectionHelper.release();
            } catch (Exception e) {
                exceptions.add(e);
                LOG.unableToCloseConnection(e);
            }
            try {
                if (outputFileWriter != null) {
                    outputFileWriter.close();
                }
            } catch (Exception e) {
                exceptions.add(e);
                LOG.unableToCloseConnection(e);
            }
        }
    }

    /**
     * Returns a List of all Exceptions which occured during the export.
     *
     * @return A List containig the Exceptions occured during the export
     */
    public List getExceptions() {
        return exceptions;
    }

    public void setHaltOnError(boolean haltOnError) {
        this.haltOnError = haltOnError;
    }

    public void setFormat(boolean format) {
        this.formatter = (format ? FormatStyle.DDL : FormatStyle.NONE).getFormatter();
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}


class ManagedProviderConnectionHelper implements ConnectionHelper {
    private Properties cfgProperties;
    private StandardServiceRegistryImpl serviceRegistry;
    private Connection connection;

    public ManagedProviderConnectionHelper(Properties cfgProperties) {
        this.cfgProperties = cfgProperties;
    }

    private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
        Environment.verifyProperties(properties);
        ConfigurationHelper.resolvePlaceHolders(properties);
        return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings(properties).build();
    }

    public void prepare(boolean needsAutoCommit) throws SQLException {
        serviceRegistry = createServiceRegistry(cfgProperties);
        connection = serviceRegistry.getService(ConnectionProvider.class).getConnection();
        if (needsAutoCommit && !connection.getAutoCommit()) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public void release() throws SQLException {
        try {
            releaseConnection();
        } finally {
            releaseServiceRegistry();
        }
    }

    private void releaseConnection() throws SQLException {
        if (connection != null) {
            try {
                new SqlExceptionHelper().logAndClearWarnings(connection);
            } finally {
                try {
                    serviceRegistry.getService(ConnectionProvider.class).closeConnection(connection);
                } finally {
                    connection = null;
                }
            }
        }
    }

    private void releaseServiceRegistry() {
        if (serviceRegistry != null) {
            try {
                serviceRegistry.destroy();
            } finally {
                serviceRegistry = null;
            }
        }
    }
}

class SuppliedConnectionProviderConnectionHelper implements ConnectionHelper {
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
            } catch (Throwable ignore) {
                // might happen with a managed connection
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
