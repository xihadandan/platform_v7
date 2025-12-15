/*
 * @(#)2013-3-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;

import java.util.Map;

/**
 * Description: 找不到子流程的流程ID异常类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-26.1	zhulh		2013-3-26		Create
 * </pre>
 * @date 2013-3-26
 */
public class SubFlowNotFoundException extends WorkFlowException {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7688596621660553611L;

    private Map<String, Object> variables;

    /**
     * @param taskNode
     */
    public SubFlowNotFoundException(Map<String, Object> variables) {
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
        return JsonDataErrorCode.SubFlowNotFound;
    }

    /**
     * @return the variables
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * @param variables 要设置的variables
     */
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

}
