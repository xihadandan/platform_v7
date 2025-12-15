/*
 * @(#)2012-11-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.repository;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.dao.FlowInstanceDao;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.SuspensionState;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.util.ReservedFieldUtils;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
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
 * 2012-11-23.1	zhulh		2012-11-23		Create
 * </pre>
 * @date 2012-11-23
 */
@Service
@Transactional
public class SubTaskRepositoryImpl implements SubTaskRepository {

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private FlowInstanceDao flowInstanceDao;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskBranchService taskBranchService;

    @Autowired
    private AclTaskService aclTaskService;

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private TaskOperationService taskOperationService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.repository.Repository#storeEnter(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void storeEnter(Node node, ExecutionContext executionContext) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.repository.Repository#store(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void store(Node node, ExecutionContext executionContext) {
        final String taskId = node.getId();
        Token token = executionContext.getToken();
        FlowInstance flowInstance = token.getFlowInstance();
        TaskData taskData = token.getTaskData();
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        TaskInstance parentTask = token.getParentTask();

        // 保存任务
        TaskInstance taskInstance = createTask(flowDefinition, flowInstance, parentTask, node, token);

        token.setTask(taskInstance);
        token.setParentTask(taskInstance.getParent());

        // 设置流程实例的当前环节
        // flowInstance.setCurrentTaskInstance(taskInstance);
        flowInstanceDao.save(flowInstance);

        // 设置权限
        String entityUuid = token.getTaskData().getPreTaskInstUuid(taskInstance.getId());
        String newEntityUuid = taskInstance.getUuid();

        String toTaskId = taskInstance.getId();
        String fromTaskId = taskData.getPreTaskId(toTaskId);
        boolean taskForking = taskData.isTaskForking(fromTaskId);
        boolean taskJoining = taskData.isTaskJoining(toTaskId);
        if (!taskForking && !taskJoining) {
            aclTaskService.changeAcl(entityUuid, newEntityUuid);
            // aclServiceWrapper.addPermission(TaskInstance.class,
            // newEntityUuid, AclPermission.TODO, userId);
        } else if (taskForking && !taskJoining) {
            // 分支流转
            taskService.copyPermissions(entityUuid, newEntityUuid, null, AclPermission.TODO);
            taskService.addDonePermission(taskData.getUserId(), newEntityUuid);
            taskService.removeDonePermission(taskData.getUserId(), entityUuid);
            // aclServiceWrapper.addPermission(TaskInstance.class,
            // newEntityUuid, AclPermission.TODO, userId);
        } else if (!taskForking && taskJoining) {
            // 合并流转
            aclTaskService.changeAcl(entityUuid, newEntityUuid);
            // aclServiceWrapper.addPermission(TaskInstance.class,
            // newEntityUuid, AclPermission.TODO, userId);
        } else {
            throw new WorkFlowException("无法识别的环节流转模型[多路分支->多路聚合]!");
        }

        String preTaskInstUuid = taskData.getPreTaskInstUuid(taskId);
        List<String> preGatewayIds = taskData.getPreGatewayIds(taskId);
        Integer transferCode = taskData.getTransferCode(preTaskInstUuid);
        transferCode = transferCode == null ? TransferCode.Submit.getCode() : transferCode;

        // 保存任务活动数据
        TaskActivity taskActivity = new TaskActivity();
        taskActivity.setTaskId(taskId);
        taskActivity.setTaskInstUuid(taskInstance.getUuid());
        taskActivity.setPreTaskId(taskData.getPreTaskId(taskId));
        taskActivity.setPreTaskInstUuid(preTaskInstUuid);
        if (CollectionUtils.isNotEmpty(preGatewayIds)) {
            taskActivity.setPreGatewayIds(StringUtils.join(preGatewayIds, Separator.SEMICOLON.getValue()));
        }
        taskActivity.setFlowInstUuid(flowInstance.getUuid());
        taskActivity.setStartTime(Calendar.getInstance().getTime());
        taskActivity.setTransferCode(transferCode);
        taskActivityService.save(taskActivity);
    }

    /**
     * 保存任务
     *
     * @param flowDefinition
     * @param flowInstance
     * @param parentTask
     * @param node
     * @return
     */
    private TaskInstance createTask(FlowDefinition flowDefinition, FlowInstance flowInstance, TaskInstance parentTask,
                                    Node node, Token token) {
        TaskData taskData = token.getTaskData();
        String taskId = node.getId();
        // 创建任务
        TaskInstance task = new TaskInstance();
        task.setName(node.getName());
        task.setId(taskId);
        task.setType(Integer.valueOf(node.getType()));
        task.setFlowDefinition(flowDefinition);
        task.setFlowInstance(flowInstance);

        // 操作动作及操作动作类型
        String preTaskInstUuid = taskData.getPreTaskInstUuid(node.getId());
        String userId = taskData.getUserId();
        String key = StringUtils.isBlank(preTaskInstUuid) ? userId : preTaskInstUuid + userId;
        String action = taskData.getAction(key);
        String actionType = taskData.getActionType(key);
        task.setAction(action);
        task.setActionType(actionType);

        // 并联信息
        Boolean isParallel = (Boolean) taskData.getPreTaskProperties(taskId, FlowConstant.BRANCH.IS_PARALLEL);
        String parallelTaskInstUuid = (String) taskData.getPreTaskProperties(taskId,
                FlowConstant.BRANCH.PARALLEL_TASK_INST_UUID);
        String fromTaskId = taskData.getPreTaskId(taskId);
        String toTaskId = taskId;
        boolean taskForking = taskData.isTaskForking(fromTaskId);
        boolean taskJoining = taskData.isTaskJoining(toTaskId);
        // 正常单一流转
        if (!taskForking && !taskJoining) {
        } else if (taskForking && !taskJoining) {
            // 分支流转
            isParallel = true;
            parallelTaskInstUuid = preTaskInstUuid;
        } else if (!taskForking && taskJoining) {
            // 合并流转
            isParallel = false;
            parallelTaskInstUuid = null;
        } else {
            throw new WorkFlowException("无法识别的环节流转模型[多路分支->多路聚合]!");
        }
        task.setIsParallel((isParallel == null) ? false : isParallel);
        task.setParallelTaskInstUuid(parallelTaskInstUuid);

        // 计时信息
        task.setTimingState(0);
        task.setAlarmState(0);
        task.setOverDueState(0);

        // 设置动态表单
        task.setFormUuid(taskData.getFormUuid());
        task.setDataUuid(taskData.getDataUuid());

        // 设置流水号
        task.setSerialNo(taskData.getTaskSerialNo());

        // 设置前办理人
        task.setAssignee(taskData.getUserId());
        task.setAssigneeName(taskData.getUserName());
        task.setStartTime(Calendar.getInstance().getTime());
        // 任务父节点
        task.setParent(parentTask);

        // 设置预留字段
        ReservedFieldUtils.setReservedFields(flowInstance, taskData);
        this.flowInstanceDao.save(flowInstance);

        // 保存任务
        this.taskInstanceService.save(task);
        this.taskInstanceService.flushSession();

        // 正常单一流转
        if (!taskForking && !taskJoining) {
            if (Boolean.TRUE.equals(task.getIsParallel())) {
                taskBranchService.changeCurrentTask(task, flowInstance, taskData);
            }
        } else if (taskForking && !taskJoining) {
            // 分支流转
            taskBranchService.createBranchTask(task, flowInstance, taskData);
        } else if (!taskForking && taskJoining) {
            // 完成前一分支
            TaskInstance preTaskInstance = taskService.getTask(preTaskInstUuid);
            if (StringUtils.isNotBlank(preTaskInstance.getParallelTaskInstUuid())) {
                taskBranchService.completeBranchTask(preTaskInstance);
            }
            // 合并流转
            parallelTaskInstUuid = (String) taskData.getPreTaskProperties(taskId,
                    FlowConstant.BRANCH.PARALLEL_TASK_INST_UUID);
            if (StringUtils.isNotBlank(parallelTaskInstUuid)) {
                taskBranchService.joinBranchTask(task, flowInstance, taskData);
            }
        }
        // 相关联的分支UUID
        String relatedTaskBranchUuid = taskData.getRelatedTaskBranchUuid(toTaskId);
        if (StringUtils.isNotBlank(relatedTaskBranchUuid)) {
            taskBranchService.syncBranchTask(task, relatedTaskBranchUuid);
        }

        return task;
    }

    /**
     * @param node
     * @param executionContext
     * @return
     */
    public boolean complete(Node node, ExecutionContext executionContext) {
        // 任务结束
        TaskInstance taskInstance = executionContext.getToken().getTask();
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        TaskData taskData = executionContext.getToken().getTaskData();

        String userId = taskData.getUserId();

        // 保存任务历史
        TaskOperation taskOperation = new TaskOperation();

        // 操作类型
        taskOperation.setFlowInstUuid(flowInstance.getUuid());

        // 非撤回、退回主流程操作，记录提交操作
        if (!(taskData.isCancel(node.getId()) || taskData.isRollback(node.getId()))) {
            taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.SUBMIT),
                    ActionCode.SUBMIT.getCode(), WorkFlowOperation.SUBMIT, "", "", "", userId, null, null, null, null,
                    taskInstance, flowInstance, taskData);
        }

        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.repository.Repository#storeLeave(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void storeLeave(Node node, ExecutionContext executionContext) {
        // 任务结束
        TaskInstance taskInstance = executionContext.getToken().getTask();
        Date startTime = taskInstance.getStartTime();
        Date endTime = Calendar.getInstance().getTime();
        taskInstance.setDuration(endTime.getTime() - startTime.getTime());
        taskInstance.setEndTime(endTime);
        // 环节结束，挂起非最新结点
        if (executionContext.getToken().getTransition() == null) {
            taskInstance.setSuspensionState(SuspensionState.SUSPEND.getState());
        } else {
            for (Node n : executionContext.getToken().getTransition().getTos()) {
                if (!FlowDelegate.END_FLOW_ID.equals(n.getId())) {
                    taskInstance.setSuspensionState(SuspensionState.SUSPEND.getState());
                }
            }
        }
        this.taskInstanceService.save(taskInstance);

        // 保存任务活动数据
        TaskActivity taskActivity = new TaskActivity();
        taskActivity.setTaskInstUuid(taskInstance.getUuid());
        List<TaskActivity> taskActivities = taskActivityService.findByExample(taskActivity);
        if (taskActivities.size() == 1) {
            taskActivity = taskActivities.get(0);
            taskActivity.setEndTime(endTime);
            taskActivityService.save(taskActivity);
        }
    }

}
