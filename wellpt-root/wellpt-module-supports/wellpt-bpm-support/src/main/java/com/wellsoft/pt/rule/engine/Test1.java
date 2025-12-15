/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine;

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
public class Test1 {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        // 1 + 2 > 3 || 1 + 2 < 1
        // ||、&&
        // >、>=、==、!=、<、<=
        // +、-、contains	not、contains
        // *、/
        // (、)
        // String text = "1 + 2 > 3 || 1 + 2 < 1";
        String text = "a + b * c + (d * e + f) * g";
        System.out.println(text);
        Deque<String> stack = new ArrayDeque<String>();
        for (int index = 0; index < text.length(); index++) {
            char c = text.charAt(index);
            switch (c) {
                case '+':
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        System.out.print(stack.pop());
                    }
                    stack.push("+");
                    break;
                case '*':
                    stack.push("*");
                    break;
                case '(':
                    stack.push("(");
                    break;
                case ')':
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        System.out.print(stack.pop());
                    }
                    stack.pop();
                    break;
                default:
                    System.out.print(c);
                    break;
            }
        }
        while (!stack.isEmpty()) {
            System.out.print(stack.pop());
        }
    }
}
