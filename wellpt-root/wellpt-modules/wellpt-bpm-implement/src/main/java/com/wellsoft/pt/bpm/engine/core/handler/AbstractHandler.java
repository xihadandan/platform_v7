/*
 * @(#)2012-11-27 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.handler;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.event.WorkFlowEvent;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.core.Transition;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.repository.TaskRepository;
import com.wellsoft.pt.bpm.engine.service.TaskBranchService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.timer.TimerExecutor;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.workflow.enums.WorkFlowFieldMapping;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 结点处理抽象类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-27.1	zhulh		2012-11-27		Create
 * </pre>
 * @date 2012-11-27
 */
public class AbstractHandler extends BaseServiceImpl implements Handler {

    @Autowired
    protected TimerExecutor timerExecutor;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskBranchService taskBranchService;

    /**
     * @param executionContext
     * @return
     */
    public static Event getEvent(Node node, String type, ExecutionContext executionContext) {
        TaskData taskData = executionContext.getToken().getTaskData();
        TaskInstance taskInstance = executionContext.getToken().getTask();
        FlowInstance flowInstance = executionContext.getToken().getFlowInstance();
        String action = null;
        String actionType = null;
        Integer actionCode = null;
        Integer transferCode = null;
        String startedTaskNodeId = null;
        String taskId = null;
        String taskName = null;
        String taskInstUuid = null;
        String opinionValue = null;
        String opinionName = null;
        String opinionText = null;
        List<LogicFileInfo> opinionFiles = null;
        String preTaskId = null;
        String nextTaskId = null;
        String directionId = null;
        String userId = taskData.getUserId();
        Map<WorkFlowFieldMapping, Object> reservedFieldMap = new HashMap<WorkFlowFieldMapping, Object>(0);
        startedTaskNodeId = executionContext.getToken().getFlowDelegate().getStartNode().getId();
        if (taskInstance != null && Listener.TASK.equals(type)) {
            taskId = taskInstance.getId();
            taskName = taskInstance.getName();
            taskInstUuid = taskInstance.getUuid();
            preTaskId = taskData.getPreTaskId(taskId);
            String key = taskInstUuid + userId;
            String preTaskInstUuid = taskData.getPreTaskInstUuid(taskId);
            if (StringUtils.isNotBlank(preTaskInstUuid)) {
                key = preTaskInstUuid + userId;
            }
            action = taskData.getAction(key);
            actionType = taskData.getActionType(key);
            actionCode = taskData.getActionCode(preTaskInstUuid);
            transferCode = taskData.getTransferCode(preTaskInstUuid);
            opinionValue = taskData.getOpinionValue(key);
            opinionName = taskData.getOpinionLabel(key);
            opinionText = taskData.getOpinionText(key);
            opinionFiles = taskData.getOpinionFiles(key);

            // 预留字段
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_1, flowInstance.getReservedText1());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_2, flowInstance.getReservedText2());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_3, flowInstance.getReservedText3());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_4, flowInstance.getReservedText4());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_5, flowInstance.getReservedText5());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_6, flowInstance.getReservedText6());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_7, flowInstance.getReservedText7());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_8, flowInstance.getReservedText8());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_9, flowInstance.getReservedText9());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_10, flowInstance.getReservedText10());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_11, flowInstance.getReservedText11());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_12, flowInstance.getReservedText12());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_NUMBER_1, flowInstance.getReservedNumber1());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_NUMBER_2, flowInstance.getReservedNumber2());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_NUMBER_3, flowInstance.getReservedNumber3());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_DATE_1, flowInstance.getReservedDate1());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_DATE_2, flowInstance.getReservedDate2());
        } else {
            if (node != null) {
                taskId = node.getId();
                taskName = node.getName();
                taskInstUuid = "";
                preTaskId = taskData.getPreTaskId(taskId);
                String key = taskInstUuid + userId;
                String preTaskInstUuid = taskData.getPreTaskInstUuid(taskId);
                if (StringUtils.isNotBlank(preTaskInstUuid)) {
                    key = preTaskInstUuid + userId;
                }
                action = taskData.getAction(key);
                actionType = taskData.getActionType(key);
                actionCode = taskData.getActionCode(preTaskInstUuid);
                transferCode = taskData.getTransferCode(preTaskInstUuid);
                opinionValue = taskData.getOpinionValue(key);
                opinionName = taskData.getOpinionLabel(key);
                opinionText = taskData.getOpinionText(key);
                opinionFiles = taskData.getOpinionFiles(key);
            }
            // 预留字段
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_1, taskData.getReservedText1());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_2, taskData.getReservedText2());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_3, taskData.getReservedText3());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_4, taskData.getReservedText4());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_5, taskData.getReservedText5());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_6, taskData.getReservedText6());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_7, taskData.getReservedText7());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_8, taskData.getReservedText8());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_9, taskData.getReservedText9());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_10, taskData.getReservedText10());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_11, taskData.getReservedText11());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_TEXT_12, taskData.getReservedText12());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_NUMBER_1, taskData.getReservedNumber1());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_NUMBER_2, taskData.getReservedNumber2());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_NUMBER_3, taskData.getReservedNumber3());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_DATE_1, taskData.getReservedDate1());
            reservedFieldMap.put(WorkFlowFieldMapping.RESERVED_DATE_2, taskData.getReservedDate2());
        }
        Direction direction = null;
        if (Listener.DIRECTION.equals(type)) {
            Transition transition = executionContext.getToken().getTransition();
            preTaskId = transition.getFrom().getId();
            nextTaskId = transition.getRuntimeToNode().getId();
            directionId = transition.getRuntimeToNode().getDirectionId();
            if (StringUtils.isBlank(directionId)) {
                direction = executionContext.getFlowDelegate().getDirection(preTaskId, nextTaskId);
                if (direction != null) {
                    directionId = direction.getId();
                }
            }
            taskInstUuid = taskData.getTaskInstUuid();
            String key = taskInstUuid + userId;
            action = taskData.getAction(key);
            actionType = taskData.getActionType(key);
            opinionValue = taskData.getOpinionValue(key);
            opinionName = taskData.getOpinionLabel(key);
            opinionText = taskData.getOpinionText(key);
            opinionFiles = taskData.getOpinionFiles(key);
        } else {
            nextTaskId = StringUtils.join(executionContext.getFlowDelegate().getNextTaskNodes(taskId),
                    Separator.SEMICOLON.getValue());
            if (StringUtils.isNotBlank(preTaskId)) {
                direction = taskData.getTaskDirection(preTaskId, taskId);
                if (direction != null) {
                    directionId = direction.getId();
                }
            }
        }
        String flowInstId = flowInstance.getId();
        String flowInstUuid = flowInstance.getUuid();
        String title = flowInstance.getTitle();
        String flowStartUserId = flowInstance.getStartUserId();
        String flowOwnerId = flowInstance.getOwnerId();
        String flowStartDepartmentId = flowInstance.getStartDepartmentId();
        String flowOwnerDepartmentId = flowInstance.getOwnerDepartmentId();
        String flowStartUnitId = flowInstance.getStartUnitId();
        String flowOwnerUnitId = flowInstance.getOwnerUnitId();
        Date dueTime = flowInstance.getDueTime();
        boolean isSubFlowInstce = executionContext.getToken().getFlowInstance().getParent() != null;
        String flowDefUuid = flowInstance.getFlowDefinition().getUuid();
        String flowId = flowInstance.getFlowDefinition().getId();
        String flowName = flowInstance.getFlowDefinition().getName();
        String formUuid = taskData.getFormUuid();
        String dataUuid = taskData.getDataUuid();
        DyFormData formData = taskData.getDyFormData(dataUuid);
        Event event = new WorkFlowEvent(userId, action, actionType, actionCode, transferCode, startedTaskNodeId,
                taskId, taskName, taskInstUuid, opinionValue, opinionName, opinionText, opinionFiles, preTaskId, nextTaskId, directionId,
                flowInstId, flowInstUuid, title, flowStartUserId, flowOwnerId, flowStartDepartmentId,
                flowOwnerDepartmentId, flowStartUnitId, flowOwnerUnitId, dueTime, isSubFlowInstce, flowDefUuid, flowId,
                flowName, formUuid, dataUuid, formData, taskData, reservedFieldMap);
        return event;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.Handler#enter(com.wellsoft.pt.workflow.engine.node.Node,
     * com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void enter(Node node, ExecutionContext executionContext) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.Handler#execute(com.wellsoft.pt.workflow.engine.node.Node,
     * com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void execute(Node node, ExecutionContext executionContext) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.handler.Handler#afterEnter(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public void afterExecuted(Node node, ExecutionContext executionContext) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.Handler#leave(com.wellsoft.pt.workflow.engine.node.Node,
     * com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void leave(Node node, ExecutionContext executionContext) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.Handler#complete(com.wellsoft.pt.workflow.engine.node.Node,
     * com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public boolean complete(Node node, ExecutionContext executionContext) {
        return true;
    }

    /**
     * @param node
     * @param executionContext
     */
    public boolean completeBranchTaskIfRequire(Node node, ExecutionContext executionContext) {
        TaskInstance taskInstance = executionContext.getToken().getTask();
        TaskData taskData = executionContext.getToken().getTaskData();
        boolean taskAllowJoin = taskData.isTaskAllowJoin(node.getId());
        // 分支结束，未聚合，复制权限到其他未结束的分支
        if (!taskAllowJoin) {
            TaskRepository taskRepository = ApplicationContextHolder.getBean(TaskRepository.class);
            taskRepository.copyPermissions2OtherParallelTaskInstances(taskData.getUserId(), executionContext.getToken()
                    .getTask());

            // 完成未聚合的分支
            taskBranchService.completeBranchTask(taskInstance);

            // 暂停分支时限
            timerExecutor.pause(taskInstance, executionContext.getFlowInstance(), taskData);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param node
     * @param executionContext
     */
    public void completeFlowInstanceWhileBranchTaskCompletedIfRequire(Node node, ExecutionContext executionContext) {
        FlowInstance flowInstance = executionContext.getToken().getFlowInstance();
        long unfinishedTasksCount = taskService.countUnfinishedTasks(flowInstance.getUuid());
        // 结束流程
        if (unfinishedTasksCount == 0) {
            Token token = executionContext.getToken();
            token.setNode(node);
            Transition transition = node.getLeavingTransition();
            executionContext.setTransition(transition);
            executionContext.setTransitionSource(node);
            List<Node> tos = Lists.newArrayList();
            tos.add(executionContext.getToken().getFlowDelegate().getEndNode());
            transition.setTos(tos);
            transition.take(executionContext);
        }
    }

}
