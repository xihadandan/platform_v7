/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.dao.TsTimerDao;
import com.wellsoft.pt.timer.entity.TsTimerEntity;
import com.wellsoft.pt.timer.support.TimerWorkTime;
import com.wellsoft.pt.timer.support.TsTimerConfigUsedChecker;
import com.wellsoft.pt.timer.support.TsTimerParam;
import com.wellsoft.pt.timer.support.TsWorkTimePlanUsedChecker;

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
 * 2021年4月7日.1	zhulh		2021年4月7日		Create
 * </pre>
 * @date 2021年4月7日
 */
public interface TsTimerService
        extends JpaService<TsTimerEntity, TsTimerDao, String>, TsTimerConfigUsedChecker, TsWorkTimePlanUsedChecker {

    /**
     * 启动计时器
     *
     * @param timerParam
     * @return
     */
    TsTimerEntity startTimer(TsTimerParam timerParam);

    /**
     * 暂停计时器
     *
     * @param timerUuid
     * @return
     */
    double pauseTimer(String timerUuid);

    /**
     * 恢复计时器
     *
     * @param timerUuid
     * @return
     */
    Date resumeTimer(String timerUuid);

    /**
     * 停止计时器
     *
     * @param timerUuid
     * @return
     */
    double stopTimer(String timerUuid);

    /**
     * 重新开始计时器
     *
     * @param timerUuid
     * @return
     */
    Date restartTimer(String timerUuid);

    /**
     * @param timerUuid
     * @return
     */
    double getRemainingTimeLimit(String timerUuid);

    /**
     * @param timerUuid
     * @return
     */
    String getTimeLimitNameInMinute(String timerUuid);

    /**
     * 变更时限，返回剩余时限
     *
     * @param timerUuid
     * @param timeLimit
     * @return
     */
    int changeTimeLimit(String timerUuid, int timeLimit);

    /**
     * 变更到期时间，返回剩余时限
     *
     * @param timerUuid
     * @param dueTime
     */
    int changeDueTime(String timerUuid, Date dueTime);

    /**
     * 获取逾期时间
     *
     * @param timerEntity
     * @return
     */
    Date getOverDueTime(TsTimerEntity timerEntity);

    /**
     * 计算时间
     *
     * @param timerUuid
     * @param fromTime
     * @param amount
     * @param timingMode
     * @return
     */
    Date calculateTime(String timerUuid, Date fromTime, double amount, String timingMode);

    /**
     * 停止计时器
     *
     * @return
     */
    List<TsTimerEntity> getToScheduleOfDueTimers();

    /**
     * 停止计时器
     *
     * @return
     */
    List<TsTimerEntity> getToScheduleOfOverDueTimers();

    /**
     * @return
     */
    List<TsTimerEntity> getToScheduleOfAlarmTimers();

    /**
     * 到期处理
     *
     * @param timerUuid
     * @param dueTime
     */
    void dueDoing(String timerUuid, Date dueTime);

    /**
     * 强制标识到期信息
     *
     * @param timerUuid
     * @param remark
     */
    void forceMarkDueInfo(String timerUuid, String remark);

    /**
     * 逾期处理
     *
     * @param timerUuid
     * @param overdueTime
     */
    void overDueDoing(String timerUuid, Date overdueTime);

    /**
     * 强制标识逾期信息
     *
     * @param timerUuid
     * @param overdueTime
     * @param remark
     */
    void forceMarkOverDueInfo(String timerUuid, Date overdueTime, String remark);

    /**
     * 预警处理
     *
     * @param timerUuid
     * @param alarmUuid
     * @param alarmTime
     */
    void alarmDoing(String timerUuid, String alarmUuid, Date alarmTime);

    /**
     * 强制标识预警信息
     *
     * @param timerUuid
     * @param alarmUuid
     */
    void forceMarkAlarmInfo(String timerUuid, String alarmUuid);

    /**
     * 获取计时器的工作时间信息
     *
     * @param timerUuid
     * @return
     */
    TimerWorkTime getTimerWorkTime(String timerUuid);
}
