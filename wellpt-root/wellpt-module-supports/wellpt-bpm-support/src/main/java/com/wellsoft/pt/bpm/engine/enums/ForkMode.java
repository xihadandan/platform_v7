/*
 * @(#)2014-8-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-24.1	zhulh		2014-8-24		Create
 * </pre>
 * @date 2014-8-24
 */
public enum ForkMode {
    SINGLE(1), MULTI(2), ALL(3);

    private int value;

    private ForkMode(int value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(int value) {
        this.value = value;
    }

}
