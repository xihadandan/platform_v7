/*
 * @(#)2018年6月7日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.enums;

/**
 * Description: 送审批内容
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月7日.1	zhulh		2018年6月7日		Create
 * </pre>
 * @date 2018年6月7日
 */
public enum WorkFlowApproveContentEnum {
    Source("源文送审批", "1"), // 源文送审批
    CopySource("复制源文送审批", "2"), // 复制源文送审批
    Link("原文作为链接送审批", "3"); // 原文作为链接送审批

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private WorkFlowApproveContentEnum(String name, String value) {
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
