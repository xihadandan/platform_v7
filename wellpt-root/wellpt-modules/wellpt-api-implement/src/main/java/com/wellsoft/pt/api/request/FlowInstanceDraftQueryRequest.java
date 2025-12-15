/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptQueryRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.FlowInstanceDraftQueryResponse;

/**
 * Description: 流程实例列表查询
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
public class FlowInstanceDraftQueryRequest extends WellptQueryRequest<FlowInstanceDraftQueryResponse> {

    // 流程实例标题
    private String title;

    // 流程定义ID
    private String flowDefinitionId;

    // 数据UUID
    private String dataUuid;

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
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return ApiServiceName.FLOW_INSTANCE_DRAFT_QUERY;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<FlowInstanceDraftQueryResponse> getResponseClass() {
        return FlowInstanceDraftQueryResponse.class;
    }

}
