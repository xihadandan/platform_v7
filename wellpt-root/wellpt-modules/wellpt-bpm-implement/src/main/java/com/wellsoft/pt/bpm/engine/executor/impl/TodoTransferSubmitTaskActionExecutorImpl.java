/*
 * @(#)2014-10-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.executor.*;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-2.1	zhulh		2014-10-2		Create
 * </pre>
 * @date 2014-10-2
 */
@Service
@Transactional
public class TodoTransferSubmitTaskActionExecutorImpl extends TaskActionExecutor implements
        TodoTransferSubmitTaskActionExecutor {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.executor.TaskExecutor#execute(com.wellsoft.pt.bpm.engine.executor.Param)
     */
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
        String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
        boolean isLastTaskIdentity = identityService
                .isLastTodoTaskIdentityBySourceTaskIdentityUuid(sourceTaskIdentityUuid, taskIdentity.getCreator(), WorkFlowTodoType.Transfer);
        TaskIdentity sourceTaskIdentity = identityService.get(sourceTaskIdentityUuid);
        // 删除转办待办权限
        identityService.removeTodo(taskIdentity);
        // 添加已办权限
        if (IdPrefix.startsUser(sourceTaskIdentity.getUserId())) {
            taskService.addDonePermission(userId, taskInstUuid);
        } else {
            aclTaskService.addDonePermission(userId, PermissionGranularityUtils.getCurrentUserSids(), taskInstUuid);
        }
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
                // 设置流转代码为转办提交
                taskData.setTransferCode(taskInstUuid, TransferCode.TransferSubmit.getCode());
                // 设置操作代码为转办提交
                taskData.setActionCode(taskInstUuid, ActionCode.TRANSFER_SUBMIT.getCode());
                taskOperationUuid = taskOperationService.saveTaskOperation(
                        WorkFlowOperation.getName(WorkFlowOperation.TRANSFER_SUBMIT), ActionCode.TRANSFER_SUBMIT.getCode(),
                        WorkFlowOperation.TRANSFER_SUBMIT, null, null, null, userId, null, null, taskIdentity.getUuid(),
                        identityJson, taskInstance, flowInstance, taskData);
            } else {
                taskOperationUuid = taskOperationService.saveTaskOperation(null, null, null, null, null, null, userId,
                        null, null, taskIdentityUuid, identityJson, taskInstance, flowInstance, taskData);
            }

            taskData.setOperationUuid(taskInstUuid, taskOperationUuid);
        }

        // 如果是转办的最后一个结点，恢愎挂起状态转办发起人(所有者)的待办权限
        if (isLastTaskIdentity) {
            boolean completeSource = true;
            // 权限粒度大于用户的转办处理，需要判断是否所有参与人都完成了
            if (!IdPrefix.startsUser(sourceTaskIdentity.getUserId())) {
                Token token = taskData.getToken();
                if (token == null) {
                    token = new Token(taskInstance, taskData);
                }
                completeSource = isSidTaskIdentityComplete(sourceTaskIdentity.getUserId(), taskInstUuid, token);
            }
            if (completeSource) {
                Integer todoType = sourceTaskIdentity.getTodoType();
                Param sourceParam = new Param(taskInstance, taskData, sourceTaskIdentity, false);
                if (WorkFlowTodoType.Submit.equals(todoType)) {
                    // 正常提交
                    TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
                    taskExecutor.execute(sourceParam);
                } else if (WorkFlowTodoType.CounterSign.equals(todoType)) {
                    // 会签提交
                    TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.COUNTER_SIGN_SUBMIT);
                    taskExecutor.execute(sourceParam);
                } else if (WorkFlowTodoType.AddSign.equals(todoType)) {
                    // 加签提交
                    TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.ADD_SIGN_SUBMIT);
                    taskExecutor.execute(sourceParam);
                } else if (WorkFlowTodoType.Transfer.equals(todoType)) {
                    // 转办提交
                    TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.TRANSFER_SUBMIT);
                    taskExecutor.execute(sourceParam);
                } else if (WorkFlowTodoType.Delegation.equals(todoType)) {
                    // 委托提交
                    TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.DELEGATION_SUBMIT);
                    taskExecutor.execute(sourceParam);
                } else if (WorkFlowTodoType.HandOver.equals(todoType)) {
                    // 正常提交
                    TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
                    taskExecutor.execute(sourceParam);
                } else if (WorkFlowTodoType.Supplement.equals(todoType)) {
                    // 补审补办提交
                    TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUPPLEMENT);
                    taskExecutor.execute(sourceParam);
                } else {
                    throw new WorkFlowException("未知的流转类型，无法提交工作!");
                }
            }
        }

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());
    }

}
