/*
 * @(#)2012-11-12 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.support;

import java.util.Date;

/**
 * Description: 工作时间区段类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-12.1	zhulh		2012-11-12		Create
 * </pre>
 * @date 2012-11-12
 */
public class WorkPeriod {
    /**
     * 开始时间
     */
    private Date fromTime;
    /**
     * 结束时间
     */
    private Date toTime;
    /**
     * 经历的工作日数
     */
    private Integer days;

    // 有效工作日数
    private double workDay;
    // 有效工作小时数
    private double workHour;
    // 有效工作分钟数
    private double workMinute;

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
     * @return the days
     */
    public Integer getDays() {
        return days;
    }

    /**
     * @param days 要设置的days
     */
    public void setDays(Integer days) {
        this.days = days;
    }

    public double getWorkDay() {
        return workDay;
    }

    public void setWorkDay(double workDay) {
        this.workDay = workDay;
    }

    public double getWorkHour() {
        return workHour;
    }

    public void setWorkHour(double workHour) {
        this.workHour = workHour;
    }

    public double getWorkMinute() {
        return workMinute;
    }

    public void setWorkMinute(double workMinute) {
        this.workMinute = workMinute;
    }

}
