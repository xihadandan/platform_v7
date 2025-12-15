/*
 * @(#)8/11/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.collections.MapUtils;
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
public class FlowHandleOverdueOfCountByDeptDataStore extends FlowHandleOverdueOfAvgByDeptDataStore {

    @Override
    public String getQueryName() {
        return "流程统计分析_办理逾期分析_部门逾期次数";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("deptId", "t.dept_id", "部门ID", String.class);
        criteriaMetadata.add("deptName", "t.dept_name", "部门名称", String.class);
        criteriaMetadata.add("overdueCount", "t.overdue_count", "逾期次数", Long.class);
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
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("flowHandleOverdueCountByDeptGroupByDeptQuery",
                queryParams, QueryItem.class, context.getPagingInfo());
        queryItems.forEach(item -> {
            item.put("deptName", deptMap.get(item.getString("deptId")));
        });
        return queryItems;
    }

}
