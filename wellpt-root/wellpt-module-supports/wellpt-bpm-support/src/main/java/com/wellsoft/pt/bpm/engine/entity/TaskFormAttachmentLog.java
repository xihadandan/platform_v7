/*
 * @(#)2015-1-8 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年3月26日.1	zhulh		2019年3月26日		Create
 * </pre>
 * @date 2019年3月26日
 */
@Entity
@Table(name = "wf_task_form_attachment_log")
@DynamicUpdate
@DynamicInsert
public class TaskFormAttachmentLog extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2020870545045671224L;

    // 流程实例UUID
    private String flowInstUuid;

    // 环节实例UUID
    private String taskInstUuid;

    // 环节操作UUID
    private String taskOperationUuid;

    // 附件记录UUID
    private String taskFormAttachmentUuid;

    // 表单附件域字段名
    private String fieldName;

    // 表单附件域内容
    private String content;

    // 操作类型
    private String operateType;

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
     * @return the taskOperationUuid
     */
    public String getTaskOperationUuid() {
        return taskOperationUuid;
    }

    /**
     * @param taskOperationUuid 要设置的taskOperationUuid
     */
    public void setTaskOperationUuid(String taskOperationUuid) {
        this.taskOperationUuid = taskOperationUuid;
    }

    /**
     * @return the taskFormAttachmentUuid
     */
    public String getTaskFormAttachmentUuid() {
        return taskFormAttachmentUuid;
    }

    /**
     * @param taskFormAttachmentUuid 要设置的taskFormAttachmentUuid
     */
    public void setTaskFormAttachmentUuid(String taskFormAttachmentUuid) {
        this.taskFormAttachmentUuid = taskFormAttachmentUuid;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName 要设置的fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the operateType
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * @param operateType 要设置的operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

}
