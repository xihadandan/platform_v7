/*
 * @(#)2013-10-31 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
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
 * 2013-10-31.1	zhulh		2013-10-31		Create
 * </pre>
 * @date 2013-10-31
 */
public enum SuspensionState {
    NORMAL(0), SUSPEND(1), DELETED(2), LOGIC_DELETED(3), LOGIC_SUSPEND(4);

    private int state;

    private SuspensionState(int state) {
        this.state = state;
    }

    /**
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * @param state 要设置的state
     */
    public void setState(int state) {
        this.state = state;
    }

}
