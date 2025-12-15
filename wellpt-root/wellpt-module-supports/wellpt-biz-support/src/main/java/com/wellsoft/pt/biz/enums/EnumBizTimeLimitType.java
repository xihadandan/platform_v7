/*
 * @(#)10/19/22 V1.0
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
 * 10/19/22.1	zhulh		10/19/22		Create
 * </pre>
 * @date 10/19/22
 */
public enum EnumBizTimeLimitType {
    WorkingDay("工作日", 1),
    NaturalDay("自然日", 2);

    // 成员变量
    private String name;
    private Integer value;

    // 构造方法
    private EnumBizTimeLimitType(String name, Integer value) {
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
    public Integer getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    public static String getNameByValue(Integer timeLimitType) {
        EnumBizTimeLimitType[] values = values();
        for (EnumBizTimeLimitType enumBizTimeLimitType : values) {
            if (enumBizTimeLimitType.getValue().equals(timeLimitType)) {
                return enumBizTimeLimitType.getName();
            }
        }
        return StringUtils.EMPTY;
    }
}
