/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月9日.1	zhulh		2021年4月9日		Create
 * </pre>
 * @date 2021年4月9日
 */
public enum EnumTimingState {

    NORMAL("正常", 0), // 正常
    ALARM("预警", 1), // 预警
    DUE("到期", 2), // 到期
    OVER_DUE("逾期", 3); // 逾期

    // 成员变量
    private String name;
    private int value;

    // 构造方法
    private EnumTimingState(String name, int value) {
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
    public int getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(int value) {
        this.value = value;
    }

}
