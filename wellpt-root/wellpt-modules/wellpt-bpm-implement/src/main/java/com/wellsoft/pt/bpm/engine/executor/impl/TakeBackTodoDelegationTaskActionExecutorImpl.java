/*
 * @(#)2015-3-30 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.SuspensionState;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TakeBackTodoDelegationTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.TaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.param.TakeBackTodoDelegationParam;
import com.wellsoft.pt.bpm.engine.service.TaskDelegationService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.workflow.event.WorkDelegationTakeBackEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 委托的代办工作直接回收
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-3-30.1	zhulh		2015-3-30		Create
 * </pre>
 * @date 2015-3-30
 */
@Service
@Transactional
public class TakeBackTodoDelegationTaskActionExecutorImpl extends TaskActionExecutor implements
        TakeBackTodoDelegationTaskActionExecutor {

    @Autowired
    private TaskDelegationService taskDelegationService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.executor.TaskExecutor#execute(com.wellsoft.pt.bpm.engine.executor.Param)
     */
    @Override
    public void execute(Param param) {
        TaskInstance taskInstance = param.getTaskInstance();
        TaskIdentity taskIdentity = param.getTaskIdentity();
        TaskData taskData = param.getTaskData();
        String taskInstUuid = taskInstance.getUuid();
        String userId = taskData.getUserId();

        // 委托人ID
        String consignor = ((TakeBackTodoDelegationParam) param).getConsignor();
        TaskDelegation taskDelegation = ((TakeBackTodoDelegationParam) param).getTaskDelegation();
        List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuid(taskInstUuid);
        List<TaskIdentity> delegationTaskIdentities = filterDelegation(taskIdentity, taskIdentities);

        // 1、删除环节办理人及对应的待办权限
        for (TaskIdentity todoTaskIdentity : delegationTaskIdentities) {
            // 用户没有自身的待办时删除待办权限
            if (hasUserTaskIdentity(todoTaskIdentity, taskIdentities)) {
                todoTaskIdentity.setSuspensionState(SuspensionState.DELETED.getState());
                identityService.save(todoTaskIdentity);
            } else {
                identityService.removeTodo(todoTaskIdentity);
            }
            if (!StringUtils.equals(taskIdentity.getUuid(), todoTaskIdentity.getUuid()) && WorkFlowTodoType.Delegation.equals(todoTaskIdentity.getTodoType())) {
                taskDelegationService.cancelByTaskIdentityUuid(todoTaskIdentity.getUuid());
            }
        }

        // 2、记录操作
        String opinionText = "工作委托到期回收";
        if (Boolean.TRUE.equals(taskDelegation.getDeactiveToTakeBackWork())) {
            Date toTime = taskDelegation.getToTime();
            if (toTime == null || toTime.after(Calendar.getInstance().getTime())) {
                opinionText = "工作委托终止回收";
            }
        }
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.TAKE_BACK_TODO_DELEGATION),
                ActionCode.TAKE_BACK_TODO_DELEGATION.getCode(), WorkFlowOperation.TAKE_BACK_TODO_DELEGATION, null,
                null, opinionText, userId, null, null, null, null, taskInstance, flowInstance, taskData);

        // 3、增加指定人的待办权限
        if (!taskService.hasTodoPermission(consignor, taskInstUuid)) {
            String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
            // 待办工作手动委托回收
            if (StringUtils.isNotBlank(sourceTaskIdentityUuid)) {
                identityService.restoreDelegation(this.dao.get(TaskIdentity.class, sourceTaskIdentityUuid));
            } else {
                // 待办工作到达委托回收
                TaskIdentity identity = new TaskIdentity();
                identity.setTaskInstUuid(taskInstance.getUuid());
                identity.setTodoType(taskIdentity.getTodoType());
                identity.setUserId(consignor);
                identity.setSuspensionState(SuspensionState.NORMAL.getState());
                identityService.addTodo(identity);

                // 4、更新环节的待办人员列表
                identityService.updateTaskIdentity(taskInstance);

                readMarkerService.markNew(taskInstUuid, consignor);
            }

            // 发布事件
            ApplicationContextHolder.publishEvent(new WorkDelegationTakeBackEvent(this, Sets.newHashSet(consignor), flowInstance, taskInstance, taskData));
        } else {
            // 已回收受托人的权限，更新任务当前处理人信息
            identityService.updateTaskIdentity(taskInstance);
        }

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());
    }

    /**
     * @param delegationTaskIdentity
     * @param taskIdentities
     * @return
     */
    private boolean hasUserTaskIdentity(TaskIdentity delegationTaskIdentity, List<TaskIdentity> taskIdentities) {
        String userId = delegationTaskIdentity.getUserId();
        boolean hasUserTaskIdentity = taskIdentities.stream().filter(identity -> StringUtils.equals(userId, identity.getUserId())
                && !StringUtils.equals(identity.getUuid(), delegationTaskIdentity.getUuid())
                && Integer.valueOf(SuspensionState.NORMAL.getState()).equals(identity.getSuspensionState())).findFirst().isPresent();
        return hasUserTaskIdentity;
    }

    private List<TaskIdentity> filterDelegation(TaskIdentity taskIdentity, List<TaskIdentity> taskIdentities) {
        List<TaskIdentity> identities = Lists.newArrayList();
        identities.add(taskIdentity);
        String sourceTaskIdentityUuid = taskIdentity.getUuid();
        Map<String, List<TaskIdentity>> taskIdentityMap = ListUtils.list2group(taskIdentities, "sourceTaskIdentityUuid");
        addDelegationTargetTaskIdentity(sourceTaskIdentityUuid, taskIdentityMap, identities);
        return identities;
    }

    private void addDelegationTargetTaskIdentity(String sourceTaskIdentityUuid, Map<String, List<TaskIdentity>> taskIdentityMap, List<TaskIdentity> identities) {
        List<TaskIdentity> targetTaskIdentities = taskIdentityMap.get(sourceTaskIdentityUuid);
        if (CollectionUtils.isNotEmpty(targetTaskIdentities)) {
            identities.addAll(targetTaskIdentities);
            targetTaskIdentities.forEach(targetTaskIdentity -> {
                addDelegationTargetTaskIdentity(targetTaskIdentity.getUuid(), taskIdentityMap, identities);
            });
        }
    }

}
