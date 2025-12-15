/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-24.1	zhulh		2015-6-24		Create
 * </pre>
 * @date 2015-6-24
 */
public class ExecuteException extends RuntimeException {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5833408982383022182L;

    /**
     * 如何描述该构造方法
     */
    public ExecuteException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * 如何描述该构造方法
     *
     * @param message
     * @param cause
     */
    public ExecuteException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * 如何描述该构造方法
     *
     * @param message
     */
    public ExecuteException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * 如何描述该构造方法
     *
     * @param cause
     */
    public ExecuteException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
