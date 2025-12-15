/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.FlowInstanceQueryRequest;
import com.wellsoft.pt.api.response.FlowInstanceQueryResponse;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-11.1	zhulh		2014-8-11		Create
 * </pre>
 * @date 2014-8-11
 */
@Service(ApiServiceName.FLOW_INSTANCE_QUERY)
@Transactional
public class FlowInstanceQueryServiceImpl extends BaseServiceImpl implements WellptService<FlowInstanceQueryRequest> {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FlowInstanceQueryRequest queryRequest) {
        String queryTitle = queryRequest.getTitle();
        String queryFlowDefinitionId = queryRequest.getFlowDefinitionId();
        String queryDataUuid = queryRequest.getDataUuid();

        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setCurrentPage(queryRequest.getPageNo());
        pagingInfo.setPageSize(queryRequest.getPageSize());

        FlowInstance example = new FlowInstance();
        if (StringUtils.isNotBlank(queryTitle)) {
            example.setTitle(queryTitle);
        }
        if (StringUtils.isNotBlank(queryFlowDefinitionId)) {
            example.setId(queryFlowDefinitionId);
        }
        if (StringUtils.isNotBlank(queryDataUuid)) {
            example.setDataUuid(queryDataUuid);
        }
        List<FlowInstance> flowInstances = this.dao.findByExample(example, null, pagingInfo);

        List<com.wellsoft.pt.api.domain.FlowInstance> instances = new ArrayList<com.wellsoft.pt.api.domain.FlowInstance>();
        for (FlowInstance flowInstance : flowInstances) {
            // 流程实例UUID
            String uuid = flowInstance.getUuid();
            // 流程实例标题
            String title = flowInstance.getTitle();
            // 流程定义Id
            String flowDefinitionId = flowInstance.getId();
            // 流程实例开始时间
            Date startTime = flowInstance.getStartTime();
            // 流程实例结束时间
            Date endTime = flowInstance.getEndTime();
            // 流程实例发起人ID
            String startUserId = flowInstance.getStartUserId();
            // 表单定义UUId
            String formUuid = flowInstance.getFormUuid();
            // 表单数据UUID
            String dataUuid = flowInstance.getDataUuid();

            com.wellsoft.pt.api.domain.FlowInstance instance = new com.wellsoft.pt.api.domain.FlowInstance();
            instance.setUuid(uuid);
            instance.setTitle(title);
            instance.setFlowDefinitionId(flowDefinitionId);
            instance.setStartTime(startTime);
            instance.setEndTime(endTime);
            instance.setStartUserId(startUserId);
            instance.setFormUuid(formUuid);
            instance.setDataUuid(dataUuid);

            instances.add(instance);
        }

        FlowInstanceQueryResponse response = new FlowInstanceQueryResponse();
        response.setDataList(instances);
        response.setTotal(pagingInfo.getTotalCount());
        response.setStart(pagingInfo.getFirst());
        response.setSize(flowInstances.size());

        return response;
    }

}
