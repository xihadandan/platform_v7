/*
 * @(#)8/26/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.wellsoft.context.base.BaseObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * Description: 计时器工作时间
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/26/24.1	    zhulh		8/26/24		    Create
 * </pre>
 * @date 8/26/24
 */
public class TimerWorkTime extends BaseObject {

    /**
     * 计时器UUID
     */
    private String timerUuid;

    /**
     * 时限
     */
    private Double timeLimit;

    /**
     * 开始时间
     */
    private Date fromTime;
    /**
     * 结束时间
     */
    private Date toTime;

    /**
     * 工作分钟
     */
    private int effectiveWorkMinute;

    /**
     * @param timerUuid
     * @param timeLimit
     * @param fromTime
     * @param toTime
     * @param effectiveWorkMinute
     */
    public TimerWorkTime(String timerUuid, Double timeLimit, Date fromTime, Date toTime, int effectiveWorkMinute) {
        this.timerUuid = timerUuid;
        this.timeLimit = timeLimit;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.effectiveWorkMinute = effectiveWorkMinute;
    }

    /**
     * @return the timerUuid
     */
    public String getTimerUuid() {
        return timerUuid;
    }

    /**
     * @return the timeLimit
     */
    public Double getTimeLimit() {
        return timeLimit;
    }

    /**
     * @return the fromTime
     */
    public Date getFromTime() {
        return fromTime;
    }

    /**
     * @return the toTime
     */
    public Date getToTime() {
        return toTime;
    }

    /**
     * 获取经历的分钟
     *
     * @return
     */
    public int getMinute() {
        Calendar fromCalendar = Calendar.getInstance();
        Calendar toCalendar = Calendar.getInstance();
        fromCalendar.setTime(fromTime);
        toCalendar.setTime(toTime);
        int sign = fromTime.before(toTime) ? 1 : -1;
        LocalDateTime fromDate = LocalDateTime.of(fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                fromCalendar.get(Calendar.DAY_OF_MONTH), fromCalendar.get(Calendar.HOUR_OF_DAY), fromCalendar.get(Calendar.MINUTE));
        LocalDateTime toDate = LocalDateTime.of(toCalendar.get(Calendar.YEAR), toCalendar.get(Calendar.MONTH),
                toCalendar.get(Calendar.DAY_OF_MONTH), toCalendar.get(Calendar.HOUR_OF_DAY), toCalendar.get(Calendar.MINUTE));
        return sign * ((int) ChronoUnit.MINUTES.between(fromDate, toDate) + 1 * sign);
    }

    /**
     * @return the effectiveWorkMinute
     */
    public int getEffectiveWorkMinute() {
        return effectiveWorkMinute;
    }

    /**
     * 获取经历的小时
     *
     * @return
     */
    public double getHour() {
        Calendar fromCalendar = Calendar.getInstance();
        Calendar toCalendar = Calendar.getInstance();
        fromCalendar.setTime(fromTime);
        toCalendar.setTime(toTime);
        int sign = fromTime.before(toTime) ? 1 : -1;
        LocalDateTime fromDate = LocalDateTime.of(fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                fromCalendar.get(Calendar.DAY_OF_MONTH), fromCalendar.get(Calendar.HOUR_OF_DAY), fromCalendar.get(Calendar.MINUTE));
        LocalDateTime toDate = LocalDateTime.of(toCalendar.get(Calendar.YEAR), toCalendar.get(Calendar.MONTH),
                toCalendar.get(Calendar.DAY_OF_MONTH), toCalendar.get(Calendar.HOUR_OF_DAY), toCalendar.get(Calendar.MINUTE));
        return sign * ((int) ChronoUnit.HOURS.between(fromDate, toDate) + 1 * sign);
    }

    /**
     * @return
     */
    public double getEffectiveWorkHour() {
        return Double.valueOf(effectiveWorkMinute) / 60;
    }

    /**
     * 获取经历的天数
     *
     * @return
     */
    public int getDay() {
        Calendar fromCalendar = Calendar.getInstance();
        Calendar toCalendar = Calendar.getInstance();
        fromCalendar.setTime(fromTime);
        toCalendar.setTime(toTime);
        int sign = fromTime.before(toTime) ? 1 : -1;
        LocalDate fromDate = LocalDate.of(fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH));
        LocalDate toDate = LocalDate.of(toCalendar.get(Calendar.YEAR), toCalendar.get(Calendar.MONTH), toCalendar.get(Calendar.DAY_OF_MONTH));
        return sign * ((int) ChronoUnit.DAYS.between(fromDate, toDate) + 1 * sign);
    }

    /**
     * @return
     */
    public double getEffectiveWorkDay() {
        return Double.valueOf(effectiveWorkMinute) / (60 * 24);
    }

}
