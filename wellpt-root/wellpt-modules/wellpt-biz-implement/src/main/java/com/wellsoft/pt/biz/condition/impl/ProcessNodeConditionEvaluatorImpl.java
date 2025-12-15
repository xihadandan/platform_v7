/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.condition.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.biz.condition.BizCondition;
import com.wellsoft.pt.biz.condition.BizConditionFactory;
import com.wellsoft.pt.biz.condition.BizConditionParam;
import com.wellsoft.pt.biz.condition.ProcessNodeConditionEvaluator;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import ognl.Ognl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

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
@Component
public class ProcessNodeConditionEvaluatorImpl implements ProcessNodeConditionEvaluator {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param processInstanceEntity
     * @param stateCondition
     * @param parser
     * @return
     */
    @Override
    public boolean evaluate(BizProcessInstanceEntity processInstanceEntity, ProcessNodeConfig.StateCondition stateCondition,
                            ProcessDefinitionJsonParser parser) {
        List<ProcessNodeConfig.StateConditionConfig> configs = stateCondition.getConfigs();
        if (CollectionUtils.isEmpty(configs)) {
            return true;
        }

        List<BizCondition> conditions = Lists.newArrayList();
        for (ProcessNodeConfig.StateConditionConfig config : configs) {
            conditions.add(BizConditionFactory.getCondition(config));
        }

        BizConditionParam conditionParam = getConditionParam(processInstanceEntity, parser);
        StringBuilder expression = new StringBuilder();
        for (BizCondition condition : conditions) {
            expression.append(condition.evaluate(conditionParam));
        }

        Object result = 0;
        try {
            result = Ognl.getValue(expression.toString(), new HashMap<String, String>());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return (result instanceof Boolean) ? (Boolean) result : Integer.valueOf(1).equals(result);
    }

    /**
     * 获取条件参数
     *
     * @param processInstanceEntity
     * @param parser
     * @return
     */
    private BizConditionParam getConditionParam(BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser) {
        String processInstUuid = processInstanceEntity.getUuid();
        String entityName = processInstanceEntity.getEntityName();
        String entityId = processInstanceEntity.getEntityId();
        BizConditionParam bizConditionParam = new BizConditionParam(processInstUuid, entityName, entityId, parser);
        return bizConditionParam;
    }

}
