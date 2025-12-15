/*
 * @(#)2013-5-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-24.1	zhulh		2013-5-24		Create
 * </pre>
 * @date 2013-5-24
 */
@Entity
@Table(name = "wf_task_timer")
@DynamicUpdate
@DynamicInsert
public class TaskTimer extends IdEntity {

    // 办理时限类型——固定时限
    public static final String LIMIT_TIME_TYPE_NUMBER = "1";
    // 办理时限类型——动态时限
    public static final String LIMIT_TIME_TYPE_FORM_FIELD = "2";
    // 办理时限类型——动态截止日期
    public static final String LIMIT_TIME_TYPE_FORM_DATE = "3";
    // 办理时限类型——固定截止日期
    public static final String LIMIT_TIME_TYPE_FIXED_DATE = "4";
    private static final long serialVersionUID = -8604146278482194784L;
    // 配置信息开始
    // 定时器ID
    private String id;
    // 引用定时器ID列表
    private String refIds;
    // 名称
    private String name;
    // 办理时限类型
    private String limitTimeType;
    // 办理时限数字、或日期
    private String limitTime1;
    // 办理时限动态表单字段
    private String limitTime2;
    // 办理时限单位
    private String limitUnit;
    // 计时环节
    private String taskIds;
    // 计时结束流向
    private String overDirections;
    // 自动更新时限
    private Boolean autoUpdateLimitTime;
    // 时限为空时不计时
    private Boolean ignoreEmptyLimitTime;
    // 计时暂停/恢复影响主流程
    private Boolean affectMainFlow;
    // 预警提醒
    private Boolean enableAlarm;
    // 逾期处理
    private Boolean enableDueDoing;

    // 预警提醒
    // 提醒时间
    private String alarmTime;
    // 提醒单位
    private Integer alarmUnit;
    // 提醒总次数
    private Integer alarmFrequency;
    // 发起流程
    private String alarmFlowId;

    // 逾期处理
    // 处理时间
    private String dueTime;
    // 处理时间单位
    private Integer dueUnit;
    // 逾期处理总次数
    private Integer dueFrequency;
    // 处理动作
    private Integer dueAction;
    // 自动进入下一个办理环节ID
    private String dueToTaskId;
    // 发起流程
    private String dueFlowId;
    // 配置信息结束

    // 租户ID
    private String tenantId;
    // 环节ID
    private String taskId;
    // 环节实例UUID
    private String taskInstUuid;
    // 流程实例UUID
    private String flowInstUuid;

    // 计时器是运行状态(0未启动、1已启动、2暂停、3结束)
    private Integer status;
    // 计时状态(0正常、1预警、2到期、3逾期)
    private Integer timingState;
    // 开始计时时间点
    private Date startTime;
    // 最新开始时间
    private Date lastStartTime;

    // 预警提醒是否已经提醒过
    private Boolean alarmDone;
    // 预警状态(0未预警、1预警中)
    private Integer alarmState;
    // 流程计算后初始化的办理时限数字
    private Double taskInitLimitTime;
    // 流程最新计算后的办理时限数字
    private Double taskLimitTime;
    // 流程计算后初始化的办理时限日期，当时限类型为指定日期或表单日期字段时有效
    private Date taskInitDueTime;
    // 预警提醒时间间隔
    private Long alarmRepeatInterval;
    // 流程计算后的提醒时间
    private Date taskAlarmTime;
    // 流程计算后的办理时限时间
    private Date taskDueTime;
    // 提醒流程已发起
    private Boolean alarmFlowStarted;

    // 逾期处理是否已经处理过
    private Boolean dueDoingDone;
    // 逾期状态(0未逾期、1逾期中)
    private Integer overDueState;
    // 逾期处理流程已发起
    private Boolean dueFlowStarted;

    // 事件监听
    private String listener;

    // 计时器配置UUID
    private String timerConfigUuid;
    // 工作时间方案UUID
    private String workTimePlanUuid;
    // 工作时间方案ID
    private String workTimePlanId;
    // 计时器UUID
    private String timerUuid;

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
     * @return the refIds
     */
    public String getRefIds() {
        return refIds;
    }

    /**
     * @param refIds 要设置的refIds
     */
    public void setRefIds(String refIds) {
        this.refIds = refIds;
    }

    /**
     * @return the limitTimeType
     */
    public String getLimitTimeType() {
        return limitTimeType;
    }

    /**
     * @param limitTimeType 要设置的limitTimeType
     */
    public void setLimitTimeType(String limitTimeType) {
        this.limitTimeType = limitTimeType;
    }

    /**
     * @return the limitTime1
     */
    public String getLimitTime1() {
        return limitTime1;
    }

    /**
     * @param limitTime1 要设置的limitTime1
     */
    public void setLimitTime1(String limitTime1) {
        this.limitTime1 = limitTime1;
    }

    /**
     * @return the limitTime2
     */
    public String getLimitTime2() {
        return limitTime2;
    }

    /**
     * @param limitTime2 要设置的limitTime2
     */
    public void setLimitTime2(String limitTime2) {
        this.limitTime2 = limitTime2;
    }

    /**
     * @return the limitUnit
     */
    public String getLimitUnit() {
        return limitUnit;
    }

    /**
     * @param limitUnit 要设置的limitUnit
     */
    public void setLimitUnit(String limitUnit) {
        this.limitUnit = limitUnit;
    }

    /**
     * @return the taskIds
     */
    public String getTaskIds() {
        return taskIds;
    }

    /**
     * @param taskIds 要设置的taskIds
     */
    public void setTaskIds(String taskIds) {
        this.taskIds = taskIds;
    }

    /**
     * @return the overDirections
     */
    public String getOverDirections() {
        return overDirections;
    }

    /**
     * @param overDirections 要设置的overDirections
     */
    public void setOverDirections(String overDirections) {
        this.overDirections = overDirections;
    }

    /**
     * @return the autoUpdateLimitTime
     */
    public Boolean getAutoUpdateLimitTime() {
        return autoUpdateLimitTime;
    }

    /**
     * @param autoUpdateLimitTime 要设置的autoUpdateLimitTime
     */
    public void setAutoUpdateLimitTime(Boolean autoUpdateLimitTime) {
        this.autoUpdateLimitTime = autoUpdateLimitTime;
    }

    /**
     * @return the ignoreEmptyLimitTime
     */
    public Boolean getIgnoreEmptyLimitTime() {
        return ignoreEmptyLimitTime;
    }

    /**
     * @param ignoreEmptyLimitTime 要设置的ignoreEmptyLimitTime
     */
    public void setIgnoreEmptyLimitTime(Boolean ignoreEmptyLimitTime) {
        this.ignoreEmptyLimitTime = ignoreEmptyLimitTime;
    }

    /**
     * @return the affectMainFlow
     */
    public Boolean getAffectMainFlow() {
        return affectMainFlow;
    }

    /**
     * @param affectMainFlow 要设置的affectMainFlow
     */
    public void setAffectMainFlow(Boolean affectMainFlow) {
        this.affectMainFlow = affectMainFlow;
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

    /**
     * @return the enableDueDoing
     */
    public Boolean getEnableDueDoing() {
        return enableDueDoing;
    }

    /**
     * @param enableDueDoing 要设置的enableDueDoing
     */
    public void setEnableDueDoing(Boolean enableDueDoing) {
        this.enableDueDoing = enableDueDoing;
    }

    /**
     * @return the alarmTime
     */
    public String getAlarmTime() {
        return alarmTime;
    }

    /**
     * @param alarmTime 要设置的alarmTime
     */
    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    /**
     * @return the alarmUnit
     */
    public Integer getAlarmUnit() {
        return alarmUnit;
    }

    /**
     * @param alarmUnit 要设置的alarmUnit
     */
    public void setAlarmUnit(Integer alarmUnit) {
        this.alarmUnit = alarmUnit;
    }

    /**
     * @return the alarmFrequency
     */
    public Integer getAlarmFrequency() {
        return alarmFrequency;
    }

    /**
     * @param alarmFrequency 要设置的alarmFrequency
     */
    public void setAlarmFrequency(Integer alarmFrequency) {
        this.alarmFrequency = alarmFrequency;
    }

    /**
     * @return the alarmFlowId
     */
    public String getAlarmFlowId() {
        return alarmFlowId;
    }

    /**
     * @param alarmFlowId 要设置的alarmFlowId
     */
    public void setAlarmFlowId(String alarmFlowId) {
        this.alarmFlowId = alarmFlowId;
    }

    /**
     * @return the dueTime
     */
    public String getDueTime() {
        return dueTime;
    }

    /**
     * @param dueTime 要设置的dueTime
     */
    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * @return the dueUnit
     */
    public Integer getDueUnit() {
        return dueUnit;
    }

    /**
     * @param dueUnit 要设置的dueUnit
     */
    public void setDueUnit(Integer dueUnit) {
        this.dueUnit = dueUnit;
    }

    /**
     * @return the dueFrequency
     */
    public Integer getDueFrequency() {
        return dueFrequency;
    }

    /**
     * @param dueFrequency 要设置的dueFrequency
     */
    public void setDueFrequency(Integer dueFrequency) {
        this.dueFrequency = dueFrequency;
    }

    /**
     * @return the dueAction
     */
    public Integer getDueAction() {
        return dueAction;
    }

    /**
     * @param dueAction 要设置的dueAction
     */
    public void setDueAction(Integer dueAction) {
        this.dueAction = dueAction;
    }

    /**
     * @return the dueToTaskId
     */
    public String getDueToTaskId() {
        return dueToTaskId;
    }

    /**
     * @param dueToTaskId 要设置的dueToTaskId
     */
    public void setDueToTaskId(String dueToTaskId) {
        this.dueToTaskId = dueToTaskId;
    }

    /**
     * @return the dueFlowId
     */
    public String getDueFlowId() {
        return dueFlowId;
    }

    /**
     * @param dueFlowId 要设置的dueFlowId
     */
    public void setDueFlowId(String dueFlowId) {
        this.dueFlowId = dueFlowId;
    }

    /**
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId 要设置的tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
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
     * @return the alarmDone
     */
    public Boolean getAlarmDone() {
        return alarmDone;
    }

    /**
     * @param alarmDone 要设置的alarmDone
     */
    public void setAlarmDone(Boolean alarmDone) {
        this.alarmDone = alarmDone;
    }

    /**
     * @return the alarmState
     */
    public Integer getAlarmState() {
        return alarmState;
    }

    /**
     * @param alarmState 要设置的alarmState
     */
    public void setAlarmState(Integer alarmState) {
        this.alarmState = alarmState;
    }

    /**
     * @return the taskInitLimitTime
     */
    public Double getTaskInitLimitTime() {
        return taskInitLimitTime;
    }

    /**
     * @param taskInitLimitTime 要设置的taskInitLimitTime
     */
    public void setTaskInitLimitTime(Double taskInitLimitTime) {
        this.taskInitLimitTime = taskInitLimitTime;
    }

    /**
     * @return the taskLimitTime
     */
    public Double getTaskLimitTime() {
        return taskLimitTime;
    }

    /**
     * @param taskLimitTime 要设置的taskLimitTime
     */
    public void setTaskLimitTime(Double taskLimitTime) {
        this.taskLimitTime = taskLimitTime;
    }

    /**
     * @return the taskInitDueTime
     */
    public Date getTaskInitDueTime() {
        return taskInitDueTime;
    }

    /**
     * @param taskInitDueTime 要设置的taskInitDueTime
     */
    public void setTaskInitDueTime(Date taskInitDueTime) {
        this.taskInitDueTime = taskInitDueTime;
    }

    /**
     * @return the alarmRepeatInterval
     */
    public Long getAlarmRepeatInterval() {
        return alarmRepeatInterval;
    }

    /**
     * @param alarmRepeatInterval 要设置的alarmRepeatInterval
     */
    public void setAlarmRepeatInterval(Long alarmRepeatInterval) {
        this.alarmRepeatInterval = alarmRepeatInterval;
    }

    /**
     * @return the taskAlarmTime
     */
    public Date getTaskAlarmTime() {
        return taskAlarmTime;
    }

    /**
     * @param taskAlarmTime 要设置的taskAlarmTime
     */
    public void setTaskAlarmTime(Date taskAlarmTime) {
        this.taskAlarmTime = taskAlarmTime;
    }

    /**
     * @return the taskDueTime
     */
    public Date getTaskDueTime() {
        return taskDueTime;
    }

    /**
     * @param taskDueTime 要设置的taskDueTime
     */
    public void setTaskDueTime(Date taskDueTime) {
        this.taskDueTime = taskDueTime;
    }

    /**
     * @return the alarmFlowStarted
     */
    public Boolean getAlarmFlowStarted() {
        return alarmFlowStarted;
    }

    /**
     * @param alarmFlowStarted 要设置的alarmFlowStarted
     */
    public void setAlarmFlowStarted(Boolean alarmFlowStarted) {
        this.alarmFlowStarted = alarmFlowStarted;
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
     * @return the overDueState
     */
    public Integer getOverDueState() {
        return overDueState;
    }

    /**
     * @param overDueState 要设置的overDueState
     */
    public void setOverDueState(Integer overDueState) {
        this.overDueState = overDueState;
    }

    /**
     * @return the dueFlowStarted
     */
    public Boolean getDueFlowStarted() {
        return dueFlowStarted;
    }

    /**
     * @param dueFlowStarted 要设置的dueFlowStarted
     */
    public void setDueFlowStarted(Boolean dueFlowStarted) {
        this.dueFlowStarted = dueFlowStarted;
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
     * @return the workTimePlanId
     */
    public String getWorkTimePlanId() {
        return workTimePlanId;
    }

    /**
     * @param workTimePlanId 要设置的workTimePlanId
     */
    public void setWorkTimePlanId(String workTimePlanId) {
        this.workTimePlanId = workTimePlanId;
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
