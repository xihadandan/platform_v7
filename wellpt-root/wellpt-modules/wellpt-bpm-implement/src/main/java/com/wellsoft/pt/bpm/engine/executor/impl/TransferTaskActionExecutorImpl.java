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
import com.wellsoft.pt.bpm.engine.exception.TaskNotAssignedTransferUserException;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.TransferTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import com.wellsoft.pt.workflow.event.WorkTransferEvent;
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
public class TransferTaskActionExecutorImpl extends TaskActionExecutor implements TransferTaskActionExecutor {

//    @Autowired
//    private OrgApiFacade orgApiFacade;

    @Autowired
    private DelegationExecutor delegationExecutor;

    @Autowired
    private WfFlowSettingService flowSettingService;

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

        // 转办操作选择身份
        flowUserJobIdentityService.selectSubmtJobIdentity(taskInstance, taskIdentity, taskData, flowInstance);

        // 转办配置
        RightConfigElement transferConfig = getTransferConfig(taskInstance.getId(), token.getFlowDelegate(), taskData);

        // 解析转办人员
        List<String> rawUsers = (List<String>) taskData.getCustomData("transferUsers");
        rawUsers = getTransferTaskSids(taskInstance, taskData, rawUsers, transferConfig);
        List<FlowUserSid> transferUserSids = resolveTaskUserSids(taskInstance, taskData, rawUsers);
        List<String> transferUsers = IdentityResolverStrategy.resolveAsOrgIds(transferUserSids);
        String ignoreUserId = taskIdentity == null ? userId : taskIdentity.getUserId();
        prepare(ignoreUserId, taskInstUuid, transferUsers);
        // 设置信息记录
        String taskUserNames = StringUtils.join(IdentityResolverStrategy.resolveAsOrgNames(transferUserSids),
                Separator.SEMICOLON.getValue());
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("transferUserNames", taskUserNames);
        taskData.put("transferUserNames", taskUserNames);

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
        // 增加转办人员
        String sourceTaskIdentityUuid = taskIdentity.getUuid();
        String ownerId = taskIdentity.getOwnerId();
        FlowDelegationSettingsContextHolder.remove();
        for (FlowUserSid transferUserSid : transferUserSids) {
            String transferUser = transferUserSid.getId();
            TaskIdentity identity = new TaskIdentity();
            identity.setTaskInstUuid(taskInstance.getUuid());
            identity.setTodoType(WorkFlowTodoType.Transfer);
            identity.setUserId(transferUser);
            identity.setOwnerId(ownerId);
            identity.setSourceTaskIdentityUuid(sourceTaskIdentityUuid);
            identity.setSuspensionState(SuspensionState.NORMAL.getState());
            identity.setViewFormMode(ViewFormMode.from(getViewFormMode(taskData, transferConfig)));
            identity.setTodoTypeOperate(TodoTypeOperate.from(transferConfig.getTransferOperateRight()));
            identity.setIdentityId(transferUserSid.getIdentityId());
            identity.setIdentityIdPath(transferUserSid.getIdentityIdPath());
            identityService.addTodo(identity);
            readMarkerService.markNew(taskInstUuid, transferUser);
            // 工作委托
            TaskDelegation taskDelegation = delegationExecutor.checkedAndPrepareDelegation(transferUserSid, Sets.<String>newHashSet(transferUsers),
                    taskInstance, flowInstance, null);
            if (taskDelegation != null) {
                taskDelegationMap.put(transferUser, taskDelegation);
                taskIdentityMap.put(transferUser, identity);
            }
        }
        FlowDelegationSettingsContextHolder.remove();

        // 记录操作
        if (param.isLog()) {
            String key = taskInstance.getUuid() + userId;
            String action = taskData.getAction(key);
            action = StringUtils.isNotBlank(action) ? action : WorkFlowOperation.getName(WorkFlowOperation.TRANSFER);
            taskData.setActionCode(taskInstUuid, ActionCode.TRANSFER.getCode());
            // 记录转办本身
            String identityJson = JsonUtils.object2Json(taskIdentity);
            taskOperationService.saveTaskOperation(action,
                    ActionCode.TRANSFER.getCode(), WorkFlowOperation.TRANSFER, null, null, null, userId, transferUsers,
                    null, taskIdentity.getUuid(), identityJson, taskInstance, flowInstance, taskData);
        }

        // 转办人员工作委托
        for (Entry<String, TaskDelegation> entry : taskDelegationMap.entrySet()) {
            TaskDelegation taskDelegation = entry.getValue();
            delegationExecutor.delegationWork(taskInstance, taskIdentityMap.get(entry.getKey()), taskDelegation);
            identityService.flushSession();
            identityService.clearSession();
        }

        // 更新环节的待办人员列表
        identityService.updateTaskIdentity(taskInstance);

        // 发布转办事件
        ApplicationContextHolder.publishEvent(new WorkTransferEvent(this, Sets.newHashSet(transferUsers), flowInstance, taskInstance, taskData));

        // 工作转办到达通知
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        List<MessageTemplate> transferMessageTemplates = flowDelegate.getMessageTemplateMap().get(
                WorkFlowMessageTemplate.WF_WORK_TRANSFER.getType());
        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_TRANSFER, transferMessageTemplates,
                taskInstance, flowInstance, transferUsers, ParticipantType.TodoUser);

        // 环节转办消息通知
        List<MessageTemplate> templates = new FlowDelegate(taskInstance.getFlowDefinition()).getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_TASK_TRANSFER_NOTIFY.getType());
        MessageClientUtils.send(taskData,
                WorkFlowMessageTemplate.WF_WORK_TASK_TRANSFER_NOTIFY, templates, taskInstance,
                flowInstance, Collections.EMPTY_LIST, ParticipantType.CopyUser);

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, flowInstance);
    }

    /**
     * @param taskData
     * @param transferConfig
     * @return
     */
    private String getViewFormMode(TaskData taskData, RightConfigElement transferConfig) {
        String viewFormMode = transferConfig.getTransferViewFormMode();
        return StringUtils.equals("custom", viewFormMode) ? taskData.getViewFormMode() : viewFormMode;
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param rawUsers
     * @param transferConfig
     * @return
     */
    private List<String> getTransferTaskSids(TaskInstance taskInstance, TaskData taskData, List<String> rawUsers, RightConfigElement transferConfig) {
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
            variables.put("taskInstUuid", taskInstance.getUuid());
            variables.put("submitButtonId", token.getTaskData().getSubmitButtonId());
            variables.put("viewFormMode", transferConfig.getTransferViewFormMode());
            boolean isSetTransferUser = transferConfig.isSetTransferUser(); // flowDelegate.getIsSetTransferUser(taskId);
            if (isSetTransferUser) {
                UnitUser unitUser = null;
                if (token.getTask() != null) {
                    String taskInstUuid = token.getTask().getUuid();
                    String key = taskInstUuid + "_" + taskId;
                    unitUser = (UnitUser) taskData.get(key);
                }
                List<UserUnitElement> unitElements = transferConfig.getTransferUsers();//  flowDelegate.getTaskTransferUsers(taskId);
                List<FlowUserSid> optionUserIds = identityResolverStrategy.resolve(node, token, unitElements, ParticipantType.TransferUser);
                String userId = token.getTaskData().getUserId();
                List<FlowUserSid> flowUserSids = optionUserIds.stream().filter(flowUserSid -> !userId.equals(flowUserSid.getId())).collect(Collectors.toList());
                variables.put("users", optionUserIds);
                List<Map<String, String>> users = getOrgIdNameMapList(flowUserSids);
                // List<String> userIds = flowUserSids.stream().map(FlowUserSid::getId).collect(Collectors.toList());
                ChooseUnitUserException chooseUnitUserException = new ChooseUnitUserException(node, token, unitUser, taskName, taskId,
                        flowUserSids, taskData.getSubmitButtonId(), users,
                        JsonDataErrorCode.TaskNotAssignedTransferUser, unitElements);
                chooseUnitUserException.setViewFormMode(transferConfig.getTransferViewFormMode());
                throw chooseUnitUserException;
            }

            throw new TaskNotAssignedTransferUserException(variables, token);
        }
        return rawUsers;
    }

    /**
     * @param taskId
     * @param flowDelegate
     * @param taskData
     * @return
     */
    private RightConfigElement getTransferConfig(String taskId, FlowDelegate flowDelegate, TaskData taskData) {
        RightConfigElement rightConfig = null;
        if (flowDelegate.isXmlDefinition()) {
            rightConfig = new RightConfigElement();
            rightConfig.setIsSetTransferUser(flowDelegate.getTransferUser(taskId));
            rightConfig.setTransferUsers(flowDelegate.getTaskTransferUsers(taskId));
            return rightConfig;
        }

        TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
        Map<String, RightConfigElement> rightConfigMap = taskElement.getRightConfigMap(WorkFlowPrivilege.Transfer.getCode());
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        if (workFlowSettings.isAclRoleIsolation()) {
            rightConfig = rightConfigMap.get(taskData.getAclRole());
        } else {
            rightConfig = rightConfigMap.values().stream().filter(rightConfigElement -> StringUtils.isNotBlank(rightConfigElement.getIsSetTransferUser())).findFirst().orElse(null);
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
     * 如何描述该方法
     *
     * @param ignoreUserId
     * @param taskInstUuid
     * @param transferUsers
     */
    private void prepare(final String ignoreUserId, final String taskInstUuid, List<String> transferUsers) {
        // 转办人员不能包括当前办理人
        if (transferUsers.contains(ignoreUserId)) {
            throw new WorkFlowException("转办人员不能包括当前办理人!");
        }
        // 转办人员不能为空
        if (transferUsers.isEmpty()) {
            throw new WorkFlowException("转办人员不能为空!");
        }
        // 获取任务待办人员
        List<String> todoUsers = identityService.getTodoUserIds(taskInstUuid);
        // 获取冲突的人员(转办人员不能包含待办人员)，忽略当前用户
        List<String> conflictUserIds = getConflictTodoUsers(todoUsers, transferUsers, ignoreUserId);
        if (!conflictUserIds.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("转办人员[");
            sb.append(IdentityResolverStrategy.resolveAsNames(conflictUserIds));
            sb.append("]已经是该任务的待办人员!");
            throw new WorkFlowException(sb.toString());
        }
    }

}
