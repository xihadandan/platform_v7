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
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * 7/24/25.1	    zhulh		7/24/25		    Create
 * </pre>
 * @date 7/24/25
 */
@Component
public class FlowTodoCountDetailByUserDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private FlowTodoReportService flowTodoReportService;

    @Override
    public String getQueryName() {
        return "流程统计分析_待办流程分析_流程办理情况统计表_按人员统计";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "唯一标识", String.class);
        criteriaMetadata.add("userId", "t.user_id", "用户ID", String.class);
        criteriaMetadata.add("userName", "t.user_name", "用户名称", String.class);
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
        String userRange = Objects.toString(context.getQueryParams().get("userRange"), StringUtils.EMPTY);
        if (org.apache.commons.lang.StringUtils.isBlank(userRange)) {
            return Collections.emptyList();
        }

        Map<String, Object> queryParams = getQueryParams(context);
        queryParams.put("isOverdue", 0);
        List<QueryItem> normalQueryItems = context.getNativeDao().namedQuery("listFlowTodoCountByUserGroupByUserAndFlowQuery", queryParams, QueryItem.class, context.getPagingInfo());
        queryParams.put("isOverdue", 1);
        List<QueryItem> overdueQueryItems = context.getNativeDao().namedQuery("listFlowTodoCountByUserGroupByUserAndFlowQuery", queryParams, QueryItem.class, context.getPagingInfo());
        List<QueryItem> queryItems = mergeQueryItems(normalQueryItems, overdueQueryItems);
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
        Map<String, String> userNameMap = Maps.newHashMap();
        Map<String, List<QueryItem>> userItemsMap = Maps.newHashMap();
        queryItems.forEach(item -> {
            String userId = Objects.toString(item.get("userId"), StringUtils.EMPTY);
            if (!userNameMap.containsKey(userId)) {
                userNameMap.put(userId, Objects.toString(item.get("userName"), StringUtils.EMPTY));
            }
            if (!userItemsMap.containsKey(userId)) {
                userItemsMap.put(userId, Lists.newArrayList());
            }
            userItemsMap.get(userId).add(item);
        });
        long normalCount = normalQueryItems.stream().mapToLong(item -> item.getLong("count")).sum();
        long overdueCount = overdueQueryItems.stream().mapToLong(item -> item.getLong("overdueCount")).sum();
        queryItems = createUserItems(userNameMap, userItemsMap, normalCount + overdueCount);
        return queryItems;
    }

    private List<QueryItem> createUserItems(Map<String, String> userNameMap, Map<String, List<QueryItem>> userItemsMap, long totalCount) {
        List<QueryItem> queryItems = Lists.newArrayList();
        userNameMap.forEach((userId, userName) -> {
            List<QueryItem> items = userItemsMap.get(userId);
            QueryItem queryItem = new QueryItem();
            queryItem.put("uuid", userId);
            queryItem.put("user_id", userId);
            queryItem.put("user_name", userName);
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
            queryItem.put("children", createUserChildrenByFlowCategory(userId, items, todoCount));
            queryItems.add(queryItem);
        });
        queryItems.sort((o1, o2) -> {
            Long count1 = o1.getLong("todoCount");
            Long count2 = o2.getLong("todoCount");
            return -count1.compareTo(count2);
        });
        return queryItems;
    }

    private List<QueryItem> createUserChildrenByFlowCategory(String userId, List<QueryItem> userItems, Long todoCount) {
        List<QueryItem> categoryItems = Lists.newArrayList();
        Map<String, List<QueryItem>> categoryItemsMap = userItems.stream().collect(Collectors.groupingBy(item -> item.getString("categoryUuid")));
        categoryItemsMap.forEach((categoryUuid, items) -> {
            QueryItem categoryItem = new QueryItem();
            categoryItem.put("uuid", userId + "_" + categoryUuid);
            categoryItem.put("user_id", userId);
            String categoryName = items.get(0).getString("categoryName");
            categoryItem.put("user_name", categoryName);
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
            categoryItem.put("children", createUserChildrenByFlow(userId, categoryUuid, items, todoCount));
            categoryItems.add(categoryItem);
        });
        return categoryItems;
    }

    private List<QueryItem> createUserChildrenByFlow(String userId, String categoryUuid, List<QueryItem> items, Long todoCount) {
        List<QueryItem> flowItems = Lists.newArrayList();
        Map<String, List<QueryItem>> flowItemsMap = items.stream().collect(Collectors.groupingBy(item -> item.getString("id")));
        flowItemsMap.forEach((id, item) -> {
            QueryItem flowItem = new QueryItem();
            flowItem.put("uuid", userId + "_" + categoryUuid + "_" + id);
            flowItem.put("user_id", userId);
            String name = item.get(0).getString("name");
            flowItem.put("user_name", name);
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
        queryParams.put("userQuerySql", FlowReportUtils.getUserQuerySql(context.getNativeDao(), queryParams));
        return queryParams;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }
}
