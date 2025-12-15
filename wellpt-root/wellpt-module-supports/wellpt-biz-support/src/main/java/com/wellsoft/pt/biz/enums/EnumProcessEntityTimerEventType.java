/*
 * @(#)12/6/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/6/23.1	zhulh		12/6/23		Create
 * </pre>
 * @date 12/6/23
 */
public enum EnumProcessEntityTimerEventType {

    //    Started("计时开始", "started"),
//    Paused("计时暂停", "paused"),
//    Resumed("计时恢复", "resumed"),
    Due("计时到期", "due"),
    Overdue("计时逾期", "overdue");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumProcessEntityTimerEventType(String name, String value) {
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

    /**
     * @param type
     * @return
     */
    public static EnumProcessEntityTimerEventType getByValue(String type) {
        EnumProcessEntityTimerEventType[] values = values();
        for (EnumProcessEntityTimerEventType eventType : values) {
            if (StringUtils.equals(eventType.getValue(), type)) {
                return eventType;
            }
        }
        return null;
    }

}
