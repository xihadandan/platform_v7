package com.wellsoft.pt.mt.dbmigrate;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Properties;

public interface IMigrateCommon {

    public static final String DB_BUILD_BEFORE = "before_build_db.tpl";
    public static final String DB_BUILD_AFTER = "after_build_db.tpl";
    public static final String DB_MIGRATE_BEFORE = "before_build_db.tpl";
    public static final String DB_MIGTATE_AFTER = "after_build_db.tpl";
    public static final String DB_DROP_BEFORE = "before_drop_db.tpl";
    public static final String DB_DROP_AFTER = "after_drop_db.tpl";

    public static final String JDBC_URL = "url";
    public static final String JDBC_DRIVER = "driver";
    public static final String JDBC_USERNAME = "username";
    public static final String JDBC_PASSWORD = "password";
    public static final String JDBC_SERVER_NAME = "server_name";
    public static final String JDBC_DATABASE_NAME = "database_name";

    public Properties getProperties();

    // 在system-jdbc.properties中获取
    public DataSource getDataSource();

    /**
     * 构建公共库Schema之前的判断,如果返回false将不再构建
     *
     * @param dbaDataSource
     * @param jdbcTemplate
     * @param dbName
     * @return false->不构建数据库
     */
    public boolean beforeBuildDb(DataSource dbaDataSource, JdbcTemplate jdbcTemplate, String dbName);

    /**
     * 构建公共库Schema之后执行的SQL,DBA权限(数据源)执行(注：需要公共模块的数据源,可以实现模块回调接口(脚本)),在try{}finally{}中执行
     *
     * @return 构建公共库Schema之后执行的SQL
     */
    public String afterBuildDb(String jdbcType);

    /**
     * 删除公共库Schema之前的判断,如果返回false将不再删除
     *
     * @param dbaDataSource
     * @param jdbcTemplate
     * @param dbName
     * @return false->不删除数据库
     */
    public boolean beforeDropDb(DataSource dbaDataSource, JdbcTemplate jdbcTemplate, String dbName);

    /**
     * 删除公共库Schema之后执行的SQL,DBA权限(数据源)执行(注：需要公共模块的数据源,可以实现模块回调接口(脚本)),在try{}finally{}中执行
     *
     * @return 删除公共库Schema之后执行的SQL
     */
    public String afterDropDb(String jdbcType);
}
