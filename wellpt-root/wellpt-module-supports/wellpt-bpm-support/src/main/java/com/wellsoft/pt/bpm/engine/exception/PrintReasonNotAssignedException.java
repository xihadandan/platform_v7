/*
 * @(#)2014-3-30 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-30.1	zhulh		2014-3-30		Create
 * </pre>
 * @date 2014-3-30
 */
public class PrintReasonNotAssignedException extends WorkFlowException {
    private static final long serialVersionUID = 8489998261918676229L;

    private Map<String, Object> variables;

    /**
     * @param flowExceptionInfo
     */
    public PrintReasonNotAssignedException(Map<String, Object> variables) {
        this.variables = variables;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getData()
     */
    @Override
    public Object getData() {
        return variables;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.PrintReasonNotAssigned;
    }
}
