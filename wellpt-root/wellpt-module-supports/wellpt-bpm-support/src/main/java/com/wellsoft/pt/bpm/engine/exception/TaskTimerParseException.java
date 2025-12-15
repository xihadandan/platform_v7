/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
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
 * 2013-5-25.1	zhulh		2013-5-25		Create
 * </pre>
 * @date 2013-5-25
 */
public class TaskTimerParseException extends WorkFlowException {

    private static final long serialVersionUID = 5041750961962745882L;

    /**
     *
     */
    public TaskTimerParseException() {
        super();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public TaskTimerParseException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public TaskTimerParseException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public TaskTimerParseException(Throwable arg0) {
        super(arg0);
    }

}
