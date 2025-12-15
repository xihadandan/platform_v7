/*
 * @(#)2013-3-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-8.1	zhulh		2013-3-8		Create
 * </pre>
 * @date 2013-3-8
 */
public abstract class JsonDataException extends RuntimeException {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2946682749351716046L;

    /**
     * 如何描述该构造方法
     */
    public JsonDataException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * 如何描述该构造方法
     *
     * @param message
     * @param cause
     */
    public JsonDataException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * 如何描述该构造方法
     *
     * @param message
     */
    public JsonDataException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * 如何描述该构造方法
     *
     * @param cause
     */
    public JsonDataException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public abstract Object getData();

    public abstract JsonDataErrorCode getErrorCode();
}
