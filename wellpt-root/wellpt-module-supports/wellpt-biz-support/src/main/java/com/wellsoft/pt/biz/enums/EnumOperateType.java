/*
 * @(#)10/17/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/17/22.1	zhulh		10/17/22		Create
 * </pre>
 * @date 10/17/22
 */
public enum EnumOperateType {
    Save("保存", "save"),
    Submit("提交", "submit"),
    StartTimer("启动计时器", "startTimer"),
    PauseTimer("暂停计时器", "pauseTimer"),
    ResumeTimer("恢复计时器", "resumeTimer"),
    Suspend("挂起", "suspend"),
    Resume("恢复", "resume"),
    Cancel("撤销", "cancel"),
    Complete("办结", "complete"),
    Restart("重新开始", "restart");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumOperateType(String name, String value) {
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
     * 根据枚举值获取枚举名称
     *
     * @param operateType
     * @return
     */
    public static String getNameByValue(String operateType) {
        EnumOperateType[] values = values();
        for (EnumOperateType enumOperateType : values) {
            if (StringUtils.equals(enumOperateType.getValue(), operateType)) {
                return enumOperateType.getName();
            }
        }
        return StringUtils.EMPTY;
    }
}
