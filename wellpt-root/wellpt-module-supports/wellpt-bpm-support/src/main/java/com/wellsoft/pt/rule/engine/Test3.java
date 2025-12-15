/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

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
public class Test3 {

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
        String text = "LeaderOf(Unit(J0000000566,U0010000036)) ∩ (Unit(J0000000566,U0010000036) ∪ FormField(tdr))";
        String text1 = "3 + (5 - 4)";

        StringBuilder sb = new StringBuilder();
        Deque<String> stack = new ArrayDeque<String>();
        Deque<String> tokenStack = new ArrayDeque<String>();
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
    }

    /**
     * @param text
     * @param index
     * @param c
     * @param stack
     * @return
     */
    private static boolean isMethodStart(String text, int index, char c, Deque<String> stack) {
        String tmp = text.substring(0, index);
        if (tmp.endsWith("LeaderOf") || tmp.endsWith("Unit") || tmp.endsWith("FormField")) {
            stack.push(c + "");
            return true;
        }
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
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.peek().equals("(")) {
            stack.pop();
            return true;
        }

        return false;
    }

}
