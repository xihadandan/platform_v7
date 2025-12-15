/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;

import java.util.Map;

/**
 * Description: 多个判断分支流向异常类
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
public class MultipleJudgmentBranchFlowException extends WorkFlowException {

    private static final long serialVersionUID = 8285802398475591511L;

    private Map<String, Object> variables;

    /**
     * @param variables
     */
    public MultipleJudgmentBranchFlowException(Map<String, Object> variables) {
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
     * @see com.wellsoft.pt.bpm.engine.exception.WorkFlowException#getData()
     */
    @Override
    public Object getData() {
        return variables;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.exception.WorkFlowException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.MultiJudgmentBranch;
    }

}
