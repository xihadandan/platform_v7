package com.wellsoft.pt.mt.wizard.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.service.TenantService;
import com.wellsoft.pt.mt.wizard.Step;
import com.wellsoft.pt.mt.wizard.WizardContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CreateTenantStep extends BaseServiceImpl implements Step {
    @Autowired
    TenantService tenantService;

    @Override
    public String process(WizardContext context) {
        // TODO Auto-generated method stub
        Tenant tenant = new Tenant();
        if (StringUtils.isNotBlank(context.getUuid())) {
            tenant = tenantService.get(context.getUuid());
        }
        BeanUtils.copyProperties(context, tenant);
        tenant.setJdbcDatabaseName(Config.getValue("multi.tenancy.common.database_name"));
        tenant.setJdbcPassword(context.getPassword());
        tenant.setJdbcUsername(context.getAccount());
        tenant.setJdbcPort(String.valueOf(context.getJdbcPort()));
        tenant.setJdbcServer(Config.getValue("multi.tenancy.common.server_name"));
        tenant.setId("T" + tenant.getCode());
        tenant.setJdbcType("Oracle11g");
        tenant.setStatus(Tenant.STATUS_TO_REVIEW);
        tenantService.save(tenant);
        context.setUuid(tenant.getUuid());
        return "租户数据生成成功！";
    }

}
