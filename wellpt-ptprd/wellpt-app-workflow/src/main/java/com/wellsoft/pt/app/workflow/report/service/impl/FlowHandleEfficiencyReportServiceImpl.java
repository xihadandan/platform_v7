/*
 * @(#)8/5/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowHandleEfficiencyReportService;
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
 * 8/5/25.1	    zhulh		8/5/25		    Create
 * </pre>
 * @date 8/5/25
 */
@Service
@Transactional(readOnly = true)
public class FlowHandleEfficiencyReportServiceImpl implements FlowHandleEfficiencyReportService {

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private NativeDao nativeDao;

    @Override
    public double queryAverageHandleTimeInMinuteByUser(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleEfficiencyCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleEfficiencyAverageHandleTimeByUserQuery", params, null);
        Double avgHandleTime = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getDouble("avgHandleTime") : null;
        return avgHandleTime != null ? avgHandleTime : 0.0;
    }

    @Override
    public double queryMaxHandleTimeInMinuteByUser(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleEfficiencyCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("orderBy", "order by t.handle_time desc");
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleEfficiencyListHandleTimeByUserQuery", params, new PagingInfo(1, 1));
        Double handleTime = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getDouble("handleTime") : null;
        return handleTime != null ? handleTime : 0.0;
    }

    @Override
    public List<QueryItem> queryAverageHandleTimeInMinuteByUserGroupByUser(Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleEfficiencyCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleEfficiencyAverageHandleTimeGroupByUserQuery", params, pagingInfo);
        return queryItems;
    }

    @Override
    public double queryAverageHandleTimeInMinuteByDept(List<String> deptIds, Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleEfficiencyCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("deptIds", deptIds);
        params.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleEfficiencyAverageHandleTimeByDeptQuery", params, null);
        Double avgHandleTime = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getDouble("avgHandleTime") : null;
        return avgHandleTime != null ? avgHandleTime : 0.0;
    }

    @Override
    public List<QueryItem> queryAverageHandleTimeInMinuteByDeptGroupByDept(List<String> deptIds, Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowHandleEfficiencyCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("deptIds", deptIds);
        params.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("flowHandleEfficiencyAverageHandleTimeGroupByDeptQuery", params, pagingInfo);
        return queryItems;
    }

}
