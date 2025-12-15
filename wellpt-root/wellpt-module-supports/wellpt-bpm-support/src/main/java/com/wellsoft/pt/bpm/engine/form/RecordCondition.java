/*
 * @(#)2021年9月25日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.form;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年9月25日.1	zhulh		2021年9月25日		Create
 * </pre>
 * @date 2021年9月25日
 */
public class RecordCondition extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2276302163711072378L;

    private Integer type;

    private String name;

    private String value;

    // 附加存储的数据
    private String data;

    /**
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(Integer type) {
        this.type = type;
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
