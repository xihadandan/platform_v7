/*
 * @(#)2020年9月25日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.bpm.engine.context.listener.InternalListener;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.timer.TimerExecutor;
import com.wellsoft.pt.bpm.engine.timer.service.TaskAlarmHanlderService;
import com.wellsoft.pt.bpm.engine.timer.service.TaskDueHanlderService;
import com.wellsoft.pt.bpm.engine.timer.service.TaskOverDueHanlderService;
import com.wellsoft.pt.bpm.engine.timer.support.TimingState;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Description: 主流程子流程环节按子流程计时器计时监听处理
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年9月25日.1	zhulh		2020年9月25日		Create
 * </pre>
 * @date 2020年9月25日
 */
@Component
public class SubTaskTimerRefNewFlowTimerListener extends TimerListenerAdapter implements InternalListener {

    @Autowired
    private TaskSubFlowService taskSubFlowService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private TimerExecutor timerExecutor;

    @Autowired
    private TaskTimerService taskTimerService;

    @Autowired
    private TaskTimerLogService taskTimerLogService;

    @Autowired
    private TaskAlarmHanlderService taskAlarmHanlderService;

    @Autowired
    private TaskDueHanlderService taskDueHanlderService;

    @Autowired
    private TaskOverDueHanlderService taskOverDueHanlderService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TimerListenerAdapter#getName()
     */
    @Override
    public String getName() {
        return "主流程子流程环节按子流程计时器计时监听器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TimerListenerAdapter#onTimerStarted(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void onTimerStarted(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                               TaskData taskData) {
        // 处理引用子流程计时器
        handleRefNewFlowTimer(taskTimer, taskInstance, flowInstance, taskData, TaskTimerLog.TYPE_START);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TimerListenerAdapter#onTimerPause(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void onTimerPause(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                             TaskData taskData) {
        // 处理引用子流程计时器
        handleRefNewFlowTimer(taskTimer, taskInstance, flowInstance, taskData, TaskTimerLog.TYPE_PAUSE);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TimerListenerAdapter#onTimerRestarted(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void onTimerRestarted(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                                 TaskData taskData) {
        // 处理引用子流程计时器
        handleRefNewFlowTimer(taskTimer, taskInstance, flowInstance, taskData, TaskTimerLog.TYPE_RESUME);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TimerListenerAdapter#onTimerAlarm(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void onTimerAlarm(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                             TaskData taskData) {
        // 处理引用子流程计时器
        handleRefNewFlowTimer(taskTimer, taskInstance, flowInstance, taskData, TaskTimerLog.TYPE_ALARM);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TimerListenerAdapter#onTimerDue(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void onTimerDue(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        // 处理引用子流程计时器
        handleRefNewFlowTimer(taskTimer, taskInstance, flowInstance, taskData, TaskTimerLog.TYPE_DUE_DOING);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TimerListenerAdapter#onTimerOverDue(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void onTimerOverDue(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                               TaskData taskData) {
        // 处理引用子流程计时器
        handleRefNewFlowTimer(taskTimer, taskInstance, flowInstance, taskData, TaskTimerLog.TYPE_OVER_DUE);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TimerListenerAdapter#onTimerStopped(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void onTimerStopped(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                               TaskData taskData) {
        // 处理引用子流程计时器
        handleRefNewFlowTimer(taskTimer, taskInstance, flowInstance, taskData, TaskTimerLog.TYPE_END);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TimerListenerAdapter#getOrder()
     */
    @Override
    public int getOrder() {
        return 9999;
    }

    /**
     * @param taskTimer
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     */
    private void handleRefNewFlowTimer(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                                       TaskData taskData, String timerAction) {
        // 上级流程环节实例
        TaskInstance parentTaskInstance = getParentTaskInst(taskInstance, flowInstance, taskData);
        FlowInstance parentFlowInstance = parentTaskInstance.getFlowInstance();
        // 上级流程环节实例是否允许计时
        if (!isAllowTiming(parentTaskInstance, taskTimer)) {
            log(parentTaskInstance, parentFlowInstance, taskTimer, "子环节实例不允许计时！");
            return;
        }

        // 获取要同步的计时器信息
        TaskTimer syncTaskTimer = getSyncTaskTimer(taskTimer, taskInstance, flowInstance, parentTaskInstance,
                parentFlowInstance);
        String remark = "到期时间：" + DateUtils.formatDateTime(syncTaskTimer.getTaskDueTime()) + ", 计时状态："
                + syncTaskTimer.getTimingState();
        // 判断计时信息是否变更
        if (!isTimmingInfoChanged(syncTaskTimer, parentTaskInstance)) {
            log(parentTaskInstance, parentFlowInstance, syncTaskTimer, "计时信息未变更！" + remark);
            return;
        }

        // 同步上级环节、流程数据计时信息
        switch (timerAction) {
            case TaskTimerLog.TYPE_ALARM:
                handleRefNewFlowTimerAlarm(parentTaskInstance, parentFlowInstance, syncTaskTimer);
                break;
            case TaskTimerLog.TYPE_DUE_DOING:
                handleRefNewFlowTimerDueDoing(parentTaskInstance, parentFlowInstance, syncTaskTimer);
                break;
            case TaskTimerLog.TYPE_OVER_DUE:
                handleRefNewFlowTimerOverDue(parentTaskInstance, parentFlowInstance, syncTaskTimer);
                break;
            default:
                timerExecutor.syncTaskFlowData(parentTaskInstance, parentFlowInstance, syncTaskTimer);
                break;
        }

        // 记录日志
        log(parentTaskInstance, parentFlowInstance, syncTaskTimer, "计时信息更新！" + remark);
    }

    /**
     * @param parentTaskInstance
     * @param parentFlowInstance
     * @param syncTaskTimer
     */
    private void handleRefNewFlowTimerAlarm(TaskInstance parentTaskInstance, FlowInstance parentFlowInstance,
                                            TaskTimer syncTaskTimer) {
        List<TaskTimer> taskTimers = getRefNewFlowTimer(parentTaskInstance, parentFlowInstance, syncTaskTimer);
        for (TaskTimer taskTimer : taskTimers) {
            if (taskAlarmHanlderService.markTaskAlarmInfo(taskTimer.getUuid())) {
                taskAlarmHanlderService.handler(taskTimer.getUuid());
            }
        }
    }

    /**
     * @param parentTaskInstance
     * @param parentFlowInstance
     * @param syncTaskTimer
     */
    private void handleRefNewFlowTimerDueDoing(TaskInstance parentTaskInstance, FlowInstance parentFlowInstance,
                                               TaskTimer syncTaskTimer) {
        List<TaskTimer> taskTimers = getRefNewFlowTimer(parentTaskInstance, parentFlowInstance, syncTaskTimer);
        for (TaskTimer taskTimer : taskTimers) {
            if (taskDueHanlderService.markDueInfo(taskTimer.getUuid())) {
                taskDueHanlderService.handler(taskTimer.getUuid());
            }
        }
    }

    /**
     * @param parentTaskInstance
     * @param parentFlowInstance
     * @param syncTaskTimer
     */
    private void handleRefNewFlowTimerOverDue(TaskInstance parentTaskInstance, FlowInstance parentFlowInstance,
                                              TaskTimer syncTaskTimer) {
        List<TaskTimer> taskTimers = getRefNewFlowTimer(parentTaskInstance, parentFlowInstance, syncTaskTimer);
        for (TaskTimer taskTimer : taskTimers) {
            if (taskOverDueHanlderService.markOverDueInfo(taskTimer.getUuid(), parentFlowInstance.getOverdueTime())) {
                taskOverDueHanlderService.handler(taskTimer.getUuid());
            }
        }
    }

    /**
     * @param parentTaskInstance
     * @param parentFlowInstance
     * @param syncTaskTimer
     * @return
     */
    private List<TaskTimer> getRefNewFlowTimer(TaskInstance parentTaskInstance, FlowInstance parentFlowInstance,
                                               TaskTimer syncTaskTimer) {
        List<TaskTimer> refTaskTimers = Lists.newArrayList();
        String taskId = parentTaskInstance.getId();
        String taskInstUuid = parentTaskInstance.getUuid();
        String flowInstUuid = parentFlowInstance.getUuid();
        List<TaskTimer> taskTimers = taskTimerService.getActiveTimersByTaskInstUuidAndFlowInstUuid(taskInstUuid,
                flowInstUuid);
        for (TaskTimer taskTimer : taskTimers) {
            if (StringUtils.equals(taskId, taskTimer.getTaskId())
                    && StringUtils.contains(taskTimer.getRefIds(), syncTaskTimer.getId())
                    && (Boolean.TRUE.equals(taskTimer.getEnableAlarm()) || Boolean.TRUE.equals(taskTimer
                    .getEnableDueDoing()))) {
                refTaskTimers.add(taskTimer);
            }
        }
        // 同步计时信息
        for (TaskTimer taskTimer : refTaskTimers) {
            taskTimer.setTaskDueTime(syncTaskTimer.getTaskDueTime());
            taskTimer.setTaskAlarmTime(syncTaskTimer.getTaskAlarmTime());
            taskTimer.setTimingState(syncTaskTimer.getTimingState());
            taskTimer.setOverDueState(syncTaskTimer.getOverDueState());
            taskTimer.setAlarmState(syncTaskTimer.getAlarmState());
        }
        taskTimerService.saveAll(refTaskTimers);
        return refTaskTimers;
    }

    /**
     * @param taskInstance
     * @return
     */
    private boolean isAllowTiming(TaskInstance taskInstance, TaskTimer taskTimer) {
        if (taskInstance == null || taskInstance.getEndTime() != null) {
            return false;
        }
        String parentTaskInstUuid = taskInstance.getUuid();
        String parentFlowInstUuid = taskInstance.getFlowInstance().getUuid();
        String timerId = taskTimer.getId();
        // 上级子流程环节按子流程计时器计时标记
        String name = parentTaskInstUuid + "_" + timerId + "_parent_task_ref_timer";
        long count = flowInstanceParameterService.countByFlowInstUuidAndName(parentFlowInstUuid, name);
        return count > 0;
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @return
     */
    private TaskInstance getParentTaskInst(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        String flowInstUuid = flowInstance.getUuid();
        String parentTaskInstUuid = null;
        if (taskData != null) {
            parentTaskInstUuid = taskData.getParentTaskInstUuid(flowInstUuid);
        }
        if (StringUtils.isBlank(parentTaskInstUuid)) {
            List<TaskSubFlow> taskSubFlows = taskSubFlowService.getByFlowInstUuid(flowInstUuid);
            if (CollectionUtils.isNotEmpty(taskSubFlows)) {
                parentTaskInstUuid = taskSubFlows.get(0).getParentTaskInstUuid();
            }
        }
        return StringUtils.isNotBlank(parentTaskInstUuid) ? taskInstanceService.getOne(parentTaskInstUuid) : null;
    }

    /**
     * @param taskTimer
     * @param taskInstance
     * @param flowInstance
     * @return
     */
    private TaskTimer getSyncTaskTimer(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                                       TaskInstance parentTaskInstance, FlowInstance parentFlowInstance) {
        // 子流程环节的时限按照子流程实例中的未结束计时的最长时限计算
        Date taskDueTime = null;
        Date taskAlarmTime = null;
        // 子流程环节的计时状态（预警、到期、逾期）和各个子流程实例中未结束计时的时限最近的一个一致
        Integer timingState = TimingState.NORMAL;
        Integer overDueState = 0;
        Integer alarmState = 0;
        Date nearestDueTime = null;
        // 获取正在计时的子流程实例
        List<FlowInstance> subFlowInstances = flowService.getUnfinishedSubFlowInstances(parentFlowInstance.getUuid());
        if (CollectionUtils.isNotEmpty(subFlowInstances)) {
            for (FlowInstance subFlowInstance : subFlowInstances) {
                Date dueTime = subFlowInstance.getDueTime();
                Date alarmTime = subFlowInstance.getAlarmTime();
                // 到期时间
                if (taskDueTime == null || (dueTime != null && taskDueTime.before(dueTime))) {
                    taskDueTime = dueTime;
                    // 预警时间
                    if (taskAlarmTime == null || (alarmTime != null && taskAlarmTime.before(alarmTime))) {
                        taskAlarmTime = alarmTime;
                    }
                }
                // 计时状态
                if (nearestDueTime == null || (dueTime != null && nearestDueTime.after(dueTime))) {
                    nearestDueTime = dueTime;
                    timingState = subFlowInstance.getTimingState();
                    overDueState = subFlowInstance.getIsOverDue() ? 1 : 0;
                    alarmState = taskAlarmTime != null && taskAlarmTime.before(Calendar.getInstance().getTime()) ? 1
                            : 0;
                }
            }
        } else {
            taskDueTime = taskTimer.getTaskDueTime();
            timingState = taskTimer.getTimingState();
            overDueState = taskTimer.getOverDueState();
            alarmState = taskTimer.getAlarmState();
        }

        TaskTimer timer = new TaskTimer();
        BeanUtils.copyProperties(taskTimer, timer);
        timer.setTaskDueTime(taskDueTime);
        timer.setTaskAlarmTime(taskAlarmTime);
        timer.setTimingState(timingState);
        timer.setOverDueState(overDueState);
        timer.setAlarmState(alarmState);

        return timer;
    }

    /**
     * @param unfinishedSubFlowInstances
     * @return
     */
    private List<FlowInstance> filterTimingFlowInstance(List<FlowInstance> unfinishedSubFlowInstances) {
        List<FlowInstance> timingFlowInstances = Lists.newArrayList();
        for (FlowInstance flowInstance : unfinishedSubFlowInstances) {
            if (Boolean.TRUE.equals(flowInstance.getIsTiming())) {
                timingFlowInstances.add(flowInstance);
            }
        }
        return timingFlowInstances;
    }

    /**
     * @param syncTaskTimer
     * @param parentTaskInstance
     * @return
     */
    private boolean isTimmingInfoChanged(TaskTimer syncTaskTimer, TaskInstance parentTaskInstance) {
        Date dueTime = parentTaskInstance.getDueTime();
        Date alarmTime = parentTaskInstance.getAlarmTime();
        Integer timingState = parentTaskInstance.getTimingState();
        Date taskDueTime = syncTaskTimer.getTaskDueTime();
        Date taskAlarmTime = syncTaskTimer.getTaskAlarmTime();
        Integer taskTimingState = syncTaskTimer.getTimingState();
        if (dueTime == null || alarmTime == null || taskDueTime == null || taskAlarmTime == null || timingState == null
                || taskTimingState == null) {
            return true;
        }
        return !dueTime.equals(taskDueTime) || !alarmTime.equals(taskAlarmTime) || !timingState.equals(taskTimingState);
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @param taskTimer
     * @param remark
     */
    private void log(TaskInstance taskInstance, FlowInstance flowInstance, TaskTimer taskTimer, String remark) {
        taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer, Calendar.getInstance()
                .getTime(), TaskTimerLog.TYPE_INFO, remark);
    }

}
