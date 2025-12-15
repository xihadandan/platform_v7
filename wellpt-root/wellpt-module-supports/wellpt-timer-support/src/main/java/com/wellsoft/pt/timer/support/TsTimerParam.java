/*
 * @(#)2021年5月28日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年5月28日.1	zhulh		2021年5月28日		Create
 * </pre>
 * @date 2021年5月28日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TsTimerParam extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7188859668246094320L;

    // 计时配置UUID
    private String timerConfigUuid;

    // 计时方式
    private String timingMode;

    // 工作时间方案UUID
    private String workTimePlanUuid;

    // 开始时间
    private Date startTime;

    // 时限是否为指定日期
    private boolean isDateOfLimitTime;

    // 办理时限
    private Double timeLimit;

    // 截止时间
    private Date dueTime;

    // 监听器
    private String listener;

    // 预警信息
    private List<TsTimerAlarm> timerAlarms;

    // 附加数据
    private Map<String, Object> extraData;

    /**
     * @return the timerConfigUuid
     */
    public String getTimerConfigUuid() {
        return timerConfigUuid;
    }

    /**
     * @param timerConfigUuid 要设置的timerConfigUuid
     */
    public void setTimerConfigUuid(String timerConfigUuid) {
        this.timerConfigUuid = timerConfigUuid;
    }

    /**
     * @return the timingMode
     */
    public String getTimingMode() {
        return timingMode;
    }

    /**
     * @param timingMode 要设置的timingMode
     */
    public void setTimingMode(String timingMode) {
        this.timingMode = timingMode;
    }

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
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 要设置的startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the isDateOfLimitTime
     */
    public boolean isDateOfLimitTime() {
        return isDateOfLimitTime;
    }

    /**
     * @param isDateOfLimitTime 要设置的isDateOfLimitTime
     */
    public void setDateOfLimitTime(boolean isDateOfLimitTime) {
        this.isDateOfLimitTime = isDateOfLimitTime;
    }

    /**
     * @return the timeLimit
     */
    public Double getTimeLimit() {
        return timeLimit;
    }

    /**
     * @param timeLimit 要设置的timeLimit
     */
    public void setTimeLimit(Double timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * @return the dueTime
     */
    public Date getDueTime() {
        return dueTime;
    }

    /**
     * @param dueTime 要设置的dueTime
     */
    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * @return the listener
     */
    public String getListener() {
        return listener;
    }

    /**
     * @param listener 要设置的listener
     */
    public void setListener(String listener) {
        this.listener = listener;
    }

    /**
     * @return the timerAlarms
     */
    public List<TsTimerAlarm> getTimerAlarms() {
        return timerAlarms;
    }

    /**
     * @param timerAlarms 要设置的timerAlarms
     */
    public void setTimerAlarms(List<TsTimerAlarm> timerAlarms) {
        this.timerAlarms = timerAlarms;
    }

    /**
     * @return the extraData
     */
    public Map<String, Object> getExtraData() {
        return extraData;
    }

    /**
     * @param extraData 要设置的extraData
     */
    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }

    public static final class TsTimerAlarm extends BaseObject {
        private String id;

        // 预警时限
        private Double timeLimit;

        // 计时方式
        private String timingMode;

        // 提醒总次数
        private Integer alarmCount;

        /**
         *
         */
        public TsTimerAlarm() {
        }

        /**
         * @param id
         * @param timeLimit
         * @param timingMode
         * @param alarmCount
         */
        public TsTimerAlarm(String id, Double timeLimit, String timingMode, Integer alarmCount) {
            this.id = id;
            this.timeLimit = timeLimit;
            this.timingMode = timingMode;
            this.alarmCount = alarmCount;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the timeLimit
         */
        public Double getTimeLimit() {
            return timeLimit;
        }

        /**
         * @param timeLimit 要设置的timeLimit
         */
        public void setTimeLimit(Double timeLimit) {
            this.timeLimit = timeLimit;
        }

        /**
         * @return the timingMode
         */
        public String getTimingMode() {
            return timingMode;
        }

        /**
         * @param timingMode 要设置的timingMode
         */
        public void setTimingMode(String timingMode) {
            this.timingMode = timingMode;
        }

        /**
         * @return the alarmCount
         */
        public Integer getAlarmCount() {
            return alarmCount;
        }

        /**
         * @param alarmCount 要设置的alarmCount
         */
        public void setAlarmCount(Integer alarmCount) {
            this.alarmCount = alarmCount;
        }
    }
}
