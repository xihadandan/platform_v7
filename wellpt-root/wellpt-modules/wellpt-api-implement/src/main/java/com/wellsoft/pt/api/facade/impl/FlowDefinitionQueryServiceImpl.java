/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.FlowDefinitionQueryRequest;
import com.wellsoft.pt.api.response.FlowDefinitionQueryResponse;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQueryItem;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

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
@Service(ApiServiceName.FLOW_DEFINITION_QUERY)
public class FlowDefinitionQueryServiceImpl extends BaseServiceImpl implements WellptService<FlowDefinitionQueryRequest> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FlowDefinitionQueryRequest queryRequest) {
        String flowDefinitionName = queryRequest.getFlowDefinitionName();
        String category = queryRequest.getCategory();
        Boolean enabled = queryRequest.getEnabled();

        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setCurrentPage(queryRequest.getPageNo());
        pagingInfo.setPageSize(queryRequest.getPageSize());

        //        FlowDefinition example = new FlowDefinition();
        //        example.setName(flowDefinitionName);
        //        example.setCategory(category);
        //        List<FlowDefinition> flowDefinitions = this.dao.findByExample(example, null, pagingInfo);

        FlowDefinitionQuery flowDefinitionQuery = FlowEngine.getInstance().createQuery(FlowDefinitionQuery.class);
        if (StringUtils.isNotBlank(flowDefinitionName)) {
            flowDefinitionQuery.nameLike(flowDefinitionName);
        }
        if (StringUtils.isNotBlank(category)) {
            flowDefinitionQuery.category(category);
        }
        if (enabled != null) {
            flowDefinitionQuery.enabled(enabled);
        }
        List<Permission> permissions = Lists.newArrayList();
        permissions.add(BasePermission.CREATE);
        flowDefinitionQuery.permission(SpringSecurityUtils.getCurrentUserId(), permissions);
        flowDefinitionQuery.distinctVersion();
        flowDefinitionQuery.setFirstResult(pagingInfo.getFirst());
        flowDefinitionQuery.setMaxResults(pagingInfo.getPageSize());
        flowDefinitionQuery.orderByCodeAsc();
        List<FlowDefinitionQueryItem> flowDefinitions = flowDefinitionQuery.list();
        long totalCount = flowDefinitionQuery.count();

        List<com.wellsoft.pt.api.domain.FlowDefinition> definitions = new ArrayList<com.wellsoft.pt.api.domain.FlowDefinition>();
        for (FlowDefinitionQueryItem flowDefinition : flowDefinitions) {
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

            com.wellsoft.pt.api.domain.FlowDefinition definition = new com.wellsoft.pt.api.domain.FlowDefinition();
            definition.setId(id);
            definition.setName(name);
            definition.setVersion(version);
            definition.setFormUuid(formUuid);
            definition.setFormName(formName);
            definition.setCreateTime(createTime);

            definitions.add(definition);
        }

        FlowDefinitionQueryResponse response = new FlowDefinitionQueryResponse();
        response.setDataList(definitions);
        response.setTotal(totalCount);
        response.setStart(pagingInfo.getFirst());
        response.setSize(flowDefinitions.size());

        return response;
    }

}
