/*
 * @(#)2013-5-16 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;

import java.util.Map;

/**
 * Description: 子流程合并等待异常类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-16.1	zhulh		2013-5-16		Create
 * </pre>
 * @date 2013-5-16
 */
public class SubFlowMergeException extends WorkFlowException {

    private static final long serialVersionUID = 3774463528698447422L;

    private Map<String, String> variables;

    /**
     *
     */
    public SubFlowMergeException(Map<String, String> variables) {
        this.variables = variables;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.exception.WorkFlowException#getData()
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
        return JsonDataErrorCode.SubFlowMerge;
    }
}
