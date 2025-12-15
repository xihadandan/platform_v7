/*
 * @(#)10/26/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.condition.impl;

import com.wellsoft.pt.biz.condition.BizCondition;
import com.wellsoft.pt.biz.condition.BizConditionParam;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import ognl.Ognl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/26/22.1	zhulh		10/26/22		Create
 * </pre>
 * @date 10/26/22
 */
public class BizFormFieldCondition implements BizCondition {
    private Logger logger = LoggerFactory.getLogger(getClass());

    // 大于
    public static final String GT = ">";
    // 大于等于
    public static final String GEQ = ">=";
    // 小于
    public static final String LT = "<";
    // 小于等于
    public static final String LEQ = "<=";
    // 等于
    public static final String EQ = "==";
    // 不等于
    public static final String NEQ = "!=";
    // 包含
    public static final String LIKE = "like";
    // 不包含
    public static final String NOLIKE = "notlike";

    private ProcessItemConfig.SituationConditionConfig situationConditionConfig;

    public BizFormFieldCondition(ProcessItemConfig.SituationConditionConfig situationConditionConfig) {
        this.situationConditionConfig = situationConditionConfig;
    }

    @Override
    public String evaluate(BizConditionParam conditionParam) {
        Map<String, Object> paramMap = conditionParam.getParamMap();
        String fieldName = situationConditionConfig.getFieldName();
        Object fieldValue = paramMap.get(fieldName);
        String matchValue = situationConditionConfig.getValue();
        String operator = situationConditionConfig.getOperator();
        boolean evaluateResult = false;
        try {
            switch (operator) {
                case LIKE:
                    evaluateResult = StringUtils.contains(Objects.toString(fieldValue), matchValue);
                    break;
                case NOLIKE:
                    evaluateResult = !StringUtils.contains(Objects.toString(fieldValue), matchValue);
                    break;
                default:
                    String newExpression = fieldName + ' ' + operator + ' ' + matchValue;
                    Object result = Ognl.getValue(newExpression, paramMap);
                    evaluateResult = (result instanceof Boolean) ? (Boolean) result : Integer.valueOf(1).equals(result);
                    break;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return situationConditionConfig.getLeftBracket() + evaluateResult + situationConditionConfig.getRightBracket();
    }

}
