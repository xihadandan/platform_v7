/*
 * @(#)2015-6-23 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query;

import com.wellsoft.context.jdbc.entity.IdEntity;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-23.1	zhulh		2015-6-23		Create
 * </pre>
 * @date 2015-6-23
 */
public class TaskSubFlowDeleteQueryItem extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 568245189156447339L;

    // 父任务实例UUID
    private String parentTaskInstUuid;
    // 父流程实例UUID
    private String parentFlowInstUuid;
    // 父流程任务ID
    private String parentTaskId;
    // 子流程实例UUID
    private String flowInstUuid;
    // 子流程ID
    private String flowId;
    // 排序
    private Integer sortOrder;
    // 办理对象ID
    //	private String todoId;
    // 办理对象名称
    private String todoName;
    // 跟踪人员
    //	private String monitorId;
    // 是否主办
    private Boolean isMajor;
    // 是否合并
    private Boolean isMerge;
    // 是否等待
    private Boolean isWait;
    // 是否共享
    private Boolean isShare;
    // 办结通知其他子流程在办人员
    private Boolean notifyDoing;
    // 拷贝信息
    private String copyBotRuleId;
    // 实时同步
    private String syncBotRuleId;
    // 办结时反馈
    private Boolean returnWithOver;
    // 流向反馈
    private Boolean returnWithDirection;
    // 反馈流向
    private String returnDirectionId;
    // 反馈信息
    private String returnBotRuleId;
    // 复制字段
    private String copyFields;
    // 返回复盖的字段
    private String returnOverrideFields;
    // 返回附加的字段
    private String returnAdditionFields;

    // 标记子流程是否已经完成
    private Boolean completed;

    // 完成状态 0运行中、1正常结束、2终止、3撤销、4退回主流程
    private Integer completionState;

    /**
     * @return the parentTaskInstUuid
     */
    public String getParentTaskInstUuid() {
        return parentTaskInstUuid;
    }

    /**
     * @param parentTaskInstUuid 要设置的parentTaskInstUuid
     */
    public void setParentTaskInstUuid(String parentTaskInstUuid) {
        this.parentTaskInstUuid = parentTaskInstUuid;
    }

    /**
     * @return the parentFlowInstUuid
     */
    public String getParentFlowInstUuid() {
        return parentFlowInstUuid;
    }

    /**
     * @param parentFlowInstUuid 要设置的parentFlowInstUuid
     */
    public void setParentFlowInstUuid(String parentFlowInstUuid) {
        this.parentFlowInstUuid = parentFlowInstUuid;
    }

    /**
     * @return the parentTaskId
     */
    public String getParentTaskId() {
        return parentTaskId;
    }

    /**
     * @param parentTaskId 要设置的parentTaskId
     */
    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
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
     * @return the flowId
     */
    public String getFlowId() {
        return flowId;
    }

    /**
     * @param flowId 要设置的flowId
     */
    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
     * @return the isMajor
     */
    public Boolean getMajor() {
        return isMajor;
    }

    /**
     * @param isMajor 要设置的isMajor
     */
    public void setMajor(Boolean major) {
        isMajor = major;
    }

    /**
     * @return the isMerge
     */
    public Boolean getMerge() {
        return isMerge;
    }

    /**
     * @param isMerge 要设置的isMerge
     */
    public void setMerge(Boolean merge) {
        isMerge = merge;
    }

    /**
     * @return the isWait
     */
    public Boolean getWait() {
        return isWait;
    }

    /**
     * @param isWait 要设置的isWait
     */
    public void setWait(Boolean wait) {
        isWait = wait;
    }

    /**
     * @return the isShare
     */
    public Boolean getShare() {
        return isShare;
    }

    /**
     * @param isShare 要设置的isShare
     */
    public void setShare(Boolean share) {
        isShare = share;
    }

    /**
     * @return the notifyDoing
     */
    public Boolean getNotifyDoing() {
        return notifyDoing;
    }

    /**
     * @param notifyDoing 要设置的notifyDoing
     */
    public void setNotifyDoing(Boolean notifyDoing) {
        this.notifyDoing = notifyDoing;
    }

    /**
     * @return the copyBotRuleId
     */
    public String getCopyBotRuleId() {
        return copyBotRuleId;
    }

    /**
     * @param copyBotRuleId 要设置的copyBotRuleId
     */
    public void setCopyBotRuleId(String copyBotRuleId) {
        this.copyBotRuleId = copyBotRuleId;
    }

    /**
     * @return the syncBotRuleId
     */
    public String getSyncBotRuleId() {
        return syncBotRuleId;
    }

    /**
     * @param syncBotRuleId 要设置的syncBotRuleId
     */
    public void setSyncBotRuleId(String syncBotRuleId) {
        this.syncBotRuleId = syncBotRuleId;
    }

    /**
     * @return the returnWithOver
     */
    public Boolean getReturnWithOver() {
        return returnWithOver;
    }

    /**
     * @param returnWithOver 要设置的returnWithOver
     */
    public void setReturnWithOver(Boolean returnWithOver) {
        this.returnWithOver = returnWithOver;
    }

    /**
     * @return the returnWithDirection
     */
    public Boolean getReturnWithDirection() {
        return returnWithDirection;
    }

    /**
     * @param returnWithDirection 要设置的returnWithDirection
     */
    public void setReturnWithDirection(Boolean returnWithDirection) {
        this.returnWithDirection = returnWithDirection;
    }

    /**
     * @return the returnDirectionId
     */
    public String getReturnDirectionId() {
        return returnDirectionId;
    }

    /**
     * @param returnDirectionId 要设置的returnDirectionId
     */
    public void setReturnDirectionId(String returnDirectionId) {
        this.returnDirectionId = returnDirectionId;
    }

    /**
     * @return the returnBotRuleId
     */
    public String getReturnBotRuleId() {
        return returnBotRuleId;
    }

    /**
     * @param returnBotRuleId 要设置的returnBotRuleId
     */
    public void setReturnBotRuleId(String returnBotRuleId) {
        this.returnBotRuleId = returnBotRuleId;
    }

    /**
     * @return the copyFields
     */
    public String getCopyFields() {
        return copyFields;
    }

    /**
     * @param copyFields 要设置的copyFields
     */
    public void setCopyFields(String copyFields) {
        this.copyFields = copyFields;
    }

    /**
     * @return the returnOverrideFields
     */
    public String getReturnOverrideFields() {
        return returnOverrideFields;
    }

    /**
     * @param returnOverrideFields 要设置的returnOverrideFields
     */
    public void setReturnOverrideFields(String returnOverrideFields) {
        this.returnOverrideFields = returnOverrideFields;
    }

    /**
     * @return the returnAdditionFields
     */
    public String getReturnAdditionFields() {
        return returnAdditionFields;
    }

    /**
     * @param returnAdditionFields 要设置的returnAdditionFields
     */
    public void setReturnAdditionFields(String returnAdditionFields) {
        this.returnAdditionFields = returnAdditionFields;
    }

    /**
     * @return the completed
     */
    public Boolean getCompleted() {
        return completed;
    }

    /**
     * @param completed 要设置的completed
     */
    public void setCompleted(Boolean completed) {
        this.completed = completed;
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
}
