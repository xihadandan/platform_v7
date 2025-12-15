/*
 * @(#)2013-1-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import bitronix.tm.resource.ResourceRegistrar;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.wellsoft.context.config.Config;
import com.wellsoft.pt.jpa.datasource.XADataSource;
import com.wellsoft.pt.jpa.datasource.XADataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;
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
 * 2013-1-6.1	zhulh		2013-1-6		Create
 * </pre>
 * @date 2013-1-6
 */
public class BitronixDataSourceFactoryBean implements FactoryBean<DataSource> {
    public static final int ACQUISITION_TIME_OUT = 600;
    public static final String KEY_LOG4J_LOGGER_JDBC = "log4j.logger.jdbc";
    public static final String LOG4J_LOGGER_JDBC_OFF = "off";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String uniqueName;

    private String className;

    private int maxPoolSize;

    private boolean allowLocalTransactions = false;

    private Properties driverProperties;

    private String databaseType = Config.getValue("database.type");

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public boolean isAllowLocalTransactions() {
        return allowLocalTransactions;
    }

    public void setAllowLocalTransactions(boolean allowLocalTransactions) {
        this.allowLocalTransactions = allowLocalTransactions;
    }

    public Properties getDriverProperties() {
        return driverProperties;
    }

    public void setDriverProperties(Properties driverProperties) {
        this.driverProperties = driverProperties;
    }

    /**
     * @return the databaseType
     */
    public String getDatabaseType() {
        return databaseType;
    }

    /**
     * @param databaseType 要设置的databaseType
     */
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public DataSource getObject() throws Exception {
        if (ResourceRegistrar.get(uniqueName) != null) {
            return (DataSource) ResourceRegistrar.get(uniqueName);
        }
        PoolingDataSource ds = buildDataSource(uniqueName, allowLocalTransactions);
        return ds;
    }


    private PoolingDataSource buildDataSource(String uniqueName, boolean allowLocalTransactions) {
        XADataSource xaDataSource = XADataSourceFactory.getXADataSource(databaseType);
        xaDataSource.extractDriverProperties(getDriverProperties());

        PoolingDataSource ds = null;
        if (LOG4J_LOGGER_JDBC_OFF.equals(Config.getValue(KEY_LOG4J_LOGGER_JDBC))) {
            ds = new PoolingDataSource();
        } else {
            ds = new BitronixLog4jdbcDataSource();
        }
        ds.setUniqueName(uniqueName);
        ds.setClassName(xaDataSource.getXaDatasourceClass());
        ds.setMaxPoolSize(xaDataSource.getMaxPoolSize());
        ds.setDriverProperties(xaDataSource.getDriverProperties());
        ds.setApplyTransactionTimeout(true);
        ds.setAcquisitionTimeout(ACQUISITION_TIME_OUT);
        // 本地事务设为true
        ds.setAllowLocalTransactions(allowLocalTransactions);
        ds.setIsolationLevel(XADataSource.TRANSACTION_READ_COMMITTED);
        ds.setEnableJdbc4ConnectionTest(xaDataSource.isEnableJdbc4ConnectionTest());
        ds.setTestQuery(xaDataSource.getTestQuery());
        ds.init();
        return ds;
    }


    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    @Override
    public boolean isSingleton() {
        return false;
    }


}
