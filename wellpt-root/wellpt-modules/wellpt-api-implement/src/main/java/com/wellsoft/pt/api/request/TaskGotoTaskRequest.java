/*
 * @(#)2019年8月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.TaskGotoTaskResponse;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月13日.1	zhulh		2019年8月13日		Create
 * </pre>
 * @date 2019年8月13日
 */
public class TaskGotoTaskRequest extends WellptRequest<TaskGotoTaskResponse> {
    // 任务实例UUID
    private String uuid;
    // 要跳转的环节ID
    private String gotoTaskId;
    // 跳转环节办理人
    private Map<String, List<String>> taskUsers;
    private Map<String, List<String>> taskCopyUsers;
    private Map<String, List<String>> taskMonitorUsers;
    // 办理意见立场名称
    private String opinionName;
    // 办理意见立场值
    private String opinionValue;
    // 办理意见内容
    private String opinionText;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the gotoTaskId
     */
    public String getGotoTaskId() {
        return gotoTaskId;
    }

    /**
     * @param gotoTaskId 要设置的gotoTaskId
     */
    public void setGotoTaskId(String gotoTaskId) {
        this.gotoTaskId = gotoTaskId;
    }

    /**
     * @return the taskUsers
     */
    public Map<String, List<String>> getTaskUsers() {
        return taskUsers;
    }

    /**
     * @param taskUsers 要设置的taskUsers
     */
    public void setTaskUsers(Map<String, List<String>> taskUsers) {
        this.taskUsers = taskUsers;
    }

    /**
     * @return the taskCopyUsers
     */
    public Map<String, List<String>> getTaskCopyUsers() {
        return taskCopyUsers;
    }

    /**
     * @param taskCopyUsers 要设置的taskCopyUsers
     */
    public void setTaskCopyUsers(Map<String, List<String>> taskCopyUsers) {
        this.taskCopyUsers = taskCopyUsers;
    }

    /**
     * @return the taskMonitorUsers
     */
    public Map<String, List<String>> getTaskMonitorUsers() {
        return taskMonitorUsers;
    }

    /**
     * @param taskMonitorUsers 要设置的taskMonitorUsers
     */
    public void setTaskMonitorUsers(Map<String, List<String>> taskMonitorUsers) {
        this.taskMonitorUsers = taskMonitorUsers;
    }

    /**
     * @return the opinionName
     */
    public String getOpinionName() {
        return opinionName;
    }

    /**
     * @param opinionName 要设置的opinionName
     */
    public void setOpinionName(String opinionName) {
        this.opinionName = opinionName;
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
     * @return the opinionText
     */
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
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return ApiServiceName.TASK_GOTO_TASK;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<TaskGotoTaskResponse> getResponseClass() {
        return TaskGotoTaskResponse.class;
    }

}
