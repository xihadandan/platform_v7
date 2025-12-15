/*
 * @(#)2013-01-29 V1.0
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
public class JobDetailNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -4626120582955073524L;

    /**
     *
     */
    public JobDetailNotFoundException() {
    }

    /**
     * @param arg0
     */
    public JobDetailNotFoundException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public JobDetailNotFoundException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public JobDetailNotFoundException(Throwable arg0) {
        super(arg0);
    }

}
