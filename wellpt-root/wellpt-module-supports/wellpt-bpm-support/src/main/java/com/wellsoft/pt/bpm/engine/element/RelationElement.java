/*
 * @(#)2013-10-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-28.1	zhulh		2013-10-28		Create
 * </pre>
 * @date 2013-10-28
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationElement implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8594530176422980135L;

    private String newFlowName;

    private String newFlowId;

    private String taskName;

    private String taskId;

    private String frontNewFlowName;

    private String frontNewFlowId;

    private String frontTaskName;

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
