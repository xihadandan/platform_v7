/*
 * @(#)2021年6月3日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.enums;

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
 * 2021年6月3日.1	zhulh		2021年6月3日		Create
 * </pre>
 * @date 2021年6月3日
 */
public enum EnumTimeLimitUnit {

    Day("天", "1"), // 天
    Hour("小时", "2"), // 小时
    Minute("分钟", "3");// 分钟

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumTimeLimitUnit(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @param value
     * @return
     */
    public static EnumTimeLimitUnit getByValue(String value) {
        EnumTimeLimitUnit[] values = values();
        for (EnumTimeLimitUnit enumTimeLimitUnit : values) {
            if (StringUtils.equals(enumTimeLimitUnit.getValue(), value)) {
                return enumTimeLimitUnit;
            }
        }
        return null;
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
