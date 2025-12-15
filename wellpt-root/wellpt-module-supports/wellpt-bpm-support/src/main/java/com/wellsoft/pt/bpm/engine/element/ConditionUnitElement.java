/*
 * @(#)4/30/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 4/30/24.1	zhulh		4/30/24		Create
 * </pre>
 * @date 4/30/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConditionUnitElement extends UnitElement {
    private static final long serialVersionUID = -1603635090880261442L;

    // 附加存储的数据
    private String data;

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(String data) {
        this.data = data;
    }
}
