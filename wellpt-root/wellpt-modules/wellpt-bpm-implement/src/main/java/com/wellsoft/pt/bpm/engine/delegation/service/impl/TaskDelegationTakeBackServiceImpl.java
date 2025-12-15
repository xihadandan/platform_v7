/*
 * @(#)2015-3-30 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.delegation.service.impl;

import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.delegation.service.TaskDelegationTakeBackService;
import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.SuspensionState;
import com.wellsoft.pt.bpm.engine.executor.TaskExecutor;
import com.wellsoft.pt.bpm.engine.executor.TaskExecutorFactory;
import com.wellsoft.pt.bpm.engine.executor.param.TakeBackTodoDelegationParam;
import com.wellsoft.pt.bpm.engine.service.IdentityService;
import com.wellsoft.pt.bpm.engine.service.TaskDelegationService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 委托工作到期
 * 1、委托任务在待办状态中，直接回收
 * 2、特送个人在待办状态中，直接回收
 * 3、工作转办，不回收
 * 4、会签人员提交工作，会签返回回收
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
public class TaskDelegationTakeBackServiceImpl extends BaseServiceImpl implements TaskDelegationTakeBackService {

    private static final String GET_WAITING_TAKE_BACK_DELEGATION = "from TaskDelegation t where "
            + "t.completionState = 1 and t.taskInstUuid = :taskInstUuid and t.consignor = :consignor "
            + "and t.trustee like '%' || :trustee || '%' and t.taskIdentityUuid like '%' || :taskIdentityUuid || '%'";
    private static final String GET_NORMAL_TASK_DELEGATION_BY_TASK_ID_AND_USER_ID = "from TaskDelegation t where "
            + "t.completionState = 0 and t.taskInstUuid = :taskInstUuid and t.consignor = :consignor ";
    @Autowired
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private TaskDelegationService taskDelegationService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.delegation.service.TaskDelegationTakeBackService#takeBack(java.lang.String)
     */
    @Override
    public void takeBack(String taskDelegationUuid) {
        if (StringUtils.isBlank(taskDelegationUuid)) {
            return;
        }

        TaskDelegation taskDelegation = this.dao.get(TaskDelegation.class, taskDelegationUuid);
        if (taskDelegation == null || TaskDelegation.STATUS_COMPLETED.equals(taskDelegation.getCompletionState())) {
            return;
        }

        takeBack(taskDelegation);
    }

    /**
     * @param taskDelegation
     */
    public void takeBack(TaskDelegation taskDelegation) {
        if (!(Boolean.TRUE.equals(taskDelegation.getDueToTakeBackWork()) || Boolean.TRUE.equals(taskDelegation
                .getDeactiveToTakeBackWork()))) {
            return;
        }

        String identityUuid = taskDelegation.getTaskIdentityUuid();
        if (StringUtils.isBlank(identityUuid)) {
            taskDelegation.setCompletionState(TaskDelegation.STATUS_CANCEL);
            this.dao.save(taskDelegation);
            return;
        }

        String[] taskIdentityUuids = StringUtils.split(identityUuid, Separator.SEMICOLON.getValue());
        List<TaskIdentity> taskIdentities = identityService.get(Arrays.asList(taskIdentityUuids));
        // 单人受托人回收
        if (taskIdentities.size() == 1) {
            takeBackSingleDelegation(taskDelegation, taskIdentities.get(0));
        } else if (taskIdentities.size() > 1) {
            // 多个受托人回收
            takeBackMultiDelegation(taskDelegation, taskIdentities);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param taskDelegation
     * @param taskIdentity
     */
    private void takeBackSingleDelegation(TaskDelegation taskDelegation, TaskIdentity taskIdentity) {
        boolean isAllowedTakeBack = isAllowedTakeBackSingleTodoTask(taskDelegation, taskIdentity);
        if (isAllowedTakeBack) {
            takeBackSingleTodoTask(taskDelegation, taskIdentity);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param taskDelegation
     * @param taskIdentities
     */
    private void takeBackMultiDelegation(TaskDelegation taskDelegation, List<TaskIdentity> taskIdentities) {
        boolean allowedTackBack = isAllowedTakeBackMultiTodoTask(taskIdentities);
        if (allowedTackBack) {
            // 委托回收多个任务
            takeBackMultiTodoTask(taskDelegation, taskIdentities);
        }
    }

    /**
     * 判断委托工作是否允许回收
     *
     * @param taskDelegation
     * @param taskIdentity
     * @return
     */
    private boolean isAllowedTakeBackSingleTodoTask(TaskDelegation taskDelegation, TaskIdentity taskIdentity) {
        Integer suspensionState = taskIdentity.getSuspensionState();
        // 1、委托任务在待办状态中，直接回收
        if (Integer.valueOf(SuspensionState.NORMAL.getState()).equals(suspensionState)) {
            return true;
        }
        // 2、委托任务在挂起状态中(转办、会签、委托)
        if (Integer.valueOf(SuspensionState.SUSPEND.getState()).equals(suspensionState)) {
            // 办理类型转办待办(3)
            Integer todoType = identityService.getTodoTypeForSuspension(taskIdentity.getUuid());
            // 受托人转办，不回收
            if (WorkFlowTodoType.Transfer.equals(todoType)) {
                return false;
            } else if (WorkFlowTodoType.CounterSign.equals(todoType) && taskDelegation != null) {
                // 受托人会签，会签返回时回收
                // 委托人
                String consignor = taskDelegation.getConsignor();
                taskIdentity.setOwnerId(consignor);
                identityService.save(taskIdentity);

                // 委托任务等带回收中
                taskDelegation.setCompletionState(TaskDelegation.STATUS_WAITING_TAKE_BACK);
            } else if (WorkFlowTodoType.Delegation.equals(todoType)) {
                // 委托人委托，委托返回时回收
                // 受托人再次委托
                List<TaskIdentity> taskIdentities = identityService.listBySourceTaskIdentityUuid(taskIdentity.getUuid());
                TaskIdentity delegateTaskIdentity = taskIdentities.stream().filter(t -> WorkFlowTodoType.Delegation.equals(t.getTodoType())).findFirst().orElse(null);
                if (delegateTaskIdentity != null) {
                    return isAllowedTakeBackSingleTodoTask(null, delegateTaskIdentity);
                } else {
                    return false;
                }
            }
            return false;
        }
        // 3、委托任务已处理(转办、会签)
        return false;
    }

    /**
     * 判断委托工作是否允许回收
     *
     * @param taskIdentities
     * @return
     */
    private boolean isAllowedTakeBackMultiTodoTask(Collection<TaskIdentity> taskIdentities) {
        // 多个受托人，只有在受托人都未办理时才回收
        for (TaskIdentity taskIdentity : taskIdentities) {
            Integer suspensionState = taskIdentity.getSuspensionState();
            if (!Integer.valueOf(SuspensionState.NORMAL.getState()).equals(suspensionState)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param taskDelegation
     * @param taskIdentity
     */
    private void takeBackSingleTodoTask(TaskDelegation taskDelegation, TaskIdentity taskIdentity) {
        List<TaskIdentity> taskIdentities = new ArrayList<TaskIdentity>();
        taskIdentities.add(taskIdentity);
        takeBackMultiTodoTask(taskDelegation, taskIdentities);
    }

    /**
     * @param taskDelegation
     * @param taskIdentities
     */
    private void takeBackMultiTodoTask(TaskDelegation taskDelegation, List<TaskIdentity> taskIdentities) {
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.TAKE_BACK_TODO_DELEGATION);
        TaskInstance taskInstance = taskService.get(taskDelegation.getTaskInstUuid());
        // 工作已经结束，不再回收
        if (taskInstance.getEndTime() != null) {
            return;
        }

        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String key = taskInstance.getUuid() + userDetails.getUserId();
        TaskData taskData = new TaskData();
        taskData.setUserId(userDetails.getUserId());
        taskData.setUserName(userDetails.getUserName());
        taskData.setTaskInstUuid(taskInstance.getUuid());
        taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.TAKE_BACK_TODO_DELEGATION));
        taskData.setActionType(key, WorkFlowOperation.TAKE_BACK_TODO_DELEGATION);
        taskData.setActionCode(taskInstance.getUuid(), ActionCode.TAKE_BACK_TODO_DELEGATION.getCode());
        TakeBackTodoDelegationParam param = new TakeBackTodoDelegationParam(taskInstance, taskData,
                taskIdentities.get(0), taskDelegation, true, taskIdentities);
        taskExecutor.execute(param);

        // 非等待的委托任务标记取消
        if (!TaskDelegation.STATUS_WAITING_TAKE_BACK.equals(taskDelegation.getCompletionState())) {
            taskDelegation.setCompletionState(TaskDelegation.STATUS_CANCEL);
        }
        this.taskDelegationService.save(taskDelegation);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.delegation.service.TaskDelegationTakeBackService#takeBackCounterSignDelegation(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    public void takeBackCounterSignDelegation(TaskIdentity taskIdentity) {
        String taskInstUuid = taskIdentity.getTaskInstUuid();
        String ownerId = taskIdentity.getOwnerId();
        String userId = taskIdentity.getUserId();
        String taskIdentityUuid = taskIdentity.getUuid();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("consignor", ownerId);
        values.put("trustee", userId);
        values.put("taskIdentityUuid", taskIdentityUuid);
        List<TaskDelegation> taskDelegations = this.dao.query(GET_WAITING_TAKE_BACK_DELEGATION, values,
                TaskDelegation.class);
        if (taskDelegations.size() != 1) {
            return;
        }

        TaskDelegation taskDelegation = taskDelegations.get(0);
        // 回收工作
        takeBackSingleTodoTask(taskDelegation, taskIdentity);

        taskDelegation.setCompletionState(TaskDelegation.STATUS_COMPLETED);
        this.taskDelegationService.save(taskDelegation);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.delegation.service.TaskDelegationTakeBackService#deactiveToTakeBack(java.lang.String)
     */
    @Override
    public void deactiveToTakeBack(String delegationSettingsUuid) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("delegationSettingsUuid", delegationSettingsUuid);
        values.put("consignor", userId);
        String hql = "from TaskDelegation t where t.delegationSettingsUuid = :delegationSettingsUuid and t.consignor = :consignor ";
        List<TaskDelegation> taskDelegations = this.dao.query(hql, values, TaskDelegation.class);
        List<TaskDelegation> todoTaskDelegations = taskDelegations.stream().filter(t -> TaskDelegation.STATUS_NORMAL.equals(t.getCompletionState())).collect(Collectors.toList());
        for (TaskDelegation taskDelegation : todoTaskDelegations) {
            takeBack(taskDelegation.getUuid());
        }

        // 二次委托的任务
        List<String> completedTaskIdentityUuids = taskDelegations.stream().filter(t -> TaskDelegation.STATUS_COMPLETED.equals(t.getCompletionState())).map(TaskDelegation::getTaskIdentityUuid).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(completedTaskIdentityUuids)) {
            Map<String, TaskDelegation> taskDelegationMap = taskDelegations.stream().collect(Collectors.toMap(TaskDelegation::getTaskIdentityUuid, t -> t));
            List<TaskIdentity> secondaryDelegationIdentities = identityService.listBySourceTaskIdentityUuidsAndTodoType(completedTaskIdentityUuids, WorkFlowTodoType.Delegation);
            Set<TaskDelegation> secondaryTaskDelegations = Sets.newHashSet();
            if (CollectionUtils.isNotEmpty(secondaryDelegationIdentities)) {
                secondaryDelegationIdentities.forEach(taskIdentity -> {
                    if (taskDelegationMap.containsKey(taskIdentity.getSourceTaskIdentityUuid())) {
                        secondaryTaskDelegations.add(taskDelegationMap.get(taskIdentity.getSourceTaskIdentityUuid()));
                    }
                });
            }
            secondaryTaskDelegations.forEach(taskDelegation -> {
                takeBack(taskDelegation);
            });
        }
    }

    /**
     * 委托人提交，回收受托人任务
     */
    @Override
    public void taskSubmitToTakeBack(String taskInstUuid, String userId) {
        Map<String, Object> values = new HashMap<>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("consignor", userId);
        List<TaskDelegation> taskDelegations = this.dao.query(GET_NORMAL_TASK_DELEGATION_BY_TASK_ID_AND_USER_ID, values,
                TaskDelegation.class);

        for (TaskDelegation taskDelegation : taskDelegations) {
            taskSubmitToTakeBack(taskDelegation);
        }

    }

    /**
     * @param taskDelegation
     */
    public void taskSubmitToTakeBack(TaskDelegation taskDelegation) {
        if (taskDelegation == null || TaskDelegation.STATUS_COMPLETED.equals(taskDelegation.getCompletionState())
                || TaskDelegation.STATUS_CANCEL.equals(taskDelegation.getCompletionState())) {
            return;
        }

        String identityUuid = taskDelegation.getTaskIdentityUuid();
        String[] taskIdentityUuids = StringUtils.split(identityUuid, Separator.SEMICOLON.getValue());
        List<TaskIdentity> taskIdentities = identityService.get(Arrays.asList(taskIdentityUuids));

        for (TaskIdentity taskIdentity : taskIdentities) {
            if (SuspensionState.DELETED.getState() != taskIdentity.getSuspensionState()) {
                identityService.removeTodo(taskIdentity);
            }

            // 回收二次委托、转办、会签的任务
            List<TaskIdentity> secondaryDelegationIdentities = identityService.listBySourceTaskIdentityUuid(taskIdentity.getUuid());
            for (TaskIdentity secondaryTaskIdentity : secondaryDelegationIdentities) {
                if (SuspensionState.DELETED.getState() == taskIdentity.getSuspensionState()) {
                    continue;
                }

                if (WorkFlowTodoType.Delegation.equals(secondaryTaskIdentity.getTodoType())) {
                    TaskDelegation secondaryTaskDelegation = taskDelegationService.getByTaskIdentityUuid(secondaryTaskIdentity.getUuid());
                    if (secondaryTaskDelegation != null) {
                        taskSubmitToTakeBack(secondaryTaskDelegation);
                    }
                } else {
                    identityService.removeTodo(taskIdentity);
                }
            }
        }

        // 非等待的委托任务标记完成
        if (!TaskDelegation.STATUS_WAITING_TAKE_BACK.equals(taskDelegation.getCompletionState())) {
            taskDelegation.setCompletionState(TaskDelegation.STATUS_CANCEL);
        }
        this.dao.save(taskDelegation);
    }

}
