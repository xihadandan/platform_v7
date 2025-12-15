/*
 * @(#)2021年7月14日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
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
 * 2021年7月14日.1	zhulh		2021年7月14日		Create
 * </pre>
 * @date 2021年7月14日
 */
public enum EnumFlowDefinitionDeleteType {

    logicalDelete("逻辑删除", 1), // 逻辑删除
    physicalDelete("物理删除", 2);// 物理删除

    // 成员变量
    private String name;
    private Integer code;

    // 构造方法
    private EnumFlowDefinitionDeleteType(String name, Integer code) {
        this.name = name;
        this.code = code;
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
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

}
