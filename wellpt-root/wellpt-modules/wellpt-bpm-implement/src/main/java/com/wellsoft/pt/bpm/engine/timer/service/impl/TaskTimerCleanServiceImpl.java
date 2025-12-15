/*
 * @(#)2018年10月22日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.service.impl;

import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.entity.TaskTimerLog;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.service.TaskTimerLogService;
import com.wellsoft.pt.bpm.engine.service.TaskTimerService;
import com.wellsoft.pt.bpm.engine.timer.TimerExecutor;
import com.wellsoft.pt.bpm.engine.timer.service.TaskTimerCleanService;
import com.wellsoft.pt.bpm.engine.timer.support.TaskTimerStatus;
import com.wellsoft.pt.bpm.engine.timer.support.TimingState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
 * 2018年10月22日.1	zhulh		2018年10月22日		Create
 * </pre>
 * @date 2018年10月22日
 */
@Service
@Transactional(readOnly = true)
public class TaskTimerCleanServiceImpl implements TaskTimerCleanService {

    private static final String GET_ALARM_TASK_TIMERS = "from TaskTimer t where t.status = :status and (t.alarmDone is null or t.alarmDone = false)"
            + " and t.alarmTime is not null and t.alarmState <> 1 and t.taskAlarmTime < :deadlineTime";

    private static final String CHECK_ALARM_TASK_TIMER = "from TaskTimer t where t.status = :status and (t.alarmDone is null or t.alarmDone = false)"
            + " and t.alarmTime is not null and t.alarmState <> 1 and t.uuid = :timerUuid";

    private static final String GET_OVERDUE_TASK_TIMERS = "from TaskTimer t where t.status = :status and (t.dueDoingDone is null or t.dueDoingDone = false)"
            + " and t.overDueState <> 1 and t.taskDueTime < :deadlineTime";

    private static final String CHECK_OVERDUE_TASK_TIMER = "from TaskTimer t where t.status = :status and (t.dueDoingDone is null or t.dueDoingDone = false)"
            + " and t.overDueState <> 1 and t.uuid = :timerUuid";

    @Autowired
    private TaskTimerService taskTimerService;

    @Autowired
    private TaskTimerLogService taskTimerLogService;

    @Autowired
    private TimerExecutor timerExecutor;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FlowService flowService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TaskTimerCleanService#getAlarmTaskTimers()
     */
    public List<TaskTimer> getToScheduleOfAlarmTaskTimers() {
        Calendar calendar = Calendar.getInstance();
        // 截止时间
        calendar.add(Calendar.SECOND, 120);
        Date deadlineTime = calendar.getTime();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", TaskTimerStatus.STARTED);
        values.put("deadlineTime", deadlineTime);
        return taskTimerService.listByHQL(GET_ALARM_TASK_TIMERS, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TaskTimerCleanService#checkAlarmTaskTimer(java.lang.String)
     */
    @Override
    public boolean checkAlarmTaskTimer(String timerUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", TaskTimerStatus.STARTED);
        values.put("timerUuid", timerUuid);
        return taskTimerService.countByHQL(CHECK_ALARM_TASK_TIMER, values) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TaskTimerCleanService#forceStopAlarmTaskTimer(java.lang.String)
     */
    @Transactional
    public void forceStopAlarmTaskTimer(String taskTimerUuid) {
        this.forceStopAlarmTaskTimer(taskTimerUuid, null);
    }

    @Override
    @Transactional
    public void forceStopAlarmTaskTimer(String taskTimerUuid, String msg) {
        TaskTimer taskTimer = taskTimerService.get(taskTimerUuid);
        taskTimer.setAlarmDone(true);
        taskTimer.setTimingState(TimingState.ALARM);
        taskTimer.setAlarmState(1);
        taskTimerService.save(taskTimer);
        TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
        FlowInstance flowInstance = flowService.getFlowInstanceByTaskInstUuid(taskTimer.getTaskInstUuid());
        if (taskInstance != null && flowInstance != null) {
            timerExecutor.syncTaskFlowData(taskInstance, flowInstance, taskTimer);
            taskTimerLogService.log(taskTimer.getTaskInstUuid(), taskTimer.getFlowInstUuid(), taskTimer, Calendar
                    .getInstance().getTime(), TaskTimerLog.TYPE_FORCE_STOP_ALARM, msg);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TaskTimerCleanService#getOverDueTaskTimers()
     */
    public List<TaskTimer> getToScheduleOfOverDueTaskTimers() {
        Calendar calendar = Calendar.getInstance();
        // 截止时间
        calendar.add(Calendar.SECOND, 120);
        Date deadlineTime = calendar.getTime();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", TaskTimerStatus.STARTED);
        values.put("deadlineTime", deadlineTime);
        return taskTimerService.listByHQL(GET_OVERDUE_TASK_TIMERS, values);
    }

    @Override
    public void forceStopDueTaskTimer(String taskTimerUuid, String remark) {
        TaskTimer taskTimer = taskTimerService.get(taskTimerUuid);
        taskTimer.setDueDoingDone(true);
        taskTimer.setTimingState(TimingState.DUE);
        taskTimerService.save(taskTimer);
        TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
        FlowInstance flowInstance = flowService.getFlowInstanceByTaskInstUuid(taskTimer.getTaskInstUuid());
        if (taskInstance != null && flowInstance != null) {
            timerExecutor.syncTaskFlowData(taskInstance, flowInstance, taskTimer);
            taskTimerLogService.log(taskTimer.getTaskInstUuid(), taskTimer.getFlowInstUuid(), taskTimer, Calendar
                    .getInstance().getTime(), TaskTimerLog.TYPE_FORCE_STOP_DUE_DOING, remark);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TaskTimerCleanService#checkOverDueTaskTimer(java.lang.String)
     */
    @Override
    public boolean checkOverDueTaskTimer(String timerUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", TaskTimerStatus.STARTED);
        values.put("timerUuid", timerUuid);
        return taskTimerService.countByHQL(CHECK_OVERDUE_TASK_TIMER, values) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TaskTimerCleanService#forceStopOverDueTaskTimer(java.lang.String)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void forceStopOverDueTaskTimer(String taskTimerUuid) {
        this.forceStopOverDueTaskTimer(taskTimerUuid, Calendar.getInstance().getTime(), null);
    }

    @Override
    @Transactional
    public void forceStopOverDueTaskTimer(String taskTimerUuid, Date overdueTime, String remark) {
        TaskTimer taskTimer = taskTimerService.get(taskTimerUuid);
        taskTimer.setDueDoingDone(true);
        taskTimer.setTimingState(TimingState.OVER_DUE);
        taskTimer.setOverDueState(1);
        taskTimerService.save(taskTimer);
        TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
        FlowInstance flowInstance = flowService.getFlowInstanceByTaskInstUuid(taskTimer.getTaskInstUuid());
        if (taskInstance != null && flowInstance != null) {
            Date currentOverdueTime = flowInstance.getOverdueTime();
            flowInstance.setOverdueTime(overdueTime);
            taskInstance.setOverdueTime(overdueTime);
            timerExecutor.syncTaskFlowData(taskInstance, flowInstance, taskTimer);
            taskTimerLogService.log(taskTimer.getTaskInstUuid(), taskTimer.getFlowInstUuid(), taskTimer, Calendar
                    .getInstance().getTime(), TaskTimerLog.TYPE_FORCE_STOP_OVER_DUE_DOING, remark);
        }
    }

}
