/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;

import java.util.Map;

/**
 * Description: 找不到判断分支流向异常类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-21.1	zhulh		2012-11-21		Create
 * </pre>
 * @date 2012-11-21
 */
public class JudgmentBranchFlowNotFoundException extends WorkFlowException {

    private static final long serialVersionUID = 4309841671565464135L;

    private Map<String, Object> variables;

    /**
     * @param variables
     */
    public JudgmentBranchFlowNotFoundException(Map<String, Object> variables) {
        this.variables = variables;
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
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.JudgmentBranchFlowNotFound;
    }

}
