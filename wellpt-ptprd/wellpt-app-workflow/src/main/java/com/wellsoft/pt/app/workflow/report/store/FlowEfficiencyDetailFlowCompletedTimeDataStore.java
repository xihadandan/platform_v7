/*
 * @(#)7/10/25 V1.0
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

import java.util.Date;
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
 * 7/10/25.1	    zhulh		7/10/25		    Create
 * </pre>
 * @date 7/10/25
 */
@Component
public class FlowEfficiencyDetailFlowCompletedTimeDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String getQueryName() {
        return "流程统计分析_流程效率分析_流程用时详细统计表";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "流程实例UUID", String.class);
        criteriaMetadata.add("title", "t.title", "流程标题", String.class);
        criteriaMetadata.add("id", "t.id", "流程定义ID", String.class);
        criteriaMetadata.add("name", "t.name", "流程定义名称", String.class);
        criteriaMetadata.add("startUserId", "t.start_user_id", "发起人ID", String.class);
        criteriaMetadata.add("startUserName", "t.start_user_name", "发起人名称", String.class);
        criteriaMetadata.add("startTime", "t.start_time", "发起时间", Date.class);
        criteriaMetadata.add("endTime", "t.end_time", "结束时间", Date.class);
        criteriaMetadata.add("taskCount", "t.task_count", "流转环节数", Integer.class);
        criteriaMetadata.add("duration", "t.duration", "流程用时", Double.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("listFlowCompletedTimeDetailQuery", queryParams, QueryItem.class, context.getPagingInfo());
        queryItems.forEach(item -> {
            Integer taskCount = item.getInt("taskCount");
            Double duration = item.getDouble("duration");
            if (taskCount == null) {
                item.put("taskCount", 0);
            }
            if (duration != null) {
                item.put("duration", Math.round(duration));
            } else {
                item.put("duration", 0d);
            }
        });
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
        Criterion criterion = FlowReportUtils.createFlowEfficiencyCriterion(queryParams, workFlowSettings);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        return queryParams;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

}
