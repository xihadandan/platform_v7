/*
 * @(#)8/11/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang.StringUtils;
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
 * 8/11/25.1	    zhulh		8/11/25		    Create
 * </pre>
 * @date 8/11/25
 */
@Component
public class FlowHandleOverdueOfCountByUserDataStore extends FlowHandleOverdueOfAvgByUserDataStore {

    @Override
    public String getQueryName() {
        return "流程统计分析_办理逾期分析_人员逾期次数";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("userId", "t.user_id", "用户ID", String.class);
        criteriaMetadata.add("userName", "t.user_name", "用户名称", String.class);
        criteriaMetadata.add("overdueCount", "t.overdue_count", "逾期次数", Long.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        String userRange = Objects.toString(context.getQueryParams().get("userRange"), StringUtils.EMPTY);
        if (StringUtils.isBlank(userRange)) {
            return Collections.emptyList();
        }

        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("flowHandleOverdueCountByUserGroupByUserQuery",
                queryParams, QueryItem.class, context.getPagingInfo());
        return queryItems;
    }

}
