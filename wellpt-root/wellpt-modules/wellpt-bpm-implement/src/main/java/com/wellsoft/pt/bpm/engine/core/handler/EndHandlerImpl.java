/*
 * @(#)2012-11-27 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.handler;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotParam.BotFromParam;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.FlowListener;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.Pointcut;
import com.wellsoft.pt.bpm.engine.core.Script;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.AlarmElement;
import com.wellsoft.pt.bpm.engine.element.PropertyElement;
import com.wellsoft.pt.bpm.engine.element.TimerElement;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlow;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.SuspensionState;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import com.wellsoft.pt.bpm.engine.repository.EndRepository;
import com.wellsoft.pt.bpm.engine.repository.TaskRepository;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.CustomRuntimeData;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowScriptHelper;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
public class EndHandlerImpl extends AbstractHandler implements EndHandler {

    @Autowired
    private EndRepository endRepository;

    @Autowired
    private TaskSubFlowService taskSubFlowService;

    @Autowired
    private TaskBranchService taskBranchService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    @Autowired
    private AclTaskService aclTaskService;

    @Autowired(required = false)
    private Map<String, FlowListener> listenerMap;

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BotFacadeService botFacadeService;

    @Autowired
    private SameUserSubmitService sameUserSubmitService;

    @Autowired
    private WfTaskTodoTempService taskTodoTempService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.AbstractHandler#enter(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void enter(Node node, ExecutionContext executionContext) {
        TaskData taskData = executionContext.getToken().getTaskData();
        // 环节跳转消息通知：是否触发该环节跳转消息通知，由分发条件判断
        if (taskData.isGotoTask(taskData.getPreTaskId(node.getId()))) {
            List<MessageTemplate> jumMsgTemplates = executionContext.getFlowDelegate().getMessageTemplateMap()
                    .get(WorkFlowMessageTemplate.WF_WORK_TASK_JUMP_FORWARD_NOTIFY.getType());
            Direction direction = new Direction();
            direction.setFromID(taskData.getPreTaskId(node.getId()));
            direction.setToID(node.getId());
            // direction.setId(direction.getFromID() + Separator.UNDERLINE.getValue() +
            // direction.getToID());
            MessageClientUtils.send(WorkFlowMessageTemplate.WF_WORK_TASK_JUMP_FORWARD_NOTIFY, jumMsgTemplates,
                    direction, node, executionContext.getToken(), Collections.EMPTY_LIST);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.AbstractHandler#execute(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void execute(Node node, ExecutionContext executionContext) {
        Token token = executionContext.getToken();
        TaskData taskData = token.getTaskData();
        FlowInstance flowInstance = token.getFlowInstance();
        String flowInstUuid = flowInstance.getUuid();
        // // 判断子流程是否需要合并等待
        // if (isCallBackParentTask(node, executionContext)) {
        // // 最后一个不需要等待流程提交时，存在需要等待的流程未结束，询问还有流程未结束，提示是否要合并，若是不提交该流程，否则提交该流程
        // List<TaskSubFlow> taskSubFlows =
        // taskSubFlowService.getAllByParentFlowInstUuid(flowInstance.getParent()
        // .getUuid());
        // String currentFlowInstUuid = flowInstance.getUuid();
        // // 判断当前流程实例是否需要等待
        // if (isCurrentFlowInstNeedWait(currentFlowInstUuid, taskSubFlows)) {
        // Boolean currentWait =
        // taskData.getWaitForMerge().get(currentFlowInstUuid);
        // // 判断是否存在其他流程实例需要等待
        // if (isExistsOtherFlowInstNeedWait(currentFlowInstUuid, taskSubFlows)
        // && !Boolean.FALSE.equals(currentWait)) {
        // Map<String, String> variables = new HashMap<String, String>();
        // variables.put("msg", "还有流程未结束，是否要等待合并!");
        // variables.put("subFlowInstUuid", currentFlowInstUuid);
        // variables.put("submitButtonId", taskData.getSubmitButtonId());
        // throw new SubFlowMergeException(variables);
        // }
        // }
        // }
        // executionContext.persistTask(node);
        boolean isAllTaskFinished = false;
        // 判断所有环节实例是否都完成，存在分支流独立分支先完成的情况
        if (taskService.countUnfinishedTasks(flowInstUuid) == 0) {
            isAllTaskFinished = true;
        }

        String preTaskInstUuid = taskData.getPreTaskInstUuid(node.getId());
        if (isAllTaskFinished) {
            endRepository.store(node, executionContext);

            // 激活父流程实例
            String currentFlowInstUuid = flowInstUuid;
            FlowInstance parent = flowInstance.getParent();
            if (parent != null) {
                // 子流程信息标记完成
                TaskSubFlow taskSubFlow = getTaskSubFlow(currentFlowInstUuid);
                if (!Boolean.TRUE.equals(taskSubFlow.getCompleted())) {
                    taskSubFlow.setCompleted(true);
                    taskSubFlow.setCompletionState(TaskSubFlow.STATUS_COMPLETED);
                    taskSubFlowService.save(taskSubFlow);
                }
                // 合并子流程数据到父流程
                merge2ParentFlow(taskSubFlow, flowInstance, parent, executionContext);
            }

            // 删除督办、监控权限
            if (StringUtils.isNotBlank(preTaskInstUuid)
                    && !executionContext.getFlowDelegate().isKeepRuntimePermission()) {
                aclTaskService.removePermission(preTaskInstUuid, AclPermission.SUPERVISE);
                aclTaskService.removePermission(preTaskInstUuid, AclPermission.MONITOR);
            }
            // 删除并联中的遗留权限
            if (StringUtils.isNotBlank(preTaskInstUuid)) {
                List<TaskActivityQueryItem> activityItems = taskActivityService
                        .getAllActivityByTaskInstUuid(preTaskInstUuid);
                // 过滤掉不是并行环节及并行分支环节的节点
                activityItems = filterParallelActivity(activityItems);
                // 判断是否为并行分支环节进入结束环节
                if (isAllPallelTaskOver(preTaskInstUuid, activityItems)) {
                    // 删除本分支的并行环节权限
                    for (TaskActivityQueryItem taskActivityQueryItem : activityItems) {
                        if (Boolean.TRUE.equals(taskActivityQueryItem.getIsParallel())) {
                            String entityUuid = taskActivityQueryItem.getTaskInstUuid();
                            String parallelTaskInstUuid = taskActivityQueryItem.getParallelTaskInstUuid();
                            if (preTaskInstUuid.equals(entityUuid)) {
                                aclTaskService.removePermission(parallelTaskInstUuid);
                            }
                        }
                    }
                } else {
                    for (TaskActivityQueryItem taskActivityQueryItem : activityItems) {
                        if (Boolean.TRUE.equals(taskActivityQueryItem.getIsParallel())) {
                            // 分支流未结束，忽略掉
                            if (taskActivityQueryItem.getEndTime() == null) {
                                continue;
                            }
                            String entityUuid = taskActivityQueryItem.getTaskInstUuid();
                            String parallelTaskInstUuid = taskActivityQueryItem.getParallelTaskInstUuid();
                            if (!preTaskInstUuid.equals(entityUuid)) {
                                aclTaskService.removePermission(entityUuid);
                            }
                            aclTaskService.removePermission(parallelTaskInstUuid);
                        }
                    }
                }
            }
            // 清空没有权限的数据
            aclTaskService.clearEmptyPermission(preTaskInstUuid);

            // 停止计时系统
            if (StringUtils.isNotBlank(preTaskInstUuid)) {
                TaskInstance taskInstance = taskService.get(preTaskInstUuid);
                timerExecutor.stopByTaskInstance(taskInstance, executionContext);
            }

            // 流程结束停止计时器
            stopTimerByFlowEndIfRequired(executionContext);

            // 8、流程结束消息通知
            sendMessage(node, executionContext);

            // 发布流程结束事件
            publishEvent(node, executionContext);

            // 执行事件脚本
            executeEventScript(node, executionContext, Pointcut.END);
        } else {
            // 复制独立分支的权限到其他并行分支
            if (StringUtils.isNotBlank(preTaskInstUuid)) {
                TaskRepository taskRepository = ApplicationContextHolder.getBean(TaskRepository.class);
                TaskInstance taskInstance = taskService.get(preTaskInstUuid);
                taskRepository.copyPermissions2OtherParallelTaskInstances(taskData.getUserId(), taskInstance);
            }
        }

        // 存在多个送结束的流向时，记录送结束的流向到流程参数
        String toDirectionId = taskData.getToDirectionId(taskData.getPreTaskId(node.getId()));
        if (StringUtils.isNotBlank(toDirectionId)) {
            List<Direction> directions = token.getFlowDelegate().getDirections(taskData.getPreTaskId(node.getId()));
            long endDirectionCount = directions.stream().filter(direction -> StringUtils.equals(direction.getToID(), node.getId())).count();
            if (endDirectionCount > 1) {
                FlowInstanceParameter parameter = new FlowInstanceParameter();
                parameter.setFlowInstUuid(flowInstUuid);
                parameter.setName("toDirectionId_" + taskData.getPreTaskInstUuid(node.getId()));
                parameter.setValue(toDirectionId);
                flowInstanceParameterService.save(parameter);
            }
        }
    }

    private void stopTimerByFlowEndIfRequired(ExecutionContext executionContext) {
        List<TimerElement> timerElements = executionContext.getFlowDelegate().getTimers();
        for (TimerElement timerElement : timerElements) {
            // 流出计时环节结束计时
            if (AlarmElement.ALARM_TIMER_END_BY_FLOW_END.equalsIgnoreCase(timerElement.getTimeEndType())) {
                timerExecutor.stop(executionContext.getToken().getFlowInstance());
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param activityItems
     * @return
     */
    private List<TaskActivityQueryItem> filterParallelActivity(List<TaskActivityQueryItem> activityItems) {
        List<TaskActivityQueryItem> activityQueryItems = new ArrayList<TaskActivityQueryItem>();
        for (TaskActivityQueryItem taskActivityQueryItem : activityItems) {
            if (Boolean.TRUE.equals(taskActivityQueryItem.getIsParallel())) {
                activityQueryItems.add(taskActivityQueryItem);
            }
        }

        Map<String, TaskActivityQueryItem> activityItemMap = ConvertUtils.convertElementToMap(activityItems,
                "taskInstUuid");
        List<TaskActivityQueryItem> parallelActivityItems = new ArrayList<TaskActivityQueryItem>();
        for (TaskActivityQueryItem taskActivityQueryItem : activityQueryItems) {
            String key = taskActivityQueryItem.getParallelTaskInstUuid();
            TaskActivityQueryItem item = activityItemMap.get(key);
            if (item != null && !parallelActivityItems.contains(item)) {
                parallelActivityItems.add(item);
            }
        }

        activityQueryItems.addAll(parallelActivityItems);
        return activityQueryItems;
    }

    /**
     * 判断是否为并行分支环节进入结束环节
     *
     * @param activityItems
     * @return
     */
    private boolean isAllPallelTaskOver(String taskInstUuid, List<TaskActivityQueryItem> activityItems) {
        for (TaskActivityQueryItem taskActivityQueryItem : activityItems) {
            if (taskInstUuid.equals(taskActivityQueryItem.getTaskInstUuid()) && taskActivityQueryItem.getIsParallel()
                    && taskActivityQueryItem.getEndTime() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param node
     * @param executionContext
     */
    private void sendMessage(Node node, ExecutionContext executionContext) {
        // 8、流程结束消息通知
        FlowInstance flowInstance = executionContext.getToken().getFlowInstance();
        String preTaskInstUuid = executionContext.getToken().getTaskData().getPreTaskInstUuid(node.getId());
        TaskInstance preTaskInstance = null;
        if (StringUtils.isNotBlank(preTaskInstUuid)) {
            preTaskInstance = taskService.getTask(preTaskInstUuid);
        }
        List<MessageTemplate> messageTemplates = executionContext.getToken().getFlowDelegate().getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_OVER.getType());
        // 流程结束消息通知两类人：流程发起人/指定人员
        if (CollectionUtils.isNotEmpty(messageTemplates)) {
            for (MessageTemplate template : messageTemplates) {
                MessageClientUtils.send(executionContext.getToken().getTaskData(), WorkFlowMessageTemplate.WF_WORK_OVER,
                        Lists.<MessageTemplate>newArrayList(template), preTaskInstance, flowInstance,
                        CollectionUtils.isEmpty(template.getMsgRecipients()) ? flowInstance.getStartUserId() : null,
                        ParticipantType.TodoUser);
            }
        }

        // 30、子流程办结通知其他子流程在办人员
        if (flowInstance.getParent() != null) {
            TaskSubFlow currentTaskSubFlow = taskSubFlowService.get(flowInstance.getParent().getUuid(),
                    flowInstance.getUuid());
            if (currentTaskSubFlow != null && Boolean.TRUE.equals(currentTaskSubFlow.getNotifyDoing())) {
                // 其他子流程
                List<TaskSubFlow> subFlows = taskSubFlowService.getOthersBySubFlowInstUuid(flowInstance.getUuid());
                // 未办结子流程的办理人员
                List<String> messageTodoUserIds = Lists.newArrayList();
                // 二级分发子流程
                List<TaskInstance> subTaskInstances = Lists.newArrayList();
                for (TaskSubFlow taskSubFlow : subFlows) {
                    this.dao.getSession().evict(taskSubFlow);
                    List<TaskInstance> taskInstances = taskService.getUnfinishedTasks(taskSubFlow.getFlowInstUuid());
                    for (TaskInstance taskInstance : taskInstances) {
                        if (Integer.valueOf(2).equals(taskInstance.getType())) {
                            subTaskInstances.add(taskInstance);
                        } else {
                            messageTodoUserIds.addAll(taskService.getTodoUserIds(taskInstance.getUuid()));
                        }
                    }
                }
                messageTemplates = FlowDelegateUtils.getFlowDelegate(flowInstance.getParent().getFlowDefinition())
                        .getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_NOTIFY_SUB_FLOW_DOING.getType());
                TaskData msgTaskData = executionContext.getToken().getTaskData();
                if (CollectionUtils.isNotEmpty(messageTodoUserIds)) {
                    msgTaskData.setCustomData("承办部门", currentTaskSubFlow.getTodoName());
                    msgTaskData.setCustomData("流程实例名称", flowInstance.getParent().getTitle());
                    MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_NOTIFY_SUB_FLOW_DOING,
                            messageTemplates, preTaskInstance.getParent(), flowInstance.getParent(), messageTodoUserIds,
                            ParticipantType.TodoUser);
                }
                // 二级分发子流程
                if (CollectionUtils.isNotEmpty(messageTemplates) && CollectionUtils.isNotEmpty(subTaskInstances)) {
                    for (TaskInstance subTaskInstance : subTaskInstances) {
                        msgTaskData.setCustomData("流程实例名称", subTaskInstance.getFlowInstance().getTitle());
                        messageTodoUserIds = Lists.newArrayList();
                        List<TaskInstance> subNormalTaskInstanceList = taskInstanceService
                                .getNormalByParentTaskInstUuid(subTaskInstance.getUuid());
                        for (TaskInstance subNormalTaskInstance : subNormalTaskInstanceList) {
                            messageTodoUserIds.addAll(taskService.getTodoUserIds(subNormalTaskInstance.getUuid()));
                        }
                        if (CollectionUtils.isNotEmpty(messageTodoUserIds)) {
                            MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_NOTIFY_SUB_FLOW_DOING,
                                    messageTemplates, preTaskInstance.getParent(), flowInstance.getParent(),
                                    messageTodoUserIds, ParticipantType.TodoUser);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param node
     * @param executionContext
     */
    protected void publishEvent(Node node, ExecutionContext executionContext) {
        String[] listeners = node.getListeners();
        String rtFlowListener = (String) executionContext.getToken().getTaskData()
                .getCustomData(CustomRuntimeData.KEY_FLOW_LISTENER);
        if (StringUtils.isNotBlank(rtFlowListener)) {
            listeners = (String[]) ArrayUtils.addAll(listeners, StringUtils.split(rtFlowListener, Separator.SEMICOLON.getValue()));
        }

        if (listeners == null || listeners.length == 0 || listenerMap == null || listenerMap.isEmpty()) {
            return;
        }

        Event event = getEvent(node, Listener.FLOW, executionContext);
        // task#8100: 跳转环节触发环节的监听器
        // if (!WorkFlowOperation.GOTO_TASK.equals(event.getActionType())) {
        for (String listener : listeners) {
            FlowListener flowListener = listenerMap.get(listener);
            if (flowListener == null) {
                continue;
            }
            try {
                flowListener.onEnd(event);
            } catch (Exception e) {
                String errorString = ExceptionUtils.getStackTrace(e);
                logger.error(errorString);
                if (e instanceof WorkFlowException) {
                    throw (WorkFlowException) e;
                } else {
                    throw new WorkFlowException(
                            "流程实例事件监听器" + "[" + flowListener.getName() + "]" + "执行流程结束事件处理出现异常: " + errorString);
                }
            }
        }
        // }
    }

    /**
     * @param node
     * @param executionContext
     * @param pointcut
     */
    private void executeEventScript(Node node, ExecutionContext executionContext, String pointcut) {
        Script script = executionContext.getFlowDelegate().getFlowEventScript(pointcut);
        if (script == null) {
            return;
        }
        Event event = getEvent(node, Listener.FLOW, executionContext);
        WorkFlowScriptHelper.executeEventScript(event, script);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.AbstractHandler#leave(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void leave(Node node, ExecutionContext executionContext) {
        endRepository.storeLeave(node, executionContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.handler.AbstractHandler#complete(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public boolean complete(Node node, ExecutionContext executionContext) {
        TaskData taskData = executionContext.getToken().getTaskData();
        String taskId = node.getId();
        // 并行分支独立结束时，结束分支流
        Boolean isParallel = (Boolean) taskData.getPreTaskProperties(taskId, FlowConstant.BRANCH.IS_PARALLEL);
        if (Boolean.TRUE.equals(isParallel)) {
            String preTaskInstUuid = taskData.getPreTaskInstUuid(taskId);
            taskBranchService.completeBranchTask(taskService.getTask(preTaskInstUuid));
        }

        // 子流程办结返回主流程
        if (Boolean.TRUE.equals(isCallBackParentTask(node, executionContext))) {
            Token token = executionContext.getToken();
            String subFlowId = token.getFlowInstance().getId();
            // 设置已完成的子流程实例UUID
            taskData.setCompletedSubFlowInstUuid(subFlowId, token.getFlowInstance().getUuid());
            // 设置已完成的子流程ID
            taskData.setCompletedSubFlowId(subFlowId);
            // 设置已完成的子流程动态表单定义UUID
            taskData.setCompletedSubFlowFormUuid(subFlowId, taskData.getFormUuid());
            // 设置已完成的子流程动态表单数据UUID
            taskData.setCompletedSubFlowDataUuid(subFlowId, taskData.getDataUuid());
            // 设置已完成的子流程动态表单数据
            taskData.setCompletedSubFlowFormData(subFlowId, taskData.getDyFormData(taskData.getDataUuid()));

            TaskInstance parentTask = token.getParentTask();
            parentTask = taskService.get(parentTask.getUuid());
            taskData.setFormUuid(parentTask.getFormUuid());
            taskData.setDataUuid(parentTask.getDataUuid());
            boolean isLogUserOperation = taskData.isLogUserOperation();
            taskData.setLogUserOperation(false);
            taskService.submit(parentTask, taskData);
            taskData.setLogUserOperation(isLogUserOperation);
        }

        // 后置审批，补审补办
        boolean supplement = false;
        PropertyElement propertyElement = executionContext.getFlowDelegate().getFlow().getProperty();
        if (propertyElement.isEnabledAutoSubmit() && propertyElement.getAutoSubmitRule() != null &&
                StringUtils.equals(propertyElement.getAutoSubmitRule().getMode(), "after")) {
            supplement = sameUserSubmitService.doAutoSupplement(executionContext, node, propertyElement.getAutoSubmitRule());
        }
        // 合并补审补办的权限到最后的环节
        if (!supplement) {
            sameUserSubmitService.completedSupplement(executionContext, node);
        }
        // 删除待办临时存储
        taskTodoTempService.deleteByFlowInstUuid(executionContext.getFlowInstance().getUuid());
        return true;
    }

    /**
     * 如何描述该方法
     *
     * @param flowInstance
     * @param parent
     */
    private void merge2ParentFlow(TaskSubFlow taskSubFlow, FlowInstance flowInstance, FlowInstance parentFlowInstance,
                                  ExecutionContext executionContext) {
        // 流程实例按静默的方式执行，不合并主流程
        if (executionContext.getToken().getTaskData().isSilent(flowInstance.getUuid())) {
            return;
        }

        // 合并流程数据
        // if (Boolean.TRUE.equals(taskSubFlow.getIsMerge())) {
        // // 设置返回字段
        // setReturnFields(taskSubFlow, executionContext);
        // }
        // 通过规则转换合并数据
        if (Boolean.TRUE.equals(taskSubFlow.getReturnWithOver())) {
            merge2ParentFlowByBotRule(taskSubFlow, executionContext);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    private TaskSubFlow getTaskSubFlow(String flowInstUuid) {
        List<TaskSubFlow> taskSubFlows = taskSubFlowService.getByFlowInstUuid(flowInstUuid);
        if (taskSubFlows.isEmpty()) {
            return null;
        }
        if (taskSubFlows.size() > 1) {
            throw new RuntimeException("存在多个流程实例UUID为{}的子流程实例信息!");
        }
        return taskSubFlows.get(0);
    }

    /**
     * 判断任务执行完后是否需要返回父节点或主流程
     *
     * @return
     */
    public boolean isCallBackParentTask(Node node, ExecutionContext executionContext) {
        TaskInstance parentTaskInstance = executionContext.getToken().getParentTask();
        // 父环节不存在或已办结，不需要再次回调
        if (parentTaskInstance == null) {
            return false;
        }

        // 父环节不存在或已办结，不需要再次回调
        parentTaskInstance = taskService.getTask(parentTaskInstance.getUuid());
        if (parentTaskInstance == null || parentTaskInstance.getEndTime() != null) {
            return false;
        }

        FlowInstance flowInstance = executionContext.getToken().getFlowInstance();
        String flowInstUuid = flowInstance.getUuid();

        // 流程实例按静默的方式执行，不回调主流程
        if (executionContext.getToken().getTaskData().isSilent(flowInstUuid)) {
            return false;
        }

        TaskSubFlow taskSubFlow = getTaskSubFlow(flowInstUuid);

        // 子流程不需要合并直接返回false
        if (!Boolean.TRUE.equals(taskSubFlow.getIsWait())) {
            return false;
        }
        // 判断是否存在需要等待且未完成的其他子流程，不存在返回true，进行回调主流程
        boolean otherCompleted = isExistsRequiredWaitAndUnCompletedOfOtherFlowInst(flowInstUuid);
        return !otherCompleted;
    }

    /**
     * 判断是否存在需要等待且未完成的其他子流程
     *
     * @param flowInstUuid
     * @return
     */
    private boolean isExistsRequiredWaitAndUnCompletedOfOtherFlowInst(String flowInstUuid) {
        List<TaskSubFlow> taskSubFlows = taskSubFlowService.getOthersBySubFlowInstUuid(flowInstUuid);
        for (TaskSubFlow taskSubFlow : taskSubFlows) {
            if (Boolean.TRUE.equals(taskSubFlow.getIsWait()) && !Boolean.TRUE.equals(taskSubFlow.getCompleted())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param taskSubFlow
     * @param executionContext
     */
    private void setReturnFields(TaskSubFlow taskSubFlow, ExecutionContext executionContext) {
        TaskInstance parentTaskInstance = executionContext.getToken().getParentTask();
        parentTaskInstance = taskService.getTask(parentTaskInstance.getUuid());

        TaskData taskData = executionContext.getToken().getTaskData();
        String subFlowInstUuid = taskSubFlow.getFlowInstUuid();
        String completedSubFlowFormUuid = taskData.getFormUuid();
        String completedSubFlowDataUuid = taskData.getDataUuid();
        // 1、设置表单复盖字段
        Object returnOverrideFields = taskSubFlow.getReturnOverrideFields();
        String overrideFields = returnOverrideFields == null ? "" : returnOverrideFields.toString();
        if (StringUtils.isNotBlank(overrideFields)) {
            // 主流程动态表单定义及数据UUID
            String formUuid = parentTaskInstance.getFormUuid();
            String dataUuid = parentTaskInstance.getDataUuid();

            // 获取从表返回的字段数据
            // Map<String, Object> data = new HashMap<String, Object>();
            DyFormData data = dyFormFacade.getDyFormData(formUuid, dataUuid);
            String[] fields = overrideFields.split(Separator.SEMICOLON.getValue());
            // FormAndDataBean subFormAndDataBean =
            // dytableApiFacade.getFormData(completedSubFlowFormUuid,
            // completedSubFlowDataUuid);
            DyFormData subFlowDyFormData = dyFormFacade.getDyFormData(completedSubFlowFormUuid,
                    completedSubFlowDataUuid);
            /* modified by huanglinchuan 2014.10.24 begin */
            // 取相应的主流程的对应的从表数据
            String subFormDataUuidOfParent = null, subFormUuidOfParent = null;
            DyFormData subFormDataOfParent = null;
            List<FlowInstanceParameter> paras = flowInstanceParameterService.getByFlowInstanceUuid(subFlowInstUuid);
            if (paras != null) {
                for (FlowInstanceParameter para : paras) {
                    if (para.getName().equals("parentSubFormUuid")) {
                        subFormUuidOfParent = para.getValue();
                    } else if (para.getName().equals("parentSubFormDataUuid")) {
                        subFormDataUuidOfParent = para.getValue();
                    }
                }
            }
            if (subFormDataUuidOfParent != null) {
                subFormDataOfParent = dyFormFacade.getDyFormData(subFormUuidOfParent, subFormDataUuidOfParent);
            }
            for (String fieldName : fields) {
                if (StringUtils.isNotBlank(fieldName) && fieldName.indexOf("<<") != -1) {
                    String[] fieldNames = StringUtils.split(fieldName, "<<");
                    Object retVal = subFlowDyFormData.getFieldValueByMappingName(fieldNames[1]);
                    retVal = retVal == null ? "" : retVal;
                    // data.put(fieldNames[0], retVal);
                    if (fieldNames[0].indexOf(":") != -1) {
                        // 如果是子流程字段数据需要覆盖主流程的从表
                        String[] arr = fieldNames[0].split(":");
                        if (subFormDataOfParent != null) {
                            subFormDataOfParent.setFieldValue(arr[1], retVal);
                        }
                    } else {
                        data.setFieldValue(fieldNames[0], retVal);
                    }

                    /* modified by huanglinchuan 2013.10.31 begin */
                    List<Map<String, Object>> subFormDatas = subFlowDyFormData.getFormDatas(fieldNames[1]);
                    if (subFormDatas != null && subFormDatas.size() > 0) {
                        // 拷贝整个从表数据
                        List<Map<String, Object>> newSubFormDatasForParent = new ArrayList<Map<String, Object>>();
                        for (Map<String, Object> subFormData : subFormDatas) {
                            newSubFormDatasForParent.add(subFormData);
                        }
                        data.getFormDatas().put(fieldNames[0], newSubFormDatasForParent);
                    }
                    /* modified by huanglinchuan 2013.10.31 end */
                }
            }
            // 更新主流程动态表单数据
            dataUuid = dyFormFacade.saveFormData(data);

            if (subFormDataOfParent != null) {
                // 更新主流程从表数据
                dyFormFacade.saveFormData(subFormDataOfParent);
            }
            /* modified by huanglinchuan 2014.10.24 end */
        }

        // 2、设置表单附加字段
        Object returnAdditionFields = taskSubFlow.getReturnAdditionFields();
        String additionFields = returnAdditionFields == null ? "" : returnAdditionFields.toString();
        if (StringUtils.isNotBlank(additionFields)) {
            // 主流程动态表单定义及数据UUID
            String formUuid = parentTaskInstance.getFormUuid();
            String dataUuid = parentTaskInstance.getDataUuid();
            // 获取从表返回的字段数据
            // Map<String, Object> data = new HashMap<String, Object>();
            DyFormData data = dyFormFacade.getDyFormData(formUuid, dataUuid);
            // data.put("uuid", dataUuid);
            String[] fields = additionFields.split(Separator.SEMICOLON.getValue());
            // FormAndDataBean subFormAndDataBean =
            // dytableApiFacade.getFormData(completedSubFlowFormUuid,
            // completedSubFlowDataUuid);
            DyFormData subFlowDyFormData = dyFormFacade.getDyFormData(completedSubFlowFormUuid,
                    completedSubFlowDataUuid);

            /* modified by huanglinchuan 2014.10.24 begin */
            // 取相应的主流程的对应的从表数据
            String subFormDataUuidOfParent = null, subFormUuidOfParent = null;
            DyFormData subFormDataOfParent = null;
            List<FlowInstanceParameter> paras = flowInstanceParameterService.getByFlowInstanceUuid(subFlowInstUuid);
            if (paras != null && !paras.isEmpty()) {
                for (FlowInstanceParameter para : paras) {
                    if (para.getName().equals("parentSubFormUuid")) {
                        subFormUuidOfParent = para.getValue();
                    } else if (para.getName().equals("parentSubFormDataUuid")) {
                        subFormDataUuidOfParent = para.getValue();
                    }
                }
            }
            if (subFormDataUuidOfParent != null) {
                subFormDataOfParent = dyFormFacade.getDyFormData(subFormUuidOfParent, subFormDataUuidOfParent);
            }
            for (String fieldName : fields) {
                if (StringUtils.isNotBlank(fieldName) && fieldName.indexOf("<<") != -1) {
                    String[] fieldNames = StringUtils.split(fieldName, "<<");
                    if (fieldNames[0].indexOf(":") != -1) {
                        // 子流程字段数据需要附加到主流程的从表
                        if (subFormDataOfParent != null) {
                            Object oriVal = subFormDataOfParent.getFieldValueByMappingName(fieldNames[0]);
                            Object retVal = subFlowDyFormData.getFieldValueByMappingName(fieldNames[1]);
                            if (subFormDataOfParent.isValueAsMapField(fieldNames[0])
                                    && subFlowDyFormData.isValueAsMapField(fieldNames[1])) {
                                JSONObject values = new JSONObject();
                                if (StringUtils.isNotBlank((String) oriVal)) {
                                    values.putAll(JSONObject.fromObject(oriVal));
                                }
                                if (StringUtils.isNotBlank((String) retVal)) {
                                    values.putAll(JSONObject.fromObject(retVal));
                                }
                                subFormDataOfParent.setFieldValue(fieldNames[0], values.toString());
                            } else if (oriVal != null && oriVal instanceof List && retVal != null
                                    && retVal instanceof List) {
                                List<Object> values = new ArrayList<Object>();
                                values.addAll((List) oriVal);
                                values.addAll((List) retVal);
                                subFormDataOfParent.setFieldValue(fieldNames[0], values);
                            } else {
                                oriVal = oriVal == null ? "" : oriVal + Separator.LINE.getValue();
                                retVal = retVal == null ? "" : retVal.toString();
                                subFormDataOfParent.setFieldValue(fieldNames[0], oriVal.toString() + retVal);
                            }
                        }
                    } else {
                        Object oriVal = data.getFieldValueByMappingName(fieldNames[0]);
                        Object retVal = subFlowDyFormData.getFieldValueByMappingName(fieldNames[1]);
                        if (data.isValueAsMapField(fieldNames[0])
                                && subFlowDyFormData.isValueAsMapField(fieldNames[1])) {
                            JSONObject values = new JSONObject();
                            if (StringUtils.isNotBlank((String) oriVal)) {
                                values.putAll(JSONObject.fromObject(oriVal));
                            }
                            if (StringUtils.isNotBlank((String) retVal)) {
                                values.putAll(JSONObject.fromObject(retVal));
                            }
                            data.setFieldValue(fieldNames[0], values.toString());
                        } else if (oriVal != null && oriVal instanceof List && retVal != null
                                && retVal instanceof List) {
                            List<Object> values = new ArrayList<Object>();
                            values.addAll((List) oriVal);
                            values.addAll((List) retVal);
                            data.setFieldValue(fieldNames[0], values);
                        } else {
                            oriVal = oriVal == null ? "" : oriVal + Separator.LINE.getValue();
                            retVal = retVal == null ? "" : retVal.toString();
                            data.setFieldValue(fieldNames[0], oriVal.toString() + retVal);
                        }
                    }

                    /* modified by huanglinchuan 2013.10.31 begin */
                    List<Map<String, Object>> subFormDatas = subFlowDyFormData.getFormDatas(fieldNames[1]);
                    if (subFormDatas != null && subFormDatas.size() > 0) {
                        // 附加从表数据
                        for (Map<String, Object> subFormData : subFormDatas) {
                            DyFormData copySubFormData = dyFormFacade.createDyformData(fieldNames[1]);
                            Set<Map.Entry<String, Object>> _set = subFormData.entrySet();
                            Iterator<Map.Entry<String, Object>> _it = _set.iterator();
                            while (_it.hasNext()) {
                                Map.Entry<String, Object> _entry = _it.next();
                                copySubFormData.setFieldValue(_entry.getKey(), _entry.getValue());
                            }
                            data.addSubformData(copySubFormData);
                        }
                    }
                    /* modified by huanglinchuan 2013.10.31 end */
                }
            }
            // 更新主流程动态表单数据
            dataUuid = dyFormFacade.saveFormData(data);

            if (subFormDataOfParent != null) {
                // 更新主流程从表数据
                dyFormFacade.saveFormData(subFormDataOfParent);
            }
            /* modified by huanglinchuan 2014.10.24 end */
        }

        // 当前父流程待办挂起的环节实例标记为非挂起
        TaskInstance currentParentTaskInstance = taskService
                .getLastTaskInstanceByFlowInstUuid(parentTaskInstance.getFlowInstance().getUuid());
        if (Integer.valueOf(1).equals(currentParentTaskInstance.getType())
                && currentParentTaskInstance.getEndTime() == null
                && SuspensionState.SUSPEND.getState() == currentParentTaskInstance.getSuspensionState()) {
            currentParentTaskInstance.setSuspensionState(SuspensionState.NORMAL.getState());
            taskService.save(currentParentTaskInstance);
        }
    }

    /**
     * @param taskSubFlow
     * @param executionContext
     */
    private void merge2ParentFlowByBotRule(TaskSubFlow taskSubFlow, ExecutionContext executionContext) {
        TaskData taskData = executionContext.getToken().getTaskData();
        String returnBotRuleId = taskSubFlow.getReturnBotRuleId();
        String completedSubFlowFormUuid = taskData.getFormUuid();
        String completedSubFlowDataUuid = taskData.getDataUuid();

        if (StringUtils.isBlank(returnBotRuleId)) {
            return;
        }

        FlowInstance parentFlowInstance = executionContext.getFlowInstance().getParent();
        Set<BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
        BotFromParam botFromParam = new BotFromParam(completedSubFlowDataUuid, completedSubFlowFormUuid,
                taskData.getDyFormData(completedSubFlowDataUuid));
        froms.add(botFromParam);
        BotParam botParam = new BotParam(returnBotRuleId, froms);
        botParam.setFroms(froms);
        botParam.setTargetUuid(flowInstanceService.get(parentFlowInstance.getUuid()).getDataUuid());
        try {
            botFacadeService.startBot(botParam);
        } catch (Exception e) {
            logger.error("单据转换:流程数据合并到主流程时出错！", e);
            throw new RuntimeException("流程数据合并到主流程时出错,请检查单据转换配置！", e);
        }
    }

}
