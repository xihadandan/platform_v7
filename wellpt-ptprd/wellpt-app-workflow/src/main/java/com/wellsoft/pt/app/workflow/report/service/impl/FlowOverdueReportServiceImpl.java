/*
 * @(#)7/14/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowOverdueReportService;
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
 * 7/14/25.1	    zhulh		7/14/25		    Create
 * </pre>
 * @date 7/14/25
 */
@Service
@Transactional(readOnly = true)
public class FlowOverdueReportServiceImpl implements FlowOverdueReportService {

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private NativeDao nativeDao;

    @Override
    public long queryOverdueCount(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "listFlowOverdueTimeQuery");
        Criterion criterion = FlowReportUtils.createFlowOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        return nativeDao.countByNamedQuery("listFlowOverdueTimeQuery", params);
    }

    @Override
    public List<QueryItem> listOverdue(Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "listFlowOverdueTimeQuery");
        Criterion criterion = FlowReportUtils.createFlowOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        return flowInstanceService.listQueryItemByNameSQLQuery("listFlowOverdueTimeQuery", params, pagingInfo);
    }

    @Override
    public QueryItem getMaxOverdue(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "listFlowOverdueTimeQuery");
        Criterion criterion = FlowReportUtils.createFlowOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("orderBy", "order by t.overdue_minutes desc");
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowOverdueTimeQuery", params, new PagingInfo(1, 1));
        QueryItem queryItem = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0) : null;
        return queryItem;
    }

    @Override
    public Double getMaxOverdueTime(Map<String, Object> params) {
        QueryItem queryItem = getMaxOverdue(params);
        if (queryItem != null) {
            Double overdueMinutes = queryItem.getDouble("overdueMinutes");
            return overdueMinutes != null ? overdueMinutes : 0d;
        }
        return 0d;
    }

    @Override
    public Double getAvgOverdueTime(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "flowAvgOverdueTimeQuery");
        Criterion criterion = FlowReportUtils.createFlowOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowAvgOverdueTimeQuery", params, new PagingInfo(1, 1));
        QueryItem queryItem = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0) : null;
        if (queryItem != null) {
            Double avgOverdueMinutes = queryItem.getDouble("avgOverdueMinutes");
            return avgOverdueMinutes != null ? avgOverdueMinutes : 0d;
        }
        return 0d;
    }

    @Override
    public List<QueryItem> listOverdueCount(Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "listFlowOverdueCountQuery");
        Criterion criterion = FlowReportUtils.createFlowOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        return flowInstanceService.listQueryItemByNameSQLQuery("listFlowOverdueCountQuery", params, pagingInfo);
    }

    @Override
    public List<QueryItem> listAvgOverdueTime(Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "listFlowAvgOverdueTimeQuery");
        Criterion criterion = FlowReportUtils.createFlowOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        return flowInstanceService.listQueryItemByNameSQLQuery("listFlowAvgOverdueTimeQuery", params, pagingInfo);
    }

    @Override
    public QueryItem getMaxOverdueCountByDefinition(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "listFlowOverdueCountQuery");
        Criterion criterion = FlowReportUtils.createFlowOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("orderBy", "order by t.count desc");
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowOverdueCountQuery", params, new PagingInfo(1, 1));
        QueryItem queryItem = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0) : null;
        return queryItem;
    }

}
