/*
 * @(#)2018年6月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

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
 * 2018年6月4日.1	zhulh		2018年6月4日		Create
 * </pre>
 * @date 2018年6月4日
 */
public class FlowShareDataQueryItem implements BaseQueryItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6652705198255020289L;

    private String taskInstUuid;

    private String flowInstUuid;

    private String formUuid;

    private String dataUuid;

    private String todoId;

    private String todoName;

    private String currentTaskId;

    private String currentTaskName;

    private String currentTodoUserId;

    private String currentTodoUserName;

    private Date dueTime;

    private Integer timingState;

    private Integer limitUnit;

    private String timerUuid;

    private String flowDefId;

    private String branchType;

    private Boolean isMajor;

    private Boolean isShare;

    private Boolean isWait;

    private Integer completionState;

    private String belongToTaskId;

    private String belongToTaskInstUuid;

    private String belongToFlowInstUuid;

    // 分发完成状态
    private Integer dispatchState;

    // 分发结果信息
    private String dispatchResultMsg;

    private Object extraColumn0;

    private Object extraColumn1;

    private Object extraColumn2;

    private Object extraColumn3;

    private Object extraColumn4;

    private Object extraColumn5;

    private Object extraColumn6;

    private Object extraColumn7;

    private Object extraColumn8;

    private Object extraColumn9;

    /**
     * 开始时间
     */
    private Date startTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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
     * @return the todoId
     */
    public String getTodoId() {
        return todoId;
    }

    /**
     * @param todoId 要设置的todoId
     */
    public void setTodoId(String todoId) {
        this.todoId = todoId;
    }

    /**
     * @return the todoName
     */
    public String getTodoName() {
        return todoName;
    }

    /**
     * @param todoName 要设置的todoName
     */
    public void setTodoName(String todoName) {
        this.todoName = todoName;
    }

    /**
     * @return the currentTaskId
     */
    public String getCurrentTaskId() {
        return currentTaskId;
    }

    /**
     * @param currentTaskId 要设置的currentTaskId
     */
    public void setCurrentTaskId(String currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    /**
     * @return the currentTaskName
     */
    public String getCurrentTaskName() {
        return currentTaskName;
    }

    /**
     * @param currentTaskName 要设置的currentTaskName
     */
    public void setCurrentTaskName(String currentTaskName) {
        this.currentTaskName = currentTaskName;
    }

    /**
     * @return the currentTodoUserId
     */
    public String getCurrentTodoUserId() {
        return currentTodoUserId;
    }

    /**
     * @param currentTodoUserId 要设置的currentTodoUserId
     */
    public void setCurrentTodoUserId(String currentTodoUserId) {
        this.currentTodoUserId = currentTodoUserId;
    }

    /**
     * @return the currentTodoUserName
     */
    public String getCurrentTodoUserName() {
        return currentTodoUserName;
    }

    /**
     * @param currentTodoUserName 要设置的currentTodoUserName
     */
    public void setCurrentTodoUserName(String currentTodoUserName) {
        this.currentTodoUserName = currentTodoUserName;
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

    public Integer getTimingState() {
        return timingState;
    }

    public void setTimingState(Integer timingState) {
        this.timingState = timingState;
    }

    /**
     * @return the limitUnit
     */
    public Integer getLimitUnit() {
        return limitUnit;
    }

    /**
     * @param limitUnit 要设置的limitUnit
     */
    public void setLimitUnit(Integer limitUnit) {
        this.limitUnit = limitUnit;
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
     * @return the branchType
     */
    public String getBranchType() {
        return branchType;
    }

    /**
     * @param branchType 要设置的branchType
     */
    public void setBranchType(String branchType) {
        this.branchType = branchType;
    }

    /**
     * @return the isMajor
     */
    public Boolean getIsMajor() {
        return isMajor;
    }

    /**
     * @param isMajor 要设置的isMajor
     */
    public void setIsMajor(Boolean isMajor) {
        this.isMajor = isMajor;
    }

    /**
     * @return the isShare
     */
    public Boolean getIsShare() {
        return isShare;
    }

    /**
     * @param isShare 要设置的isShare
     */
    public void setIsShare(Boolean isShare) {
        this.isShare = isShare;
    }

    public Boolean getIsWait() {
        return isWait;
    }

    public void setIsWait(Boolean isWait) {
        this.isWait = isWait;
    }

    /**
     * @return the completionState
     */
    public Integer getCompletionState() {
        return completionState;
    }

    /**
     * @param completionState 要设置的completionState
     */
    public void setCompletionState(Integer completionState) {
        this.completionState = completionState;
    }

    /**
     * @return the belongToTaskId
     */
    public String getBelongToTaskId() {
        return belongToTaskId;
    }

    /**
     * @param belongToTaskId 要设置的belongToTaskId
     */
    public void setBelongToTaskId(String belongToTaskId) {
        this.belongToTaskId = belongToTaskId;
    }

    /**
     * @return the belongToTaskInstUuid
     */
    public String getBelongToTaskInstUuid() {
        return belongToTaskInstUuid;
    }

    /**
     * @param belongToTaskInstUuid 要设置的belongToTaskInstUuid
     */
    public void setBelongToTaskInstUuid(String belongToTaskInstUuid) {
        this.belongToTaskInstUuid = belongToTaskInstUuid;
    }

    /**
     * @return the belongToFlowInstUuid
     */
    public String getBelongToFlowInstUuid() {
        return belongToFlowInstUuid;
    }

    /**
     * @param belongToFlowInstUuid 要设置的belongToFlowInstUuid
     */
    public void setBelongToFlowInstUuid(String belongToFlowInstUuid) {
        this.belongToFlowInstUuid = belongToFlowInstUuid;
    }

    /**
     * @return the dispatchState
     */
    public Integer getDispatchState() {
        return dispatchState;
    }

    /**
     * @param dispatchState 要设置的dispatchState
     */
    public void setDispatchState(Integer dispatchState) {
        this.dispatchState = dispatchState;
    }

    /**
     * @return the dispatchResultMsg
     */
    public String getDispatchResultMsg() {
        return dispatchResultMsg;
    }

    /**
     * @param dispatchResultMsg 要设置的dispatchResultMsg
     */
    public void setDispatchResultMsg(String dispatchResultMsg) {
        this.dispatchResultMsg = dispatchResultMsg;
    }

    /**
     * @return the extraColumn0
     */
    public Object getExtraColumn0() {
        return extraColumn0;
    }

    /**
     * @param extraColumn0 要设置的extraColumn0
     */
    public void setExtraColumn0(Object extraColumn0) {
        this.extraColumn0 = extraColumn0;
    }

    /**
     * @return the extraColumn1
     */
    public Object getExtraColumn1() {
        return extraColumn1;
    }

    /**
     * @param extraColumn1 要设置的extraColumn1
     */
    public void setExtraColumn1(Object extraColumn1) {
        this.extraColumn1 = extraColumn1;
    }

    /**
     * @return the extraColumn2
     */
    public Object getExtraColumn2() {
        return extraColumn2;
    }

    /**
     * @param extraColumn2 要设置的extraColumn2
     */
    public void setExtraColumn2(Object extraColumn2) {
        this.extraColumn2 = extraColumn2;
    }

    /**
     * @return the extraColumn3
     */
    public Object getExtraColumn3() {
        return extraColumn3;
    }

    /**
     * @param extraColumn3 要设置的extraColumn3
     */
    public void setExtraColumn3(Object extraColumn3) {
        this.extraColumn3 = extraColumn3;
    }

    /**
     * @return the extraColumn4
     */
    public Object getExtraColumn4() {
        return extraColumn4;
    }

    /**
     * @param extraColumn4 要设置的extraColumn4
     */
    public void setExtraColumn4(Object extraColumn4) {
        this.extraColumn4 = extraColumn4;
    }

    /**
     * @return the extraColumn5
     */
    public Object getExtraColumn5() {
        return extraColumn5;
    }

    /**
     * @param extraColumn5 要设置的extraColumn5
     */
    public void setExtraColumn5(Object extraColumn5) {
        this.extraColumn5 = extraColumn5;
    }

    /**
     * @return the extraColumn6
     */
    public Object getExtraColumn6() {
        return extraColumn6;
    }

    /**
     * @param extraColumn6 要设置的extraColumn6
     */
    public void setExtraColumn6(Object extraColumn6) {
        this.extraColumn6 = extraColumn6;
    }

    /**
     * @return the extraColumn7
     */
    public Object getExtraColumn7() {
        return extraColumn7;
    }

    /**
     * @param extraColumn7 要设置的extraColumn7
     */
    public void setExtraColumn7(Object extraColumn7) {
        this.extraColumn7 = extraColumn7;
    }

    /**
     * @return the extraColumn8
     */
    public Object getExtraColumn8() {
        return extraColumn8;
    }

    /**
     * @param extraColumn8 要设置的extraColumn8
     */
    public void setExtraColumn8(Object extraColumn8) {
        this.extraColumn8 = extraColumn8;
    }

    /**
     * @return the extraColumn9
     */
    public Object getExtraColumn9() {
        return extraColumn9;
    }

    /**
     * @param extraColumn9 要设置的extraColumn9
     */
    public void setExtraColumn9(Object extraColumn9) {
        this.extraColumn9 = extraColumn9;
    }
}
