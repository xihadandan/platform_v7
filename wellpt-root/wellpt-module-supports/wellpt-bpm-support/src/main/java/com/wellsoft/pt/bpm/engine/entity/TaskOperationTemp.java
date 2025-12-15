/*
 * @(#)2012-11-20 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 环节保存操作意见
 */
@Entity
@Table(name = "wf_task_operation_temp")
@DynamicUpdate
@DynamicInsert
public class TaskOperationTemp extends IdEntity {
    private static final long serialVersionUID = 4284460319741566316L;

    // 办理意见立场
    private String opinionValue;
    // 办理意见立场文本
    private String opinionLabel;
    // 办理意见内容
    private String opinionText;
    // 操作人ID
    private String assignee;
    // 操作人名称
    private String assigneeName;
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

    public String getOpinionValue() {
        return opinionValue;
    }

    public void setOpinionValue(String opinionValue) {
        this.opinionValue = opinionValue;
    }

    public String getOpinionLabel() {
        return opinionLabel;
    }

    public void setOpinionLabel(String opinionLabel) {
        this.opinionLabel = opinionLabel;
    }

    public String getOpinionText() {
        return opinionText;
    }

    public void setOpinionText(String opinionText) {
        this.opinionText = opinionText;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    public String getTaskIdentityUuid() {
        return taskIdentityUuid;
    }

    public void setTaskIdentityUuid(String taskIdentityUuid) {
        this.taskIdentityUuid = taskIdentityUuid;
    }
}
