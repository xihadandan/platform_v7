/*
 * @(#)7/24/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.dataset.FlowTodoCountByUserEchartDatasetLoad;
import com.wellsoft.pt.app.workflow.report.service.FlowTodoReportService;
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
 * 7/24/25.1	    zhulh		7/24/25		    Create
 * </pre>
 * @date 7/24/25
 */
@Component
public class FlowTodoCountByUserDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private FlowTodoReportService flowTodoReportService;

    @Override
    public String getQueryName() {
        return "流程统计分析_待办流程分析按人员_待办数量排名";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("userId", "t.user_id", "用户ID", String.class);
        criteriaMetadata.add("userName", "t.user_name", "用户名称", String.class);
        criteriaMetadata.add("todoCount", "t.todo_count", "待办数量", Integer.class);
        criteriaMetadata.add("normalCount", "t.normal_count", "正常数量", Integer.class);
        criteriaMetadata.add("overdueCount", "t.overdue_count", "逾期数量", Integer.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        String userRange = Objects.toString(context.getQueryParams().get("userRange"), StringUtils.EMPTY);
        if (org.apache.commons.lang.StringUtils.isBlank(userRange)) {
            return Collections.emptyList();
        }

        Map<String, Object> queryParams = getQueryParams(context);
        queryParams.put("isOverdue", 0);
        List<QueryItem> normalQueryItems = flowTodoReportService.listTodoCountByUserGroupByUser(Maps.newHashMap(queryParams), null);
        queryParams.put("isOverdue", 1);
        List<QueryItem> overdueQueryItems = flowTodoReportService.listTodoCountByUserGroupByUser(Maps.newHashMap(queryParams), null);
        List<QueryItem> queryItems = FlowTodoCountByUserEchartDatasetLoad.mergeQueryItems(normalQueryItems, overdueQueryItems);
        for (QueryItem queryItem : queryItems) {
            long count = queryItem.getLong("count");
            long overdueCount = queryItem.getLong("overdueCount");
            queryItem.put("normalCount", count);
            queryItem.put("todoCount", count + overdueCount);
        }
        context.getPagingInfo().setTotalCount(queryItems.size());
        return queryItems;
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
        Criterion criterion = FlowReportUtils.createFlowTodoCriterion(queryParams, workFlowSettings);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        return queryParams;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }
}
