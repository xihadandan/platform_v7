/*
 * @(#)12/6/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/6/24.1	    zhulh		12/6/24		    Create
 * </pre>
 * @date 12/6/24
 */
public enum IdentityReplaceAction {

    Replace("替换", "replace"),
    Add("增加", "add"),
    Delete("删除", "delete");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private IdentityReplaceAction(String name, String value) {
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
     * @param value
     * @return
     */
    public static String getNameByValue(String value) {
        IdentityReplaceAction[] actions = values();
        for (IdentityReplaceAction action : actions) {
            if (StringUtils.equals(action.getValue(), value)) {
                return action.getName();
            }
        }
        return StringUtils.EMPTY;
    }
}
