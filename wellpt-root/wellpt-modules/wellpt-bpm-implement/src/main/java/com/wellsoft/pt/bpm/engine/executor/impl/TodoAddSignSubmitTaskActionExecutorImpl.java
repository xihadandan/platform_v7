/*
 * @(#)7/10/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
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
import com.wellsoft.pt.bpm.engine.executor.TodoAddSignSubmitTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.support.AclPermission;
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
 * 7/10/24.1	    zhulh		7/10/24		    Create
 * </pre>
 * @date 7/10/24
 */
@Service
@Transactional
public class TodoAddSignSubmitTaskActionExecutorImpl extends TaskActionExecutor implements
        TodoAddSignSubmitTaskActionExecutor {

    @Override
    public void execute(Param param) {
        TaskInstance taskInstance = param.getTaskInstance();
        TaskData taskData = param.getTaskData();
        String taskInstUuid = taskInstance.getUuid();
        String userId = taskData.getUserId();

        TaskIdentity taskIdentity = param.getTaskIdentity();
        if (taskIdentity == null) {
            taskIdentity = getCurrentUserTaskIdentity(taskInstUuid, taskData.getUserDetails());
        }
        String taskIdentityUuid = taskIdentity.getUuid();
//        String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
//        boolean isLastTaskIdentity = identityService
//                .isLastTodoTaskIdentityBySourceTaskIdentityUuid(sourceTaskIdentityUuid);
        // 删除待办权限
        identityService.removeTodo(taskIdentity);
        // 添加已办权限
        taskService.addDonePermission(userId, taskInstUuid);
        // 更新环节的待办人员列表
        identityService.updateTaskIdentity(taskInstance);

        // 记录操作
        if (param.isLog()) {
            FlowInstance flowInstance = taskInstance.getFlowInstance();
            // 记录会签提交本身
            String identityJson = JsonUtils.object2Json(taskIdentity);

            Integer actionCode = taskData.getActionCode(taskInstUuid);
            String taskOperationUuid = null;
            if (actionCode == null) {
                // 设置流转代码为提交
                taskData.setTransferCode(taskInstUuid, TransferCode.Submit.getCode());
                // 设置操作代码为提交
                taskData.setActionCode(taskInstUuid, ActionCode.ADD_SIGN_SUBMIT.getCode());
                taskOperationUuid = taskOperationService.saveTaskOperation(
                        WorkFlowOperation.getName(WorkFlowOperation.SUBMIT), ActionCode.ADD_SIGN_SUBMIT.getCode(),
                        WorkFlowOperation.SUBMIT, null, null, null, userId, null, null, taskIdentity.getUuid(),
                        identityJson, taskInstance, flowInstance, taskData);
            } else {
                taskOperationUuid = taskOperationService.saveTaskOperation(null, null, null, null, null, null, userId,
                        null, null, taskIdentityUuid, identityJson, taskInstance, flowInstance, taskData);
            }

            taskData.setOperationUuid(taskInstUuid, taskOperationUuid);
        }

        List<AclTaskEntry> todoSids = aclTaskService.getSid(taskInstUuid, AclPermission.TODO);
        if (CollectionUtils.isEmpty(todoSids)) {
            // 正常提交
            Token token = new Token(taskInstance, taskData);
            token.signal();
        }

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());
    }

}
