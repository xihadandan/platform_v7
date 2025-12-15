/*
 * @(#)2014-9-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.TaskOperateProcessPostResponse;

import java.util.Date;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-24.1	Asus		2014-9-24		Create
 * </pre>
 * @date 2014-9-24
 */
public class TaskOperateProcessPostRequest extends WellptRequest<TaskOperateProcessPostResponse> {
    // 流程实例UUID
    private String flowInstUuid;
    // 流程定义ID
    private String flowDefinitonId;
    // 流程名称
    private String flowName;
    // 环节实例UUID
    private String taskInstUuid;
    // 环节ID
    private String taskId;
    // 环节名称
    private String taskName;
    // 办理人ID
    private String operatorId;
    // 办理人名称
    private String operatorName;
    // 操作动作名称
    private String action;
    // 操作动作类型
    private String actionType;
    // 第一个环节是否提交完成
    private Boolean completedSubmitStartedTask;
    // 退回到的环节
    private String rollbackToTaskId;
    // 是否退回到流程的开始环节
    private Boolean rollbackToStartedTask;
    // 是否返回到流程实例的开始环节
    private Boolean returnToStartedTask;
    // 办理时间
    private Date operateTime;
    // 办理意见立场名称
    private String opinionName;
    // 办理意见立场值
    private String opinionValue;
    // 办理意见内容
    private String opinionText;
    // 流程是否结束
    private boolean flowCompleted;

    // 表单数据
    private Map<String, Object> formData;

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
     * @return the flowDefinitonId
     */
    public String getFlowDefinitonId() {
        return flowDefinitonId;
    }

    /**
     * @param flowDefinitonId 要设置的flowDefinitonId
     */
    public void setFlowDefinitonId(String flowDefinitonId) {
        this.flowDefinitonId = flowDefinitonId;
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
     * @return the operatorId
     */
    public String getOperatorId() {
        return operatorId;
    }

    /**
     * @param operatorId 要设置的operatorId
     */
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * @return the operatorName
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * @param operatorName 要设置的operatorName
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
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
     * @return the completedSubmitStartedTask
     */
    public Boolean getCompletedSubmitStartedTask() {
        return completedSubmitStartedTask;
    }

    /**
     * @param completedSubmitStartedTask 要设置的completedSubmitStartedTask
     */
    public void setCompletedSubmitStartedTask(Boolean completedSubmitStartedTask) {
        this.completedSubmitStartedTask = completedSubmitStartedTask;
    }

    /**
     * @return the rollbackToTaskId
     */
    public String getRollbackToTaskId() {
        return rollbackToTaskId;
    }

    /**
     * @param rollbackToTaskId 要设置的rollbackToTaskId
     */
    public void setRollbackToTaskId(String rollbackToTaskId) {
        this.rollbackToTaskId = rollbackToTaskId;
    }

    /**
     * @return the rollbackToStartedTask
     */
    public Boolean getRollbackToStartedTask() {
        return rollbackToStartedTask;
    }

    /**
     * @param rollbackToStartedTask 要设置的rollbackToStartedTask
     */
    public void setRollbackToStartedTask(Boolean rollbackToStartedTask) {
        this.rollbackToStartedTask = rollbackToStartedTask;
    }

    /**
     * @return the returnToStartedTask
     */
    public Boolean getReturnToStartedTask() {
        return returnToStartedTask;
    }

    /**
     * @param returnToStartedTask 要设置的returnToStartedTask
     */
    public void setReturnToStartedTask(Boolean returnToStartedTask) {
        this.returnToStartedTask = returnToStartedTask;
    }

    /**
     * @return the operateTime
     */
    public Date getOperateTime() {
        return operateTime;
    }

    /**
     * @param operateTime 要设置的operateTime
     */
    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * @return the opinionName
     */
    public String getOpinionName() {
        return opinionName;
    }

    /**
     * @param opinionName 要设置的opinionName
     */
    public void setOpinionName(String opinionName) {
        this.opinionName = opinionName;
    }

    /**
     * @return the opinionValue
     */
    public String getOpinionValue() {
        return opinionValue;
    }

    /**
     * @param opinionValue 要设置的opinionValue
     */
    public void setOpinionValue(String opinionValue) {
        this.opinionValue = opinionValue;
    }

    /**
     * @return the opinionText
     */
    public String getOpinionText() {
        return opinionText;
    }

    /**
     * @param opinionText 要设置的opinionText
     */
    public void setOpinionText(String opinionText) {
        this.opinionText = opinionText;
    }

    /**
     * @return the flowCompleted
     */
    public boolean isFlowCompleted() {
        return flowCompleted;
    }

    /**
     * @param flowCompleted 要设置的flowCompleted
     */
    public void setFlowCompleted(boolean flowCompleted) {
        this.flowCompleted = flowCompleted;
    }

    /**
     * @return the formData
     */
    public Map<String, Object> getFormData() {
        return formData;
    }

    /**
     * @param formData 要设置的formData
     */
    public void setFormData(Map<String, Object> formData) {
        this.formData = formData;
    }

    @Override
    public String getApiServiceName() {
        return ApiServiceName.TASK_OPERATE_PROCESS_POST;
    }

    @Override
    public Class<TaskOperateProcessPostResponse> getResponseClass() {
        return TaskOperateProcessPostResponse.class;
    }

}
