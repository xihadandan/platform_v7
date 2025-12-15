/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-17.1	zhulh		2013-1-17		Create
 * </pre>
 * @date 2013-1-17
 */
public enum ResourceType {
    MENU("菜单", "MENU"), BUTTON("按钮", "BUTTON"), METHOD("方法", "METHOD");

    // 成员变量
    private String name;
    private String value;

    /**
     * @param name
     * @param value
     */
    private ResourceType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static String nameByValue(String value) {
        ResourceType[] types = ResourceType.values();
        for (ResourceType t : types) {
            if (t.getValue().equals(value)) {
                return t.getName();
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
