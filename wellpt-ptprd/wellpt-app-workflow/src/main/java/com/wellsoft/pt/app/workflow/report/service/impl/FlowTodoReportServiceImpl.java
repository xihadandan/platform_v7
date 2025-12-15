/*
 * @(#)7/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowTodoReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.TableCriteria;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.criterion.MatchMode;
import com.wellsoft.pt.jpa.criterion.Restrictions;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/23/25.1	    zhulh		7/23/25		    Create
 * </pre>
 * @date 7/23/25
 */
@Service
@Transactional(readOnly = true)
public class FlowTodoReportServiceImpl implements FlowTodoReportService {

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    private NativeDao nativeDao;

    @Override
    public long getFlowTodoCountByUser(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowTodoCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("userQuerySql", FlowReportUtils.getUserQuerySql(this.nativeDao, params));
        return nativeDao.countByNamedQuery("listFlowTodoByUserQuery", params);
    }

    @Override
    public QueryItem getMaxFlowTodoCountByUser(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowTodoCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("userQuerySql", FlowReportUtils.getUserQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowTodoCountByUserGroupByFlowQuery", params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0) : null;
    }

    @Override
    public List<QueryItem> listTodoCountByUserGroupByUser(Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowTodoCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("userQuerySql", FlowReportUtils.getUserQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowTodoCountByUserGroupByUserQuery", params, pagingInfo);
        return queryItems;
    }

    @Override
    public List<String> listFlowTodoDeptId(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowTodoCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("system", RequestSystemContextPathResolver.system());

        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowTodoDeptIdByDeptQuery", params, null);
        return queryItems.stream().map(item -> item.getString("deptId").toString()).distinct().collect(Collectors.toList());
    }

    @Override
    public Map<String, String> listFlowTodoDeptIdAndName(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowTodoCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("system", RequestSystemContextPathResolver.system());
        String keyword = Objects.toString(params.get("keyword"), StringUtils.EMPTY);
        if (StringUtils.isNotBlank(keyword)) {
            Criterion nameLikeCriterion = Restrictions.like("d.name", keyword, MatchMode.ANYWHERE);
            whereSql = nameLikeCriterion.toSqlString(criteria);
            whereSql = StringUtils.replace(whereSql, "this_.", "");
            params.put("whereSql", whereSql);
            params.putAll(criteria.getQueryParams());
        }

        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowTodoDeptIdByDeptQuery", params, null);
        Map<String, String> deptMap = Maps.newHashMap();
        queryItems.forEach(item -> deptMap.put(item.getString("deptId"), item.getString("deptName")));
        return deptMap;
    }

    @Override
    public long getFlowTodoCountByDept(List<String> deptIds, Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowTodoCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);

//        List<Long> counts = Lists.newArrayList();
//        ListUtils.handleSubList(deptIds, 1000, subDeptIds -> {
        params.put("deptIds", deptIds);
        params.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowTodoCountByDeptGroupByDeptQuery", params, null);
        long todoCount = queryItems.stream().mapToLong(item -> item.getLong("count")).sum();
//            counts.add(todoCount);
//        });
//        return counts.stream().mapToLong(count -> count).sum();
        return todoCount;
    }

    @Override
    public QueryItem getMaxFlowTodoCountByDept(List<String> deptIds, Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowTodoCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("whereSql", "t.count > 0");
        params.put("orderBy", "order by t.count desc");
        params.put("deptIds", deptIds);
        params.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(this.nativeDao, params));
        params.put("orderBy", "order by t.count desc");
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowTodoCountByDeptGroupByFlowQuery", params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0) : null;
//        List<QueryItem> maxCountItems = Lists.newArrayList();
//        ListUtils.handleSubList(deptIds, 1000, subDeptIds -> {
//            params.put("deptIds", subDeptIds);
//            List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowTodoCountByDeptGroupByFlowQuery", params, null);
//            if (CollectionUtils.isEmpty(maxCountItems)) {
//                maxCountItems.addAll(queryItems);
//            } else {
//                queryItems.forEach(item -> {
//                    QueryItem maxItem = maxCountItems.stream().filter(max -> StringUtils.equals(max.getString("id"), item.getString("id"))).findFirst().orElse(null);
//                    if (maxItem != null) {
//                        maxItem.put("count", maxItem.getLong("count") + item.getLong("count"));
//                    } else {
//                        maxCountItems.add(item);
//                    }
//                });
//            }
//        });
//        if (CollectionUtils.isNotEmpty(maxCountItems)) {
//            Collections.sort(maxCountItems, (item1, item2) -> item2.getLong("count").compareTo(item1.getLong("count")));
//            return maxCountItems.get(0);
//        } else {
//            return null;
//        }
    }

    @Override
    public List<QueryItem> listTodoCountByDeptGroupByDept(List<String> deptIds, Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowTodoCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("orderBy", "order by t.count desc");

//        List<QueryItem> queryItems = Lists.newArrayList();
//        ListUtils.handleSubList(deptIds, 1000, subDeptIds -> {
        params.put("deptIds", deptIds);
        params.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(this.nativeDao, params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowTodoCountByDeptGroupByDeptQuery", params, null);
//        queryItems.addAll(items);
//        });
        Collections.sort(queryItems, (item1, item2) -> item2.getLong("count").compareTo(item1.getLong("count")));
        return queryItems;
    }

    private QueryItem getMaxCountItem(QueryItem item1, QueryItem item2) {
        return item1.getLong("count") > item2.getLong("count") ? item1 : item2;
    }

}
