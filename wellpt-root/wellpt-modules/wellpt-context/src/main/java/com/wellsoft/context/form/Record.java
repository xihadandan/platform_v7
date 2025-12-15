/*
 * @(#)2013-5-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.form;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-2.1	zhulh		2013-5-2		Create
 * </pre>
 * @date 2013-5-2
 */
public class Record {
    // 不替换
    public static final String WAY_NO_REPLACE = "1";
    // 替换原值
    public static final String WAY_REPLACE = "2";
    // 附加
    public static final String WAY_APPEND = "3";

    // 忽略空意见
    public static final String IGNORE_EMPTY_VALUE_TRUE = "1";

    // 信息格式名称
    private String name;

    // 动态表单字段
    private String field;

    // 记录方式
    private String way;

    // 组装器默认按时间先后记录
    private String assembler;

    // 忽略空意见
    private String ignoreEmpty;

    // 信息格式ID
    private String value;

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
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field 要设置的field
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the way
     */
    public String getWay() {
        return way;
    }

    /**
     * @param way 要设置的way
     */
    public void setWay(String way) {
        this.way = way;
    }

    /**
     * @return the assembler
     */
    public String getAssembler() {
        return assembler;
    }

    /**
     * @param assembler 要设置的assembler
     */
    public void setAssembler(String assembler) {
        this.assembler = assembler;
    }

    /**
     * @return the ignoreEmpty
     */
    public String getIgnoreEmpty() {
        return ignoreEmpty;
    }

    /**
     * @param ignoreEmpty 要设置的ignoreEmpty
     */
    public void setIgnoreEmpty(String ignoreEmpty) {
        this.ignoreEmpty = ignoreEmpty;
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
