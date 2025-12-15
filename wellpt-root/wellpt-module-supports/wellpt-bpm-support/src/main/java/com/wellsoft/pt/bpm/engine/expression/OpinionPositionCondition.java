/*
 * @(#)2021年9月25日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.expression;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年9月25日.1	zhulh		2021年9月25日		Create
 * </pre>
 * @date 2021年9月25日
 */
public class OpinionPositionCondition extends AbstractCondition {

    private String prefix = "";

    private String suffix = "";

    /**
     * @param expression
     */
    public OpinionPositionCondition(String expression) {
        String exp = StringUtils.trim(expression);
        if (exp.startsWith("& (") || exp.startsWith("| (")) {
            prefix = exp.substring(0, 3);
            exp = exp.substring(3);
        } else if (exp.startsWith("& ") || exp.startsWith("| ")) {
            prefix = exp.substring(0, 2);
            exp = exp.substring(2);
        } else if (exp.startsWith("&") || exp.startsWith("|")) {
            prefix = exp.substring(0, 1);
            exp = exp.substring(1);
        }

        if (exp.startsWith("(") && !exp.endsWith(")")) {
            prefix += exp.substring(0, 1);
            exp = exp.substring(1);
        }

        if (!exp.startsWith("(") && exp.endsWith(")")) {
            suffix += exp.substring(exp.length() - 1, exp.length());
            exp = exp.substring(0, exp.length() - 1);
        }

        if (exp.startsWith("(") && exp.endsWith(")")) {
            prefix += "(";
            suffix += ")";
            exp = exp.substring(1, exp.length() - 1);
        }

        setExpression(exp);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.expression.Condition#evaluate(com.wellsoft.pt.bpm.engine.core.Token, com.wellsoft.pt.bpm.engine.node.Node)
     */
    @Override
    public String evaluate(Token token, Node to) {
        boolean result = false;
        String expression = getExpression();
        String[] opinionPositionValues = StringUtils.split(expression, Separator.SPACE.getValue());
        if (opinionPositionValues.length == 3) {
            TaskData taskData = token.getTaskData();
            String key = taskData.getTaskInstUuid() + SpringSecurityUtils.getCurrentUserId();
            String opinionValue = taskData.getOpinionValue(key);
            String operator = opinionPositionValues[1];
            String value = opinionPositionValues[2];
            if (StringUtils.equals("==", operator) && StringUtils.equals(value, opinionValue)) {
                result = true;
            } else if (StringUtils.equals("!=", operator) && !StringUtils.equals(value, opinionValue)) {
                result = true;
            }
        }
        return prefix + result + suffix;
    }

}
