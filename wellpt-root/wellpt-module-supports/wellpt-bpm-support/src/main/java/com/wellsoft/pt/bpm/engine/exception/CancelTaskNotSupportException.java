/*
 * @(#)2012-12-5 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-5.1	zhulh		2012-12-5		Create
 * </pre>
 * @date 2012-12-5
 */
public class CancelTaskNotSupportException extends WorkFlowException {

    private static final long serialVersionUID = 8569013724999520776L;

    /**
     *
     */
    public CancelTaskNotSupportException() {
    }

    /**
     * @param arg0
     */
    public CancelTaskNotSupportException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public CancelTaskNotSupportException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public CancelTaskNotSupportException(Throwable arg0) {
        super(arg0);
    }

}
