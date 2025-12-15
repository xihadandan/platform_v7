/*
 * @(#)2012-11-27 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.handler;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.InternalTaskListener;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.context.listener.TaskListener;
import com.wellsoft.pt.bpm.engine.core.Pointcut;
import com.wellsoft.pt.bpm.engine.core.Script;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor;
import com.wellsoft.pt.bpm.engine.element.AlarmElement;
import com.wellsoft.pt.bpm.engine.element.AutoSubmitRuleElement;
import com.wellsoft.pt.bpm.engine.element.TimerElement;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.TaskNode;
import com.wellsoft.pt.bpm.engine.repository.TaskRepository;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.SameUserSubmitService;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceService;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
public class TaskHandlerImpl extends AbstractHandler implements TaskHandler {

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private SameUserSubmitService sameUserSubmitService;

    @Autowired(required = false)
    private Map<String, TaskListener> listenerMap;

    @Autowired(required = false)
    private Map<String, InternalTaskListener> internalTaskListenerMap;

    @Autowired
    private DelegationExecutor delegationExecutor;

    /**
     * 初始化环节配置信息
     *
     * @param node
     * @param executionContext
     */
    private static void initTaskVariable(Node node, ExecutionContext executionContext) {
        FlowDelegate flowDelegate = executionContext.getFlowDelegate();
        String serialNoDefId = flowDelegate.getTaskSerialNoDefId(node.getId());
        executionContext.getToken().getTaskData().setSerialNoDefId(serialNoDefId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.AbstractHandler#enter(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void enter(Node node, ExecutionContext executionContext) {
        // 添加环节配置信息
        initTaskVariable(node, executionContext);

        // 用户计算、权限计算在进入结点时计算
        // executionContext.evaluate(node);

        // 用户计算
        // List<String> userIds = identityResolverStrategy.resolve(node,
        // executionContext.getToken());
        // Token token = executionContext.getToken();
        // token.getTaskData().putParticipant(node.getId(), userIds);
        //
        // //权限判断
        // for (String userId : userIds) {
        // Object permission = getRuntimePermission();
        // Identity identity = new Identity();
        // identity.setUserId(userId);
        // if (!permissionEvaluator.hasPermission(identity, node, permission)) {
        // throw new IdentityNotFlowPermissionException(identity, node,
        // permission);
        // }
        // }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.AbstractHandler#execute(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void execute(Node node, ExecutionContext executionContext) {
        // System.out.println(taskRepository);
        logger.debug(taskRepository.toString());
        // executionContext.persistTask(node);

        // 环节停止计时
        stopTimerByTaskInstanceIfRequired(node, executionContext);

        // 保存任务数据
        taskRepository.store(node, executionContext);

        // 启动计时系统
        timerExecutor.timer(node, executionContext);

        // 发布环节开始事件
        publishTaskCreatedEvent(node, executionContext);

        // 执行事件脚本
        executeEventScript(node, executionContext, Pointcut.CREATED);

        // 发送到达消息
        sendTaskMessage(node, executionContext);
    }

    /**
     * 流出计时环节结束计时
     *
     * @param node
     * @param executionContext
     */
    private void stopTimerByTaskInstanceIfRequired(Node node, ExecutionContext executionContext) {
        List<TimerElement> timerElements = executionContext.getFlowDelegate().getTimers();
        TaskData taskData = executionContext.getToken().getTaskData();
        String taskId = node.getId();
        for (TimerElement timerElement : timerElements) {
            // 流出计时环节结束计时
            if (!AlarmElement.ALARM_TIMER_END_BY_TIME_TASK.equalsIgnoreCase(timerElement.getTimeEndType())) {
                continue;
            }
            String preTaskId = taskData.getPreTaskId(taskId);
            String preTaskInstUuid = taskData.getPreTaskInstUuid(taskId);
            String taskIds = timerElement.getTaskIdsAsString();
            if (StringUtils.isNotBlank(taskIds) && StringUtils.isNotBlank(preTaskId) && StringUtils.isNotBlank(preTaskInstUuid)
                    && StringUtils.contains(taskIds, preTaskId) && !StringUtils.contains(taskIds, taskId)) {
                // 环节不会同时存在于多个计时器内，所以可以直接根据环节来停计时器
                TaskInstance taskInstance = executionContext.getToken().getTask();
                if (taskInstance == null) {
                    taskInstance = taskInstanceService.getOne(preTaskInstUuid);
                }
                timerExecutor.stopByTaskInstance(taskInstance, executionContext);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.handler.AbstractHandler#afterExecuted(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public void afterExecuted(Node node, ExecutionContext executionContext) {
        Token token = executionContext.getToken();
        TaskData taskData = token.getTaskData();
        String taskId = node.getId();
        String preTaskInstUuid = taskData.getPreTaskInstUuid(taskId);
        Integer transferCode = taskData.getTransferCode(preTaskInstUuid);
        // 是否开启审批去重功能
        if (token.getFlowDelegate().getFlow().getProperty().isEnabledAutoSubmit()) {
            AutoSubmitRuleElement autoSubmitRuleElement = token.getFlowDelegate().getFlow().getProperty().getAutoSubmitRule();
            if (autoSubmitRuleElement != null && TransferCode.Submit.getCode().equals(transferCode)
                    || TransferCode.TransferSubmit.getCode().equals(transferCode)
                    || TransferCode.DelegationSubmit.getCode().equals(transferCode)
                    || TransferCode.SkipTask.getCode().equals(transferCode)
                    || TransferCode.RollBack.getCode().equals(transferCode)
                    || TransferCode.Cancel.getCode().equals(transferCode)) {
                sameUserSubmitService.doAutoSubmit(executionContext, node, autoSubmitRuleElement);
            }
            if (autoSubmitRuleElement != null && TransferCode.Cancel.getCode().equals(transferCode)) {
                sameUserSubmitService.updateCanceledSupplementTaskName(executionContext, node, autoSubmitRuleElement);
            }
        }

        String sameUserSubmit = ((TaskNode) node).getSameUserSubmit();
        if (SameUserSubmit.NO_ACTION.equals(sameUserSubmit)) {
            return;
        }

        if (TransferCode.Submit.getCode().equals(transferCode)
                || TransferCode.TransferSubmit.getCode().equals(transferCode)
                || TransferCode.DelegationSubmit.getCode().equals(transferCode)
                || TransferCode.SkipTask.getCode().equals(transferCode)) {
            sameUserSubmitService.doSameUserSubmit(executionContext, taskId, sameUserSubmit);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.AbstractHandler#complete(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public boolean complete(Node node, ExecutionContext executionContext) {
        boolean result = taskRepository.complete(node, executionContext);

        // 发布环节执行后事件
        publishTaskAfterExecutedEvent(node, executionContext);

        // 执行事件脚本
        executeEventScript(node, executionContext, Pointcut.AFTER_EXECUTED);

        // 委托任务完成标记处理
        delegationExecutor.completeDelegation(executionContext, result);

        // 同步子流程数据
        syncSubFlowDataIfRequire(node, executionContext);

        this.dao.getSession().flush();
        this.dao.getSession().clear();
        return result;
    }

    /**
     * @param node
     * @param executionContext
     */
    private void syncSubFlowDataIfRequire(Node node, ExecutionContext executionContext) {
        FlowInstance flowInstance = executionContext.getToken().getFlowInstance();
        TaskData taskData = executionContext.getToken().getTaskData();
        flowService.syncSubFlowInstances(flowInstance, taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.AbstractHandler#leave(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void leave(Node node, ExecutionContext executionContext) {
        // 任务结束时，保存前一任务的部分数据到临时变量中
        TaskInstance taskInstance = executionContext.getToken().getTask();
        if (taskInstance != null) {
            executionContext.getToken().getTaskData().setTaskSerialNo(taskInstance.getSerialNo());
        }

        // 完成当前分支环节的处理
        boolean completeBranchTask = completeBranchTaskIfRequire(node, executionContext);

        taskRepository.storeLeave(node, executionContext);

        // 暂停任务 在timerExecutor.start中判断前流程环节处理
        // timerExecutor.pause(node, executionContext);

        // 环节到达消息通知
        List<MessageTemplate> templates = executionContext.getFlowDelegate().getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_TASK_LEAVE_NOTIFY.getType());
        MessageClientUtils.send(executionContext.getToken().getTaskData(),
                WorkFlowMessageTemplate.WF_WORK_TASK_LEAVE_NOTIFY, templates, executionContext.getToken().getTask(),
                executionContext.getToken().getFlowInstance(), Collections.EMPTY_LIST, ParticipantType.CopyUser);

        // 发布环节结束事件
        publishTaskEndEvent(node, executionContext);

        // 执行事件脚本
        executeEventScript(node, executionContext, Pointcut.COMPLETED);

        // 完成当前分支环节的处理，检测流程是要结束流程
        if (completeBranchTask) {
            completeFlowInstanceWhileBranchTaskCompletedIfRequire(node, executionContext);
        }
    }

    /**
     * @param node
     * @param executionContext
     */
    protected void publishTaskCreatedEvent(Node node, ExecutionContext executionContext) {
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
        // task#8100: 跳转环节触发环节的监听器
        // if (!WorkFlowOperation.GOTO_TASK.equals(event.getActionType())) {
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
        // }
    }

    /**
     * @param node
     * @param executionContext
     */
    private void publishTaskAfterExecutedEvent(Node node, ExecutionContext executionContext) {
        String[] listeners = node.getListeners();
        if (listeners == null || listeners.length == 0 || internalTaskListenerMap == null
                || internalTaskListenerMap.isEmpty()) {
            return;
        }
        Event event = getEvent(node, Listener.TASK, executionContext);
        // task#8100: 跳转环节触发环节的监听器
        // if (!WorkFlowOperation.GOTO_TASK.equals(event.getActionType())) {
        for (String listener : listeners) {
            InternalTaskListener taskListener = internalTaskListenerMap.get(listener);
            if (taskListener == null) {
                continue;
            }
            try {
                taskListener.onAfterExecuted(event);
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
        // }
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
        // task#8100: 跳转环节触发环节的监听器
        // if (!WorkFlowOperation.GOTO_TASK.equals(event.getActionType())) {
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
        // }
    }

    /**
     * @param node
     * @param pointcut
     * @param executionContext
     */
    private void executeEventScript(Node node, ExecutionContext executionContext, String pointcut) {
        Script script = executionContext.getFlowDelegate().getTaskEventScript(node.getId(), pointcut);
        if (script == null) {
            return;
        }
        Event event = getEvent(node, Listener.TASK, executionContext);
        WorkFlowScriptHelper.executeEventScript(event, script);
    }

    /**
     * @param node
     * @param executionContext
     */
    private void sendTaskMessage(Node node, ExecutionContext executionContext) {
        TaskData taskData = executionContext.getToken().getTaskData();
        TaskInstance taskInstance = executionContext.getToken().getTask();
        FlowInstance flowInstance = executionContext.getToken().getFlowInstance();
        // 环节到达消息通知：是否触发该环节到达消息通知，由分发条件判断
        List<MessageTemplate> arriveMsgTemplates = executionContext.getFlowDelegate().getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_TASK_ARRIVE_NOTIFY.getType());
        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_TASK_ARRIVE_NOTIFY, arriveMsgTemplates,
                taskInstance, flowInstance, Collections.EMPTY_LIST // 通过指定人员决定发送的对象
                , ParticipantType.CopyUser);

        // 环节跳转消息通知：是否触发该环节跳转消息通知，由分发条件判断
        if (taskData.isGotoTask(taskData.getPreTaskId(node.getId()))) {
            List<MessageTemplate> jumMsgTemplates = executionContext.getFlowDelegate().getMessageTemplateMap()
                    .get(WorkFlowMessageTemplate.WF_WORK_TASK_JUMP_FORWARD_NOTIFY.getType());
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_TASK_JUMP_FORWARD_NOTIFY, jumMsgTemplates,
                    taskInstance, flowInstance, Collections.EMPTY_LIST // 通过指定人员决定发送的对象
                    , ParticipantType.CopyUser);
        }
    }

}
