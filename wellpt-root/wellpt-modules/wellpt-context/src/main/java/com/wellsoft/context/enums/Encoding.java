/*
 * @(#)2013-1-29 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.enums;


/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-29.1	zhulh		2013-1-29		Create
 * </pre>
 * @date 2013-1-29
 */
public enum Encoding {
    UTF8("UTF-8"), GBK("GBK");

    private String value;

    private Encoding(String value) {

        this.value = value;
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
