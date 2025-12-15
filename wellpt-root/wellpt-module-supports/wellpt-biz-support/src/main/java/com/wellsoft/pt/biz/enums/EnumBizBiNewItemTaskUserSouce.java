/*
 * @(#)12/20/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.enums;

/**
 * Description: 办理人来源，1、按流程定义配置，2、现在指定
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/20/22.1	zhulh		12/20/22		Create
 * </pre>
 * @date 12/20/22
 */
public enum EnumBizBiNewItemTaskUserSouce {
    WorkflowConfig("按流程定义配置", "1"),
    Custom("现在指定", "2");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumBizBiNewItemTaskUserSouce(String name, String value) {
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
