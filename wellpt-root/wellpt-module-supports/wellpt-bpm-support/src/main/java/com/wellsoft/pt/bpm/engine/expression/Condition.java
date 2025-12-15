/*
 * @(#)2013-4-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.expression;

import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.node.Node;

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
public interface Condition {
    // 大于
    public static final String GT = ">";
    // 大于等于
    public static final String GEQ = ">=";
    // 小于
    public static final String LT = "<";
    // 小于等于
    public static final String LEQ = "<=";
    // 等于
    public static final String EQ = "==";
    // 不等于
    public static final String NEQ = "!=";
    // 包含
    public static final String LIKE = "like";
    // 不包含
    public static final String NOLIKE = "notlike";

    // 职级职等条件名称
    // 申请人职等
    public static final String APPLY_USER_GRADE = "A1";
    // 申请人职级
    public static final String APPLY_DUTY = "A2";
    // 当前办理人职等
    public static final String CURRENT_USER_GRADE = "A3";
    // 当前办理人职级
    public static final String CURRENT_USER_DUTY = "A4";
    // 职级职等条件
    // 等于
    public static final String EQUAL = "equal";
    // 不等于
    public static final String NOT_EQUAL = "notEqual";
    // 高于
    public static final String OVER_TOP = "overTop";
    // 高于等于
    public static final String OVER_TOP_EQUAL = "overTopEqual";
    // 低于
    public static final String LOWER_THAN = "lowerThan";
    // 低于等于
    public static final String LOWER_THAN_EQUAL = "lowerThanEqual";
    // 值类型
    // 常量
    public static final String VALUE_TYPE_CONSTANT = "1";
    // 表单字段
    public static final String VALUE_TYPE_FORM_FIELD = "2";
    // 组织
    public static final String VALUE_TYPE_ORG = "1";

    String evaluate(Token token, Node to);
}
