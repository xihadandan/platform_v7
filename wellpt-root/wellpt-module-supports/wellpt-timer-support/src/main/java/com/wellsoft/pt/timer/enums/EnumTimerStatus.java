/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月9日.1	zhulh		2021年4月9日		Create
 * </pre>
 * @date 2021年4月9日
 */
public enum EnumTimerStatus {

    READY("未启动", 0), // 未启动
    STARTED("已启动", 1), // 已启动
    PASUE("暂停", 2), // 暂停
    STOP("结束", 3); // 结束

    // 成员变量
    private String name;
    private int value;

    // 构造方法
    private EnumTimerStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @param status
     * @return
     */
    public static EnumTimerStatus getByValue(Integer status) {
        EnumTimerStatus[] values = values();
        for (EnumTimerStatus enumTimerStatus : values) {
            if (Integer.valueOf(enumTimerStatus.getValue()).equals(status)) {
                return enumTimerStatus;
            }
        }
        return null;
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
    public int getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(int value) {
        this.value = value;
    }

}
