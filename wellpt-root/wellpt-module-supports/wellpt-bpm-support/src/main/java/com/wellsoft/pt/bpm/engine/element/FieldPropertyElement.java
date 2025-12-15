/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Description: FieldPropertyElement.java
 *
 * @author wujx
 * @date 2016-07-28
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-07-28.1	wujx	2016-07-28		Create
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldPropertyElement implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5253410335559212531L;

    // 信息格式名称
    private String name;

    // 信息格式ID
    private String value;

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
