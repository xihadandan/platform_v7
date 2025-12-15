/*
 * @(#)2015年8月31日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.rule.engine.Context;
import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.expression.wf.NewFlowMemberOfConditionExpression;
import com.wellsoft.pt.rule.engine.suport.CommandName;
import com.wellsoft.pt.rule.engine.suport.Operator;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月31日.1	zhulh		2015年8月31日		Create
 * </pre>
 * @date 2015年8月31日
 */
public class ConditionExpression implements Expression {

    // 优先级
    private static Map<String, Integer> levelMap = new HashMap<String, Integer>();

    static {
        // ||、&&
        levelMap.put(Operator.NAME_OR, 1);
        levelMap.put(Operator.NAME_AND, 1);
        // >、>=、==、!=、<、<=
        levelMap.put(Operator.NAME_GT, 10);
        levelMap.put(Operator.NAME_GE, 10);
        levelMap.put(Operator.NAME_EQ, 10);
        levelMap.put(Operator.NAME_NEQ, 10);
        levelMap.put(Operator.NAME_LE, 10);
        levelMap.put(Operator.NAME_LEQ, 10);
        // +、-、contains、not contains、MemberOf
        levelMap.put(Operator.NAME_ADD, 100);
        levelMap.put(Operator.NAME_MINUS, 100);
        levelMap.put(Operator.NAME_CONTAINS, 100);
        levelMap.put(Operator.NAME_NOT_CONTAINS, 100);
        levelMap.put(Operator.NAME_NEW_FLOW_MEMBER_OF, 100);
        // *、/
        levelMap.put(Operator.NAME_MULTIPLY, 1000);
        levelMap.put(Operator.NAME_DIVIDE, 1000);
        // (、)
    }

    private String expression;

    private List<String> postfixTokens;

    private List<String> tokens;

    /**
     * @param string
     */
    public ConditionExpression(String expression) {
        this.expression = expression;

        tokens = parseTokens(expression);

        postfixTokens = convertPostfixToken(tokens);
    }

    /**
     * 包含不包含
     *
     * @param dyFormData
     * @param containsExpression
     * @return
     */
    private static boolean evaluateContainsExpression(DyFormData dyFormData, String containsExpression) {
        String contains = StringUtils.replace(containsExpression, "${", "");
        contains = StringUtils.replace(contains, "}", "");
        String[] e = StringUtils.split(contains, " ");
        if (e.length >= 3) {
            String fieldName = StringUtils.replace(e[0], "dyform.", "");
            Object fieldValue = dyFormData.getFieldValue(fieldName);

            String compareValue = e[2];
            boolean isNotContains = false;
            if (e.length == 4) {
                compareValue = e[3];
                isNotContains = true;
            }
            if (compareValue.startsWith("'") && compareValue.endsWith("'")) {
                compareValue = compareValue.substring(1);
                compareValue = compareValue.substring(0, compareValue.length() - 1);
            }

            if (fieldValue != null) {
                if (isNotContains) {
                    if (!fieldValue.toString().contains(compareValue)) {
                        return true;
                    }
                } else {
                    if (fieldValue.toString().contains(compareValue)) {
                        return true;
                    }
                }
            } else if (isNotContains && StringUtils.isNotBlank(compareValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转化为后缀表达式
     *
     * @param allTokens
     */
    private static List<String> convertPostfixToken(List<String> allTokens) {
        List<String> tmpPostfixTokens = new ArrayList<String>();
        Deque<String> stack = new ArrayDeque<String>();
        for (int index = 0; index < allTokens.size(); index++) {
            String token = allTokens.get(index);
            Integer code = Operator.getCode(token);
            if (code == null) {
                code = -1;
            }
            switch (code) {
                case 16:
                case 17:
                    // ||、&&
                    compareToken(tmpPostfixTokens, stack, token);
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                    // >、>=、==、!=、<、<=
                    compareToken(tmpPostfixTokens, stack, token);
                    break;
                case 1:
                case 2:
                case 14:
                case 15:
                case 20:
                    // +、-、contains、not contains、MemberOf
                    compareToken(tmpPostfixTokens, stack, token);
                    break;
                case 3:
                case 4:
                    // *、/
                    // 比*、/同级或更高优先级的操作符出栈，直到预到(
                    compareToken(tmpPostfixTokens, stack, token);
                    break;
                case 18:
                    // (
                    stack.push(token);
                    break;
                case 19:
                    // )
                    // 操作符出栈，直到预到(
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        tmpPostfixTokens.add(stack.pop());
                    }
                    stack.pop();
                    break;
                default:
                    tmpPostfixTokens.add(token);
                    break;
            }
        }
        while (!stack.isEmpty()) {
            tmpPostfixTokens.add(stack.pop());
        }

        return tmpPostfixTokens;
    }

    /**
     * 如何描述该方法
     *
     * @param mdList
     * @param tmpPostfixTokens
     * @param stack
     * @param token
     */
    private static void compareToken(List<String> tmpPostfixTokens, Deque<String> stack, String token) {
        if (stack.isEmpty()) {
            stack.push(token);
        } else {
            while (!stack.isEmpty()) {
                String operatorChar = stack.peek();
                if (operatorChar.equals("(")) {
                    stack.push(token);
                    break;
                }
                // 优先级比较
                // 更高优先级的操作符出栈
                Integer level = levelMap.get(operatorChar);
                Integer currentLevel = levelMap.get(token);
                if (level > currentLevel) {
                    // 输出
                    tmpPostfixTokens.add(stack.pop());
                    // 栈空入栈
                    if (stack.isEmpty()) {
                        stack.push(token);
                        break;
                    }
                } else if (level == currentLevel) {
                    // 同级操作符输出
                    tmpPostfixTokens.add(token);
                    break;
                } else {
                    // 入栈
                    stack.push(token);
                    break;
                }
            }
        }
    }

    /**
     * @param text
     * @param index
     * @param c
     * @param stack
     * @return
     */
    private static boolean isMethodStart(String text, int index, char c, Deque<String> stack) {
        return false;
    }

    /**
     * @param text
     * @param index
     * @param c
     * @param stack
     * @return
     */
    private static boolean isMethodEnd(String text, int index, char c, Deque<String> stack) {
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.expression.Expression#evaluate(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Object evaluate(Param param) {
        Context context = param.getContext();
        // 后缀表达式集合操作正确性解析
        Deque<String> postfixStack = new ArrayDeque<String>();
        for (int index = 0; index < postfixTokens.size(); index++) {
            String token = postfixTokens.get(index);
            if (StringUtils.isBlank(token)) {
                continue;
            }

            Integer code = Operator.getCode(token);
            if (code == null) {
                code = -1;
            }

            switch (code) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 20:
                    String s1 = postfixStack.pop();
                    String s2 = postfixStack.pop();
                    String exp = s2 + " " + token + " " + s1;
                    try {
                        Object result = null;
                        if (StringUtils.indexOf(exp, CommandName.NewFlowMemberOf) != -1) {
                            result = evaluateMemberOfExpression(exp, context, param);
                        } else if (exp.indexOf(Operator.NAME_CONTAINS) != -1) {
                            DyFormData dyFormData = (DyFormData) context.getValue("dyFormData");
                            Map<Object, Object> dyformValues = new HashMap<Object, Object>(0);
                            dyformValues = (Map<Object, Object>) context.getValue("dyform");

                            if (null != dyformValues && null != dyformValues.get("opinionText")) {
                                dyFormData.setFieldValue("opinionText", dyformValues.get("opinionText").toString());
                            } else {
                                dyFormData.setFieldValue("opinionText", "");
                            }

                            result = evaluateContainsExpression(dyFormData, exp);
                        } else {
                            result = Ognl.getValue(StringUtils.replace(exp, "'", "\""), context.getOgnlContext());
                        }

                        String uuid = "a" + StringUtils.replace(UUID.randomUUID().toString(), "-", "") + "b";
                        context.setValue(uuid, result);
                        postfixStack.push(uuid);
                    } catch (OgnlException e) {
                        // e.printStackTrace();
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    break;
                default:
                    postfixStack.push(token);
                    break;
            }
        }

        if (postfixStack.size() > 1) {
            throw new RuntimeException("表达式语法有误!");
        }

        return context.getValue(postfixStack.pop());
    }

    /**
     * @param memberOfExpression
     * @param context
     * @param param
     * @return
     */
    private boolean evaluateMemberOfExpression(String memberOfExpression, Context context, Param param) {
        String[] params = StringUtils.split(memberOfExpression, Separator.SPACE.getValue());
        String type = trimQuotation(params[0]);
        String member = trimQuotation(params[2]);
        NewFlowMemberOfConditionExpression expression = new NewFlowMemberOfConditionExpression(type, member);
        return Boolean.TRUE.equals(expression.evaluate(param));
    }

    /**
     * @param string
     * @return
     */
    private String trimQuotation(String string) {
        String retString = string;
        if (StringUtils.startsWith(retString, "'")) {
            retString = StringUtils.substring(retString, 1);
        }
        if (StringUtils.endsWith(retString, "'")) {
            retString = StringUtils.substring(retString, 0, retString.length() - 1);
        }
        return retString;
    }

    /**
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @param tmpExpression
     * @return
     */
    private List<String> parseTokens(String tmpExpression) {
        StringBuilder sb = new StringBuilder();
        Deque<String> tmpStack = new ArrayDeque<String>();
        List<String> tmpTokens = new ArrayList<String>();
        String text = tmpExpression;
        for (int index = 0; index < text.length(); index++) {
            char c = text.charAt(index);
            if (StringUtils.isBlank(c + "")) {
                // 忽略空格
                if (sb.length() == 0) {
                    continue;
                }
                tmpTokens.add(sb.toString());
                sb.setLength(0);

                continue;
            }

            switch (c) {
                case '>':
                case '=':
                case '!':
                case '<':
                    char nextChar = text.charAt(index + 1);
                    String tmpChar = c + "";
                    if (nextChar == '=') {
                        tmpChar += '=';
                        index++;
                    }
                    if (sb.length() == 0) {
                        sb.append(tmpChar);
                    } else {
                        tmpTokens.add(sb.toString());
                        sb.setLength(0);
                        sb.append(tmpChar);
                        if (tmpChar.length() > 1) {
                            tmpTokens.add(sb.toString());
                            sb.setLength(0);
                        }
                    }
                    if (nextChar != ' ' && sb.length() != 0) {
                        tmpTokens.add(sb.toString());
                        sb.setLength(0);
                    }
                    break;
                case '+':
                case '-':
                    char nextChar2 = text.charAt(index + 1);
                    if (sb.length() == 0) {
                        sb.append(c);
                    } else {
                        tmpTokens.add(sb.toString());
                        sb.setLength(0);
                        sb.append(c);
                    }
                    if (nextChar2 != ' ') {
                        tmpTokens.add(sb.toString());
                        sb.setLength(0);
                    }
                    break;
                case '(':
                    if (isMethodStart(text, index, c, tmpStack)) {
                        sb.append(c);
                    } else {
                        tmpTokens.add(c + "");
                    }
                    break;
                case ')':
                    if (isMethodEnd(text, index, c, tmpStack)) {
                        sb.append(c);
                    } else {
                        if (sb.length() > 0) {
                            tmpTokens.add(sb.toString());
                            sb.setLength(0);
                        }
                        tmpTokens.add(c + "");
                    }
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        tmpTokens.add(sb.toString());

        // 数据清理
        List<String> newTokens = new ArrayList<String>();
        for (int index = 0; index < tmpTokens.size(); index++) {
            String token = tmpTokens.get(index);
            if (StringUtils.isBlank(token)) {
                continue;
            }
            if ("not".equals(token)) {
                String nextToken = tmpTokens.get(index + 1);
                if ("contains".equals(nextToken)) {
                    newTokens.add(token + " " + nextToken);
                    index++;
                }
            } else {
                newTokens.add(token);
            }
        }
        return newTokens;
    }

}
