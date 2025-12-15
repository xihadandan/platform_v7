/*
 * @(#)2013-5-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.context.listener.TimerListener;
import com.wellsoft.pt.bpm.engine.core.BackUser;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.element.TimerElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowTimerUser;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.bpm.engine.support.SubmitResult;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.timer.TimerExecutor;
import com.wellsoft.pt.bpm.engine.timer.service.AbstractTaskOverDueService;
import com.wellsoft.pt.bpm.engine.timer.service.TaskOverDueHanlderService;
import com.wellsoft.pt.bpm.engine.timer.support.TimerDueAction;
import com.wellsoft.pt.bpm.engine.timer.support.TimerUnit;
import com.wellsoft.pt.bpm.engine.timer.support.TimerUser;
import com.wellsoft.pt.bpm.engine.timer.support.TimingState;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.bpm.engine.util.TaskTimerUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.service.JobHandlerName;
import com.wellsoft.pt.xxljob.service.XxlJobService;
import com.xxl.job.core.well.model.TmpJobParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 流程任务逾期时间到达处理服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-27.1	zhulh		2013-5-27		Create
 * </pre>
 * @date 2013-5-27
 */
@Service
@Transactional
public class TaskOverDueHanlderServiceImpl extends AbstractTaskOverDueService implements TaskOverDueHanlderService {

    @Autowired
    private TaskTimerUserService taskTimerUserService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    @Autowired
    private TsWorkTimePlanFacadeService workTimePlanFacadeService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskTimerService taskTimerService;

    @Autowired
    private TimerExecutor timerExecutor;

    @Autowired(required = false)
    private Map<String, TimerListener> listenerMap;

    @Autowired
    private WorkService workService;

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskTimerLogService taskTimerLogService;
    @Autowired
    private XxlJobService xxlJobService;

    @Autowired
    private WfTaskTodoTempService taskTodoTempService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TaskDueHanlderService#markOverDueInfo(java.lang.String)
     */
    @Override
    public boolean markOverDueInfo(String taskTimerUuid, Date overdueTime) {
        TaskTimer taskTimer = taskTimerService.get(taskTimerUuid);
        // 是否有效的计时器
        boolean isValid = isValidTaskTimer(taskTimerUuid, taskTimer);

        if (isValid) {
            // 环节实例
            TaskInstance taskInstance = taskService.get(taskTimer.getTaskInstUuid());
            Integer taskInstRecVer = taskInstance.getRecVer();
            List<WfTaskTodoTempEntity> taskTodoTempEntities = taskTodoTempService.listByTaskInstUuidAndTaskInstRecVer(taskInstance.getUuid(), taskInstRecVer);
            // 流程实例
            FlowInstance flowInstance = flowService.getFlowInstanceByTaskInstUuid(taskTimer.getTaskInstUuid());
            flowInstance.setOverdueTime(overdueTime);
            taskInstance.setOverdueTime(overdueTime);
            // 更新计时器状态
            taskTimer.setDueDoingDone(true);
            taskTimer.setTimingState(TimingState.OVER_DUE);
            taskTimer.setOverDueState(1);
            taskTimerService.save(taskTimer);
            // 同步环节、流程数据
            timerExecutor.syncTaskFlowData(taskInstance, flowInstance, taskTimer);
            // 记录日志
            taskTimerLogService.log(taskTimerUuid, TaskTimerLog.TYPE_OVER_DUE, "计时器逾期！");
            // 更新用户待办临时表
            if (CollectionUtils.isNotEmpty(taskTodoTempEntities)) {
                taskTimerLogService.flushSession();
                taskInstance = taskService.get(taskTimer.getTaskInstUuid());
                if (!taskInstRecVer.equals(taskInstance.getRecVer())) {
                    taskTodoTempService.updateTaskTodoTemp(taskTodoTempEntities, taskInstance.getRecVer());
                }
            }
        } else {
            taskTimer.setDueDoingDone(true);
            taskTimerService.save(taskTimer);
            // 记录日志
            taskTimerLogService.log(taskTimerUuid, TaskTimerLog.TYPE_INFO, "标记计时器信息返回false，不进行逾期处理！");
        }

        return isValid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.AbstractTaskTimerService#handler(java.lang.String)
     */
    @Override
    public void handler(String taskTimerUuid) {
        // 计时器
        TaskTimer taskTimer = taskTimerService.get(taskTimerUuid);
        // 环节实例
        TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
        // 流程实例
        FlowInstance flowInstance = taskInstance.getFlowInstance();

        // 发布逾期事件
        String listener = taskTimer.getListener();
        if (StringUtils.isNotBlank(listener)) {
            String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
            for (String l : listeners) {
                TimerListener timerListener = listenerMap.get(l);
                if (timerListener == null) {
                    continue;
                }
                timerListener.onTimerOverDue(taskTimer, taskInstance, flowInstance, null);
            }
        }

        // 逾期处理动作
        if (taskTimer.getDueAction() != null) {
            hanlderDueAction(taskInstance, taskTimer);
        }

        // 逾期处理
        if (!Boolean.TRUE.equals(taskTimer.getEnableDueDoing())) {
            return;
        }

        // 处理逾期消息通知
        boolean updateTimer = sendAndStartMessageJob(taskInstance, taskTimer);

        // 发起流程
        String dueFlowId = taskTimer.getDueFlowId();
        if (StringUtils.isNotBlank(dueFlowId) && !Boolean.TRUE.equals(taskTimer.getDueFlowStarted())) {
            List<TaskTimerUser> taskTimerUsers = taskTimerUserService.getByTaskTimerUuid(taskTimerUuid);
            startDueFlow(taskTimer, taskTimerUsers, taskInstance);
            // 标记流程已经发起
            taskTimer.setDueFlowStarted(true);
            taskTimerService.save(taskTimer);
        } else if (updateTimer) {
            taskTimerService.save(taskTimer);
        }
    }

    /**
     * 逾期处理动作
     *
     * @param taskInstance
     * @param taskTimer
     */
    private void hanlderDueAction(TaskInstance taskInstance, TaskTimer taskTimer) {
        Integer dueAction = taskTimer.getDueAction();
        TaskData taskData = new TaskData();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        taskData.setUserId(userId);
        taskData.setUserName(userName);
        taskData.setFormUuid(taskInstance.getFormUuid());
        taskData.setDataUuid(taskInstance.getDataUuid());
        switch (dueAction) {
            case TimerDueAction.NO_PROCESSING: // 不处理
                break;
            case TimerDueAction.TURN_OVER_TRUSTEE: // 移交给B岗人员办理
                List<String> userIds = taskService.getTodoUserIds(taskInstance.getUuid());
                Token token = new Token(taskInstance, taskData);
                List<BackUser> backUsers = token.getFlowDelegate().getBakUsers();
                List<String> backUserIds = new ArrayList<String>();
                for (String todoUserId : userIds) {
                    for (BackUser backUser : backUsers) {
                        if (todoUserId.equals(backUser.getaUserId())) {
                            backUserIds.addAll(backUser.getbUserIds());
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(backUserIds)) {
                    List<String> oldTodoUserIds1 = Lists.newArrayList();
                    oldTodoUserIds1.addAll(userIds);
                    String totOpinionText = "计时系统[" + taskTimer.getName() + "]逾期处理, 移交给B岗人员办理";
                    TaskIdentity taskIdentity = getUserDoneIdentity(userId, taskInstance.getUuid());
                    if (taskIdentity != null) {
                        taskData.setJobSelected(userId, taskIdentity.getIdentityId());
                    }
                    taskService.handOver(userId, taskInstance.getUuid(), backUserIds, null, null,
                            totOpinionText, Collections.emptyList(), false, taskData);

                    // 逾期工作移交给B岗人员办理通知
                    // 旧的办理人
                    taskData.put("oldTodoUserIds", StringUtils.join(oldTodoUserIds1, Separator.SEMICOLON.getValue()));
                    taskData.put("oldTodoUserNames", IdentityResolverStrategy.resolveAsNames(oldTodoUserIds1));
                    // 新的办理人
                    taskData.put("newTodoUserIds", StringUtils.join(backUserIds, Separator.SEMICOLON.getValue()));
                    taskData.put("newTodoUserNames", IdentityResolverStrategy.resolveAsNames(backUserIds));
                    taskData.put("limitTime", taskTimer.getTaskInitLimitTime());
                    List<String> noticeUserIds = Lists.newArrayList();
                    noticeUserIds.addAll(oldTodoUserIds1);
                    noticeUserIds.addAll(backUserIds);
                    List<MessageTemplate> messageTemplates = token.getFlowDelegate().getMessageTemplateMap()
                            .get(WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_TRUSTEE.getType());
                    MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_TRUSTEE,
                            messageTemplates, taskInstance, taskInstance.getFlowInstance(), noticeUserIds,
                            ParticipantType.TodoUser);
                }
                break;
            case TimerDueAction.TURN_OVER_SUPERVISE: // 移交给督办人员办理
                Set<String> monitorUserIds = getMonitorUserIds(taskInstance);
                if (CollectionUtils.isNotEmpty(monitorUserIds)) {
                    List<String> oldTodoUserIds2 = taskService.getTodoUserIds(taskInstance.getUuid());
                    List<String> monitorUserIdList = Lists.newArrayList();
                    monitorUserIdList.addAll(monitorUserIds);
                    String tosOpinionText = "计时系统[" + taskTimer.getName() + "]逾期处理, 移交给督办人员办理";
                    TaskIdentity taskIdentity = getUserDoneIdentity(userId, taskInstance.getUuid());
                    if (taskIdentity != null) {
                        taskData.setJobSelected(userId, taskIdentity.getIdentityId());
                    }
                    List<String> jobPaths = getMonitorJobPaths(taskInstance);
                    if (CollectionUtils.isNotEmpty(jobPaths)) {
                        Map<String, List<String>> jobPathsMap = new HashMap<>();
                        jobPathsMap.put(taskInstance.getId(), jobPaths);
                        taskData.setTaskUserJobPaths(jobPathsMap);
                    }
                    taskService.handOver(userId, taskInstance.getUuid(), monitorUserIdList, null, null, tosOpinionText,
                            Collections.emptyList(), false, taskData);

                    // 逾期工作移交给督办人员办理通知
                    // 旧的办理人
                    taskData.put("oldTodoUserIds", StringUtils.join(oldTodoUserIds2, Separator.SEMICOLON.getValue()));
                    taskData.put("oldTodoUserNames", IdentityResolverStrategy.resolveAsNames(oldTodoUserIds2));
                    // 新的办理人
                    taskData.put("newTodoUserIds", StringUtils.join(monitorUserIdList, Separator.SEMICOLON.getValue()));
                    taskData.put("newTodoUserNames", IdentityResolverStrategy.resolveAsNames(monitorUserIdList));
                    taskData.put("limitTime", taskTimer.getTaskInitLimitTime());
                    List<String> noticeUserIds = Lists.newArrayList();
                    noticeUserIds.addAll(monitorUserIdList);
                    // 通知督办人员
                    List<MessageTemplate> messageTemplates = FlowDelegateUtils
                            .getFlowDelegate(taskInstance.getFlowDefinition()).getMessageTemplateMap()
                            .get(WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_SUPERVISE.getType());
                    MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_SUPERVISE,
                            messageTemplates, taskInstance, taskInstance.getFlowInstance(), noticeUserIds,
                            ParticipantType.TodoUser);
                    noticeUserIds.clear();
                    // 通知原办理人
                    noticeUserIds.addAll(oldTodoUserIds2);
                    List<MessageTemplate> oldTodoUserMessageTemplates = FlowDelegateUtils
                            .getFlowDelegate(taskInstance.getFlowDefinition()).getMessageTemplateMap()
                            .get(WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_NOTIFY_OLD_DOING.getType());// 新版流程定义通知原办理人有对应的消息格式,如果没有则按督办人消息格式进行通知
                    MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_SUPERVISE,
                            CollectionUtils.isEmpty(oldTodoUserMessageTemplates) ? messageTemplates
                                    : oldTodoUserMessageTemplates,
                            taskInstance, taskInstance.getFlowInstance(), noticeUserIds, ParticipantType.TodoUser);

                }
                break;
            case TimerDueAction.TURN_OVER_OTHER: // 移交给其他人员办理
                TaskActivity taskActivity = taskActivityService.getByTaskInstUuid(taskTimer.getTaskInstUuid());
                String creator = taskActivity.getCreator();
                String tenantId = SpringSecurityUtils.getCurrentTenantId();
                try {
                    IgnoreLoginUtils.login(tenantId, creator);
                    taskData.setUserId(creator);
                    taskData.setUserName(SpringSecurityUtils.getCurrentUserName());
                    List<TaskTimerUser> taskTimerUsers = taskTimerUserService.getByTaskTimerUuid(taskTimer.getUuid());
                    Set<String> otherUserIds = getOtherUserIds(filterTaskTimerUser(taskTimerUsers, TimerUser.DUE_TO_USER),
                            taskInstance, taskData);
                    if (!otherUserIds.isEmpty()) {
                        List<String> oldTodoUserIds3 = taskService.getTodoUserIds(taskInstance.getUuid());
                        List<String> otherUserIdList = Lists.newArrayList();
                        otherUserIdList.addAll(otherUserIds);
                        String tooOpinionText = "计时系统[" + taskTimer.getName() + "]逾期处理, 移交给其他人员办理";
                        taskService.handOver(userId, taskInstance.getUuid(), otherUserIdList, null, null, tooOpinionText,
                                Collections.emptyList(), false);

                        // 逾期工作移交给其他人员办理通知
                        // 旧的办理人
                        taskData.put("oldTodoUserIds", StringUtils.join(oldTodoUserIds3, Separator.SEMICOLON.getValue()));
                        taskData.put("oldTodoUserNames", IdentityResolverStrategy.resolveAsNames(oldTodoUserIds3));
                        // 新的办理人
                        taskData.put("newTodoUserIds", StringUtils.join(otherUserIdList, Separator.SEMICOLON.getValue()));
                        taskData.put("newTodoUserNames", IdentityResolverStrategy.resolveAsNames(otherUserIdList));
                        taskData.put("limitTime", taskTimer.getTaskInitLimitTime());
                        List<String> noticeUserIds = Lists.newArrayList();
                        noticeUserIds.addAll(otherUserIdList);
                        List<MessageTemplate> messageTemplates = FlowDelegateUtils
                                .getFlowDelegate(taskInstance.getFlowDefinition()).getMessageTemplateMap()
                                .get(WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_OTHER.getType());
                        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_OTHER,
                                messageTemplates, taskInstance, taskInstance.getFlowInstance(), noticeUserIds,
                                ParticipantType.TodoUser);
                        //通知原办理人
                        noticeUserIds.clear();
                        noticeUserIds.addAll(oldTodoUserIds3);
                        List<MessageTemplate> oldTodoUserMessageTemplates = FlowDelegateUtils
                                .getFlowDelegate(taskInstance.getFlowDefinition()).getMessageTemplateMap()
                                .get(WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_OTHER_NOTIFY_OLD_DOING.getType());// 新版流程定义通知原办理人有对应的消息格式,如果没有则按其他人消息格式进行通知
                        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_SUPERVISE,
                                CollectionUtils.isEmpty(oldTodoUserMessageTemplates) ? messageTemplates
                                        : oldTodoUserMessageTemplates,
                                taskInstance, taskInstance.getFlowInstance(), noticeUserIds, ParticipantType.TodoUser);

                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    IgnoreLoginUtils.logout();
                }
                break;
            case TimerDueAction.ROLL_BACK: // 退回上一个办理环节
                rollbackTask(taskInstance, taskTimer);
                break;
            case TimerDueAction.AUTO_SUBMIT: // 自动进入下一个办理环节
                String toTaskId = taskTimer.getDueToTaskId();
                if (StringUtils.isNotBlank(toTaskId)) {
                    submitTask(taskInstance, taskTimer);
                }
                break;
            default:
                break;
        }
    }

    /**
     * @param taskInstance
     * @return
     */
    private List<String> getMonitorJobPaths(TaskInstance taskInstance) {
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        TaskElement taskElement = flowDelegate.getFlow().getTask(taskInstance.getId());
        List<UserUnitElement> unitElements = Lists.newArrayList();
        unitElements.addAll(taskElement.getMonitors());
        unitElements.addAll(flowDelegate.getFlowMonitors());
        return unitElements.stream().flatMap(unitElement -> {
            String valuePath = unitElement.getValuePath();
            if (StringUtils.isBlank(valuePath)) {
                return Stream.empty();
            }
            return Stream.of(StringUtils.split(valuePath, Separator.SEMICOLON.getValue()));
        }).collect(Collectors.toList());
    }

    private TaskIdentity getUserDoneIdentity(String userId, String taskInstUuid) {
        TaskActivity taskActivity = taskActivityService.getByTaskInstUuid(taskInstUuid);
        String preTaskInstUuid = null;
        if (taskActivity != null) {
            preTaskInstUuid = taskActivity.getPreTaskInstUuid();
        }
        if (StringUtils.isBlank(preTaskInstUuid)) {
            return null;
        }

        List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuidAndUserIds(preTaskInstUuid, Lists.newArrayList(userId));
        Collections.sort(taskIdentities, IdEntityComparators.CREATE_TIME_DESC);
        return CollectionUtils.isNotEmpty(taskIdentities) ? taskIdentities.get(0) : null;
    }

    /**
     * @param taskInstance
     * @param taskTimer
     */
    private void rollbackTask(TaskInstance taskInstance, TaskTimer taskTimer) {
        // 任务实例UUID
        String taskInstUuid = taskInstance.getUuid();
        String flowInstUuid = taskTimer.getFlowInstUuid();
        // String formUuid = taskInstance.getFormUuid();
        // String dataUuid = taskInstance.getDataUuid();

        // 办理意见立场
        String opinionName = "";
        // 办理意见立场
        String opinionValue = "";
        // 办理意见内容
        String opinionText = "计时系统[" + taskTimer.getName() + "]逾期处理, 退回上一个办理环节";

        // 退回上一个办理环节
        String todoUserId = taskInstance.getTodoUserId();
        if (StringUtils.isBlank(todoUserId)) {
            return;
        }
        String[] todoUserIds = StringUtils.split(todoUserId, Separator.SEMICOLON.getValue());
        if (todoUserIds.length <= 0) {
            return;
        }
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        try {
            IgnoreLoginUtils.login(tenantId, todoUserIds[0]);

            WorkBean workBean = workService.getTodo(taskInstUuid, flowInstUuid);
            workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.DIRECT_ROLLBACK));
            workBean.setActionType(WorkFlowOperation.DIRECT_ROLLBACK);
            workBean.setRollbackToPreTask(true);
            workBean.setOpinionLabel(opinionName);
            workBean.setOpinionValue(opinionValue);
            workBean.setOpinionText(opinionText);
            workBean.setDaemon(true);
            workService.rollbackWithWorkData(workBean);

            TaskData taskData = new TaskData();
            String userId = SpringSecurityUtils.getCurrentUserId();
            String userName = SpringSecurityUtils.getCurrentUserName();
            taskData.setUserId(userId);
            taskData.setUserName(userName);
            taskData.setFormUuid(taskInstance.getFormUuid());
            taskData.setDataUuid(taskInstance.getDataUuid());
            Token token = new Token(taskInstance, taskData);
            List<MessageTemplate> jumMsgTemplates = token.getFlowDelegate().getMessageTemplateMap()
                    .get(WorkFlowMessageTemplate.WF_WORK_DUE_RETRUN_PREV_TASK.getType());
            // 当前办理人
            Set<String> userIds = getTodoUserIdsByFlowInstUuid(flowInstUuid);
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_RETRUN_PREV_TASK, jumMsgTemplates,
                    taskInstance, taskInstance.getFlowInstance(), userIds, ParticipantType.TodoUser);
            // 原办理人
            String taskInstOwner = taskInstance.getOwner();
            if (StringUtils.isNotBlank(taskInstOwner)) {
                List<String> prevUserIds = Lists
                        .newArrayList(StringUtils.split(taskInstOwner, Separator.SEMICOLON.getValue()));
                List<MessageTemplate> preUserMsgTemplates = token.getFlowDelegate().getMessageTemplateMap()
                        .get(WorkFlowMessageTemplate.WF_WORK_DUE_RETRUN_PREV_TASK_NOTIFY_OLD_DOING.getType());
                MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_RETRUN_PREV_TASK_NOTIFY_OLD_DOING,
                        preUserMsgTemplates, taskInstance, taskInstance.getFlowInstance(), prevUserIds,
                        ParticipantType.TodoUser);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            taskTimerLogService.log(taskInstUuid, flowInstUuid, taskTimer, Calendar.getInstance().getTime(),
                    TaskTimerLog.TYPE_ERROR, "逾期处理——退回上一个办理环节时异常！");
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    /**
     * @param flowInstUuid
     * @return
     */
    private Set<String> getTodoUserIdsByFlowInstUuid(String flowInstUuid) {
        Set<String> userIds = Sets.newLinkedHashSet();
        List<String> taskInstUuids = taskService.getUnfinishedTaskInstanceUuids(flowInstUuid);
        for (String taskInstUuid : taskInstUuids) {
            userIds.addAll(taskService.getTodoUserIds(taskInstUuid));
        }
        return userIds;
    }

    /**
     * @param taskInstance
     * @param taskTimer
     */
    private void submitTask(TaskInstance taskInstance, TaskTimer taskTimer) {
        // 任务实例UUID
        String taskInstUuid = taskInstance.getUuid();
        String flowInstUuid = taskTimer.getFlowInstUuid();
        // String formUuid = taskInstance.getFormUuid();
        // String dataUuid = taskInstance.getDataUuid();
        // String fromTaskId = taskInstance.getId();
        // 目标环节ID
        String toTaskId = taskTimer.getDueToTaskId();

        // 办理意见立场
        String opinionName = "";
        // 办理意见立场
        String opinionValue = "";
        // 办理意见内容
        String opinionText = "计时系统[" + taskTimer.getName() + "]逾期处理, 进入下一环节";

        // 进入下一环节
        String todoUserId = taskInstance.getTodoUserId();
        if (StringUtils.isBlank(todoUserId)) {
            return;
        }
        String[] todoUserIds = StringUtils.split(todoUserId, Separator.SEMICOLON.getValue());
        if (todoUserIds.length <= 0) {
            return;
        }
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        try {
            IgnoreLoginUtils.login(tenantId, todoUserIds[0]);

            WorkBean workBean = workService.getTodo(taskInstUuid, flowInstUuid);
            workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.SUBMIT));
            workBean.setActionType(WorkFlowOperation.SUBMIT);
            workBean.setActionCode(ActionCode.SUBMIT.getCode());
            workBean.setOpinionLabel(opinionName);
            workBean.setOpinionValue(opinionValue);
            workBean.setOpinionText(opinionText);
            workBean.setToTaskId(toTaskId);
            workBean.setGotoTask(true);
            workBean.setGotoTaskId(toTaskId);
            workBean.setDaemon(true);
            ResultMessage resultMessage = workService.gotoTask(workBean);

            // 逾期自动提交下一个环节消息提醒，发送给原办理人与下个环节的办理人
            SubmitResult result = (SubmitResult) resultMessage.getData();
            Set<String> nextFlowUsers = Sets.newHashSet();
            if (!MapUtils.isEmpty(result.getTaskTodoUsers())) { // 下一个环节办理人
                Collection<Map<String, String>> flowUserSids = result.getTaskTodoUsers().values();
                if (!CollectionUtils.isEmpty(flowUserSids)) {
                    for (Map<String, String> flowUser : flowUserSids) {
                        nextFlowUsers.addAll(flowUser.keySet());
                    }
                }
            }
            TaskData taskData = new TaskData();
            String userId = SpringSecurityUtils.getCurrentUserId();
            String userName = SpringSecurityUtils.getCurrentUserName();
            taskData.setUserId(userId);
            taskData.setUserName(userName);
            taskData.setFormUuid(taskInstance.getFormUuid());
            taskData.setDataUuid(taskInstance.getDataUuid());
            Token token = new Token(taskInstance, taskData);
            List<MessageTemplate> jumMsgTemplates = token.getFlowDelegate().getMessageTemplateMap()
                    .get(WorkFlowMessageTemplate.WF_WORK_DUE_ENTER_NEXT_TASK.getType());
            // 当前办理人
            Set<String> userIds = Sets.newHashSet(nextFlowUsers);
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_ENTER_NEXT_TASK, jumMsgTemplates,
                    taskInstance, taskInstance.getFlowInstance(), userIds, ParticipantType.TodoUser);
            // 原办理人
            String taskInstOwner = taskInstance.getOwner();
            if (StringUtils.isNotBlank(taskInstOwner)) {
                List<String> prevUserIds = Lists
                        .newArrayList(StringUtils.split(taskInstOwner, Separator.SEMICOLON.getValue()));
                List<MessageTemplate> prevUserMsgTemplates = token.getFlowDelegate().getMessageTemplateMap()
                        .get(WorkFlowMessageTemplate.WF_WORK_DUE_ENTER_NEXT_TASK_NOTIFY_OLD_DOING.getType());
                MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_ENTER_NEXT_TASK_NOTIFY_OLD_DOING,
                        prevUserMsgTemplates, taskInstance, taskInstance.getFlowInstance(), prevUserIds,
                        ParticipantType.TodoUser);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            taskTimerLogService.log(taskInstUuid, flowInstUuid, taskTimer, Calendar.getInstance().getTime(),
                    TaskTimerLog.TYPE_ERROR, "逾期处理——自动进入下一个办理环节时异常！");
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    /**
     * 如何描述该方法
     *
     * @param taskTimer
     */
    private boolean sendAndStartMessageJob(TaskInstance taskInstance, TaskTimer taskTimer) {
        Map<String, Object> keepData = Maps.newHashMap();
        String dueTime = getDueTime(taskInstance, taskTimer, keepData);
        if (StringUtils.isBlank(dueTime)) {
            return false;
        }
        Integer dueUnit = getDueUnit(taskInstance, taskTimer, keepData);
        Integer dueFrequency = getDueFrequency(taskInstance, taskTimer, keepData);
        if (dueUnit == null || dueFrequency == null) {
            return false;
        }

        Double dueRepeatInterval = Double.valueOf(dueTime);
        Integer dueTotalCount = dueFrequency;// taskTimer.getDueFrequency();
        if (dueRepeatInterval == null || dueRepeatInterval <= 0 && dueTotalCount == null || dueTotalCount <= 0) {
            return false;
        }

        taskTimer.setDueTime(dueTime);
        taskTimer.setDueUnit(dueUnit);
        taskTimer.setDueFrequency(dueFrequency);

        // 逾期时间到达，发送第一次消息
        TaskTimerUser example = new TaskTimerUser();
        example.setTaskTimerUuid(taskTimer.getUuid());
        hanlderAndSendMessage(this.dao.findByExample(example), taskInstance);

        // 如果最多只发送一次消息直接返回
        if (dueTotalCount <= 1) {
            return true;
        }
        Date taskDueTime = taskTimer.getTaskDueTime();
        // Integer dueUnit = taskTimer.getDueUnit();

//        if (dueUnit == null) {
//            logger.error("Unknow dueUnit " + dueUnit);
//            return;
//        }

        List<Date> workingTimeList = new ArrayList<>();
        // 任务逾期处理，消息通知
        if (dueUnit.equals(TimerUnit.WORKING_DAY) || dueUnit.equals(TimerUnit.WORKING_HOUR)
                || dueUnit.equals(TimerUnit.WORKING_MINUTE)) {
            // 任务逾期处理工作时间计算
            workingTimeList = calculateWorkingTime(dueRepeatInterval, dueUnit, dueTotalCount, taskDueTime, taskTimer);
        } else if (dueUnit.equals(TimerUnit.DAY) || dueUnit.equals(TimerUnit.HOUR)
                || dueUnit.equals(TimerUnit.MINUTE)) {
            long repeatInterval = calculateRepeatInterval(dueRepeatInterval, dueUnit);
            //执行次数
            int repeatCount = dueTotalCount - 1;
            //开始时间
            Date startTime = new Date(taskDueTime.getTime() + repeatInterval);
            //结束时间
            Date endTime = new Date(taskDueTime.getTime() + repeatInterval * repeatCount);
            workingTimeList = DateUtils.calculationInterval(startTime, endTime, repeatCount);
        }

        if (workingTimeList.size() == 0) {
            logger.error("Unknow dueUnit " + dueUnit);
            return false;
        }
        //xxlJob执行需要的参数
        ExecutionParam executionParam = new ExecutionParam().setTenantId(SpringSecurityUtils.getCurrentTenantId())
                .setUserId(taskTimer.getCreator()).putKeyVal("taskTimerUuid", taskTimer.getUuid());
        String param = executionParam.toJson();
        //xxlJob定义
        TmpJobParam.Builder builder = TmpJobParam.toBuilder().setJobDesc(TaskTimerUtils.getDueDoingMsgTitle(taskTimer))
                .setExecutorHandler(JobHandlerName.Temp.TaskOverDueSendRepeatMessageJob);
        //xxlJob执行时间+参数
        for (Date date : workingTimeList) {
            builder.addExecutionTimeParams(date, param);
        }
        //远程调用添加到xxlJobAdmin 并启动
        xxlJobService.addTmpStart(builder.build());
        return true;
    }


    private Integer getDueFrequency(TaskInstance taskInstance, TaskTimer taskTimer, Map<String, Object> keepData) {
        if (taskTimer.getDueFrequency() != null) {
            return taskTimer.getDueFrequency();
        }

        TimerElement timerElement = getTimerElement(taskInstance, taskTimer, keepData);
        if (timerElement == null) {
            return taskTimer.getDueFrequency();
        }
        if (!StringUtils.equals("2", timerElement.getDueFrequencyType())) {
            return taskTimer.getDueFrequency();
        }
        DyFormData dyFormData = getDyformData(taskInstance, taskTimer, keepData);
        return Integer.valueOf(StringUtils.trim(Objects.toString(dyFormData.getFieldValue(timerElement.getDueFrequency()), StringUtils.EMPTY)));
    }

    private Integer getDueUnit(TaskInstance taskInstance, TaskTimer taskTimer, Map<String, Object> keepData) {
        if (taskTimer.getDueUnit() != null) {
            return taskTimer.getDueUnit();
        }

        TimerElement timerElement = getTimerElement(taskInstance, taskTimer, keepData);
        if (timerElement == null) {
            return taskTimer.getDueUnit();
        }
        if (!StringUtils.equals("2", timerElement.getDueUnitType())) {
            return taskTimer.getDueUnit();
        }
        DyFormData dyFormData = getDyformData(taskInstance, taskTimer, keepData);
        return Integer.valueOf(StringUtils.trim(Objects.toString(dyFormData.getFieldValue(timerElement.getDueUnit()), StringUtils.EMPTY)));
    }

    private String getDueTime(TaskInstance taskInstance, TaskTimer taskTimer, Map<String, Object> keepData) {
        if (NumberUtils.isNumber(taskTimer.getDueTime())) {
            return taskTimer.getDueTime();
        }

        TimerElement timerElement = getTimerElement(taskInstance, taskTimer, keepData);
        if (timerElement == null) {
            return taskTimer.getDueTime();
        }
        if (!StringUtils.equals("2", timerElement.getDueTimeType())) {
            return taskTimer.getDueTime();
        }
        DyFormData dyFormData = getDyformData(taskInstance, taskTimer, keepData);
        return StringUtils.trim(Objects.toString(dyFormData.getFieldValue(timerElement.getDueTime()), StringUtils.EMPTY));
    }

    private TimerElement getTimerElement(TaskInstance taskInstance, TaskTimer taskTimer, Map<String, Object> keepData) {
        FlowDelegate flowDelegate = (FlowDelegate) keepData.get("flowDelegate");
        if (flowDelegate == null) {
            TaskInstance task = taskInstance;
            if (task == null) {
                task = taskService.get(taskTimer.getTaskInstUuid());
            }
            flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
            keepData.put("flowDelegate", flowDelegate);
        }
        TimerElement timerElement = flowDelegate.getTimers().stream()
                .filter(timer -> StringUtils.equals(timer.getTimerId(), taskTimer.getId()))
                .findFirst().orElse(null);
        if (timerElement == null) {
            timerElement = flowDelegate.getTimers().stream()
                    .filter(timer -> StringUtils.equals(timer.getName(), taskTimer.getName()))
                    .findFirst().orElse(null);
        }
        return timerElement;
    }

    private DyFormData getDyformData(TaskInstance taskInstance, TaskTimer taskTimer, Map<String, Object> keepData) {
        DyFormData dyFormData = (DyFormData) keepData.get("dyFormData");
        if (dyFormData == null) {
            TaskInstance task = taskInstance;
            if (task == null) {
                task = taskService.get(taskTimer.getTaskInstUuid());
            }
            dyFormData = dyFormFacade.getDyFormData(task.getFormUuid(), task.getDataUuid());
            keepData.put("dyFormData", dyFormData);
        }
        return dyFormData;
    }

    /**
     * @param dueTime
     * @param dueUnit
     * @param dueTotalCount
     * @param taskDueTime
     * @return
     */
    private List<Date> calculateWorkingTime(Double dueTime, Integer dueUnit, int dueTotalCount, Date taskDueTime,
                                            TaskTimer taskTimer) {
        List<Date> workingTimes = new ArrayList<Date>();
        String workTimePlanUuid = taskTimer.getWorkTimePlanUuid();
        String workTimePlanId = taskTimer.getWorkTimePlanId();
        workTimePlanUuid = workTimePlanFacadeService.getActiveWorkTimePlanUuidById(workTimePlanId, workTimePlanUuid);
        if (StringUtils.isNotBlank(workTimePlanUuid)) {
            for (int index = 1; index < dueTotalCount; index++) {
                Double amount = dueTime * index;
                switch (dueUnit) {
                    case TimerUnit.WORKING_DAY:
                        workingTimes.add(workTimePlanFacadeService.getWorkDate(workTimePlanUuid, taskDueTime,
                                amount, WorkUnit.WorkingDay));
                        break;
                    case TimerUnit.WORKING_HOUR:
                        workingTimes.add(workTimePlanFacadeService.getWorkDate(workTimePlanUuid, taskDueTime,
                                amount, WorkUnit.WorkingHour));
                        break;
                    case TimerUnit.WORKING_MINUTE:
                        workingTimes.add(workTimePlanFacadeService.getWorkDate(workTimePlanUuid, taskDueTime,
                                amount, WorkUnit.WorkingMinute));
                        break;
                    default:
                        break;
                }
            }
        } else {
            for (int index = 1; index < dueTotalCount; index++) {
                Double amount = dueTime * index;
                switch (dueUnit) {
                    case TimerUnit.WORKING_DAY:
                        workingTimes.add(basicDataApiFacade.getWorkDate(taskDueTime, amount, WorkUnit.WorkingDay));
                        break;
                    case TimerUnit.WORKING_HOUR:
                        workingTimes.add(basicDataApiFacade.getWorkDate(taskDueTime, amount, WorkUnit.WorkingHour));
                        break;
                    case TimerUnit.WORKING_MINUTE:
                        workingTimes.add(basicDataApiFacade.getWorkDate(taskDueTime, amount, WorkUnit.WorkingMinute));
                        break;
                    default:
                        break;
                }
            }
        }
        return workingTimes;
    }

    /**
     * @param dueRepeatInterval
     * @param dueUnit
     * @return
     */
    private long calculateRepeatInterval(Double dueRepeatInterval, Integer dueUnit) {
        long repeatInterval = 0;
        switch (dueUnit) {
            case TimerUnit.DAY:
                repeatInterval = Double.valueOf(dueRepeatInterval * dueUnit * 1000l).longValue();
                break;
            case TimerUnit.HOUR:
                repeatInterval = Double.valueOf(dueRepeatInterval * dueUnit * 1000l).longValue();
                break;
            case TimerUnit.MINUTE:
                repeatInterval = Double.valueOf(dueRepeatInterval * dueUnit * 1000l).longValue();
                break;
            default:
                break;
        }
        return repeatInterval;
    }

    /**
     * 发起逾期处理流程
     *
     * @param taskTimer
     * @param taskTimerUsers
     * @param taskInstance
     * @param flowInstance
     */
    private void startDueFlow(TaskTimer taskTimer, List<TaskTimerUser> taskTimerUsers, TaskInstance taskInstance) {
        List<String> userIds = new ArrayList<String>();
        // 逾期处理——发起流程办理人
        List<TaskTimerUser> dueFlowUsers = filterTaskTimerUser(taskTimerUsers, TimerUser.DUE_FLOW_DOING);
        for (TaskTimerUser taskTimerUser : dueFlowUsers) {
            String value = taskTimerUser.getValue();
            WorkFlowTimerUser alertFlowUser = Enum.valueOf(WorkFlowTimerUser.class, value.trim());
            switch (alertFlowUser) {
                case Doing: // 在办人员
                    userIds.addAll(getDoingUserIds(taskInstance));
                    break;
                case Monitor: // 督办人员
                    userIds.addAll(getMonitorUserIds(taskInstance));
                    break;
                case Tracer: // 跟踪人员
                    userIds.addAll(getTracerUserIds(taskInstance));
                    break;
                case Admin: // 流程管理人员
                    userIds.addAll(getAdminUserIds(taskInstance));
                    break;
                case Other: // 其他人员
                    List<TaskTimerUser> otherUsers = filterTaskTimerUser(taskTimerUsers, TimerUser.DUE_FLOW_DOING_USER);
                    TaskData taskData = new TaskData();
                    taskData.setFormUuid(taskInstance.getFormUuid());
                    taskData.setDataUuid(taskInstance.getDataUuid());
                    userIds.addAll(getOtherUserIds(otherUsers, taskInstance, taskData));
                    break;
                default:
                    break;
            }
        }

        if (!userIds.isEmpty()) {
            // 启动流程
            String flowDefId = taskTimer.getDueFlowId();
            String formUuid = flowService.getFlowDefinitionById(flowDefId).getFormUuid();
            // String dataUuid =
            // dytableApiFacade.copyFormData(taskInstance.getFormUuid(),
            // taskInstance.getDataUuid(),
            // formUuid);
            String dataUuid = dyFormFacade.copyFormData(taskInstance.getFormUuid(), taskInstance.getDataUuid(),
                    formUuid);
            flowService.startByFlowDefId(flowDefId, taskTimer.getCreator(), FlowService.AUTO_SUBMIT, userIds, formUuid,
                    dataUuid);
        } else {
            logger.error("Assignee is empty for due flow[" + taskTimer.getDueFlowId() + "]");
        }
    }

}
