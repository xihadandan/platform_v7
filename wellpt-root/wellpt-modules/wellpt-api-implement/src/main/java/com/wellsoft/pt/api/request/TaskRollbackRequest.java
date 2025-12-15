/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.TaskRollbackResponse;

/**
 * Description: 任务退回请求
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
public class TaskRollbackRequest extends WellptRequest<TaskRollbackResponse> {
    // 要退回环节
    protected String rollbackToTaskId;
    // 要退回环节实例UUID
    protected String rollbackToTaskInstUuid;
    // 任务实例UUID
    private String uuid;
    // 办理意见立场名称
    private String opinionName;
    // 办理意见立场
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
     * @return the rollbackToTaskId
     */
    public String getRollbackToTaskId() {
        return rollbackToTaskId;
    }

    /**
     * @param rollbackToTaskId 要设置的rollbackToTaskId
     */
    public void setRollbackToTaskId(String rollbackToTaskId) {
        this.rollbackToTaskId = rollbackToTaskId;
    }

    /**
     * @return the rollbackToTaskInstUuid
     */
    public String getRollbackToTaskInstUuid() {
        return rollbackToTaskInstUuid;
    }

    /**
     * @param rollbackToTaskInstUuid 要设置的rollbackToTaskInstUuid
     */
    public void setRollbackToTaskInstUuid(String rollbackToTaskInstUuid) {
        this.rollbackToTaskInstUuid = rollbackToTaskInstUuid;
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
        return ApiServiceName.TASK_ROLL_BACK;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<TaskRollbackResponse> getResponseClass() {
        return TaskRollbackResponse.class;
    }

}
