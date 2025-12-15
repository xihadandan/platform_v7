/*
 * @(#)10/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.JsonDataErrorCode;

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
 * 10/13/25.1	    zhulh		10/13/25		    Create
 * </pre>
 * @date 10/13/25
 */
public class TaskDataChangedException extends WorkFlowException {
    private static final long serialVersionUID = -6350600687105547539L;

    private Map<String, Object> variables = Maps.newHashMap();

    /**
     * @param taskInstUuid
     * @param taskInstRecVer
     * @param action
     * @param actionType
     */
    public TaskDataChangedException(String taskInstUuid, Integer taskInstRecVer, String action, String actionType) {
        variables.put("taskInstUuid", taskInstUuid);
        variables.put("taskInstRecVer", taskInstRecVer);
        variables.put("action", action);
        variables.put("actionType", actionType);
    }

    @Override
    public Object getData() {
        return variables;
    }

    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.TaskDataChanged;
    }

}
