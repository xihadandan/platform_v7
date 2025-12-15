/*
 * @(#)2014-10-9 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.google.common.collect.Sets;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.delegation.DefaultDelegationExecutor.FlowDelegationSettingsContextHolder;
import com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.SuspensionState;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.executor.HandOverTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.param.HandOverParam;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.workflow.event.WorkTodoEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
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
public class HandOverTaskActionExecutorImpl extends TaskActionExecutor implements HandOverTaskActionExecutor {

    @Autowired
    private DelegationExecutor delegationExecutor;

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

        if (taskInstance.getEndTime() != null) {
            List<AclTaskEntry> getAclSid = aclTaskService.getSid(taskInstUuid, AclPermission.TODO);
            if (getAclSid.isEmpty()) {
                throw new WorkFlowException("工作已结束或已被处理，无法进行操作!");
            }
        }

        List<String> handOverUser = ((HandOverParam) param).getHandOverUser();
        boolean requiredHandOverPermission = ((HandOverParam) param).isRequiredHandOverPermission();
        // FlowInstance flowInstance = task.getFlowInstance();
        List<FlowUserSid> handOverUserSids = resolveTaskUserSids(taskInstance, taskData, handOverUser);
        List<String> userIds = IdentityResolverStrategy.resolveAsOrgIds(handOverUserSids);
        if (CollectionUtils.isEmpty(userIds)) {
            if (CollectionUtils.isNotEmpty(handOverUser)) {
                throw new WorkFlowException("请选择流程使用组织下的移交人员!");
            } else {
                throw new WorkFlowException("请选择移交人员!");
            }
        }
        // 权限判断
        if (requiredHandOverPermission && !taskService.hasMonitorPermission(taskData.getUserDetails(), taskInstUuid)) {
            throw new WorkFlowException("没有权限时行移交工作!");
        }

        // 将对象设置为对所有人未读
        readMarkerService.markNew(taskInstUuid);

        // List<TaskIdentity> identities =
        // identityService.getByTaskInstUuid(taskInstUuid);
        // String identityJson = convertToJson(taskInstUuid, identities);
        // 删除流程其他人的待办权限
        // 1、删除环节办理人及对应的待办权限
        identityService.removeTodoByTaskInstUuid(taskInstUuid);

        // 记录操作
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        // String taskOperationUuid =
        // taskRepository.storeTaskHistory(taskInstance, flowInstance,
        // WorkFlowOperation.HAND_OVER, taskData);
        // taskOperationService.setUsers(taskOperationUuid, userIds, new
        // HashSet<String>(0));
        // String taskOperationUuid =
        // taskOperationService.saveTaskOperation(taskInstance, flowInstance,
        // WorkFlowOperation.HAND_OVER, taskData, userIds, new
        // HashSet<String>(0), identityJson);
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.HAND_OVER),
                ActionCode.HAND_OVER.getCode(), WorkFlowOperation.HAND_OVER, null, null, null, userId, userIds, null,
                null, null, taskInstance, flowInstance, taskData);

        // 委托撤回
        delegationExecutor.cancelDelegationByTaskInstUuid(taskInstUuid);

        Set<String> userIdSet = Sets.newHashSet(userIds);
        // 增加指定人的待办权限
        FlowDelegationSettingsContextHolder.remove();
        for (FlowUserSid todoUserSid : handOverUserSids) {
            String todoUserId = todoUserSid.getId();
            TaskIdentity identity = new TaskIdentity();
            identity.setTaskInstUuid(taskInstance.getUuid());
            identity.setTodoType(WorkFlowTodoType.HandOver);
            identity.setUserId(todoUserId);
            // identity.setRelatedTaskOperationUuid(taskOperationUuid);
            identity.setSuspensionState(SuspensionState.NORMAL.getState());
            identity.setIdentityId(todoUserSid.getIdentityId());
            identity.setIdentityIdPath(todoUserSid.getIdentityIdPath());
            identityService.addTodo(identity);
            readMarkerService.markNew(taskInstUuid, todoUserId);

            // 委托处理
            if (!StringUtils.equals(todoUserId, userId)) {
                TaskDelegation taskDelegation = delegationExecutor.checkedAndPrepareDelegation(todoUserSid, userIdSet,
                        taskInstance, flowInstance, null);
                if (taskDelegation != null) {
                    identityService.getDao().getSession().flush();
                    delegationExecutor.delegationWork(taskInstance, identity, taskDelegation);
                }
            }
        }
        FlowDelegationSettingsContextHolder.remove();
        Set<String> msgUserIds = new HashSet<String>(userIds);
        ApplicationContextHolder.publishEvent(new WorkTodoEvent(this, msgUserIds, flowInstance, taskInstance, taskData));

        // 更新环节的待办人员列表
        identityService.updateTaskIdentity(taskInstance);

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());
    }

}
