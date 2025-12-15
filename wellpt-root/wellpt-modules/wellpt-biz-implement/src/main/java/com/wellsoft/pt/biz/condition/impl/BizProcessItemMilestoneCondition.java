/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.condition.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.biz.condition.BizCondition;
import com.wellsoft.pt.biz.condition.BizConditionParam;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/25/22.1	zhulh		10/25/22		Create
 * </pre>
 * @date 10/25/22
 */
public class BizProcessItemMilestoneCondition implements BizCondition {

    private ProcessNodeConfig.StateConditionConfig config;

    /**
     * @param config
     */
    public BizProcessItemMilestoneCondition(ProcessNodeConfig.StateConditionConfig config) {
        this.config = config;
    }

    @Override
    public String evaluate(BizConditionParam conditionParam) {
        String processInstUuid = conditionParam.getProcessInstUuid();
        String itemCode = config.getProcessItemCode();
        BizProcessItemInstanceService processItemInstanceService = ApplicationContextHolder.getBean(BizProcessItemInstanceService.class);
        boolean isCompleteMilestone = processItemInstanceService.isCompleteMilestone(itemCode, processInstUuid);
        return config.getLeftBracket() + isCompleteMilestone + config.getRightBracket();
    }
}
