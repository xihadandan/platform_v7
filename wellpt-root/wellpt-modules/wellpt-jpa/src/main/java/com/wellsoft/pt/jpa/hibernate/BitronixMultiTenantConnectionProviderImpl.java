/*
 * @(#)2012-10-22 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import bitronix.tm.resource.ResourceRegistrar;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.datasource.XADataSource;
import com.wellsoft.pt.jpa.datasource.XADataSourceFactory;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: BitDataSourceBasedMultiTenantConnectionProviderImpl.java
 *
 * @author Administrator
 * @date 2013-1-6
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2013-1-6.1	Administrator		2013-1-6		Create
 * </pre>
 */
public class BitronixMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl
        implements ServiceRegistryAwareService, Stoppable, MultiTenantDataSourceProvider {

    public static final String TENANT_IDENTIFIER_TO_USE_FOR_ANY_KEY = "hibernate.multi_tenant.datasource.identifier_for_any";
    private static final long serialVersionUID = -1079160079680285667L;
    private final Map<String, DataSource> DATA_SOURCE_MAP = new ConcurrentHashMap<String, DataSource>();
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private String tenantIdentifierForAny;
    private String baseJdbcNamespace;

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.service.jdbc.connections.spi.
     * AbstractDataSourceBasedMultiTenantConnectionProviderImpl
     * #selectAnyDataSource()
     */
    @Override
    protected DataSource selectAnyDataSource() {
        return selectDataSource(tenantIdentifierForAny);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.service.jdbc.connections.spi.
     * AbstractDataSourceBasedMultiTenantConnectionProviderImpl
     * #selectDataSource(java.lang.String)
     */
    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        String tenant = tenantIdentifier;
        if (Config.COMMON_TENANT.equals(tenant) || Config.DEFAULT_TENANT.equals(tenant)) {
            tenant = tenantIdentifierForAny;
        }
        DataSource dataSource = DATA_SOURCE_MAP.get(tenant);
        if (dataSource == null) {
            synchronized (DATA_SOURCE_MAP) {
                logger.info("开始创建租户[" + tenant + "]的数据源");
                dataSource = createDataSource(tenant);
                logger.info("结束创建租户[" + tenant + "]的数据源");
                DATA_SOURCE_MAP.put(tenant, dataSource);
            }
        }

        PoolingDataSource pds = (PoolingDataSource) dataSource;
        if (pds.isDisabled() || pds.isFailed()) {
            try {
                pds.reset();
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return dataSource;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl#getConnection(java.lang.String)
     */
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        try {
            return super.getConnection(tenantIdentifier);
        } catch (Exception e) {
            // removeDataSource(tenantIdentifier);
            // createDataSource(tenantIdentifier);
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return super.getConnection(tenantIdentifier);
    }

    /**
     * Description how to use this method
     *
     * @param tenantIdentifier
     * @return
     */
    private DataSource createDataSource(String tenantIdentifier) {
        if (ResourceRegistrar.get(baseJdbcNamespace + '/' + tenantIdentifier) != null) {
            return (DataSource) ResourceRegistrar.get(baseJdbcNamespace + '/' + tenantIdentifier);
        }
        TenantFacadeService tenantService = ApplicationContextHolder.getBean(TenantFacadeService.class);
        Tenant tenant = null;
        if (tenantIdentifier.startsWith("T")) {
            tenant = tenantService.getById(tenantIdentifier);
        } else {
            tenant = tenantService.getByAccount(tenantIdentifier);
        }
        PoolingDataSource ds = null;
        if (tenant != null) {
            XADataSource xaDataSource = XADataSourceFactory.build(tenant);
            String jndiName = baseJdbcNamespace + '/' + xaDataSource.getJndiName();
            if (BitronixDataSourceFactoryBean.LOG4J_LOGGER_JDBC_OFF.equals(Config
                    .getValue(BitronixDataSourceFactoryBean.KEY_LOG4J_LOGGER_JDBC))) {
                ds = new PoolingDataSource();
            } else {
                ds = new BitronixLog4jdbcDataSource();
            }
            ds.setUniqueName(jndiName);
            ds.setClassName(xaDataSource.getXaDatasourceClass());
            ds.setMaxPoolSize(xaDataSource.getMaxPoolSize());
            ds.setDriverProperties(xaDataSource.getDriverProperties());
            ds.setApplyTransactionTimeout(true);
            ds.setAcquisitionTimeout(BitronixDataSourceFactoryBean.ACQUISITION_TIME_OUT);
            // 本地事务设为false
            ds.setAllowLocalTransactions(false);
            ds.setIsolationLevel(XADataSource.TRANSACTION_READ_COMMITTED);
            ds.setEnableJdbc4ConnectionTest(xaDataSource.isEnableJdbc4ConnectionTest());
            ds.setTestQuery(xaDataSource.getTestQuery());
            ds.setMaxIdleTime(xaDataSource.getMaxIdleTime());
            ds.init();
        }
        return ds;
    }

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        final Object dataSourceConfigValue = serviceRegistry.getService(ConfigurationService.class).getSettings()
                .get(AvailableSettings.DATASOURCE);
        if (dataSourceConfigValue == null || !String.class.isInstance(dataSourceConfigValue)) {
            throw new HibernateException("Improper set up of DataSourceBasedMultiTenantConnectionProviderImpl");
        }
        final String jndiName = (String) dataSourceConfigValue;

        final int loc = jndiName.lastIndexOf("/");
        this.baseJdbcNamespace = jndiName.substring(0, loc);
        this.tenantIdentifierForAny = jndiName.substring(loc + 1);

        final Object namedObject = createDataSource(tenantIdentifierForAny);
        if (namedObject == null) {
            throw new HibernateException("JNDI name [" + jndiName + "] could not be resolved");
        }

        if (DataSource.class.isInstance(namedObject)) {
            DATA_SOURCE_MAP.put(tenantIdentifierForAny, (DataSource) namedObject);
        } else {
            throw new HibernateException("Unknown object type [" + namedObject.getClass().getName()
                    + "] found in JNDI location [" + jndiName + "]");
        }
    }

    @Override
    public void stop() {
        DATA_SOURCE_MAP.clear();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.hibernate.MultiTenantDataSourceProvider#removeDataSource(java.lang.String)
     */
    @Override
    public DataSource removeDataSource(String tenantIdentifier) {
        if (ResourceRegistrar.get(baseJdbcNamespace + '/' + tenantIdentifier) != null) {
            ResourceRegistrar.unregister(ResourceRegistrar.get(baseJdbcNamespace + '/' + tenantIdentifier));
        }
        return DATA_SOURCE_MAP.remove(tenantIdentifier);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.hibernate.MultiTenantDataSourceProvider#getDataSource(java.lang.String)
     */
    @Override
    public DataSource getDataSource(String tenantIdentifier) {
        return this.selectDataSource(tenantIdentifier);
    }

}
