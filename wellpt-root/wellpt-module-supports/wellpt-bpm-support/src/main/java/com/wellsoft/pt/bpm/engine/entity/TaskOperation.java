/*
 * @(#)2012-11-20 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 任务执行历史表
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-20.1	zhulh		2012-11-20		Create
 * </pre>
 * @date 2012-11-20
 */
@Entity
@Table(name = "wf_task_operation")
@DynamicUpdate
@DynamicInsert
public class TaskOperation extends IdEntity {
    private static final long serialVersionUID = 4284460319741566316L;

    // 操作动作
    private String action;
    // 操作类型
    private String actionType;
    // 操作代码
    private Integer actionCode;
    // 办理意见立场
    private String opinionValue;
    // 办理意见立场文本
    private String opinionLabel;
    // 办理意见内容
    private String opinionText;
    // 办理意见附件ID
    private String opinionFileIds;
    // 操作人ID
    private String assignee;
    // 操作人名称
    private String assigneeName;
    // 操作人身份ID
    private String operatorIdentityId;
    // 操作人身份路径名称
    private String operatorIdentityNamePath;
    // 主送人ID
    private String userId;
    // 抄送人ID
    private String copyUserId;
    // 所在任务实例
    private String taskId;
    // 所在任务实例
    private String taskName;
    // 所在任务实例
    private String taskInstUuid;
    // 所在流程实例
    private String flowInstUuid;
    // 所在待办实体UUID
    private String taskIdentityUuid;
    // 附加信息
    private String extraInfo;
    // 是否移动端应用的操作
    private Boolean isMobileApp;

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
     * @return the actionCode
     */
    public Integer getActionCode() {
        return actionCode;
    }

    /**
     * @param actionCode 要设置的actionCode
     */
    public void setActionCode(Integer actionCode) {
        this.actionCode = actionCode;
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
     * @return the opinionLabel
     */
    public String getOpinionLabel() {
        return opinionLabel;
    }

    /**
     * @param opinionLabel 要设置的opinionLabel
     */
    public void setOpinionLabel(String opinionLabel) {
        this.opinionLabel = opinionLabel;
    }

    /**
     * @return the opinionText
     */
    @Column(length = 2000)
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
     * @return the opinionFileIds
     */
    public String getOpinionFileIds() {
        return opinionFileIds;
    }

    /**
     * @param opinionFileIds 要设置的opinionFileIds
     */
    public void setOpinionFileIds(String opinionFileIds) {
        this.opinionFileIds = opinionFileIds;
    }

    /**
     * @return the assignee
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     * @param assignee 要设置的assignee
     */
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    /**
     * @return the assigneeName
     */
    public String getAssigneeName() {
        return assigneeName;
    }

    /**
     * @param assigneeName 要设置的assigneeName
     */
    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    /**
     * @return the operatorIdentityId
     */
    public String getOperatorIdentityId() {
        return operatorIdentityId;
    }

    /**
     * @param operatorIdentityId 要设置的operatorIdentityId
     */
    public void setOperatorIdentityId(String operatorIdentityId) {
        this.operatorIdentityId = operatorIdentityId;
    }

    /**
     * @return the operatorIdentityNamePath
     */
    public String getOperatorIdentityNamePath() {
        return operatorIdentityNamePath;
    }

    /**
     * @param operatorIdentityNamePath 要设置的operatorIdentityNamePath
     */
    public void setOperatorIdentityNamePath(String operatorIdentityNamePath) {
        this.operatorIdentityNamePath = operatorIdentityNamePath;
    }

    /**
     * @return the userId
     */
    @Column(length = 2000)
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the copyUserId
     */
    @Column(length = 2000)
    public String getCopyUserId() {
        return copyUserId;
    }

    /**
     * @param copyUserId 要设置的copyUserId
     */
    public void setCopyUserId(String copyUserId) {
        this.copyUserId = copyUserId;
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
     * @return the taskInstUuid
     */
    @Column(name = "task_inst_uuid")
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
    @Column(name = "flow_inst_uuid")
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
     * @return the taskIdentityUuid
     */
    public String getTaskIdentityUuid() {
        return taskIdentityUuid;
    }

    /**
     * @param taskIdentityUuid 要设置的taskIdentityUuid
     */
    public void setTaskIdentityUuid(String taskIdentityUuid) {
        this.taskIdentityUuid = taskIdentityUuid;
    }

    /**
     * @return the extraInfo
     */
    @Column(length = 4000)
    public String getExtraInfo() {
        return extraInfo;
    }

    /**
     * @param extraInfo 要设置的extraInfo
     */
    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    /**
     * @return the isMobileApp
     */
    public Boolean getIsMobileApp() {
        return isMobileApp;
    }

    /**
     * @param isMobileApp 要设置的isMobileApp
     */
    public void setIsMobileApp(Boolean isMobileApp) {
        this.isMobileApp = isMobileApp;
    }

}
