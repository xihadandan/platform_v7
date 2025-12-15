/*
 * @(#)2014-10-3 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.WorkFlowException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.access.TaskHistoryIdentityResolver;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.*;
import com.wellsoft.pt.bpm.engine.exception.RollbackTaskNotFoundException;
import com.wellsoft.pt.bpm.engine.exception.RollbackTaskNotSupportException;
import com.wellsoft.pt.bpm.engine.executor.*;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityItem;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityStack;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityStackFactary;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceParameterService;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.service.TaskActivityService;
import com.wellsoft.pt.bpm.engine.service.TaskBranchService;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.event.WorkTodoEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-3.1	zhulh		2014-10-3		Create
 * </pre>
 * @date 2014-10-3
 */
@Service
@Transactional
public class RollbackTaskActionExecutorImpl extends TaskActionExecutor implements RollbackTaskActionExecutor {

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private TaskBranchService taskBranchService;

    @Autowired
    private TaskHistoryIdentityResolver taskHistoryIdentityResolver;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    /**
     * 遍历获取需要删除的待办权限
     *
     * @param sourceTaskIdentityUuid
     * @param toDeleteTaskIdentities
     * @param otherAllTaskIdentities
     */
    private static void traverseDeleteTaskIdentities(String sourceTaskIdentityUuid,
                                                     List<TaskIdentity> toDeleteTaskIdentities, List<TaskIdentity> otherAllTaskIdentities) {
        if (StringUtils.isBlank(sourceTaskIdentityUuid)) {
            return;
        }

        for (TaskIdentity taskIdentity2 : otherAllTaskIdentities) {
            if (sourceTaskIdentityUuid.equals(taskIdentity2.getSourceTaskIdentityUuid())) {
                toDeleteTaskIdentities.add(taskIdentity2);

                // 递归遍历获取需要删除的待办权限
                traverseDeleteTaskIdentities(taskIdentity2.getUuid(), toDeleteTaskIdentities, otherAllTaskIdentities);
            }
        }
    }

    /**
     * 判断指定的任务是否支持回退操作
     *
     * @param flowDelegate
     * @param taskID
     */
    private static Boolean isSupportRollBack(TaskInstance task) {
        String taskId = task.getId();
        FlowDelegate flowDelegate = new FlowDelegate(task.getFlowDefinition());
        // 1.本环节的定义不允许对任务执行退回操作
        if (!flowDelegate.hasPermission(taskId, WorkFlowOperation.ROLLBACK)) {
            return false;
        }

        // 2.本环节为流程定义的第一个任务，即没有可退回、撤回的前任务
        if (flowDelegate.isFirstTaskNode(taskId)) {
            return false;
        }

        // 3.前环节为子流程任务的情况
        // List<String> nodeIDs = flowDelegate.getPreviousTaskNodes(taskId);
        // for (String nodeID : nodeIDs) {
        // if (flowDelegate.isSubTaskNode(nodeID)) {
        // return false;
        // }
        // }

        // 4.前环节分发出两个或两个以上的分支
        // for (String nodeID : nodeIDs) {
        // TaskNode node = (TaskNode) flowDelegate.getTaskNode(nodeID);
        // if (node != null && node.getForkMode() != ForkMode.SINGLE.getValue())
        // {
        // return false;
        // }
        // }

        // 5.前环节由两个或两个以上的分支合并过来
        // if (nodeIDs.size() != 1) {
        // return false;
        // }

        return true;
    }

    /**
     * 如何描述该方法
     *
     * @param activityItems
     * @param stack
     */
    private static void findAndAddOtherParallelTask(List<TaskActivityQueryItem> activityItems, TaskActivityStack stack,
                                                    List<TaskActivityItem> taskActivityItems) {
        List<TaskActivityItem> deletedItems = new ArrayList<TaskActivityItem>();
        for (TaskActivityItem item : taskActivityItems) {
            if (Boolean.TRUE.equals(item.getIsParallel())) {
                deletedItems.add(item);
            }
        }
        taskActivityItems.removeAll(deletedItems);
        // List<TaskActivityQueryItem> queryItems = new
        // ArrayList<TaskActivityQueryItem>();
        // TaskActivityItem topItem = stack.peek();
        // String preTaskInstUuid = topItem.getPreTaskInstUuid();
        // TaskActivityQueryItem preActivityQueryItem = null;
        // if (StringUtils.isNotBlank(preTaskInstUuid)) {
        // for (TaskActivityQueryItem activityItem : activityItems) {
        // if (activityItem.getTaskInstUuid().equals(preTaskInstUuid)) {
        // preActivityQueryItem = activityItem;
        // }
        // }
        // }
        //
        // if (preActivityQueryItem != null &&
        // Boolean.TRUE.equals(preActivityQueryItem.getIsParallel())) {
        // // String parallelTaskInstUuid =
        // preActivityQueryItem.getParallelTaskInstUuid();
        // // for (TaskActivityQueryItem activityItem : activityItems) {
        // // if (StringUtils.equals(parallelTaskInstUuid,
        // activityItem.getParallelTaskInstUuid())) {
        // // queryItems.add(activityItem);
        // // }
        // // }
        // TaskActivityItem item = new TaskActivityItem();
        // BeanUtils.copyProperties(preActivityQueryItem, item);
        // taskActivityItems.remove(item);
        // }

        // boolean mergeQueryItem = false;
        // for (TaskActivityQueryItem taskActivityItem : queryItems) {
        // TaskActivityItem item = new TaskActivityItem();
        // BeanUtils.copyProperties(taskActivityItem, item);
        //
        // if (!taskActivityItems.contains(item)) {
        // taskActivityItems.add(item);
        // mergeQueryItem = true;
        // }
        // }
        // if (mergeQueryItem) {
        // Collections.sort(taskActivityItems, new
        // Comparator<TaskActivityItem>() {
        // @Override
        // public int compare(TaskActivityItem o1, TaskActivityItem o2) {
        // return o1.getCreateTime().before(o2.getCreateTime()) ? 1 : -1;
        // }
        // });
        // }
    }

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

        TaskIdentity taskIdentity = param.getTaskIdentity();
        if (taskIdentity == null) {
            taskIdentity = getCurrentUserTaskIdentity(taskInstUuid, taskData.getUserDetails());
        }

        taskData.setTransferCode(taskInstUuid, TransferCode.RollBack.getCode());

        // 1、退回到前环节
        if (taskData.isRollbackToPreTask(taskInstUuid)) {
            rollbackToPreTask(taskInstance, taskData, taskIdentity);
        } else {
            // 2、退回到指定环节
            rollbackTodoSubmit(taskInstance, taskData, taskIdentity);
        }

        // 环节退回消息通知
        List<MessageTemplate> templates = new FlowDelegate(taskInstance.getFlowDefinition()).getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_TASK_RETURN_NOTIFY.getType());
        MessageClientUtils.send(taskData,
                WorkFlowMessageTemplate.WF_WORK_TASK_RETURN_NOTIFY, templates, taskInstance,
                taskInstance.getFlowInstance(), Collections.EMPTY_LIST, ParticipantType.CopyUser);

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());
    }

    /**
     * 退回到前环节
     *
     * @param taskInstance
     * @param taskData
     * @param taskIdentity
     */
    private void rollbackToPreTask(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity) {
        Integer todoType = taskIdentity.getTodoType();
        switch (todoType) {
            case 1:
            case 6:
                // 正常待办退回，一个退回，所有退回
                rollbackTodoSubmit(taskInstance, taskData, taskIdentity);
                break;
            case 2:
                // 会签待办退回，一个退回，只退一个
                rollbackTodoCounterSignSubmit(taskInstance, taskData, taskIdentity);
                break;
            case 3:
                // 转办待办退回，一个退回，所有退回
                rollbackTodoTransferSubmit(taskInstance, taskData, taskIdentity);
                break;
            case 4:
                // 移交个人待办退回
                rollbackTodoSubmit(taskInstance, taskData, taskIdentity);
                // rollbackPreHandOverTodoSubmit(taskInstance, taskData,
                // taskIdentity);
                break;
            case 5:
                // 委托待办退回，一个退回，所有退回
                rollbackTodoDelegationSubmit(taskInstance, taskData, taskIdentity);
                break;
            default:
                break;
        }
    }

    /**
     * 退回到指定环节
     *
     * @param param
     */
    private void rollbackTodoSubmit(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity) {
        String taskInstUuid = taskInstance.getUuid();

        // 1、判断环节是否支持退回
        Boolean result = isSupportRollBack(taskInstance);
        if (result.equals(Boolean.FALSE)) {
            throw new RollbackTaskNotSupportException("没有可退回的环节，无法退回!");
        }

        // 2、使用自定义按钮退回
        // taskData.setUseCustomDynamicButton(true);

        // 3、获取退回到的环节ID
        // 获取是否退回到前环节已办理的人
        String key = taskInstUuid + taskData.getUserId();
        // 操作名称
        String taskAction = taskData.getAction(key);
        String taskActionType = taskData.getActionType(key);
        String rollBackToTaskId = null;
        String rollBackToTaskInstUuid = null;
        TaskActivityItem toItem = null;
        if (taskData.isRollbackToPreTask(taskInstUuid)) {
            // 退回只有一个环节转直接退回的处理
            rollBackToTaskId = taskData.getRollbackToTaskId(taskInstUuid);
            rollBackToTaskInstUuid = taskData.getRollbackToTaskInstUuid(taskInstUuid);
            if (StringUtils.isBlank(rollBackToTaskId) || StringUtils.isBlank(rollBackToTaskInstUuid)) {
                toItem = getRollBackToTaskId(taskInstUuid);
                rollBackToTaskId = toItem.getTaskId();
                // 直接退回的环节已不存在
                FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
                if (!flowDelegate.existsTaskNode(rollBackToTaskId)) {
                    throw new WorkFlowException("直接退回的[" + toItem.getTaskName() + "]在流程定义中已删除，请使用退回功能！");
                }
            }
            if (StringUtils.isBlank(taskAction)) {
                taskAction = WorkFlowOperation.getName(WorkFlowOperation.DIRECT_ROLLBACK);
            }
            if (StringUtils.isBlank(taskActionType)) {
                taskActionType = WorkFlowOperation.DIRECT_ROLLBACK;
            }
        } else {
            if (StringUtils.isBlank(taskAction)) {
                taskAction = WorkFlowOperation.getName(WorkFlowOperation.ROLLBACK);
            }
            if (StringUtils.isBlank(taskActionType)) {
                taskActionType = WorkFlowOperation.ROLLBACK;
            }
            rollBackToTaskId = taskData.getRollbackToTaskId(taskInstUuid);
            rollBackToTaskInstUuid = taskData.getRollbackToTaskInstUuid(taskInstUuid);
        }

        if (StringUtils.isBlank(rollBackToTaskId)) {
            // 构造找不到退回到的环节并抛出异常
            Map<String, Object> variables = new HashMap<String, Object>();
            List<RollBackTask> rollbackTasks = buildToRollbackTasks(taskInstance, variables);
            if (rollbackTasks.size() == 0) {
                throw new RollbackTaskNotSupportException("退回失败！没有可退回的环节！");
            }
            // 只有一个退回环节，直接退回，不需要用户再次选择退回环节
            if (rollbackTasks.size() == 1) {
                RollBackTask rollBackTask = rollbackTasks.get(0);
                rollBackToTaskId = rollBackTask.getId();
                rollBackToTaskInstUuid = rollBackTask.getTaskInstUuid();
                // 退回到前环节
                if (StringUtils.isNotBlank(rollBackToTaskId) && StringUtils.isNotBlank(rollBackToTaskInstUuid)) {
                    taskData.setRollbackToTaskId(taskInstUuid, rollBackToTaskId);
                    taskData.setRollbackToTaskInstUuid(taskInstUuid, rollBackToTaskInstUuid);
                    if (!BooleanUtils.isTrue((Boolean) variables.get("ignoreNotRollbackTask"))) {
                        taskData.setRollbackToPreTask(taskInstUuid, true);
                    }
                    rollbackToPreTask(taskInstance, taskData, taskIdentity);
                    return;
                }
            } else {
                // 自定义操作按钮传入的可选环节ID
                Object candidateRollbackToTaskId = taskData.getCustomData(taskInstUuid + "_candidateRollbackToTaskId");
                RollBackTask rollBackTask = getRollbackTask(rollbackTasks, ObjectUtils.toString(candidateRollbackToTaskId));
                if (rollBackTask != null) {
                    rollBackToTaskId = rollBackTask.getId();
                    rollBackToTaskInstUuid = rollBackTask.getTaskInstUuid();
                } else {
                    variables.put("rollbackTasks", rollbackTasks);
                    throw new RollbackTaskNotFoundException(variables);
                }
            }
        }
        String fromTaskId = taskInstance.getId();
        String toTaskId = rollBackToTaskId;
        taskData.setToTaskId(fromTaskId, toTaskId);
        taskData.setRollbackToTaskId(taskInstUuid, toTaskId);
        taskData.setIsRollback(fromTaskId, true);
        // bug#62218 删除待办标识，避免与调用taskService.submit的逻辑冲突
        // taskData.setTaskIdentityUuid(key, null);

        // 4、获取是否退回到前环节已办理的人
        if (taskData.isRollbackToPreTask(taskInstUuid)) {
            List<String> userIds = Lists.newArrayList();
            // 退回转直接退回时取rollBackToTaskInstUuid的办理人
            String toTaskInstUuid = rollBackToTaskInstUuid;
            if (StringUtils.isNotBlank(rollBackToTaskInstUuid)) {
                userIds.addAll(getRollbackAssignees(rollBackToTaskInstUuid));
            } else {
                toTaskInstUuid = toItem.getTaskInstUuid();
                userIds.addAll(getRollbackToPreTaskAssignees(toItem));
            }
            List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuid(toTaskInstUuid).stream()
                    .filter(identity -> StringUtils.isBlank(identity.getSourceTaskIdentityUuid()) && userIds.contains(identity.getUserId())).collect(Collectors.toList());
            List<FlowUserSid> flowUserSids = flowUserJobIdentityService.getFlowUserSids(taskIdentities);
            if (CollectionUtils.isNotEmpty(flowUserSids)) {
                taskData.addTaskUserSids(toTaskId, flowUserSids);
            } else {
                Map<String, List<String>> taskUsers = Maps.newHashMap();
                taskUsers.put(toTaskId, userIds);
                taskData.setTaskUsers(taskUsers);
            }
        } else {
            // 退回的用户如果有指定，使用指定的用户
            Set<String> rollbackUsers = taskData.getTaskUsers(toTaskId);
            if (CollectionUtils.isEmpty(rollbackUsers)) {
                List<String> userIds = getRollbackAssignees(rollBackToTaskInstUuid);
                List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuid(rollBackToTaskInstUuid).stream()
                        .filter(identity -> StringUtils.isBlank(identity.getSourceTaskIdentityUuid()) && userIds.contains(identity.getUserId())).collect(Collectors.toList());
                List<FlowUserSid> flowUserSids = flowUserJobIdentityService.getFlowUserSids(taskIdentities);
                if (CollectionUtils.isNotEmpty(flowUserSids)) {
                    taskData.addTaskUserSids(toTaskId, flowUserSids);
                } else {
                    Map<String, List<String>> taskUsers = Maps.newHashMap();
                    taskUsers.put(toTaskId, userIds);
                    taskData.setTaskUsers(taskUsers);
                }
            }
        }

        // 环节决策人
        String decisionMakerName = "decisionMakers_" + toTaskId + "_" + rollBackToTaskInstUuid;
        FlowInstanceParameter parameter = flowInstanceParameterService.getByFlowInstUuidAndName(taskInstance.getFlowInstance().getUuid(), decisionMakerName);
        if (parameter != null && StringUtils.isNotBlank(parameter.getValue())) {
            if (StringUtils.startsWith(parameter.getValue(), "{")) {
                Token token = new Token(taskInstance, taskData);
                String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
                List<FlowUserSid> decisionMakerSids = Lists.newArrayList();
                Map<String, String> decisionMakerMap = JsonUtils.json2Object(parameter.getValue(), Map.class);
                decisionMakerMap.forEach((decisionMakerId, jobId) -> {
                    FlowUserSid decisionMakerSid = new FlowUserSid(decisionMakerId, decisionMakerId);
                    if (StringUtils.isNotBlank(jobId)) {
                        List<String> jobIds = Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue()));
                        List<OrgUserJobDto> orgUserJobDtos = workflowOrgService.listUserJobIdentity(decisionMakerId, orgVersionIds);
                        decisionMakerSid.setOrgUserJobDtos(orgUserJobDtos.stream().filter(orgUserJobDto -> jobIds.contains(orgUserJobDto.getJobId())).collect(Collectors.toList()));
                    }
                    decisionMakerSids.add(decisionMakerSid);
                });
                taskData.addTaskDecisionMakerSids(toTaskId, decisionMakerSids);
            } else {
                taskData.addTaskDecisionMakers(toTaskId, Arrays.asList(StringUtils.split(parameter.getValue(), Separator.SEMICOLON.getValue())));
            }
        }

        // 设置信息记录
        Map<String, Object> values = new HashMap<String, Object>(0);
        values.put("toTaskId", rollBackToTaskId);
        boolean updatedFormData = setOpinionRecords(taskInstance, taskData, taskIdentity, values);
        if (!updatedFormData && taskData.getDyFormData(taskData.getDataUuid()) != null) {
            dyFormFacade.saveFormData(taskData.getDyFormData(taskData.getDataUuid()));
        }

        Boolean isParallel = taskInstance.getIsParallel();
        if (!Boolean.TRUE.equals(isParallel)) {
            // 删除所有待办权限
            // identityService.removeTodoByTaskInstUuid(taskInstUuid);
            taskData.setAction(key, taskAction);
            taskData.setActionCode(taskInstUuid, ActionCode.ROLLBACK.getCode());
            taskData.setActionType(key, taskActionType);
            taskService.submit(taskInstance, taskData);

            // 工作退回通知
            sendRollbackMessage(taskInstance, taskInstance.getFlowInstance(), taskData, false);
        } else {
            // 并联退回
            // 1、退回到并行任务发出环节
            // String parallelTaskInstUuid =
            // taskInstance.getParallelTaskInstUuid();
            // TaskInstance parallelTaskInstance =
            // this.dao.get(TaskInstance.class, parallelTaskInstUuid);
            if (isRollbackInParallelTask(taskInstance, rollBackToTaskId)) {
                // 删除所有待办权限
                identityService.removeTodoByTaskInstUuid(taskInstUuid);
                // 2、退回到并行任务内部的环节
                taskData.setAction(key, taskAction);
                taskData.setActionCode(taskInstUuid, ActionCode.ROLLBACK.getCode());
                taskData.setActionType(key, taskActionType);
                // 分支信息
                taskData.setPreTaskProperties(toTaskId, FlowConstant.BRANCH.IS_PARALLEL, taskInstance.getIsParallel());
                taskData.setPreTaskProperties(toTaskId, FlowConstant.BRANCH.PARALLEL_TASK_INST_UUID,
                        taskInstance.getParallelTaskInstUuid());
                taskService.submit(taskInstance, taskData);

                // 工作退回通知
                sendRollbackMessage(taskInstance, taskInstance.getFlowInstance(), taskData, false);
            } else {
                // 结束并行的其他任务
                List<TaskInstance> unfinishedTasks = taskService.getUnfinishedTasks(taskInstance.getFlowInstance()
                        .getUuid());
                Date endTime = Calendar.getInstance().getTime();
                List<Map<String, Object>> parallelTaskIdentities = Lists.newArrayList();
                for (TaskInstance unfinishedTask : unfinishedTasks) {
                    if (!StringUtils.equals(unfinishedTask.getUuid(), taskInstance.getUuid())) {
                        List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuid(unfinishedTask.getUuid());
                        for (TaskIdentity parallelTaskIdentity : taskIdentities) {
                            Map<String, Object> parallelTaskIdentityMap = Maps.newHashMap();
                            parallelTaskIdentityMap.put("uuid", parallelTaskIdentity.getUuid());
                            parallelTaskIdentityMap.put("suspensionState", parallelTaskIdentity.getSuspensionState());
                            parallelTaskIdentities.add(parallelTaskIdentityMap);
                        }
                        // 删除所有待办权限
                        identityService.removeTodoByTaskInstUuid(unfinishedTask.getUuid());
                        unfinishedTask.setTodoUserId(StringUtils.EMPTY);
                        unfinishedTask.setTodoUserName(StringUtils.EMPTY);
                        unfinishedTask.setEndTime(endTime);
                        unfinishedTask.setSuspensionState(SuspensionState.SUSPEND.getState());

                        this.dao.save(unfinishedTask);
                    } else {
                        // 删除当前环节其他办理人的待办权限
                        List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuid(unfinishedTask.getUuid());
                        for (TaskIdentity identity : taskIdentities) {
                            if (!StringUtils.equals(identity.getUserId(), taskData.getUserId())) {
                                identityService.removeTodo(identity);
                            }
                        }
                    }
                }
                // 退回结束其他并行分支
                taskBranchService.rollbackBranchTaskByParallelTaskInstUuid(taskInstance.getParallelTaskInstUuid());

                taskData.setAction(key, taskAction);
                taskData.setActionCode(taskInstUuid, ActionCode.ROLLBACK.getCode());
                taskData.setActionType(key, taskActionType);
                taskService.submit(taskInstance, taskData);

                // 退回操作UUID，附加相关并行的待办标识
                String operationUuid = taskData.getOperationUuid(taskInstance.getUuid());
                if (StringUtils.isNotBlank(operationUuid)) {
                    TaskOperation taskOperation = taskOperationService.get(operationUuid);
                    String extraInfo = taskOperation.getExtraInfo();
                    if (StringUtils.isNotBlank(extraInfo)) {
                        TaskIdentity extraInfoTaskIdentity = JsonUtils.json2Object(extraInfo, TaskIdentity.class);
                        extraInfoTaskIdentity.setParallelTaskIdentities(parallelTaskIdentities);
                        taskOperation.setExtraInfo(JsonUtils.object2Json(extraInfoTaskIdentity));
                        taskOperationService.save(taskOperation);
                    }
                }

                // 工作退回通知
                sendRollbackMessage(taskInstance, taskInstance.getFlowInstance(), taskData, false);
            }
        }

    }

    /**
     * @param taskId
     * @return
     */
    private RollBackTask getRollbackTask(List<RollBackTask> rollbackTasks, String taskId) {
        if (StringUtils.isBlank(taskId) || CollectionUtils.isEmpty(rollbackTasks)) {
            return null;
        }
        for (RollBackTask rollBackTask : rollbackTasks) {
            if (StringUtils.equals(rollBackTask.getId(), taskId)) {
                return rollBackTask;
            }
        }
        return null;
    }

    /**
     * 发送工作退回通知
     *
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     */
    private void sendRollbackMessage(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData, boolean publishEvent) {
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        TaskInstance lastTaskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstance.getUuid());
        List<MessageTemplate> rollbackMessageTemplates = flowDelegate.getMessageTemplateMap().get(
                WorkFlowMessageTemplate.WF_WORK_ROLL_BACK.getType());
        List<MessageTemplate> rollbackDoneMessageTemplates = flowDelegate.getMessageTemplateMap().get(
                WorkFlowMessageTemplate.WF_WORK_ROLL_BACK_DONE.getType());
        String fromTaskId = taskInstance.getId();
        String toTaskId = taskData.getToTaskId(fromTaskId);
        Token token = taskData.getToken();
        if (StringUtils.isNotBlank(toTaskId) && token != null) {
            String toTaskName = token.getFlowDelegate().getTaskNode(toTaskId).getName();
            taskData.setCustomData("rollbackToTaskName", toTaskName);
            taskData.setCustomData("退回目标环节名称", toTaskName);
        }
        List<String> todoUserIds = null;
        // 退回消息发送办理人
        if (CollectionUtils.isNotEmpty(rollbackMessageTemplates)) {
            todoUserIds = taskService.getTodoUserIds(lastTaskInstance.getUuid());
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_ROLL_BACK, rollbackMessageTemplates,
                    taskInstance, flowInstance, todoUserIds, ParticipantType.TodoUser);
        }
        // 退回消息发送全部已办人
        List<String> doneUserIds = null;
        if (CollectionUtils.isNotEmpty(rollbackDoneMessageTemplates)) {
            doneUserIds = taskHistoryIdentityResolver.getDoneUserIds(flowInstance.getUuid(), fromTaskId, toTaskId);
            if (CollectionUtils.isNotEmpty(todoUserIds)) {
                doneUserIds.removeAll(todoUserIds);
            }
            doneUserIds.remove(taskData.getUserId());
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_ROLL_BACK, rollbackDoneMessageTemplates,
                    taskInstance, flowInstance, doneUserIds, ParticipantType.TodoUser);
        }
        if (publishEvent && CollectionUtils.isNotEmpty(todoUserIds)) {
            Set<String> msgUserIds = Sets.newHashSet(todoUserIds);
            ApplicationContextHolder.publishEvent(new WorkTodoEvent(this, msgUserIds, flowInstance, taskInstance, taskData));
        }
    }

    /**
     * 如何描述该方法
     *
     * @param param
     */
    private void rollbackTodoCounterSignSubmit(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity) {
        String taskInstUuid = taskInstance.getUuid();
        String userId = taskData.getUserId();
        String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();

        boolean isLastTaskIdentity = identityService
                .isLastTodoTaskIdentityBySourceTaskIdentityUuid(sourceTaskIdentityUuid);
        // 删除会签待办权限
        identityService.removeTodo(taskIdentity);
        // 添加已办权限
        taskService.addDonePermission(userId, taskInstUuid);

        if (isLastTaskIdentity) {
            TaskIdentity source = identityService.get(sourceTaskIdentityUuid);
            source.setSuspensionState(SuspensionState.NORMAL.getState());
            identityService.addTodo(source);
            String sourceUserId = source.getUserId();
            readMarkerService.markNew(taskInstUuid, sourceUserId);
            // 添加退回的办理人到提交结果
            Set<FlowUserSid> todoUserSids = Sets.newHashSet();
            todoUserSids.add(new FlowUserSid(sourceUserId, IdentityResolverStrategy.resolveAsName(sourceUserId)));
            taskData.getSubmitResult().addNextTaskInfo(taskInstance, todoUserSids);
        }
        // 更新环节的待办人员列表
        identityService.updateTaskIdentity(taskInstance);

        // 设置信息记录
        boolean updatedFormData = setOpinionRecords(taskInstance, taskData, taskIdentity,
                new HashMap<String, Object>(0));
        if (!updatedFormData && taskData.getDyFormData(taskData.getDataUuid()) != null) {
            dyFormFacade.saveFormData(taskData.getDyFormData(taskData.getDataUuid()));
        }

        // 记录操作
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        String key = taskInstUuid + taskData.getUserId();
        // 操作名称
        String taskAction = taskData.getAction(key);
        taskAction = StringUtils.isNotBlank(taskAction) ? taskAction : WorkFlowOperation.getName(WorkFlowOperation.ROLLBACK);
        // 操作代码
        Integer taskActionCode = taskData.getActionCode(taskInstUuid);
        taskActionCode = taskActionCode != null ? taskActionCode : ActionCode.ROLLBACK.getCode();
        // 操作类型
        String taskActionType = taskData.getActionType(key);
        taskActionType = StringUtils.isNotBlank(taskActionType) ? taskActionType : WorkFlowOperation.ROLLBACK;
        if (taskData.isRollbackToPreTask(taskInstUuid)) {
            taskOperationService.saveTaskOperation(taskAction, taskActionCode, taskActionType, null, null, null, userId, null,
                    null, taskIdentity.getUuid(), JsonUtils.object2Json(taskIdentity), taskInstance, flowInstance, taskData);
        } else {
            taskOperationService.saveTaskOperation(taskAction, taskActionCode, taskActionType, null, null, null, userId, null, null,
                    taskIdentity.getUuid(), JsonUtils.object2Json(taskIdentity), taskInstance, flowInstance, taskData);
        }

        // 工作退回通知
        sendRollbackMessage(taskInstance, flowInstance, taskData, true);
    }

    /**
     * 退回到移交前的办理状态，全部退回处理
     *
     * @param taskInstance
     * @param taskData
     * @param taskIdentity
     */
    // private void rollbackPreHandOverTodoSubmit(TaskInstance taskInstance,
    // TaskData taskData, TaskIdentity taskIdentity) {
    // String taskInstUuid = taskInstance.getUuid();
    // // 1、删除环节办理人及对应的待办权限
    // identityService.removeByTaskInstUuid(taskInstUuid);
    // List<AclSid> getAclSid = aclServiceWrapper.getSid(TaskInstance.class,
    // taskInstUuid, AclPermission.TODO);
    // for (AclSid aclSid : getAclSid) {
    // aclServiceWrapper.removePermission(TaskInstance.class, taskInstUuid,
    // AclPermission.TODO, aclSid.getSid());
    // }
    //
    // String taskOperationUuid = taskIdentity.getRelatedTaskOperationUuid();
    // TaskOperation taskOperation =
    // taskOperationService.get(taskOperationUuid);
    // String extraInfo = taskOperation.getExtraInfo();
    // List<TaskIdentity> identities =
    // HandOverTaskExecutor.jsonToTaskIdentity(extraInfo);
    // List<String> userIds = new ArrayList<String>();
    // // 恢复指定人的待办权限
    // for (TaskIdentity identity : identities) {
    // String todoUserId = identity.getUserId();
    // userIds.add(todoUserId);
    // identity.setTaskInstUuid(taskInstance.getUuid());
    // identityService.save(identity);
    //
    // aclServiceWrapper.addPermission(TaskInstance.class, taskInstUuid,
    // AclPermission.TODO, todoUserId);
    // readMarkerService.markNew(taskInstUuid, todoUserId);
    // }
    //
    // String userId = taskData.getUserId();
    // // 添加当前人已办权限
    // aclServiceWrapper.addPermission(TaskInstance.class, taskInstUuid,
    // AclPermission.DONE, userId);
    //
    // // 记录操作
    // FlowInstance flowInstance = taskInstance.getFlowInstance();
    // if (taskData.isRollbackToPreTask(taskInstUuid)) {
    // taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.DIRECT_ROLLBACK),
    // ActionCode.ROLLBACK.getCode(), WorkFlowOperation.DIRECT_ROLLBACK, null,
    // null, null, userId, null,
    // null, null, taskInstance, flowInstance, taskData);
    // } else {
    // taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.ROLLBACK),
    // ActionCode.ROLLBACK.getCode(), WorkFlowOperation.ROLLBACK, null, null,
    // null, userId, null, null,
    // null, taskInstance, flowInstance, taskData);
    // }
    // }

    /**
     * 转办待办退回，一个退回，所有退回
     *
     * @param param
     */
    private void rollbackTodoTransferSubmit(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity) {
        String taskInstUuid = taskInstance.getUuid();
        String userId = taskData.getUserId();
        String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();

        // boolean isLastTaskIdentity =
        // isLastTaskIdentityBySourceTaskIdentityUuid(sourceTaskIdentityUuid);
        // 删除转办待办权限
        identityService.removeTodo(taskIdentity);
        // 添加已办权限
        taskService.addDonePermission(userId, taskInstUuid);

        // 递归删除其他转办待办权限、包含其他转办待办又会签、转办
        List<TaskIdentity> otherAllTaskIdentities = identityService.getTodoByTaskInstUuid(taskInstUuid);
        List<TaskIdentity> toDeleteTaskIdentities = new ArrayList<TaskIdentity>();

        // 遍历获取需要删除的待办权限
        traverseDeleteTaskIdentities(sourceTaskIdentityUuid, toDeleteTaskIdentities, otherAllTaskIdentities);
        for (TaskIdentity deleteTaskIdentity : toDeleteTaskIdentities) {
            identityService.removeTodo(deleteTaskIdentity);
        }

        // if (isLastTaskIdentity) {
        TaskIdentity source = identityService.get(sourceTaskIdentityUuid);
        source.setSuspensionState(SuspensionState.NORMAL.getState());
        identityService.addTodo(source);
        String sourceUserId = source.getUserId();
        readMarkerService.markNew(taskInstUuid, sourceUserId);
        // 更新环节的待办人员列表
        identityService.updateTaskIdentity(taskInstance);
        // 添加退回的办理人到提交结果
        Set<FlowUserSid> todoUserSids = Sets.newHashSet();
        todoUserSids.add(new FlowUserSid(sourceUserId, IdentityResolverStrategy.resolveAsName(sourceUserId)));
        taskData.getSubmitResult().addNextTaskInfo(taskInstance, todoUserSids);
        // }

        // 设置信息记录
        boolean updatedFormData = setOpinionRecords(taskInstance, taskData, taskIdentity,
                new HashMap<String, Object>(0));
        if (!updatedFormData && taskData.getDyFormData(taskData.getDataUuid()) != null) {
            dyFormFacade.saveFormData(taskData.getDyFormData(taskData.getDataUuid()));
        }

        // 记录操作
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        String key = taskInstUuid + taskData.getUserId();
        // 操作名称
        String taskAction = taskData.getAction(key);
        taskAction = StringUtils.isNotBlank(taskAction) ? taskAction : WorkFlowOperation.getName(WorkFlowOperation.ROLLBACK);
        // 操作代码
        Integer taskActionCode = taskData.getActionCode(taskInstUuid);
        taskActionCode = taskActionCode != null ? taskActionCode : ActionCode.ROLLBACK.getCode();
        // 操作类型
        String taskActionType = taskData.getActionType(key);
        taskActionType = StringUtils.isNotBlank(taskActionType) ? taskActionType : WorkFlowOperation.ROLLBACK;
        if (taskData.isRollbackToPreTask(taskInstUuid)) {
            taskOperationService.saveTaskOperation(taskAction, taskActionCode, taskActionType, null, null, null, userId,
                    null, null, taskIdentity.getUuid(), JsonUtils.object2Json(taskIdentity), taskInstance, flowInstance, taskData);
        } else {
            taskOperationService.saveTaskOperation(taskAction, taskActionCode, taskActionType, null, null, null, userId, null, null,
                    taskIdentity.getUuid(), JsonUtils.object2Json(taskIdentity), taskInstance, flowInstance, taskData);
        }

        // 工作退回通知
        sendRollbackMessage(taskInstance, flowInstance, taskData, true);
    }

    /**
     * 委托待办退回，按提交、转办、会签的情况退回
     *
     * @param taskInstance
     * @param taskData
     * @param taskIdentity
     */
    private void rollbackTodoDelegationSubmit(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity) {
        String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
        if (StringUtils.isBlank(sourceTaskIdentityUuid)) {
            rollbackTodoSubmit(taskInstance, taskData, taskIdentity);
        }
        TaskIdentity sourceTaskIdentity = identityService.get(sourceTaskIdentityUuid);
        Integer sourceTodoType = sourceTaskIdentity.getTodoType();
        // 退回提交
        if (WorkFlowTodoType.Submit.equals(sourceTodoType)) {
            rollbackTodoSubmit(taskInstance, taskData, taskIdentity);
        } else if (WorkFlowTodoType.CounterSign.equals(sourceTodoType)) {
            // 退回会签
            rollbackTodoCounterSignSubmit(taskInstance, taskData, sourceTaskIdentity);
        } else if (WorkFlowTodoType.Transfer.equals(sourceTodoType)) {
            // 退回转办
            rollbackTodoCounterSignSubmit(taskInstance, taskData, sourceTaskIdentity);
        } else {
            rollbackTodoSubmit(taskInstance, taskData, taskIdentity);
        }
    }

    /**
     * 获取退回到的环节ID
     *
     * @return
     */
    private TaskActivityItem getRollBackToTaskId(String taskInstUuid) {
        // 生成任务活动堆栈
        List<TaskActivityQueryItem> taskActivities = taskActivityService.getAllActivityByTaskInstUuid(taskInstUuid);
        TaskActivityStack stack = TaskActivityStackFactary.build(taskInstUuid, taskActivities);

        // 返回要退回的环节
        return stack.getRollbackItem(taskInstUuid);
    }

    /**
     * 构造找不到退回到的环节并抛出异常
     *
     * @param taskInstance
     */
    @Override
    public List<RollBackTask> buildToRollbackTasks(TaskInstance taskInstance) {
        Map<String, Object> variables = Maps.newLinkedHashMapWithExpectedSize(0);
        return buildToRollbackTasks(taskInstance, variables);
    }

    /**
     * 构造找不到退回到的环节并抛出异常
     *
     * @param taskInstance
     */
    @Override
    public List<RollBackTask> buildToRollbackTasks(TaskInstance taskInstance, Map<String, Object> variables) {
        String parallelTaskInstUuid = taskInstance.getParallelTaskInstUuid();
        String taskInstUuid = taskInstance.getUuid();
        List<RollBackTask> rollBackTasks = new ArrayList<RollBackTask>();
        // 并行任务退回
        if (StringUtils.isNotBlank(parallelTaskInstUuid)) {
            // 并行动态分支退回
            rollBackTasks = buildBranchToRollbackTasks(taskInstance, parallelTaskInstUuid);
            return rollBackTasks;
        } else {
            // 非并行任务退回
            List<TaskActivityQueryItem> activityItems = taskActivityService.getAllActivityByTaskInstUuid(taskInstUuid);
            TaskActivityStack stack = TaskActivityStackFactary.build(taskInstUuid, activityItems);
            List<TaskActivityItem> items = stack.getAvailableToRollbackTaskActivityItems();
            // 获取前一环节是并行环节的所有并行环节
            findAndAddOtherParallelTask(activityItems, stack, items);

            Set<String> taskIdSet = new HashSet<String>();
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
            for (TaskActivityItem item : items) {
                // 流程定义中的环节已经不存在，忽略掉
                if (!flowDelegate.existsTaskNode(item.getTaskId())) {
                    continue;
                }
                if (flowDelegate.isNotRollback(item.getTaskId())) {
                    variables.put("ignoreNotRollbackTask", true);
                    continue;
                }
                Node node = flowDelegate.getTaskNode(item.getTaskId());
                // 去掉子环节、重复的环节
                if (node instanceof SubTaskNode || taskIdSet.contains(node.getId())
                        || node.getId().equals(taskInstance.getId())) {
                    continue;
                }
                taskIdSet.add(node.getId());
                rollBackTasks.add(new RollBackTask(node.getId(), node.getName(), item.getTaskInstUuid()));
            }
            return rollBackTasks;
        }
    }

    /**
     * @param currentTaskInstUuid
     * @param rollbackToTaskInstUuid
     * @param taskIdentityUuid
     * @return
     */
    @Override
    public RollBackTaskInfo getDirectRollbackTaskInfo(String currentTaskInstUuid, String rollbackToTaskInstUuid, String taskIdentityUuid) {
        String taskInstUuid = currentTaskInstUuid;
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        TaskInstance taskInstance = taskService.getTask(taskInstUuid);
        TaskIdentity taskIdentity = null;
        if (StringUtils.isNotBlank(taskIdentityUuid)) {
            taskIdentity = identityService.get(taskIdentityUuid);
        }
        if (taskIdentity == null) {
            taskIdentity = getCurrentUserTaskIdentity(taskInstUuid, userDetails);
        }

        RollBackTaskInfo rollBackTaskInfo = null;
        List<String> userIds = Lists.newArrayList();
        Integer todoType = taskIdentity.getTodoType();
        switch (todoType) {
            case 1:
                // 正常待办退回，一个退回，所有退回
            case 4:
                // 移交个人待办退回
                TaskActivityItem toItem = getRollBackToTaskId(taskInstUuid);
                userIds.addAll(getRollbackToPreTaskAssignees(toItem));
                rollBackTaskInfo = new RollBackTaskInfo(toItem.getTaskId(), toItem.getTaskName(), toItem.getTaskInstUuid());
                break;
            case 2:
                // 会签待办退回，一个退回，只退一个
            case 3:
                // 转办待办退回，一个退回，所有退回
            case 5:
                // 委托待办退回，一个退回，所有退回
                String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
                TaskIdentity source = identityService.get(sourceTaskIdentityUuid);
                if (StringUtils.equals(taskInstUuid, source.getTaskInstUuid()) && StringUtils.isBlank(rollbackToTaskInstUuid)) {
                    userIds.add(source.getUserId());
                } else {
                    if (StringUtils.isNotBlank(rollbackToTaskInstUuid)) {
                        taskInstance = taskService.getTask(rollbackToTaskInstUuid);
                    }
                    if (StringUtils.isNotBlank(taskInstance.getOwner())) {
                        userIds.addAll(Arrays.asList(StringUtils.split(taskInstance.getOwner(), Separator.SEMICOLON.getValue())));
                    } else {
                        userIds.add(taskInstance.getCreator());
                    }
                }
                rollBackTaskInfo = new RollBackTaskInfo(taskInstance.getId(), taskInstance.getName(), taskInstance.getUuid());
                break;
            default:
                break;
        }
        if (rollBackTaskInfo != null) {
            rollBackTaskInfo.setUserIds(userIds);
        }
        return rollBackTaskInfo;
    }

    /**
     * @param taskInstance
     * @param parallelTaskInstUuid
     * @return
     */
    private List<RollBackTask> buildBranchToRollbackTasks(TaskInstance taskInstance, String parallelTaskInstUuid) {
        boolean isDynamicBranchTask = taskBranchService.isDynamicBranchTask(parallelTaskInstUuid);
        List<RollBackTask> rollBackTasks = Lists.newArrayList();
        String taskInstUuid = taskInstance.getUuid();
        // 非并行任务退回
        List<TaskActivityQueryItem> activityItems = taskActivityService.getAllActivityByTaskInstUuid(taskInstUuid);
        TaskActivityStack stack = TaskActivityStackFactary.build(taskInstUuid, activityItems);
        List<TaskActivityItem> items = stack.getAvailableToRollbackTaskActivityItems();
        List<TaskActivityItem> deletedItems = new ArrayList<TaskActivityItem>();
        for (TaskActivityItem item : items) {
            // 动态分支只能退回到动态分支内办理的结点
            if (isDynamicBranchTask && !Boolean.TRUE.equals(item.getIsParallel())) {
                deletedItems.add(item);
            } else if (Boolean.TRUE.equals(item.getIsParallel())
                    && !parallelTaskInstUuid.equals(item.getParallelTaskInstUuid())) {
                deletedItems.add(item);
            }
        }
        items.removeAll(deletedItems);

        Set<String> taskIdSet = new HashSet<String>();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        for (TaskActivityItem item : items) {
            if (flowDelegate.isNotRollback(item.getTaskId())) {
                continue;
            }
            Node node = flowDelegate.getTaskNode(item.getTaskId());
            // 去掉子环节、重复的环节
            if (node instanceof SubTaskNode || taskIdSet.contains(node.getId())
                    || node.getId().equals(taskInstance.getId())) {
                continue;
            }
            taskIdSet.add(node.getId());
            rollBackTasks.add(new RollBackTask(node.getId(), node.getName(), item.getTaskInstUuid()));
        }
        return rollBackTasks;
    }

    /**
     * 获取退回到前环节已办理的人
     *
     * @return
     */
    private List<String> getRollbackToPreTaskAssignees(TaskActivityItem item) {
        List<String> userIds = new ArrayList<String>();
        Set<String> users = taskService.getTaskOwners(item.getTaskInstUuid());
        userIds.addAll(users);
        return userIds;
    }

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     * @param toTaskId
     * @return
     */
    private List<String> getRollbackAssignees(String rollBackToTaskInstUuid) {
        List<String> userIds = new ArrayList<String>();
        Set<String> users = taskService.getTaskOwners(rollBackToTaskInstUuid);
        userIds.addAll(users);
        return userIds;
    }

    /**
     * 判断退回到并行任务内部的环节
     *
     * @param taskInstance
     * @param rollBackToTaskId
     * @return
     */
    private boolean isRollbackInParallelTask(TaskInstance taskInstance, String rollBackToTaskId) {
        String parallelTaskInstUuid = taskInstance.getParallelTaskInstUuid();
        if (StringUtils.isBlank(parallelTaskInstUuid)) {
            return false;
        }

        String hql = "from TaskInstance t where t.isParallel = true and t.parallelTaskInstUuid = :parallelTaskInstUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("parallelTaskInstUuid", taskInstance.getParallelTaskInstUuid());

        List<TaskInstance> taskInstances = this.dao.query(hql, values, TaskInstance.class);
        Map<String, TaskInstance> taskInstanceMap = ConvertUtils.convertElementToMap(taskInstances, "id");
        if (!taskInstanceMap.containsKey(rollBackToTaskId)) {
            return false;
        }

        List<TaskActivity> taskActivities = taskService.getTaskActivities(taskInstance.getFlowInstance().getUuid());
        List<TaskActivity> parallelActivities = new ArrayList<TaskActivity>();
        for (TaskActivity taskActivity : taskActivities) {
            if (taskInstanceMap.containsKey(taskActivity.getTaskId())) {
                parallelActivities.add(taskActivity);
            }
        }

        Map<String, TaskActivity> parallelActivityMap = ConvertUtils.convertElementToMap(parallelActivities,
                "taskInstUuid");

        TaskActivity currentTaskActivity = parallelActivityMap.get(taskInstance.getUuid());
        if (currentTaskActivity == null) {
            return false;
        }
        String preParallelTaskInstUuid = currentTaskActivity.getPreTaskInstUuid();
        while (StringUtils.isNotBlank(preParallelTaskInstUuid)) {
            currentTaskActivity = parallelActivityMap.get(preParallelTaskInstUuid);
            if (currentTaskActivity == null) {
                break;
            }
            if (currentTaskActivity.getTaskId().equals(rollBackToTaskId)) {
                return true;
            }
            preParallelTaskInstUuid = currentTaskActivity.getPreTaskInstUuid();
        }

        return false;
    }

}
