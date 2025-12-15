/*
 * @(#)2013-5-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.service.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.bpm.engine.context.listener.TimerListener;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowTimerUser;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.timer.TimerExecutor;
import com.wellsoft.pt.bpm.engine.timer.service.AbstractTaskAlarmService;
import com.wellsoft.pt.bpm.engine.timer.service.TaskAlarmHanlderService;
import com.wellsoft.pt.bpm.engine.timer.support.TimerUser;
import com.wellsoft.pt.bpm.engine.timer.support.TimingState;
import com.wellsoft.pt.bpm.engine.util.TaskTimerUtils;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.service.JobHandlerName;
import com.wellsoft.pt.xxljob.service.XxlJobService;
import com.xxl.job.core.well.model.TmpJobParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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
 * 2013-5-27.1	zhulh		2013-5-27		Create
 * </pre>
 * @date 2013-5-27
 */
@Service
@Transactional
public class TaskAlarmHanlderServiceImpl extends AbstractTaskAlarmService implements TaskAlarmHanlderService {

    @Autowired
    private TaskTimerService taskTimerService;

    @Autowired
    private TaskTimerUserService taskTimerUserService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired(required = false)
    private Map<String, TimerListener> listenerMap;

    @Autowired
    private TimerExecutor timerExecutor;

    @Autowired
    private TaskTimerLogService taskTimerLogService;
    @Autowired
    private XxlJobService xxlJobService;

    @Autowired
    private WfTaskTodoTempService taskTodoTempService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TaskAlarmHanlderService#markTaskAlarmInfo(java.lang.String)
     */
    @Override
    public boolean markTaskAlarmInfo(String taskTimerUuid) {
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
            // 更新计时器状态
            taskTimer.setAlarmDone(true);
            taskTimer.setTimingState(TimingState.ALARM);
            taskTimer.setAlarmState(1);
            taskTimerService.save(taskTimer);
            // 同步环节、流程数据
            timerExecutor.syncTaskFlowData(taskInstance, flowInstance, taskTimer);
            // 记录日志
            taskTimerLogService.log(taskTimerUuid, TaskTimerLog.TYPE_ALARM, "计时器预警！");
            // 更新用户待办临时表
            if (CollectionUtils.isNotEmpty(taskTodoTempEntities)) {
                taskTimerLogService.flushSession();
                taskInstance = taskService.get(taskTimer.getTaskInstUuid());
                if (!taskInstRecVer.equals(taskInstance.getRecVer())) {
                    taskTodoTempService.updateTaskTodoTemp(taskTodoTempEntities, taskInstance.getRecVer());
                }
            }
        } else {
            taskTimer.setAlarmDone(true);
            taskTimerService.save(taskTimer);
            // 记录日志
            taskTimerLogService.log(taskTimerUuid, TaskTimerLog.TYPE_INFO, "标记计时器信息返回false，不进行预警处理！");
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

        // 发布预警事件
        String listener = taskTimer.getListener();
        if (StringUtils.isNotBlank(listener)) {
            String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
            for (String l : listeners) {
                TimerListener timerListener = listenerMap.get(l);
                if (timerListener == null) {
                    continue;
                }
                timerListener.onTimerAlarm(taskTimer, taskInstance, taskInstance.getFlowInstance(), null);
            }
        }

        // 预警处理
        if (!Boolean.TRUE.equals(taskTimer.getEnableAlarm())) {
            return;
        }

        // 解析人员及发送消息
        sendAndStartMessageJob(taskInstance, taskTimer);

        // 发起流程
        String alarmFlowId = taskTimer.getAlarmFlowId();
        if (StringUtils.isBlank(alarmFlowId)) {
            return;
        }
        if (!Boolean.TRUE.equals(taskTimer.getAlarmFlowStarted())) {
            List<TaskTimerUser> taskTimerUsers = taskTimerUserService.getByTaskTimerUuid(taskTimerUuid);
            startAlarmFlow(taskTimer, taskTimerUsers, taskInstance, flowInstance);
            // 标记流程已经发起
            taskTimer.setAlarmFlowStarted(true);
            taskTimerService.save(taskTimer);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     * @param data
     * @param taskTimer
     */
    private void sendAndStartMessageJob(TaskInstance taskInstance, TaskTimer taskTimer) {
        int alarmTotalCount = taskTimer.getAlarmFrequency();
        if (alarmTotalCount <= 0) {
            return;
        }

        // 预警时间到达，发送第一次消息
        TaskTimerUser example = new TaskTimerUser();
        example.setTaskTimerUuid(taskTimer.getUuid());
        hanlderAndSendMessage(this.dao.findByExample(example), taskInstance);

        // 如果最多只发送一次消息直接返回
        if (alarmTotalCount <= 1) {
            return;
        }

        long repeatInterval = taskTimer.getAlarmRepeatInterval().longValue();
        int repeatCount = alarmTotalCount - 1;
        Date startTime = new Date(taskTimer.getTaskAlarmTime().getTime() + repeatInterval);
        //结束时间
        Date endTime = new Date(taskTimer.getTaskAlarmTime().getTime() + repeatInterval * repeatCount);
        List<Date> workingTimeList = DateUtils.calculationInterval(startTime, endTime, repeatCount);

        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        //xxlJob执行需要的参数
        ExecutionParam executionParam = new ExecutionParam()
                .setTenantId(tenantId)
                .setUserId(taskTimer.getCreator())
                .putKeyVal("taskTimerUuid", taskTimer.getUuid());
        String param = executionParam.toJson();
        //xxlJob定义
        TmpJobParam.Builder builder = TmpJobParam.toBuilder()
                .setJobDesc(TaskTimerUtils.getDueDoingMsgTitle(taskTimer))
                .setExecutorHandler(JobHandlerName.Temp.TaskAlarmSendRepeatMessageJob);
        //xxlJob执行时间+参数
        for (Date date : workingTimeList) {
            builder.addExecutionTimeParams(date, param);
        }
        //远程调用添加到xxlJobAdmin 并启动
        xxlJobService.addTmpStart(builder.build());
    }

    /**
     * 发起流程
     *
     * @param taskTimer
     * @param taskTimerUsers
     * @param taskInstance
     * @param flowInstance
     */
    private void startAlarmFlow(TaskTimer taskTimer, List<TaskTimerUser> taskTimerUsers, TaskInstance taskInstance,
                                FlowInstance flowInstance) {
        List<String> userIds = new ArrayList<String>();
        // 预警提醒——发起流程办理人
        List<TaskTimerUser> alarmFlowUsers = filterTaskTimerUser(taskTimerUsers, TimerUser.ALARM_FLOW_DOING);
        for (TaskTimerUser taskTimerUser : alarmFlowUsers) {
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
                    List<TaskTimerUser> otherUsers = filterTaskTimerUser(taskTimerUsers, TimerUser.ALARM_FLOW_DOING_USER);
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
            String flowDefId = taskTimer.getAlarmFlowId();
            String formUuid = flowService.getFlowDefinitionById(flowDefId).getFormUuid();
            // String dataUuid =
            // dytableApiFacade.copyFormData(taskInstance.getFormUuid(),
            // taskInstance.getDataUuid(),
            // formUuid);
            String dataUuid = dyFormFacade.copyFormData(taskInstance.getFormUuid(), taskInstance.getDataUuid(),
                    formUuid);
            flowService.startByFlowDefId(taskTimer.getAlarmFlowId(), taskTimer.getCreator(), FlowService.AUTO_SUBMIT,
                    userIds, formUuid, dataUuid);
        } else {
            logger.error("Assignee is empty for alarm flow[" + taskTimer.getAlarmFlowId() + "]");
        }
    }

}
