/*
 * @(#)2021年4月11日 V1.0
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
 * 2021年4月11日.1	zhulh		2021年4月11日		Create
 * </pre>
 * @date 2021年4月11日
 */
public enum EnumTimerLogType {

    START("开始", "START"), // 开始
    PAUSE("暂停", "PAUSE"), // 暂停
    RESUME("恢复", "RESUME"), // 恢复
    END("结束", "END"), // 结束
    RESTART("重新开始", "RESTART"), // 重新开始
    ALARM_DOING("预警处理", "ALARM_DOING"), // 预警处理
    DUE_DOING("到期处理", "DUE_DOING"), // 到期处理
    OVER_DUE_DOING("逾期处理", "OVER_DUE_DOING"), // 逾期处理
    FORCE_STOP_ALARM_DOING("强制停止预警处理", "FORCE_STOP_ALARM_DOING"), // 强制停止预警处理
    FORCE_STOP_DUE_DOING("强制停止到期处理", "FORCE_STOP_DUE_DOING"), // 强制停止到期处理
    FORCE_STOP_OVER_DUE_DOING("强制停止逾期处理", "FORCE_STOP_OVER_DUE_DOING"), // 强制停止逾期处理
    INFO("信息", "INFO"), // 信息
    ERROR("错误", "ERROR"); // 错误

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumTimerLogType(String name, String value) {
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
