/*
 * @(#)2021年6月1日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
 * 2021年6月1日.1	zhulh		2021年6月1日		Create
 * </pre>
 * @date 2021年6月1日
 */
public class WorkTimePlan extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -180319035874266980L;

    // 工作时间方案UUID
    private String workTimePlanUuid;

    // 年份
    private int year;

    // 工作时间安排
    private List<WorkTimeScheduleInstance> workTimeSchedules;

    // 节假日安排
    private List<HolidaySchedule> holidaySchedules;

    /**
     * @return the workTimePlanUuid
     */
    public String getWorkTimePlanUuid() {
        return workTimePlanUuid;
    }

    /**
     * @param workTimePlanUuid 要设置的workTimePlanUuid
     */
    public void setWorkTimePlanUuid(String workTimePlanUuid) {
        this.workTimePlanUuid = workTimePlanUuid;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year 要设置的year
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the workTimeSchedules
     */
    public List<WorkTimeScheduleInstance> getWorkTimeSchedules() {
        return workTimeSchedules;
    }

    /**
     * @param workTimeSchedules 要设置的workTimeSchedules
     */
    public void setWorkTimeSchedules(List<WorkTimeScheduleInstance> workTimeSchedules) {
        this.workTimeSchedules = workTimeSchedules;
    }

    /**
     * @return the holidaySchedules
     */
    public List<HolidaySchedule> getHolidaySchedules() {
        return holidaySchedules;
    }

    /**
     * @param holidaySchedules 要设置的holidaySchedules
     */
    public void setHolidaySchedules(List<HolidaySchedule> holidaySchedules) {
        this.holidaySchedules = holidaySchedules;
    }

    /**
     * Description: 节假日安排
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
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class HolidaySchedule extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -9068701332783278966L;

        // 引用的节假日UUID
        private String holidayUuid;
        // 引用的节假日名称
        private String holidayName;
        // 年份
        private int year;
        // 具体年份节假日日期，格式yyyy-MM-dd
        private String holidayInstanceDate;
        // true/false，自动更新假期
        private boolean autoUpdate;
        // 开始日期，格式yyyy-MM-dd
        private String fromDate;
        // 结束日期，格式yyyy-MM-dd
        private String toDate;
        // 补班日期，格式yyyy-MM-dd|yyyy-MM-dd，多个以分号隔开
        private String makeupDate;
        // 备注
        private String remark;

        /**
         * @return the holidayUuid
         */
        public String getHolidayUuid() {
            return holidayUuid;
        }

        /**
         * @param holidayUuid 要设置的holidayUuid
         */
        public void setHolidayUuid(String holidayUuid) {
            this.holidayUuid = holidayUuid;
        }

        /**
         * @return the holidayName
         */
        public String getHolidayName() {
            return holidayName;
        }

        /**
         * @param holidayName 要设置的holidayName
         */
        public void setHolidayName(String holidayName) {
            this.holidayName = holidayName;
        }

        /**
         * @return the year
         */
        public int getYear() {
            return year;
        }

        /**
         * @param year 要设置的year
         */
        public void setYear(int year) {
            this.year = year;
        }

        /**
         * @return the holidayInstanceDate
         */
        public String getHolidayInstanceDate() {
            return holidayInstanceDate;
        }

        /**
         * @param holidayInstanceDate 要设置的holidayInstanceDate
         */
        public void setHolidayInstanceDate(String holidayInstanceDate) {
            this.holidayInstanceDate = holidayInstanceDate;
        }

        /**
         * @return the autoUpdate
         */
        public boolean isAutoUpdate() {
            return autoUpdate;
        }

        /**
         * @param autoUpdate 要设置的autoUpdate
         */
        public void setAutoUpdate(boolean autoUpdate) {
            this.autoUpdate = autoUpdate;
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
         * @return the makeupDate
         */
        public String getMakeupDate() {
            return makeupDate;
        }

        /**
         * @param makeupDate 要设置的makeupDate
         */
        public void setMakeupDate(String makeupDate) {
            this.makeupDate = makeupDate;
        }

        /**
         * @return the remark
         */
        public String getRemark() {
            return remark;
        }

        /**
         * @param remark 要设置的remark
         */
        public void setRemark(String remark) {
            this.remark = remark;
        }

    }

}
