/*
 * @(#)2017年10月12日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper;

/**
 * Description: MapperException
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月12日.1	zhongzh		2017年10月12日		Create
 * </pre>
 * @date 2017年10月12日
 */
public class MapperException extends RuntimeException {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 如何描述该构造方法
     *
     * @param message
     * @param cause
     */
    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 如何描述该构造方法
     *
     * @param message
     */
    public MapperException(String message) {
        super(message);
    }

    /**
     * 如何描述该构造方法
     *
     * @param cause
     */
    public MapperException(Throwable cause) {
        super(cause);
    }

}
