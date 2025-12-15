package com.wellsoft.pt.mt.wizard.impl;

import com.wellsoft.pt.mt.wizard.Step;
import com.wellsoft.pt.mt.wizard.WizardContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CreateUpgradeBatchStep extends AbstractStep implements Step {

    @Override
    public String process(WizardContext context) {
        // TODO Auto-generated method stub
        return null;
    }

}
