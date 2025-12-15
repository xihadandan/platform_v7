/*
 * @(#)12/12/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 事项流元素类型
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/12/23.1	zhulh		12/12/23		Create
 * </pre>
 * @date 12/12/23
 */
public enum EnumItemFlowElement {
    StartNode("开始节点", "start"),
    Edge("边", "edge"),
    Gateway("网关", "gateway"),
    ItemNode("事项节点", "item"),
    EndNode("结束节点", "end");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumItemFlowElement(String name, String value) {
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
    public static EnumItemFlowElement getByValue(String value) {
        EnumItemFlowElement[] values = values();
        for (EnumItemFlowElement flowElement : values) {
            if (StringUtils.equals(flowElement.getValue(), value)) {
                return flowElement;
            }
        }
        return null;
    }
}
