/*
 * @(#)9/5/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;

import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 9/5/24.1	    zhulh		9/5/24		    Create
 * </pre>
 * @date 9/5/24
 */
public class TaskNotAssignedDecisionMakerException extends WorkFlowException {


    private Map<String, Object> variables;

    /**
     * @param variables
     * @param token
     */
    public TaskNotAssignedDecisionMakerException(Map<String, Object> variables, Token token) {
        this.variables = variables;

        // 添加组织版本信息
        OrgVersionUtils.addOrgVersionInfo(this.variables, token);
    }

    /**
     * @return
     */
    @Override
    public Object getData() {
        return this.variables;
    }

    /**
     * @return
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.TaskNotAssignedDecisionMaker;
    }

}
