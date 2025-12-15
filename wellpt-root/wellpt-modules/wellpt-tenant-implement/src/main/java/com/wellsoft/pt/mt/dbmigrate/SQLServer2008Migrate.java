/*
 * @(#)2016年3月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dbmigrate;

import com.wellsoft.pt.jpa.support.SQLServer2008TenantDatabaseBuilder;
import com.wellsoft.pt.mt.entity.Tenant;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月1日.1	zhongzh		2016年3月1日		Create
 * </pre>
 * @date 2016年3月1日
 */
@Component
public class SQLServer2008Migrate extends AbstractFlywayMigrator {

    private final static String dbExistQuery = "select COUNT(*) as cnt from master.dbo.sysdatabases where upper(name) = upper(?)";

    @Override
    protected boolean doDatabaseBuild(Tenant tenant, Properties dbaProp, IMigrateCommon commonModule,
                                      ExecuteCallback callback) {
        try {
            SQLServer2008TenantDatabaseBuilder tnBuilder = new SQLServer2008TenantDatabaseBuilder();
            tnBuilder.build(tenant);
            return true;
        } catch (Exception t) {
            logger.error(t.getMessage(), t);
        }
        return false;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.AbstractFlywayMigrator#fetchAvaiableDataSource(com.wellsoft.pt.mt.entity.Tenant)
     */
    @Override
    protected DataSource fetchAvaiableDataSource(Tenant tenant) {
        Properties prop = new Properties();
        String server = tenant.getJdbcServer();
        String dbName = tenant.getJdbcDatabaseName();
        String port = StringUtils.isBlank(tenant.getJdbcPort()) ? "1433" : tenant.getJdbcPort();
        String url = "jdbc:sqlserver://" + server + ":" + port + ";DatabaseName=" + dbName;
        prop.put(propUrl, url);
        prop.put(propUsername, tenant.getJdbcUsername());
        if (tenant.getJdbcPassword() != null) {
            prop.put(propPassword, tenant.getJdbcPassword());
        }
        String driver = prop.getProperty(propDeiver);
        DataSource slaveDS = null;
        try {
            slaveDS = MigratorUtils.getDataSource(driver, url, prop);
            JdbcUtils.closeConnection(slaveDS.getConnection());
        } catch (Exception t) {
            slaveDS = null;
            // throw new RuntimeException("fail getConnect for :" + prop, ex);
            logger.error("fail getConnect for :" + prop, t);
        }
        return slaveDS;
    }

    @Override
    protected DataSource fetchDbDataSource(Tenant tenant, Properties dbaProp) {
        Properties prop = new Properties(dbaProp);
        String url, server = tenant.getJdbcServer(), dbName = tenant.getJdbcDatabaseName();
        String port = StringUtils.isBlank(tenant.getJdbcPort()) ? "1433" : tenant.getJdbcPort();
        if (StringUtils.isBlank(url = dbaProp.getProperty(propUrl))) {
            url = "jdbc:sqlserver://" + server + ":" + port + ";DatabaseName=" + dbName;
        }
        prop.setProperty(propUrl, url);
        String driver = prop.getProperty(propDeiver);
        DataSource masterDS = null;
        try {
            // login as sysdba;
            masterDS = MigratorUtils.getDataSource(driver, url, prop, new String[]{"USE [master]", "GO"});
            JdbcUtils.closeConnection(masterDS.getConnection());
        } catch (Exception ex) {
            masterDS = null;
            // throw new RuntimeException("fail getConnect for :" + prop, ex);
            logger.error("fail getConnect for :" + prop, ex);
        }
        return masterDS;
    }

    @Override
    public String getDbExistQuery() {
        return dbExistQuery;
    }

    @Override
    protected String getDefaultDbTemplate(Tenant tenant) {
        return super.getDefaultDbTemplate(tenant);
    }

    @Override
    protected String getDropDdTempalte() {
        return "USE MASTER\nGO\nDROP DATABASE ${multi.tenancy.tenant.jdbcUsername}";
    }

}
