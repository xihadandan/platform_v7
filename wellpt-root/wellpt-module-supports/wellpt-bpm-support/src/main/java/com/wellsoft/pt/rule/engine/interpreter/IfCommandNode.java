/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.interpreter;

import com.wellsoft.context.web.controller.Message;
import com.wellsoft.pt.rule.engine.*;
import com.wellsoft.pt.rule.engine.expression.ConditionExpression;
import com.wellsoft.pt.rule.engine.suport.CommandName;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
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
 * 2015-6-24.1	zhulh		2015-6-24		Create
 * </pre>
 * @date 2015-6-24
 */
public class IfCommandNode extends Node {
    private String name;
    private List<StringBuilder> conditionExp = new ArrayList<StringBuilder>();
    private Map<StringBuilder, Node> conditionMap = new HashMap<StringBuilder, Node>();

    private List<ConditionExpression> conditionExpressions = new ArrayList<ConditionExpression>();
    private Map<ConditionExpression, Node> conditionExpressionMap = new HashMap<ConditionExpression, Node>();

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.Node#parse(com.wellsoft.pt.rule.engine.Context)
     */
    @Override
    public void parse(Context context) throws ParseException {
        name = context.currentToken();
        context.skipToken(name);
        if (!name.equals("if")) {
            throw new ParseException(name + " is undefined");
        }
        StringBuilder expression = new StringBuilder();
        while (!"end".equals(context.currentToken())) {
            expression.append(context.currentToken());

            if (context.currentToken().endsWith("){")) {
                StringBuilder condition = extractConditionExp(expression);

                context.nextToken();
                Node commandListNode = new CommandListNode();
                commandListNode.parse(context);

                conditionExp.add(condition);
                conditionMap.put(condition, commandListNode);
            }

            // expression.append(" ");
            context.nextToken();

            if (context.currentToken().equals("else")) {
                context.nextToken();
                if (context.currentToken().equals("if")) {
                    context.nextToken();
                }
            }
        }
        // end if action
        context.skipToken("end");

        // 转化为表达式
        for (int index = 0; index < conditionExp.size(); index++) {
            StringBuilder sb = conditionExp.get(index);
            String expString = sb.toString();
            expString = StringUtils.replace(expString, "&&", " && ");
            expString = StringUtils.replace(expString, "||", " || ");
            expString = StringUtils.replace(expString, "notcontains", " not contains ");
            expString = StringUtils.replace(expString, "contains", " contains ");
            expString = StringUtils.replace(expString, CommandName.NewFlowMemberOf, " " + CommandName.NewFlowMemberOf
                    + " ");
            expString = StringUtils.replace(expString, "${", "");
            expString = StringUtils.replace(expString, "}", "");
            ConditionExpression exp = new ConditionExpression(expString);
            conditionExpressions.add(exp);
            conditionExpressionMap.put(exp, conditionMap.get(sb));
        }
    }

    private StringBuilder extractConditionExp(StringBuilder exp) throws ParseException {
        StringBuilder condition = new StringBuilder();
        String expStr = exp.toString();
        if (expStr.startsWith("(") && expStr.endsWith("){")) {
            condition.append(expStr.substring(1, expStr.length() - 2));
        } else {
            throw new ParseException(name + " is undefined");
        }
        exp.setLength(0);
        return condition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.Executor#execute(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Result execute(Param param) throws ExecuteException {
        Context context = param.getContext();
        Integer level = param.getLevel();
        Message msg = param.getMsg();

        logger.debug("start execute IfCommonNode at level " + level);
        Result result = Result.NORMAL;
        for (int index = 0; index < conditionExpressions.size(); index++) {
            ConditionExpression expression = conditionExpressions.get(index);
            if (Boolean.TRUE.equals(expression.evaluate(param))) {
                result = conditionExpressionMap.get(expression).execute(
                        new Param(context, getNextLevel(level), new StringBuilder(expression.getExpression()), msg));
                break;
            }
        }
        logger.debug("over execute IfCommonNode at level " + level);

        return result;
    }

    protected boolean evaluate(Context context, Integer level, StringBuilder sb) {
        try {
            // 判断条件是否为true
            Object result = Ognl.getValue(sb.toString(), context.getOgnlContext());
            if (result instanceof Boolean) {
                return (Boolean) result;
                // if (result.equals(true)) {
                // return true;
                // }
            }
        } catch (OgnlException e) {
            throw new ExecuteException(e);
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[if " + conditionExp.toString() + " end]";
    }

}
