/*
 * @(#)7/9/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowEfficiencyReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.NamedQueryCriteria;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 7/9/25.1	    zhulh		7/9/25		    Create
 * </pre>
 * @date 7/9/25
 */
@Service
@Transactional(readOnly = true)
public class FlowEfficiencyReportServiceImpl implements FlowEfficiencyReportService {

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private NativeDao nativeDao;

    @Override
    public Double queryAverageCompletedTimeInMinute(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "flowAverageCompletedTimeQuery");
        Criterion criterion = FlowReportUtils.createFlowEfficiencyCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("whereSql", whereSql);
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowAverageCompletedTimeQuery", params, null);
        Double avgCompletedTime = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getDouble("avgCompletedTime") : null;
        return avgCompletedTime != null ? avgCompletedTime : 0.0;
    }

    @Override
    public List<QueryItem> listFlowAverageCompletedTimeInMinute(Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "listFlowAverageCompletedTimeQuery");
        Criterion criterion = FlowReportUtils.createFlowEfficiencyCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowAverageCompletedTimeQuery", params, pagingInfo);
        return queryItems;
    }

    @Override
    public Double queryAverageTaskDurationInMinute(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "taskAverageDurationQuery");
        Criterion criterion = FlowReportUtils.createFlowEfficiencyCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        queryParams.put("whereSql", whereSql);
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("taskAverageDurationQuery", queryParams, null);
        Double avgDuration = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getDouble("avgDuration") : null;
        return avgDuration != null ? avgDuration : 0.0;
    }

    @Override
    public List<QueryItem> listAverageTaskDurationInMinute(Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "listTaskAverageDurationQuery");
        Criterion criterion = FlowReportUtils.createFlowEfficiencyCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        queryParams.put("flowInstWhereSql", whereSql);
        return flowInstanceService.listQueryItemByNameSQLQuery("listTaskAverageDurationQuery", queryParams, pagingInfo);
    }

    @Override
    public long queryTaskCompletedCount(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "taskCompletedCountQuery");
        Criterion criterion = FlowReportUtils.createFlowEfficiencyCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        queryParams.put("whereSql", whereSql);
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("taskCompletedCountQuery", queryParams, null);
        Long count = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getLong("count") : null;
        return count != null ? count.longValue() : 0l;
    }

    @Override
    public long queryTaskAllCount(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "flowEfficiencyTaskAllCountQuery");
        Criterion criterion = FlowReportUtils.createFlowEfficiencyCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        queryParams.put("whereSql", whereSql);
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowEfficiencyTaskAllCountQuery", queryParams, null);
        Long count = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getLong("count") : null;
        return count != null ? count.longValue() : 0l;
    }

}
