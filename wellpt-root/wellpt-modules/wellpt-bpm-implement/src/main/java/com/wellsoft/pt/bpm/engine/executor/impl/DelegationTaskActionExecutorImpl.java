/*
 * @(#)2014-10-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.SuspensionState;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.executor.DelegationTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.param.DelegationParam;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowDelegationSettingsService;
import com.wellsoft.pt.bpm.engine.service.TaskDelegationService;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.workflow.event.WorkDelegationEvent;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 委托操作执行器
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
public class DelegationTaskActionExecutorImpl extends TaskActionExecutor implements DelegationTaskActionExecutor {

//    @Autowired
//    private OrgApiFacade orgApiFacade;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private FlowDelegationSettingsService flowDelegationSettingsService;

    @Autowired
    private TaskDelegationService taskdelegationService;

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
        TaskDelegation taskDelegation = ((DelegationParam) param).getTaskDelegation();
        List<String> trustees = Arrays.asList(StringUtils.split(taskDelegation.getTrustee(),
                Separator.SEMICOLON.getValue()));

        TaskIdentity taskIdentity = param.getTaskIdentity();
        if (taskIdentity == null) {
            taskIdentity = getCurrentUserTaskIdentity(taskInstUuid, taskData.getUserDetails());
        }
        // 二次委托时完成当前委托
        if (taskIdentity != null && WorkFlowTodoType.Delegation.equals(taskIdentity.getTodoType())) {
            taskdelegationService.completeByTaskIdentityUuid(taskIdentity.getUuid());
        }

        FlowDelegationSettings flowDelegationSettings = flowDelegationSettingsService.getOne(taskDelegation.getDelegationSettingsUuid());
        if (flowDelegationSettings != null
                && flowDelegationSettings.getDelegationTaskVisible() != null
                && flowDelegationSettings.getDelegationTaskVisible()) {
            // 挂起原待办人员
//			identityService.suspenseTodoWithNotRemoveTodoPermission(taskIdentity);
        } else {
            // 挂起原待办人员
            identityService.suspenseTodo(taskIdentity);
            // identityService.updateTaskIdentity(taskInstance, taskData);
        }
        // 添加已办权限
        taskService.addDonePermission(userId, taskInstUuid);

        String ownerId = taskIdentity.getOwnerId();
        if (StringUtils.isBlank(ownerId)) {
            ownerId = taskIdentity.getUserId();
        }
        String sourceTaskIdentityUuid = taskIdentity.getUuid();

        // 解析受托人员
        List<String> trusteeUsers = IdentityResolverStrategy.resolveUserIds(trustees);
        // prepare(userId, taskInstUuid, trusteeUsers);

        // 设置信息记录
        String trusteeUserNames = IdentityResolverStrategy.resolveAsNames(trusteeUsers);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("trusteeUserNames", trusteeUserNames);
        taskData.put("delegationUserNames", trusteeUserNames);
        // 设置信息记录
        boolean updatedFormData = setOpinionRecords(taskInstance, taskData, taskIdentity, values);
        if (!updatedFormData && taskData.getDyFormData(taskData.getDataUuid()) != null) {
            dyFormFacade.saveFormData(taskData.getDyFormData(taskData.getDataUuid()));
        }

        // 增加受托人员
        for (String trusteeUser : trusteeUsers) {
            TaskIdentity identity = new TaskIdentity();
            identity.setTaskInstUuid(taskInstance.getUuid());
            identity.setTodoType(WorkFlowTodoType.Delegation);
            identity.setUserId(trusteeUser);
            identity.setOwnerId(ownerId);
            identity.setSourceTaskIdentityUuid(sourceTaskIdentityUuid);
            identity.setSuspensionState(SuspensionState.NORMAL.getState());
            identityService.addTodo(identity);
            readMarkerService.markNew(taskInstUuid, trusteeUser);

            // 设置受托人待办信息引用
            String taskIdentityUuid = identity.getUuid();
            if (StringUtils.isBlank(taskDelegation.getTaskIdentityUuid())) {
                taskDelegation.setTaskIdentityUuid(taskIdentityUuid);
            } else {
                taskDelegation.setTaskIdentityUuid(taskDelegation.getTaskIdentityUuid()
                        + Separator.SEMICOLON.getValue() + taskIdentityUuid);
            }
        }

        // 更新环节的待办人员列表
        identityService.updateTaskIdentity(taskInstance);

        // 记录操作
        if (param.isLog()) {
            taskData.setActionCode(taskInstUuid, ActionCode.DELEGATION.getCode());
            FlowInstance flowInstance = taskInstance.getFlowInstance();
            // 记录受托本身
            String identityJson = JsonUtils.object2Json(taskIdentity);
            taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.DELEGATION),
                    ActionCode.DELEGATION.getCode(), WorkFlowOperation.DELEGATION, null, null, null, userId,
                    trusteeUsers, null, taskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);
        }

        // 5、工作委托到达通知
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        List<MessageTemplate> trusteeTodoMessageTemplates = flowDelegate.getMessageTemplateMap().get(
                WorkFlowMessageTemplate.WF_WORK_ENTRUST.getType());
        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_ENTRUST, trusteeTodoMessageTemplates,
                taskInstance, flowInstance, trusteeUsers, ParticipantType.TodoUser);

        // 发布事件
        if (BooleanUtils.isTrue((Boolean) taskData.get("publishDelegationEvent"))) {
            ApplicationContextHolder.publishEvent(new WorkDelegationEvent(this, Sets.newHashSet(trusteeUsers), flowInstance, taskInstance, taskData));
        }

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());
    }

    /**
     * 如何描述该方法
     *
     * @param currentUserId
     * @param taskInstUuid
     * @param trusteeUsers
     */
    private void prepare(final String currentUserId, final String taskInstUuid, List<String> trusteeUsers) {
        // 代办人员不能为空
        if (trusteeUsers.isEmpty()) {
            throw new WorkFlowException("代办人员不能为空!");
        }
        // 获取任务待办人员
        List<String> todoUsers = identityService.getTodoUserIds(taskInstUuid);
        // 获取冲突的人员(代办人员不能包含待办人员)，忽略当前用户
        List<String> conflictUserIds = getConflictTodoUsers(todoUsers, trusteeUsers, currentUserId);
        if (!conflictUserIds.isEmpty()) {
            Map<String, String> idNameMap = workflowOrgService.getNamesByIds(conflictUserIds);
//            List<MultiOrgUserAccount> conflictUsers = orgApiFacade.queryUserAccountListByIds(conflictUserIds);
            StringBuilder sb = new StringBuilder();
            sb.append("代办人员[");
            sb.append(StringUtils.join(idNameMap.values(), Separator.COMMA.getValue()));
//            Iterator<MultiOrgUserAccount> it = conflictUsers.listIterator();
//            while (it.hasNext()) {
//                sb.append(it.next().getUserName());
//                if (it.hasNext()) {
//                    sb.append(Separator.COMMA.getValue());
//                }
//            }
            sb.append("]已经是该任务的待办人员!");
            throw new WorkFlowException(sb.toString());
        }
    }

}
