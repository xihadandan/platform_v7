/*
 * @(#)2014-10-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.*;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.executor.*;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

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
public class TodoDelegationSubmitTaskActionExecutorImpl extends TaskActionExecutor implements
        TodoDelegationSubmitTaskActionExecutor {

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
        String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
        boolean isLastTaskIdentity = identityService
                .isLastTodoTaskIdentityBySourceTaskIdentityUuid(sourceTaskIdentityUuid);
        // 删除转办待办权限
        identityService.removeTodo(taskIdentity);
        // 用户自身的待办标识
        List<TaskIdentity> userTodoIdentities = identityService.getTodoByTaskInstUuidAndUserId(taskInstUuid, userId);
        if (CollectionUtils.isNotEmpty(userTodoIdentities)) {
            userTodoIdentities.forEach(identity -> identity.setSuspensionState(SuspensionState.DELETED.getState()));
            identityService.saveAll(userTodoIdentities);
        }
        // 添加已办权限
        taskService.addDonePermission(userId, taskInstUuid);
        // 更新环节的待办人员列表
        identityService.updateTaskIdentity(taskInstance);

        // 记录操作
        if (param.isLog()) {
            String key = taskInstUuid + taskData.getUserId();
            // 操作名称、类型
            String taskAction = taskData.getAction(key);
            String taskActionType = taskData.getActionType(key);
            String action = WorkFlowOperation.getName(WorkFlowOperation.DELEGATION_SUBMIT);
            taskAction = StringUtils.isNotBlank(taskAction) ? taskAction : action;
            taskActionType = StringUtils.isNotBlank(taskActionType) ? taskActionType
                    : WorkFlowOperation.DELEGATION_SUBMIT;

            taskData.setActionCode(taskInstUuid, ActionCode.DELEGATION_SUBMIT.getCode());
            FlowInstance flowInstance = taskInstance.getFlowInstance();

            // 设置流转代码为委托提交
            taskData.setTransferCode(taskInstUuid, TransferCode.DelegationSubmit.getCode());
            // 设置操作代码为委托提交
            taskData.setActionCode(taskInstUuid, ActionCode.DELEGATION_SUBMIT.getCode());

            // 记录会签提交本身
            String identityJson = JsonUtils.object2Json(taskIdentity);
            String taskOperationUuid = taskOperationService.saveTaskOperation(taskAction,
                    ActionCode.DELEGATION_SUBMIT.getCode(), taskActionType, null, null, null, userId, null, null,
                    taskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);
            taskData.setOperationUuid(taskInstUuid, taskOperationUuid);
        }

        // 如果是转办的最后一个结点，恢愎挂起状态转办发起人(所有者)的待办权限
        if (isLastTaskIdentity) {
            TaskIdentity source = identityService.get(sourceTaskIdentityUuid);
            Integer todoType = source.getTodoType();
            Param sourceParam = new Param(taskInstance, taskData, source, false);
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

                // 环节提交消息通知
                List<MessageTemplate> templates = new FlowDelegate(taskInstance.getFlowDefinition()).getMessageTemplateMap()
                        .get(WorkFlowMessageTemplate.WF_WORK_TASK_SUBMIT_NOTIFY.getType());
                MessageClientUtils.send(taskData,
                        WorkFlowMessageTemplate.WF_WORK_TASK_SUBMIT_NOTIFY, templates, taskInstance,
                        taskInstance.getFlowInstance(), Collections.EMPTY_LIST, ParticipantType.CopyUser);

            } else if (WorkFlowTodoType.Supplement.equals(todoType)) {
                // 补审补办提交
                TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUPPLEMENT);
                taskExecutor.execute(sourceParam);
            } else {
                throw new WorkFlowException("未知的流转类型，无法提交工作!");
            }
        }

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());
    }
}
