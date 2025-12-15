/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.condition;

import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
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
public interface ProcessNodeConditionEvaluator {

    /**
     * @param processInstanceEntity
     * @param stateCondition
     * @param parser
     * @return
     */
    boolean evaluate(BizProcessInstanceEntity processInstanceEntity, ProcessNodeConfig.StateCondition stateCondition,
                     ProcessDefinitionJsonParser parser);
}
