/*
 * @(#)8/8/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowHandleOverdueReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.TableCriteria;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
 * 8/8/25.1	    zhulh		8/8/25		    Create
 * </pre>
 * @date 8/8/25
 */
@Service
@Transactional(readOnly = true)
public class FlowHandleOverdueReportServiceImpl implements FlowHandleOverdueReportService {

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private NativeDao nativeDao;

    @Override
    public long queryOverdueCountByUser(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("userQuerySql", FlowReportUtils.getUserQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("getFlowHandleOverdueCountByUserQuery", params, null);
        Long count = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getLong("count") : null;
        return count != null ? count : 0;
    }

    @Override
    public double queryAvgOverdueCountByUser(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("userQuerySql", FlowReportUtils.getUserQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("getFlowHandleAvgOverdueCountByUserQuery", params, null);
        Double avgOverdueCount = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getDouble("avgOverdueCount") : null;
        return avgOverdueCount != null ? avgOverdueCount : 0;
    }

    @Override
    public double queryAvgOverdueTimeByUser(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("userQuerySql", FlowReportUtils.getUserQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleOverdueAvgOverdueTimeByUserQuery", params, new PagingInfo(1, 1));
        QueryItem queryItem = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0) : null;
        if (queryItem == null) {
            return 0d;
        }
        Double avgOverdueMinutes = queryItem.getDouble("avgOverdueMinutes");
        return avgOverdueMinutes != null ? avgOverdueMinutes : 0d;
    }

    @Override
    public QueryItem getAvgMaxOverdueTimeByUser(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("orderBy", "order by t.avg_overdue_minutes desc");
        params.put("userQuerySql", FlowReportUtils.getUserQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleOverdueAvgMaxOverdueTimeByUserQuery", params, new PagingInfo(1, 1));
        QueryItem queryItem = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0) : null;
        return queryItem;
    }

    @Override
    public List<QueryItem> queryAvgOverdueTimeByUserGroupByUser(Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("userQuerySql", FlowReportUtils.getUserQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleOverdueAvgOverdueTimeByUserGroupByUserQuery", params, pagingInfo);
        return queryItems;
    }

    @Override
    public List<QueryItem> queryOverdueCountByUserGroupByUser(Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("userQuerySql", FlowReportUtils.getUserQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleOverdueCountByUserGroupByUserQuery", params, pagingInfo);
        return queryItems;
    }

    @Override
    public long queryOverdueCountByDept(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("getFlowHandleOverdueCountByDeptQuery", params, null);
        Long count = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getLong("count") : null;
        return count != null ? count : 0;
    }

    @Override
    public double queryAvgOverdueTimeByDept(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleOverdueAvgOverdueTimeByDeptQuery", params, new PagingInfo(1, 1));
        QueryItem queryItem = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0) : null;
        if (queryItem == null) {
            return 0d;
        }
        Double avgOverdueMinutes = queryItem.getDouble("avgOverdueMinutes");
        return avgOverdueMinutes != null ? avgOverdueMinutes : 0d;
    }

    @Override
    public double queryAvgOverdueCountByDept(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("getFlowHandleAvgOverdueCountByDeptQuery", params, null);
        Double avgOverdueCount = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getDouble("avgOverdueCount") : null;
        return avgOverdueCount != null ? avgOverdueCount : 0;
    }

    @Override
    public QueryItem getAvgMaxOverdueTimeByDept(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(this.nativeDao, params));
        params.put("orderBy", "order by t.avg_overdue_minutes desc");
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleOverdueAvgMaxOverdueTimeByDeptQuery", params, new PagingInfo(1, 1));
        QueryItem queryItem = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0) : null;
        return queryItem;
    }

    @Override
    public List<QueryItem> queryAvgOverdueTimeByDeptGroupByDept(List<String> deptIds, Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("deptIds", deptIds);
        params.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleOverdueAvgOverdueTimeByDeptGroupByDeptQuery", params, pagingInfo);
        return queryItems;
    }

    @Override
    public List<QueryItem> queryOverdueCountByDeptGroupByDept(List<String> deptIds, Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("deptIds", deptIds);
        params.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleOverdueCountByDeptGroupByDeptQuery", params, pagingInfo);
        return queryItems;
    }

}
