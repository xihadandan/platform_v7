/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayDeque;
import java.util.Deque;

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
public class Test2 {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        //		String text = "A ∩ (B ∪ C)";
        // 集合并、交操作的中缀表达式转化为后缀表达式
        // String text = "A ∩ ((B ∪ C) ∪  (D ∩ E))";
        // String text = "1 ∩ ((2 ∪ 3) ∪  (4 ∩ 5))";
        String text = "3 ∩ (5 ∪ 4)";
        String text1 = "3 + (5 - 4)";
        Deque<String> stack = new ArrayDeque<String>();
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < text.length(); index++) {
            char c = text.charAt(index);
            switch (c) {
                case '∩':
                case '∪':
                    // 比∩、∪同级或更高优先级的操作符出栈，直到预到(
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        sb.append(stack.pop());
                    }
                    stack.push(c + "");
                    break;
                case '(':
                    stack.push("(");
                    break;
                case ')':
                    // 操作符出栈，直到预到(
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        sb.append(stack.pop());
                    }
                    stack.pop();
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        while (!stack.isEmpty()) {
            sb.append(stack.pop());
        }

        System.out.println(sb.toString());
        System.out.println();

        // 后缀表达式集合操作
        String postfixExpression = sb.toString();
        Deque<String> postfixStack = new ArrayDeque<String>();
        for (int index = 0; index < postfixExpression.length(); index++) {
            System.out.print(postfixExpression.charAt(index));
            char c = postfixExpression.charAt(index);
            if (StringUtils.isBlank(c + "")) {
                continue;
            }

            switch (c) {
                case '∩':
                    String s1 = postfixStack.pop();
                    String s2 = postfixStack.pop();
                    String s3 = (Integer.valueOf(s2) + Integer.valueOf(s1)) + "";
                    postfixStack.push(s3);
                    break;
                case '∪':
                    String s4 = postfixStack.pop();
                    String s5 = postfixStack.pop();
                    String s6 = (Integer.valueOf(s5) - Integer.valueOf(s4)) + "";
                    postfixStack.push(s6);
                    break;
                default:
                    postfixStack.push(c + "");
                    break;
            }
        }

        System.out.println(postfixStack);
    }
}
