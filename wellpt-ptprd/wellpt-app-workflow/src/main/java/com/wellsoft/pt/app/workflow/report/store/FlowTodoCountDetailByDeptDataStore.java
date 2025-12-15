/*
 * @(#)7/24/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowTodoReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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
 * 7/24/25.1	    zhulh		7/24/25		    Create
 * </pre>
 * @date 7/24/25
 */
@Component
public class FlowTodoCountDetailByDeptDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private FlowTodoReportService flowTodoReportService;

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Override
    public String getQueryName() {
        return "流程统计分析_待办流程分析_流程办理情况统计表_按部门统计";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "唯一标识", String.class);
        criteriaMetadata.add("deptId", "t.dept_id", "部门ID", String.class);
        criteriaMetadata.add("deptName", "t.dept_name", "部门名称", String.class);
        criteriaMetadata.add("id", "t.id", "流程定义ID", String.class);
        criteriaMetadata.add("name", "t.name", "流程定义名称", String.class);
        criteriaMetadata.add("categoryUuid", "t.category_uuid", "流程分类UUID", String.class);
        criteriaMetadata.add("categoryName", "t.category_name", "流程分类名称", String.class);
        criteriaMetadata.add("todoCount", "t.todo_count", "待办数量", Integer.class);
        criteriaMetadata.add("overdueCount", "t.overdue_count", "逾期数量", Integer.class);
        criteriaMetadata.add("percent", "t.percent", "占比", Double.class);
        criteriaMetadata.add("children", "children", "子节点", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, String> deptMap = flowTodoReportService.listFlowTodoDeptIdAndName(context.getQueryParams());
        if (MapUtils.isEmpty(deptMap)) {
            return Lists.newArrayList();
        }

        List<String> deptIds = Lists.newArrayList(deptMap.keySet());

        Map<String, Object> queryParams = getQueryParams(context);
        queryParams.put("isOverdue", 0);
        List<QueryItem> normalQueryItems = listItems(deptIds, queryParams, context);// context.getNativeDao().namedQuery("listFlowTodoCountByDeptGroupByDeptAndFlowQuery", queryParams, QueryItem.class, context.getPagingInfo());
        queryParams.put("isOverdue", 1);
        List<QueryItem> overdueQueryItems = listItems(deptIds, queryParams, context);//context.getNativeDao().namedQuery("listFlowTodoCountByDeptGroupByDeptAndFlowQuery", queryParams, QueryItem.class, context.getPagingInfo());
        List<QueryItem> queryItems = mergeQueryItems(normalQueryItems, overdueQueryItems);
        // 填充部门名称
        queryItems.forEach(item -> {
            item.put("dept_name", deptMap.get(item.getString("deptId")));
        });
        // 补充没有数据的部门
        Map<String, QueryItem> queryItemMap = queryItems.stream().collect(Collectors.toMap(item -> item.getString("deptId"), Function.identity()));
        deptMap.forEach((deptId, deptName) -> {
            if (!queryItemMap.containsKey(deptId)) {
                QueryItem queryItem = new QueryItem();
                queryItem.put("uuid", deptId);
                queryItem.put("dept_id", deptId);
                queryItem.put("dept_name", deptName);
                queryItem.put("todo_count", 0);
                queryItem.put("overdue_count", 0);
                queryItem.put("percent", 0.00);
                queryItem.put("children", Collections.emptyList());
                queryItems.add(queryItem);
            }
        });
        return queryItems;
    }

    private List<QueryItem> listItems(List<String> deptIds, Map<String, Object> queryParams, QueryContext context) {
//        List<QueryItem> queryItems = Lists.newArrayList();
//        ListUtils.handleSubList(deptIds, 1000, subDeptIds -> {
        queryParams.put("deptIds", deptIds);
        queryParams.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(context.getNativeDao(), queryParams));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowTodoCountByDeptGroupByDeptAndFlowQuery", queryParams, null);
//            queryItems.addAll(items);
//        });
        Collections.sort(queryItems, (item1, item2) -> item2.getLong("count").compareTo(item1.getLong("count")));
        return queryItems;
    }

    private List<QueryItem> mergeQueryItems(List<QueryItem> normalQueryItems, List<QueryItem> overdueQueryItems) {
        normalQueryItems.forEach(item -> {
            item.put("overdue_count", 0L);
        });
        overdueQueryItems.forEach(item -> {
            item.put("overdue_count", item.get("count"));
            item.put("count", 0L);
        });
        List<QueryItem> queryItems = Lists.newArrayList();
        queryItems.addAll(normalQueryItems);
        queryItems.addAll(overdueQueryItems);
        Map<String, String> deptNameMap = Maps.newHashMap();
        Map<String, List<QueryItem>> deptItemsMap = Maps.newHashMap();
        queryItems.forEach(item -> {
            String deptId = Objects.toString(item.get("deptId"), StringUtils.EMPTY);
            if (!deptNameMap.containsKey(deptId)) {
                deptNameMap.put(deptId, Objects.toString(item.get("deptName"), StringUtils.EMPTY));
            }
            if (!deptItemsMap.containsKey(deptId)) {
                deptItemsMap.put(deptId, Lists.newArrayList());
            }
            deptItemsMap.get(deptId).add(item);
        });
        long normalCount = normalQueryItems.stream().mapToLong(item -> item.getLong("count")).sum();
        long overdueCount = overdueQueryItems.stream().mapToLong(item -> item.getLong("overdueCount")).sum();
        queryItems = createDeptItems(deptNameMap, deptItemsMap, normalCount + overdueCount);
        return queryItems;
    }

    private List<QueryItem> createDeptItems(Map<String, String> deptNameMap, Map<String, List<QueryItem>> deptItemsMap, long totalCount) {
        List<QueryItem> queryItems = Lists.newArrayList();
        deptNameMap.forEach((deptId, deptName) -> {
            List<QueryItem> items = deptItemsMap.get(deptId);
            QueryItem queryItem = new QueryItem();
            queryItem.put("uuid", deptId);
            queryItem.put("dept_id", deptId);
            queryItem.put("dept_name", deptName);
            Long normalCount = items.stream().mapToLong(item -> item.getLong("count")).sum();
            Long overdueCount = items.stream().mapToLong(item -> item.getLong("overdueCount")).sum();
            Long todoCount = normalCount + overdueCount;
            queryItem.put("todo_count", todoCount);
            queryItem.put("overdue_count", overdueCount);
            if (totalCount > 0) {
                queryItem.put("percent", String.format("%.2f", todoCount * 100.0 / totalCount));
            } else {
                queryItem.put("percent", "0.00");
            }
            queryItem.put("children", createDeptChildrenByFlowCategory(deptId, items, todoCount));
            queryItems.add(queryItem);
        });
        queryItems.sort((o1, o2) -> {
            Long count1 = o1.getLong("todoCount");
            Long count2 = o2.getLong("todoCount");
            return -count1.compareTo(count2);
        });
        return queryItems;
    }

    private List<QueryItem> createDeptChildrenByFlowCategory(String deptId, List<QueryItem> deptItems, Long todoCount) {
        List<QueryItem> categoryItems = Lists.newArrayList();
        Map<String, List<QueryItem>> categoryItemsMap = deptItems.stream().collect(Collectors.groupingBy(item -> item.getString("categoryUuid")));
        categoryItemsMap.forEach((categoryUuid, items) -> {
            QueryItem categoryItem = new QueryItem();
            categoryItem.put("uuid", deptId + "_" + categoryUuid);
            categoryItem.put("dept_id", deptId);
            String categoryName = items.get(0).getString("categoryName");
            categoryItem.put("dept_name", categoryName);
            categoryItem.put("category_uuid", categoryUuid);
            categoryItem.put("category_name", categoryName);
            Long normalCount = items.stream().mapToLong(item -> item.getLong("count")).sum();
            Long overdueCount = items.stream().mapToLong(item -> item.getLong("overdueCount")).sum();
            Long categoryTodoCount = normalCount + overdueCount;
            categoryItem.put("todo_count", categoryTodoCount);
            categoryItem.put("overdue_count", overdueCount);
            if (todoCount > 0) {
                categoryItem.put("percent", String.format("%.2f", categoryTodoCount * 100.0 / todoCount));
            } else {
                categoryItem.put("percent", "0.00");
            }
            categoryItem.put("children", createDeptChildrenByFlow(deptId, categoryUuid, items, todoCount));
            categoryItems.add(categoryItem);
        });
        return categoryItems;
    }

    private List<QueryItem> createDeptChildrenByFlow(String deptId, String categoryUuid, List<QueryItem> items, Long todoCount) {
        List<QueryItem> flowItems = Lists.newArrayList();
        Map<String, List<QueryItem>> flowItemsMap = items.stream().collect(Collectors.groupingBy(item -> item.getString("id")));
        flowItemsMap.forEach((id, item) -> {
            QueryItem flowItem = new QueryItem();
            flowItem.put("uuid", deptId + "_" + categoryUuid + "_" + id);
            flowItem.put("dept_id", deptId);
            String name = item.get(0).getString("name");
            flowItem.put("dept_name", name);
            flowItem.put("category_uuid", categoryUuid);
            flowItem.put("id", id);
            Long normalCount = item.stream().mapToLong(i -> i.getLong("count")).sum();
            Long overdueCount = item.stream().mapToLong(i -> i.getLong("overdueCount")).sum();
            Long flowTodoCount = normalCount + overdueCount;
            flowItem.put("todo_count", flowTodoCount);
            flowItem.put("overdue_count", overdueCount);
//            if (todoCount > 0) {
//                flowItem.put("percent", String.format("%.2f", flowTodoCount * 100.0 / todoCount));
//            } else {
//                flowItem.put("percent", "0.00");
//            }
            flowItems.add(flowItem);
        });
        return flowItems;
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
        Criterion criterion = FlowReportUtils.createFlowTodoCriterion(queryParams, workFlowSettings);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        return queryParams;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }
}
