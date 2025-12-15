package com.wellsoft.pt;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.MergeStatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.wellsoft.context.config.Config;
import com.wellsoft.pt.jpa.event.AsyncWellptEventMulticaster;
import com.wellsoft.pt.jpa.hibernate4.CustomeImprovedNamingStrategy;
import com.wellsoft.pt.jpa.hibernate4.LocalSpringSessionContext;
import com.wellsoft.pt.jpa.log4tx.support.TransactionInterceptorFactory;
import com.wellsoft.pt.jpa.support.CommonEntityAnnotatedClassesFactoryBean;
import com.wellsoft.pt.jpa.support.HibernateMappingLocationsFactoryBean;
import com.wellsoft.pt.jpa.support.SupportMultiDatabaseLocalSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Description: 切换成本地事务，数据源连接池用Druid
 *
 * @author liuxj
 * @version 6.2
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * V6.2	liuxj		2021/12/6		Create
 * </pre>
 * @da
 */
@Configuration
// @EnableTransactionManagement(proxyTargetClass = true)
@PropertySource(value = {"classpath:/system-jdbc.properties", "classpath:/system.properties"})
public class LocalDataSourceConfiguration {

    public static final String KEY_LOG4J_LOGGER_JDBC = "log4j.logger.jdbc";

    public static final String LOG4J_LOGGER_JDBC_ON = "on";

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

    //===========druid数据源配置====================

    /**
     * 配置公共初始化大小、最小、最大
     */
    @Value("${multi.tenancy.common.pool.initSize:50}")
    private int commonInitSize;

    @Value("${multi.tenancy.common.pool.minIdle:50}")
    private int commonMinIdel;

    /**
     * 配置租户初始化大小、最小、最大
     */
    @Value("${multi.tenancy.tenant.pool.initSize:50}")
    private int tenantInitSize;

    @Value("${multi.tenancy.tenant.pool.minIdle:50}")
    private int tenantMinIdel;


    /**
     * 配置获取连接等待超时的时间
     */
    @Value("${jdbc.maxWait:60000}")
    private long maxWait;
    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    @Value("${jdbc.timeBetweenEvictionRunsMillis:60000}")
    private long timeBetweenEvictionRunsMillis;
    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    @Value("${jdbc.minEvictableIdleTimeMillis:300000}")
    private long minEvictableIdleTimeMillis;

    @Value("${jdbc.validationQuery: select 1 from dual }")
    private String validationQuery;

    @Value("${jdbc.testOnBorrow:false}")
    private boolean testOnBorrow;

    @Value("${jdbc.testOnReturn:false}")
    private boolean testOnReturn;

    @Value("${jdbc.testWhileIdle:true}")
    private boolean testWhileIdle;

    @Value("${jdbc.poolPreparedStatements:true}")
    private boolean poolPreparedStatements;

    @Value("${jdbc.maxPoolPreparedStatementPerConnectionSize:50}")
    private int maxPoolPreparedStatementPerConnectionSize;

    /**
     * 公共数据源
     *
     * @return
     */
    @Bean
    public DruidDataSource commonDataSource(@Qualifier("userDataSource") DruidDataSource dataSource) {
//        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setUrl(commonDsUrl);
//        dataSource.setUsername(commonDsUsername);
//        dataSource.setPassword(comonDsPassword);
//        dataSource.setInitialSize(commonInitSize);
//        dataSource.setMinIdle(commonMinIdel);
//        dataSource.setMaxActive(commonDsMaxPoolSize);
//        dataSource.setMaxWait(maxWait);
//        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
//        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
////		dataSource.setValidationQuery(validationQuery);
//        dataSource.setTestOnBorrow(testOnBorrow);
//        dataSource.setTestOnReturn(testOnReturn);
//        dataSource.setTestWhileIdle(testWhileIdle);
//        dataSource.setPoolPreparedStatements(poolPreparedStatements);
//        dataSource.setMaxPoolPreparedStatementPerConnectionSize(
//                maxPoolPreparedStatementPerConnectionSize);
//
//        // 开发环境开启SQL输出
//        log4JdbcIfRequired(dataSource);
//        try {
//            dataSource.getConnection().close();
//        } catch (Exception e) {
//            throw new RuntimeException("无法获取数据库连接，请检查数据库properties配置是否正确");
//        }
        return dataSource;
    }

    /**
     * 用户库数据库资源
     *
     * @return
     */
    @Bean
    public DruidDataSource userDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(tenantDsUrl);
        dataSource.setUsername(tenantDsUsername);
        dataSource.setPassword(tenantDsPassword);
        dataSource.setInitialSize(tenantInitSize);
        dataSource.setMinIdle(tenantMinIdel);
        dataSource.setMaxActive(tenantDsMaxPoolSize);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
//		dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(60);
        dataSource.setLogAbandoned(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(
                maxPoolPreparedStatementPerConnectionSize);
        MergeStatFilter statFilter = new MergeStatFilter();
        dataSource.getProxyFilters().add(statFilter);
        // 开发环境开启SQL输出
        log4JdbcIfRequired(dataSource);

        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("userDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Qualifier("userDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }


    /**
     * @param dataSource
     */
    private void log4JdbcIfRequired(DruidDataSource dataSource) {
        // 开发环境开启SQL输出
        if (Config.ENV_DEV.equalsIgnoreCase(Config.getAppEnv())
                && LOG4J_LOGGER_JDBC_ON.equalsIgnoreCase(Config.getValue(KEY_LOG4J_LOGGER_JDBC))) {
            Slf4jLogFilter logFilter = new Slf4jLogFilter();
            logFilter.setConnectionLogEnabled(false);
            logFilter.setStatementLogEnabled(false);
            logFilter.setResultSetLogEnabled(false);
            logFilter.setStatementLogEnabled(true);
            logFilter.setStatementExecutableSqlLogEnable(true);
            logFilter.setStatementLogErrorEnabled(true);
            dataSource.getProxyFilters().add(logFilter);
        }
    }

    /**
     * 构建会话工厂
     *
     * @param dataSource
     * @return
     * @throws Exception
     */
    private SupportMultiDatabaseLocalSessionFactoryBean buildSessionFactory(javax.sql.DataSource dataSource)
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
        properties.put("hibernate.current_session_context_class", LocalSpringSessionContext.class.getCanonicalName());
        // bug#58194: hibernate.enable_lazy_load_no_trans设置为true或taskService.startSubFlow即时加载流程定义
        properties.put("hibernate.enable_lazy_load_no_trans", true);
        sessionFactoryBean.setHibernateProperties(properties);
        return sessionFactoryBean;
    }

    /**
     * 公共会话工厂
     *
     * @return
     */
    @Bean
    public SupportMultiDatabaseLocalSessionFactoryBean commonSessionFactory(
            @Qualifier("commonDataSource") javax.sql.DataSource dataSource) throws Exception {
        SupportMultiDatabaseLocalSessionFactoryBean sessionFactoryBean = buildSessionFactory(dataSource);
        return sessionFactoryBean;
    }

    /**
     * 用户会话工厂
     *
     * @return
     */
    @Bean
    public SupportMultiDatabaseLocalSessionFactoryBean sessionFactory(
            @Qualifier("dynamicDataSource") javax.sql.DataSource dataSource) throws Exception {
        return buildSessionFactory(dataSource);
    }

    /**
     * 公共事务管理器
     *
     * @return
     */
    @Bean
    public HibernateTransactionManager commonTransactionManager(@Qualifier("commonSessionFactory") SupportMultiDatabaseLocalSessionFactoryBean commonSessionFactory) {
        HibernateTransactionManager commonTransactionManager = new HibernateTransactionManager();
        commonTransactionManager.setSessionFactory(commonSessionFactory.getObject());
        return commonTransactionManager;
    }

    /**
     * 用户事务管理器
     *
     * @return
     */
    @Bean
    public HibernateTransactionManager transactionManager(@Qualifier("sessionFactory") SupportMultiDatabaseLocalSessionFactoryBean sessionFactory) {
        HibernateTransactionManager commonTransactionManager = new HibernateTransactionManager();
        commonTransactionManager.setSessionFactory(sessionFactory.getObject());
        return commonTransactionManager;
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
    public AnnotationTransactionAttributeSource annotationTransactionAttributeSource() {
        return new AnnotationTransactionAttributeSource();
    }

    /**
     * 公共事务拦截器
     *
     * @param commonTransactionManager
     * @param annotationTransactionAttributeSource
     * @return
     */
    @Bean
    public TransactionInterceptor commonTransactionInterceptor(HibernateTransactionManager commonTransactionManager,
                                                               AnnotationTransactionAttributeSource annotationTransactionAttributeSource) {
        TransactionInterceptor interceptor = TransactionInterceptorFactory.createTransactionInterceptor();
        interceptor.setTransactionManager(commonTransactionManager);
        interceptor.setTransactionAttributeSource(annotationTransactionAttributeSource);
        return interceptor;
    }

    /**
     * 用户事务拦截器
     *
     * @param transactionManager
     * @param annotationTransactionAttributeSource
     * @return
     */
    @Bean
    public TransactionInterceptor transactionInterceptor(HibernateTransactionManager transactionManager,
                                                         AnnotationTransactionAttributeSource annotationTransactionAttributeSource) {
        TransactionInterceptor interceptor = TransactionInterceptorFactory.createTransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setTransactionAttributeSource(annotationTransactionAttributeSource);
        return interceptor;
    }

    /**
     * 公共事务通知
     *
     * @param annotationTransactionAttributeSource
     * @param commonTransactionInterceptor
     * @return
     */
    @Bean
    public BeanFactoryTransactionAttributeSourceAdvisor commonTransactionAdvisor(
            AnnotationTransactionAttributeSource annotationTransactionAttributeSource,
            TransactionInterceptor commonTransactionInterceptor) {
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(annotationTransactionAttributeSource);
        advisor.setAdvice(commonTransactionInterceptor);
        return advisor;
    }

    /**
     * 用户事务通知
     *
     * @param annotationTransactionAttributeSource
     * @param transactionInterceptor
     * @return
     */
    @Bean
    public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor(
            AnnotationTransactionAttributeSource annotationTransactionAttributeSource,
            TransactionInterceptor transactionInterceptor) {
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(annotationTransactionAttributeSource);
        advisor.setAdvice(transactionInterceptor);
        return advisor;
    }

    /**
     * 创建动态数据源
     *
     * @param dataSource 主数据源
     * @return
     */
    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource createDynamicDataSource(@Qualifier("userDataSource") DataSource dataSource) {
        Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();
        dataSourceMap.put("master", dataSource);
        return new DynamicDataSource(dataSource, dataSourceMap);
    }

}
