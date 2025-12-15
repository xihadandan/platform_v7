package com.wellsoft.pt.mt.dbmigrate;

import com.wellsoft.context.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

public abstract class AbstractMigrateModule implements IMigrateModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected String moduleCode;
    protected String moduleCodeUpper;
    protected String type;
    protected boolean schemaEnabled;
    protected boolean dataEnabled;
    protected boolean repairEnabled;

    /**
     * 如何描述该构造方法
     *
     * @param moduleCode
     */
    public AbstractMigrateModule(String moduleCode) {
        Assert.notNull(moduleCode, "MigrateModuleAbstract with field[moduleCode] is not null");
        this.moduleCode = moduleCode;
        moduleCodeUpper = moduleCode.toUpperCase();
    }

    public boolean isSchemaEnabled() {
        return schemaEnabled;
    }

    public void setSchemaEnabled(boolean schemaEnabled) {
        this.schemaEnabled = schemaEnabled;
    }

    public boolean isDataEnabled() {
        return dataEnabled;
    }

    public void setDataEnabled(boolean dataEnabled) {
        this.dataEnabled = dataEnabled;
    }

    public boolean isRepairEnabled() {
        return repairEnabled;
    }

    public void setRepairEnabled(boolean repairEnabled) {
        this.repairEnabled = repairEnabled;
    }

    public String getCode() {
        return moduleCode;
    }

    public String getSchemaTable() {
        return "SV_SCHEMA_" + moduleCodeUpper;
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + "." + moduleCode + "." + "ddl";
    }

    public String getDataTable() {
        return "SV_DATA_" + moduleCodeUpper;
    }

    public String getDataLocation() {
        return "dbmigrate." + type + "." + moduleCode + "." + "dml";
    }

    @Value("${database.type}")
    public void setType(String type) {
        this.type = type;
    }

    protected String getModuleSql(String fileName) {
        String sp = File.separator;
        String cpFile = Config.CLASS_DIR + sp + "dbmigrate" + sp + type + sp + moduleCode + sp + fileName;
        return MigratorUtils.getFileString(cpFile);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateModule#beforeDdlMigrate()
     */
    @Override
    public String beforeDdlMigrate() {
        return this.getModuleSql(DDL_MIGRATE_BEFORE);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateModule#afterDdlMigrate()
     */
    @Override
    public String afterDdlMigrate() {
        return this.getModuleSql(DDL_MIGRATE_AFTER);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateModule#beforeDmlMigrate()
     */
    @Override
    public String beforeDmlMigrate() {
        return this.getModuleSql(DML_MIGRATE_BEFORE);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateModule#afterDmlMigrate()
     */
    @Override
    public String afterDmlMigrate() {
        return this.getModuleSql(DML_MIGRATE_AFTER);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateModule#beforeExecute()
     */
    @Override
    public String beforeExecute() {
        return this.getModuleSql(SQL_EXECUTE_BEFORE);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateModule#afterExecute()
     */
    @Override
    public String afterExecute() {
        return this.getModuleSql(SQL_EXECUTE_AFTER);
    }

    protected Properties getProperties(String prifix) {
        return MigratorUtils.getJdbcProperties(prifix);
    }

    /**
     * 根据配置文件获取公共库数据源
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#getDataSource()
     */
    protected DataSource getDataSource(Properties conProps) {
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
}
