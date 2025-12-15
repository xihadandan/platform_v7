/*
 * @(#)2012-11-8 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.exception;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-8.1	zhulh		2012-11-8		Create
 * </pre>
 * @date 2012-11-8
 */
public class JobDetailDeleteFailureException extends RuntimeException {

    private static final long serialVersionUID = -7877031556406967676L;

    /**
     *
     */
    public JobDetailDeleteFailureException() {
    }

    /**
     * @param message
     */
    public JobDetailDeleteFailureException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public JobDetailDeleteFailureException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public JobDetailDeleteFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
