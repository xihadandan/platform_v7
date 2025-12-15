/*
 * @(#)2015-6-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression;

import com.wellsoft.pt.bpm.engine.enums.Participant;
import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.expression.wf.WorkFlowExpression;
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
 * 2015-6-25.1	zhulh		2015-6-25		Create
 * </pre>
 * @date 2015-6-25
 */
public class SetOperationListExpression extends WorkFlowExpression {

    private Participant[] participants = Participant.values();

    private String expression;

    private List<String> postfixTokens;

    private List<String> tokens;

    /**
     * @param expression
     */
    public SetOperationListExpression(String expression) {
        this.expression = expression;

        tokens = parseTokens(expression);

        postfixTokens = convertPostfixToken(tokens);

        parseEvaluate();
    }

    /**
     * 如何描述该方法
     *
     * @param expression
     * @return
     */
    private List<String> parseTokens(String text) {
        StringBuilder sb = new StringBuilder();
        Deque<String> stack = new ArrayDeque<String>();
        List<String> tmpTokens = new ArrayList<String>();
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
                case '∩':
                    sb.append(c);
                    break;
                case '∪':
                    sb.append(c);
                    break;
                case '(':
                    if (isMethodStart(text, index, c, stack)) {
                        sb.append(c);
                    } else {
                        tmpTokens.add(c + "");
                    }
                    break;
                case ')':
                    if (isMethodEnd(text, index, c, stack)) {
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
        if (sb.length() > 0) {
            tmpTokens.add(sb.toString());
        }
        return tmpTokens;
    }

    /**
     * 如何描述该方法
     *
     * @param text
     * @param index
     * @param c
     * @param stack
     * @return
     */
    private boolean isMethodStart(String text, int index, char c, Deque<String> stack) {
        String tmp = text.substring(0, index);
        for (Participant participant : participants) {
            if (tmp.endsWith(participant.name())) {
                stack.push(c + "");
                return true;
            }
        }
        return false;
    }

    /**
     * 如何描述该方法
     *
     * @param text
     * @param index
     * @param c
     * @param stack
     * @return
     */
    private boolean isMethodEnd(String text, int index, char c, Deque<String> stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.peek().equals("(")) {
            stack.pop();
            return true;
        }

        return false;
    }

    /**
     * 转化为后缀表达式
     *
     * @param allTokens
     */
    private List<String> convertPostfixToken(List<String> allTokens) {
        List<String> tmpPostfixTokens = new ArrayList<String>();
        Deque<String> stack = new ArrayDeque<String>();
        for (int index = 0; index < allTokens.size(); index++) {
            String token = allTokens.get(index);
            char c = 0;
            if (token.length() == 1) {
                c = token.charAt(0);
            }
            switch (c) {
                case '∩':
                case '∪':
                    // 比∩、∪同级或更高优先级的操作符出栈，直到预到(
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        tmpPostfixTokens.add(stack.pop());
                    }
                    stack.push(c + "");
                    break;
                case '(':
                    stack.push("(");
                    break;
                case ')':
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
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.expression.Expression#evaluate(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Object evaluate(Param param) {
        // 后缀表达式集合操作
        Deque<String> postfixStack = new ArrayDeque<String>();
        for (int index = 0; index < postfixTokens.size(); index++) {
            String token = postfixTokens.get(index);
            char c = 0;
            if (token.length() == 1) {
                c = token.charAt(0);
            }
            if (StringUtils.isBlank(c + "")) {
                continue;
            }

            switch (c) {
                case '∩':
                case '∪':
                    String s1 = postfixStack.pop();
                    String s2 = postfixStack.pop();
                    Expression rightExp = ExpressionParserFactory.getParser().parse(s1);
                    Expression leftExp = ExpressionParserFactory.getParser().parse(s2);

                    SetOperationExpression setOperationExpression1 = new SetOperationExpression(leftExp, c + "", rightExp);
                    Object s3 = setOperationExpression1.evaluate(param);

                    String uuid = "a" + StringUtils.replace(UUID.randomUUID().toString(), "-", "") + "b";
                    param.getContext().setValue(uuid, s3);
                    postfixStack.push(uuid);
                    break;
                default:
                    postfixStack.push(token);
                    break;
            }
        }

        return param.getContext().getValue(postfixStack.pop());
    }

    /**
     * 解析取值语法正确性
     *
     * @return
     */
    public Object parseEvaluate() {
        // 后缀表达式集合操作正确性解析
        Deque<String> postfixStack = new ArrayDeque<String>();
        for (int index = 0; index < postfixTokens.size(); index++) {
            String token = postfixTokens.get(index);
            char c = 0;
            if (token.length() == 1) {
                c = token.charAt(0);
            }
            if (StringUtils.isBlank(c + "")) {
                continue;
            }

            switch (c) {
                case '∩':
                case '∪':
                    String s1 = postfixStack.pop();
                    String s2 = postfixStack.pop();
                    ExpressionParserFactory.getParser().parse(s1);
                    ExpressionParserFactory.getParser().parse(s2);

                    String uuid = UUID.randomUUID().toString();
                    postfixStack.push(uuid);
                    break;
                default:
                    postfixStack.push(token);
                    break;
            }
        }

        if (postfixStack.size() > 1) {
            throw new RuntimeException("表达式语法有误!");
        }

        return postfixStack.pop();
    }

}
