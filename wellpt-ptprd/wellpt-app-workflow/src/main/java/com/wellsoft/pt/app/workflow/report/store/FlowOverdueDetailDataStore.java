/*
 * @(#)7/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.service.TaskTimerService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/16/25.1	    zhulh		7/16/25		    Create
 * </pre>
 * @date 7/16/25
 */
@Component
public class FlowOverdueDetailDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private TaskTimerService taskTimerService;

    @Override
    public String getQueryName() {
        return "流程统计分析_流程逾期分析_逾期流程明细";
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
        criteriaMetadata.add("timingTaskCount", "t.timing_task_count", "计时环节数", Integer.class);
        criteriaMetadata.add("overdueMinutes", "t.overdue_minutes", "逾期时长", Double.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("listFlowOverdueTimeQuery", queryParams, QueryItem.class, context.getPagingInfo());
        // 统计计时环节数
        List<String> flowInstUuids = queryItems.stream().map(item -> item.getString("uuid")).collect(Collectors.toList());
        List<TaskTimer> taskTimers = taskTimerService.listByFlowInstUuids(flowInstUuids);
        Map<String, List<TaskTimer>> timingTaskCountMap = taskTimers.stream().collect(Collectors.groupingBy(TaskTimer::getFlowInstUuid));

        queryItems.forEach(item -> {
            // 计时环节数
            String uuid = item.getString("uuid");
            List<TaskTimer> timers = timingTaskCountMap.get(uuid);
            if (CollectionUtils.isNotEmpty(timers)) {
                long timingTaskCount = timers.stream().flatMap(timer -> {
                    String taskIds = timer.getTaskIds();
                    if (StringUtils.isBlank(taskIds)) {
                        return Stream.empty();
                    }
                    return Stream.of(StringUtils.split(taskIds, Separator.SEMICOLON.getValue()));
                }).distinct().count();
                item.put("timingTaskCount", timingTaskCount);
            } else {
                item.put("timingTaskCount", 0);
            }

            // 逾期时长
            Double overdueMinutes = item.getDouble("overdueMinutes");
            if (overdueMinutes != null) {
                item.put("overdueMinutes", Math.round(overdueMinutes));
            } else {
                item.put("overdueMinutes", 0d);
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
        Criterion criterion = FlowReportUtils.createFlowOverdueCriterion(queryParams, workFlowSettings);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        return queryParams;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

}
