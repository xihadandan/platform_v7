/*
 * @(#)2013-2-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.util.dyform;

import com.google.common.base.Stopwatch;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dyform.implement.definition.service.DyformSqlLogService;
import com.wellsoft.pt.jpa.hibernate.MultiTenantConnectionProviderConnectionHelper;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.*;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
 * 2013-2-8.1	zhulh		2013-2-8		Create
 * </pre>
 * @date 2013-2-8
 */
public class CustomSchemaUpdate {
    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
            SchemaUpdate.class.getName());

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(
            CustomSchemaUpdate.class);

    private final Configuration configuration;
    private final ConnectionHelper connectionHelper;
    private final SqlStatementLogger sqlStatementLogger;
    private final SqlExceptionHelper sqlExceptionHelper;
    private final Dialect dialect;
    private final List<Exception> exceptions = new ArrayList<Exception>();
    private DyformSqlLogService dyformSqlLogService;
    private Formatter formatter;

    private boolean haltOnError;
    private boolean format = true;
    private String outputFile;
    private String delimiter;
    private String uuid = null;

    public CustomSchemaUpdate(Configuration cfg) throws HibernateException {
        this(cfg, cfg.getProperties());
    }

    public CustomSchemaUpdate(Configuration configuration,
                              Properties properties) throws HibernateException {

        this.configuration = configuration;
        this.dialect = Dialect.getDialect(properties);

        Properties props = new Properties();
        props.putAll(dialect.getDefaultProperties());
        props.putAll(properties);
        this.connectionHelper = new ManagedProviderConnectionHelper(props);

        this.sqlExceptionHelper = new SqlExceptionHelper();
        this.sqlStatementLogger = new SqlStatementLogger(false, true);
        this.formatter = FormatStyle.DDL.getFormatter();
        dyformSqlLogService = ApplicationContextHolder.getBean(
                DyformSqlLogService.class);
    }

    public CustomSchemaUpdate(ServiceRegistry serviceRegistry, Configuration cfg, String uuid)
            throws HibernateException {
        this.configuration = cfg;
        this.uuid = uuid;
        final JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
        this.dialect = jdbcServices.getDialect();
        if (Config.MULTI_TENANCY) {
            this.connectionHelper = new MultiTenantConnectionProviderConnectionHelper();
        } else {
            this.connectionHelper = new SuppliedConnectionProviderConnectionHelper(
                    serviceRegistry.getService(ConnectionProvider.class));
        }
        dyformSqlLogService = ApplicationContextHolder.getBean(
                DyformSqlLogService.class);
        this.sqlExceptionHelper = new SqlExceptionHelper();
        this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
        this.formatter = (sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE).getFormatter();
    }

    private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
        Environment.verifyProperties(properties);
        ConfigurationHelper.resolvePlaceHolders(properties);
        return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings(
                properties).build();
    }

    public static void main(String[] args) {
        try {
            Configuration cfg = new Configuration();

            boolean script = true;
            // If true then execute db updates, otherwise just generate and
            // display updates
            boolean doUpdate = true;
            String propFile = null;

            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("--")) {
                    if (args[i].equals("--quiet")) {
                        script = false;
                    } else if (args[i].startsWith("--properties=")) {
                        propFile = args[i].substring(13);
                    } else if (args[i].startsWith("--config=")) {
                        cfg.configure(args[i].substring(9));
                    } else if (args[i].startsWith("--text")) {
                        doUpdate = false;
                    } else if (args[i].startsWith("--naming=")) {
                        cfg.setNamingStrategy(
                                (NamingStrategy) ReflectHelper.classForName(args[i].substring(9))
                                        .newInstance());
                    }
                } else {
                    cfg.addFile(args[i]);
                }

            }

            if (propFile != null) {
                Properties props = new Properties();
                props.putAll(cfg.getProperties());
                props.load(new FileInputStream(propFile));
                cfg.setProperties(props);
            }

            StandardServiceRegistryImpl serviceRegistry = createServiceRegistry(
                    cfg.getProperties());
            try {
                new SchemaUpdate(serviceRegistry, cfg).execute(script, doUpdate);
            } finally {
                serviceRegistry.destroy();
            }
        } catch (Exception e) {
            LOG.unableToRunSchemaUpdate(e);
        }
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
                DatabaseMetaData dbmd = connection.getMetaData();
                logger.debug(">>>>>>>>>>>>>>>表单执行脚本时的数据库连接信息:>>>>>>>>>>>>>>> ");

                logger.debug("数据库的系统函数的逗号分隔列表: " + dbmd.getSystemFunctions());
                logger.debug("数据库的时间和日期函数的逗号分隔列表: " + dbmd.getTimeDateFunctions());
                logger.debug("数据库的字符串函数的逗号分隔列表: " + dbmd.getStringFunctions());
                logger.debug("数据库供应商用于 'schema' 的首选术语: " + dbmd.getSchemaTerm());
                logger.debug("数据库URL: " + dbmd.getURL());
                logger.debug("用户名: " + dbmd.getUserName());
                logger.debug("是否允许只读:" + dbmd.isReadOnly());
                logger.debug("数据库的产品名称:" + dbmd.getDatabaseProductName());
                logger.debug("数据库的版本:" + dbmd.getDatabaseProductVersion());
                logger.debug("驱动程序的名称:" + dbmd.getDriverName());
                logger.debug("驱动程序的版本:" + dbmd.getDriverVersion());

                logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                meta = new DatabaseMetadata(connection, dialect, configuration);
                stmt = connection.createStatement();
            } catch (SQLException sqle) {
                exceptions.add(sqle);
                LOG.unableToGetDatabaseMetadata(sqle);
                throw new HibernateException(sqle.getMessage());
            }

            LOG.updatingSchema();
            Stopwatch timer = Stopwatch.createStarted();
            logger.debug(">>> 开始解析表单DDL语句");
            List<SchemaUpdateScript> scripts = configuration.generateSchemaUpdateScriptList(dialect,
                    meta);
            logger.debug(">>> 结束解析表单DDL语句，耗时：{}", timer.stop());
            timer.reset().start();
            logger.debug(">>> 开始执行表单DDL语句 ");
            // ddl无法回滚，所以这里的日志显得非常重要，如果出现ddl失败,只能通过日志把ddl执行过的信息还原
            for (SchemaUpdateScript script : scripts) {

                String formatted = formatter.format(script.getScript());
                try {
                    if (delimiter != null) {
                        formatted += delimiter;
                    }

                    if (target.doExport()) {
                        LOG.debug(script.getScript());
                        logger.info("执行表单DDL语句: {} ", formatted);
                        stmt.executeUpdate(formatted);
                        try {
                            String presevedTableKey = "table";
                            int index = formatted.indexOf(presevedTableKey);
                            if (index == -1) {// 非修改table的script
                                continue;
                            }
                            String subFormatted = formatted.substring(
                                    index + presevedTableKey.length()).trim();
                            String qualifiedTblName = subFormatted.substring(0,
                                    subFormatted.indexOf(" "));
                            String tblName = qualifiedTblName.substring(
                                    qualifiedTblName.indexOf(".") + 1);
                            formatted = formatted.replace(qualifiedTblName, tblName);
                            // dyformSqlLogService.saveSqlLog(formatted, tblName, uuid);
                        } catch (Exception e) {
                            logger.error("执行表单DDL语句异常：", e);
                        }

                    }

                } catch (SQLException e) {
                    logger.error("执行数据库表单相关DDL语句处理异常：", e);
                }
            }
            logger.debug(">>> 结束执行表单DDL语句，耗时：{}", timer.stop());
            LOG.schemaUpdateComplete();

        } catch (HibernateException e) {
            logger.error("执行数据库表单相关DDL语句处理异常：", e);
            throw e;
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
