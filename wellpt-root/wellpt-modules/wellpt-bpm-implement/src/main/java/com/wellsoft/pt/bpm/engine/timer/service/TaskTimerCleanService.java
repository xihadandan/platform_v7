/*
 * @(#)2018年10月22日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.service;

import com.wellsoft.pt.bpm.engine.entity.TaskTimer;

import java.util.Date;
import java.util.List;

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
public interface TaskTimerCleanService {

    /**
     * 获取将要进行调度清理的预警计时器
     *
     * @return
     */
    List<TaskTimer> getToScheduleOfAlarmTaskTimers();

    /**
     * 如何描述该方法
     *
     * @param timerUuid
     * @return
     */
    boolean checkAlarmTaskTimer(String timerUuid);

    /**
     * 强制停止预警的计时器
     *
     * @param taskTimerUuid
     */
    void forceStopAlarmTaskTimer(String taskTimerUuid);

    void forceStopAlarmTaskTimer(String taskTimerUuid, String msg);

    /**
     * 获取将要进行调度清理的逾期计时器
     *
     * @return
     */
    List<TaskTimer> getToScheduleOfOverDueTaskTimers();

    void forceStopDueTaskTimer(String taskTimerUuid, String remark);

    /**
     * 如何描述该方法
     *
     * @param timerUuid
     * @return
     */
    boolean checkOverDueTaskTimer(String timerUuid);

    /**
     * 强制停止逾期的计时器
     *
     * @param taskTimerUuid
     */
    void forceStopOverDueTaskTimer(String taskTimerUuid);

    void forceStopOverDueTaskTimer(String taskTimerUuid, Date overdueTime, String remark);
}
