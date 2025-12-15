/*
 * @(#)7/7/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/7/25.1	    zhulh		7/7/25		    Create
 * </pre>
 * @date 7/7/25
 */
@Component
public class FlowHandleCountDetailDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String getQueryName() {
        return "流程统计分析_流程办理情况详情统计表";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "流程定义UUID", String.class);
        criteriaMetadata.add("name", "t.name", "流程定义名称", String.class);
        criteriaMetadata.add("category_uuid", "t.category_uuid", "流程分类UUID", String.class);
        criteriaMetadata.add("category_name", "t.category_name", "流程分类名称", String.class);
        criteriaMetadata.add("count", "t.count", "数量", Long.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        String queryName = Objects.toString(context.getQueryParams().get("queryName"), StringUtils.EMPTY);
        if (StringUtils.isBlank(queryName)) {
            return Collections.emptyList();
        }

        String orgId = Objects.toString(context.getQueryParams().get("orgId"), StringUtils.EMPTY);
        if (StringUtils.isBlank(orgId)) {
            return Collections.emptyList();
        }

        return BooleanUtils.isTrue((Boolean) context.getQueryParams().get("byDept")) ? queryByDept(orgId, queryName, context) : queryByUser(orgId, queryName, context);
    }

    private List<QueryItem> queryByDept(String deptId, String queryName, QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        queryParams.put("deptIds", Lists.newArrayList(deptId));
        queryParams.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(context.getNativeDao(), queryParams));
        return context.getNativeDao().namedQuery(queryName, queryParams, QueryItem.class, context.getPagingInfo());
    }

    private List<QueryItem> queryByUser(String userId, String queryName, QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        queryParams.put("userIds", Lists.newArrayList(userId));
        return context.getNativeDao().namedQuery(queryName, queryParams, QueryItem.class, context.getPagingInfo());
    }

    private Map<String, Object> getQueryParams(QueryContext context) {
        Map<String, Object> queryParams = context.getQueryParams();
        String system = RequestSystemContextPathResolver.system();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(system)) {
            queryParams.put("system", system);
        }
        queryParams.put("whereSql", context.getWhereSqlString());
        queryParams.put("orderBy", context.getOrderString());

        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        Criterion criterion = FlowReportUtils.createFlowHandleCountCriterion(queryParams, workFlowSettings);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        return queryParams;
    }


    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

}
