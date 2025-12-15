/*
 * @(#)2014-10-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.delegation.DefaultDelegationExecutor.FlowDelegationSettingsContextHolder;
import com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor;
import com.wellsoft.pt.bpm.engine.element.RightConfigElement;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.*;
import com.wellsoft.pt.bpm.engine.exception.ChooseUnitUserException;
import com.wellsoft.pt.bpm.engine.exception.TaskNotAssignedCounterSignUserException;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.executor.CounterSignTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskActionExecutor;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import com.wellsoft.pt.workflow.event.WorkCounterSignEvent;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Description: 会签操作执行器
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
public class CounterSignTaskActionExecutorImpl extends TaskActionExecutor implements CounterSignTaskActionExecutor {

//    @Autowired
//    private OrgApiFacade orgApiFacade;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private DelegationExecutor delegationExecutor;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.executor.TaskExecutor#execute(com.wellsoft.pt.bpm.engine.executor.Param)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute(Param param) {
        TaskInstance taskInstance = param.getTaskInstance();
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        TaskData taskData = param.getTaskData();
        String taskInstUuid = taskInstance.getUuid();
        String userId = SpringSecurityUtils.getCurrentUserId();
        Token token = taskData.getToken();
        if (token == null) {
            token = new Token(taskInstance, taskData);
        }

        TaskIdentity taskIdentity = param.getTaskIdentity();
        if (taskIdentity == null) {
            taskIdentity = getCurrentUserTaskIdentity(taskInstUuid, taskData.getUserDetails());
        }

        // 会签操作选择身份
        flowUserJobIdentityService.selectSubmtJobIdentity(taskInstance, taskIdentity, taskData, flowInstance);

        // 会签配置
        RightConfigElement counterSignConfig = getCounterSignConfig(taskInstance.getId(), token.getFlowDelegate(), taskData);

        // 解析会签人员
        List<String> rawUsers = (List<String>) taskData.getCustomData("counterSignUsers");
        rawUsers = getCounterSignTaskSids(taskInstance, taskData, rawUsers, counterSignConfig);
        List<FlowUserSid> counterSignUserSids = resolveTaskUserSids(taskInstance, taskData, rawUsers);
        List<String> counterSignUsers = IdentityResolverStrategy.resolveAsOrgIds(counterSignUserSids);
        String ignoreUserId = taskIdentity == null ? userId : taskIdentity.getUserId();
        prepare(ignoreUserId, taskInstUuid, counterSignUsers);
        // 设置信息记录
        String taskUserNames = StringUtils.join(IdentityResolverStrategy.resolveAsOrgNames(counterSignUserSids),
                Separator.SEMICOLON.getValue());
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("counterSignUserNames", taskUserNames);
        taskData.put("counterSignUserNames", taskUserNames);

        // 设置信息记录
        boolean updatedFormData = setOpinionRecords(taskInstance, taskData, taskIdentity, values);
        if (!updatedFormData && taskData.getDyFormData(taskData.getDataUuid()) != null) {
            dyFormFacade.saveFormData(taskData.getDyFormData(taskData.getDataUuid()));
        }

        // 权限颗粒度大于用户处理，且每个人者要办理
        if (!IdPrefix.startsUser(taskIdentity.getUserId()) && !token.getFlowDelegate().isAnyone(taskInstance.getId())) {
            handleSidTaskIdentity(userId, taskInstUuid, taskIdentity, token);
        } else {
            // 挂起原待办人员
            identityService.suspenseTodo(taskIdentity);
            // 添加已办权限
            taskService.addDonePermission(userId, taskInstUuid);
        }

        // <办理人，办理人对应的委托信息>
        Map<String, TaskDelegation> taskDelegationMap = Maps.newHashMap();
        Map<String, TaskIdentity> taskIdentityMap = Maps.newHashMap();
        // 增加会签人员
        String sourceTaskIdentityUuid = taskIdentity.getUuid();
        FlowDelegationSettingsContextHolder.remove();
        for (FlowUserSid counterSignUserSid : counterSignUserSids) {
            String counterSignUser = counterSignUserSid.getId();
            TaskIdentity identity = new TaskIdentity();
            identity.setTaskInstUuid(taskInstance.getUuid());
            identity.setTodoType(WorkFlowTodoType.CounterSign);
            identity.setUserId(counterSignUser);
            identity.setSourceTaskIdentityUuid(sourceTaskIdentityUuid);
            identity.setSuspensionState(SuspensionState.NORMAL.getState());
            identity.setViewFormMode(ViewFormMode.from(getViewFormMode(taskData, counterSignConfig)));
            identity.setTodoTypeOperate(TodoTypeOperate.from(counterSignConfig.getCounterSignOperateRight()));
            identity.setIdentityId(counterSignUserSid.getIdentityId());
            identity.setIdentityIdPath(counterSignUserSid.getIdentityIdPath());
            // 添加待办
            identityService.addTodo(identity);

            readMarkerService.markNew(taskInstUuid, counterSignUser);
            // 工作委托
            TaskDelegation taskDelegation = delegationExecutor.checkedAndPrepareDelegation(counterSignUserSid, Sets.<String>newHashSet(counterSignUsers),
                    taskInstance, flowInstance, null);
            if (taskDelegation != null) {
                taskDelegationMap.put(counterSignUser, taskDelegation);
                taskIdentityMap.put(counterSignUser, identity);
            }
        }
        FlowDelegationSettingsContextHolder.remove();

        // 记录操作
        if (param.isLog()) {
            taskData.setActionCode(taskInstUuid, ActionCode.COUNTER_SIGN.getCode());
            // 记录转办本身
            String identityJson = JsonUtils.object2Json(taskIdentity);
            String key = taskInstUuid + userId;
            String action = taskData.getAction(key);
            action = StringUtils.isBlank(action) ? WorkFlowOperation.getName(WorkFlowOperation.COUNTER_SIGN) : action;
            taskOperationService.saveTaskOperation(action, ActionCode.COUNTER_SIGN.getCode(), WorkFlowOperation.COUNTER_SIGN, null, null, null, userId,
                    counterSignUsers, null, taskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);
        }

        // 会签人员工作委托
        for (Entry<String, TaskDelegation> entry : taskDelegationMap.entrySet()) {
            TaskDelegation taskDelegation = entry.getValue();
            delegationExecutor.delegationWork(taskInstance, taskIdentityMap.get(entry.getKey()), taskDelegation);
            identityService.flushSession();
            identityService.clearSession();
        }

        // 更新环节的待办人员列表
        identityService.updateTaskIdentity(taskInstance);

        // 发布会签事件
        ApplicationContextHolder.publishEvent(new WorkCounterSignEvent(this, Sets.newHashSet(counterSignUsers), flowInstance, taskInstance, taskData));

        // 发送会签工作到达通知
        sendCounterSignMessage(counterSignUsers, taskInstance, flowInstance, taskData);

        // 环节会签消息通知
        List<MessageTemplate> templates = new FlowDelegate(taskInstance.getFlowDefinition()).getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_TASK_COUNTERSIGN_NOTIFY.getType());
        MessageClientUtils.send(taskData,
                WorkFlowMessageTemplate.WF_WORK_TASK_COUNTERSIGN_NOTIFY, templates, taskInstance,
                flowInstance, Collections.EMPTY_LIST, ParticipantType.CopyUser);

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, flowInstance);
    }

    /**
     * @param taskData
     * @param counterSignConfig
     * @return
     */
    private String getViewFormMode(TaskData taskData, RightConfigElement counterSignConfig) {
        String viewFormMode = counterSignConfig.getCounterSignViewFormMode();
        return StringUtils.equals("custom", viewFormMode) ? taskData.getViewFormMode() : viewFormMode;
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param rawUsers
     * @return
     */
    private List<String> getCounterSignTaskSids(TaskInstance taskInstance, TaskData taskData, List<String> rawUsers, RightConfigElement counterSignConfig) {
        Token token = taskData.getToken();
        if (token == null) {
            token = new Token(taskInstance, taskData);
        }

        FlowDelegate flowDelegate = token.getFlowDelegate();
        Node node = flowDelegate.getTaskNode(taskInstance.getId());
        if (CollectionUtils.isEmpty(rawUsers)) {
            String taskId = node.getId();
            Map<String, Object> variables = new HashMap<String, Object>();
            String name = token.getFlowDelegate().getFlow().getName();
            String taskName = name + ":" + node.getName();
            variables.put("title", "(" + taskName + ")");
            variables.put("taskName", taskName);
            variables.put("taskId", taskId);
            variables.put("submitButtonId", token.getTaskData().getSubmitButtonId());
            variables.put("viewFormMode", counterSignConfig.getCounterSignViewFormMode());
            boolean isSetCounterSignUser = counterSignConfig.isSetCounterSignUser(); // flowDelegate.getIsSetTransferUser(taskId);
            if (isSetCounterSignUser) {
                UnitUser unitUser = null;
                if (token.getTask() != null) {
                    String taskInstUuid = token.getTask().getUuid();
                    String key = taskInstUuid + "_" + taskId;
                    unitUser = (UnitUser) taskData.get(key);
                }
                List<UserUnitElement> unitElements = counterSignConfig.getCounterSignUsers();//  flowDelegate.getTaskTransferUsers(taskId);
                List<FlowUserSid> optionUserIds = identityResolverStrategy.resolve(node, token, unitElements, ParticipantType.TransferUser);
                String userId = token.getTaskData().getUserId();
                List<FlowUserSid> flowUserSids = optionUserIds.stream().filter(flowUserSid -> !userId.equals(flowUserSid.getId())).collect(Collectors.toList());
                variables.put("users", optionUserIds);
                List<Map<String, String>> users = getOrgIdNameMapList(flowUserSids);
                // List<String> userIds = flowUserSids.stream().map(FlowUserSid::getId).collect(Collectors.toList());
                ChooseUnitUserException chooseUnitUserException = new ChooseUnitUserException(node, token, unitUser, taskName, taskId,
                        flowUserSids, taskData.getSubmitButtonId(), users,
                        JsonDataErrorCode.TaskNotAssignedCounterSignUser, unitElements);
                chooseUnitUserException.setViewFormMode(counterSignConfig.getCounterSignViewFormMode());
                throw chooseUnitUserException;
            }

            throw new TaskNotAssignedCounterSignUserException(variables, token);
        }
        return rawUsers;
    }

    /**
     * @param taskId
     * @param flowDelegate
     * @param taskData
     * @return
     */
    private RightConfigElement getCounterSignConfig(String taskId, FlowDelegate flowDelegate, TaskData taskData) {
        RightConfigElement rightConfig = null;
        if (flowDelegate.isXmlDefinition()) {
            rightConfig = new RightConfigElement();
            rightConfig.setIsSetCounterSignUser("2");
            return rightConfig;
        }

        TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
        Map<String, RightConfigElement> rightConfigMap = taskElement.getRightConfigMap(WorkFlowPrivilege.CounterSign.getCode());
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        if (workFlowSettings.isAclRoleIsolation()) {
            rightConfig = rightConfigMap.get(taskData.getAclRole());
        } else {
            rightConfig = rightConfigMap.values().stream().filter(rightConfigElement -> StringUtils.isNotBlank(rightConfigElement.getIsSetCounterSignUser())).findFirst().orElse(null);
        }

        if (rightConfig != null) {
            return rightConfig;
        }

        rightConfig = new RightConfigElement();
        return rightConfig;
    }

    /**
     * @param orgSids
     * @return
     */
    private List<Map<String, String>> getOrgIdNameMapList(List<FlowUserSid> orgSids) {
        List<Map<String, String>> sids = new ArrayList<Map<String, String>>();
        if (CollectionUtils.isEmpty(orgSids)) {
            return sids;
        }
        for (FlowUserSid orgSid : orgSids) {
            Map<String, String> userMap = new HashMap<String, String>();
            userMap.put("id", orgSid.getId());
            userMap.put("name", orgSid.getName());
            // isSaveJobPath jobIdPath
//            userMap.put("isSaveJobPath", String.valueOf(orgSid.getSaveJobPath()));
//            userMap.put("jobIdPath", orgSid.getJobIdPath());
//
//            userMap.put("applyUserFlag", String.valueOf(orgSid.getApplyUserFlag()));
//            userMap.put("applyUserJobIds", orgSid.getApplyUserJobIds() == null ? "" : StringUtils.join(orgSid.getApplyUserJobIds(), ";"));
//            userMap.put("filterUserFlag", String.valueOf(orgSid.getFilterUserFlag()));
//            userMap.put("filterUserJobIds", orgSid.getFilterUserJobIds() == null ? "" : StringUtils.join(orgSid.getFilterUserJobIds(), ";"));
            sids.add(userMap);
        }
        return sids;
    }

    /**
     * @param counterSignUsers
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     */
    private void sendCounterSignMessage(List<String> counterSignUsers, TaskInstance taskInstance,
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
        List<MessageTemplate> counterSignMessageTemplates = flowDelegate.getMessageTemplateMap().get(
                WorkFlowMessageTemplate.WF_WORK_COUNTER_SIGN.getType());
        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_COUNTER_SIGN, counterSignMessageTemplates,
                taskInstance, flowInstance, counterSignUsers, ParticipantType.TodoUser);
    }

    /**
     * 如何描述该方法
     *
     * @param ignoreUserId
     * @param taskInstUuid
     * @param counterSignUsers
     */
    private void prepare(String ignoreUserId, String taskInstUuid, List<String> counterSignUsers) {
        // 会签人员不能包括当前办理人
        if (counterSignUsers.contains(ignoreUserId)) {
            throw new WorkFlowException("会签人员不能包括当前办理人!");
        }
        // 会签人员不能为空
        if (counterSignUsers.isEmpty()) {
            throw new WorkFlowException("会签人员不能为空!");
        }

        // 获取已保存的任务待办人员
        List<String> todoUsers = identityService.getTodoUserIds(taskInstUuid);
        // 获取冲突的人员(会签人员不能包含待办人员)，忽略当前用户
        List<String> conflictUserIds = getConflictTodoUsers(todoUsers, counterSignUsers, ignoreUserId);
        if (!conflictUserIds.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("会签人员[");
            sb.append(IdentityResolverStrategy.resolveAsNames(conflictUserIds));
            sb.append("]已经是该任务的待办人员!");
            throw new WorkFlowException(sb.toString());
        }
    }

}
