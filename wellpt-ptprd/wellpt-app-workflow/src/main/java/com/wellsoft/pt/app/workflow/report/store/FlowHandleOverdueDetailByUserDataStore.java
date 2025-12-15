/*
 * @(#)8/11/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
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
public class FlowHandleOverdueDetailByUserDataStore extends FlowHandleOverdueOfAvgByUserDataStore {

    @Override
    public String getQueryName() {
        return "流程统计分析_办理逾期分析_人员逾期明细";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "UUID", String.class);
        criteriaMetadata.add("id", "t.id", "流程定义ID", String.class);
        criteriaMetadata.add("name", "t.name", "流程定义名称", String.class);
        criteriaMetadata.add("title", "t.title", "流程标题", String.class);
        criteriaMetadata.add("overdueMinutes", "t.overdue_minutes", "逾期时长", Double.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("flowHandleOverdueDetailByUserQuery",
                queryParams, QueryItem.class, context.getPagingInfo());
        queryItems.forEach(item -> {
            item.put("overdueMinutes", Math.round(item.getDouble("overdueMinutes")));
        });
        return queryItems;
    }

}
