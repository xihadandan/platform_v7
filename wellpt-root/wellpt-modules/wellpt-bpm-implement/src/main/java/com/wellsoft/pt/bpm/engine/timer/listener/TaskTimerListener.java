/*
 * @(#)2021年4月11日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.listener;

import com.google.common.base.Throwables;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.service.TaskTimerService;
import com.wellsoft.pt.bpm.engine.timer.service.TaskDueHanlderService;
import com.wellsoft.pt.bpm.engine.timer.service.TaskOverDueHanlderService;
import com.wellsoft.pt.bpm.engine.timer.service.TaskTimerCleanService;
import com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService;
import com.wellsoft.pt.timer.listener.AbstractTimerListener;
import com.wellsoft.pt.timer.support.event.TimerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月11日.1	zhulh		2021年4月11日		Create
 * </pre>
 * @date 2021年4月11日
 */
@Component
public class TaskTimerListener extends AbstractTimerListener {

    public static final String LISTENER_NAME = "taskTimerListener";
    private static Logger LOG = LoggerFactory.getLogger(TaskTimerListener.class);
    @Autowired
    private TaskTimerService taskTimerService;

    @Autowired
    private TaskTimerCleanService taskTimerCleanService;

    @Autowired
    private TimerManagerService timerManagerService;

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.listener.TimerListener#getName()
     */
    @Override
    public String getName() {
        return "计时服务_流程计时监听器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.listener.TimerListener#onTimerDue(java.lang.String, java.util.Date)
     */
    @Override
    public void onTimerDue(TimerEvent event) {
        boolean markDueInfo = false;
        TaskTimer taskTimer = taskTimerService.getByTimerUuid(event.getTimerUuid());
        String taskTimerUuid = taskTimer.getUuid();
        try {
            LOG.info("due doing thread start at " + DateUtils.formatDateTime(event.getDueTime()) + ", current time is "
                    + DateUtils.formatDateTime(Calendar.getInstance().getTime()));
            // 如果任务已经不在预警状态忽略掉
            if (!taskTimerCleanService.checkOverDueTaskTimer(taskTimerUuid)) {
                return;
            }
            TaskDueHanlderService taskDueService = ApplicationContextHolder.getBean(TaskDueHanlderService.class);
            markDueInfo = taskDueService.markDueInfo(taskTimerUuid);
            if (markDueInfo) {
                taskDueService.handler(taskTimerUuid);
            }
        } catch (Exception e) {
            String error = Throwables.getStackTraceAsString(e);
            LOG.error("流程到期提醒计时任务执行异常：{}", error);
            taskTimerCleanService.forceStopDueTaskTimer(taskTimerUuid, error.substring(0, 1500));
        } finally {
            if (markDueInfo) {
                timerManagerService.removeDueSchedule(taskTimerUuid);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.listener.TimerListener#onTimerOverDue(java.lang.String, java.util.Date)
     */
    @Override
    public void onTimerOverDue(TimerEvent event) {
        boolean markOverDueInfo = false;
        TaskTimer taskTimer = taskTimerService.getByTimerUuid(event.getTimerUuid());
        String taskTimerUuid = taskTimer.getUuid();
        try {
            LOG.info("over due doing thread start at " + DateUtils.formatDateTime(event.getOverdueTime()) + ", current time is "
                    + DateUtils.formatDateTime(Calendar.getInstance().getTime()));
            // 如果任务已经不在预警状态忽略掉
            if (!taskTimerCleanService.checkOverDueTaskTimer(taskTimerUuid)) {
                return;
            }
            TaskOverDueHanlderService taskOverDueService = ApplicationContextHolder
                    .getBean(TaskOverDueHanlderService.class);
            markOverDueInfo = taskOverDueService.markOverDueInfo(taskTimerUuid, event.getOverdueTime());
            if (markOverDueInfo) {
                taskOverDueService.handler(taskTimerUuid);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            taskTimerCleanService.forceStopOverDueTaskTimer(taskTimerUuid, event.getOverdueTime(), "流程逾期计时任务执行异常：" + e.getMessage());
        } finally {
            if (markOverDueInfo) {
                timerManagerService.removeOverDueSchedule(taskTimerUuid);
            }
        }

    }

}
