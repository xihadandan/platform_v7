package com.wellsoft.pt;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.jndi.BitronixInitialContextFactory;
import com.wellsoft.pt.jpa.event.AsyncWellptEventMulticaster;
import com.wellsoft.pt.jpa.hibernate.BitronixDataSourceFactoryBean;
import com.wellsoft.pt.jpa.hibernate.BitronixMultiTenantConnectionProviderImpl;
import com.wellsoft.pt.jpa.hibernate.DefaultCurrentTenantIdentifierResolver;
import com.wellsoft.pt.jpa.hibernate4.CustomeImprovedNamingStrategy;
import com.wellsoft.pt.jpa.log4tx.support.JtaTransactionManagerFactory;
import com.wellsoft.pt.jpa.log4tx.support.TransactionInterceptorFactory;
import com.wellsoft.pt.jpa.support.CommonEntityAnnotatedClassesFactoryBean;
import com.wellsoft.pt.jpa.support.HibernateMappingLocationsFactoryBean;
import com.wellsoft.pt.jpa.support.SupportMultiDatabaseLocalSessionFactoryBean;
import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
import org.hibernate.engine.transaction.jta.platform.internal.BitronixJtaPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate4.SpringJtaSessionContext;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Description: 数据源配置
 *
 * @author chenq
 * @date 2019/11/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/11    chenq		2019/11/11		Create
 * </pre>
 */
//改成用本地事务 localDataSourceConfiguration
//@Configuration
// @EnableTransactionManagement(proxyTargetClass = true)
@PropertySource(value = {"classpath:/system-jdbc.properties", "classpath:/system.properties"})
public class DataSourceConfiguration {

    @Value("#{'jdbc/'+'${multi.tenancy.common.datasource}'}")
    private String commonDatasourceId;
    @Value("${database.type}")
    private String databaseType;
    @Value("${multi.tenancy.common.server_name}")
    private String commonDsServerName;
    @Value("${multi.tenancy.common.database_name}")
    private String commonDatabasename;
    @Value("${multi.tenancy.common.url}")
    private String commonDsUrl;
    @Value("${multi.tenancy.common.username}")
    private String commonDsUsername;
    @Value("${multi.tenancy.common.password}")
    private String comonDsPassword;

    @Value("#{'jdbc/'+'${multi.tenancy.tenant.datasource}'}")
    private String tenantDatasourceId;
    @Value("${multi.tenancy.tenant.server_name}")
    private String tenantDsServerName;
    @Value("${multi.tenancy.tenant.database_name}")
    private String tenantDatabasename;
    @Value("${multi.tenancy.tenant.url}")
    private String tenantDsUrl;
    @Value("${multi.tenancy.tenant.username}")
    private String tenantDsUsername;
    @Value("${multi.tenancy.tenant.password}")
    private String tenantDsPassword;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;
    @Value("#{new Boolean('${hibernate.show_sql}')}")
    private boolean showSql;
    @Value("#{new Boolean('${hibernate.format_sql}')}")
    private boolean formatSql;
    @Value("#{new Boolean('${hibernate.cache.use_query_cache}')}")
    private boolean hibernateCacheUseQueryCache;
    @Value("#{new Boolean('${hibernate.cache.use_second_level_cache}')}")
    private boolean hibernateCacheUseSecondLevelCache;
    @Value("${hibernate.cache.region.factory_class}")
    private String hibernateCacheRegionFactoryClass;
    @Value("${hibernate.jdbc.fetch_size}")
    private int jdbcFetchSize;
    @Value("${hibernate.jdbc.batch_size}")
    private int jdbcBatchSize;
    @Value("${hibernate.connection.release_mode:after_statement}")
    private String hibernateConnectReleaseMode;
    @Value("${multi.tenancy.common.max.pool.size:100}")
    private int commonDsMaxPoolSize;
    @Value("${multi.tenancy.tenant.max.pool.size:100}")
    private int tenantDsMaxPoolSize;

    /**
     * 公共库数据库资源
     *
     * @return
     */
    @Bean
    public BitronixDataSourceFactoryBean commonDataSource() {
        BitronixDataSourceFactoryBean factoryBean = new BitronixDataSourceFactoryBean();
        factoryBean.setUniqueName(commonDatasourceId);
        factoryBean.setMaxPoolSize(commonDsMaxPoolSize);
        factoryBean.setDatabaseType(databaseType);
        Properties properties = new Properties();
        properties.put("ServerName", commonDsServerName);
        properties.put("DatabaseName", commonDatabasename);
        properties.put("SelectMethod", "cursor");
        properties.put("URL", commonDsUrl);
        properties.put("User", commonDsUsername);
        properties.put("Password", comonDsPassword);
        factoryBean.setDriverProperties(properties);
        return factoryBean;
    }

    /**
     * 用户库数据库资源
     *
     * @return
     */
    @Bean
    public BitronixDataSourceFactoryBean userDataSource() {
        BitronixDataSourceFactoryBean factoryBean = new BitronixDataSourceFactoryBean();
        factoryBean.setUniqueName(tenantDatasourceId);
        factoryBean.setMaxPoolSize(tenantDsMaxPoolSize);
        factoryBean.setDatabaseType(databaseType);
        Properties properties = new Properties();
        properties.put("ServerName", tenantDsServerName);
        properties.put("DatabaseName", tenantDatabasename);
        properties.put("SelectMethod", "cursor");
        properties.put("URL", tenantDsUrl);
        properties.put("User", tenantDsUsername);
        properties.put("Password", tenantDsPassword);
        factoryBean.setDriverProperties(properties);
        return factoryBean;
    }

    private SupportMultiDatabaseLocalSessionFactoryBean buildSessionFactory(javax.sql.DataSource dataSource, String dsId)
            throws Exception {
        SupportMultiDatabaseLocalSessionFactoryBean sessionFactoryBean = new SupportMultiDatabaseLocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setNamingStrategy(new CustomeImprovedNamingStrategy());
        CommonEntityAnnotatedClassesFactoryBean classesFactoryBean = new CommonEntityAnnotatedClassesFactoryBean();
        classesFactoryBean.setPackagesToScan("com.wellsoft.***.**");
        try {
            sessionFactoryBean.setAnnotatedClasses(classesFactoryBean.getObject().toArray(new Class[]{}));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HibernateMappingLocationsFactoryBean locationsFactoryBean = new HibernateMappingLocationsFactoryBean();
        locationsFactoryBean.setIgnoreUrlResource(true);
        try {
            sessionFactoryBean.setMappingLocations(locationsFactoryBean.getObject().toArray(new Resource[]{}));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Properties properties = new Properties();
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.format_sql", formatSql);
        properties.put("hibernate.cache.use_query_cache", hibernateCacheUseQueryCache);
        properties.put("hibernate.cache.use_second_level_cache", hibernateCacheUseSecondLevelCache);
        properties.put("hibernate.cache.region.factory_class", hibernateCacheRegionFactoryClass);
        properties.put("hibernate.jdbc.fetch_size", jdbcFetchSize);
        properties.put("hibernate.jdbc.batch_size", jdbcBatchSize);
//		properties.put("hibernate.connection.release_mode", hibernateConnectReleaseMode);
        properties.put("hibernate.connection.datasource", dsId);
        properties.put("hibernate.jndi.class", BitronixInitialContextFactory.class.getCanonicalName());
        properties.put("hibernate.current_session_context_class", SpringJtaSessionContext.class.getCanonicalName());
        properties.put("hibernate.transaction.factory_class", CMTTransactionFactory.class.getCanonicalName());
        properties.put("hibernate.transaction.jta.platform", BitronixJtaPlatform.class.getCanonicalName());
        // bug#58194: hibernate.enable_lazy_load_no_trans设置为true或taskService.startSubFlow即时加载流程定义
        properties.put("hibernate.enable_lazy_load_no_trans", true);
        sessionFactoryBean.setHibernateProperties(properties);
        return sessionFactoryBean;
    }

    @Bean
    public SupportMultiDatabaseLocalSessionFactoryBean commonSessionFactory(
            @Qualifier("commonDataSource") javax.sql.DataSource dataSource) throws Exception {
        SupportMultiDatabaseLocalSessionFactoryBean sessionFactoryBean = buildSessionFactory(dataSource,
                commonDatasourceId);
        sessionFactoryBean.getHibernateProperties().put("hibernate.multiTenancy", "DATABASE");
        sessionFactoryBean.getHibernateProperties().put("hibernate.multi_tenant_connection_provider",
                BitronixMultiTenantConnectionProviderImpl.class.getCanonicalName());
        sessionFactoryBean.getHibernateProperties().put("hibernate.multiTenancy", "DATABASE");
        sessionFactoryBean.getHibernateProperties().put("hibernate.tenant_identifier_resolver",
                DefaultCurrentTenantIdentifierResolver.class.getCanonicalName());
        return sessionFactoryBean;
    }

    @Bean
    public SupportMultiDatabaseLocalSessionFactoryBean sessionFactory(
            @Qualifier("userDataSource") javax.sql.DataSource dataSource) throws Exception {
        return buildSessionFactory(dataSource, tenantDatasourceId);
    }

    @Bean(name = "applicationEventMulticaster")
    public AsyncWellptEventMulticaster asyncWellptEventMulticaster(
            @Autowired(required = false) @Qualifier("asyncExecutor") Executor asyncExecutor) {
        AsyncWellptEventMulticaster asyncWellptEventMulticaster = new AsyncWellptEventMulticaster();
        if (null != asyncExecutor) {
            asyncWellptEventMulticaster.setAsyncTaskExecutor(asyncExecutor);
        }
        return asyncWellptEventMulticaster;
    }

    @Bean
    public bitronix.tm.Configuration btmConfig() {
        bitronix.tm.Configuration configuration = TransactionManagerServices.getConfiguration();
        configuration.setServerId("spring-btm");
        return configuration;
    }

    @Bean(destroyMethod = "shutdown")
    @DependsOn("btmConfig")
    public BitronixTransactionManager bitronixTransactionManager() {
        BitronixTransactionManager manager = TransactionManagerServices.getTransactionManager();
        return manager;
    }

    @Bean
    public AnnotationTransactionAttributeSource annotationTransactionAttributeSource() {
        return new AnnotationTransactionAttributeSource();
    }

    @Bean
    public TransactionInterceptor transactionInterceptor(JtaTransactionManager transactionManager,
                                                         AnnotationTransactionAttributeSource annotationTransactionAttributeSource) {
        TransactionInterceptor interceptor = TransactionInterceptorFactory.createTransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setTransactionAttributeSource(annotationTransactionAttributeSource);
        return interceptor;
    }

    @Bean
    public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor(
            AnnotationTransactionAttributeSource annotationTransactionAttributeSource,
            TransactionInterceptor transactionInterceptor) {
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(annotationTransactionAttributeSource);
        advisor.setAdvice(transactionInterceptor);
        return advisor;
    }

    @Bean
    public JtaTransactionManager transactionManager(BitronixTransactionManager bitronixTransactionManager) {
        JtaTransactionManager jtaTransactionManager = JtaTransactionManagerFactory.createJtaTransactionManager();
        jtaTransactionManager.setUserTransaction(bitronixTransactionManager);
        jtaTransactionManager.setTransactionManager(bitronixTransactionManager);
        return jtaTransactionManager;
    }

}
