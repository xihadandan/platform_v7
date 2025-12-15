package com.wellsoft.pt.mt.wizard.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mt.dbmigrate.IMigrateModule;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.entity.TenantUpgradeBatch;
import com.wellsoft.pt.mt.entity.TenantUpgradeProcessLog;
import com.wellsoft.pt.mt.wizard.WizardContext;
import com.wellsoft.pt.mt.wizard.WizardUpdateStepLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Transactional
@Service
public class UpdateWizardStepLog extends BaseServiceImpl implements WizardUpdateStepLog {
    @Override
    public String updateStepLog(IMigrateModule module, Tenant tenant, String message, int status, int batchNo,
                                WizardContext context) {
        TenantUpgradeProcessLog tenantUpgradeProcessLog = new TenantUpgradeProcessLog();
        String hql = "from TenantUpgradeProcessLog t where t.tenantUuid=:uuid and t.tenantUpgradeBatch.uuid=:upgradeBatchUuid ";
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("uuid", tenant.getUuid());
        queryMap.put("upgradeBatchUuid", context.getUpgradeBatchUuid());
        List<TenantUpgradeProcessLog> tenantUpgradeProcessLogs = this.getCommonDao().query(hql, queryMap);
        int setup = 1;
        if (tenantUpgradeProcessLogs.size() != 0) {
            setup = tenantUpgradeProcessLogs.size() + 1;
        }
        TenantUpgradeBatch tenantUpgradeBatch = this.getCommonDao().get(TenantUpgradeBatch.class,
                context.getUpgradeBatchUuid());
        tenantUpgradeProcessLog.setName(message);
        tenantUpgradeProcessLog.setStatus(status);
        tenantUpgradeProcessLog.setTenantUuid(tenant.getUuid());
        tenantUpgradeProcessLog.setTenantUpgradeBatch(tenantUpgradeBatch);
        tenantUpgradeProcessLog.setSortOrder(setup);
        tenantUpgradeProcessLog.setFileName(context.getRepoFileName());
        this.getCommonDao().save(tenantUpgradeProcessLog);
        return tenantUpgradeProcessLog.getUuid();
    }

}
