package com.wellsoft.pt;

import com.alibaba.druid.pool.DruidDataSource;
import com.wellsoft.pt.jpa.hibernate4.CustomeImprovedNamingStrategy;
import com.wellsoft.pt.jpa.hibernate4.LocalSpringSessionContext;
import com.wellsoft.pt.jpa.log4tx.support.TransactionInterceptorFactory;
import com.wellsoft.pt.jpa.support.CommonEntityAnnotatedClassesFactoryBean;
import com.wellsoft.pt.jpa.support.HibernateMappingLocationsFactoryBean;
import com.wellsoft.pt.jpa.support.SupportMultiDatabaseLocalSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Properties;

/**
 * Description: 邮件服务配置化
 *
 * @author chenq
 * @date 2019/11/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/8    chenq		2019/11/8		Create
 * </pre>
 */
@Configuration
public class JamesMailConfiguration {
    @Value("#{'jdbc/'+'${multi.tenancy.james.datasource}'}")
    private String jamesDatasourceId;
    @Value("${multi.tenancy.james.server_name}")
    private String jamesDsServerName;
    @Value("${multi.tenancy.james.database_name}")
    private String jamesDatabasename;
    @Value("${multi.tenancy.james.url}")
    private String jamesDsUrl;
    @Value("${multi.tenancy.james.username}")
    private String jamesDsUsername;
    @Value("${multi.tenancy.james.password}")
    private String jamesDsPassword;
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

    //===========druid数据源配置====================
    /**
     * 配置初始化大小、最小、最大
     */
    @Value("${multi.tenancy.james.pool.initSize:10}")
    private int jdbcInitSize;

    @Value("${multi.tenancy.james.pool.minIdle:10}")
    private int minIdle;

    @Value("${jdbc.maxActive:100}")
    private int maxActive;

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
     * 用户库数据库资源
     *
     * @return
     */
    @Bean
    public DruidDataSource mailDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(jamesDsUrl);
        dataSource.setUsername(jamesDsUsername);
        dataSource.setPassword(jamesDsPassword);
        dataSource.setInitialSize(jdbcInitSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
//		dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(
                maxPoolPreparedStatementPerConnectionSize);
        return dataSource;
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
        classesFactoryBean.setPackagesToScan("com.wellsoft.pt.mail.entity");
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
        properties.put("hibernate.current_session_context_class", LocalSpringSessionContext.class.getCanonicalName());
        // bug#58194: hibernate.enable_lazy_load_no_trans设置为true或taskService.startSubFlow即时加载流程定义
        properties.put("hibernate.enable_lazy_load_no_trans", true);
        sessionFactoryBean.setHibernateProperties(properties);
        return sessionFactoryBean;
    }


    @Bean
    public SupportMultiDatabaseLocalSessionFactoryBean james(
            @Qualifier("mailDataSource") javax.sql.DataSource dataSource) throws Exception {
        return buildSessionFactory(
                dataSource);
    }

    /**
     * 邮件事务管理器
     *
     * @return
     */
    @Bean
    public HibernateTransactionManager mailTransactionManager(@Qualifier("james") SupportMultiDatabaseLocalSessionFactoryBean sessionFactory) {
        HibernateTransactionManager mailTransactionManager = new HibernateTransactionManager();
        mailTransactionManager.setSessionFactory(sessionFactory.getObject());
        return mailTransactionManager;
    }

    /**
     * 邮件事务拦截器
     *
     * @param mailTransactionManager
     * @param annotationTransactionAttributeSource
     * @return
     */
    @Bean
    public TransactionInterceptor mailTransactionInterceptor(HibernateTransactionManager mailTransactionManager,
                                                             AnnotationTransactionAttributeSource annotationTransactionAttributeSource) {
        TransactionInterceptor interceptor = TransactionInterceptorFactory.createTransactionInterceptor();
        interceptor.setTransactionManager(mailTransactionManager);
        interceptor.setTransactionAttributeSource(annotationTransactionAttributeSource);
        return interceptor;
    }


    /**
     * 邮件事务通知
     *
     * @param annotationTransactionAttributeSource
     * @param mailTransactionInterceptor
     * @return
     */
    @Bean
    public BeanFactoryTransactionAttributeSourceAdvisor mailTransactionAdvisor(
            AnnotationTransactionAttributeSource annotationTransactionAttributeSource,
            TransactionInterceptor mailTransactionInterceptor) {
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(annotationTransactionAttributeSource);
        advisor.setAdvice(mailTransactionInterceptor);
        return advisor;
    }


}
