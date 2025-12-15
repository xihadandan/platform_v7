/*
 * @(#)2012-11-16 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Iterator;
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
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-16.1	zhulh		2012-11-16		Create
 * </pre>
 * @date 2012-11-16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimerElement extends AlarmElement {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7387183243539818033L;

    // 定时器名称
    private String name;
    // 定时器ID
    private String timerId;
    // 计时器配置UUID
    private String timerConfigUuid;
    // 引用计时器
    private String introductionType;
    // 包含启动后开始计时结点
    private String includeStartTimePoint;
    // 自动推迟到下一工作时间的起始点前结点
    private String autoDelay;
    // 时限单位
    private String timeLimitUnit;
    // 工作时间方案UUID
    private String workTimePlanUuid;
    // 工作时间方案ID
    private String workTimePlanId;
    // 工作时间方案名称
    private String workTimePlanName;
    // 办理时限类型(1:设置固定时限  2:按字段设置动态时限 21:按字段设置时间)
    private String limitTimeType;
    // 办理时限数字
    private String limitTime1;
    // 办理时限动态表单字段
    private String limitTime;
    // 自动更新办理时限
    private String autoUpdateLimitTime;
    // 时限为空时不计时
    private String ignoreEmptyLimitTime;
    // 计时方式类型: 1工作日、2工作日(一天24小时)、3自然日、-1从表单字段读取
    private String timingModeType;
    // 计时方式单位: 1按天、2按小时、3按分钟
    private String timingModeUnit;
    // 办理时限单位
    private String limitUnit;
    // 办理时限单位字段
    private String limitUnitField;
    // 责任人
    private List<UnitElement> dutys;
    // 计时环节
    private List<UnitElement> tasks;
    // 计时子环节
    private List<SubTaskTimerElement> subTasks;
    private Map<String, UnitElement> taskMap;

    // 计时结束流向
    private String overDirections;

    // 计时暂停/恢复影响主流程
    private String affectMainFlow;
    // 预警提醒
    private String enableAlarm;
    // 逾期处理
    private String enableDueDoing;

	/*// 预警提醒
	// 提醒时间
	private String alarmTime;
	// 提醒单位
	private String alarmUnit;
	// 预警提醒次数
	private String alarmFrequency;
	// 人员
	private List<UnitElement> alarmObjects;
	// 其他人员
	private List<UnitElement> alarmUsers;
	// 发起流程
	private AlarmFlowElement alarmFlow;
	// 发起流程办理人
	private List<UnitElement> alarmFlowDoings;
	// 发起流程其他人员
	private List<UnitElement> alarmFlowDoingUsers;*/

    private List<AlarmElement> alarmElements;

    // 逾期处理
    // 处理时间类型，1常量、2字段值
    private String dueTimeType;
    // 处理时间
    private String dueTime;
    // 处理单位类型，1常量、2字段值
    private String dueUnitType;
    // 处理单位
    private String dueUnit;
    // 逾期处理次数类型，1常量、2字段值
    private String dueFrequencyType;
    // 逾期处理次数
    private String dueFrequency;
    // 人员
    private List<UserUnitElement> dueObjects;
    // 其他人员
    private List<UserUnitElement> dueUsers;
    // 处理动作
    private String dueAction;
    // 移交给其他人员办理
    private List<UserUnitElement> dueToUsers;
    // 自动进入下一个办理环节
    private String dueToTask;
    // 发起流程
    private DueFlowElement dueFlow;
    // 发起流程办理人
    private List<UserUnitElement> dueFlowDoings;
    // 发起流程其他人员
    private List<UserUnitElement> dueFlowDoingUsers;

    // 计时器事件监听
    private String timerListener;

    // 计时结束设置
    private String timeEndType;

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
     * @return the timerId
     */
    public String getTimerId() {
        return timerId;
    }

    /**
     * @param timerId 要设置的timerId
     */
    public void setTimerId(String timerId) {
        this.timerId = timerId;
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
     * @return the introductionType
     */
    public String getIntroductionType() {
        return introductionType;
    }

    /**
     * @param introductionType 要设置的introductionType
     */
    public void setIntroductionType(String introductionType) {
        this.introductionType = introductionType;
    }

    /**
     * @return the includeStartTimePoint
     */
    public String getIncludeStartTimePoint() {
        return includeStartTimePoint;
    }

    /**
     * @param includeStartTimePoint 要设置的includeStartTimePoint
     */
    public void setIncludeStartTimePoint(String includeStartTimePoint) {
        this.includeStartTimePoint = includeStartTimePoint;
    }

    /**
     * @return the autoDelay
     */
    public String getAutoDelay() {
        return autoDelay;
    }

    /**
     * @param autoDelay 要设置的autoDelay
     */
    public void setAutoDelay(String autoDelay) {
        this.autoDelay = autoDelay;
    }

    /**
     * @return the timeLimitUnit
     */
    public String getTimeLimitUnit() {
        return timeLimitUnit;
    }

    /**
     * @param timeLimitUnit 要设置的timeLimitUnit
     */
    public void setTimeLimitUnit(String timeLimitUnit) {
        this.timeLimitUnit = timeLimitUnit;
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
     * @return the workTimePlanName
     */
    public String getWorkTimePlanName() {
        return workTimePlanName;
    }

    /**
     * @param workTimePlanName 要设置的workTimePlanName
     */
    public void setWorkTimePlanName(String workTimePlanName) {
        this.workTimePlanName = workTimePlanName;
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
     * @return the limitTime
     */
    public String getLimitTime() {
        return limitTime;
    }

    /**
     * @param limitTime 要设置的limitTime
     */
    public void setLimitTime(String limitTime) {
        this.limitTime = limitTime;
    }

    /**
     * @return the autoUpdateLimitTime
     */
    public String getAutoUpdateLimitTime() {
        return autoUpdateLimitTime;
    }

    /**
     * @return the autoUpdateLimitTime
     */
    @JsonIgnore
    public boolean getIsAutoUpdateLimitTime() {
        return "1".equals(autoUpdateLimitTime);
    }

    /**
     * @param autoUpdateLimitTime 要设置的autoUpdateLimitTime
     */
    public void setAutoUpdateLimitTime(String autoUpdateLimitTime) {
        this.autoUpdateLimitTime = autoUpdateLimitTime;
    }

    /**
     * @return the ignoreEmptyLimitTime
     */
    public String getIgnoreEmptyLimitTime() {
        return ignoreEmptyLimitTime;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsIgnoreEmptyLimitTime() {
        return "1".equals(ignoreEmptyLimitTime);
    }

    /**
     * @param ignoreEmptyLimitTime 要设置的ignoreEmptyLimitTime
     */
    public void setIgnoreEmptyLimitTime(String ignoreEmptyLimitTime) {
        this.ignoreEmptyLimitTime = ignoreEmptyLimitTime;
    }

    /**
     * @return the timingModeType
     */
    public String getTimingModeType() {
        return timingModeType;
    }

    /**
     * @param timingModeType 要设置的timingModeType
     */
    public void setTimingModeType(String timingModeType) {
        this.timingModeType = timingModeType;
    }

    /**
     * @return the timingModeUnit
     */
    public String getTimingModeUnit() {
        return timingModeUnit;
    }

    /**
     * @param timingModeUnit 要设置的timingModeUnit
     */
    public void setTimingModeUnit(String timingModeUnit) {
        this.timingModeUnit = timingModeUnit;
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
     * @return the limitUnitField
     */
    public String getLimitUnitField() {
        return limitUnitField;
    }

    /**
     * @param limitUnitField 要设置的limitUnitField
     */
    public void setLimitUnitField(String limitUnitField) {
        this.limitUnitField = limitUnitField;
    }

    /**
     * @return the dutys
     */
    public List<UnitElement> getDutys() {
        return dutys;
    }

    /**
     * @param dutys 要设置的dutys
     */
    public void setDutys(List<UnitElement> dutys) {
        this.dutys = dutys;
    }

    /**
     * @return the tasks
     */
    public List<UnitElement> getTasks() {
        return tasks;
    }

    /**
     * @param tasks 要设置的tasks
     */
    public void setTasks(List<UnitElement> tasks) {
        this.tasks = tasks;
    }

    /**
     * @return
     */
    @JsonIgnore
    public Map<String, UnitElement> getTaskMap() {
        if (taskMap == null) {
            taskMap = new HashMap<String, UnitElement>();

            for (UnitElement unitElement : tasks) {
                taskMap.put(unitElement.getValue(), unitElement);
            }
        }
        return taskMap;
    }

    /**
     * @return
     */
    @JsonIgnore
    public String getTaskIdsAsString() {
        List<String> taskIds = Lists.newArrayList();
        // 普通流程环节
        Iterator<UnitElement> it = tasks.iterator();
        while (it.hasNext()) {
            taskIds.add(it.next().getValue());
        }
        // 子流程环节按当前计时器计时
        Iterator<SubTaskTimerElement> subTaskIt = subTasks.iterator();
        while (subTaskIt.hasNext()) {
            SubTaskTimerElement timer = subTaskIt.next();
            if ("1".equals(timer.getTimingMode())) {
                taskIds.add(timer.getTaskId());
            }
        }
        return StringUtils.join(taskIds, Separator.SEMICOLON.getValue());
    }

    /**
     * @return the subTasks
     */
    public List<SubTaskTimerElement> getSubTasks() {
        return subTasks;
    }

    /**
     * @param subTasks 要设置的subTasks
     */
    public void setSubTasks(List<SubTaskTimerElement> subTasks) {
        this.subTasks = subTasks;
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
     * @return the affectMainFlow
     */
    public String getAffectMainFlow() {
        return affectMainFlow;
    }

    /**
     * @param affectMainFlow 要设置的affectMainFlow
     */
    public void setAffectMainFlow(String affectMainFlow) {
        this.affectMainFlow = affectMainFlow;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsAffectMainFlow() {
        return "1".equals(this.affectMainFlow);
    }

    /**
     * @return the enableAlarm
     */
    public String getEnableAlarm() {
        return enableAlarm;
    }

    /**
     * @param enableAlarm 要设置的enableAlarm
     */
    public void setEnableAlarm(String enableAlarm) {
        this.enableAlarm = enableAlarm;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsEnableAlarm() {
        return "1".equals(this.enableAlarm);
    }

    /**
     * @return the enableDueDoing
     */
    public String getEnableDueDoing() {
        return enableDueDoing;
    }

    /**
     * @param enableDueDoing 要设置的enableDueDoing
     */
    public void setEnableDueDoing(String enableDueDoing) {
        this.enableDueDoing = enableDueDoing;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsEnableDueDoing() {
        return "1".equals(this.enableDueDoing);
    }

    /**
     * @return the dueTimeType
     */
    public String getDueTimeType() {
        return dueTimeType;
    }

    /**
     * @param dueTimeType 要设置的dueTimeType
     */
    public void setDueTimeType(String dueTimeType) {
        this.dueTimeType = dueTimeType;
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
     * @return the dueUnitType
     */
    public String getDueUnitType() {
        return dueUnitType;
    }

    /**
     * @param dueUnitType 要设置的dueUnitType
     */
    public void setDueUnitType(String dueUnitType) {
        this.dueUnitType = dueUnitType;
    }

    /**
     * @return the dueUnit
     */
    public String getDueUnit() {
        return dueUnit;
    }

    /**
     * @param dueUnit 要设置的dueUnit
     */
    public void setDueUnit(String dueUnit) {
        this.dueUnit = dueUnit;
    }

    /**
     * @return the dueFrequencyType
     */
    public String getDueFrequencyType() {
        return dueFrequencyType;
    }

    /**
     * @param dueFrequencyType 要设置的dueFrequencyType
     */
    public void setDueFrequencyType(String dueFrequencyType) {
        this.dueFrequencyType = dueFrequencyType;
    }

    /**
     * @return the dueFrequency
     */
    public String getDueFrequency() {
        return dueFrequency;
    }

    /**
     * @param dueFrequency 要设置的dueFrequency
     */
    public void setDueFrequency(String dueFrequency) {
        this.dueFrequency = dueFrequency;
    }

    /**
     * @return the dueObjects
     */
    public List<UserUnitElement> getDueObjects() {
        return dueObjects;
    }

    /**
     * @param dueObjects 要设置的dueObjects
     */
    public void setDueObjects(List<UserUnitElement> dueObjects) {
        this.dueObjects = dueObjects;
    }

    /**
     * @return the dueUsers
     */
    public List<UserUnitElement> getDueUsers() {
        return dueUsers;
    }

    /**
     * @param dueUsers 要设置的dueUsers
     */
    public void setDueUsers(List<UserUnitElement> dueUsers) {
        this.dueUsers = dueUsers;
    }

    /**
     * @return the dueAction
     */
    public String getDueAction() {
        return dueAction;
    }

    /**
     * @param dueAction 要设置的dueAction
     */
    public void setDueAction(String dueAction) {
        this.dueAction = dueAction;
    }

    /**
     * @return the dueToUsers
     */
    public List<UserUnitElement> getDueToUsers() {
        return dueToUsers;
    }

    /**
     * @param dueToUsers 要设置的dueToUsers
     */
    public void setDueToUsers(List<UserUnitElement> dueToUsers) {
        this.dueToUsers = dueToUsers;
    }

    /**
     * @return the dueToTask
     */
    public String getDueToTask() {
        return dueToTask;
    }

    /**
     * @param dueToTask 要设置的dueToTask
     */
    public void setDueToTask(String dueToTask) {
        this.dueToTask = dueToTask;
    }

    /**
     * @return the dueFlow
     */
    public DueFlowElement getDueFlow() {
        return dueFlow;
    }

    /**
     * @param dueFlow 要设置的dueFlow
     */
    public void setDueFlow(DueFlowElement dueFlow) {
        this.dueFlow = dueFlow;
    }

    /**
     * @return the dueFlowDoings
     */
    public List<UserUnitElement> getDueFlowDoings() {
        return dueFlowDoings;
    }

    /**
     * @param dueFlowDoings 要设置的dueFlowDoings
     */
    public void setDueFlowDoings(List<UserUnitElement> dueFlowDoings) {
        this.dueFlowDoings = dueFlowDoings;
    }

    /**
     * @return the dueFlowDoingUsers
     */
    public List<UserUnitElement> getDueFlowDoingUsers() {
        return dueFlowDoingUsers;
    }

    /**
     * @param dueFlowDoingUsers 要设置的dueFlowDoingUsers
     */
    public void setDueFlowDoingUsers(List<UserUnitElement> dueFlowDoingUsers) {
        this.dueFlowDoingUsers = dueFlowDoingUsers;
    }

    /**
     * @return the timerListener
     */
    public String getTimerListener() {
        return timerListener;
    }

    /**
     * @param timerListener 要设置的timerListener
     */
    public void setTimerListener(String timerListener) {
        this.timerListener = timerListener;
    }

    public List<AlarmElement> getAlarmElements() {
        return alarmElements;
    }

    public void setAlarmElements(List<AlarmElement> alarmElements) {
        this.alarmElements = alarmElements;
    }

    public String getTimeEndType() {
        return timeEndType;
    }

    public void setTimeEndType(String timeEndType) {
        this.timeEndType = timeEndType;
    }
}
