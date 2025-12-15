/*
 * @(#)2019年8月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.TaskRemindResponse;

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
public class TaskRemindRequest extends WellptRequest<TaskRemindResponse> {

    // 任务实例UUID
    private String uuid;
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
        return ApiServiceName.TASK_REMIND;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<TaskRemindResponse> getResponseClass() {
        return TaskRemindResponse.class;
    }

}
