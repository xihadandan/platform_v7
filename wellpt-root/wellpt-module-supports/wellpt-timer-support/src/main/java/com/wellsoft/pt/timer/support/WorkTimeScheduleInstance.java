/*
 * @(#)2021年6月1日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.timer.enums.EnumWorkTimeType;
import com.wellsoft.pt.timer.support.WorkTimeScheduleConfig.WorkTimeConfig;
import org.apache.commons.compress.utils.Lists;

import java.util.Date;
import java.util.List;

/**
 * Description: 工作时间安排实例
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年6月1日.1	zhulh		2021年6月1日		Create
 * </pre>
 * @date 2021年6月1日
 */
public class WorkTimeScheduleInstance extends BaseObject implements Comparable<WorkTimeScheduleInstance> {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2268919488204508485L;

    // 开始时间
    private Date fromDate;

    // 结束时间
    private Date toDate;

    // 类型1固定工时、2单双周、3弹性工时
    private EnumWorkTimeType workTimeType;

    // 每周工作时长
    private String workHoursPerWeek;

    // true/false, 设置核心工作日
    private boolean coreWorkDay;

    // 工作时间
    private List<WorkTimeConfig> workTimes;

    /**
     * @return the fromDate
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate 要设置的fromDate
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the toDate
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * @param toDate 要设置的toDate
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * @return the workTimeType
     */
    public EnumWorkTimeType getWorkTimeType() {
        return workTimeType;
    }

    /**
     * @param workTimeType 要设置的workTimeType
     */
    public void setWorkTimeType(EnumWorkTimeType workTimeType) {
        this.workTimeType = workTimeType;
    }

    /**
     * @return the workHoursPerWeek
     */
    public String getWorkHoursPerWeek() {
        return workHoursPerWeek;
    }

    /**
     * @param workHoursPerWeek 要设置的workHoursPerWeek
     */
    public void setWorkHoursPerWeek(String workHoursPerWeek) {
        this.workHoursPerWeek = workHoursPerWeek;
    }

    /**
     * @return the coreWorkDay
     */
    public boolean isCoreWorkDay() {
        return coreWorkDay;
    }

    /**
     * @param coreWorkDay 要设置的coreWorkDay
     */
    public void setCoreWorkDay(boolean coreWorkDay) {
        this.coreWorkDay = coreWorkDay;
    }

    /**
     * @return the workTimes
     */
    public List<WorkTimeConfig> getWorkTimes() {
        return workTimes;
    }

    /**
     * @param workTimes 要设置的workTimes
     */
    public void setWorkTimes(List<WorkTimeConfig> workTimes) {
        this.workTimes = workTimes;
    }

    /**
     * @param oddWeeks
     * @return
     */
    public List<WorkTimeConfig> getWorkTimes(boolean oddWeeks) {
        List<WorkTimeConfig> retWorkTimes = Lists.newArrayList();
        // 单周
        if (oddWeeks) {
            for (WorkTimeConfig workTime : workTimes) {
                if (workTime.isOddWeeks()) {
                    retWorkTimes.add(workTime);
                }
            }
        } else {
            // 双周
            for (WorkTimeConfig workTime : workTimes) {
                if (!workTime.isOddWeeks()) {
                    retWorkTimes.add(workTime);
                }
            }
        }
        return retWorkTimes;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(WorkTimeScheduleInstance o) {
        return this.fromDate.compareTo(o.getFromDate());
    }

    /**
     * @param date
     * @return
     */
    public boolean isDateInSchedule(Date date) {
        Date compareTime = DateUtils.getMinTimeCalendar(DateUtils.getCalendar(date)).getTime();
        if (compareTime.before(this.getFromDate()) || compareTime.after(this.getToDate())) {
            return false;
        }
        return true;
    }

}
