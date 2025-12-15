/*
 * @(#)2014-9-29 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-29.1	zhulh		2014-9-29		Create
 * </pre>
 * @date 2014-9-29
 */
public class WorkFlowTodoType {

    // 办理类型工作待办(1)
    public static final Integer Submit = 1;

    // 办理类型会签待办(2)
    public static final Integer CounterSign = 2;

    // 办理类型转办待办(3)
    public static final Integer Transfer = 3;

    // 办理类型移交人员待办(4)
    public static final Integer HandOver = 4;

    // 办理类型委托待办(5)
    public static final Integer Delegation = 5;

    // 办理类型加签待办(6)
    public static final Integer AddSign = 6;

    // 补审补办
    public static final Integer Supplement = 7;
}
