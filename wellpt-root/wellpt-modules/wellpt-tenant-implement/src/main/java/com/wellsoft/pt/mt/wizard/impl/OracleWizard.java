package com.wellsoft.pt.mt.wizard.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.bean.TenantBean;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.entity.TenantCreateProcessLog;
import com.wellsoft.pt.mt.entity.TenantUpgradeProcessLog;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.mt.service.TenantService;
import com.wellsoft.pt.mt.wizard.Step;
import com.wellsoft.pt.mt.wizard.Wizard;
import com.wellsoft.pt.mt.wizard.WizardContext;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: Oracle向导
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月10日.1	Administrator		2016年3月10日		Create
 * </pre>
 * @date 2016年3月10日
 */
public class OracleWizard implements Wizard {

    private List<Step> steps = new ArrayList<Step>();

    @Override
    public Wizard addStep(Step step) {
        steps.add(step);
        return this;
    }

    @Override
    public void start(WizardContext context) {
        CreateWizardStepLog createWizardStepLog = ApplicationContextHolder.getBean(CreateWizardStepLog.class);
        TenantService tenantService = ApplicationContextHolder.getBean(TenantService.class);
        TenantManagerService tenantManagerService = ApplicationContextHolder.getBean(TenantManagerService.class);
        Tenant tenant = null;
        if (!context.getIsUpdate()) {
            try {
                for (Step step : steps) {
                    String result = step.process(context);
                    if (tenant == null) {
                        tenant = tenantService.getById("T" + context.getCode());
                    }
                    IgnoreLoginUtils.login(tenant.getId(), tenant.getId());
                    createWizardStepLog.createStepLog(null, tenant, result, TenantCreateProcessLog.SUCCESS,
                            context.getBatchNo(), context);
                }
                tenant = tenantService.get(tenant.getUuid());
                tenant.setStatus(Tenant.STATUS_ENABLED);
                TenantBean tenantBean = new TenantBean();
                BeanUtils.copyProperties(tenant, tenantBean);
                tenantManagerService.saveTenant(tenantBean);
                createWizardStepLog.createStepLog(null, tenant, "租户创建成功！", TenantCreateProcessLog.COMPLETE,
                        context.getBatchNo(), context);
                IgnoreLoginUtils.logout();
            } catch (Exception e) {
                IgnoreLoginUtils.logout();
                createWizardStepLog.createStepLog(null, tenant, e.getMessage(), TenantCreateProcessLog.FAIL,
                        context.getBatchNo(), context);
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            UpdateWizardStepLog updateWizardStepLog = ApplicationContextHolder.getBean(UpdateWizardStepLog.class);
            tenant = tenantService.get(context.getUuid());
            try {
                for (Step step : steps) {
                    step.process(context);
                }
            } catch (Exception e) {
                updateWizardStepLog.updateStepLog(null, tenant, e.getMessage(), TenantUpgradeProcessLog.FAIL, -1,
                        context);
                throw new RuntimeException(e.getMessage(), e);
            }

        }
    }
}
