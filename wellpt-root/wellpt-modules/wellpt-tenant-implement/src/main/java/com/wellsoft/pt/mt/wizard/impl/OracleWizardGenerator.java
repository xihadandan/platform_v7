package com.wellsoft.pt.mt.wizard.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.mt.wizard.Wizard;
import com.wellsoft.pt.mt.wizard.WizardGenerator;

/**
 * Description: Oracle向导生成器
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
public class OracleWizardGenerator implements WizardGenerator {

    @Override
    public Wizard getCreateWizard(String databaseType) {//Oracle11g
        OracleWizard oracleWizard = new OracleWizard();
        CreateTenantStep createTenantStep = ApplicationContextHolder.getBean(CreateTenantStep.class);
        CreateDatabaseStep createDatabaseStep = ApplicationContextHolder.getBean(CreateDatabaseStep.class);
        InitDatabaseStep initDatabaseStep = ApplicationContextHolder.getBean(InitDatabaseStep.class);
        InitDefinitionDataStep initDefinitionDataStep = ApplicationContextHolder.getBean(InitDefinitionDataStep.class);
        oracleWizard.addStep(createTenantStep);
        oracleWizard.addStep(createDatabaseStep);
        oracleWizard.addStep(initDatabaseStep);
        oracleWizard.addStep(initDefinitionDataStep);
        return oracleWizard;
    }

    @Override
    public Wizard getUpgradeWizard(String databaseType) {
        UpgradeTenantDataStep upgradeTenantDataStep = ApplicationContextHolder.getBean(UpgradeTenantDataStep.class);
        OracleWizard oracleWizard = new OracleWizard();
        return oracleWizard.addStep(upgradeTenantDataStep);
    }
}
