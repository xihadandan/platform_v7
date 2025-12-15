/*
 * @(#)2018年11月13日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.service;

import java.util.Date;
import java.util.TimerTask;

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
public interface TimerManagerService {

    /**
     * @param taskTimerUuid
     * @return
     */
    boolean isInAlarmSchedule(String taskTimerUuid);

    /**
     * @param taskTimerUuid
     * @return
     */
    boolean isInDueSchedule(String taskTimerUuid);

    /**
     * @param taskTimerUuid
     * @return
     */
    boolean isInOverDueSchedule(String taskTimerUuid);

    /**
     * @param taskTimerUuid
     * @param timerTask
     */
    void addAlarmSchedule(String taskTimerUuid, TimerTask timerTask, Date time);

    /**
     * @param taskTimerUuid
     * @param timerTask
     */
    void addDueSchedule(String taskTimerUuid, TimerTask timerTask, Date time);

    /**
     * @param taskTimerUuid
     * @param timerTask
     */
    void addOverDueSchedule(String taskTimerUuid, TimerTask timerTask, Date time);

    /**
     * @param taskTimerUuid
     * @return
     */
    void removeAlarmSchedule(String taskTimerUuid);

    /**
     * @param taskTimerUuid
     * @return
     */
    void removeDueSchedule(String taskTimerUuid);

    /**
     * @param taskTimerUuid
     * @return
     */
    void removeOverDueSchedule(String taskTimerUuid);

    /**
     * @param taskTimerUuid
     * @return
     */
    void removeAllSchedule(String taskTimerUuid);

    /**
     * 如何描述该方法
     *
     * @param taskTimerUuid
     */
    void handleIncreaseAlarmCounter(String taskTimerUuid);

    /**
     * 如何描述该方法
     *
     * @param taskTimerUuid
     */
    void handleIncreaseOverDueCounter(String taskTimerUuid);

}
