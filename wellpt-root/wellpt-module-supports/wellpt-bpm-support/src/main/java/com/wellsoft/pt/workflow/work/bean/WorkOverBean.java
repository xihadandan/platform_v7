/*
 * @(#)2013-3-7 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
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
 * 2013-3-7.1	zhulh		2013-3-7		Create
 * </pre>
 * @date 2013-3-7
 */
public class WorkOverBean {
    private String taskUuid;

    private String flowInstUuid;

    /**
     * 标题
     */
    private String title;
    /**
     * 完成时间
     */
    private Date endTime;
    /**
     * 流程
     */
    private String flowName;
    /**
     * 流程分类
     */
    private String category;

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

    public String getFlowInstUuid() {
        return flowInstUuid;
    }

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
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category 要设置的category
     */
    public void setCategory(String category) {
        this.category = category;
    }

}
