/*
 * @(#)8/11/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowTodoReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.MapUtils;
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
 * 8/11/25.1	    zhulh		8/11/25		    Create
 * </pre>
 * @date 8/11/25
 */
@Component
public class FlowHandleOverdueOfAvgByDeptDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    protected FlowTodoReportService flowTodoReportService;

    @Override
    public String getQueryName() {
        return "流程统计分析_办理逾期分析_部门平均逾期时长";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("deptId", "t.dept_id", "部门ID", String.class);
        criteriaMetadata.add("deptName", "t.dept_name", "部门名称", String.class);
        criteriaMetadata.add("avgOverdueMinutes", "t.avg_overdue_minutes", "平均逾期时长", Double.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, String> deptMap = flowTodoReportService.listFlowTodoDeptIdAndName(context.getQueryParams());
        if (MapUtils.isEmpty(deptMap)) {
            return Lists.newArrayList();
        }

        List<String> deptIds = Lists.newArrayList(deptMap.keySet());
        context.getQueryParams().put("deptIds", deptIds);
        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("flowHandleOverdueAvgOverdueTimeByDeptGroupByDeptQuery",
                queryParams, QueryItem.class, context.getPagingInfo());
        queryItems.forEach(item -> {
            item.put("deptName", deptMap.get(item.getString("deptId")));
            item.put("avgOverdueMinutes", Math.round(item.getDouble("avgOverdueMinutes")));
        });
        return queryItems;
    }

    protected Map<String, Object> getQueryParams(QueryContext context) {
        Map<String, Object> queryParams = context.getQueryParams();
        String system = RequestSystemContextPathResolver.system();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(system)) {
            queryParams.put("system", system);
        }
        queryParams.put("whereSql", context.getWhereSqlString());
        queryParams.put("orderBy", context.getOrderString());

        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(queryParams, workFlowSettings);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        queryParams.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(context.getNativeDao(), queryParams));
        return queryParams;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

}
