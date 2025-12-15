/*
 * @(#)12/7/23 V1.0
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
 * 12/7/23.1	zhulh		12/7/23		Create
 * </pre>
 * @date 12/7/23
 */
public enum EnumStateTriggerType {

    PROCESS_STATE_CHANGED("业务流程状态变更", "PROCESS_STATE_CHANGED"),
    PROCESS_NODE_STATE_CHANGED("过程节点状态变更", "PROCESS_NODE_STATE_CHANGED"),
    ITEM_STATE_CHANGED("业务事项状态变更", "ITEM_STATE_CHANGED"),
    ITEM_EVENT_PUBLISHED("业务事项事件发生", "ITEM_EVENT_PUBLISHED"),
    PROCESS_ENTITY_TIMER_EVENT_PUBLISHED("业务主体状态计时事件发生", "PROCESS_ENTITY_TIMER_EVENT_PUBLISHED");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumStateTriggerType(String name, String value) {
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
