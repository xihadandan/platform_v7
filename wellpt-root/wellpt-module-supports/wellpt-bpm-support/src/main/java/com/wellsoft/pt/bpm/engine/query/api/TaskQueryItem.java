/*
 * @(#)2015-10-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 环节实例查询项
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-10-19.1	zhulh		2015-10-19		Create
 * </pre>
 * @date 2015-10-19
 */
public class TaskQueryItem implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5458438498515874378L;

    // UUID
    protected String uuid;
    // 版本号
    private Integer recVer;
    // 创建人
    private String creator;
    // 创建时间
    private Date createTime;
    // 修改人
    private String modifier;
    // 修改时间
    private Date modifyTime;
    // 流程定义ID
    private String flowDefId;
    // 流程定义UUID
    private String flowDefUuid;
    // 流程名称
    private String flowName;
    // 流程实例UUID
    private String flowInstUuid;
    // 流程标题
    private String title;
    // 流程启动人ID
    private String flowStartUserId;
    // 流程启动时间
    private Date flowStartTime;
    // 流程结束时间
    private Date flowEndTime;
    // 流程是否激活
    private Boolean isActive;
    // 环节名称
    private String taskName;
    // 环节ID
    private String taskId;
    // 表单定义UUID
    private String formUuid;
    // 表单数据UUID
    private String dataUuid;
    // 流水号
    private String serialNo;
    // 环节所有者
    private String owner;
    // 前办理人ID
    private String preOperatorId;
    // 前办理人名称
    private String preOperatorName;
    // 待办人员ID
    private String todoUserId;
    // 待办人员名称
    private String todoUserName;
    // 环节操作动作
    private String action;
    // 环节操作动作类型
    private String actionType;
    // 环节开始时间
    private Date startTime;
    // 环节结束时间
    private Date endTime;
    // 环节预警时间
    private Date alarmTime;
    // 环节到期时间
    private Date dueTime;
    // 计时状态
    private Integer timingState;
    // 预警状态
    private Integer alarmState;
    // 逾期状态
    private Integer overDueState;
    // 挂起状态
    private Integer suspensionState;
    /**
     * 预留字段
     **/
    // 255字符长度
    private String reservedText1;
    // 255字符长度
    private String reservedText2;
    // 255字符长度
    private String reservedText3;
    // 255字符长度
    private String reservedText4;
    // 255字符长度
    private String reservedText5;
    // 255字符长度
    private String reservedText6;
    // 255字符长度
    private String reservedText7;
    // 255字符长度
    private String reservedText8;
    // 255字符长度
    private String reservedText9;
    // 255字符长度
    private String reservedText10;
    // 255字符长度
    private String reservedText11;
    // 255字符长度
    private String reservedText12;

    private Integer reservedNumber1;
    private Double reservedNumber2;
    private Double reservedNumber3;

    private Date reservedDate1;
    private Date reservedDate2;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the recVer
     */
    public Integer getRecVer() {
        return recVer;
    }

    /**
     * @param recVer 要设置的recVer
     */
    public void setRecVer(Integer recVer) {
        this.recVer = recVer;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator 要设置的creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 要设置的createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the modifier
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * @param modifier 要设置的modifier
     */
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    /**
     * @return the modifyTime
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime 要设置的modifyTime
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * @return the flowDefId
     */
    public String getFlowDefId() {
        return flowDefId;
    }

    /**
     * @param flowDefId 要设置的flowDefId
     */
    public void setFlowDefId(String flowDefId) {
        this.flowDefId = flowDefId;
    }

    /**
     * @return the flowDefUuid
     */
    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    /**
     * @param flowDefUuid 要设置的flowDefUuid
     */
    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
    }

    /**
     * @return the flowName
     */
    public String getFlowName() {
        return flowName;
    }

    /**
     * @param flowName 要设置的flowName
     */
    public void setFlowName(String flowName) {
        this.flowName = flowName;
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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the flowStartUserId
     */
    public String getFlowStartUserId() {
        return flowStartUserId;
    }

    /**
     * @param flowStartUserId 要设置的flowStartUserId
     */
    public void setFlowStartUserId(String flowStartUserId) {
        this.flowStartUserId = flowStartUserId;
    }

    /**
     * @return the flowStartTime
     */
    public Date getFlowStartTime() {
        return flowStartTime;
    }

    /**
     * @param flowStartTime 要设置的flowStartTime
     */
    public void setFlowStartTime(Date flowStartTime) {
        this.flowStartTime = flowStartTime;
    }

    /**
     * @return the flowEndTime
     */
    public Date getFlowEndTime() {
        return flowEndTime;
    }

    /**
     * @param flowEndTime 要设置的flowEndTime
     */
    public void setFlowEndTime(Date flowEndTime) {
        this.flowEndTime = flowEndTime;
    }

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive 要设置的isActive
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName 要设置的taskName
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
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
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the serialNo
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * @param serialNo 要设置的serialNo
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner 要设置的owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the preOperatorId
     */
    public String getPreOperatorId() {
        return preOperatorId;
    }

    /**
     * @param preOperatorId 要设置的preOperatorId
     */
    public void setPreOperatorId(String preOperatorId) {
        this.preOperatorId = preOperatorId;
    }

    /**
     * @return the preOperatorName
     */
    public String getPreOperatorName() {
        return preOperatorName;
    }

    /**
     * @param preOperatorName 要设置的preOperatorName
     */
    public void setPreOperatorName(String preOperatorName) {
        this.preOperatorName = preOperatorName;
    }

    /**
     * @return the todoUserId
     */
    public String getTodoUserId() {
        return todoUserId;
    }

    /**
     * @param todoUserId 要设置的todoUserId
     */
    public void setTodoUserId(String todoUserId) {
        this.todoUserId = todoUserId;
    }

    /**
     * @return the todoUserName
     */
    public String getTodoUserName() {
        return todoUserName;
    }

    /**
     * @param todoUserName 要设置的todoUserName
     */
    public void setTodoUserName(String todoUserName) {
        this.todoUserName = todoUserName;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action 要设置的action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the actionType
     */
    public String getActionType() {
        return actionType;
    }

    /**
     * @param actionType 要设置的actionType
     */
    public void setActionType(String actionType) {
        this.actionType = actionType;
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
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
     * @return the suspensionState
     */
    public Integer getSuspensionState() {
        return suspensionState;
    }

    /**
     * @param suspensionState 要设置的suspensionState
     */
    public void setSuspensionState(Integer suspensionState) {
        this.suspensionState = suspensionState;
    }

    /**
     * @return the reservedText1
     */
    public String getReservedText1() {
        return reservedText1;
    }

    /**
     * @param reservedText1 要设置的reservedText1
     */
    public void setReservedText1(String reservedText1) {
        this.reservedText1 = reservedText1;
    }

    /**
     * @return the reservedText2
     */
    public String getReservedText2() {
        return reservedText2;
    }

    /**
     * @param reservedText2 要设置的reservedText2
     */
    public void setReservedText2(String reservedText2) {
        this.reservedText2 = reservedText2;
    }

    /**
     * @return the reservedText3
     */
    public String getReservedText3() {
        return reservedText3;
    }

    /**
     * @param reservedText3 要设置的reservedText3
     */
    public void setReservedText3(String reservedText3) {
        this.reservedText3 = reservedText3;
    }

    /**
     * @return the reservedText4
     */
    public String getReservedText4() {
        return reservedText4;
    }

    /**
     * @param reservedText4 要设置的reservedText4
     */
    public void setReservedText4(String reservedText4) {
        this.reservedText4 = reservedText4;
    }

    /**
     * @return the reservedText5
     */
    public String getReservedText5() {
        return reservedText5;
    }

    /**
     * @param reservedText5 要设置的reservedText5
     */
    public void setReservedText5(String reservedText5) {
        this.reservedText5 = reservedText5;
    }

    /**
     * @return the reservedText6
     */
    public String getReservedText6() {
        return reservedText6;
    }

    /**
     * @param reservedText6 要设置的reservedText6
     */
    public void setReservedText6(String reservedText6) {
        this.reservedText6 = reservedText6;
    }

    /**
     * @return the reservedText7
     */
    public String getReservedText7() {
        return reservedText7;
    }

    /**
     * @param reservedText7 要设置的reservedText7
     */
    public void setReservedText7(String reservedText7) {
        this.reservedText7 = reservedText7;
    }

    /**
     * @return the reservedText8
     */
    public String getReservedText8() {
        return reservedText8;
    }

    /**
     * @param reservedText8 要设置的reservedText8
     */
    public void setReservedText8(String reservedText8) {
        this.reservedText8 = reservedText8;
    }

    /**
     * @return the reservedText9
     */
    public String getReservedText9() {
        return reservedText9;
    }

    /**
     * @param reservedText9 要设置的reservedText9
     */
    public void setReservedText9(String reservedText9) {
        this.reservedText9 = reservedText9;
    }

    /**
     * @return the reservedText10
     */
    public String getReservedText10() {
        return reservedText10;
    }

    /**
     * @param reservedText10 要设置的reservedText10
     */
    public void setReservedText10(String reservedText10) {
        this.reservedText10 = reservedText10;
    }

    /**
     * @return the reservedText11
     */
    public String getReservedText11() {
        return reservedText11;
    }

    /**
     * @param reservedText11 要设置的reservedText11
     */
    public void setReservedText11(String reservedText11) {
        this.reservedText11 = reservedText11;
    }

    /**
     * @return the reservedText12
     */
    public String getReservedText12() {
        return reservedText12;
    }

    /**
     * @param reservedText12 要设置的reservedText12
     */
    public void setReservedText12(String reservedText12) {
        this.reservedText12 = reservedText12;
    }

    /**
     * @return the reservedNumber1
     */
    public Integer getReservedNumber1() {
        return reservedNumber1;
    }

    /**
     * @param reservedNumber1 要设置的reservedNumber1
     */
    public void setReservedNumber1(Integer reservedNumber1) {
        this.reservedNumber1 = reservedNumber1;
    }

    /**
     * @return the reservedNumber2
     */
    public Double getReservedNumber2() {
        return reservedNumber2;
    }

    /**
     * @param reservedNumber2 要设置的reservedNumber2
     */
    public void setReservedNumber2(Double reservedNumber2) {
        this.reservedNumber2 = reservedNumber2;
    }

    /**
     * @return the reservedNumber3
     */
    public Double getReservedNumber3() {
        return reservedNumber3;
    }

    /**
     * @param reservedNumber3 要设置的reservedNumber3
     */
    public void setReservedNumber3(Double reservedNumber3) {
        this.reservedNumber3 = reservedNumber3;
    }

    /**
     * @return the reservedDate1
     */
    public Date getReservedDate1() {
        return reservedDate1;
    }

    /**
     * @param reservedDate1 要设置的reservedDate1
     */
    public void setReservedDate1(Date reservedDate1) {
        this.reservedDate1 = reservedDate1;
    }

    /**
     * @return the reservedDate2
     */
    public Date getReservedDate2() {
        return reservedDate2;
    }

    /**
     * @param reservedDate2 要设置的reservedDate2
     */
    public void setReservedDate2(Date reservedDate2) {
        this.reservedDate2 = reservedDate2;
    }

}
