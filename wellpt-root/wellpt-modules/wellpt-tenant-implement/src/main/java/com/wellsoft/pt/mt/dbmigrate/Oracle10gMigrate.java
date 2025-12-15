/*
 * @(#)2016年3月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dbmigrate;

import com.wellsoft.context.util.i18n.MsgUtils;
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
public class Oracle10gMigrate extends AbstractFlywayMigrator {
    private final static String dbExistQuery = "select COUNT(*) as cnt from dba_users where UPPER(username) = UPPER(?)";

    @Override
    protected DataSource fetchAvaiableDataSource(Tenant tenant) {
        Properties prop = new Properties();
        String server = tenant.getJdbcServer();
        String dbName = tenant.getJdbcDatabaseName();
        String port = StringUtils.isBlank(tenant.getJdbcPort()) ? "1521" : tenant.getJdbcPort();
        String url = "jdbc:oracle:thin:@" + server + ":" + port + ":" + dbName;
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
        } catch (Exception cause) {
            if (tenant.getStatus() == DB_STATUS_ALREADY_EXIST) {
                String message = MsgUtils.getMessage("tenant.dbmigrate.invalidatePassword", tenant.getJdbcUsername(),
                        tenant.getJdbcPassword());
                logger.error(message, cause);
                throw new RuntimeException(message, cause);//return false;
            }
            logger.error(cause.getMessage(), cause);
            throw new RuntimeException(cause.getMessage(), cause);//return false;
        }
        return slaveDS;
    }

    @Override
    protected DataSource fetchDbDataSource(Tenant tenant, Properties dbaProp) {
        Properties prop = new Properties(dbaProp);
        String url, dbaUser, server = tenant.getJdbcServer(), dbName = tenant.getJdbcDatabaseName();
        String port = StringUtils.isBlank(tenant.getJdbcPort()) ? "1521" : tenant.getJdbcPort();
        if (StringUtils.isBlank(url = dbaProp.getProperty(propUrl))) {
            url = "jdbc:oracle:thin:@" + server + ":" + port + ":" + dbName;
        }
        prop.setProperty(propUrl, url);
        dbaUser = dbaProp.getProperty(propUsername);
        if (dbaUser != null && dbaUser.indexOf("sysdba") < 0) {
            prop.setProperty(propUsername, dbaUser + " as sysdba");
        }
        String driver = prop.getProperty(propDeiver);
        DataSource masterDS = null;
        try {
            // login as sysdba;
            masterDS = MigratorUtils.getDataSource(driver, url, prop);
            JdbcUtils.closeConnection(masterDS.getConnection());
        } catch (Exception cause) {
            logger.error(cause.getMessage(), cause);
            throw new RuntimeException(cause.getMessage(), cause);//return false;
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
        return "DROP USER ${multi.tenancy.tenant.jdbcUsername} CASCADE";
    }

}
