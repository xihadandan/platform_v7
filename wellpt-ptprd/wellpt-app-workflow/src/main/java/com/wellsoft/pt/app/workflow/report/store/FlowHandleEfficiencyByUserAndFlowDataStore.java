/*
 * @(#)8/6/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.SnowFlake;
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
 * 8/6/25.1	    zhulh		8/6/25		    Create
 * </pre>
 * @date 8/6/25
 */
@Component
public class FlowHandleEfficiencyByUserAndFlowDataStore extends FlowHandleEfficiencyByUserDataStore {

    @Override
    public String getQueryName() {
        return "流程统计分析_办理效率分析按人员及流程统计";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = super.initCriteriaMetadata(context);
        criteriaMetadata.add("uuid", "t.uuid", "UUID", String.class);
        criteriaMetadata.add("id", "t.id", "流程定义ID", String.class);
        criteriaMetadata.add("name", "t.name", "流程定义名称", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        String userRange = Objects.toString(context.getQueryParams().get("userRange"), StringUtils.EMPTY);
        if (StringUtils.isBlank(userRange)) {
            return Collections.emptyList();
        }

        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("flowHandleEfficiencyAverageHandleTimeGroupByUserAndFlowQuery",
                queryParams, QueryItem.class, context.getPagingInfo());
        queryItems.forEach(item -> {
            item.put("uuid", SnowFlake.getId() + StringUtils.EMPTY);
            item.put("avgHandleTime", Math.round(item.getDouble("avgHandleTime")));
        });
        return queryItems;
    }

}
