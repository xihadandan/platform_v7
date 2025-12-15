/*
 * @(#)12/20/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.enums;

/**
 * Description: 办理人类型，1、组织机构、4、源流程历史环节办理人、8、人员选项
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
public enum EnumBizBiNewItemTaskUserType {
    Org("组织机构", "1"),
    TaskHistory("源流程历史环节办理人", "4"),
    UserOption("人员选项", "8");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumBizBiNewItemTaskUserType(String name, String value) {
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
     * @param userTypeValue
     * @return
     */
    public static EnumBizBiNewItemTaskUserType getByValue(String userTypeValue) {
        EnumBizBiNewItemTaskUserType[] userTypes = values();
        for (EnumBizBiNewItemTaskUserType userType : userTypes) {
            if (userType.getValue().equals(userTypeValue)) {
                return userType;
            }
        }
        return null;
    }
}
