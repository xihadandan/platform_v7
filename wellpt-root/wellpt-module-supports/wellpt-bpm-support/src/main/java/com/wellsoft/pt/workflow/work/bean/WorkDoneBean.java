/*
 * @(#)2012-11-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

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
 * 2012-11-29.1	zhulh		2012-11-29		Create
 * </pre>
 * @date 2012-11-29
 */
public class WorkDoneBean {
    /**
     * 任务实例UUID
     */
    private String taskUuid;
    /**
     * 流程实例UUID
     */
    private String flowInstUuid;
    /**
     * 流程定义UUID
     */
    private String flowDefUuid;

    /**
     * 标题
     */
    private String title;
    /**
     * 办理环节
     */
    private String taskName;
    /**
     * 办理人
     */
    private String assignee;
    /**
     * 最新的办理时间
     */
    private Date lastTime;
    /**
     * 流程
     */
    private String flowName;

    /**
     * @return the taskUuid
     */
    public String getTaskUuid() {
        return taskUuid;
    }

    /**
     * @param taskUuid 要设置的taskUuid
     */
    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
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
     * @return the flowDefUuid
     */
    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    /**
     * @param flowDefUuid 要设置的flowDefUuid
     */
    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @return the lastTime
     */
    public Date getLastTime() {
        return lastTime;
    }

    /**
     * @param lastTime 要设置的lastTime
     */
    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
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

}
