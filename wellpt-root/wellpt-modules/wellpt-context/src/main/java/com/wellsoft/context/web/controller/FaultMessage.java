/*
 * @(#)2013-3-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.web.controller;

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
public class FaultMessage extends ResultMessage {

    private String errorCode;

    public FaultMessage() {
        super("error", false);
    }

    /**
     * @param message
     * @param success
     */
    public FaultMessage(String message) {
        super(message, false, null);
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode 要设置的errorCode
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
