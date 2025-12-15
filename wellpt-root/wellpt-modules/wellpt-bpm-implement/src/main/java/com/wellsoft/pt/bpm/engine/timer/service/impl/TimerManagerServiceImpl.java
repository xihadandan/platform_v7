/*
 * @(#)2018年11月13日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.service.impl;

import com.wellsoft.pt.bpm.engine.timer.service.TaskTimerCleanService;
import com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年11月13日.1	zhulh		2018年11月13日		Create
 * </pre>
 * @date 2018年11月13日
 */
@Service
public class TimerManagerServiceImpl implements TimerManagerService {

    // 预警
    private static final String SCHEDULE_TYPE_ALARM = "1";
    // 到期
    private static final String SCHEDULE_TYPE_DUE = "2";
    // 逾期
    private static final String SCHEDULE_TYPE_OVER_DUE = "3";

    private static Map<String, TimerTask> scheduleMap = new HashMap<String, TimerTask>();

    private static Map<String, Integer> counterMap = new HashMap<String, Integer>();

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private TaskTimerCleanService taskTimerCleanService;

    /**
     * @param taskTimerUuid
     * @return
     */
    private static String getAlarmKey(String taskTimerUuid) {
        return taskTimerUuid + "_" + SCHEDULE_TYPE_ALARM;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#isInAlarmSchedule(java.lang.String)
     */
    @Override
    public boolean isInAlarmSchedule(String taskTimerUuid) {
        return scheduleMap.containsKey(getAlarmKey(taskTimerUuid));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#isInDueSchedule(java.lang.String)
     */
    @Override
    public boolean isInDueSchedule(String taskTimerUuid) {
        return scheduleMap.containsKey(getDueKey(taskTimerUuid));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#isInOverDueSchedule(java.lang.String)
     */
    @Override
    public boolean isInOverDueSchedule(String taskTimerUuid) {
        return scheduleMap.containsKey(getOverDueKey(taskTimerUuid));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#addAlarmSchedule(java.lang.String, java.util.TimerTask, java.util.Date)
     */
    @Override
    public void addAlarmSchedule(String taskTimerUuid, TimerTask timerTask, Date time) {
        scheduleMap.put(getAlarmKey(taskTimerUuid), timerTask);
        scheduledExecutorService.schedule(timerTask, delay(time), TimeUnit.MILLISECONDS);
    }

    private long delay(Date time) {
        Date now = new Date();
        long delay = 0L;
        if (time.after(now)) {
            delay = time.getTime() - now.getTime();
        }
        return delay;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#addDueSchedule(java.lang.String, java.util.TimerTask, java.util.Date)
     */
    @Override
    public void addDueSchedule(String taskTimerUuid, TimerTask timerTask, Date time) {
        scheduleMap.put(getDueKey(taskTimerUuid), timerTask);
        scheduledExecutorService.schedule(timerTask, delay(time), TimeUnit.MILLISECONDS);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#addOverDueSchedule(java.lang.String, java.util.TimerTask, java.util.Date)
     */
    @Override
    public void addOverDueSchedule(String taskTimerUuid, TimerTask timerTask, Date time) {
        scheduleMap.put(getOverDueKey(taskTimerUuid), timerTask);
        scheduledExecutorService.schedule(timerTask, delay(time), TimeUnit.MILLISECONDS);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#removeAlarmSchedule(java.lang.String)
     */
    @Override
    public void removeAlarmSchedule(String taskTimerUuid) {
        TimerTask timerTask = scheduleMap.remove(getAlarmKey(taskTimerUuid));
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#removeDueSchedule(java.lang.String)
     */
    @Override
    public void removeDueSchedule(String taskTimerUuid) {
        TimerTask timerTask = scheduleMap.remove(getDueKey(taskTimerUuid));
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#removeOverDueSchedule(java.lang.String)
     */
    @Override
    public void removeOverDueSchedule(String taskTimerUuid) {
        TimerTask timerTask = scheduleMap.remove(getOverDueKey(taskTimerUuid));
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#removeAllSchedule(java.lang.String)
     */
    @Override
    public void removeAllSchedule(String taskTimerUuid) {
        removeAlarmSchedule(taskTimerUuid);
        removeDueSchedule(taskTimerUuid);
        removeOverDueSchedule(taskTimerUuid);
    }

    /**
     * @param taskTimerUuid
     * @return
     */
    private String getDueKey(String taskTimerUuid) {
        return taskTimerUuid + "_" + SCHEDULE_TYPE_DUE;
    }

    /**
     * @param taskTimerUuid
     * @return
     */
    private String getOverDueKey(String taskTimerUuid) {
        return taskTimerUuid + "_" + SCHEDULE_TYPE_OVER_DUE;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#handleIncreaseAlarmCounter(java.lang.String)
     */
    @Override
    public void handleIncreaseAlarmCounter(String taskTimerUuid) {
        String key = getAlarmKey(taskTimerUuid);
        int counter = addCounter(key);
        if (counter > 3600) {
            taskTimerCleanService.forceStopAlarmTaskTimer(taskTimerUuid);
            removeCounter(key);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService#handleIncreaseOverDueCounter(java.lang.String)
     */
    @Override
    public void handleIncreaseOverDueCounter(String taskTimerUuid) {
        String key = getOverDueKey(taskTimerUuid);
        int counter = addCounter(key);
        if (counter > 3600) {
            taskTimerCleanService.forceStopOverDueTaskTimer(taskTimerUuid);
            removeCounter(key);
        }
    }

    /**
     * @param key
     * @return
     */
    private int addCounter(String key) {
        int counter = 1;
        if (!counterMap.containsKey(key)) {
            counterMap.put(key, 1);
        } else {
            counter = counterMap.get(key);
            counter++;
        }
        counterMap.put(key, counter);
        return counter;
    }

    /**
     * @param key
     */
    private void removeCounter(String key) {
        counterMap.remove(key);
    }

}
