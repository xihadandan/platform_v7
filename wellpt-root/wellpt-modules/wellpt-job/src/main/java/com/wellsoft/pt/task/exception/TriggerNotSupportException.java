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
public class TriggerNotSupportException extends RuntimeException {

    private static final long serialVersionUID = -5383146115944673794L;

    /**
     *
     */
    public TriggerNotSupportException() {
    }

    /**
     * @param arg0
     */
    public TriggerNotSupportException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public TriggerNotSupportException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public TriggerNotSupportException(Throwable arg0) {
        super(arg0);
    }

}
