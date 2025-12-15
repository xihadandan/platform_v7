/*
 * @(#)1/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.TodoSupplementSubmitTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 1/21/25.1	    zhulh		1/21/25		    Create
 * </pre>
 * @date 1/21/25
 */
@Service
@Transactional
public class TodoSupplementSubmitTaskActionExecutorImpl extends TaskActionExecutor implements TodoSupplementSubmitTaskActionExecutor {

    /**
     * @param param
     */
    @Override
    public void execute(Param param) {
        TaskInstance taskInstance = param.getTaskInstance();
        TaskData taskData = param.getTaskData();
        String userId = taskData.getUserId();
        String taskInstUuid = taskInstance.getUuid();
        Integer transferCode = taskData.getTransferCode(taskInstUuid);
        if (transferCode == null) {
            // 设置流转代码为提交
            taskData.setTransferCode(taskInstUuid, TransferCode.Submit.getCode());
            // 设置操作代码为提交
            taskData.setActionCode(taskInstUuid, ActionCode.SUBMIT.getCode());
        }

        TaskIdentity taskIdentity = param.getTaskIdentity();
        // 删除转办待办权限
        identityService.removeTodo(taskIdentity);
        // 添加已办权限
        taskService.addDonePermission(userId, taskInstUuid);
        // 更新环节的待办人员列表
        identityService.updateTaskIdentity(taskInstance);

        /*modified by huanglinchuan 2014.10.27 begin*/
        // 记录操作
        if (param.isLog()) {
            String identityJson = null;
            String taskIdentityUuid = null;
            if (taskIdentity != null) {
                identityJson = JsonUtils.object2Json(taskIdentity);
                taskIdentityUuid = taskIdentity.getUuid();
            }
            FlowInstance flowInstance = taskInstance.getFlowInstance();
            Integer actionCode = taskData.getActionCode(taskInstUuid);
            String taskOperationUuid = null;
            if (actionCode == null) {
                taskOperationUuid = taskOperationService.saveTaskOperation(
                        WorkFlowOperation.getName(WorkFlowOperation.SUBMIT), ActionCode.SUBMIT.getCode(),
                        WorkFlowOperation.SUBMIT, null, null, null, userId, null, null, taskIdentityUuid, identityJson,
                        taskInstance, flowInstance, taskData);
            } else {
                taskOperationUuid = taskOperationService.saveTaskOperation(null, null, null, null, null, null, userId,
                        null, null, taskIdentityUuid, identityJson, taskInstance, flowInstance, taskData);
            }
            taskData.setOperationUuid(taskInstUuid, taskOperationUuid);
        }
        /*modified by huanglinchuan 2014.10.27 begin*/

        List<TaskIdentity> todoIdentities = identityService.getTodoByTaskInstUuidAndTodoType(taskInstUuid, WorkFlowTodoType.Supplement);
        // 正常提交
        if (CollectionUtils.isEmpty(todoIdentities)) {
            Token token = new Token(taskInstance, taskData);
            token.signal();
        }

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());
    }

}
