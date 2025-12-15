/*
 * @(#)2021年6月3日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.date.DateUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.Calendar;
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
 * 2021年6月3日.1	zhulh		2021年6月3日		Create
 * </pre>
 * @date 2021年6月3日
 */
public class WorkTime extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5037134827792792218L;

    private Date workDate;

    // 是否补班的工作时间
    private boolean isMakeupWorkDay;

    // 补班日期的日期
    private Date makeupDate;

    // 每日工作时长
    private Double workHoursPerDay;

    private List<TimePeriod> timePeriods = Lists.newArrayList();

    /**
     * @param workDate
     */
    public WorkTime(Date workDate) {
        this.workDate = workDate;
    }

    /**
     * @return the workDate
     */
    public Date getWorkDate() {
        return workDate;
    }

    /**
     * @param isMakeupWorkDay
     */
    public void setIsMakeupWorkDay(boolean isMakeupWorkDay) {
        this.isMakeupWorkDay = isMakeupWorkDay;
    }

    /**
     * @return the isMakeupWorkDay
     */
    public boolean isMakeupWorkDay() {
        return isMakeupWorkDay;
    }

    /**
     * @param makeupDate
     */
    public void setMakeupDate(Date makeupDate) {
        this.makeupDate = makeupDate;

        // 补班工作时间，替换日期
        for (TimePeriod timePeriod : timePeriods) {
            changeTimePeriodOfDate(timePeriod, this.makeupDate);
        }
    }

    /**
     * @param timePeriod
     * @param makeupDate
     */
    private void changeTimePeriodOfDate(TimePeriod timePeriod, Date makeupDate) {
        Calendar fromCalendar = DateUtils.getCalendar(timePeriod.getFromTime());
        Calendar toCalendar = DateUtils.getCalendar(timePeriod.getToTime());
        Calendar makeupDateCalendar = DateUtils.getCalendar(makeupDate);

        fromCalendar.set(Calendar.YEAR, makeupDateCalendar.get(Calendar.YEAR));
        fromCalendar.set(Calendar.MONTH, makeupDateCalendar.get(Calendar.MONTH));
        fromCalendar.set(Calendar.DAY_OF_MONTH, makeupDateCalendar.get(Calendar.DAY_OF_MONTH));

        toCalendar.set(Calendar.YEAR, makeupDateCalendar.get(Calendar.YEAR));
        toCalendar.set(Calendar.MONTH, makeupDateCalendar.get(Calendar.MONTH));
        toCalendar.set(Calendar.DAY_OF_MONTH, makeupDateCalendar.get(Calendar.DAY_OF_MONTH));

        timePeriod.setFromTime(fromCalendar.getTime());
        timePeriod.setToTime(toCalendar.getTime());
    }

    /**
     * @param fromTime
     * @param toTime
     */
    public void addTimePeriod(String name, Date fromTime, Date toTime) {
        TimePeriod timePeriod = new TimePeriod(name, fromTime, toTime);
        timePeriods.add(timePeriod);

        // 补班工作时间，替换日期
        if (isMakeupWorkDay && makeupDate != null) {
            changeTimePeriodOfDate(timePeriod, makeupDate);
        }
    }

    /**
     * @return
     */
    public Date getFromTime() {
        if (CollectionUtils.isNotEmpty(timePeriods)) {
            return timePeriods.get(0).getFromTime();
        }
        return workDate;
    }

    /**
     * @return
     */
    public Date getEndTime() {
        if (CollectionUtils.isNotEmpty(timePeriods)) {
            return timePeriods.get(timePeriods.size() - 1).getToTime();
        }
        return workDate;
    }

    /**
     * @param time
     * @return
     */
    public boolean isInTimePeriod(Date time) {
        for (TimePeriod timePeriod : timePeriods) {
            if (timePeriod.isInTimePeriod(time)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param time
     * @return
     */
    public Date getToTimeInTimePeriod(Date time) {
        for (TimePeriod timePeriod : timePeriods) {
            if (timePeriod.isInTimePeriod(time)) {
                return timePeriod.getToTime();
            }
        }
        return null;
    }

    /**
     * @param time
     * @return
     */
    public Date getNextFromTimeInTimePeriod(Date time) {
        int periodIndex = 0;
        for (int index = 0; index < timePeriods.size(); index++) {
            TimePeriod timePeriod = timePeriods.get(index);
            if (timePeriod.isInTimePeriod(time)) {
                periodIndex = ++index;
                break;
            }
        }
        return timePeriods.get(periodIndex).getFromTime();
    }

    /**
     * @param time
     * @return
     */
    public boolean isBeforeEndTime(Date time) {
        return time.before(getEndTime());
    }

    /**
     * @param time
     */
    public boolean isAfterEndTime(Date time) {
        return time.after(getEndTime());
    }

    /**
     * @param time
     * @return
     */
    public Date getPreviousEndTimeBetweenTimePeriod(Date time) {
        int periodIndex = 0;
        for (int index = 0; index < timePeriods.size(); index++) {
            TimePeriod timePeriod = timePeriods.get(index);
            if (time.before(timePeriod.getFromTime())) {
                periodIndex = --index;
                break;
            }
        }
        if (periodIndex > 0) {
            return timePeriods.get(periodIndex).getToTime();
        }
        return time;
    }

    /**
     * @param time
     * @return
     */
    public Date getNextFromTimeBetweenTimePeriod(Date time) {
        int periodIndex = 0;
        for (int index = 0; index < timePeriods.size(); index++) {
            TimePeriod timePeriod = timePeriods.get(index);
            if (time.before(timePeriod.getFromTime())) {
                periodIndex = index;
                break;
            }
        }
        return timePeriods.get(periodIndex).getFromTime();
    }

    /**
     * @return
     */
    public long getTimeInMillisecond() {
        long millisecond = 0;
        for (TimePeriod timePeriod : timePeriods) {
            millisecond += (timePeriod.getToTime().getTime() - timePeriod.getFromTime().getTime());
        }
        return millisecond;
    }

    /**
     * @param fromTime
     * @param toTime
     * @return
     */
    public long getTimeInMillisecond(Date fromTime, Date toTime) {
        long millisecond = 0;
        for (TimePeriod timePeriod : timePeriods) {
            Date tmpFromTime = timePeriod.getFromTime();
            Date tmpToTime = timePeriod.getToTime();
//            // 补班工作时间，替换日期
//            if (isMakeupWorkDay) {
//                tmpFromTime = getTimeOfMakeupDate(tmpFromTime);
//                tmpToTime = getTimeOfMakeupDate(tmpToTime);
//            }
            if (fromTime.after(tmpFromTime)) {
                tmpFromTime = fromTime;
            }
            if (toTime.before(tmpToTime)) {
                tmpToTime = toTime;
            }
            if (tmpFromTime.after(tmpToTime)) {
                continue;
            }
            millisecond += (tmpToTime.getTime() - tmpFromTime.getTime());
        }
        return millisecond;
    }

    /**
     * @param fromTime
     * @param toTime
     * @return
     */
    public long getTimeInMillisecondBy24Rule(Date fromTime, Date toTime) {
        long millisecond = 0;
        for (TimePeriod timePeriod : timePeriods) {
            Date tmpFromTime = timePeriod.getFromTime();
            Date tmpToTime = timePeriod.getToTime();
//            // 补班工作时间，替换日期
//            if (isMakeupWorkDay) {
//                tmpFromTime = getTimeOfMakeupDate(tmpFromTime);
//                tmpToTime = getTimeOfMakeupDate(tmpToTime);
//            }
            tmpFromTime = DateUtils.getMinTimeCalendar(DateUtils.getCalendar(tmpFromTime)).getTime();
            Calendar maxCalendar = DateUtils.getMaxTimeCalendar(DateUtils.getCalendar(tmpToTime));
            // 加一秒到24小时整
            maxCalendar.add(Calendar.SECOND, 1);
            tmpToTime = maxCalendar.getTime();
            if (fromTime.after(tmpFromTime)) {
                tmpFromTime = fromTime;
            }
            if (toTime.before(tmpToTime)) {
                tmpToTime = toTime;
            }
            if (tmpFromTime.after(tmpToTime)) {
                continue;
            }
            millisecond += (tmpToTime.getTime() - tmpFromTime.getTime());
            break;
        }
        return millisecond;
    }


//    /**
//     * @param toTime
//     * @return
//     */
//    private Date getTimeOfMakeupDate(Date toTime) {
//        Calendar calendar = DateUtils.getCalendar(toTime);
//        Calendar makeupDateCalendar = DateUtils.getCalendar(makeupDate);
//        calendar.set(makeupDateCalendar.get(Calendar.YEAR), makeupDateCalendar.get(Calendar.MONTH),
//                makeupDateCalendar.get(Calendar.DAY_OF_MONTH));
//        return calendar.getTime();
//    }

    /**
     * @author zhulh
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2021年6月3日.1	zhulh		2021年6月3日		Create
     * </pre>
     * @date 2021年6月3日
     */
    public static final class TimePeriod extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -3620901035758071677L;

        // 时段名称
        private String name;

        // 开始时间，格式HH:mm
        private Date fromTime;

        // 结束时间，格式HH:mm
        private Date toTime;

        /**
         * 如何描述该构造方法
         *
         * @param name
         * @param fromTime
         * @param toTime
         */
        public TimePeriod(String name, Date fromTime, Date toTime) {
            super();
            this.name = name;
            this.fromTime = fromTime;
            this.toTime = toTime;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
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
         * @param time
         * @return
         */
        public boolean isInTimePeriod(Date time) {
            if (time.before(fromTime) || time.after(toTime)) {
                return false;
            }
            return true;
        }

    }

    public Double getWorkHoursPerDay() {
        return workHoursPerDay;
    }

    public void setWorkHoursPerDay(Double workHoursPerDay) {
        this.workHoursPerDay = workHoursPerDay;
    }
}
