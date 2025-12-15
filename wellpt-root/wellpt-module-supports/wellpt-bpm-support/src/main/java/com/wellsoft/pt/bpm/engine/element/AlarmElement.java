package com.wellsoft.pt.bpm.engine.element;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 预警提醒设置元素
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年07月28日   chenq	 Create
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlarmElement implements Serializable {
    public static final String ALARM_TIMER_END_BY_FLOW_END = "0";
    public static final String ALARM_TIMER_END_BY_TIME_TASK = "1";
    public static final String ALARM_TIMER_END_BY_DIRECTION = "2";
    private static final long serialVersionUID = 5944715200735634835L;
    // 预警提醒
    // 提醒时间类型，1常量、2字段值
    protected String alarmTimeType;
    // 提醒时间
    protected String alarmTime;
    // 提醒单位类型，1常量、2字段值
    protected String alarmUnitType;
    // 提醒单位
    protected String alarmUnit;
    // 预警提醒次数类型，1常量、2字段值
    protected String alarmFrequencyType;
    // 预警提醒次数
    protected String alarmFrequency;
    // 人员
    protected List<UserUnitElement> alarmObjects;
    // 其他人员
    protected List<UserUnitElement> alarmUsers;
    // 发起流程
    protected AlarmFlowElement alarmFlow;
    // 发起流程办理人
    protected List<UserUnitElement> alarmFlowDoings;
    // 发起流程其他人员
    protected List<UserUnitElement> alarmFlowDoingUsers;

    /**
     * @return the alarmTimeType
     */
    public String getAlarmTimeType() {
        return alarmTimeType;
    }

    /**
     * @param alarmTimeType 要设置的alarmTimeType
     */
    public void setAlarmTimeType(String alarmTimeType) {
        this.alarmTimeType = alarmTimeType;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    /**
     * @return the alarmUnitType
     */
    public String getAlarmUnitType() {
        return alarmUnitType;
    }

    /**
     * @param alarmUnitType 要设置的alarmUnitType
     */
    public void setAlarmUnitType(String alarmUnitType) {
        this.alarmUnitType = alarmUnitType;
    }

    public String getAlarmUnit() {
        return alarmUnit;
    }

    public void setAlarmUnit(String alarmUnit) {
        this.alarmUnit = alarmUnit;
    }

    /**
     * @return the alarmFrequencyType
     */
    public String getAlarmFrequencyType() {
        return alarmFrequencyType;
    }

    /**
     * @param alarmFrequencyType 要设置的alarmFrequencyType
     */
    public void setAlarmFrequencyType(String alarmFrequencyType) {
        this.alarmFrequencyType = alarmFrequencyType;
    }

    public String getAlarmFrequency() {
        return alarmFrequency;
    }

    public void setAlarmFrequency(String alarmFrequency) {
        this.alarmFrequency = alarmFrequency;
    }

    public List<UserUnitElement> getAlarmObjects() {
        return alarmObjects;
    }

    public void setAlarmObjects(List<UserUnitElement> alarmObjects) {
        this.alarmObjects = alarmObjects;
    }

    public List<UserUnitElement> getAlarmUsers() {
        return alarmUsers;
    }

    public void setAlarmUsers(List<UserUnitElement> alarmUsers) {
        this.alarmUsers = alarmUsers;
    }

    public AlarmFlowElement getAlarmFlow() {
        return alarmFlow;
    }

    public void setAlarmFlow(AlarmFlowElement alarmFlow) {
        this.alarmFlow = alarmFlow;
    }

    public List<UserUnitElement> getAlarmFlowDoings() {
        return alarmFlowDoings;
    }

    public void setAlarmFlowDoings(List<UserUnitElement> alarmFlowDoings) {
        this.alarmFlowDoings = alarmFlowDoings;
    }

    public List<UserUnitElement> getAlarmFlowDoingUsers() {
        return alarmFlowDoingUsers;
    }

    public void setAlarmFlowDoingUsers(List<UserUnitElement> alarmFlowDoingUsers) {
        this.alarmFlowDoingUsers = alarmFlowDoingUsers;
    }

}
