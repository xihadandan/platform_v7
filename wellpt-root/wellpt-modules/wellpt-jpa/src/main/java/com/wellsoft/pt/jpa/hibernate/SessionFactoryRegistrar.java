/*
 * @(#)2015年11月27日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import bitronix.tm.resource.ResourceRegistrar;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.datasource.XADataSource;
import com.wellsoft.pt.jpa.datasource.XADataSourceFactory;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * 2015年11月27日.1	zhulh		2015年11月27日		Create
 * </pre>
 * @date 2015年11月27日
 */
public class SessionFactoryRegistrar {
    private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryRegistrar.class);

    private static final String JDBC_PREFIX = "jdbc/";

    private static final Map<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();

    /**
     * 如何描述该方法
     *
     * @param tenant
     */
    public static void register(Tenant tenant) {
        String tenantId = tenant.getId();
        String unitName = JDBC_PREFIX + tenantId;
        if (ResourceRegistrar.get(unitName) != null) {
            DataSource dataSource = (DataSource) ResourceRegistrar.get(unitName);
            SessionFactoryRegistrar.buildAndRegister(tenantId, dataSource);
            return;
        }
        PoolingDataSource ds = new BitronixLog4jdbcDataSource();
        XADataSource xaDataSource = XADataSourceFactory.build(tenant);
        ds.setUniqueName(unitName);
        ds.setClassName(xaDataSource.getXaDatasourceClass());
        ds.setMaxPoolSize(xaDataSource.getMaxPoolSize());
        ds.setDriverProperties(xaDataSource.getDriverProperties());
        ds.setApplyTransactionTimeout(true);
        ds.setAcquisitionTimeout(BitronixDataSourceFactoryBean.ACQUISITION_TIME_OUT);
        // 本地事务设为true
        ds.setAllowLocalTransactions(true);
        ds.setIsolationLevel(XADataSource.TRANSACTION_READ_COMMITTED);

        ds.setEnableJdbc4ConnectionTest(xaDataSource.isEnableJdbc4ConnectionTest());
        ds.setTestQuery(xaDataSource.getTestQuery());
        ds.init();
        SessionFactoryRegistrar.buildAndRegister(tenantId, ds);
    }

    private static SessionFactory buildAndRegister(String id, DataSource dataSource) {
        try {
            SessionFactory sessionFactory = buildSessionFactory(id, dataSource);
            sessionFactoryMap.put(id, sessionFactory);
            LOG.info("rigister session factory [" + id + "]");
            return sessionFactory;
        } catch (IOException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));

            throw new RuntimeException(e);
        }

    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    private static SessionFactory buildSessionFactory(String id, DataSource dataSource) throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        LocalSessionFactoryBuilder sfb = new LocalSessionFactoryBuilder(dataSource, resourcePatternResolver);

        SessionFactoryBasedMultiTenancyConfiguration schemaBasedMultiTenancyConfiguration = ApplicationContextHolder
                .getBean(SessionFactoryBasedMultiTenancyConfiguration.class);

        Class<?>[] annotatedClasses = schemaBasedMultiTenancyConfiguration.getAnnotatedClasses();

        Resource[] mappingLocations = schemaBasedMultiTenancyConfiguration.getMappingLocations();

        Properties hibernateProperties = schemaBasedMultiTenancyConfiguration.getHibernateProperties();

        sfb.setNamingStrategy(schemaBasedMultiTenancyConfiguration.getNamingStrategy());

        if (annotatedClasses != null) {
            sfb.addAnnotatedClasses(annotatedClasses);
        }

        if (mappingLocations != null) {
            // Register given Hibernate mapping definitions, contained in
            // resource files.
            for (Resource resource : mappingLocations) {
                sfb.addInputStream(resource.getInputStream());
            }
        }

        if (hibernateProperties != null) {
            hibernateProperties.put(Environment.DATASOURCE, JDBC_PREFIX + id);
            sfb.addProperties(hibernateProperties);
        }

        return sfb.buildSessionFactory();
    }

    /**
     * @param sessionFactoryId
     * @return
     */
    public static SessionFactory get(String sessionFactoryId) {
        return sessionFactoryMap.get(sessionFactoryId);
    }

    public static SessionFactory getCurrentTenantSessionFactory() {
        return get(TenantContextHolder.getTenantId());
    }

    /**
     * 注册可用的租户sessionFactory
     */
    public static void registerActiveTenants() {
        TenantFacadeService tenantService = ApplicationContextHolder.getBean(TenantFacadeService.class);
        List<Tenant> tenants = tenantService.getActiveTenants();
        for (Tenant t : tenants) {
            SessionFactoryRegistrar.register(t);
        }
    }

}
