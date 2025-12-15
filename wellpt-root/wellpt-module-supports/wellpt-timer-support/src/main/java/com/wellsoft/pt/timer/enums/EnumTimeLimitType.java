/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 时限类型枚举类
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
public enum EnumTimeLimitType {

    NUMBER("固定时限", "10"), // 设置固定时限
    DATE("固定截止时间", "20"), // 设置截止时间
    CUSTOM_NUMBER("动态时限", "30"), // 动态时限
    CUSTOM_DATE("动态截止时间", "40"); // 动态截止时间

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumTimeLimitType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @param timeLimitType
     * @return
     */
    public static EnumTimeLimitType getByValue(String timeLimitType) {
        EnumTimeLimitType[] values = values();
        for (EnumTimeLimitType enumTimeLimitType : values) {
            if (StringUtils.equals(enumTimeLimitType.getValue(), timeLimitType)) {
                return enumTimeLimitType;
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
