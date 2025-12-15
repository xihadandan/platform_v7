/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
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
 * 2015-6-24.1	zhulh		2015-6-24		Create
 * </pre>
 * @date 2015-6-24
 */
public class BusinessException extends JsonDataException {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4556315306975753976L;

    private JsonDataErrorCode errorCode = JsonDataErrorCode.BusinessException;

    /**
     *
     */
    public BusinessException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public BusinessException(Throwable cause) {
        super(cause);
    }

    /**
     * @param errorCode
     */
    public BusinessException(String message, JsonDataErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getData()
     */
    @Override
    public Object getData() {
        return this.getMessage();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return errorCode;
    }

}
