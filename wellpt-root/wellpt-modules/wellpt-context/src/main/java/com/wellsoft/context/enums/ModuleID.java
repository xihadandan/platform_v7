/*
 * @(#)2013-3-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-1.1	zhulh		2013-3-1		Create
 * </pre>
 * @date 2013-3-1
 */
public enum ModuleID {
    // 组织机构
    ORG("Organization", "ORG", "001"),
    // 权限管理
    SECURITY("Security", "SECURITY", "002"),
    // 基础数据
    BASIC_DATA("Basic Data", "BASICDATA", "003"),
    // 工作流程
    WORKFLOW("Work flow", "WORKFLOW", "004"),
    // 邮件
    MAIL("Mail", "MAIL", "005"),
    // 文件管理
    FILE("File Manager", "FILE", "006"),
    // 日程
    SCHEDULE("Schedule", "SCHEDULE", "007"),
    // 公文交换
    EXCHANGE("Exchange", "EXCHANGE", "008"),
    // 在线考试
    EXAM("Exam", "EXAM", "009"),
    // 工作计划管理
    WORKTASK("Worktask", "WORKTASK", "010"),
    // 动态表单
    DYTABLE("Dynamic Table", "DYTABLE", "011"),

    // 信息共享
    INFO_SHARED("Information Shared", "INFO_SHARED", "012"),
    // 数据交换
    DATA_EXCHANGE("Data Exchange", "DATA_EXCHANGE", "013"),
    // 商示登记
    SELF_PUBLICITY("SELF PUBLICITY", "SELF_PUBLICITY", "014"),
    // 页面管理
    CMS("CMS", "CMS", "015"),
    // 产品集成开发及管理
    APP("APP", "APP", "016"),
    // 动态表单
    DYFORM("Dynamic Form", "DYFORM", "017"),
    //主题
    THEME("Theme", "THEME", "018"),

    I18N("I18N", "I18N", "019");
    // 名称
    private String name;
    // 值
    private String value;
    // 编号
    private String code;

    private ModuleID(String name, String value, String code) {
        this.name = name;
        this.value = value;
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
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

}
