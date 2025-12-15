/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.FlowInstanceDraftQueryRequest;
import com.wellsoft.pt.api.response.FlowInstanceQueryResponse;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.acl.support.QueryInfo;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
 * 2014-8-11.1	zhulh		2014-8-11		Create
 * </pre>
 * @date 2014-8-11
 */
@Service(ApiServiceName.FLOW_INSTANCE_DRAFT_QUERY)
public class FlowInstanceDraftQueryServiceImpl extends BaseServiceImpl implements WellptService<FlowInstanceDraftQueryRequest> {

    private static String SELECTION_HQL = "o.uuid as uuid, o.title as title, o.name as name, o.createTime as createTime, o.creator as creator,"
            + " o.modifyTime as modifyTime, o.modifier as modifier, o.formUuid as formUuid, o.dataUuid as dataUuid, o.reservedText1 as reservedText1,"
            + " o.reservedText2 as reservedText2, o.reservedText3 as reservedText3, o.reservedText4 as reservedText4, o.reservedText5 as reservedText5,"
            + " o.reservedText6 as reservedText6, o.reservedText7 as reservedText7, o.reservedText8 as reservedText8, o.reservedText9 as reservedText9,"
            + " o.reservedText10 as reservedText10, o.reservedText11 as reservedText11, o.reservedText12 as reservedText12, o.reservedNumber1 as reservedNumber1,"
            + " o.reservedNumber2 as reservedNumber2, o.reservedNumber3 as reservedNumber3, o.reservedDate1 as reservedDate1, o.reservedDate2 as reservedDate2";
    @Autowired
    private AclService aclService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FlowInstanceDraftQueryRequest queryRequest) {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setCurrentPage(queryRequest.getPageNo());
        pagingInfo.setPageSize(queryRequest.getPageSize());

        QueryInfo<FlowInstance> aclQueryInfo = createAclQueryInfo(queryRequest);
        QueryInfo<FlowInstance> flowInstanceQueryInfo = aclService.query(FlowInstance.class, aclQueryInfo,
                AclPermission.DRAFT, SpringSecurityUtils.getCurrentUserId());
        List<FlowInstance> flowInstances = flowInstanceQueryInfo.getPage().getResult();

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
        response.setTotal(flowInstanceQueryInfo.getPage().getTotalCount());
        response.setStart(pagingInfo.getFirst());
        response.setSize(flowInstances.size());

        return response;
    }

    /**
     * @param queryRequest
     * @return
     */
    private QueryInfo<FlowInstance> createAclQueryInfo(FlowInstanceDraftQueryRequest queryRequest) {
        QueryInfo<FlowInstance> aclQueryInfo = new QueryInfo<FlowInstance>();
        // 大小
        aclQueryInfo.getPage().setPageNo(queryRequest.getPageNo());
        aclQueryInfo.getPage().setPageSize(queryRequest.getPageSize());
        aclQueryInfo.getPage().setAutoCount(true);
        // 选择列
        aclQueryInfo.setSelectionHql(SELECTION_HQL);
        StringBuilder whereHql = new StringBuilder(" 1 = 1 ");
        Map<String, Object> values = Maps.newHashMap();
        // 查询
        if (StringUtils.isNotBlank(queryRequest.getDataUuid())) {
            whereHql.append(" and o.dataUuid = :dataUuid ");
            values.put("dataUuid", queryRequest.getDataUuid());
        }
        if (StringUtils.isNotBlank(queryRequest.getFlowDefinitionId())) {
            whereHql.append(" and o.id = :flowDefinitionId ");
            values.put("flowDefinitionId", queryRequest.getFlowDefinitionId());
        }
        if (StringUtils.isNotBlank(queryRequest.getTitle())) {
            whereHql.append(" and o.title like '%' || :title || '%' ");
            values.put("title", queryRequest.getTitle());
        }
        aclQueryInfo.setWhereHql(whereHql.toString());
        aclQueryInfo.getQueryParams().putAll(values);
        // 排序
        aclQueryInfo.addOrderby("createTime", "desc");
        return aclQueryInfo;
    }

}
