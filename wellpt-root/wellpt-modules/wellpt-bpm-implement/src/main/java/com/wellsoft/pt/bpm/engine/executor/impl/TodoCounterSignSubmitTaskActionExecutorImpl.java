/*
 * @(#)2014-10-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.SuspensionState;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.TodoCounterSignSubmitTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class TodoCounterSignSubmitTaskActionExecutorImpl extends TaskActionExecutor implements
        TodoCounterSignSubmitTaskActionExecutor {

    @Autowired
    private DelegationExecutor delegationExecutor;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.executor.TaskExecutor#execute(com.wellsoft.pt.bpm.engine.executor.Param)
     */
    @Override
    public void execute(Param param) {
        TaskInstance taskInstance = param.getTaskInstance();
        TaskData taskData = param.getTaskData();
        String taskInstUuid = taskInstance.getUuid();
        String userId = SpringSecurityUtils.getCurrentUserId();

        TaskIdentity taskIdentity = param.getTaskIdentity();
        if (taskIdentity == null) {
            taskIdentity = getCurrentUserTaskIdentity(taskInstUuid, taskData.getUserDetails());
        }
        String taskIdentityUuid = taskIdentity.getUuid();
        String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
        boolean isLastTaskIdentity = identityService
                .isLastTodoTaskIdentityBySourceTaskIdentityUuid(sourceTaskIdentityUuid, taskIdentity.getCreator(), WorkFlowTodoType.CounterSign);
        TaskIdentity sourceTaskIdentity = identityService.get(sourceTaskIdentityUuid);
        // 删除会签待办权限
        identityService.removeTodo(taskIdentity);
        // 添加已办权限
        if (IdPrefix.startsUser(sourceTaskIdentity.getUserId())) {
            taskService.addDonePermission(userId, taskInstUuid);
        } else {
            aclTaskService.addDonePermission(userId, PermissionGranularityUtils.getCurrentUserSids(), taskInstUuid);
        }

        // 如果是会签的最的一个结点，恢愎挂起状态会签发起人(所有者)的待办权限
        if (isLastTaskIdentity) {
            // 权限粒度大于用户的转办处理，需要判断是否所有参与人都完成了
            String sourceUserId = null;
            if (!IdPrefix.startsUser(sourceTaskIdentity.getUserId())) {
                if (!Integer.valueOf(SuspensionState.NORMAL.getState()).equals(sourceTaskIdentity.getSuspensionState())) {
                    sourceTaskIdentity.setSuspensionState(SuspensionState.NORMAL.getState());
                    identityService.addTodo(sourceTaskIdentity, taskIdentity.getCreator());
                }

                sourceUserId = taskIdentity.getCreator();
                readMarkerService.markNew(taskInstUuid, sourceUserId);
                aclTaskService.removeUserDoneMarker(taskInstUuid, sourceUserId);
            } else {
                sourceTaskIdentity.setSuspensionState(SuspensionState.NORMAL.getState());
                identityService.addTodo(sourceTaskIdentity);

                sourceUserId = sourceTaskIdentity.getUserId();
                readMarkerService.markNew(taskInstUuid, sourceUserId);
            }

            // 会签工作返回通知
            sendCounterSignReturnMessage(sourceUserId, taskInstance, taskInstance.getFlowInstance(), taskData);
        }

        // 更新环节的待办人员列表
        identityService.updateTaskIdentity(taskInstance);

        // 记录操作
        if (param.isLog()) {
            FlowInstance flowInstance = taskInstance.getFlowInstance();
            // 记录会签提交本身
            String identityJson = JsonUtils.object2Json(taskIdentity);
            Integer actionCode = taskData.getActionCode(taskInstUuid);
            if (actionCode == null) {
                taskData.setActionCode(taskInstUuid, ActionCode.COUNTER_SIGN_SUBMIT.getCode());
                taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.COUNTER_SIGN_SUBMIT),
                        ActionCode.COUNTER_SIGN_SUBMIT.getCode(), WorkFlowOperation.COUNTER_SIGN_SUBMIT, null, null, null,
                        userId, null, null, taskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);
            } else {
                taskOperationService.saveTaskOperation(null, null, null, null, null, null, userId,
                        null, null, taskIdentityUuid, identityJson, taskInstance, flowInstance, taskData);
            }
        }

        // 委托工作会签到期回收
        if (sourceTaskIdentity != null) {
            String ownerId = sourceTaskIdentity.getOwnerId();
            if (StringUtils.isNotBlank(ownerId)) {
                delegationExecutor.takeBackCounterSignDelegation(sourceTaskIdentity);
            }
        }

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());
    }

    /**
     * @param sourceUserId
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     */
    private void sendCounterSignReturnMessage(String sourceUserId, TaskInstance taskInstance,
                                              FlowInstance flowInstance, TaskData taskData) {
        FlowDelegate flowDelegate = null;
        Token token = taskData.getToken();
        if (token != null) {
            flowDelegate = token.getFlowDelegate();
        } else {
            token = new Token(taskInstance, taskData);
            flowDelegate = token.getFlowDelegate();
            taskData.setToken(token);
        }
        List<MessageTemplate> counterSignReturnMessageTemplates = flowDelegate.getMessageTemplateMap().get(
                WorkFlowMessageTemplate.WF_WORK_COUNTER_SIGN_RETURN.getType());
        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_COUNTER_SIGN_RETURN,
                counterSignReturnMessageTemplates, taskInstance, flowInstance, Lists.newArrayList(sourceUserId),
                ParticipantType.TodoUser);
    }

}
