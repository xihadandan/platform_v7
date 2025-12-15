/*
 * @(#)7/1/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.criterion.Junction;
import com.wellsoft.pt.jpa.criterion.Restrictions;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/1/25.1	    zhulh		7/1/25		    Create
 * </pre>
 * @date 7/1/25
 */
public class FlowReportUtils {
    private static Logger LOG = LoggerFactory.getLogger(FlowReportUtils.class);

    private static final int DEFAULT_MAX_BAR_COUNT = 12;

    private FlowReportUtils() {
    }

    public static Criterion createFlowInstanceCountCriterion(Map<String, Object> params, WorkFlowSettings workFlowSettings) {
        Junction conjunction = Restrictions.conjunction();
        try {
            int recentStartTimeRange = getRecentStartTimeRange(workFlowSettings);
            if (MapUtils.isNotEmpty(params)) {
                // 发起人范围
                addStartUserRangeCriterion(params, "t1.start_user_id", conjunction);

                // 开始时间范围
                addStartTimeRangeCriterion(params, "t1.start_time", conjunction, recentStartTimeRange);

                // 结束时间范围
                addEndTimeRangeCriterion(params, "t1.end_time", conjunction);

                // 流程范围
                addFlowRangeCriterion(params, "t1.id", "t2.category", conjunction);

                // 仅统计计时流程
                addOnlyIncludeTimingFlowCriterion(params, conjunction);

                // 包含禁用流程
                addIncludeDisabledFlowCriterion(params, "t2.enabled", conjunction);
            } else {
                Calendar calendar = Calendar.getInstance();
                Object hi = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, recentStartTimeRange);
                Object lo = DateUtils.parse(DateUtils.formatDate(calendar.getTime()));
                conjunction.add(Restrictions.between("t1.start_time", lo, hi));
                // conjunction.add(Restrictions.eq("t2.enabled", true));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        String system = StringUtils.defaultIfBlank(RequestSystemContextPathResolver.system(), Objects.toString(params.get("system"), StringUtils.EMPTY));
        if (StringUtils.isNotBlank(system)) {
            conjunction.add(Restrictions.eq("t1.system", system));
            params.put("system", system);
        }
        return conjunction;
    }

    private static int getRecentStartTimeRange(WorkFlowSettings workFlowSettings) {
        int defaultDays = -30;
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("search")) {
            JSONObject search = jsonObject.getJSONObject("search");
            if (search.has("recentStartTimeRange")) {
                return -search.getInt("recentStartTimeRange");
            }
        }
        return defaultDays;
    }

    public static int getMaxBarCountOfFlowInstCount(WorkFlowSettings workFlowSettings, int defaultMaxBarCount) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowInstCount")) {
            JSONObject flowInstCount = jsonObject.getJSONObject("flowInstCount");
            if (flowInstCount.has("maxBarCount")) {
                return flowInstCount.getInt("maxBarCount");
            }
        }
        return defaultMaxBarCount;
    }

    public static Criterion createFlowHandleCountCriterion(Map<String, Object> params, WorkFlowSettings workFlowSettings) {
        Junction conjunction = Restrictions.conjunction();
        try {
            int recentStartTimeRange = getRecentStartTimeRange(workFlowSettings);
            if (MapUtils.isNotEmpty(params)) {
                // 发起人范围
                addStartUserRangeCriterion(params, "t3.start_user_id", conjunction);

                // 开始时间范围
                addStartTimeRangeCriterion(params, "t3.start_time", conjunction, recentStartTimeRange);

                // 结束时间范围
                addEndTimeRangeCriterion(params, "t3.end_time", conjunction);

                // 流程范围
                addFlowRangeCriterion(params, "t3.id", "t4.category", conjunction);

                // 流程状态
                addFlowStateCriterion(params, "t3.end_time", conjunction);

                // 包含禁用流程
                addIncludeDisabledFlowCriterion(params, "t4.enabled", conjunction);

                // 部门
                addDeptRangeParams(params);
            } else {
                Calendar calendar = Calendar.getInstance();
                Object hi = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, recentStartTimeRange);
                Object lo = DateUtils.parse(DateUtils.formatDate(calendar.getTime()));
                conjunction.add(Restrictions.between("t3.start_time", lo, hi));
                // conjunction.add(Restrictions.eq("t2.enabled", true));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        String system = StringUtils.defaultIfBlank(RequestSystemContextPathResolver.system(), Objects.toString(params.get("system"), StringUtils.EMPTY));
        if (StringUtils.isNotBlank(system)) {
            conjunction.add(Restrictions.eq("t3.system", system));
            params.put("system", system);
        }
        return conjunction;
    }

    private static void addDeptRangeParams(Map<String, Object> params) {
        if (params.containsKey("deptRange")) {
            String deptRange = Objects.toString(params.get("deptRange"), StringUtils.EMPTY);
            if (StringUtils.isNotBlank(deptRange)) {
                List<String> deptIds = Arrays.asList(StringUtils.split(deptRange, Separator.SEMICOLON.getValue()));
                if (CollectionUtils.isNotEmpty(deptIds)) {
                    params.put("limitDeptIds", deptIds);
                }
            }
        }
    }

    private static void addIncludeDisabledFlowCriterion(Map<String, Object> params, String columnName, Junction conjunction) {
        if (!params.containsKey("includeDisabledFlow") || BooleanUtils.isTrue((Boolean) params.get("includeDisabledFlow"))
                || "true".equals(params.get("includeDisabledFlow"))) {
        } else {
            conjunction.add(Restrictions.eq(columnName, true));
        }
    }

    private static void addFlowStateCriterion(Map<String, Object> params, String columnName, Junction conjunction) {
        if (params.containsKey("flowState")) {
            String flowState = (String) params.get("flowState");
            if (StringUtils.equals(flowState, "completed")) {
                conjunction.add(Restrictions.sql(columnName + " is not null"));
            } else if (StringUtils.equals(flowState, "uncompleted")) {
                conjunction.add(Restrictions.sql(columnName + " is null"));
            }
        }
    }

    private static void addFlowRangeCriterion(Map<String, Object> params, String idColumnName, String categoryColumnName, Junction conjunction) {
        if (params.containsKey("flowRange")) {
            String flowRange = Objects.toString(params.get("flowRange"), StringUtils.EMPTY);
            List<String> flowIds = Arrays.asList(StringUtils.split(flowRange, Separator.SEMICOLON.getValue()));
            if (CollectionUtils.isNotEmpty(flowIds)) {
                Junction disjunction = Restrictions.disjunction();
                disjunction.add(Restrictions.in(idColumnName, flowIds));
                disjunction.add(Restrictions.in(categoryColumnName, flowIds));
                conjunction.add(disjunction);
            }
        }
    }

    private static void addEndTimeRangeCriterion(Map<String, Object> params, String columnName, Junction conjunction) throws ParseException {
        if (params.containsKey("endTimeRange")) {
            List<String> endTimeRange = (List<String>) params.get("endTimeRange");
            if (CollectionUtils.size(endTimeRange) == 2) {
                Object lo = DateUtils.parse(endTimeRange.get(0));
                Object hi = DateUtils.getMaxTimeCalendar(DateUtils.getCalendar(DateUtils.parse(endTimeRange.get(1)))).getTime();
                conjunction.add(Restrictions.between(columnName, lo, hi));
            }
        }
    }

    private static void addStartTimeRangeCriterion(Map<String, Object> params, String columnName, Junction conjunction, int recentStartTimeRange) throws ParseException {
        if (params.containsKey("startTimeRange")) {
            Object range = params.get("startTimeRange");
            List<String> startTimeRange = Lists.newArrayList();
            if (range instanceof List) {
                startTimeRange.addAll((List<String>) range);
            } else if (range instanceof String) {
                startTimeRange.addAll(Arrays.asList(StringUtils.split(Objects.toString(range, StringUtils.EMPTY), ",")));
            }
            if (CollectionUtils.size(startTimeRange) == 2) {
                Object lo = DateUtils.parse(startTimeRange.get(0));
                Object hi = DateUtils.getMaxTimeCalendar(DateUtils.getCalendar(DateUtils.parse(startTimeRange.get(1)))).getTime();
                conjunction.add(Restrictions.between(columnName, lo, hi));
            } else {
                Calendar calendar = Calendar.getInstance();
                Object hi = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, recentStartTimeRange);
                Object lo = DateUtils.parse(DateUtils.formatDate(calendar.getTime()));
                conjunction.add(Restrictions.between(columnName, lo, hi));
            }
        } else {
            Calendar calendar = Calendar.getInstance();
            Object hi = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, recentStartTimeRange);
            Object lo = DateUtils.parse(DateUtils.formatDate(calendar.getTime()));
            conjunction.add(Restrictions.between(columnName, lo, hi));
        }
    }

    private static void addStartUserRangeCriterion(Map<String, Object> params, String columnName, Junction conjunction) {
        if (params.containsKey("startUserRange")) {
            String startUserRange = Objects.toString(params.get("startUserRange"), StringUtils.EMPTY);
            Map<String, String> userMap = getUsersByIds(startUserRange);
            if (MapUtils.isNotEmpty(userMap)) {
                conjunction.add(Restrictions.in(columnName, userMap.keySet()));
                params.put("limitUserIds", userMap.keySet());
            }
        }
    }

    public static Criterion createFlowEfficiencyCriterion(Map<String, Object> params, WorkFlowSettings workFlowSettings) {
        Junction conjunction = Restrictions.conjunction();
        try {
            int recentStartTimeRange = getRecentStartTimeRange(workFlowSettings);
            if (MapUtils.isNotEmpty(params)) {
                // 发起人范围
                addStartUserRangeCriterion(params, "t1.start_user_id", conjunction);

                // 开始时间范围
                addStartTimeRangeCriterion(params, "t1.start_time", conjunction, recentStartTimeRange);

                // 结束时间范围
                addEndTimeRangeCriterion(params, "t1.end_time", conjunction);

                // 流程范围
                addFlowRangeCriterion(params, "t1.id", "t2.category", conjunction);

                // 流程状态
                addFlowStateCriterion(params, "t1.end_time", conjunction);

                // 包含禁用流程
                addIncludeDisabledFlowCriterion(params, "t2.enabled", conjunction);
            } else {
                Calendar calendar = Calendar.getInstance();
                Object hi = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, recentStartTimeRange);
                Object lo = DateUtils.parse(DateUtils.formatDate(calendar.getTime()));
                conjunction.add(Restrictions.between("t1.start_time", lo, hi));
                // conjunction.add(Restrictions.eq("t2.enabled", true));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        String system = StringUtils.defaultIfBlank(RequestSystemContextPathResolver.system(), Objects.toString(params.get("system"), StringUtils.EMPTY));
        if (StringUtils.isNotBlank(system)) {
            conjunction.add(Restrictions.eq("t1.system", system));
            params.put("system", system);
        }
        return conjunction;
    }

    public static int getMaxFlowBarCountOfFlowEfficiency(WorkFlowSettings workFlowSettings, int defaultMaxBarCount) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowEfficiency")) {
            JSONObject flowEfficiency = jsonObject.getJSONObject("flowEfficiency");
            if (flowEfficiency.has("maxFlowBarCount")) {
                return flowEfficiency.getInt("maxFlowBarCount");
            }
        }
        return defaultMaxBarCount;
    }

    public static int getMaxTaskBarCountOfFlowEfficiency(WorkFlowSettings workFlowSettings, int defaultMaxBarCount) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowEfficiency")) {
            JSONObject flowEfficiency = jsonObject.getJSONObject("flowEfficiency");
            if (flowEfficiency.has("maxTaskBarCount")) {
                return flowEfficiency.getInt("maxTaskBarCount");
            }
        }
        return defaultMaxBarCount;
    }

    public static Criterion createFlowOverdueCriterion(Map<String, Object> params, WorkFlowSettings workFlowSettings) {
        Junction conjunction = Restrictions.conjunction();
        try {
            int recentStartTimeRange = getRecentStartTimeRange(workFlowSettings);
            if (MapUtils.isNotEmpty(params)) {
                // 发起人范围
                addStartUserRangeCriterion(params, "t1.start_user_id", conjunction);

                // 开始时间范围
                addStartTimeRangeCriterion(params, "t1.start_time", conjunction, recentStartTimeRange);

                // 结束时间范围
                addEndTimeRangeCriterion(params, "t1.end_time", conjunction);

                // 流程范围
                addFlowRangeCriterion(params, "t1.id", "t2.category", conjunction);

                // 流程状态
                addFlowStateCriterion(params, "t1.end_time", conjunction);

                // 仅统计计时流程
                addOnlyIncludeTimingFlowCriterion(params, conjunction);

                // 包含禁用流程
                addIncludeDisabledFlowCriterion(params, "t2.enabled", conjunction);
            } else {
                Calendar calendar = Calendar.getInstance();
                Object hi = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, recentStartTimeRange);
                Object lo = DateUtils.parse(DateUtils.formatDate(calendar.getTime()));
                conjunction.add(Restrictions.between("t1.start_time", lo, hi));
                // conjunction.add(Restrictions.eq("t2.enabled", true));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        String system = StringUtils.defaultIfBlank(RequestSystemContextPathResolver.system(), Objects.toString(params.get("system"), StringUtils.EMPTY));
        if (StringUtils.isNotBlank(system)) {
            conjunction.add(Restrictions.eq("t1.system", system));
            params.put("system", system);
        }
        return conjunction;
    }

    private static void addOnlyIncludeTimingFlowCriterion(Map<String, Object> params, Junction conjunction) {
        if (BooleanUtils.isTrue((Boolean) params.get("onlyIncludeTimingFlow"))) {
            String sql = "exists(select 1 from wf_task_timer t3 where t3.flow_inst_uuid = t1.uuid)";
            conjunction.add(Restrictions.sql(sql));
        }
    }

    public static int getMaxBarCountOfFlowOverdue(WorkFlowSettings workFlowSettings, int defaultMaxBarCount) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowOverdue")) {
            JSONObject flowOverdue = jsonObject.getJSONObject("flowOverdue");
            if (flowOverdue.has("maxOverdueBarCount")) {
                return flowOverdue.getInt("maxOverdueBarCount");
            }
        }
        return defaultMaxBarCount;
    }

    public static int getMaxBarCountOfFlowAvgOverdue(WorkFlowSettings workFlowSettings, int defaultMaxBarCount) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowOverdue")) {
            JSONObject flowOverdue = jsonObject.getJSONObject("flowOverdue");
            if (flowOverdue.has("maxAvgOverdueBarCount")) {
                return flowOverdue.getInt("maxAvgOverdueBarCount");
            }
        }
        return defaultMaxBarCount;
    }

    public static Criterion createFlowOperationCriterion(Map<String, Object> params, WorkFlowSettings workFlowSettings,
                                                         boolean addOperationCriterion) {
        Junction conjunction = Restrictions.conjunction();
        try {
            int recentStartTimeRange = getRecentStartTimeRange(workFlowSettings);
            if (MapUtils.isNotEmpty(params)) {
                // 发起人范围
                // addStartUserRangeCriterion(params, "t1.start_user_id", conjunction);

                // 开始时间范围
                addStartTimeRangeCriterion(params, "t1.start_time", conjunction, recentStartTimeRange);

                // 结束时间范围
                addEndTimeRangeCriterion(params, "t1.end_time", conjunction);

                // 操作类型
                if (addOperationCriterion) {
                    addOperationCriterion(params, conjunction);
                }

                // 流程范围
                addFlowRangeCriterion(params, "t1.id", "t2.category", conjunction);

                // 流程状态
                addFlowStateCriterion(params, "t1.end_time", conjunction);

                // 仅统计计时流程
                // addOnlyIncludeTimingFlowCriterion(params, conjunction);

                // 包含禁用流程
                addIncludeDisabledFlowCriterion(params, "t2.enabled", conjunction);
            } else {
                Calendar calendar = Calendar.getInstance();
                Object hi = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, recentStartTimeRange);
                Object lo = DateUtils.parse(DateUtils.formatDate(calendar.getTime()));
                conjunction.add(Restrictions.between("t1.start_time", lo, hi));
                // conjunction.add(Restrictions.eq("t2.enabled", true));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        String system = StringUtils.defaultIfBlank(RequestSystemContextPathResolver.system(), Objects.toString(params.get("system"), StringUtils.EMPTY));
        if (StringUtils.isNotBlank(system)) {
            conjunction.add(Restrictions.eq("t1.system", system));
            params.put("system", system);
        }
        return conjunction;
    }

    private static void addOperationCriterion(Map<String, Object> params, Junction conjunction) {
        if (params.containsKey("operationTypes")) {
            List<String> operations = (List<String>) params.get("operationTypes");
            if (operations == null) {
                return;
            }
            List<Integer> actionCodes = Lists.newArrayList();
            for (String operation : operations) {
                switch (operation) {
                    case "Rollback":
                        actionCodes.addAll(WorkFlowOperation.getActionCodeOfRollback());
                        break;
                    case "Transfer":
                        actionCodes.add(ActionCode.TRANSFER.getCode());
                        break;
                    case "CounterSign":
                        actionCodes.add(ActionCode.COUNTER_SIGN.getCode());
                        break;
                    case "AddSign":
                        actionCodes.add(ActionCode.ADD_SIGN.getCode());
                        break;
                    case "HandOver":
                        actionCodes.add(ActionCode.HAND_OVER.getCode());
                        break;
                    case "GotoTask":
                        actionCodes.add(ActionCode.GOTO_TASK.getCode());
                        break;
                }
            }

            if (CollectionUtils.isNotEmpty(actionCodes)) {
                params.put("limitActionCodes", actionCodes);
            } else {
                params.put("limitActionCodes", Lists.newArrayList(-1));
            }
            String sql = "exists(select 1 from wf_task_operation t3 where t3.flow_inst_uuid = t1.uuid and t3.action_code in (:limitActionCodes))";
            conjunction.add(Restrictions.sql(sql));
        }
    }

    public static int getMaxBarCountOfFlowInefficient(WorkFlowSettings workFlowSettings, int defaultMaxBarCount) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowOperation")) {
            JSONObject flowOperation = jsonObject.getJSONObject("flowOperation");
            if (flowOperation.has("maxInefficientBarCount")) {
                return flowOperation.getInt("maxInefficientBarCount");
            }
        }
        return defaultMaxBarCount;
    }

    public static Criterion createFlowTodoCriterion(Map<String, Object> params, WorkFlowSettings workFlowSettings) {
        Junction conjunction = Restrictions.conjunction();
        try {
            int recentStartTimeRange = getRecentStartTimeRange(workFlowSettings);
            if (MapUtils.isNotEmpty(params)) {
                // 人员范围
                addUserRangeCriterion(params);

                // 开始时间范围
                addStartTimeRangeCriterion(params, "t3.start_time", conjunction, recentStartTimeRange);

                // 接收时间范围
                addArriveTimeRangeCriterion(params, "ti.start_time", conjunction);

                // 结束时间范围
                addEndTimeRangeCriterion(params, "t3.end_time", conjunction);

                // 流程范围
                addFlowRangeCriterion(params, "t3.id", "t4.category", conjunction);

                // 流程状态
                // addFlowStateCriterion(params, "t3.end_time", conjunction);

                // 包含禁用流程
                addIncludeDisabledFlowCriterion(params, "t4.enabled", conjunction);

                // 部门范围
                addDeptRangeParams(params);
            } else {
                Calendar calendar = Calendar.getInstance();
                Object hi = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, recentStartTimeRange);
                Object lo = DateUtils.parse(DateUtils.formatDate(calendar.getTime()));
                conjunction.add(Restrictions.between("t3.start_time", lo, hi));
                // conjunction.add(Restrictions.eq("t2.enabled", true));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        String system = StringUtils.defaultIfBlank(RequestSystemContextPathResolver.system(), Objects.toString(params.get("system"), StringUtils.EMPTY));
        if (StringUtils.isNotBlank(system)) {
            conjunction.add(Restrictions.eq("t3.system", system));
            params.put("system", system);
        }
        return conjunction;
    }

    private static void addUserRangeCriterion(Map<String, Object> params) {
        if (params.containsKey("userRange") && !params.containsKey("userIds")) {
            OrgFacadeService orgFacadeService = ApplicationContextHolder.getBean(OrgFacadeService.class);
            String userRange = Objects.toString(params.get("userRange"), StringUtils.EMPTY);
            Map<String, String> userMap = getUsersByIds(userRange);
            if (MapUtils.isNotEmpty(userMap)) {
                params.put("userIds", userMap.keySet());
            }
        }
    }

    public static int getMaxBarCountOfFlowTodoByUser(WorkFlowSettings workFlowSettings, int defaultMaxBarCount) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowTodo")) {
            JSONObject flowTodo = jsonObject.getJSONObject("flowTodo");
            if (flowTodo.has("maxUserBarCount")) {
                return flowTodo.getInt("maxUserBarCount");
            }
        }
        return defaultMaxBarCount;
    }

    public static int getMaxBarCountOfFlowTodoByDept(WorkFlowSettings workFlowSettings, int defaultMaxBarCount) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowTodo")) {
            JSONObject flowTodo = jsonObject.getJSONObject("flowTodo");
            if (flowTodo.has("maxDeptBarCount")) {
                return flowTodo.getInt("maxDeptBarCount");
            }
        }
        return defaultMaxBarCount;
    }

    public static List<QueryItem> filterByPagingInfo(List<QueryItem> items, PagingInfo pagingInfo) {
        if (pagingInfo == null || pagingInfo.getPageSize() < 0) {
            return items;
        }

        int first = pagingInfo.getFirst();
        if (first < 0 || CollectionUtils.size(items) < first + 1) {
            return Collections.emptyList();
        }
        int max = first + pagingInfo.getPageSize();
        return items.subList(first, Math.min(max, CollectionUtils.size(items)));
    }

    public static Criterion createFlowHandleEfficiencyCriterion(Map<String, Object> params, WorkFlowSettings workFlowSettings) {
        Junction conjunction = Restrictions.conjunction();
        try {
            int recentStartTimeRange = getRecentStartTimeRange(workFlowSettings);
            if (MapUtils.isNotEmpty(params)) {
                // 人员范围
                addUserRangeCriterion(params);

                // 开始时间范围
                addStartTimeRangeCriterion(params, "t3.start_time", conjunction, recentStartTimeRange);

                // 接收时间范围
                addArriveTimeRangeCriterion(params, "ti.start_time", conjunction);

                // 结束时间范围
                addEndTimeRangeCriterion(params, "t3.end_time", conjunction);

                // 流程范围
                addFlowRangeCriterion(params, "t3.id", "t4.category", conjunction);

                // 流程状态
                // addFlowStateCriterion(params, "t3.end_time", conjunction);

                // 包含禁用流程
                addIncludeDisabledFlowCriterion(params, "t4.enabled", conjunction);

                // 部门范围
                addDeptRangeParams(params);
            } else {
                Calendar calendar = Calendar.getInstance();
                Object hi = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, recentStartTimeRange);
                Object lo = DateUtils.parse(DateUtils.formatDate(calendar.getTime()));
                conjunction.add(Restrictions.between("t3.start_time", lo, hi));
                // conjunction.add(Restrictions.eq("t2.enabled", true));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        String system = StringUtils.defaultIfBlank(RequestSystemContextPathResolver.system(), Objects.toString(params.get("system"), StringUtils.EMPTY));
        if (StringUtils.isNotBlank(system)) {
            conjunction.add(Restrictions.eq("t3.system", system));
            params.put("system", system);
        }
        return conjunction;
    }

    private static void addArriveTimeRangeCriterion(Map<String, Object> params, String columnName, Junction conjunction) throws ParseException {
        if (params.containsKey("arriveTimeRange")) {
            List<String> arriveTimeRange = (List<String>) params.get("arriveTimeRange");
            if (CollectionUtils.size(arriveTimeRange) == 2) {
                Object lo = DateUtils.parse(arriveTimeRange.get(0));
                Object hi = DateUtils.getMaxTimeCalendar(DateUtils.getCalendar(DateUtils.parse(arriveTimeRange.get(1)))).getTime();
                conjunction.add(Restrictions.between(columnName, lo, hi));
            }
        }
    }

    public static int getMaxUserBarCountOfFlowHandleEfficiency(WorkFlowSettings workFlowSettings) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowHandleEfficiency")) {
            JSONObject flowHandleEfficiency = jsonObject.getJSONObject("flowHandleEfficiency");
            if (flowHandleEfficiency.has("maxUserBarCount")) {
                return flowHandleEfficiency.getInt("maxUserBarCount");
            }
        }
        return DEFAULT_MAX_BAR_COUNT;
    }

    public static int getMaxDeptBarCountOfFlowHandleEfficiency(WorkFlowSettings workFlowSettings) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowHandleEfficiency")) {
            JSONObject flowHandleEfficiency = jsonObject.getJSONObject("flowHandleEfficiency");
            if (flowHandleEfficiency.has("maxUserBarCount")) {
                return flowHandleEfficiency.getInt("maxUserBarCount");
            }
        }
        return DEFAULT_MAX_BAR_COUNT;
    }

    public static Criterion createFlowHandleOverdueCriterion(Map<String, Object> params, WorkFlowSettings workFlowSettings) {
        Junction conjunction = Restrictions.conjunction();
        try {
            int recentStartTimeRange = getRecentStartTimeRange(workFlowSettings);
            if (MapUtils.isNotEmpty(params)) {
                // 人员范围
                addUserRangeCriterion(params);

                // 开始时间范围
                addStartTimeRangeCriterion(params, "t3.start_time", conjunction, recentStartTimeRange);

                // 接收时间范围
                addArriveTimeRangeCriterion(params, "ti.start_time", conjunction);

                // 结束时间范围
                addEndTimeRangeCriterion(params, "t3.end_time", conjunction);

                // 流程范围
                addFlowRangeCriterion(params, "t3.id", "t4.category", conjunction);

                // 流程状态
                // addFlowStateCriterion(params, "t3.end_time", conjunction);

                // 包含禁用流程
                addIncludeDisabledFlowCriterion(params, "t4.enabled", conjunction);

                // 部门范围
                addDeptRangeParams(params);
            } else {
                Calendar calendar = Calendar.getInstance();
                Object hi = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, recentStartTimeRange);
                Object lo = DateUtils.parse(DateUtils.formatDate(calendar.getTime()));
                conjunction.add(Restrictions.between("t3.start_time", lo, hi));
                // conjunction.add(Restrictions.eq("t2.enabled", true));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        String system = StringUtils.defaultIfBlank(RequestSystemContextPathResolver.system(), Objects.toString(params.get("system"), StringUtils.EMPTY));
        if (StringUtils.isNotBlank(system)) {
            conjunction.add(Restrictions.eq("t3.system", system));
            params.put("system", system);
        }
        return conjunction;
    }

    public static int getMaxUserBarCountOfFlowHandleOverdue(WorkFlowSettings workFlowSettings) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowHandleOverdue")) {
            JSONObject flowHandleOverdue = jsonObject.getJSONObject("flowHandleOverdue");
            if (flowHandleOverdue.has("maxUserBarCount")) {
                return flowHandleOverdue.getInt("maxUserBarCount");
            }
        }
        return DEFAULT_MAX_BAR_COUNT;
    }

    public static int getMaxDeptBarCountOfFlowHandleOverdue(WorkFlowSettings workFlowSettings) {
        JSONObject jsonObject = new JSONObject(workFlowSettings.getReport());
        if (jsonObject != null && jsonObject.has("flowHandleOverdue")) {
            JSONObject flowHandleOverdue = jsonObject.getJSONObject("flowHandleOverdue");
            if (flowHandleOverdue.has("maxDeptBarCount")) {
                return flowHandleOverdue.getInt("maxDeptBarCount");
            }
        }
        return DEFAULT_MAX_BAR_COUNT;
    }

    public static Object getDeptQuerySql(NativeDao nativeDao, Map<String, Object> params) {
        List<String> deptIds = (List<String>) params.get("deptIds");
        if (deptIds == null && params.containsKey("deptId")) {
            params.put("deptIds", Arrays.asList(params.get("deptId")));
        } else if (deptIds != null && CollectionUtils.size(deptIds) > 1000) {
            AtomicInteger index = new AtomicInteger();
            ListUtils.handleSubList(deptIds, 1000, (subList) -> {
                params.put("deptIds" + index.incrementAndGet(), subList);
            });
            params.remove("deptIds");
        }
        return NamedQueryScriptLoader.generateDynamicNamedQueryString(nativeDao.getSessionFactory(), "flowDeptReportQuery", params);
    }

    public static Object getUserQuerySql(NativeDao nativeDao, Map<String, Object> params) {
        Collection<String> userIds = (Collection<String>) params.get("userIds");
        if (CollectionUtils.isEmpty(userIds)) {
            params.put("userIds", Lists.newArrayList("-1"));
            // params.put("userSids", Lists.newArrayList("-1"));
        } else {
            // params.put("userSids", getUserSids(userIds));
        }
        return NamedQueryScriptLoader.generateDynamicNamedQueryString(nativeDao.getSessionFactory(), "flowUserReportQuery", params);
    }

    public static Map<String, String> getUsersByIds(String userRange) {
        if (StringUtils.isBlank(userRange)) {
            return Collections.emptyMap();
        }

        OrgFacadeService orgFacadeService = ApplicationContextHolder.getBean(OrgFacadeService.class);
        String[] orgVersionIds = orgFacadeService.listOrgVersionBySystem(RequestSystemContextPathResolver.system(), null).stream().map(org -> org.getId()).toArray(String[]::new);
        return orgFacadeService.getUsersByIds(Arrays.asList(org.apache.commons.lang.StringUtils.split(userRange, Separator.SEMICOLON.getValue())), orgVersionIds);
    }

    public static Set<String> getUserSids(Collection<String> userIds) {
        Set<String> userSids = Sets.newLinkedHashSet();
        userIds.forEach(userId -> {
            userSids.addAll(PermissionGranularityUtils.getUserSids(userId));
        });
        return userSids;
    }

}
