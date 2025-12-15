/*
 * @(#)11/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/21/22.1	zhulh		11/21/22		Create
 * </pre>
 * @date 11/21/22
 */
public enum EnumFlowBizStateValueType {
    FixedValue("固定值", "constant"),
    Freemarker("freemarker表达式", "freemarker"),
    Groovy("groovy脚本", "groovy");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumFlowBizStateValueType(String name, String value) {
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
     * @param valueType
     * @return
     */
    public static EnumFlowBizStateValueType getByValue(String valueType) {
        EnumFlowBizStateValueType[] values = values();
        for (EnumFlowBizStateValueType type : values) {
            if (type.getValue().equals(valueType)) {
                return type;
            }
        }
        return null;
    }

}
