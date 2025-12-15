package com.wellsoft.oauth2.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import com.wellsoft.oauth2.repository.SpringPhysicalNamingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

/**
 * Description: 数据源配置
 *
 * @author chenq
 * @date 2019/9/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/10    chenq		2019/9/10		Create
 * </pre>
 */
@Configuration
@PropertySource(value = "classpath:application-jdbc.properties")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.wellsoft.oauth2.repository")
public class DataSourceConfiguration {


    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${jdbc.user}")
    private String jdbcUser;

    @Value("${jdbc.password}")
    private String jdbcPassword;

    @Value("${jdbc.driver:}")
    private String driver;

    @Value("${hibernate.dialect:}")
    private String dialect;

    @Value("${jdbc.initSize:50}")
    private int jdbcInitSize;

    @Value("${jdbc.minIdel:50}")
    private int minIdel;

    @Value("${jdbc.maxActive:100}")
    private int maxActive;

    @Value("${jdbc.maxWait:60000}")
    private long maxWait;

    @Value("${jdbc.timeBetweenEvictionRunsMillis:60000}")
    private long timeBetweenEvictionRunsMillis;

    @Value("${jdbc.minEvictableIdleTimeMillis:300000}")
    private long minEvictableIdleTimeMillis;

    @Value("${jdbc.validationQuery:SELECT 'x'}")
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

    @Value("${showSql:false}")
    private boolean showSql;


    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(jdbcUser);
        dataSource.setPassword(jdbcPassword);
        if (StringUtils.isNotBlank(driver)) {
            dataSource.setDriverClassName(driver);
        }
        dataSource.setInitialSize(jdbcInitSize);
        dataSource.setMinIdle(minIdel);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(
                maxPoolPreparedStatementPerConnectionSize);
        return dataSource;
    }


    @Bean
    public NamedParameterJdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean localEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localEntityManagerFactoryBean.setJpaDialect(new HibernateJpaDialect());
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setPrepareConnection(true);
        localEntityManagerFactoryBean.setPackagesToScan("com.wellsoft.oauth2.entity");
        localEntityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        localEntityManagerFactoryBean.setDataSource(dataSource);
        Map<String, Object> jpaPropertyMap = Maps.newHashMap();
        jpaPropertyMap.put("hibernate.physical_naming_strategy",
                SpringPhysicalNamingStrategy.class.getCanonicalName());
        jpaPropertyMap.put("hibernate.show_sql", showSql);
        if (StringUtils.isNotBlank(dialect)) {
            jpaPropertyMap.put("hibernate.dialect", dialect);
        }
        localEntityManagerFactoryBean.setJpaPropertyMap(jpaPropertyMap);
        return localEntityManagerFactoryBean;
    }


    @Bean
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(
                entityManagerFactory);
        return jpaTransactionManager;
    }

}
