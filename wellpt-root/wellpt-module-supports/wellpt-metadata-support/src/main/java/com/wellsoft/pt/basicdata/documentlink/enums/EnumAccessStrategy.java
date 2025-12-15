/*
 * @(#)Mar 15, 2022 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.documentlink.enums;

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
 * Mar 15, 2022.1	zhulh		Mar 15, 2022		Create
 * </pre>
 * @date Mar 15, 2022
 */
public enum EnumAccessStrategy {

    None("不检验", "0"), // 不检验
    SourceData("检验源数据", "1"), // 检验源数据
    TargetData("检验目标数据", "2"), // 检验目标数据
    Anyone("任一数据", "3"), // 任一数据
    All("全部", "4");// 全部

    // 成员变量
    private String name;
    private String value;

    /**
     * @param name
     * @param value
     */
    private EnumAccessStrategy(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @param accessStrategy
     * @return
     */
    public static EnumAccessStrategy getByValue(String accessStrategy) {
        if (StringUtils.isBlank(accessStrategy)) {
            return null;
        }
        EnumAccessStrategy[] strategies = values();
        for (EnumAccessStrategy enumAccessStrategy : strategies) {
            if (StringUtils.equals(accessStrategy, enumAccessStrategy.getValue())) {
                return enumAccessStrategy;
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
