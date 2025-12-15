/*
 * @(#)2012-11-27 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.handler;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.context.listener.TaskListener;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlowRelation;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.repository.SubTaskRepository;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowService;
import com.wellsoft.pt.bpm.engine.support.CustomRuntimeData;
import com.wellsoft.pt.bpm.engine.support.NewFlowStages;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.util.ReservedFieldUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
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
@Service
@Transactional
public class SubTaskHandlerImpl extends AbstractHandler implements SubTaskHandler {

    @Autowired
    private SubTaskRepository subTaskRepository;

    @Autowired(required = false)
    private Map<String, TaskListener> listenerMap;

    @Autowired
    private TaskSubFlowService taskSubFlowService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * @param node
     * @param executionContext
     */
    @Override
    public void enter(Node node, ExecutionContext executionContext) {
        // System.out.println("enter");
        // logger.debug("enter");
    }

    /**
     * @param node
     * @param executionContext
     */
    @Override
    public void execute(Node node, ExecutionContext executionContext) {
        // System.out.println(subTaskRepository);
        // logger.debug(subTaskRepository.toString());
        // executionContext.persistTask(node);

        // 保存任务数据
        subTaskRepository.store(node, executionContext);

        // 启动计时系统
        timerExecutor.timer(node, executionContext);

        // 发布环节开始事件
        publishTaskCreatedEvent(node, executionContext);
    }

    /**
     * @param node
     * @param executionContext
     */
    private void publishTaskCreatedEvent(Node node, ExecutionContext executionContext) {
        String[] listeners = node.getListeners();
        String rtTaskListener = (String) executionContext.getToken().getTaskData()
                .getCustomData(CustomRuntimeData.KEY_TASK_LISTENER);
        if (StringUtils.isNotBlank(rtTaskListener)) {
            listeners = (String[]) ArrayUtils.addAll(listeners, StringUtils.split(rtTaskListener, Separator.SEMICOLON.getValue()));
        }

        if (listeners == null || listeners.length == 0 || listenerMap == null || listenerMap.isEmpty()) {
            return;
        }

        Event event = getEvent(node, Listener.TASK, executionContext);
        for (String listener : listeners) {
            TaskListener taskListener = listenerMap.get(listener);
            if (taskListener == null) {
                continue;
            }
            try {
                taskListener.onCreated(event);
            } catch (Exception e) {
                String errorString = ExceptionUtils.getStackTrace(e);
                logger.error(errorString);
                if (e instanceof WorkFlowException) {
                    throw (WorkFlowException) e;
                } else {
                    throw new WorkFlowException(
                            "流程环节事件监听器" + "[" + taskListener.getName() + "]" + "执行任务创建事件处理出现异常: " + errorString);
                }
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param executionContext
     * @param subTaskNode
     */
    public void initSubTaskRelations(ExecutionContext executionContext, SubTaskNode subTaskNode) {
        TaskInstance taskInstance = executionContext.getToken().getTask();
        taskSubFlowService.initSubTaskRelations(taskInstance.getUuid(), subTaskNode);
    }

    /**
     * @param node
     * @param executionContext
     */
    public void checkSubFlowAllowSubmit(Node node, ExecutionContext executionContext) {
        FlowInstance flowInstance = executionContext.getToken().getFlowInstance();
        taskSubFlowService.checkSubFlowAllowSubmit(node.getId(), flowInstance.getUuid());
    }

    /**
     * @param node
     * @param executionContext
     */
    public void updateSubFlowRelationStatus(Node node, ExecutionContext executionContext) {
        FlowInstance flowInstance = executionContext.getToken().getFlowInstance();
        List<TaskSubFlowRelation> subFlowRelations = taskSubFlowService.updateSubFlowRelationStatus(node.getId(),
                flowInstance.getUuid(), TaskSubFlowRelation.STATUS_PASS);
        for (TaskSubFlowRelation taskSubFlowRelation : subFlowRelations) {
            String flowInstUuid = taskSubFlowRelation.getNewFlowInstUuid();
            // String taskId = taskSubFlowRelation.getTaskId();
            // 判断子流程的前置流程是否都已经通过提交
            timerExecutor.restart(flowInstUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.handler.AbstractHandler#complete(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public boolean complete(Node node, ExecutionContext executionContext) {
        Token token = executionContext.getToken();
        TaskData taskData = token.getTaskData();
        FlowInstance flowInstance = token.getFlowInstance();
        TaskInstance taskInstance = token.getTask();

        // 设置预留字段
        ReservedFieldUtils.setReservedFields(flowInstance, taskData);

        // 父流程未办结时设置流程活动状态
        if (flowInstance.getEndTime() == null) {
            flowInstance.setIsActive(true);
            flowService.saveFlowInstance(flowInstance);
        }

        // 分阶段办理时，标记当前阶段的表单数据为已完成
        String stageFormUuidKey = "stageFormUuid_" + taskInstance.getUuid();
        String stageDataUuidKey = "stageDataUuid_" + taskInstance.getUuid();
        String stageFormUuid = (String) taskData.getCustomData(stageFormUuidKey);
        String stageDataUuid = (String) taskData.getCustomData(stageDataUuidKey);
        if (StringUtils.isNotBlank(stageFormUuid) && StringUtils.isNotBlank(stageDataUuid)) {
            SubTaskNode subTaskNode = (SubTaskNode) token.getFlowDelegate().getTaskNode(taskInstance.getId());
            String stageDivisionFormId = subTaskNode.getStageDivisionFormId();
            String stageHandlingStateField = subTaskNode.getStageHandlingState();
            String stageStateField = subTaskNode.getStageState();
            DyFormData dyFormData = dyFormFacade.getDyFormData(stageFormUuid, stageDataUuid);
            dyFormData.setFieldValue(stageStateField, NewFlowStages.STAGE_STATE_COMPLETED);
            dyFormFacade.saveFormData(dyFormData);
            // 判断所有阶段是否都完成，若完成标记主表的阶段完成状态为已完成
            DyFormData mainDyFormData = taskData.getDyFormData(taskData.getDataUuid());
            if (mainDyFormData == null) {
                mainDyFormData = dyFormFacade.getDyFormData(taskInstance.getFormUuid(), taskInstance.getDataUuid());
            }
            List<DyFormData> stageDivisionFormDatas = mainDyFormData.getDyformDatasByFormId(stageDivisionFormId);
            boolean stagesCompleted = true;
            for (DyFormData stageDivisionFormData : stageDivisionFormDatas) {
                String stageState = ObjectUtils.toString(stageDivisionFormData.getFieldValue(stageStateField),
                        StringUtils.EMPTY);
                // 阶段已完成
                if (!StringUtils.equals(stageState, NewFlowStages.STAGE_STATE_COMPLETED)) {
                    stagesCompleted = false;
                    break;
                }
            }
            // 阶段办理已完成
            if (stagesCompleted) {
                mainDyFormData.setFieldValue(stageHandlingStateField, NewFlowStages.STAGE_HANDLING_STATE_COMPLETED);
                dyFormFacade.saveFormData(mainDyFormData);
            }
        }

        // 保存子流程完成的批次数据信息
        saveCompletedBatchDataUuids(flowInstance, taskInstance);

        return subTaskRepository.complete(node, executionContext);
    }

    /**
     * @param flowInstance
     * @param taskInstance
     */
    private void saveCompletedBatchDataUuids(FlowInstance flowInstance, TaskInstance taskInstance) {
        String flowInstUuid = flowInstance.getUuid();
        String taskInstUuid = taskInstance.getUuid();
        String name = FlowConstant.SUB_FLOW.KEY_BATCH_FORM_INFO + Separator.UNDERLINE.getValue() + taskInstUuid;
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        for (FlowInstanceParameter flowInstanceParameter : parameters) {
            String batchFormInfo = flowInstanceParameter.getValue();
            String[] batchFormInfos = StringUtils.split(batchFormInfo, Separator.SEMICOLON.getValue());
            for (String formInfo : batchFormInfos) {
                String[] formInfos = StringUtils.split(formInfo, Separator.COLON.getValue());
                if (formInfos.length == 2) {
                    String batchFormUuid = formInfos[0];
                    String batchDataUuid = formInfos[1];
                    saveCompletedBatchDataUuid(flowInstance.getUuid(), taskInstance.getId(), batchFormUuid,
                            batchDataUuid);
                }
            }
        }
    }

    /**
     * @param flowInstUuid
     * @param taskId
     * @param batchFormUuid
     * @param batchDataUuid
     */
    private void saveCompletedBatchDataUuid(String flowInstUuid, String taskId, String batchFormUuid,
                                            String batchDataUuid) {
        String subTaskId = taskId;
        String subFormId = dyFormFacade.getFormIdByFormUuid(batchFormUuid);
        String parentFlowInstUuid = flowInstUuid;
        String name = FlowConstant.SUB_FLOW.KEY_COMPLATED_BATCH + Separator.UNDERLINE.getValue() + subTaskId
                + Separator.UNDERLINE.getValue() + subFormId;
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(parentFlowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isEmpty(parameters)) {
            example.setValue(batchDataUuid);
            flowService.saveFlowInstanceParameter(example);
        } else {
            for (FlowInstanceParameter flowInstanceParameter : parameters) {
                String completedBatchDataUuid = flowInstanceParameter.getValue();
                String[] completedBatchDataUuids = StringUtils.split(completedBatchDataUuid,
                        Separator.SEMICOLON.getValue());
                List<String> dataUuids = Lists.newArrayList();
                dataUuids.addAll(Arrays.asList(completedBatchDataUuids));
                dataUuids.add(batchDataUuid);
                flowInstanceParameter.setValue(StringUtils.join(dataUuids, Separator.SEMICOLON.getValue()));
                flowService.saveFlowInstanceParameter(flowInstanceParameter);
            }
        }
    }

    /**
     * @param node
     * @param executionContext
     */
    @Override
    public void leave(Node node, ExecutionContext executionContext) {
        // 完成当前分支环节的处理
        boolean completeBranchTask = completeBranchTaskIfRequire(node, executionContext);

        subTaskRepository.storeLeave(node, executionContext);

        // 发布环节结束事件
        publishTaskEndEvent(node, executionContext);

        // 完成当前分支环节的处理，检测流程是要结束流程
        if (completeBranchTask) {
            completeFlowInstanceWhileBranchTaskCompletedIfRequire(node, executionContext);
        }
    }

    /**
     * @param node
     * @param executionContext
     */
    private void publishTaskEndEvent(Node node, ExecutionContext executionContext) {
        String[] listeners = node.getListeners();

        String rtTaskListener = (String) executionContext.getToken().getTaskData()
                .getCustomData(CustomRuntimeData.KEY_TASK_LISTENER);
        if (StringUtils.isNotBlank(rtTaskListener)) {
            listeners = (String[]) ArrayUtils.addAll(listeners, StringUtils.split(rtTaskListener, Separator.SEMICOLON.getValue()));
        }

        if (listeners == null || listeners.length == 0 || listenerMap == null || listenerMap.isEmpty()) {
            return;
        }

        Event event = getEvent(node, Listener.TASK, executionContext);
        for (String listener : listeners) {
            TaskListener taskListener = listenerMap.get(listener);
            if (taskListener == null) {
                continue;
            }
            try {
                taskListener.onCompleted(event);
            } catch (Exception e) {
                String errorString = ExceptionUtils.getStackTrace(e);
                logger.error(errorString);
                if (e instanceof WorkFlowException) {
                    throw (WorkFlowException) e;
                } else {
                    throw new WorkFlowException(
                            "流程环节事件监听器" + "[" + taskListener.getName() + "]" + "执行任务完成事件处理出现异常: " + errorString);
                }
            }
        }
    }

}
