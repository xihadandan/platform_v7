package com.wellsoft.pt.mt;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.jpa.datasource.XADataSource;
import com.wellsoft.pt.jpa.datasource.XADataSourceFactory;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class UpdateTenantDatabaseConnectionListener implements
        ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    TenantService tenantService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            String defaultTenantId = Config.getValue("multi.tenancy.tenant.default");
            String jdbcDataBaseName = Config.getValue("multi.tenancy.tenant.database_name");
            String jdbcPassword = Config.getValue("multi.tenancy.tenant.password");
            String jdbcPort = Config.getValue("multi.tenancy.tenant.port");
            String jdbcServer = Config.getValue("multi.tenancy.tenant.server_name");
            String jdbcType = Config.getValue("database.type");
            String jdbcUsername = Config.getValue("multi.tenancy.tenant.username");
            Tenant tenant = tenantService.getById(defaultTenantId);
            if (tenant != null) {
                tenant.setJdbcDatabaseName(jdbcDataBaseName);
                tenant.setJdbcPassword(jdbcPassword);
                tenant.setJdbcPort(jdbcPort);
                tenant.setJdbcType(jdbcType);
                tenant.setJdbcServer(jdbcServer);
                tenant.setJdbcUsername(jdbcUsername);
                tenantService.save(tenant);
                try {
                    XADataSource xaDataSource = XADataSourceFactory.build(tenant);
                    xaDataSource.getConnection().close();
                } catch (Exception e) {
                    throw new RuntimeException("无法获取数据库连接，请检查数据库properties配置是否正确");
                }
            }

        }

    }


}
