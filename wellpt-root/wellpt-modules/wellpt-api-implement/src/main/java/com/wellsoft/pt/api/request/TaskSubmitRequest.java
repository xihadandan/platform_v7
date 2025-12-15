/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.TaskSubmitResponse;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;

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
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public class TaskSubmitRequest extends WellptRequest<TaskSubmitResponse> {

    public static final String END_FLOW_ID = FlowDelegate.END_FLOW_ID;

    // 任务实例UUID
    private String uuid;
    // 目标环节ID
    private String toTaskId;
    // 目标环节办理人
    private Map<String, List<String>> taskUsers;
    // 目标环节抄送人
    private Map<String, List<String>> taskCopyUsers;
    // 目标环节督办人
    private Map<String, List<String>> taskMonitorUsers;

    // 办理意见立场名称
    private String opinionName;
    // 办理意见立场值
    private String opinionValue;
    // 办理意见内容
    private String opinionText;

    // 数据放置于DyformData类中的formDatas 成员中
    // 成员formDatas的定义如下:
    // private Map<String/*表单定义uuid*/, List<Map<String /*表单字段名*/,
    // Object/*表单字段值*/>>> formDatas;
    // 表单数据
    private Map<String, Object> formData;

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
     * @return the toTaskId
     */
    public String getToTaskId() {
        return toTaskId;
    }

    /**
     * @param toTaskId 要设置的toTaskId
     */
    public void setToTaskId(String toTaskId) {
        this.toTaskId = toTaskId;
    }

    public Map<String, List<String>> getTaskUsers() {
        return taskUsers;
    }

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
     * @return the formData
     */
    public Map<String, Object> getFormData() {
        return formData;
    }

    /**
     * @param formData 要设置的formData
     */
    public void setFormData(Map<String, Object> formData) {
        this.formData = formData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return ApiServiceName.TASK_SUBMIT;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<TaskSubmitResponse> getResponseClass() {
        return TaskSubmitResponse.class;
    }

}
