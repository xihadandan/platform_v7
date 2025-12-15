/*
 * @(#)2015-6-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.suport;

import org.apache.commons.lang.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
public class Operator {

    public static final String NAME_ADD = "+";
    public static final int CODE_ADD = 1;

    public static final String NAME_MINUS = "-";
    public static final int CODE_MINUS = 2;

    public static final String NAME_MULTIPLY = "*";
    public static final int CODE_MULTIPLY = 3;

    public static final String NAME_DIVIDE = "/";
    public static final int CODE_DIVIDE = 4;

    public static final String NAME_UNION = "∪";
    public static final int CODE_UNION = 5;

    public static final String NAME_INTERSECTION = "∩";
    public static final int CODE_INTERSECTION = 6;

    public static final String NAME_ASSIGN = "=";
    public static final int CODE_ASSIGN = 7;

    public static final String NAME_GT = ">";
    public static final int CODE_GT = 8;

    public static final String NAME_GE = ">=";
    public static final int CODE_GE = 9;

    public static final String NAME_EQ = "==";
    public static final int CODE_EQ = 10;

    public static final String NAME_NEQ = "!=";
    public static final int CODE_NEQ = 11;

    public static final String NAME_LE = "<";
    public static final int CODE_LE = 12;

    public static final String NAME_LEQ = "<=";
    public static final int CODE_LEQ = 13;

    public static final String NAME_CONTAINS = "contains";
    public static final int CODE_CONTAINS = 14;

    public static final String NAME_NOT_CONTAINS = "not contains";
    public static final int CODE_NOT_CONTAINS = 15;

    public static final String NAME_AND = "&&";
    public static final int CODE_AND = 16;

    public static final String NAME_OR = "||";
    public static final int CODE_OR = 17;

    public static final String NAME_LEFT_BRACKET = "(";
    public static final int CODE_LEFT_BRACKET = 18;

    public static final String NAME_RIGHT_BRACKET = ")";
    public static final int CODE_RIGHT_BRACKET = 19;

    public static final String NAME_NEW_FLOW_MEMBER_OF = "NewFlowMemberOf";
    public static final int CODE_NEW_FLOW_MEMBER_OF = 20;

    private static Map<String, Integer> operatorMap = new HashMap<String, Integer>();
    private static Map<Integer, String> codeOperatorMap = new HashMap<>();

    static {
        operatorMap.put(NAME_ADD, CODE_ADD);
        operatorMap.put(NAME_MINUS, CODE_MINUS);
        operatorMap.put(NAME_MULTIPLY, CODE_MULTIPLY);
        operatorMap.put(NAME_DIVIDE, CODE_DIVIDE);
        operatorMap.put(NAME_UNION, CODE_UNION);
        operatorMap.put(NAME_INTERSECTION, CODE_INTERSECTION);
        operatorMap.put(NAME_ASSIGN, CODE_ASSIGN);
        operatorMap.put(NAME_GT, CODE_GT);
        operatorMap.put(NAME_GE, CODE_GE);
        operatorMap.put(NAME_EQ, CODE_EQ);
        operatorMap.put(NAME_NEQ, CODE_NEQ);
        operatorMap.put(NAME_LE, CODE_LE);
        operatorMap.put(NAME_LEQ, CODE_LEQ);
        operatorMap.put(NAME_CONTAINS, CODE_CONTAINS);
        operatorMap.put(NAME_NOT_CONTAINS, CODE_NOT_CONTAINS);
        operatorMap.put(NAME_AND, CODE_AND);
        operatorMap.put(NAME_OR, CODE_OR);
        operatorMap.put(NAME_LEFT_BRACKET, CODE_LEFT_BRACKET);
        operatorMap.put(NAME_RIGHT_BRACKET, CODE_RIGHT_BRACKET);
        operatorMap.put(NAME_NEW_FLOW_MEMBER_OF, CODE_NEW_FLOW_MEMBER_OF);

        Set<String> keys = operatorMap.keySet();
        for (String k : keys) {
            codeOperatorMap.put(operatorMap.get(k), k);
        }

    }

    public static Integer getCode(String name) {
        return operatorMap.get(name);
    }

    public static String getName(String code) {
        if (!NumberUtils.isDigits(code)) {
            return null;
        }
        return codeOperatorMap.get(Integer.valueOf(code));
    }

}
