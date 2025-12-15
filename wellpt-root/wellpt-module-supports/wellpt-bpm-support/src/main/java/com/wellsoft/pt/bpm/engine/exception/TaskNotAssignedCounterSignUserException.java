/*
 * @(#)2013-3-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;

import java.util.Map;

/**
 * Description: 任务找不到分配的会签用户异常类
 *
 * @author baozh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-8.1	zhulh		2013-3-8		Create
 * </pre>
 * @date 2013-3-8
 */
public class TaskNotAssignedCounterSignUserException extends WorkFlowException {

    private static final long serialVersionUID = 8489998261918676229L;

    private Map<String, Object> variables;

    /**
     * @param variables
     */
    public TaskNotAssignedCounterSignUserException(Map<String, Object> variables) {
        this.variables = variables;
    }

    /**
     * @param variables
     * @param token
     */
    public TaskNotAssignedCounterSignUserException(Map<String, Object> variables, Token token) {
        this.variables = variables;

        // 添加组织版本信息
        OrgVersionUtils.addOrgVersionInfo(this.variables, token);
    }


    @Override
    public Object getData() {
        return this.variables;
    }

    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.TaskNotAssignedCounterSignUser;
    }

}
