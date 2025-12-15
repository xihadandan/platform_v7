package com.wellsoft.pt.mt.wizard.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mt.dbmigrate.ExecuteCallback;
import com.wellsoft.pt.mt.dbmigrate.IMigrateModule;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.wizard.Step;
import com.wellsoft.pt.mt.wizard.WizardContext;
import com.wellsoft.pt.mt.wizard.WizardStepLog;
import com.wellsoft.pt.mt.wizard.WizardUpdateStepLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public abstract class AbstractStep extends BaseServiceImpl implements Step {
    private static final int IS_UPDATE = -1;
    @Autowired
    private WizardStepLog wizardStepLog;
    @Autowired
    private WizardUpdateStepLog wizardUpdateStepLog;

    public ExecuteCallback createLog(final int batchNo, final WizardContext context) {
        ExecuteCallback executeCallback = new ExecuteCallback() {
            private int tempBatchNo = batchNo;

            @Override
            public void onExecuting(IMigrateModule module, Tenant tempTenant) {
            }

            @Override
            public void onExecuted(IMigrateModule module, Tenant tempTenant, Integer status, String message) {
                // TODO Auto-generated method stub
                if (IS_UPDATE != batchNo) {
                    wizardStepLog.createStepLog(module, tempTenant, message, status, batchNo, context);
                } else {
                    wizardUpdateStepLog.updateStepLog(module, tempTenant, message, status, batchNo, context);
                }
            }
        };
        return executeCallback;
    }
}
