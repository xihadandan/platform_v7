package com.wellsoft.pt.mt.wizard.impl;

import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.mt.wizard.Step;
import com.wellsoft.pt.mt.wizard.WizardContext;
import com.wellsoft.pt.mt.wizard.WizardStepLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CreateDatabaseStep extends AbstractStep implements Step {
    @Autowired
    private WizardStepLog WizardStepLog;

    @Autowired
    private TenantManagerService tenantManagerService;

    @Override
    public String process(WizardContext context) {
        Tenant tenant = this.getCommonDao().get(Tenant.class, context.getUuid());
        final int batchNo = context.getBatchNo();
        tenantManagerService.migrateTenantDb(tenant.getUuid(), context.getDbaUser(), context.getDbaPassword(),
                super.createLog(batchNo, null));
        return "租户数据库创建成功！";
    }
}
