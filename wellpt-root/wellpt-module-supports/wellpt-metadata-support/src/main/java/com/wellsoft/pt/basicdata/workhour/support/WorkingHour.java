/*
 * @(#)2012-11-12 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.support;

import com.wellsoft.context.util.date.DateUtils;

import java.util.Date;

/**
 * Description: 某天的工作时间类
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
public class WorkingHour {
    /**
     * 是否为工作时间
     */
    private Boolean isWorkHour;
    /**
     * 是否为工作日
     */
    private Boolean isWorkDay;
    /**
     * 上午上班时间
     */
    private Date fromTime1;
    /**
     * 下午下班时间
     */
    private Date toTime1;
    /**
     * 下午上班时间
     */
    private Date fromTime2;
    /**
     * 下午下班时间
     */
    private Date toTime2;

    /**
     * @return the isWorkHour
     */
    public Boolean getIsWorkHour() {
        return isWorkHour;
    }

    /**
     * @param isWorkHour 要设置的isWorkHour
     */
    public void setIsWorkHour(Boolean isWorkHour) {
        this.isWorkHour = isWorkHour;
    }

    /**
     * @return the isWorkDay
     */
    public Boolean getIsWorkDay() {
        return isWorkDay;
    }

    /**
     * @param isWorkDay 要设置的isWorkDay
     */
    public void setIsWorkDay(Boolean isWorkDay) {
        this.isWorkDay = isWorkDay;
    }

    /**
     * @return the fromTime1
     */
    public Date getFromTime1() {
        return fromTime1;
    }

    /**
     * @param fromTime1 要设置的fromTime1
     */
    public void setFromTime1(Date fromTime1) {
        this.fromTime1 = fromTime1;
    }

    /**
     * @return the toTime1
     */
    public Date getToTime1() {
        return toTime1;
    }

    /**
     * @param toTime1 要设置的toTime1
     */
    public void setToTime1(Date toTime1) {
        this.toTime1 = toTime1;
    }

    /**
     * @return the fromTime2
     */
    public Date getFromTime2() {
        return fromTime2;
    }

    /**
     * @param fromTime2 要设置的fromTime2
     */
    public void setFromTime2(Date fromTime2) {
        this.fromTime2 = fromTime2;
    }

    /**
     * @return the toTime2
     */
    public Date getToTime2() {
        return toTime2;
    }

    /**
     * @param toTime2 要设置的toTime2
     */
    public void setToTime2(Date toTime2) {
        this.toTime2 = toTime2;
    }

    public Double getTotalWorkHour() {
        double totalWorkHour = 0;
        if (fromTime1 != null && toTime1 != null) {
            totalWorkHour = DateUtils.calculateAsHour(toTime1, fromTime1);
        }
        if (fromTime2 != null && toTime2 != null) {
            totalWorkHour = totalWorkHour + DateUtils.calculateAsHour(toTime2, fromTime2);
        }
        return totalWorkHour;
    }

}
