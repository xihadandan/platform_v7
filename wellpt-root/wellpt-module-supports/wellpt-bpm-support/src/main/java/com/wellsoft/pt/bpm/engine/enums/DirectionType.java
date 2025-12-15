/*
 * @(#)9/9/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.enums;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 9/9/24.1	    zhulh		9/9/24		    Create
 * </pre>
 * @date 9/9/24
 */
public enum DirectionType {

    TaskToTask("环节到环节", "1"),
    GatewayToTask("网关到环节", "2"),
    ToGateway("指向网关", "3");

    private String name;
    private String value;

    /**
     * @param name
     * @param value
     */
    private DirectionType(String name, String value) {
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
