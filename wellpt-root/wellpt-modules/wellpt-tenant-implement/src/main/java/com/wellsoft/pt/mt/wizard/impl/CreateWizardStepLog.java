package com.wellsoft.pt.mt.wizard.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mt.dbmigrate.IMigrateModule;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.entity.TenantCreateProcessLog;
import com.wellsoft.pt.mt.wizard.WizardContext;
import com.wellsoft.pt.mt.wizard.WizardStepLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Transactional
@Service
public class CreateWizardStepLog extends BaseServiceImpl implements WizardStepLog {
    @Override
    public String createStepLog(IMigrateModule module, Tenant tenant, String message, int status, int batchNo,
                                WizardContext context) {
        TenantCreateProcessLog createProcessLog = new TenantCreateProcessLog();
        String hql = "from TenantCreateProcessLog t where t.tenantUuid=:uuid and t.batchNo=:batchNo ";
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("uuid", tenant.getUuid());
        queryMap.put("batchNo", batchNo);
        List<TenantCreateProcessLog> createProcessLogs = this.getCommonDao().query(hql, queryMap);
        int setup = 1;
        if (createProcessLogs.size() != 0) {
            setup = createProcessLogs.size() + 1;
        }
        createProcessLog.setName(message);
        createProcessLog.setStatus(status);
        createProcessLog.setTenantUuid(tenant.getUuid());
        createProcessLog.setBatchNo(batchNo);
        createProcessLog.setSortOrder(setup);
        createProcessLog.setContent(message);
        this.getCommonDao().save(createProcessLog);
        return createProcessLog.getUuid();
    }

}
