package com.wellsoft.pt.ureport;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Description:报表数据源切换成druid数据连接池
 *
 * @author chenq
 * @date 2019/11/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 6.2           liuxj		2022/1/6		Create
 * </pre>
 */
@Configuration
@ImportResource("classpath*:ureport-console-context.xml")
public class LocalUreportConfiguration {


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

    @Value("#{'jdbc/report'}")
    private String reportDatasourceId;
    @Value("${multi.tenancy.report.server_name:}")
    private String reposrtDsServerName;
    @Value("${multi.tenancy.report.database_name:}")
    private String reportDatabasename;
    @Value("${multi.tenancy.report.url:}")
    private String reportDsUrl;
    @Value("${multi.tenancy.report.username:}")
    private String reportDsUsername;
    @Value("${multi.tenancy.report.password:}")
    private String reportDsPassword;
    @Value("${database.type}")
    private String databaseType;


    //===========druid数据源配置====================
    /**
     * 配置初始化大小、最小、最大
     */
    @Value("${multi.tenancy.report.pool.initSize:5}")
    private int jdbcInitSize;

    @Value("${multi.tenancy.report.pool.minIdle:5}")
    private int minIdle;

    @Value("${multi.tenancy.report.pool.maxActive:30}")
    private int maxActive;

    /**
     * 配置获取连接等待超时的时间
     */
    @Value("${report.jdbc.maxWait:60000}")
    private long maxWait;
    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    @Value("${report.jdbc.timeBetweenEvictionRunsMillis:60000}")
    private long timeBetweenEvictionRunsMillis;
    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    @Value("${report.jdbc.minEvictableIdleTimeMillis:300000}")
    private long minEvictableIdleTimeMillis;

    @Value("${report.jdbc.validationQuery: select 1 from dual }")
    private String validationQuery;

    @Value("${report.jdbc.testOnBorrow:false}")
    private boolean testOnBorrow;

    @Value("${report.jdbc.testOnReturn:false}")
    private boolean testOnReturn;

    @Value("${report.jdbc.testWhileIdle:true}")
    private boolean testWhileIdle;

    @Value("${report.jdbc.poolPreparedStatements:true}")
    private boolean poolPreparedStatements;

    @Value("${report.jdbc.maxPoolPreparedStatementPerConnectionSize:50}")
    private int maxPoolPreparedStatementPerConnectionSize;


    /**
     * 报表数据源
     *
     * @return
     */
    @Bean
    public DruidDataSource reportDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(StringUtils.defaultIfBlank(reportDsUrl, tenantDsUrl));
        dataSource.setUsername(StringUtils.defaultIfBlank(reportDsUsername, tenantDsUsername));
        dataSource.setPassword(StringUtils.defaultIfBlank(reportDsPassword, tenantDsPassword));
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
}
