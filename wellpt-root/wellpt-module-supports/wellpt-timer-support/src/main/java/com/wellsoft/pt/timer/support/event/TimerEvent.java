/*
 * @(#)8/9/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support.event;

import com.wellsoft.context.base.BaseObject;

import java.util.Date;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/9/24.1	    zhulh		8/9/24		    Create
 * </pre>
 * @date 8/9/24
 */
public class TimerEvent extends BaseObject {
    private String timerUuid;

    private Integer timerStatus;
    private Integer timingState;
    private Date dueTime;

    private Date overdueTime;

    private String timerData;

    private String alarmUuid;
    private String alarmId;
    private Date alarmTime;


    /**
     * @param timerUuid
     * @param timerStatus
     * @param timingState
     * @param dueTime
     * @param overdueTime
     * @param timerData
     * @param alarmUuid
     * @param alarmId
     * @param alarmTime
     */
    public TimerEvent(String timerUuid, Integer timerStatus, Integer timingState, Date dueTime, Date overdueTime, String timerData,
                      String alarmUuid, String alarmId, Date alarmTime) {
        this.timerUuid = timerUuid;
        this.dueTime = dueTime;
        this.timerStatus = timerStatus;
        this.timingState = timingState;
        this.overdueTime = overdueTime;
        this.timerData = timerData;
        this.alarmUuid = alarmUuid;
        this.alarmId = alarmId;
        this.alarmTime = alarmTime;
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
     * @return the overdueTime
     */
    public Date getOverdueTime() {
        return overdueTime;
    }

    /**
     * @param overdueTime 要设置的overdueTime
     */
    public void setOverdueTime(Date overdueTime) {
        this.overdueTime = overdueTime;
    }

    /**
     * @return the alarmUuid
     */
    public String getAlarmUuid() {
        return alarmUuid;
    }

    /**
     * @param alarmUuid 要设置的alarmUuid
     */
    public void setAlarmUuid(String alarmUuid) {
        this.alarmUuid = alarmUuid;
    }

    /**
     * @return the alarmId
     */
    public String getAlarmId() {
        return alarmId;
    }

    /**
     * @param alarmId 要设置的alarmId
     */
    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
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
}
