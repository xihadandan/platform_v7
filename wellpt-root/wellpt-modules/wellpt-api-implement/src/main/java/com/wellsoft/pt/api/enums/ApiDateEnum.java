/*
 * @(#)2018年9月30日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.enums;

/**
 * Description: 如何描述该类
 *
 * @author linxr
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月30日.1	linxr		2018年9月30日		Create
 * </pre>
 * @date 2018年9月30日
 */
public enum ApiDateEnum {
    BEFORE("before"), AFTER("after");

    private String name;

    ApiDateEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
