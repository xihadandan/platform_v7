/*
 * @(#)8/11/25 V1.0
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
 * 8/11/25.1	    zhulh		8/11/25		    Create
 * </pre>
 * @date 8/11/25
 */
@Component
public class FlowHandleOverdueOfAvgByUserDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String getQueryName() {
        return "流程统计分析_办理逾期分析_人员平均逾期时长";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("userId", "t.user_id", "用户ID", String.class);
        criteriaMetadata.add("userName", "t.user_name", "用户名称", String.class);
        criteriaMetadata.add("avgOverdueMinutes", "t.avg_overdue_minutes", "平均逾期时长", Double.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        String userRange = Objects.toString(context.getQueryParams().get("userRange"), StringUtils.EMPTY);
        if (StringUtils.isBlank(userRange)) {
            return Collections.emptyList();
        }

        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("flowHandleOverdueAvgOverdueTimeByUserGroupByUserQuery",
                queryParams, QueryItem.class, context.getPagingInfo());
        queryItems.forEach(item -> {
            item.put("avgOverdueMinutes", Math.round(item.getDouble("avgOverdueMinutes")));
        });
        return queryItems;
    }

    protected Map<String, Object> getQueryParams(QueryContext context) {
        Map<String, Object> queryParams = context.getQueryParams();
        String userId = Objects.toString(queryParams.get("userId"), StringUtils.EMPTY);
        if (StringUtils.isNotBlank(userId)) {
            queryParams.remove("userRange");
            queryParams.put("userIds", Lists.newArrayList(userId));
        }
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
        queryParams.put("userQuerySql", FlowReportUtils.getUserQuerySql(context.getNativeDao(), queryParams));
        return queryParams;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

}
