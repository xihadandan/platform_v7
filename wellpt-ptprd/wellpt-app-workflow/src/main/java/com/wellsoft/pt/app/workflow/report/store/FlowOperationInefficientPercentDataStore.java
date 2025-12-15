/*
 * @(#)7/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.impl.FowOperationReportServiceImpl;
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
 * 7/21/25.1	    zhulh		7/21/25		    Create
 * </pre>
 * @date 7/21/25
 */
@Component
public class FlowOperationInefficientPercentDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String getQueryName() {
        return "流程统计分析_流程行为分析_流程低效操作率";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("id", "t.uuid", "流程定义ID", String.class);
        criteriaMetadata.add("name", "t.name", "流程定义名称", String.class);
        criteriaMetadata.add("totalCount", "t.total_count", "总数操作数量", Long.class);
        criteriaMetadata.add("inefficientCount", "t.inefficient_count", "低效操作数量", Long.class);
        criteriaMetadata.add("inefficientPercent", "t.inefficient_percent", "低效操作率", Double.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("listFlowInefficientPercentQuery", queryParams, QueryItem.class, context.getPagingInfo());
        queryItems.forEach(item -> {
            long totalCount = Math.round(item.getDouble("totalCount"));
            long inefficientCount = Math.round(item.getDouble("inefficientCount"));
            Double percent = item.getDouble("inefficientPercent");
            if (percent == null) {
                percent = inefficientCount * 100.0 / totalCount;
            }
            item.put("inefficientPercent", Double.valueOf(String.format("%.2f", percent)));
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
        Criterion criterion = FlowReportUtils.createFlowOperationCriterion(queryParams, workFlowSettings, false);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        queryParams.put("actionCodes", getInefficientActionCodes(queryParams));
        return queryParams;
    }

    public static List<Integer> getInefficientActionCodes(Map<String, Object> params) {
        return FowOperationReportServiceImpl.getInefficientActionCodes(params);
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

}
