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
public class TaskSubFlowRelationDeleteQueryItem extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7321669197526915287L;

    // 子流程实例UUID
    private String newFlowInstUuid;
    // 子流程名称
    private String newFlowName;
    // 子流程ID
    private String newFlowId;
    // 环节名称
    private String taskName;
    // 环节ID
    private String taskId;
    // 前置子流程名称
    private String frontNewFlowInstUuid;
    // 前置子流程名称
    private String frontNewFlowName;
    // 前置子流程ID
    private String frontNewFlowId;
    // 前置环节名称
    private String frontTaskName;
    // 前置环节ID
    private String frontTaskId;

    // 是否允许后置环节提交，如果允许后置环节不用等待前置环节提交便可提交
    private Boolean allowSubmit;

    // 前置流程环节提交状态，(1已提交、0未提交)
    private Integer submitStatus;

    // 子流程关系所属的子流程
    //	@UnCloneable
    //	private TaskSubFlow taskSubFlow;
    private String taskSubFlowUuid;

    /**
     * @return the newFlowInstUuid
     */
    public String getNewFlowInstUuid() {
        return newFlowInstUuid;
    }

    /**
     * @param newFlowInstUuid 要设置的newFlowInstUuid
     */
    public void setNewFlowInstUuid(String newFlowInstUuid) {
        this.newFlowInstUuid = newFlowInstUuid;
    }

    /**
     * @return the newFlowName
     */
    public String getNewFlowName() {
        return newFlowName;
    }

    /**
     * @param newFlowName 要设置的newFlowName
     */
    public void setNewFlowName(String newFlowName) {
        this.newFlowName = newFlowName;
    }

    /**
     * @return the newFlowId
     */
    public String getNewFlowId() {
        return newFlowId;
    }

    /**
     * @param newFlowId 要设置的newFlowId
     */
    public void setNewFlowId(String newFlowId) {
        this.newFlowId = newFlowId;
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
     * @return the frontNewFlowInstUuid
     */
    public String getFrontNewFlowInstUuid() {
        return frontNewFlowInstUuid;
    }

    /**
     * @param frontNewFlowInstUuid 要设置的frontNewFlowInstUuid
     */
    public void setFrontNewFlowInstUuid(String frontNewFlowInstUuid) {
        this.frontNewFlowInstUuid = frontNewFlowInstUuid;
    }

    /**
     * @return the frontNewFlowName
     */
    public String getFrontNewFlowName() {
        return frontNewFlowName;
    }

    /**
     * @param frontNewFlowName 要设置的frontNewFlowName
     */
    public void setFrontNewFlowName(String frontNewFlowName) {
        this.frontNewFlowName = frontNewFlowName;
    }

    /**
     * @return the frontNewFlowId
     */
    public String getFrontNewFlowId() {
        return frontNewFlowId;
    }

    /**
     * @param frontNewFlowId 要设置的frontNewFlowId
     */
    public void setFrontNewFlowId(String frontNewFlowId) {
        this.frontNewFlowId = frontNewFlowId;
    }

    /**
     * @return the frontTaskName
     */
    public String getFrontTaskName() {
        return frontTaskName;
    }

    /**
     * @param frontTaskName 要设置的frontTaskName
     */
    public void setFrontTaskName(String frontTaskName) {
        this.frontTaskName = frontTaskName;
    }

    /**
     * @return the frontTaskId
     */
    public String getFrontTaskId() {
        return frontTaskId;
    }

    /**
     * @param frontTaskId 要设置的frontTaskId
     */
    public void setFrontTaskId(String frontTaskId) {
        this.frontTaskId = frontTaskId;
    }

    /**
     * @return the allowSubmit
     */
    public Boolean getAllowSubmit() {
        return allowSubmit;
    }

    /**
     * @param allowSubmit 要设置的allowSubmit
     */
    public void setAllowSubmit(Boolean allowSubmit) {
        this.allowSubmit = allowSubmit;
    }

    /**
     * @return the submitStatus
     */
    public Integer getSubmitStatus() {
        return submitStatus;
    }

    /**
     * @param submitStatus 要设置的submitStatus
     */
    public void setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
    }

    /**
     * @return the taskSubFlowUuid
     */
    public String getTaskSubFlowUuid() {
        return taskSubFlowUuid;
    }

    /**
     * @param taskSubFlowUuid 要设置的taskSubFlowUuid
     */
    public void setTaskSubFlowUuid(String taskSubFlowUuid) {
        this.taskSubFlowUuid = taskSubFlowUuid;
    }

}
