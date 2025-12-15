/*
 * @(#)2018年10月22日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.job;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.service.TaskTimerService;
import com.wellsoft.pt.bpm.engine.timer.service.*;
import com.wellsoft.pt.bpm.engine.timer.support.TimerHelper;
import com.wellsoft.pt.bpm.engine.timer.support.TimingState;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TaskTimerCleanUpJob {

    private static Logger LOG = LoggerFactory.getLogger(TaskTimerCleanUpJob.class);

    private volatile static Object locked = new Object();

    public static void execute() {
        // 清理过期的定时器数据
        TaskTimerCleanService taskTimerCleanService = ApplicationContextHolder.getBean(TaskTimerCleanService.class);
        TimerManagerService timerManagerService = ApplicationContextHolder.getBean(TimerManagerService.class);
        // 当前任务批次处理的计时器
        Set<String> batchTimerUuidSet = Sets.newHashSet();
        // 清理预警计时器
        List<TaskTimer> alarmTaskTimers = taskTimerCleanService.getToScheduleOfAlarmTaskTimers();
        for (TaskTimer taskTimer : alarmTaskTimers) {
            String taskTimerUuid = taskTimer.getUuid();
            // 判断是否已经进入预警调度
            if (Integer.valueOf(TimingState.NORMAL).equals(taskTimer.getTimingState())
                    && !timerManagerService.isInAlarmSchedule(taskTimerUuid)) {
                String tenantId = taskTimer.getTenantId();
                String creator = taskTimer.getCreator();
                Date alarmTime = taskTimer.getTaskAlarmTime();
                TimerTask timerTask = new AlarmTimerTask(tenantId, creator, taskTimerUuid, alarmTime);
                timerManagerService.addAlarmSchedule(taskTimerUuid, timerTask, alarmTime);
                batchTimerUuidSet.add(taskTimerUuid);
            } else {
                timerManagerService.handleIncreaseAlarmCounter(taskTimerUuid);
            }
        }

        // 清理到期计时器
        List<TaskTimer> overDueTaskTimers = taskTimerCleanService.getToScheduleOfOverDueTaskTimers();
        for (TaskTimer taskTimer : overDueTaskTimers) {
            // 计时服务的数据由计时服务监听器TaskTimerListener处理
            if (StringUtils.isNotBlank(taskTimer.getTimerUuid())) {
                continue;
            }
            String taskTimerUuid = taskTimer.getUuid();
            // 判断是否已经进入到期调度
            if (!(Integer.valueOf(TimingState.DUE).equals(taskTimer.getTimingState()) || timerManagerService
                    .isInDueSchedule(taskTimerUuid))) {
                // 当前批次已预警，忽略掉
                if (batchTimerUuidSet.contains(taskTimerUuid)) {
                    continue;
                }
                String tenantId = taskTimer.getTenantId();
                String creator = taskTimer.getCreator();
                Date taskDueTime = taskTimer.getTaskDueTime();
                TimerTask timerTask = new DueTimerTask(tenantId, creator, taskTimerUuid, taskDueTime);
                timerManagerService.addDueSchedule(taskTimerUuid, timerTask, taskDueTime);
                batchTimerUuidSet.add(taskTimerUuid);
            }

            // 判断是否已经进入逾期调度
            if (!timerManagerService.isInOverDueSchedule(taskTimerUuid)) {
                // 当前批次已预警或到期，忽略掉
                if (batchTimerUuidSet.contains(taskTimerUuid)) {
                    continue;
                }
                String tenantId = taskTimer.getTenantId();
                String creator = taskTimer.getCreator();
                Date taskDueTime = taskTimer.getTaskDueTime();
                int limitUnit = Integer.valueOf(taskTimer.getLimitUnit());
                Date taskOverDueTime = TimerHelper.getOverDueTime(taskDueTime, limitUnit);
                TimerTask timerTask = new OverDueTimerTask(tenantId, creator, taskTimerUuid, taskOverDueTime);
                timerManagerService.addOverDueSchedule(taskTimerUuid, timerTask, taskOverDueTime);
            } else {
                timerManagerService.handleIncreaseOverDueCounter(taskTimerUuid);
            }
        }
    }

    private static class AlarmTimerTask extends TimerTask {
        private String tenantId;
        private String creator;
        private String taskTimerUuid;
        private Date taskAlarmTime;

        /**
         * @param tenantId
         * @param creator
         * @param taskTimerUuid
         * @param taskTimerUuid
         */
        public AlarmTimerTask(String tenantId, String creator, String taskTimerUuid, Date taskAlarmTime) {
            super();
            this.tenantId = tenantId;
            this.creator = creator;
            this.taskTimerUuid = taskTimerUuid;
            this.taskAlarmTime = taskAlarmTime;
        }

        /**
         * (non-Javadoc)
         *
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            synchronized (locked) {
                TaskTimerCleanService taskTimerCleanService = ApplicationContextHolder
                        .getBean(TaskTimerCleanService.class);
                TimerManagerService timerManagerService = ApplicationContextHolder.getBean(TimerManagerService.class);
                TaskTimerService taskTimerService = ApplicationContextHolder.getBean(TaskTimerService.class);
                FlowInstanceService flowInstanceService = ApplicationContextHolder.getBean(FlowInstanceService.class);
                TaskAlarmHanlderService taskAlarmService = ApplicationContextHolder.getBean(TaskAlarmHanlderService.class);
                boolean markTaskAlarmInfo = false;
                try {
                    LOG.info("alarm doing thread start at " + DateUtils.formatDateTime(taskAlarmTime)
                            + ", current time is " + DateUtils.formatDateTime(Calendar.getInstance().getTime()));
                    IgnoreLoginUtils.login(tenantId, creator);
                    // 如果任务已经不在预警状态忽略掉
                    if (!taskTimerCleanService.checkAlarmTaskTimer(taskTimerUuid)) {
                        return;
                    }
                    TaskTimer taskTimer = taskTimerService.get(taskTimerUuid);
                    if (taskTimer == null) {
                        return;
                    }
                    FlowInstance flowInstance = flowInstanceService.get(taskTimer.getFlowInstUuid());
                    if (flowInstance == null) {
                        return;
                    }
                    RequestSystemContextPathResolver.setSystem(flowInstance.getSystem());

                    markTaskAlarmInfo = taskAlarmService.markTaskAlarmInfo(taskTimerUuid);
                    if (markTaskAlarmInfo) {
                        taskAlarmService.handler(taskTimerUuid);
                    }
                } catch (Exception e) {
                    String error = Throwables.getStackTraceAsString(e);
                    LOG.error("流程预警提醒计时任务执行异常：{}", error);
                    taskTimerCleanService.forceStopAlarmTaskTimer(taskTimerUuid, error.substring(0, 1500));
                } finally {
                    if (markTaskAlarmInfo) {
                        timerManagerService.removeAlarmSchedule(taskTimerUuid);
                    }
                    IgnoreLoginUtils.logout();
                    RequestSystemContextPathResolver.clear();
                }
            }
        }

    }

    private static class DueTimerTask extends TimerTask {
        private String tenantId;
        private String creator;
        private String taskTimerUuid;
        private Date taskDueTime;

        /**
         * @param tenantId
         * @param creator
         * @param taskTimerUuid
         * @param taskDueTime
         */
        public DueTimerTask(String tenantId, String creator, String taskTimerUuid, Date taskDueTime) {
            super();
            this.tenantId = tenantId;
            this.creator = creator;
            this.taskTimerUuid = taskTimerUuid;
            this.taskDueTime = taskDueTime;
        }

        /**
         * (non-Javadoc)
         *
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            synchronized (locked) {
                TaskTimerCleanService taskTimerCleanService = ApplicationContextHolder
                        .getBean(TaskTimerCleanService.class);
                TimerManagerService timerManagerService = ApplicationContextHolder.getBean(TimerManagerService.class);
                boolean markDueInfo = false;
                try {
                    LOG.info("due doing thread start at " + DateUtils.formatDateTime(taskDueTime)
                            + ", current time is " + DateUtils.formatDateTime(Calendar.getInstance().getTime()));
                    IgnoreLoginUtils.login(tenantId, creator);
                    // 如果任务已经不在预警状态忽略掉
                    if (!taskTimerCleanService.checkOverDueTaskTimer(taskTimerUuid)) {
                        return;
                    }
                    TaskDueHanlderService taskDueService = ApplicationContextHolder
                            .getBean(TaskDueHanlderService.class);
                    markDueInfo = taskDueService.markDueInfo(taskTimerUuid);
                    if (markDueInfo) {
                        taskDueService.handler(taskTimerUuid);
                    }
                } catch (Exception e) {
                    String error = Throwables.getStackTraceAsString(e);
                    LOG.error("流程逾期提醒计时任务执行异常：{}", error);
                    taskTimerCleanService.forceStopDueTaskTimer(taskTimerUuid, error.substring(0, 1500));
                } finally {
                    if (markDueInfo) {
                        timerManagerService.removeDueSchedule(taskTimerUuid);
                    }
                    IgnoreLoginUtils.logout();
                }
            }
        }

    }

    private static class OverDueTimerTask extends TimerTask {
        private String tenantId;
        private String creator;
        private String taskTimerUuid;
        private Date taskOverDueTime;

        /**
         * @param tenantId
         * @param creator
         * @param taskTimerUuid
         * @param taskDueTime
         */
        public OverDueTimerTask(String tenantId, String creator, String taskTimerUuid, Date taskOverDueTime) {
            super();
            this.tenantId = tenantId;
            this.creator = creator;
            this.taskTimerUuid = taskTimerUuid;
            this.taskOverDueTime = taskOverDueTime;
        }

        /**
         * (non-Javadoc)
         *
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            synchronized (locked) {
                TaskTimerCleanService taskTimerCleanService = ApplicationContextHolder
                        .getBean(TaskTimerCleanService.class);
                TimerManagerService timerManagerService = ApplicationContextHolder.getBean(TimerManagerService.class);
                boolean markOverDueInfo = false;
                try {
                    LOG.info("over due doing thread start at " + DateUtils.formatDateTime(taskOverDueTime)
                            + ", current time is " + DateUtils.formatDateTime(Calendar.getInstance().getTime()));
                    IgnoreLoginUtils.login(tenantId, creator);
                    // 如果任务已经不在预警状态忽略掉
                    if (!taskTimerCleanService.checkOverDueTaskTimer(taskTimerUuid)) {
                        return;
                    }
                    TaskOverDueHanlderService taskOverDueService = ApplicationContextHolder
                            .getBean(TaskOverDueHanlderService.class);
                    markOverDueInfo = taskOverDueService.markOverDueInfo(taskTimerUuid, taskOverDueTime);
                    if (markOverDueInfo) {
                        taskOverDueService.handler(taskTimerUuid);
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                    taskTimerCleanService.forceStopOverDueTaskTimer(taskTimerUuid);
                } finally {
                    if (markOverDueInfo) {
                        timerManagerService.removeOverDueSchedule(taskTimerUuid);
                    }
                    IgnoreLoginUtils.logout();
                }
            }
        }

    }

}
