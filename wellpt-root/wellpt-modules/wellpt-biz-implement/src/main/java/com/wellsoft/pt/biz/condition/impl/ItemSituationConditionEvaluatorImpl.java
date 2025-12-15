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
import com.wellsoft.pt.biz.condition.ItemSituationConditionEvaluator;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import ognl.Ognl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ItemSituationConditionEvaluatorImpl implements ItemSituationConditionEvaluator {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param situationConfig
     * @param values
     * @return
     */
    @Override
    public boolean evaluate(ProcessItemConfig.SituationConfig situationConfig, Map<String, Object> values) {
        List<ProcessItemConfig.SituationConditionConfig> situationConditionConfigs = situationConfig.getConditionConfigs();
        if (CollectionUtils.isEmpty(situationConditionConfigs)) {
            return true;
        }

        List<BizCondition> conditions = Lists.newArrayList();
        for (ProcessItemConfig.SituationConditionConfig situationConditionConfig : situationConditionConfigs) {
            conditions.add(BizConditionFactory.getSituationCondition(situationConditionConfig));
        }

        BizConditionParam conditionParam = getConditionParam(values);
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

    private BizConditionParam getConditionParam(Map<String, Object> values) {
        BizConditionParam bizConditionParam = new BizConditionParam(values);
        return bizConditionParam;
    }
}
