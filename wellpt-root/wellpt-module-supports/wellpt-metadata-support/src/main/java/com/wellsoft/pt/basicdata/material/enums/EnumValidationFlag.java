/*
 * @(#)5/6/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 5/6/23.1	zhulh		5/6/23		Create
 * </pre>
 * @date 5/6/23
 */
public enum EnumValidationFlag {
    Valid("有效", "1"),
    Invalid("无效", "0");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumValidationFlag(String name, String value) {
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
