/*
 * @(#)8/13/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.enums;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/13/24.1	    zhulh		8/13/24		    Create
 * </pre>
 * @date 8/13/24
 */
public enum EnumDefinitionTemplateType {
    ProcessForm("业务流程表单配置模板", "10"),
    NodeForm("过程节点表单配置模板", "20"),
    ItemForm("事项表单配置模板", "30"),
    Workflow("事项集成工作流配置模板", "40"),
    ItemDefinition("事项配置模板", "50"),
    NodeDefinition("阶段配置模板", "60");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumDefinitionTemplateType(String name, String value) {
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
