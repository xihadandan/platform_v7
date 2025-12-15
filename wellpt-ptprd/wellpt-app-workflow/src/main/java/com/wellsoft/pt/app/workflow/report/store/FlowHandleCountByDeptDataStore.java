/*
 * @(#)7/3/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowInstanceReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
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
 * 7/3/25.1	    zhulh		7/3/25		    Create
 * </pre>
 * @date 7/3/25
 */
@Component
public class FlowHandleCountByDeptDataStore extends AbstractDataStoreQueryInterface {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Override
    public String getQueryName() {
        return "流程统计分析_流程办理情况按部门统计表";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("deptId", "d.id", "部门ID", String.class);
        criteriaMetadata.add("deptName", "d.dept_name", "部门名称", String.class);
        criteriaMetadata.add("participateCount", "t1.participate_count", "参与", Long.class);
        criteriaMetadata.add("startCount", "t2.start_count", "发起", Long.class);
        criteriaMetadata.add("beEntrustedCount", "t3.be_entrusted_count", "受托", Long.class);
        criteriaMetadata.add("todoCount", "t4.todo_count", "待办", Long.class);
        criteriaMetadata.add("todoReadCount", "t5.todo_read_count", "待办（已阅）", Long.class);
        criteriaMetadata.add("doneCount", "t6.done_count", "已办", Long.class);
        criteriaMetadata.add("completedCount", "t7.completed_count", "办结", Long.class);
        criteriaMetadata.add("donePercent", "done_percent", "已办率", String.class);
        criteriaMetadata.add("completedPercent", "completed_percent", "办结率", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        // 分页查询速度慢，不分页
        queryParams.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(context.getNativeDao(), queryParams));
        List<QueryItem> participateCountItems = context.getNativeDao().namedQuery("flowHandleParticipateCountByDeptQuery", queryParams, QueryItem.class);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("flowHandleCountByDeptQuery", queryParams, QueryItem.class);
        // 排序
        Collections.sort(participateCountItems, (o1, o2) -> -o1.getLong("count").compareTo(o2.getLong("count")));
        List<QueryItem> detpLimitItems = FlowReportUtils.filterByPagingInfo(participateCountItems, context.getPagingInfo());
        List<String> deptIds = detpLimitItems.stream().filter(item -> StringUtils.isNotBlank(item.getString("deptId"))).map(item -> item.getString("deptId")).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deptIds)) {
            queryParams.put("deptIds", deptIds);
            queryParams.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(context.getNativeDao(), queryParams));
        }

        List<QueryItem> startCountItems = Lists.newArrayList();// context.getNativeDao().namedQuery("flowHandleStartCountByDeptQuery", queryParams, QueryItem.class);
        List<QueryItem> beEntrustedCountItems = Lists.newArrayList();// context.getNativeDao().namedQuery("flowHandleBeEntrustedCountByDeptQuery", queryParams, QueryItem.class);
        List<QueryItem> todoCountItems = Lists.newArrayList();// context.getNativeDao().namedQuery("flowHandleTodoCountByDeptQuery", queryParams, QueryItem.class);
        List<QueryItem> todoReadCountItems = Lists.newArrayList();// context.getNativeDao().namedQuery("flowHandleTodoReadCountByDeptQuery", queryParams, QueryItem.class);
        List<QueryItem> doneCountItems = Lists.newArrayList();// context.getNativeDao().namedQuery("flowHandleDoneCountByDeptQuery", queryParams, QueryItem.class);
        List<QueryItem> completedCountItems = Lists.newArrayList();// context.getNativeDao().namedQuery("flowHandleCompletedCountByDeptQuery", queryParams, QueryItem.class);
        // List<QueryItem> queryItems = Lists.newArrayList();// context.getNativeDao().namedQuery("flowHandleCountByDeptQuery", queryParams, QueryItem.class);

        try {
            Future<List<QueryItem>> startCountFuture = futureQuery("flowHandleStartCountByDeptQuery", queryParams, context);
            Future<List<QueryItem>> beEntrustedCountFuture = futureQuery("flowHandleBeEntrustedCountByDeptQuery", queryParams, context);
            Future<List<QueryItem>> todoCountFuture = futureQuery("flowHandleTodoCountByDeptQuery", queryParams, context);
            Future<List<QueryItem>> todoReadCountFuture = futureQuery("flowHandleTodoReadCountByDeptQuery", queryParams, context);
            Future<List<QueryItem>> doneCountFuture = futureQuery("flowHandleDoneCountByDeptQuery", queryParams, context);
            Future<List<QueryItem>> completedCountFuture = futureQuery("flowHandleCompletedCountByDeptQuery", queryParams, context);
            // Future<List<QueryItem>> queryFuture = futureQuery("flowHandleCountByDeptQuery", queryParams, context);

            startCountItems = startCountFuture.get();
            beEntrustedCountItems = beEntrustedCountFuture.get();
            todoCountItems = todoCountFuture.get();
            todoReadCountItems = todoReadCountFuture.get();
            doneCountItems = doneCountFuture.get();
            completedCountItems = completedCountFuture.get();
            // queryItems = queryFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            if (CollectionUtils.isEmpty(startCountItems)) {
                startCountItems = context.getNativeDao().namedQuery("flowHandleStartCountByDeptQuery", queryParams, QueryItem.class);
            }
            if (CollectionUtils.isEmpty(beEntrustedCountItems)) {
                beEntrustedCountItems = context.getNativeDao().namedQuery("flowHandleBeEntrustedCountByDeptQuery", queryParams, QueryItem.class);
            }
            if (CollectionUtils.isEmpty(todoCountItems)) {
                todoCountItems = context.getNativeDao().namedQuery("flowHandleTodoCountByDeptQuery", queryParams, QueryItem.class);
            }
            if (CollectionUtils.isEmpty(todoReadCountItems)) {
                todoReadCountItems = context.getNativeDao().namedQuery("flowHandleTodoReadCountByDeptQuery", queryParams, QueryItem.class);
            }
            if (CollectionUtils.isEmpty(doneCountItems)) {
                doneCountItems = context.getNativeDao().namedQuery("flowHandleDoneCountByDeptQuery", queryParams, QueryItem.class);
            }
            if (CollectionUtils.isEmpty(completedCountItems)) {
                completedCountItems = context.getNativeDao().namedQuery("flowHandleCompletedCountByDeptQuery", queryParams, QueryItem.class);
            }
//            if (CollectionUtils.isEmpty(queryItems)) {
//                queryItems = context.getNativeDao().namedQuery("flowHandleCountByDeptQuery", queryParams, QueryItem.class);
//            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        Map<String, QueryItem> participateItemMap = participateCountItems.stream().collect(Collectors.toMap(item -> item.getString("deptId"), item -> item));
        Map<String, QueryItem> startItemMap = startCountItems.stream().collect(Collectors.toMap(item -> item.getString("deptId"), item -> item));
        Map<String, QueryItem> beEntrustedItemMap = beEntrustedCountItems.stream().collect(Collectors.toMap(item -> item.getString("deptId"), item -> item));
        Map<String, QueryItem> todoItemMap = todoCountItems.stream().collect(Collectors.toMap(item -> item.getString("deptId"), item -> item));
        Map<String, QueryItem> todoReadItemMap = todoReadCountItems.stream().collect(Collectors.toMap(item -> item.getString("deptId"), item -> item));
        Map<String, QueryItem> doneItemMap = doneCountItems.stream().collect(Collectors.toMap(item -> item.getString("deptId"), item -> item));
        Map<String, QueryItem> completedItemMap = completedCountItems.stream().collect(Collectors.toMap(item -> item.getString("deptId"), item -> item));
        queryItems.forEach(item -> {
            String deptId = item.getString("deptId");
            QueryItem participateCountItem = participateItemMap.get(deptId);// resultItems.stream().filter(completed -> StringUtils.equals(completed.getString("deptId"), item.getString("deptId"))).findFirst().orElse(null);
            if (participateCountItem != null) {
                item.put("participateCount", participateCountItem.getLong("count"));
            }

            QueryItem startCountItem = startItemMap.get(deptId);// startCountItems.stream().filter(completed -> StringUtils.equals(completed.getString("deptId"), item.getString("deptId"))).findFirst().orElse(null);
            if (startCountItem != null) {
                item.put("startCount", startCountItem.getLong("count"));
            }

            QueryItem beEntrustedCountItem = beEntrustedItemMap.get(deptId); // beEntrustedCountItems.stream().filter(completed -> StringUtils.equals(completed.getString("deptId"), item.getString("deptId"))).findFirst().orElse(null);
            if (beEntrustedCountItem != null) {
                item.put("beEntrustedCount", beEntrustedCountItem.getLong("count"));
            }

            QueryItem todoItem = todoItemMap.get(deptId); // todoCountItems.stream().filter(completed -> StringUtils.equals(completed.getString("deptId"), item.getString("deptId"))).findFirst().orElse(null);
            if (todoItem != null) {
                item.put("todoCount", todoItem.getLong("count"));
            }

            QueryItem todoReadItem = todoReadItemMap.get(deptId);// todoReadCountItems.stream().filter(completed -> StringUtils.equals(completed.getString("deptId"), item.getString("deptId"))).findFirst().orElse(null);
            if (todoReadItem != null) {
                item.put("todoReadCount", todoReadItem.getLong("count"));
            }

            QueryItem doneItem = doneItemMap.get(deptId);// doneCountItems.stream().filter(completed -> StringUtils.equals(completed.getString("deptId"), item.getString("deptId"))).findFirst().orElse(null);
            if (doneItem != null) {
                item.put("doneCount", doneItem.getLong("count"));
            }

            QueryItem completedItem = completedItemMap.get(deptId);// completedCountItems.stream().filter(completed -> StringUtils.equals(completed.getString("deptId"), item.getString("deptId"))).findFirst().orElse(null);
            if (completedItem != null) {
                item.put("completedCount", completedItem.getLong("count"));
            }
        });

        // 排序
        Collections.sort(queryItems, (o1, o2) -> -o1.getLong("participateCount").compareTo(o2.getLong("participateCount")));
        // 过滤
        List<QueryItem> returnItems = FlowReportUtils.filterByPagingInfo(queryItems, new PagingInfo(context.getPagingInfo().getCurrentPage(), context.getPagingInfo().getPageSize()));
        // 计算完成率
        returnItems.forEach(item -> {
            long participateCount = item.getLong("participateCount");
            if (participateCount > 0) {
                item.put("donePercent", String.format("%.2f", (item.getLong("doneCount") * 1.0 / participateCount) * 100));
                item.put("completedPercent", String.format("%.2f", (item.getLong("completedCount") * 1.0 / participateCount) * 100));
            } else {
                item.put("donePercent", "0.00");
                item.put("completedPercent", "0.00");
            }
        });
        return returnItems;
    }

    public Future<List<QueryItem>> futureQuery(String queryName, Map<String, Object> queryParams, QueryContext context) {
        return scheduledExecutorService.submit(() -> {
            return flowInstanceReportService.namedQuery(queryName, queryParams, null);
        });
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
        Criterion criterion = FlowReportUtils.createFlowHandleCountCriterion(queryParams, workFlowSettings);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        return queryParams;
    }

    @Override
    public long count(QueryContext context) {
        long totalCount = context.getPagingInfo().getTotalCount();
        return totalCount > 0 ? totalCount : totalCount(context);
    }

    private long totalCount(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        return context.getNativeDao().countByNamedQuery("flowHandleCountByDeptQuery", queryParams);
    }

}
