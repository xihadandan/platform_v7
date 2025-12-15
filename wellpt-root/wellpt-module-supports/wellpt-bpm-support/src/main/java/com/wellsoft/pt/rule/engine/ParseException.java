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
public class ParseException extends RuntimeException {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2386956767612836893L;

    /**
     * 如何描述该构造方法
     */
    public ParseException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * 如何描述该构造方法
     *
     * @param message
     * @param cause
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * 如何描述该构造方法
     *
     * @param message
     */
    public ParseException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * 如何描述该构造方法
     *
     * @param cause
     */
    public ParseException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
