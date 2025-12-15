/*
 * @(#)10/13/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 业务事项办件状态
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/13/22.1	zhulh		10/13/22		Create
 * </pre>
 * @date 10/13/22
 */
public enum EnumBizProcessItemState {

    Created("创建", "00"),
    Running("办理中", "10"),
    Suspended("挂起", "20"),
    Completed("完成", "30"),
    Cancelled("撤销", "40");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumBizProcessItemState(String name, String value) {
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
    public static EnumBizProcessItemState getByValue(String value) {
        EnumBizProcessItemState[] values = values();
        for (EnumBizProcessItemState itemState : values) {
            if (StringUtils.equals(itemState.getValue(), value)) {
                return itemState;
            }
        }
        return null;
    }

}
