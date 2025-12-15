/*
 * @(#)7/9/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.delegation.DefaultDelegationExecutor;
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
import com.wellsoft.pt.bpm.engine.exception.TaskNotAssignedAddSignUserException;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.executor.AddSignTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskActionExecutor;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import com.wellsoft.pt.workflow.event.WorkAddSignEvent;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 加签操作处理
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/9/24.1	    zhulh		7/9/24		    Create
 * </pre>
 * @date 7/9/24
 */
@Service
@Transactional
public class AddSignTaskActionExecutorImpl extends TaskActionExecutor implements AddSignTaskActionExecutor {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private DelegationExecutor delegationExecutor;

    /**
     * @param param
     */
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
        String sourceTaskIdentityUuid = null;
        // 当前提交人加签
        if (taskIdentity == null) {
            taskIdentity = getCurrentUserTaskIdentity(taskInstUuid, taskData.getUserDetails());
        }
        if (taskIdentity == null) {
            // 已办人员加签
            List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuidAndOwnerId(taskInstUuid, userId);
            if (CollectionUtils.isNotEmpty(taskIdentities)) {
                taskIdentity = taskIdentities.get(0);
                taskIdentities = identityService.listBySourceTaskIdentityUuid(taskIdentity.getUuid());
                if (CollectionUtils.isNotEmpty(taskIdentities)) {
                    sourceTaskIdentityUuid = taskIdentity.getUuid();
                }
            }
        } else {
            sourceTaskIdentityUuid = taskIdentity.getUuid();
        }

        // 加签配置
        RightConfigElement addSignConfig = getAddSignConfig(taskInstance.getId(), token.getFlowDelegate(), taskData);

        // 解析加签人员
        List<String> rawUsers = (List<String>) taskData.getCustomData("addSignUsers");
        rawUsers = getAddSignTaskSids(taskInstance, taskData, rawUsers, addSignConfig);
        List<FlowUserSid> addSignUserSids = resolveTaskUserSids(taskInstance, taskData, rawUsers);
        List<String> addSignUsers = IdentityResolverStrategy.resolveAsOrgIds(addSignUserSids);
        String ignoreUserId = taskIdentity == null ? userId : taskIdentity.getUserId();
        prepare(ignoreUserId, taskInstUuid, addSignUsers);

        // 设置信息记录
        String taskUserNames = StringUtils.join(IdentityResolverStrategy.resolveAsOrgNames(addSignUserSids),
                Separator.SEMICOLON.getValue());
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("addSignUserNames", taskUserNames);
        taskData.put("addSignUserNames", taskUserNames);

        // 设置信息记录
        boolean updatedFormData = setOpinionRecords(taskInstance, taskData, taskIdentity, values);
        if (!updatedFormData && taskData.getDyFormData(taskData.getDataUuid()) != null) {
            dyFormFacade.saveFormData(taskData.getDyFormData(taskData.getDataUuid()));
        }

        // 添加已办权限
        taskService.addDonePermission(userId, taskInstUuid);

        // <办理人，办理人对应的委托信息>
        Map<String, TaskDelegation> taskDelegationMap = Maps.newHashMap();
        Map<String, TaskIdentity> taskIdentityMap = Maps.newHashMap();
        // 增加会签人员
        DefaultDelegationExecutor.FlowDelegationSettingsContextHolder.remove();
        boolean isByOrder = token.getFlowDelegate().isByOrder(taskInstance.getId());
        Integer sortOrder = null;
        if (isByOrder) {
            sortOrder = identityService.countOrderByTaskInstUuid(taskInstUuid).intValue();
        }
        for (FlowUserSid addSignUserSid : addSignUserSids) {
            String addSignUser = addSignUserSid.getId();
            TaskIdentity identity = new TaskIdentity();
            identity.setTaskInstUuid(taskInstance.getUuid());
            identity.setTodoType(WorkFlowTodoType.AddSign);
            identity.setUserId(addSignUser);
            identity.setSourceTaskIdentityUuid(sourceTaskIdentityUuid);
            identity.setSuspensionState(SuspensionState.NORMAL.getState());
            identity.setViewFormMode(ViewFormMode.from(getViewFormMode(taskData, addSignConfig)));
            identity.setTodoTypeOperate(TodoTypeOperate.from(addSignConfig.getAddSignOperateRight()));
            identity.setIdentityId(addSignUserSid.getIdentityId());
            identity.setIdentityIdPath(addSignUserSid.getIdentityIdPath());
            if (isByOrder) {
                identity.setSortOrder(sortOrder++);
                identityService.save(identity);
            } else {
                // 添加待办
                identityService.addTodo(identity);

                readMarkerService.markNew(taskInstUuid, addSignUser);
                // 工作委托
                TaskDelegation taskDelegation = delegationExecutor.checkedAndPrepareDelegation(addSignUserSid, Sets.<String>newHashSet(addSignUsers),
                        taskInstance, flowInstance, null);
                if (taskDelegation != null) {
                    taskDelegationMap.put(addSignUser, taskDelegation);
                    taskIdentityMap.put(addSignUser, identity);
                }
            }
        }
        DefaultDelegationExecutor.FlowDelegationSettingsContextHolder.remove();

        // 记录操作
        if (param.isLog()) {
            taskData.setActionCode(taskInstUuid, ActionCode.ADD_SIGN.getCode());
            // 记录转办本身
            String identityJson = JsonUtils.object2Json(taskIdentity);
            String key = taskInstUuid + userId;
            String action = taskData.getAction(key);
            action = StringUtils.isBlank(action) ? WorkFlowOperation.getName(WorkFlowOperation.ADD_SIGN) : action;
            taskOperationService.saveTaskOperation(action, ActionCode.ADD_SIGN.getCode(), WorkFlowOperation.ADD_SIGN, null, null, null, userId,
                    addSignUsers, null, taskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);
        }

        // 加签人员工作委托
        for (Map.Entry<String, TaskDelegation> entry : taskDelegationMap.entrySet()) {
            TaskDelegation taskDelegation = entry.getValue();
            delegationExecutor.delegationWork(taskInstance, taskIdentityMap.get(entry.getKey()), taskDelegation);
            identityService.flushSession();
            identityService.clearSession();
        }

        // 更新环节的待办人员列表
        identityService.updateTaskIdentity(taskInstance);

        // 发布加签事件
        ApplicationContextHolder.publishEvent(new WorkAddSignEvent(this, Sets.newHashSet(addSignUsers), flowInstance, taskInstance, taskData));

        // 发送加签工作到达通知
        sendAddSignMessage(addSignUsers, taskInstance, flowInstance, taskData);

        // 环节加签消息通知
        List<MessageTemplate> templates = new FlowDelegate(taskInstance.getFlowDefinition()).getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_TASK_ADD_SIGN_NOTIFY.getType());
        MessageClientUtils.send(taskData,
                WorkFlowMessageTemplate.WF_WORK_TASK_ADD_SIGN_NOTIFY, templates, taskInstance,
                flowInstance, Collections.EMPTY_LIST, ParticipantType.CopyUser);

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, flowInstance);
    }

    /**
     * @param addSignUsers
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     */
    private void sendAddSignMessage(List<String> addSignUsers, TaskInstance taskInstance,
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
        List<MessageTemplate> addSignMessageTemplates = flowDelegate.getMessageTemplateMap().get(
                WorkFlowMessageTemplate.WF_WORK_ADD_SIGN.getType());
        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_ADD_SIGN, addSignMessageTemplates,
                taskInstance, flowInstance, addSignUsers, ParticipantType.TodoUser);
    }

    /**
     * @param taskData
     * @param addSignConfig
     * @return
     */
    private String getViewFormMode(TaskData taskData, RightConfigElement addSignConfig) {
        String viewFormMode = addSignConfig.getAddSignViewFormMode();
        return StringUtils.equals("custom", viewFormMode) ? taskData.getViewFormMode() : viewFormMode;
    }

    /**
     * @param ignoreUserId
     * @param taskInstUuid
     * @param addSignUsers
     */
    private void prepare(String ignoreUserId, String taskInstUuid, List<String> addSignUsers) {
        // 加签人员不能包括当前办理人
        if (addSignUsers.contains(ignoreUserId)) {
            throw new WorkFlowException("加签人员不能包括当前办理人!");
        }
        // 加签人员不能为空
        if (CollectionUtils.isEmpty(addSignUsers)) {
            throw new WorkFlowException("加签人员不能为空!");
        }

        // 获取已保存的任务待办人员
        List<String> todoUsers = identityService.getTodoUserIds(taskInstUuid);
        // 获取冲突的人员(加签人员不能包含待办人员)，忽略当前用户
        List<String> conflictUserIds = getConflictTodoUsers(todoUsers, addSignUsers, ignoreUserId);
        if (!conflictUserIds.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("加签人员[");
            sb.append(IdentityResolverStrategy.resolveAsNames(conflictUserIds));
            sb.append("]已经是该任务的待办人员!");
            throw new WorkFlowException(sb.toString());
        }
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param rawUsers
     * @return
     */
    private List<String> getAddSignTaskSids(TaskInstance taskInstance, TaskData taskData, List<String> rawUsers, RightConfigElement addSignConfig) {
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
            variables.put("viewFormMode", addSignConfig.getAddSignViewFormMode());
            boolean isSetAddSignUser = addSignConfig.isSetAddSignUser();
            if (isSetAddSignUser) {
                UnitUser unitUser = null;
                if (token.getTask() != null) {
                    String taskInstUuid = token.getTask().getUuid();
                    String key = taskInstUuid + "_" + taskId;
                    unitUser = (UnitUser) taskData.get(key);
                }
                List<UserUnitElement> unitElements = addSignConfig.getAddSignUsers();
                List<FlowUserSid> optionUserIds = identityResolverStrategy.resolve(node, token, unitElements, ParticipantType.TransferUser);
                String userId = token.getTaskData().getUserId();
                List<FlowUserSid> flowUserSids = optionUserIds.stream().filter(flowUserSid -> !userId.equals(flowUserSid.getId())).collect(Collectors.toList());
                variables.put("users", optionUserIds);
                List<Map<String, String>> users = getOrgIdNameMapList(flowUserSids);
                // List<String> userIds = flowUserSids.stream().map(FlowUserSid::getId).collect(Collectors.toList());
                ChooseUnitUserException chooseUnitUserException = new ChooseUnitUserException(node, token, unitUser, taskName, taskId,
                        flowUserSids, taskData.getSubmitButtonId(), users,
                        JsonDataErrorCode.TaskNotAssignedAddSignUser, unitElements);
                chooseUnitUserException.setViewFormMode(addSignConfig.getAddSignViewFormMode());
                throw chooseUnitUserException;
            }

            throw new TaskNotAssignedAddSignUserException(variables, token);
        }
        return rawUsers;
    }

    /**
     * @param taskId
     * @param flowDelegate
     * @param taskData
     * @return
     */
    private RightConfigElement getAddSignConfig(String taskId, FlowDelegate flowDelegate, TaskData taskData) {
        RightConfigElement rightConfig = null;
        if (flowDelegate.isXmlDefinition()) {
            rightConfig = new RightConfigElement();
            rightConfig.setIsSetAddSignUser("2");
            return rightConfig;
        }

        TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
        Map<String, RightConfigElement> rightConfigMap = taskElement.getRightConfigMap(WorkFlowPrivilege.AddSign.getCode());
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        if (workFlowSettings.isAclRoleIsolation()) {
            rightConfig = rightConfigMap.get(taskData.getAclRole());
        } else {
            rightConfig = rightConfigMap.values().stream().filter(rightConfigElement -> StringUtils.isNotBlank(rightConfigElement.getIsSetAddSignUser())).findFirst().orElse(null);
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

}
