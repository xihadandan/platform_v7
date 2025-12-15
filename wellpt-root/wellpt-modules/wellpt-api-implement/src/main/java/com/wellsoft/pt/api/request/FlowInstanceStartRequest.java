/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.FlowInstanceStartResponse;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;

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
public class FlowInstanceStartRequest extends WellptRequest<FlowInstanceStartResponse> {

    // 流程定义ID
    private String flowDefinitionId;

    // 提交到的环节(DRAFT、AUTO_SUBMIT)
    private String toTaskId;

    // 交互式的任务数据
    private InteractionTaskData interactionTaskData;

    // 表单数据
    private Map<String, Object> formData;

    /**
     * @return the flowDefinitionId
     */
    public String getFlowDefinitionId() {
        return flowDefinitionId;
    }

    /**
     * @param flowDefinitionId 要设置的flowDefinitionId
     */
    public void setFlowDefinitionId(String flowDefinitionId) {
        this.flowDefinitionId = flowDefinitionId;
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

    /**
     * @return the interactionTaskData
     */
    public InteractionTaskData getInteractionTaskData() {
        return interactionTaskData;
    }

    /**
     * @param interactionTaskData 要设置的interactionTaskData
     */
    public void setInteractionTaskData(InteractionTaskData interactionTaskData) {
        this.interactionTaskData = interactionTaskData;
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
        return ApiServiceName.FLOW_INSTANCE_START;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<FlowInstanceStartResponse> getResponseClass() {
        return FlowInstanceStartResponse.class;
    }

}
