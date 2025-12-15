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
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-8.1	Administrator		2015-1-8		Create
 * </pre>
 * @date 2015-1-8
 */
@Entity
@Table(name = "wf_task_form_opinion_log")
@DynamicUpdate
@DynamicInsert
public class TaskFormOpinionLog extends IdEntity {

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

    // 意见记录UUID
    private String taskFormOpinionUuid;

    // 表单意见域字段名
    private String fieldName;

    // 表单意见域内容
    private String content;

    // 记录方式(1不替换、2替换原值、3附加)
    private Integer recordWay;

    // 状态0正常、1、挂起、2删除
    private Integer status;

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
     * @return the taskFormOpinionUuid
     */
    public String getTaskFormOpinionUuid() {
        return taskFormOpinionUuid;
    }

    /**
     * @param taskFormOpinionUuid 要设置的taskFormOpinionUuid
     */
    public void setTaskFormOpinionUuid(String taskFormOpinionUuid) {
        this.taskFormOpinionUuid = taskFormOpinionUuid;
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
     * @return the recordWay
     */
    public Integer getRecordWay() {
        return recordWay;
    }

    /**
     * @param recordWay 要设置的recordWay
     */
    public void setRecordWay(Integer recordWay) {
        this.recordWay = recordWay;
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

}
