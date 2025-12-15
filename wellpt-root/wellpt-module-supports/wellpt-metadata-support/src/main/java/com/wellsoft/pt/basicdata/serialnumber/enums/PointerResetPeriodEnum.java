/*
 * @(#)7/12/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.enums;

/**
 * Description: 指针重置类型
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/12/22.1	zhulh		7/12/22		Create
 * </pre>
 * @date 7/12/22
 */
public enum PointerResetPeriodEnum {
    ByYear("按年重置", "10"),
    ByMonth("按月重置", "20"),
    ByWeek("按周重置", "30"),
    ByDay("按日重置", "40");

    private String name;
    private String value;

    private PointerResetPeriodEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static PointerResetPeriodEnum getByValue(String pointerResetRule) {
        PointerResetPeriodEnum[] values = PointerResetPeriodEnum.values();
        for (PointerResetPeriodEnum value : values) {
            if (value.getValue().equals(pointerResetRule)) {
                return value;
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
