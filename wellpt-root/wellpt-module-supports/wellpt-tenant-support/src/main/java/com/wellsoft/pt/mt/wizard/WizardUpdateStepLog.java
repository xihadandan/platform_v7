package com.wellsoft.pt.mt.wizard;

import com.wellsoft.pt.mt.dbmigrate.IMigrateModule;
import com.wellsoft.pt.mt.entity.Tenant;

public interface WizardUpdateStepLog {
    /**
     * 如何描述该方法
     *
     * @param module           模块
     * @param tenant           租户
     * @param message          消息
     * @param status           状态
     * @param batchNo          批次号/更新传入-1
     * @param upgradeBatchUuid 更新批次UUID
     * @return
     */
    String updateStepLog(IMigrateModule module, Tenant tenant, String message, int status, int batchNo,
                         WizardContext context);
}
