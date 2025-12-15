/*
 * @(#)2012-11-27 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

/**
 * Description: 不支持退回操作异常类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-27.1	zhulh		2012-11-27		Create
 * </pre>
 * @date 2012-11-27
 */
public class RollbackTaskNotSupportException extends WorkFlowException {

    private static final long serialVersionUID = -1384178269049414528L;

    /**
     *
     */
    public RollbackTaskNotSupportException() {
        super();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public RollbackTaskNotSupportException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public RollbackTaskNotSupportException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public RollbackTaskNotSupportException(Throwable arg0) {
        super(arg0);
    }

}
