package com.wellsoft.pt.mt.dbmigrate;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.i18n.MsgUtils;
import com.wellsoft.pt.mt.dbmigrate.module.CommonMigrateModule;
import com.wellsoft.pt.mt.dbmigrate.module.MessageMigrateModule;
import com.wellsoft.pt.mt.dbmigrate.module.OrgMigrateModule;
import com.wellsoft.pt.mt.entity.Tenant;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.SqlScript;
import org.flywaydb.core.internal.util.PlaceholderReplacer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class AbstractFlywayMigrator implements TenantBuilder, TenantSqlExecutor, IMigrateCommon {
    /**
     * propUrl：JDBC的地址
     */
    public static final String propUrl = "url";
    // Driver prop
    /**
     * propDeiver：JDBC的驱动
     */
    public static final String propDeiver = "driver";
    /**
     * propUsername：JDBC的用户
     */
    public static final String propUsername = "user";
    /**
     * propPassword：JDBC的密码
     */
    public static final String propPassword = "password";
    // -Djdbc.dba.user=sys -Djdbc.dba.password=wellpt
    public static final String JDBC_DB_TEMPLATE = "jdbc.db.template";
    public static final String JDBC_DBA_USER = "jdbc.dba.user";
    public static final String JDBC_DBA_PASSWORD = "jdbc.dba.password";
    public static final String JDBC_PREFIX_ORG = OrgMigrateModule.JDBC_PREFIX;
    public static final String JDBC_PREFIX_CIS = MessageMigrateModule.JDBC_PREFIX;
    public static final String JDBC_PREFIX_COMMON = CommonMigrateModule.JDBC_PREFIX;// "multi.tenancy.common.";
    public static final String JDBC_PREFIX_TENANT = "multi.tenancy.tenant.";
    public static final String JDBC_PREFIX_TENANT_ID = JDBC_PREFIX_TENANT + "id";
    public static final String JDBC_PREFIX_TENANT_STATUS = JDBC_PREFIX_TENANT + "status";
    public static final String JDBC_PREFIX_TENANT_DEFAULT = JDBC_PREFIX_TENANT + "default";
    /**
     * DEFAULT_DB_TEMPLATE:数据库授权模板
     */
    public static final String DEFAULT_DB_TEMPLATE = "db_template.sql";
    // template
    // status
    public static final Integer STATUS_SUCCESS = 1;
    public static final Integer STATUS_FAIL = 0;
    // db build status -> Tenant.status
    public static final int DB_STATUS_ALREADY_EXIST = Tenant.STATUS_TO_REVIEW;
    public static final int DB_STATUS_BUILD_SUCCESS = Tenant.STATUS_ENABLED;
    public static final int DB_STATUS_DROP_SUCCESS = Tenant.STATUS_DISENABLED;
    protected Logger logger = LoggerFactory.getLogger(AbstractFlywayMigrator.class);
    /**
     * 公共库配置commonProps
     */
    protected Properties commonProps;
    /**
     * 默认的租户数据库配置tenantProps
     */
    protected Properties tenantProps;
    private boolean clean;
    private boolean repair = true;
    private String tenantQuery = "select t.id,t.name,t.account,t.database_config_uuid,t.jdbc_database_name,t.jdbc_server,t.jdbc_port,t.jdbc_username,t.jdbc_password,t.jdbc_type,t.status from mt_tenant t order by t.create_time desc";

    final public void init() {
        DataSource dataSource = null;
        JdbcTemplate jdbcTemplate = null;
        try {
            Properties dbaProp = new Properties();
            dbaProp.setProperty(propUsername, System.getProperty(JDBC_DBA_USER, ""));
            dbaProp.setProperty(propPassword, System.getProperty(JDBC_DBA_PASSWORD, ""));
            dbaProp.setProperty(JDBC_DB_TEMPLATE, System.getProperty(JDBC_DB_TEMPLATE, ""));
            List<IMigrateModule> modules = new ArrayList<IMigrateModule>();
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            Map<String, IMigrateModule> moduleMap = applicationContext.getBeansOfType(IMigrateModule.class);
            for (IMigrateModule module : MigratorUtils.sortedModules(moduleMap.values())) {
                if (module instanceof IMigrateCommon == false/* 租户模块忽略 */) {
                    modules.add(module);
                    continue;
                }
                this.buildCommon(module, dbaProp, null);
            }
            dataSource = this.getCommonDataSource();
            jdbcTemplate = new JdbcTemplate(dataSource);
            List<Tenant> tenants = jdbcTemplate.query(tenantQuery, new String[]{}, getTenantRowMapper());
            if (tenants == null || tenants.size() <= 0) {
                logger.info("no tenant geted,tenant migrate ignore");
                return;
            }
            for (Tenant tenant : tenants) {
                this.buildTenant(modules, tenant, dbaProp, null);
            }
        } finally {
            if (dataSource != null && dataSource instanceof DriverDataSource) {
                ((DriverDataSource) dataSource).close();// no exception
            }
        }
    }

    @Override
    public boolean isDbExist(Tenant tenant, Properties dbaProp, ExecuteCallback callback) {
        String tnUser = tenant == null ? "nil tenant" : tenant.getAccount();
        if (validate(tenant) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateTenant", tnUser);
            executed(callback, null, tenant, STATUS_FAIL, message);
            throw new RuntimeException(message);
        } else if (dbaPropValidate(dbaProp) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateDbaProp", dbaProp);
            executed(callback, null, tenant, STATUS_FAIL, message);
            logger.info(message);
            throw new RuntimeException(message);// return false;
        }
        DataSource dbDataSource = fetchDbDataSource(tenant, dbaProp);
        if (dbDataSource == null) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateDbaProp", dbaProp);
            executed(callback, null, tenant, STATUS_FAIL, message);
            logger.info(message);
            throw new RuntimeException(message);// return false;
        }
        JdbcTemplate dbTemplate;
        try {
            dbTemplate = new JdbcTemplate(dbDataSource);
            return isTenantExist(dbTemplate, tenant.getJdbcUsername());
        } finally {
            if (dbDataSource != null && dbDataSource instanceof DriverDataSource) {
                ((DriverDataSource) dbDataSource).close();// no exception
            }
        }
    }

    @Override
    public void buildDb(Tenant tenant, Properties dbaProp, ExecuteCallback callback) {
        String tnUser = tenant == null ? "nil tenant" : tenant.getAccount();
        if (validate(tenant) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateTenant", tnUser);
            executed(callback, null, tenant, STATUS_FAIL, message);
            throw new RuntimeException(message);
        }
        if (doDatabaseBuild(tenant, dbaProp, (IMigrateCommon) this, callback) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.createDbFail", tnUser);
            executed(callback, null, tenant, STATUS_FAIL, message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public final/* 不能再重写 */void migrateDb(Tenant tenant, Properties dbaProp, ExecuteCallback callback) {
        String tnUser = tenant == null ? "nil tenant" : tenant.getAccount();
        if (validate(tenant) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateTenant", tnUser);
            executed(callback, null, tenant, STATUS_FAIL, message);
            throw new RuntimeException(message);
        }
        List<IMigrateModule> imodules = new ArrayList<IMigrateModule>();
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        Map<String, IMigrateModule> moduleMap = applicationContext.getBeansOfType(IMigrateModule.class);
        for (IMigrateModule module : MigratorUtils.sortedModules(moduleMap.values())) {
            if (module instanceof IMigrateCommon == false) {
                imodules.add(module);
            }
        }
        if (this.buildTenant(imodules, tenant, dbaProp, callback) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.migrateDbFail", tnUser);
            executed(callback, null, tenant, STATUS_FAIL, message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public/* 不能再重写 */void migrateDb(Tenant tenant, List<String> modules, Properties dbaProp, ExecuteCallback callback) {
        String tnUser = tenant == null ? "nil tenant" : tenant.getAccount();
        if (validate(tenant) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateTenant", tnUser);
            executed(callback, null, tenant, STATUS_FAIL, message);
            throw new RuntimeException(message);
        }
        if (modules == null || modules.size() <= 0) {
            // 创建空库
            modules = modules != null ? modules : new ArrayList<String>();
            logger.info("tenant[" + tnUser + "] modules contain no module");
        }
        List<IMigrateModule> imodules = new ArrayList<IMigrateModule>();
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        Map<String, IMigrateModule> moduleMap = applicationContext.getBeansOfType(IMigrateModule.class);
        for (IMigrateModule module : MigratorUtils.sortedModules(moduleMap.values())) {
            if (module instanceof IMigrateCommon == false && modules.contains(module.getCode())) {
                imodules.add(module);
            }
        }
        if (this.buildTenant(imodules, tenant, dbaProp, callback) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.migrateDbFail", tnUser);
            executed(callback, null, tenant, STATUS_FAIL, message);
            throw new RuntimeException(message);
        }
    }

    private/* 不能再重写 */boolean buildCommon(IMigrateModule module, Properties dbaProp, ExecuteCallback callback) {
        DataSource slaveDS;
        try {
            slaveDS = ((IMigrateCommon) module).getDataSource();
        } catch (Exception t) {
            slaveDS = null;
            logger.info("ignore getCommonDataSource error, try again...", t);
        }
        Tenant tenant = null;
        if (slaveDS == null && module instanceof IMigrateCommon) {
            Properties comProp = ((IMigrateCommon) module).getProperties();
            tenant = new Tenant();
            tenant.setJdbcType(Config.getValue("database.type"));
            tenant.setId(Config.getValue(JDBC_PREFIX_TENANT_DEFAULT));
            tenant.setJdbcUsername(comProp.getProperty(IMigrateCommon.JDBC_USERNAME));
            tenant.setJdbcPassword(comProp.getProperty(IMigrateCommon.JDBC_PASSWORD));
            tenant.setJdbcServer(comProp.getProperty(IMigrateCommon.JDBC_SERVER_NAME));
            tenant.setJdbcDatabaseName(comProp.getProperty(IMigrateCommon.JDBC_DATABASE_NAME));
            tenant.setStatus(Tenant.STATUS_ENABLED); // default tenant enable
            // 公共库JDBC_URL即为DBA的URL
            dbaProp.setProperty(propUrl, comProp.getProperty(IMigrateCommon.JDBC_URL));
            if (doDatabaseBuild(tenant, dbaProp, (IMigrateCommon) module, callback)) {
                slaveDS = ((IMigrateCommon) module).getDataSource();
            }
        }

        if (slaveDS == null/* 公共库不存在,且创建失败 */) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.moduleNoAvaiableDatasource", module.getCode());
            executed(callback, module, null, STATUS_FAIL, message);
            throw new RuntimeException(message);// return false;
        }
        try {
            this.doMigrateModule(module, slaveDS, getPlaceHolders(tenant));
            String message = MsgUtils.getMessage("tenant.dbmigrate.moduleSuccessMigrate", module.getCode());
            executed(callback, module, null, STATUS_SUCCESS, message);
        } finally {
            if (slaveDS != null && slaveDS instanceof DriverDataSource) {
                ((DriverDataSource) slaveDS).close();// no exception
            }
        }
        return true;
    }

    private/* 不能再重写 */boolean buildTenant(List<IMigrateModule> modules, Tenant tenant, Properties dbaProp,
                                          ExecuteCallback callback) {
        DataSource slaveDS;
        // 尝试连接租户库,不存在则创建
        try {
            slaveDS = fetchAvaiableDataSource(tenant);
        } catch (Exception t) {
            slaveDS = null;
            // ignore Throwable
            logger.info("ignore fetchAvaiableDataSource error, try again...", t);
        }
        if (slaveDS == null && doDatabaseBuild(tenant, dbaProp, (IMigrateCommon) this, callback)) {
            slaveDS = fetchAvaiableDataSource(tenant);
        }
        String tnUser = tenant == null ? "nil tenant" : tenant.getAccount();
        if (slaveDS == null/* 租户库不存在,且租户创建失败 */) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.tenantNoAvaiableDatasource", tnUser);
            executed(callback, null, tenant, STATUS_FAIL, message);
            throw new RuntimeException(message);// return false;
        }
        try {
            for (IMigrateModule module : modules) {
                if (module instanceof AbstractMigrateModule) /*异构租户构建*/ {
                    ((AbstractMigrateModule) module).setType(tenant.getJdbcType());
                }
                this.doMigrateModule(module, slaveDS, getPlaceHolders(tenant));
                String message = MsgUtils.getMessage("tenant.dbmigrate.tenantSuccessMigrateModule", tnUser,
                        module.getCode());
                executed(callback, module, tenant, STATUS_SUCCESS, message);
            }
        } finally {
            if (slaveDS != null && slaveDS instanceof DriverDataSource) {
                ((DriverDataSource) slaveDS).close();// no exception
            }
        }
        return true;
    }

    @Override
    public final/* 不能再重写 */void drop(Tenant tenant, Properties dbaPorp) {
        String tnUser = tenant == null ? "nil tenant" : tenant.getAccount();
        if (validate(tenant) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateTenant", tnUser);
            throw new RuntimeException(message);
        }
        if (this.doDatabaseDrop(tenant, this, dbaPorp) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.dropDbFail", tnUser);
            throw new RuntimeException(message);
        }
    }

    protected boolean doDatabaseDrop(Tenant tenant, IMigrateCommon commonModule, Properties dbaProp) {
        String tnType = tenant.getJdbcType(), tnUser = tenant == null ? "nil tenant" : tenant.getJdbcUsername();
        if (validate(tenant) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateTenant", tnUser);
            throw new RuntimeException(message);
        } else if (dbaPropValidate(dbaProp) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateDbaProp", dbaProp);
            throw new RuntimeException(message);
        }
        String dropDbTemplate;
        if (StringUtils.isBlank(dropDbTemplate = getDropDdTempalte())) /* 默认模板不存在 */ {
            throw new RuntimeException("Scheam[" + tnUser + "] not dropDbTemplate,ignore dropDB[" + tnType + "]");
        }
        DataSource dbDataSource = fetchDbDataSource(tenant, dbaProp);
        if (dbDataSource == null) {
            throw new RuntimeException("nil DbDataSource for dbaPorp[" + dbaProp + "],ignore dropDb");
        }
        JdbcTemplate dbTemplate;
        try {
            Map<String, String> placeHolders = getPlaceHolders(tenant);
            try {
                dbTemplate = new JdbcTemplate(dbDataSource);
                if (commonModule.beforeDropDb(dbDataSource, dbTemplate, tnUser) == false) {
                    logger.info("Scheam[" + tnUser + "] is not exist,ignore dropDB[" + tnType + "]");
                    return true;
                }
                PlaceholderReplacer placeHolder = new PlaceholderReplacer(placeHolders, "${", "}");
                String dropTenantSql = placeHolder.replacePlaceholders(dropDbTemplate);
                this.execute(dbDataSource, null, dropTenantSql);
                logger.info("drop Scheam[" + tnUser + "][" + tnType + "] success:");
                tenant.setStatus(DB_STATUS_DROP_SUCCESS);
                return true; // build success
            } catch (Exception t) {
                throw new RuntimeException(
                        "dropDB Scheam[" + tnUser + "][" + tnType + "]error[" + t.getMessage() + "]", t);
            } finally {
                placeHolders.put(JDBC_PREFIX_TENANT_STATUS, String.valueOf(tenant.getStatus()));// 更新Status
                IMigrateModule module = null;
                String moduleCode = "nil module";
                if (commonModule instanceof IMigrateModule) {
                    module = (IMigrateModule) commonModule;
                    moduleCode = module.getCode();
                }
                if (sqlCallback(module, dbDataSource, commonModule.afterDropDb(tenant.getJdbcType()), placeHolders)) {
                    logger.info("Module[" + moduleCode + "]success done sqlCallback[afterDropDb]");
                }
            }
        } finally {
            if (dbDataSource != null && dbDataSource instanceof DriverDataSource) {
                ((DriverDataSource) dbDataSource).close();// no exception
            }
        }

    }

    /**
     * 创建数据库
     *
     * @param tenant
     * @return 成功->true/失败->false
     */
    protected boolean doDatabaseBuild(Tenant tenant, Properties dbaProp, IMigrateCommon commonModule,
                                      ExecuteCallback callback) {
        String tnUser = tenant == null ? "nil tenant" : tenant.getJdbcUsername();
        if (validate(tenant) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateScheam", tnUser);
            executed(callback, null, tenant, STATUS_FAIL, message);
            logger.info(message);
            throw new RuntimeException(message);// return false;
        } else if (dbaPropValidate(dbaProp) == false) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateDbaProp", dbaProp);
            executed(callback, null, tenant, STATUS_FAIL, message);
            logger.info(message);
            throw new RuntimeException(message);// return false;
        }
        DataSource dbDataSource = fetchDbDataSource(tenant, dbaProp);
        if (dbDataSource == null) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateDbaProp", dbaProp);
            executed(callback, null, tenant, STATUS_FAIL, message);
            logger.info(message);
            throw new RuntimeException(message);// return false;
        }
        String defaultDbTemplate = dbaProp.getProperty(JDBC_DB_TEMPLATE);
        if (StringUtils.isBlank(defaultDbTemplate)) {
            defaultDbTemplate = getDefaultDbTemplate(tenant);
        }
        if (StringUtils.isBlank(defaultDbTemplate)) /* 默认模板不存在 */ {
            String message = MsgUtils.getMessage("tenant.dbmigrate.dbTemplateNoFound", DEFAULT_DB_TEMPLATE, tnUser);
            executed(callback, null, tenant, STATUS_FAIL, message);
            logger.info(message);
            throw new RuntimeException(message);// return false;
        }
        JdbcTemplate dbTemplate;
        try {
            Map<String, String> placeHolders = getPlaceHolders(tenant);
            tenant.setStatus(DB_STATUS_ALREADY_EXIST);// 数据库存在,用户名密码待审核
            try {
                dbTemplate = new JdbcTemplate(dbDataSource);
                if (commonModule.beforeBuildDb(dbDataSource, dbTemplate, tnUser) == false) {
                    String message = MsgUtils.getMessage("tenant.dbmigrate.scheamAlreadyExist", tnUser);
                    executed(callback, null, tenant, STATUS_SUCCESS, message);
                    logger.info(message);
                    return true;
                }
                PlaceholderReplacer placeHoder = new PlaceholderReplacer(placeHolders, "${", "}");
                String createTenantSql = placeHoder.replacePlaceholders(defaultDbTemplate);
                this.execute(dbDataSource, null, createTenantSql);
                tenant.setStatus(DB_STATUS_BUILD_SUCCESS);// 数据库创建成功,用户名密码即为传入
                String message = MsgUtils.getMessage("tenant.dbmigrate.buildDbSuccess", tnUser);
                executed(callback, null, tenant, STATUS_SUCCESS, message);
                logger.info(message);
                // return true; // build success
            } catch (Exception t) {
                String message = MsgUtils.getMessage("tenant.dbmigrate.buildDbFail", tnUser, t.getMessage());
                executed(callback, null, tenant, STATUS_FAIL, message);
                logger.error(message);
                throw new RuntimeException(message, t);// return false;
            } finally {
                placeHolders.put(JDBC_PREFIX_TENANT_STATUS, String.valueOf(tenant.getStatus()));// 更新Status
                IMigrateModule module = null;
                String moduleCode = "nil module";
                if (commonModule instanceof IMigrateModule) {
                    module = (IMigrateModule) commonModule;
                    moduleCode = module.getCode();
                }
                if (sqlCallback(module, dbDataSource, commonModule.afterBuildDb(tenant.getJdbcType()), placeHolders)) {
                    logger.info("Module[" + moduleCode + "]success done sqlCallback[afterBuildDb]");
                }
            }
        } finally {
            if (dbDataSource != null && dbDataSource instanceof DriverDataSource) {
                ((DriverDataSource) dbDataSource).close();// no exception
            }
        }
        return true;
    }

    public void execute(Tenant tenant, String sqlScript, ExecuteCallback callback, Object... holderObj) {
        this.execute((String) null, tenant, sqlScript, callback, holderObj);
    }

    public void execute(String iModule, Tenant tenant, String sqlScript, ExecuteCallback callback, Object... holderObj) {
        IMigrateModule module = null;
        String tnUser = tenant == null ? "nil tenant" : tenant.getAccount();
        if (StringUtils.isBlank(sqlScript) || validate(tenant) == false) {
            // 执行公共模块时,tenant也不能为空,用于记录日志
            String message = MsgUtils.getMessage("tenant.dbmigrate.invalidateTenant", tnUser);
            executed(callback, null, tenant, STATUS_FAIL, message);
            return;
        } else if (StringUtils.isNotBlank(iModule)) /* 获取执行模块 */ {
            ApplicationContext appContext = ApplicationContextHolder.getApplicationContext();
            Map<String, IMigrateModule> moduleMap = appContext.getBeansOfType(IMigrateModule.class);
            for (IMigrateModule lmodule : MigratorUtils.sortedModules(moduleMap.values())) {
                if (lmodule.getCode().equalsIgnoreCase(iModule)) {
                    module = lmodule;// 模块匹配完成
                    break;
                }
            }
        }
        this.execute(tenant, module, sqlScript, callback, holderObj);
    }

    private void execute(Tenant tenant, IMigrateModule module, String script, ExecuteCallback callback,
                         Object... holderObj) {
        String tnUser = tenant == null ? "nil tenant" : tenant.getAccount();
        String mCode = module == null ? "nil module" : module.getCode();
        DataSource slaveDS;
        try {
            slaveDS = null;
            int tryCount = 0;// 重试3次
            while (slaveDS == null && module != null && module instanceof IMigrateCommon && tryCount++ < 3) {
                slaveDS = ((IMigrateCommon) module).getDataSource();
                if (slaveDS == null/* 等待三秒后重试 */) {
                    Thread.sleep(1000 * 3);
                }
            }
            /**
             if (module != null && module instanceof IMigrateCommon) {
             slaveDS = ((IMigrateCommon) module).getDataSource();
             }
             */
        } catch (Exception t) {
            slaveDS = null;
            // ignore Throwable
            logger.info("noAvaiableDataSource error, try again...", t);
        }
        if (slaveDS == null && validate(tenant)) {
            slaveDS = fetchAvaiableDataSource(tenant);
        }
        if (slaveDS == null/* 公共模块不存在,租户库不存在 */) {
            String message = MsgUtils.getMessage("tenant.dbmigrate.tenantModuleNoAvaiableDatasource", tnUser, mCode);
            executed(callback, null, tenant, STATUS_FAIL, message);
            throw new RuntimeException(message);
        }
        try {
            Map<String, String> placeHolders = getPlaceHolders(holderObj);
            if (sqlCallback(module, slaveDS, module != null ? module.beforeExecute() : null, placeHolders)) {
                logger.info("Module[" + mCode + "]success done sqlCallback[beforeExecute]");
            }
            PlaceholderReplacer placeHolder = new PlaceholderReplacer(placeHolders, "${", "}");
            try {
                this.execute(slaveDS, null, placeHolder.replacePlaceholders(script));
                String message = MsgUtils.getMessage("tenant.dbmigrate.tenantSuccessMigrateModule", tnUser, mCode);
                executed(callback, null, tenant, STATUS_SUCCESS, message);
            } catch (Exception t) {
                String tmsg = t.getMessage();
                String rmsg = MsgUtils.getMessage("tenant.dbmigrate.tenantFailMigrateModule", tnUser, mCode, tmsg);
                throw new RuntimeException(rmsg, t);
            } finally {
                if (sqlCallback(module, slaveDS, module != null ? module.afterExecute() : null, placeHolders)) {
                    logger.info("Module[" + mCode + "]success done sqlCallback[afterExecute]");
                }
            }
        } finally {
            if (slaveDS != null && slaveDS instanceof DriverDataSource) {
                ((DriverDataSource) slaveDS).close();// no exception
            }
        }

    }

    private void execute(DataSource dbDataSource, Schema<DbSupport> masterSchema, String script) throws SQLException {
        boolean schemaChange = false;
        org.flywaydb.core.internal.dbsupport.JdbcTemplate dbTemplate = null;
        DbSupport dbSupportUserObjects = null;
        Schema<DbSupport> originalSchemaUserObjects = null;// ignore type
        try {
            dbTemplate = new org.flywaydb.core.internal.dbsupport.JdbcTemplate(dbDataSource.getConnection(), 0);
            dbSupportUserObjects = DbSupportFactory.createDbSupport(dbDataSource.getConnection(), false);
            originalSchemaUserObjects = dbSupportUserObjects.getCurrentSchema();
            schemaChange = masterSchema != null && masterSchema.equals(originalSchemaUserObjects) == false;
            if (schemaChange) {
                dbSupportUserObjects.setCurrentSchema(masterSchema);
            }
            SqlScript sqlScript = new SqlScript(script, dbSupportUserObjects);
            sqlScript.execute(dbTemplate);
        } finally {
            if (dbSupportUserObjects != null && schemaChange) {
                dbSupportUserObjects.setCurrentSchema(originalSchemaUserObjects);
            }
        }
    }

    private void executed(ExecuteCallback callback, IMigrateModule module, Tenant tenant, Integer status, String message) {
        if (callback == null) {
            return;
        }
        try {
            callback.onExecuted(module, tenant, status, message);
        } catch (Exception t) {
            logger.debug("error execute callback", t);
            return;
            // ignore exception
        }
    }

    private RowMapper<Tenant> getTenantRowMapper() {
        return new RowMapper<Tenant>() {
            public Tenant mapRow(ResultSet rs, int rowNum) throws SQLException {
                Tenant tenant = new Tenant();
                tenant.setId(rs.getString(1));
                tenant.setName(rs.getString(2));
                tenant.setAccount(rs.getString(3));
                tenant.setDatabaseConfigUuid(rs.getString(4));
                tenant.setJdbcDatabaseName(rs.getString(5));
                tenant.setJdbcServer(rs.getString(6));
                tenant.setJdbcPort(rs.getString(7));
                tenant.setJdbcUsername(rs.getString(8));
                tenant.setJdbcPassword(rs.getString(9));
                tenant.setJdbcType(rs.getString(10));
                tenant.setStatus(rs.getInt(11));
                return tenant;
            }
        };
    }

    private boolean isTenantExist(JdbcTemplate dbTemplate, String tenant) {
        if (StringUtils.isBlank(tenant) || StringUtils.isBlank(getDbExistQuery()))/* 默认存在 */ {
            logger.info("default for tenantExist : Blank[" + (StringUtils.isBlank(tenant) ? "tenant" : "Query") + "]");
            return true;
        }
        Integer ctn = 1;
        try {
            ctn = dbTemplate.queryForObject(getDbExistQuery(), new String[]{tenant}, Integer.class);
        } catch (Exception t) {
            logger.error("error query dbExist, default tenant[" + tenant + "] exist", t);
        }
        return ctn != null && ctn > 0;
    }

    public DataSource getCommonDataSource() {
        String url = commonProps.getProperty(propUrl);
        String driver = commonProps.getProperty(propDeiver);
        return MigratorUtils.getDataSource(driver, url, commonProps);
    }

    protected boolean validate(Tenant tenant) {
        if (tenant == null || StringUtils.isBlank(tenant.getJdbcType())
                || StringUtils.isBlank(tenant.getJdbcUsername()) || StringUtils.isBlank(tenant.getJdbcPassword())) {
            return false;
        }
        return true;
    }

    /**
     * 获取租户数据源
     *
     * @param tenant
     * @return
     */
    protected DataSource fetchAvaiableDataSource(Tenant tenant) {
        return null;
    }

    protected boolean dbaPropValidate(Properties dbaProp) {
        if (dbaProp == null || StringUtils.isBlank(dbaProp.getProperty(propUsername))
                || StringUtils.isBlank(dbaProp.getProperty(propPassword))) {
            return false;
        }
        return true;
    }

    /**
     * 获取DBA数据源
     *
     * @param dbConfig
     * @return
     */
    protected DataSource fetchDbDataSource(Tenant tenant, Properties dbaProp) {
        return null;
    }

    private void doMigrateModule(IMigrateModule module, DataSource dataSource, Map<String, String> placeHolders) {
        String moduleCode = module == null ? "nil module" : module.getCode();
        if (module.isSchemaEnabled()) {
            if (sqlCallback(module, dataSource, module.beforeDdlMigrate(), placeHolders)) {
                logger.info("Module[" + moduleCode + "]success done sqlCallback[beforeDdlMigrate]");
            }
            try {
                this.doMigrate(module.getSchemaTable(), module.getSchemaLocation(), dataSource,
                        module.isRepairEnabled(), placeHolders);
            } catch (Exception t) {
                String message = MsgUtils.getMessage("tenant.dbmigrate.migrateScheamFail", module.getSchemaTable(),
                        module.getSchemaLocation());
                throw new RuntimeException(message, t);
            } finally {
                if (sqlCallback(module, dataSource, module.afterDdlMigrate(), placeHolders)) {
                    logger.info("Module[" + moduleCode + "]success done sqlCallback[afterDdlMigrate]");
                }
            }
        }

        if (module.isDataEnabled()) {
            if (sqlCallback(module, dataSource, module.beforeDmlMigrate(), placeHolders)) {
                logger.info("Module[" + moduleCode + "]success done sqlCallback[beforeDmlMigrate]");
            }
            try {
                this.doMigrate(module.getDataTable(), module.getDataLocation(), dataSource, module.isRepairEnabled(),
                        placeHolders);
            } catch (Exception t) {
                String message = MsgUtils.getMessage("tenant.dbmigrate.migrateDataFail", module.getDataTable(),
                        module.getDataLocation());
                throw new RuntimeException(message, t);
            } finally {
                if (sqlCallback(module, dataSource, module.afterDmlMigrate(), placeHolders)) {
                    logger.info("Module[" + moduleCode + "]success done sqlCallback[afterDmlMigrate]");
                }
            }
        }
    }

    protected Map<String, String> getPlaceHolders(Object... describes) {
        Map<String, String> placeHolders;
        InputStream inputStream = null;
        String configJdbcFile = Config.CLASS_DIR + File.separator + Config.SYSTEM_JDBC_CONFIG;
        try {
            placeHolders = new HashMap<String, String>();
            Properties configJdbc = new Properties();
            inputStream = new FileInputStream(configJdbcFile);
            configJdbc.load(inputStream);
            Enumeration<?> jdbcKeys = configJdbc.keys();
            while (jdbcKeys.hasMoreElements()) {
                String key = jdbcKeys.nextElement().toString();
                if (key.startsWith(JDBC_PREFIX_ORG) || key.startsWith(JDBC_PREFIX_CIS)
                        || key.startsWith(JDBC_PREFIX_COMMON) || key.startsWith(JDBC_PREFIX_TENANT)) {
                    placeHolders.put(key, configJdbc.getProperty(key));
                }
            }
            // 从system.properties读取multi.tenancy.tenant.id默认值@{multi.tenancy.tenant.default}
            placeHolders.put(JDBC_PREFIX_TENANT_ID, Config.getValue(JDBC_PREFIX_TENANT_DEFAULT));
            if (describes != null && describes.length > 0) {
                for (Object desObj : describes) {
                    if (desObj == null) {
                        continue;
                    }
                    Map<String, String> properties = BeanUtils.describe(desObj);
                    for (Map.Entry<String, String> kv : properties.entrySet()) {
                        String key = kv.getKey(), value = kv.getValue();
                        if (key == null || value == null) {
                            continue;
                        }
                        // 如果存在Tenant,Tenand.id覆盖配置文件的@{multi.tenancy.tenant.id}
                        placeHolders.put(JDBC_PREFIX_TENANT + key, value);
                    }
                }
            }
        } catch (Exception t) {
            placeHolders = new HashMap<String, String>();
            logger.info("error gerPlaceHoders", t);
            // ignore exception
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return placeHolders;
    }

    private boolean sqlCallback(IMigrateModule module, DataSource dataSource, String sqlScript,
                                Map<String, String> placeHolders) {
        if (StringUtils.isBlank(sqlScript)) {
            return false;
        }
        String mc = module == null ? "nil module" : module.getCode();
        PlaceholderReplacer placeHolder = new PlaceholderReplacer(placeHolders, "${", "}");
        try {
            this.execute(dataSource, null, placeHolder.replacePlaceholders(sqlScript));
            // logger.info("Module[" + mc + "]success done sqlCallback[" +
            // StringUtils.abbreviate(sqlScript, 128) + "]");
        } catch (Exception t) {
            String message = "Module[" + mc + "]error done sqlCallback[" + StringUtils.abbreviate(sqlScript, 128)
                    + "]error[" + t.getMessage() + "]";
            throw new RuntimeException(message, t);
        }
        return true;
    }

    private void doMigrate(String table, String location, DataSource dataSource, boolean moduleRepair,
                           Map<String, String> placeHolders) {
        Flyway flyway = new Flyway();
        flyway.setPlaceholderPrefix("@{");
        flyway.setPlaceholders(placeHolders);
        flyway.setBaselineOnMigrate(true);
        flyway.setBaselineVersionAsString("1");
        flyway.setDataSource(dataSource);
        flyway.setTable(table);
        flyway.setLocations(new String[]{location});
        if (repair && moduleRepair) {
            flyway.repair();
            logger.info("successful repair :  {}, {}", table, location);
        }
        flyway.migrate();
        logger.info("successful migrate :  {}, {}", table, location);
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }

    public void setRepair(boolean repair) {
        this.repair = repair;
    }

    public void setCommonProps(Properties commonProps) {
        this.commonProps = commonProps;
    }

    public String getDbExistQuery() {
        return "";
    }

    protected String getDefaultDbTemplate(Tenant tenant) {
        String result = null, jdbcType = tenant.getJdbcType(), sp = File.separator;
        String configJdbcFile = Config.CLASS_DIR + sp + "dbmigrate" + sp + jdbcType + sp + DEFAULT_DB_TEMPLATE;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configJdbcFile);
            result = IOUtils.toString(inputStream);
        } catch (Exception t) {
            logger.error("no defaultDbTemplate found.", t);
            // ignore exception
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return result;
    }

    protected String getDropDdTempalte() {
        return "";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#getProperties()
     */
    @Override
    public final Properties getProperties() {
        if (tenantProps == null) {
            tenantProps = MigratorUtils.getJdbcProperties(JDBC_PREFIX_TENANT);
        }
        return tenantProps;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#getDataSource()
     */
    @Override
    public final DataSource getDataSource() {
        Properties conProps = getProperties();
        DataSource ds;
        try {
            ds = MigratorUtils.getDataSource(conProps);
            JdbcUtils.closeConnection(ds.getConnection());
        } catch (Exception t) {
            ds = null;
            logger.info("error getDataSource for conProps : " + conProps, t);
        }
        return ds;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#beforeBuildDb(javax.sql.DataSource)
     */
    @Override
    public final boolean beforeBuildDb(DataSource dataSource, JdbcTemplate jdbcTemplate, String dbName) {
        return isTenantExist(jdbcTemplate, dbName) == false;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#afterBuildDb()
     */
    @Override
    public final String afterBuildDb(String jdbcType) {
        String sp = File.separator;
        String cpFile = Config.CLASS_DIR + sp + "dbmigrate" + sp + jdbcType + sp + DB_BUILD_AFTER;
        return MigratorUtils.getFileString(cpFile);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#beforeDropDb(javax.sql.DataSource, org.springframework.jdbc.core.JdbcTemplate, java.lang.String)
     */
    @Override
    public final boolean beforeDropDb(DataSource dbaDataSource, JdbcTemplate jdbcTemplate, String dbName) {
        return isTenantExist(jdbcTemplate, dbName) == true;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#afterBuildDrop()
     */
    @Override
    public final String afterDropDb(String jdbcType) {
        String sp = File.separator;
        String cpFile = Config.CLASS_DIR + sp + "dbmigrate" + sp + jdbcType + sp + DB_DROP_AFTER;
        return MigratorUtils.getFileString(cpFile);
    }

}
