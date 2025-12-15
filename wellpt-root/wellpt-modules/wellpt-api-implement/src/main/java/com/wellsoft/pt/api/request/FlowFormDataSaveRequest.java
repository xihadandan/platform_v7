/*
 * @(#)2014-10-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.FlowFormDataSaveResponse;

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
 * 2014-10-2.1	zhulh		2014-10-2		Create
 * </pre>
 * @date 2014-10-2
 */
public class FlowFormDataSaveRequest extends WellptRequest<FlowFormDataSaveResponse> {

    // 流程实例UUID
    private String flowInstUuid;

    // 表单数据
    private Map<String, Object> formData;

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
        return ApiServiceName.FLOW_FORM_DATA_SAVE;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<FlowFormDataSaveResponse> getResponseClass() {
        return FlowFormDataSaveResponse.class;
    }

}
