/*
 * @(#)12/14/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/14/23.1	zhulh		12/14/23		Create
 * </pre>
 * @date 12/14/23
 */
public enum EnumBizBiEventTriggerType {

    FlowStarted("流程开始", "FLOW_STARTED"),
    FlowEnd("流程办结", "FLOW_END"),
    TaskCreated("流程办结", "TASK_CREATED"),
    TaskCompleted("流程办结", "TASK_COMPLETED"),
    TaskOperation("环节操作", "TASK_OPERATION"),
    TaskBelongTo("环节归属", "TASK_BELONG"),
    DirectionTransition("流向流转", "DIRECTION_TRANSITION");


    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumBizBiEventTriggerType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
