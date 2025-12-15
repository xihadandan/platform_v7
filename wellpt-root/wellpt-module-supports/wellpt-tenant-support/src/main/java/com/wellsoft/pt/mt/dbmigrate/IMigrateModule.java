package com.wellsoft.pt.mt.dbmigrate;

public interface IMigrateModule {

    public final static String DDL_MIGRATE_BEFORE = "before_ddl_migrate.tpl";
    public final static String DDL_MIGRATE_AFTER = "after_ddl_migrate.tpl";
    public final static String DML_MIGRATE_BEFORE = "before_dml_migrate.tpl";
    public final static String DML_MIGRATE_AFTER = "after_dml_migrate.tpl";
    public final static String SQL_EXECUTE_BEFORE = "before_execute.tpl";
    public final static String SQL_EXECUTE_AFTER = "before_execute.tpl";

    // public boolean isCommon();

    public boolean isSchemaEnabled();

    public boolean isDataEnabled();

    public boolean isRepairEnabled();

    public String getCode();

    public String getSchemaTable();

    public String getSchemaLocation();

    public String getDataTable();

    public String getDataLocation();

    /**
     * 模块执行ddlMigrate之前的SQL
     *
     * @return
     */
    public String beforeDdlMigrate();

    /**
     * 块执行ddlMigrate之后的SQL,注意：在try{}finally{}中执行,执行失败的清理
     *
     * @return
     */
    public String afterDdlMigrate();

    /**
     * 模块执行dmlMigrate之前的SQL
     *
     * @return
     */
    public String beforeDmlMigrate();

    /**
     * 模块执行dmlMigrate之后的SQL,注意：在try{}finally{}中执行,执行失败的清理
     *
     * @return
     */
    public String afterDmlMigrate();

    /**
     * 模块执行TenantSqlExecutor.execute之前的SQL
     *
     * @return
     */
    public String beforeExecute();

    /**
     * 模块执行TenantSqlExecutor.execute之后的SQL,注意：在try{}finally{}中执行,执行失败的清理
     *
     * @return
     */
    public String afterExecute();

}
