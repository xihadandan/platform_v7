/*
 * @(#)2021年6月1日 V1.0
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
 * 2021年6月1日.1	zhulh		2021年6月1日		Create
 * </pre>
 * @date 2021年6月1日
 */
public enum EnumPeriodType {

    AllYear("全年", "1"), // 全年
    TimePeriod("指定时间周期", "2");// 指定时间周期

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumPeriodType(String name, String value) {
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
