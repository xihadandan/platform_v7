/*
 * @(#)7/1/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/1/25.1	    zhulh		7/1/25		    Create
 * </pre>
 * @date 7/1/25
 */
@Component
public class FlowInstanceCountByDefinitionDataStore extends AbstractDataStoreQueryInterface {
    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String getQueryName() {
        return "流程统计分析_流程实例发起数量按流程定义统计";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "流程定义UUID", String.class);
        criteriaMetadata.add("name", "t.name", "流程定义名称", String.class);
        criteriaMetadata.add("category_uuid", "t.category", "流程分类UUID", String.class);
        criteriaMetadata.add("totalCount", "total_count", "发起量", Long.class);
        criteriaMetadata.add("completedCount", "t1.count", "已结束", Long.class);
        criteriaMetadata.add("uncompletedCount", "t2.count", "流转中", Long.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        return context.getNativeDao().namedQuery("flowInstanceCountByDefinitionQuery", queryParams, QueryItem.class, context.getPagingInfo());
    }

    private Map<String, Object> getQueryParams(QueryContext context) {
        Map<String, Object> queryParams = context.getQueryParams();
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            queryParams.put("system", system);
        }
        queryParams.put("whereSql", context.getWhereSqlString());
        queryParams.put("orderBy", context.getOrderString());

        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        Criterion criterion = FlowReportUtils.createFlowInstanceCountCriterion(queryParams, workFlowSettings);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        return queryParams;
    }


    @Override
    public long count(QueryContext context) {
        long totalCount = context.getPagingInfo().getTotalCount();
        return totalCount > 0 ? totalCount : totalCount(context);
    }

    private long totalCount(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        return context.getNativeDao().countByNamedQuery("flowInstanceCountByDefinitionQuery", queryParams);
    }

}
