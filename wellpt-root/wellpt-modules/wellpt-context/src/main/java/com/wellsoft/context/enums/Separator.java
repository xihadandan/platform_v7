/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.enums;

/**
 * Description: 分隔符枚举类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-15.1	zhulh		2013-1-15		Create
 * </pre>
 * @date 2013-1-15
 */
public enum Separator {
    SEMICOLON("Semicolon", ";"), COMMA("Comma", ","), DOT("Dot", "."), COLON("Colon", ":"), SPACE("space", " "), LINE(
            "Line Separator", System.getProperty("line.separator")), SLASH("slash", "/"), BACKSLASH("backslash", "\\"), AMPERSAND(
            "Ampersand", "&"), ASTERISK("Asterisk", "*"), SINGLE_QUOTES("Single Quotes", "'"), DOUBLE_QUOTES(
            "Double Quotes", "\""), UNDERLINE("Underline", "_"), VERTICAL("Vertical", "|");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private Separator(String name, String value) {
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
