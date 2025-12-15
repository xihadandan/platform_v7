/*
 * @(#)2021年5月31日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.Separator;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Description: 工作时间安排配置
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年5月31日.1	zhulh		2021年5月31日		Create
 * </pre>
 * @date 2021年5月31日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkTimeScheduleConfig extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7970403952217486586L;

    // 应用时间周期1全年、2指定时间周期
    private String periodType;

    // 开始时间，格式MM-dd
    private String fromDate;

    // 结束时间，格式MM-dd
    private String toDate;

    // true/false，是否到下一年
    private boolean toNextYeay;

    // 类型1固定工时、2单双周、3弹性工时
    private String workTimeType;

    // 每周工作时长
    private String workHoursPerWeek;

    // true/false, 设置核心工作日
    private boolean coreWorkDay;

    // 工作时间
    private List<WorkTimeConfig> workTimes;

    /**
     * @return the periodType
     */
    public String getPeriodType() {
        return periodType;
    }

    /**
     * @param periodType 要设置的periodType
     */
    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    /**
     * @return the fromDate
     */
    public String getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate 要设置的fromDate
     */
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the toDate
     */
    public String getToDate() {
        return toDate;
    }

    /**
     * @param toDate 要设置的toDate
     */
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    /**
     * @return the toNextYeay
     */
    public boolean isToNextYeay() {
        return toNextYeay;
    }

    /**
     * @param toNextYeay 要设置的toNextYeay
     */
    public void setToNextYeay(boolean toNextYeay) {
        this.toNextYeay = toNextYeay;
    }

    /**
     * @return the workTimeType
     */
    public String getWorkTimeType() {
        return workTimeType;
    }

    /**
     * @param workTimeType 要设置的workTimeType
     */
    public void setWorkTimeType(String workTimeType) {
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
     * Description: 工作时间配置
     *
     * @author zhulh
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2021年5月31日.1	zhulh		2021年5月31日		Create
     * </pre>
     * @date 2021年5月31日
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class WorkTimeConfig extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 7068423933920242129L;

        // true/false，是否单周，单双周时有效
        private boolean oddWeeks;

        // 工作日，多个以分号隔开，MON星期一、TUE星期二、WED星期三、THU星期四、FRI星期五、SAT星期六、SUN星期日
        private String workDay;

        // 类型1固定时间、2弹性时间
        private String type;

        // 每日工作时长
        private String workHoursPerDay;

        // true/false,设置核心工作时段
        private boolean coreWorkPeriod;

        // 时间段配置
        private List<TimePeriodConfig> timePeriods;

        /**
         * @return the oddWeeks
         */
        public boolean isOddWeeks() {
            return oddWeeks;
        }

        /**
         * @param oddWeeks 要设置的oddWeeks
         */
        public void setOddWeeks(boolean oddWeeks) {
            this.oddWeeks = oddWeeks;
        }

        /**
         * @return the workDay
         */
        public String getWorkDay() {
            return workDay;
        }

        /**
         * @param workDay 要设置的workDay
         */
        public void setWorkDay(String workDay) {
            this.workDay = workDay;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type 要设置的type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return the workHoursPerDay
         */
        public String getWorkHoursPerDay() {
            return workHoursPerDay;
        }

        /**
         * @param workHoursPerDay 要设置的workHoursPerDay
         */
        public void setWorkHoursPerDay(String workHoursPerDay) {
            this.workHoursPerDay = workHoursPerDay;
        }

        /**
         * @return the coreWorkPeriod
         */
        public boolean isCoreWorkPeriod() {
            return coreWorkPeriod;
        }

        /**
         * @param coreWorkPeriod 要设置的coreWorkPeriod
         */
        public void setCoreWorkPeriod(boolean coreWorkPeriod) {
            this.coreWorkPeriod = coreWorkPeriod;
        }

        /**
         * @return the timePeriods
         */
        public List<TimePeriodConfig> getTimePeriods() {
            return timePeriods;
        }

        /**
         * @param timePeriods 要设置的timePeriods
         */
        public void setTimePeriods(List<TimePeriodConfig> timePeriods) {
            this.timePeriods = timePeriods;
        }

    }

    /**
     * Description: 时间段配置
     *
     * @author zhulh
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2021年5月31日.1	zhulh		2021年5月31日		Create
     * </pre>
     * @date 2021年5月31日
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class TimePeriodConfig extends BaseObject {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 3102394689095738271L;

        // 时段名称
        private String name;

        // 开始时间，格式HH:mm
        private String fromTime;

        // 结束时间，格式HH:mm
        private String toTime;

        // true/false，是否到下一天
        private boolean toNextDay;

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
        public String getFromTime() {
            return fromTime;
        }

        /**
         * @param fromTime 要设置的fromTime
         */
        public void setFromTime(String fromTime) {
            this.fromTime = fromTime;
        }

        /**
         * @return the toTime
         */
        public String getToTime() {
            return toTime;
        }

        /**
         * @param toTime 要设置的toTime
         */
        public void setToTime(String toTime) {
            this.toTime = toTime;
        }

        /**
         * @return the toNextDay
         */
        public boolean isToNextDay() {
            return toNextDay;
        }

        /**
         * @param toNextDay 要设置的toNextDay
         */
        public void setToNextDay(boolean toNextDay) {
            this.toNextDay = toNextDay;
        }

        /**
         * @param date
         * @return
         */
        public Date getFromTimeInstance(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String[] fromTimes = StringUtils.split(fromTime, Separator.COLON.getValue());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(fromTimes[0]));
            calendar.set(Calendar.MINUTE, Integer.valueOf(fromTimes[1]));
            return calendar.getTime();
        }

        /**
         * @param date
         * @return
         */
        public Date getToTimeInstance(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String[] toTimes = StringUtils.split(toTime, Separator.COLON.getValue());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(toTimes[0]));
            calendar.set(Calendar.MINUTE, Integer.valueOf(toTimes[1]));
            if (toNextDay) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            return calendar.getTime();
        }

    }

}
