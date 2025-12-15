/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptQueryRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.FlowDefinitionQueryResponse;

/**
 * Description: 流程定义列表查询
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
public class FlowDefinitionQueryRequest extends WellptQueryRequest<FlowDefinitionQueryResponse> {

    // 流程名称
    private String flowDefinitionName;
    // 流程分类
    private String category;
    // 是否启用
    private Boolean enabled;

    /**
     * @return the flowDefinitionName
     */
    public String getFlowDefinitionName() {
        return flowDefinitionName;
    }

    /**
     * @param flowDefinitionName 要设置的flowDefinitionName
     */
    public void setFlowDefinitionName(String flowDefinitionName) {
        this.flowDefinitionName = flowDefinitionName;
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

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return ApiServiceName.FLOW_DEFINITION_QUERY;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<FlowDefinitionQueryResponse> getResponseClass() {
        return FlowDefinitionQueryResponse.class;
    }

}
