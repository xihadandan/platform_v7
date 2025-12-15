/*
 * @(#)2021年6月10日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.date.DateUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
 * 2021年6月10日.1	zhulh		2021年6月10日		Create
 * </pre>
 * @date 2021年6月10日
 */
public class WorkTimePeriod extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7995165594612252072L;

    /**
     * 开始时间
     */
    private Date fromTime;
    /**
     * 结束时间
     */
    private Date toTime;

    private List<WorkTime> workTimes = Lists.newArrayList();

    /**
     * @param fromTime
     * @param toTime
     */
    public WorkTimePeriod(Date fromTime, Date toTime) {
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    /**
     * @return the fromTime
     */
    public Date getFromTime() {
        return fromTime;
    }

    /**
     * @param fromTime 要设置的fromTime
     */
    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    /**
     * @return the toTime
     */
    public Date getToTime() {
        return toTime;
    }

    /**
     * @param toTime 要设置的toTime
     */
    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }

    /**
     * @param workTime
     */
    public void addWorkTime(WorkTime workTime) {
        if (workTime != null) {
            workTimes.add(workTime);
        }
    }

    /**
     * 获取经历的分钟
     *
     * @return
     */
    public int getMinute() {
        int sign = fromTime.before(toTime) ? 1 : -1;
        LocalDateTime fromDate = new Timestamp(fromTime.getTime()).toLocalDateTime();
        LocalDateTime toDate = new Timestamp(toTime.getTime()).toLocalDateTime();
        return sign * ((int) ChronoUnit.MINUTES.between(fromDate, toDate) + 1 * sign);
    }

    /**
     * 获取有效的工作分钟
     *
     * @return
     */
    public int getEffectiveWorkMinute() {
        long milisecond = 0;
        for (WorkTime workTime : workTimes) {
            milisecond += workTime.getTimeInMillisecond(fromTime, toTime);
        }
        return DateUtils.millisecondToMinute(milisecond).intValue();
    }

    /**
     * 获取有效的工作分钟(24小时制)
     *
     * @return
     */
    public int getEffectiveWorkMinuteBy24Rule() {
        long milisecond = 0;
        for (WorkTime workTime : workTimes) {
            milisecond += workTime.getTimeInMillisecondBy24Rule(fromTime, toTime);
        }
        return DateUtils.millisecondToMinute(milisecond).intValue();
    }

    /**
     * 获取经历的小时
     *
     * @return
     */
    public double getHour() {
        int sign = fromTime.before(toTime) ? 1 : -1;
        LocalDateTime fromDate = new Timestamp(fromTime.getTime())
                .toLocalDateTime();
        LocalDateTime toDate = new Timestamp(toTime.getTime())
                .toLocalDateTime();
        return sign * ((int) ChronoUnit.HOURS.between(fromDate, toDate) + 1 * sign);
    }

    /**
     * 获取有效的工作小时
     *
     * @return
     */
    public double getEffectiveWorkHour() {
        return (int) getEffectiveWorkMinute() / 60;
    }

    /**
     * 获取有效的工作小时(24小时制)
     *
     * @return
     */
    public double getEffectiveWorkHourBy24Rule() {
        return (int) getEffectiveWorkMinuteBy24Rule() / 60;
    }

    /**
     * 获取经历的天数
     *
     * @return
     */
    public int getDay() {
        int sign = fromTime.before(toTime) ? 1 : -1;
        LocalDate fromDate = new java.sql.Date(fromTime.getTime()).toLocalDate();
        LocalDate toDate = new java.sql.Date(toTime.getTime()).toLocalDate();
        return sign * ((int) ChronoUnit.DAYS.between(fromDate, toDate) + 1 * sign);
    }

    /**
     * 获取有效的工作日数
     *
     * @return
     */
    public Double getEffectiveWorkDay() {
        double workHour = 0;
        int i = 0;
        for (WorkTime workTime : workTimes) {
            if (workTime.getWorkHoursPerDay() != null) {
                workHour += workTime.getWorkHoursPerDay();
                i++;
            }
        }
        if (i > 0) {
            return this.getEffectiveWorkMinute() / ((workHour / i) * 60);
        }
        return workHour;
    }
}
