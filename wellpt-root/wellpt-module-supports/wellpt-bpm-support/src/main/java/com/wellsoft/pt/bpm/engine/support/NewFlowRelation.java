/*
 * @(#)2014-3-5 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-5.1	zhulh		2014-3-5		Create
 * </pre>
 * @date 2014-3-5
 */
public class NewFlowRelation {
    // 子流程名称
    private String newFlowName;
    // 子流程ID
    private String newFlowId;
    // 环节名称
    private String taskName;
    // 环节ID
    private String taskId;
    // 前置子流程名称
    private String frontNewFlowName;
    // 前置子流程ID
    private String frontNewFlowId;
    // 前置环节名称
    private String frontTaskName;
    // 前置环节ID
    private String frontTaskId;

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

}
