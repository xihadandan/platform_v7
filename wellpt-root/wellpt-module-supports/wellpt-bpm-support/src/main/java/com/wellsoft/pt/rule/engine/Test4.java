/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine;

import com.wellsoft.pt.rule.engine.suport.Operator;
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
 * 2015-6-24.1	zhulh		2015-6-24		Create
 * </pre>
 * @date 2015-6-24
 */
public class Test4 {
    // 中缀表达式转化为后缀表达式
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
        // +、-、contains、not contains
        levelMap.put(Operator.NAME_ADD, 100);
        levelMap.put(Operator.NAME_MINUS, 100);
        levelMap.put(Operator.NAME_CONTAINS, 100);
        levelMap.put(Operator.NAME_NOT_CONTAINS, 100);
        // *、/
        levelMap.put(Operator.NAME_MULTIPLY, 1000);
        levelMap.put(Operator.NAME_DIVIDE, 1000);
        // (、)
    }

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        // String text = "1 + 13 != 23 || ${fromTaskId} == 'T160' && ${dyform.bm} not contains '光源营销中心' && (${dyform.zjxj} contains 'M5' || ${dyform.zjxj} contains 'M8' || ${dyform.zjxj} contains 'M9' || ${dyform.zjxj} contains 'M10' || ${dyform.zjxj} contains 'M11' || ${dyform.zjxj} contains 'M12' || ${dyform.zjxj} contains 'M13' || ${dyform.zjxj} contains 'M14' || ${dyform.zjxj} contains 'M15')";
        //String text = "a + b * c + (d * e + f) * g";
        String text = "5+6>8 && fromTaskId=='T160'";
        StringBuilder sb = new StringBuilder();
        Deque<String> stack = new ArrayDeque<String>();
        List<String> tokens = new ArrayList<String>();
        for (int index = 0; index < text.length(); index++) {
            char c = text.charAt(index);
            if (StringUtils.isBlank(c + "")) {
                // 忽略空格
                if (sb.length() == 0) {
                    continue;
                }
                tokens.add(sb.toString());
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
                        tokens.add(sb.toString());
                        sb.setLength(0);
                        sb.append(tmpChar);
                        if (tmpChar.length() > 1) {
                            tokens.add(sb.toString());
                            sb.setLength(0);
                        }
                    }
                    if (nextChar != ' ' && sb.length() != 0) {
                        tokens.add(sb.toString());
                        sb.setLength(0);
                    }
                    break;
                case '+':
                case '-':
                    char nextChar2 = text.charAt(index + 1);
                    if (sb.length() == 0) {
                        sb.append(c);
                    } else {
                        tokens.add(sb.toString());
                        sb.setLength(0);
                        sb.append(c);
                    }
                    if (nextChar2 != ' ') {
                        tokens.add(sb.toString());
                        sb.setLength(0);
                    }
                    break;
                case '(':
                    if (isMethodStart(text, index, c, stack)) {
                        sb.append(c);
                    } else {
                        tokens.add(c + "");
                    }
                    break;
                case ')':
                    if (isMethodEnd(text, index, c, stack)) {
                        sb.append(c);
                    } else {
                        if (sb.length() > 0) {
                            tokens.add(sb.toString());
                            sb.setLength(0);
                        }
                        tokens.add(c + "");
                    }
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        tokens.add(sb.toString());
        System.out.println(tokens);

        // 数据清理
        List<String> newTokens = new ArrayList<String>();
        for (int index = 0; index < tokens.size(); index++) {
            String token = tokens.get(index);
            if (StringUtils.isBlank(token)) {
                continue;
            }
            if ("not".equals(token)) {
                String nextToken = tokens.get(index + 1);
                if ("contains".equals(nextToken)) {
                    newTokens.add(token + " " + nextToken);
                    index++;
                }
            } else {
                newTokens.add(token);
            }
        }
        System.out.println(newTokens);

        // 中缀表达式转化为后缀表达式
        List<String> postfixToken = convertPostfixToken(newTokens);
        System.out.println(postfixToken);
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
                    // +、-、contains、not contains
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

}
