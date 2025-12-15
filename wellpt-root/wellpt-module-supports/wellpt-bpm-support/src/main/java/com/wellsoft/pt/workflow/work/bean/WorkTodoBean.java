/*
 * @(#)2012-11-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import java.util.Date;

/**
 * Description: 待办工作值对象类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-26.1	zhulh		2012-11-26		Create
 * </pre>
 * @date 2012-11-26
 */
public class WorkTodoBean {
    /**
     * 任务UUID
     */
    private String taskUuid;

    /**
     * 任务所在流程实例
     */
    private String flowInstUuid;

    /**
     * 标题
     */
    private String title;

    /**
     * 当前环节
     */
    private String taskName;

    /**
     * 前办理人
     */
    private String previousAssignee;

    /**
     * 到达时间
     */
    private Date arrivalTime;

    /**
     * 到期时间
     */
    private Date dueTime;

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
     * @return the previousAssignee
     */
    public String getPreviousAssignee() {
        return previousAssignee;
    }

    /**
     * @param previousAssignee 要设置的previousAssignee
     */
    public void setPreviousAssignee(String previousAssignee) {
        this.previousAssignee = previousAssignee;
    }

    /**
     * @return the arrivalTime
     */
    public Date getArrivalTime() {
        return arrivalTime;
    }

    /**
     * @param arrivalTime 要设置的arrivalTime
     */
    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * @return the dueTime
     */
    public Date getDueTime() {
        return dueTime;
    }

    /**
     * @param dueTime 要设置的dueTime
     */
    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
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
