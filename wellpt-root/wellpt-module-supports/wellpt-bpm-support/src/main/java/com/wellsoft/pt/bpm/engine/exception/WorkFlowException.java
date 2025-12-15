/*
 * @(#)2012-11-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.exception.JsonDataException;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 流程执行异常类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-19.1	zhulh		2012-11-19		Create
 * </pre>
 * @date 2012-11-19
 */
public class WorkFlowException extends JsonDataException {

    private static final long serialVersionUID = -8364111602578340971L;

    private boolean autoClose = false;

    /**
     *
     */
    public WorkFlowException() {
        super();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public WorkFlowException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * @param arg0
     */
    public WorkFlowException(String message) {
        super(message);
    }

    /**
     * @param arg0
     */
    public WorkFlowException(String message, boolean autoClose) {
        super(message);
        this.autoClose = autoClose;
    }

    /**
     * @param arg0
     */
    public WorkFlowException(Throwable throwable) {
        super(throwable);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getData()
     */
    @Override
    public Object getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("autoClose", this.autoClose);
        data.put("msg", this.getMessage());
        return data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.WorkFlowException;
    }

}
