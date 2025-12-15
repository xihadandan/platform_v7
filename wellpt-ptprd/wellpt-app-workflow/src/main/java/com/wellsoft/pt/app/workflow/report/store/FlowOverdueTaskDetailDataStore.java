/*
 * @(#)7/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.enums.TaskNodeType;
import com.wellsoft.pt.bpm.engine.service.TaskOperationService;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowService;
import com.wellsoft.pt.bpm.engine.service.TaskTimerService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
public class FlowOverdueTaskDetailDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskSubFlowService taskSubFlowService;

    @Autowired
    private TaskTimerService taskTimerService;

    @Override
    public String getQueryName() {
        return "流程统计分析_流程逾期分析_逾期环节明细";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "环节实例UUID", String.class);
        criteriaMetadata.add("title", "t.title", "流程标题", String.class);
        criteriaMetadata.add("id", "t.id", "流程定义ID", String.class);
        criteriaMetadata.add("name", "t.name", "流程定义名称", String.class);
        criteriaMetadata.add("startTime", "t.start_time", "发起时间", Date.class);
        criteriaMetadata.add("endTime", "t.end_time", "结束时间", Date.class);
        criteriaMetadata.add("taskId", "t.task_id", "环节ID", String.class);
        criteriaMetadata.add("taskName", "t.task_name", "环节名称", String.class);
        criteriaMetadata.add("taskType", "t.task_type", "环节类型", Integer.class);
        criteriaMetadata.add("operatorName", "t.operator_name", "办理人", String.class);
        criteriaMetadata.add("timingState", "t.timing_state", "计时状态", Integer.class);
        criteriaMetadata.add("duration", "t.duration", "环节用时", Double.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("listTaskOverdueDetailQuery", queryParams, QueryItem.class, context.getPagingInfo());
        String flowInstUuid = Objects.toString(queryParams.get("flowInstUuid"), StringUtils.EMPTY);
        // 过滤计时环节
        AtomicInteger skippedCount = new AtomicInteger(0);
        if (StringUtils.isNotBlank(flowInstUuid)) {
            List<TaskTimer> taskTimers = taskTimerService.getByFlowInstUuid(flowInstUuid);
            Set<String> timedTaskIds = taskTimers.stream().flatMap(taskTimer -> {
                if (StringUtils.isBlank(taskTimer.getTaskIds())) {
                    return Stream.empty();
                }
                return Arrays.stream(StringUtils.split(taskTimer.getTaskIds(), Separator.SEMICOLON.getValue()));
            }).collect(Collectors.toSet());
            queryItems = queryItems.stream().filter(item -> {
                if (timedTaskIds.contains(item.getString("taskId"))) {
                    return true;
                }
                skippedCount.incrementAndGet();
                return false;
            }).collect(Collectors.toList());
        }
        context.getPagingInfo().setTotalCount(context.getPagingInfo().getTotalCount() - skippedCount.get());
        Set<String> orgIds = Sets.newHashSet();
        queryItems.forEach(item -> {
            String owner = item.getString("owner");
            if (StringUtils.isNotBlank(owner)) {
                orgIds.addAll(Arrays.asList(StringUtils.split(owner, Separator.SEMICOLON.getValue())));
            } else {
                Integer taskType = item.getInt("taskType");
                if (TaskNodeType.SubTask.getValueAsInt().equals(taskType)) {
                    String operatorName = taskSubFlowService.getAllByParentTaskInstUuid(item.getString("uuid"))
                            .stream().flatMap(subFlow -> {
                                String todoName = subFlow.getTodoName();
                                if (StringUtils.isBlank(todoName)) {
                                    return Stream.empty();
                                }
                                return Arrays.stream(StringUtils.split(todoName, Separator.SEMICOLON.getValue()));
                            }).distinct().collect(Collectors.joining(Separator.SEMICOLON.getValue()));
                    item.put("operatorName", operatorName);
                } else {
                    String operatorName = taskOperationService.listByTaskInstUuid(item.getString("uuid")).stream().map(TaskOperation::getAssigneeName).distinct().collect(Collectors.joining(Separator.SEMICOLON.getValue()));
                    item.put("operatorName", operatorName);
                }
            }
        });
        Map<String, String> userMap = orgFacadeService.getNameByOrgEleIds(Lists.newArrayList(orgIds));

        queryItems.forEach(item -> {
            String owner = item.getString("owner");
            if (StringUtils.isNotBlank(owner)) {
                Set<String> operatorNames = Sets.newLinkedHashSet();
                Arrays.stream(StringUtils.split(owner, Separator.SEMICOLON.getValue())).forEach(orgId -> {
                    String userName = userMap.get(orgId);
                    if (StringUtils.isNotBlank(userName)) {
                        operatorNames.add(userName);
                    }
                });
                item.put("operatorName", StringUtils.join(operatorNames, Separator.SEMICOLON.getValue()));
            }
            Double duration = item.getDouble("duration");
            if (duration != null) {
                item.put("duration", Math.round(duration));
            } else {
                item.put("duration", 0d);
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
