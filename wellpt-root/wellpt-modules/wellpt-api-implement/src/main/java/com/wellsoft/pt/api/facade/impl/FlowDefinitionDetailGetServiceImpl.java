/*
 * @(#)2014-8-17 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.FlowDefinitionDetailGetReqeust;
import com.wellsoft.pt.api.response.FlowDefinitionDetailGetResponse;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.service.FlowSchemaService;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-17.1	zhulh		2014-8-17		Create
 * </pre>
 * @date 2014-8-17
 */
@Service(ApiServiceName.FLOW_DEFINITION_DETAIL_GET)
@Transactional
public class FlowDefinitionDetailGetServiceImpl extends BaseServiceImpl implements WellptService<FlowDefinitionDetailGetReqeust> {

    @Autowired
    private FlowService flowService;

    @Autowired
    private FlowSchemaService flowSchemaService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FlowDefinitionDetailGetReqeust detailGetReqeust) {
        String flowDefinitionId = detailGetReqeust.getFlowDefinitionId();
        // 获取流程定义
        FlowDefinition flowDefinition = flowService.getFlowDefinitionById(flowDefinitionId);
        // 流程定义ID
        String id = flowDefinition.getId();
        // 流程定义名称
        String name = flowDefinition.getName();
        // 版本号
        Double version = flowDefinition.getVersion();
        // 使用表单的UUID
        String formUuid = flowDefinition.getFormUuid();
        // 使用表单的名称
        String formName = flowDefinition.getFormName();
        // 创建时间
        Date createTime = flowDefinition.getCreateTime();

        com.wellsoft.pt.api.domain.FlowDefinitionDetail detail = new com.wellsoft.pt.api.domain.FlowDefinitionDetail();
        detail.setId(id);
        detail.setName(name);
        detail.setVersion(version);
        detail.setFormUuid(formUuid);
        detail.setFormName(formName);
        detail.setCreateTime(createTime);
        detail.setXml(flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid()).getContentAsString());

        FlowDefinitionDetailGetResponse response = new FlowDefinitionDetailGetResponse();
        response.setData(detail);
        return response;
    }

}
