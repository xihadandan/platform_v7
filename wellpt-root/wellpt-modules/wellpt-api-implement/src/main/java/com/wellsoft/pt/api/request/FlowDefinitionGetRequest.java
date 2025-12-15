/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.FlowDefinitionGetResponse;

/**
 * Description: 获取某流程定义信息
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
public class FlowDefinitionGetRequest extends WellptRequest<FlowDefinitionGetResponse> {

    private String flowDefinitionId;

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
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return ApiServiceName.FLOW_DEFINITION_GET;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<FlowDefinitionGetResponse> getResponseClass() {
        return FlowDefinitionGetResponse.class;
    }

}
