/*
 * @(#)2016年3月27日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dbmigrate;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月27日.1	zhongzh		2016年3月27日		Create
 * </pre>
 * @date 2016年3月27日
 */
public class ChainException extends Throwable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 如何描述该构造方法
     */
    public ChainException() {
        super();
    }

    /**
     * 如何描述该构造方法
     *
     * @param message
     * @param cause
     */
    public ChainException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 如何描述该构造方法
     *
     * @param message
     * @param cause
     * @param dause
     */
    public ChainException(String message, Throwable cause, Throwable dause) {
        super(message, cause == null ? dause : cause);
    }

    /**
     * 如何描述该构造方法
     *
     * @param message
     */
    public ChainException(String message) {
        super(message);
    }

    /**
     * 如何描述该构造方法
     *
     * @param cause
     */
    public ChainException(Throwable cause) {
        super(cause);
    }

    /**
     * 如何描述该构造方法
     *
     * @param cause
     * @param dause
     */
    public ChainException(Throwable cause, Throwable dause) {
        this(cause == null ? dause : cause);
    }

}
