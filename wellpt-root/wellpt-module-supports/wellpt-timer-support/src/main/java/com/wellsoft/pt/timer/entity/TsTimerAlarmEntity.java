/*
 * @(#)8/8/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 计时器预警信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/8/24.1	    zhulh		8/8/24		    Create
 * </pre>
 * @date 8/8/24
 */
@Entity
@Table(name = "TS_TIMER_ALARM")
@DynamicUpdate
@DynamicInsert
public class TsTimerAlarmEntity extends com.wellsoft.context.jdbc.entity.Entity {

    private static final long serialVersionUID = -7642875994223679450L;

    // 预警ID
    private String id;

    // 预警时限
    private Double timeLimit;

    // 计时方式
    private String timingMode;

    // 提醒总次数
    private Integer totalAlarmCount;

    // 当前已提醒次数
    private Integer currentAlarmCount;

    // 预警时间
    private Date alarmTime;

    // 预警处理是否已经处理完成
    private Boolean alarmDoingDone;

    // 预警删除状态,0正常，1删除
    private Integer deleteStatus;

    // 计时器UUID
    private String timerUuid;

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
     * @return the totalAlarmCount
     */
    public Integer getTotalAlarmCount() {
        return totalAlarmCount;
    }

    /**
     * @param totalAlarmCount 要设置的totalAlarmCount
     */
    public void setTotalAlarmCount(Integer totalAlarmCount) {
        this.totalAlarmCount = totalAlarmCount;
    }

    /**
     * @return the currentAlarmCount
     */
    public Integer getCurrentAlarmCount() {
        return currentAlarmCount;
    }

    /**
     * @param currentAlarmCount 要设置的currentAlarmCount
     */
    public void setCurrentAlarmCount(Integer currentAlarmCount) {
        this.currentAlarmCount = currentAlarmCount;
    }

    /**
     * @return the alarmTime
     */
    public Date getAlarmTime() {
        return alarmTime;
    }

    /**
     * @param alarmTime 要设置的alarmTime
     */
    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    /**
     * @return the alarmDoingDone
     */
    public Boolean getAlarmDoingDone() {
        return alarmDoingDone;
    }

    /**
     * @param alarmDoingDone 要设置的alarmDoingDone
     */
    public void setAlarmDoingDone(Boolean alarmDoingDone) {
        this.alarmDoingDone = alarmDoingDone;
    }

    /**
     * @return the deleteStatus
     */
    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    /**
     * @param deleteStatus 要设置的deleteStatus
     */
    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    /**
     * @return the timerUuid
     */
    public String getTimerUuid() {
        return timerUuid;
    }

    /**
     * @param timerUuid 要设置的timerUuid
     */
    public void setTimerUuid(String timerUuid) {
        this.timerUuid = timerUuid;
    }

}
