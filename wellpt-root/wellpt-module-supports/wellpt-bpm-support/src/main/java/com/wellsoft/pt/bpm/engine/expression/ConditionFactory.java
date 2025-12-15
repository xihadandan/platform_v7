/*
 * @(#)2013-4-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.expression;

import com.wellsoft.context.enums.Encoding;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-26.1	zhulh		2013-4-26		Create
 * </pre>
 * @date 2013-4-26
 */
public class ConditionFactory {

    private static Logger LOG = LoggerFactory.getLogger(ConditionFactory.class);

    private static final String VOTE = "VOTE";

    private static final String IS_MEMBER = "@ISMEMBER";

    /**
     * 职等职级
     **/
    private static final String DUTY_GRADE = "@DUTYGRADE";

    private static final String LOGIC_AND = "&";

    private static final String LOGIC_OR = "|";

    public static Condition getCondition(final String rawExpression, String extraData, final Integer type) {
        String expression = decodeIfRequired(rawExpression);
        Condition condition = null;
        switch (type) {
            case 1:
                condition = new FormFieldCondition(expression);
                break;
            case 2:
                condition = new VoteCondition(expression);
                break;
            case 4:
                condition = new MemberCondition(expression, extraData);
                break;
            case 8:
                condition = new LogicCondition(expression);
                break;
            case 16:
                condition = new CustomExpressionCondition(expression);
                break;
            case 32:
                condition = new OpinionPositionCondition(expression);
                break;
            default:
                condition = getCondition(expression);
                break;
        }
        return condition;
    }

    /**
     * @param rawExpression
     * @return
     */
    private static String decodeIfRequired(String rawExpression) {
        String expression = rawExpression;
        if (StringUtils.isBlank(expression)) {
            return expression;
        }

        if (containsEncodedParts(expression)) {
            try {
                expression = URLDecoder.decode(expression, Encoding.UTF8.getValue());
            } catch (UnsupportedEncodingException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return expression;
    }

    public static Condition getCondition(final String rawExpression, String extraData) {
        String expression = decodeIfRequired(rawExpression);

        if (StringUtils.isBlank(expression)) {
            return new FormFieldCondition(expression);
        }

        // 意见立场表达式
        if (expression.indexOf(VOTE) != -1) {
            return new VoteCondition(expression);
        } else if (expression.indexOf(IS_MEMBER) != -1 || expression.indexOf(IS_MEMBER) != -1) {// 所属成员表达式
            return new MemberCondition(expression, extraData);
        } else if (StringUtils.trim(expression).equals(LOGIC_AND) || StringUtils.trim(expression).equals(LOGIC_OR)) {
            return new LogicCondition(expression);
        } else if (expression.indexOf(DUTY_GRADE) != -1) {
            // 职等职级
            return new DutyGradeCondition(expression);
        }
        return new FormFieldCondition(expression);
    }

    public static Condition getCondition(final String rawExpression) {
        return getCondition(rawExpression, StringUtils.EMPTY);
    }

    private static boolean containsEncodedParts(String expression) {
        return StringUtils.contains(expression, "%");
    }
}
