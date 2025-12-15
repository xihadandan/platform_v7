package com.wellsoft.pt.ureport;

import com.wellsoft.pt.jpa.hibernate.BitronixDataSourceFactoryBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/11/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/13    chenq		2019/11/13		Create
 * </pre>
 */
//@Configuration 已经去除分布式事务，改用本地事务，使用LocalUreportConfiguration
//@ImportResource("classpath*:ureport-console-context.xml")
public class UreportConfiguration {


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

    @Bean
    public BitronixDataSourceFactoryBean reportDataSource() {
        BitronixDataSourceFactoryBean factoryBean = new BitronixDataSourceFactoryBean();
        factoryBean.setUniqueName(reportDatasourceId);
        factoryBean.setMaxPoolSize(30);
        factoryBean.setDatabaseType(databaseType);
        factoryBean.setAllowLocalTransactions(true);
        Properties properties = new Properties();
        properties.put("ServerName",
                StringUtils.defaultIfBlank(reposrtDsServerName, tenantDsServerName));
        properties.put("DatabaseName",
                StringUtils.defaultIfBlank(reportDatabasename, tenantDatabasename));
        properties.put("SelectMethod", "cursor");
        properties.put("URL", StringUtils.defaultIfBlank(reportDsUrl, tenantDsUrl));
        properties.put("User", StringUtils.defaultIfBlank(reportDsUsername, tenantDsUsername));
        properties.put("Password", StringUtils.defaultIfBlank(reportDsPassword, tenantDsPassword));
        factoryBean.setDriverProperties(properties);
        return factoryBean;
    }
}
