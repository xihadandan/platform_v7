/*
 * @(#)2014-10-9 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.RightConfigElement;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.exception.ChooseUnitUserException;
import com.wellsoft.pt.bpm.engine.exception.TaskNotAssignedCopyUserException;
import com.wellsoft.pt.bpm.engine.executor.CopyToTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskActionExecutor;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 工作抄送执行器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-9.1	zhulh		2014-10-9		Create
 * </pre>
 * @date 2014-10-9
 */
@Service
@Transactional
public class CopyToTaskActionExecutorImpl extends TaskActionExecutor implements CopyToTaskActionExecutor {

    @Autowired
    private WfFlowSettingService flowSettingService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.executor.TaskExecutor#execute(com.wellsoft.pt.bpm.engine.executor.Param)
     */
    @Override
    public void execute(Param param) {
        TaskInstance taskInstance = param.getTaskInstance();
        TaskData taskData = param.getTaskData();
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String userId = userDetails.getUserId();
        Token token = taskData.getToken();

        // 解析抄送人员
        List<String> rawCopyUsers = (List<String>) taskData.getCustomData("copyToUsers");
        rawCopyUsers = getCopyToTaskSids(taskInstance, taskData, rawCopyUsers);
        List<FlowUserSid> copyToUserSids = resolveTaskUserSids(taskInstance, taskData, rawCopyUsers, SidGranularity.ACTIVITY);
        List<String> copyUserIds = IdentityResolverStrategy.resolveAsOrgIds(copyToUserSids);// IdentityResolverStrategy.resolveUserIds(rawCopyUsers);
        for (String copyUserId : copyUserIds) {
            aclTaskService.addPermission(taskInstance.getUuid(), AclPermission.UNREAD, copyUserId);
            readMarkerService.markNew(taskInstance.getUuid(), copyUserId);
        }

        TaskOperation relatedTaskOperation = taskOperationService.getLastestDoneByUserId(userId, taskInstance.getFlowInstance().getUuid());
        Map<String, Object> extractInfo = Maps.newHashMap();
        if (relatedTaskOperation != null) {
            extractInfo.put("taskInstUuid", relatedTaskOperation.getTaskInstUuid());
            extractInfo.put("taskId", relatedTaskOperation.getTaskId());
            extractInfo.put("relatedTaskOperationUuid", relatedTaskOperation.getUuid());
        }

        // 记录抄送日志
        String taskIdentityUuid = taskData.getTaskIdentityUuid(taskInstance.getUuid() + userId);
        if (StringUtils.isBlank(taskIdentityUuid)) {
            if (taskService.hasTodoPermission(userDetails, taskInstance.getUuid())) {
                TaskIdentity taskIdentity = getCurrentUserTaskIdentity(taskInstance.getUuid(), SpringSecurityUtils.getCurrentUser());
                if (taskIdentity != null) {
                    taskIdentityUuid = taskIdentity.getUuid();
                }
            } else {
                TaskOperation taskOperation = taskOperationService.getLastestByUserIdAndActionCodes(userId, WorkFlowOperation.getActionCodeOfSubmit(), taskInstance.getFlowInstance().getUuid());
                if (taskOperation != null) {
                    taskIdentityUuid = taskOperation.getOperatorIdentityId();
                    taskData.setUserJobIdentityId(userId, taskInstance.getUuid(), taskOperation.getOperatorIdentityId());
                }
            }
        }
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.COPY_TO),
                ActionCode.COPY_TO.getCode(), WorkFlowOperation.COPY_TO, StringUtils.EMPTY, StringUtils.EMPTY,
                StringUtils.EMPTY, userId, null, copyUserIds, taskIdentityUuid, JsonUtils.object2Json(extractInfo), taskInstance, taskInstance.getFlowInstance(),
                taskData);

        // 发送抄送工作到达通知
        List<MessageTemplate> todoCopyMessageTemplates = token.getFlowDelegate().getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_COPY.getType());
        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_COPY, todoCopyMessageTemplates, taskInstance,
                taskInstance.getFlowInstance(), copyUserIds, ParticipantType.CopyUser);
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param rawUsers
     * @return
     */
    private List<String> getCopyToTaskSids(TaskInstance taskInstance, TaskData taskData, List<String> rawUsers) {
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
            variables.put("taskName", node.getName());
            variables.put("taskId", taskId);
            variables.put("submitButtonId", token.getTaskData().getSubmitButtonId());
            RightConfigElement copyToConfig = getCopyToConfig(taskId, taskInstance, flowDelegate, taskData);
            boolean isSetCopyUser = copyToConfig.isSetCopyUser(); // flowDelegate.getIsSetTransferUser(taskId);
            if (isSetCopyUser) {
                UnitUser unitUser = null;
                if (token.getTask() != null) {
                    String taskInstUuid = token.getTask().getUuid();
                    String key = taskInstUuid + "_" + taskId;
                    unitUser = (UnitUser) taskData.get(key);
                }
                List<UserUnitElement> unitElements = copyToConfig.getCopyUsers();//  flowDelegate.getTaskTransferUsers(taskId);
                List<FlowUserSid> optionUserIds = identityResolverStrategy.resolve(node, token, unitElements, ParticipantType.TransferUser);
                String userId = token.getTaskData().getUserId();
                List<FlowUserSid> flowUserSids = optionUserIds.stream().filter(flowUserSid -> !userId.equals(flowUserSid.getId())).collect(Collectors.toList());
                variables.put("users", optionUserIds);
                List<Map<String, String>> users = Lists.newArrayListWithCapacity(0); // getOrgIdNameMapList(flowUserSids);
                // List<String> userIds = flowUserSids.stream().map(FlowUserSid::getId).collect(Collectors.toList());
                throw new ChooseUnitUserException(node, token, unitUser, taskName, taskId,
                        flowUserSids, taskData.getSubmitButtonId(), users,
                        JsonDataErrorCode.TaskNotAssignedCopyUser, unitElements);
            }

            throw new TaskNotAssignedCopyUserException(variables, token);
        }
        return rawUsers;
    }

    /**
     * @param taskId
     * @param flowDelegate
     * @param taskData
     * @return
     */
    private RightConfigElement getCopyToConfig(String taskId, TaskInstance taskInstance, FlowDelegate flowDelegate, TaskData taskData) {
        RightConfigElement rightConfig = null;
        if (flowDelegate.isXmlDefinition()) {
            rightConfig = new RightConfigElement();
            rightConfig.setIsSetCopyUser("2");
            return rightConfig;
        }
        TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
        Map<String, RightConfigElement> rightConfigMap = taskElement.getRightConfigMap(WorkFlowPrivilege.CopyTo.getCode());
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        if (workFlowSettings.isAclRoleIsolation()) {
            rightConfig = rightConfigMap.get(taskData.getAclRole());
        } else {
            rightConfig = rightConfigMap.values().stream().filter(rightConfigElement -> StringUtils.isNotBlank(rightConfigElement.getIsSetCopyUser())).findFirst().orElse(null);
//            Set<Permission> permissions = taskService.getCurrentUserPermissions(taskInstance.getUuid(), flowDelegate.getFlow().getUuid());
//            rightConfig = getRightConfig(rightConfigMap, taskElement, permissions);
        }

        if (rightConfig != null) {
            return rightConfig;
        }

        rightConfig = new RightConfigElement();
        return rightConfig;
    }

    /**
     * @param rightConfigMap
     * @param taskElement
     * @param permissions
     * @return
     */
    private RightConfigElement getRightConfig(Map<String, RightConfigElement> rightConfigMap, TaskElement taskElement, Set<Permission> permissions) {
        RightConfigElement rightConfigElement = null;
        if (permissions.contains(AclPermission.TODO) && taskElement.getButtonTodoRights().contains(WorkFlowPrivilege.CopyTo.getCode())) {
            rightConfigElement = rightConfigMap.get(WorkFlowAclRole.TODO);
        } else if (permissions.contains(AclPermission.DONE) && taskElement.getButtonDoneRights().contains(WorkFlowPrivilege.CopyTo.getCode())) {
            rightConfigElement = rightConfigMap.get(WorkFlowAclRole.DONE);
        } else if (permissions.contains(AclPermission.SUPERVISE) && taskElement.getButtonMonitorRights().contains(WorkFlowPrivilege.CopyTo.getCode())) {
            rightConfigElement = rightConfigMap.get(WorkFlowAclRole.SUPERVISE);
        } else if (permissions.contains(AclPermission.MONITOR) && taskElement.getButtonAdminRights().contains(WorkFlowPrivilege.CopyTo.getCode())) {
            rightConfigElement = rightConfigMap.get(WorkFlowAclRole.MONITOR);
        } else if ((permissions.contains(AclPermission.UNREAD) || permissions.contains(AclPermission.FLAG_READ))
                && taskElement.getButtonCopyToRights().contains(WorkFlowPrivilege.CopyTo.getCode())) {
            rightConfigElement = rightConfigMap.get(WorkFlowAclRole.UNREAD);
        } else if (permissions.contains(AclPermission.READ) && taskElement.getButtonViewerRights().contains(WorkFlowPrivilege.CopyTo.getCode())) {
            rightConfigElement = rightConfigMap.get(WorkFlowAclRole.ATTENTION);
        }
        return rightConfigElement;
    }

}
