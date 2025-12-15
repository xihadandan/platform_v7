/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 计时实例实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月7日.1	zhulh		2021年4月7日		Create
 * </pre>
 * @date 2021年4月7日
 */
@Entity
@Table(name = "TS_TIMER")
@DynamicUpdate
@DynamicInsert
public class TsTimerEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5321228020471776338L;

    // 计时配置UUID
    private String configUuid;
    // 工作时间方案UUID
    private String workTimePlanUuid;
    // 计算后初始化的办理时限数字
    private Double initTimeLimit;
    // 计算后初始化的办理时限日期，当时限类型为指定日期或表单日期字段时有效
    private Date initDueTime;
    // 开始计时时间点
    private Date startTime;
    // 最新开始时间
    private Date lastStartTime;
    // 计时方式
    private String timingMode;
    // 时限类型
    private String timeLimitType;
    // 最新的办理时限，以分钟为单位计算
    private Double timeLimit;
    // 到期时间
    private Date dueTime;
    // 计时器是运行状态(0未启动、1已启动、2暂停、3结束)
    private Integer status;
    // 计时状态(0正常、1预警、2到期、3逾期)
    private Integer timingState;
    // 到期处理是否已经处理过
    private Boolean dueDoingDone;
    // 逾期处理是否已经处理过
    private Boolean overDueDoingDone;
    // 逾期时间
    private Date overDueTime;
    // 计时监听
    private String listener;
    // 计时相关数据
    private String timerData;
    // 是否启用预警信息
    private Boolean enableAlarm;

    /**
     * @return the configUuid
     */
    public String getConfigUuid() {
        return configUuid;
    }

    /**
     * @param configUuid 要设置的configUuid
     */
    public void setConfigUuid(String configUuid) {
        this.configUuid = configUuid;
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
     * @return the initTimeLimit
     */
    public Double getInitTimeLimit() {
        return initTimeLimit;
    }

    /**
     * @param initTimeLimit 要设置的initTimeLimit
     */
    public void setInitTimeLimit(Double initTimeLimit) {
        this.initTimeLimit = initTimeLimit;
    }

    /**
     * @return the initDueTime
     */
    public Date getInitDueTime() {
        return initDueTime;
    }

    /**
     * @param initDueTime 要设置的initDueTime
     */
    public void setInitDueTime(Date initDueTime) {
        this.initDueTime = initDueTime;
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
     * @return the lastStartTime
     */
    public Date getLastStartTime() {
        return lastStartTime;
    }

    /**
     * @param lastStartTime 要设置的lastStartTime
     */
    public void setLastStartTime(Date lastStartTime) {
        this.lastStartTime = lastStartTime;
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
     * @return the timeLimitType
     */
    public String getTimeLimitType() {
        return timeLimitType;
    }

    /**
     * @param timeLimitType 要设置的timeLimitType
     */
    public void setTimeLimitType(String timeLimitType) {
        this.timeLimitType = timeLimitType;
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
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the timingState
     */
    public Integer getTimingState() {
        return timingState;
    }

    /**
     * @param timingState 要设置的timingState
     */
    public void setTimingState(Integer timingState) {
        this.timingState = timingState;
    }

    /**
     * @return the dueDoingDone
     */
    public Boolean getDueDoingDone() {
        return dueDoingDone;
    }

    /**
     * @param dueDoingDone 要设置的dueDoingDone
     */
    public void setDueDoingDone(Boolean dueDoingDone) {
        this.dueDoingDone = dueDoingDone;
    }

    /**
     * @return the overDueDoingDone
     */
    public Boolean getOverDueDoingDone() {
        return overDueDoingDone;
    }

    /**
     * @param overDueDoingDone 要设置的overDueDoingDone
     */
    public void setOverDueDoingDone(Boolean overDueDoingDone) {
        this.overDueDoingDone = overDueDoingDone;
    }

    /**
     * @return the overDueTime
     */
    public Date getOverDueTime() {
        return overDueTime;
    }

    /**
     * @param overDueTime 要设置的overDueTime
     */
    public void setOverDueTime(Date overDueTime) {
        this.overDueTime = overDueTime;
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
     * @return the timerData
     */
    public String getTimerData() {
        return timerData;
    }

    /**
     * @param timerData 要设置的timerData
     */
    public void setTimerData(String timerData) {
        this.timerData = timerData;
    }

    /**
     * @return the enableAlarm
     */
    public Boolean getEnableAlarm() {
        return enableAlarm;
    }

    /**
     * @param enableAlarm 要设置的enableAlarm
     */
    public void setEnableAlarm(Boolean enableAlarm) {
        this.enableAlarm = enableAlarm;
    }
}
