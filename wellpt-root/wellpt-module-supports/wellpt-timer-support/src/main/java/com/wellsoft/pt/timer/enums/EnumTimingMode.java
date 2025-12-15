/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 计时方式枚举类
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
public enum EnumTimingMode {

    WORKING_DAY("工作日(工作时间)", "3"), // 工作日(工作时间)
    WORKING_HOUR("工作小时(工作时间)", "2"), // 工作小时(工作时间)
    WORKING_MINUTE("工作分钟(工作时间)", "1"), // 工作分钟(工作时间)
    WORKING_DAY_24("工作日(24小时制不含非工作日)", "13"), // 工作日(24小时制不含非工作日)
    WORKING_HOUR_24("工作小时(24小时制不含非工作日)", "12"), // 工作小时(24小时制不含非工作日)
    WORKING_MINUTE_24("工作分钟(24小时制不含非工作日)", "11"), // 工作分钟(24小时制不含非工作日)
    DAY("天(自然日)", "86400"), // 天(自然日)
    HOUR("小时(自然日)", "3600"), // 小时(自然日)
    MINUTE("分钟(自然日)", "60"), // 分钟(自然日)
    CUSTOM("从表单字段读取", "-1"); // 从表单字段读取

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private EnumTimingMode(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @param timingMode
     * @return
     */
    public static EnumTimingMode getByValue(String timingMode) {
        EnumTimingMode[] values = values();
        for (EnumTimingMode enumTimingMode : values) {
            if (StringUtils.equals(enumTimingMode.getValue(), timingMode)) {
                return enumTimingMode;
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
