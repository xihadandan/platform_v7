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
public enum PointerResetTypeEnum {
    ByPeriod("按周期重置", "1"),
    ByVariable("按变量重置", "2");

    private String name;
    private String value;

    private PointerResetTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static PointerResetTypeEnum getByValue(String pointerResetType) {
        PointerResetTypeEnum[] values = PointerResetTypeEnum.values();
        for (PointerResetTypeEnum value : values) {
            if (value.getValue().equals(pointerResetType)) {
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
