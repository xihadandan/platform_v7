/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.condition.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.biz.condition.BizCondition;
import com.wellsoft.pt.biz.condition.BizConditionParam;
import com.wellsoft.pt.biz.service.BizProcessNodeInstanceService;
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
public class BizProcessStateCondition implements BizCondition {

    private ProcessNodeConfig.StateConditionConfig config;

    /**
     * @param config
     */
    public BizProcessStateCondition(ProcessNodeConfig.StateConditionConfig config) {
        this.config = config;
    }

    @Override
    public String evaluate(BizConditionParam conditionParam) {
        String processInstUuid = conditionParam.getProcessInstUuid();
        String processNodeCode = config.getProcessNodeCode();
        String processNodeState = config.getProcessNodeState();

        BizProcessNodeInstanceService processNodeInstanceService = ApplicationContextHolder.getBean(BizProcessNodeInstanceService.class);
        boolean existProcessNodeInstanct = processNodeInstanceService.isExistsProcessNodeInstanceByCode(processNodeCode, processNodeState, processInstUuid);
        return config.getLeftBracket() + existProcessNodeInstanct + config.getRightBracket();
    }

}
