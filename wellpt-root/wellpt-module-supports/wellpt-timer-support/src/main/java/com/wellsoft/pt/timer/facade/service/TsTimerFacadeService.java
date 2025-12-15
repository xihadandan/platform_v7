/*
 * @(#)2021年4月8日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.timer.dto.TsTimerDto;
import com.wellsoft.pt.timer.support.TimerWorkTime;
import com.wellsoft.pt.timer.support.TsTimerParam;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月8日.1	zhulh		2021年4月8日		Create
 * </pre>
 * @date 2021年4月8日
 */
public interface TsTimerFacadeService extends Facade {

    /**
     * 启动计时器
     *
     * @param timerParam
     * @return
     */
    TsTimerDto startTimer(TsTimerParam timerParam);

    /**
     * 根据计时器UUID停止计时，返回剩余时限
     *
     * @param timerUuid
     * @return
     */
    double pauseTimer(String timerUuid);

    /**
     * 恢复计时，返回到期时间
     *
     * @param timerUuid
     * @return
     */
    Date resumeTimer(String timerUuid);

    /**
     * @param timerUuid
     * @return
     */
    double stopTimer(String timerUuid);

    /**
     * 重新开始计时，返回到期时间
     *
     * @param timerUuid
     * @return
     */
    Date restartTimer(String timerUuid);

    /**
     * 根据计时器UUID获取计时器
     *
     * @param timerUuid
     * @return
     */
    TsTimerDto getTimer(String timerUuid);

    /**
     * 根据计时器UUID获取剩余时限
     *
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
     * @param timerUuid
     * @return
     */
    Date getOverDueTime(String timerUuid);

    /**
     * @param timerUuid
     * @param fromTime
     * @param amount
     * @param timingMode
     * @return
     */
    Date calculateTime(String timerUuid, Date fromTime, double amount, String timingMode);

    /**
     * 获取计时器的工作时间信息
     *
     * @param timerUuid
     * @return
     */
    TimerWorkTime getTimerWorkTime(String timerUuid);
    
}
