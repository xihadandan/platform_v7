/*
 * @(#)7/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
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

import java.util.Date;
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
 * 7/21/25.1	    zhulh		7/21/25		    Create
 * </pre>
 * @date 7/21/25
 */
@Component
public class FlowOperationInefficientDetailDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String getQueryName() {
        return "流程统计分析_流程行为分析_流程行为统计表";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "环节操作UUID", String.class);
        criteriaMetadata.add("title", "t.title", "流程标题", String.class);
        criteriaMetadata.add("name", "t.name", "流程定义名称", String.class);
        criteriaMetadata.add("id", "t.uuid", "流程定义ID", String.class);
        criteriaMetadata.add("startTime", "t.start_time", "流程开始时间", Date.class);
        criteriaMetadata.add("startUserName", "t.start_user_name", "流程发起人", String.class);
        criteriaMetadata.add("actionName", "t.action_name", "操作名称", String.class);
        criteriaMetadata.add("taskName", "t.task_name", "操作环节", String.class);
        criteriaMetadata.add("taskId", "t.task_id", "操作环节ID", String.class);
        criteriaMetadata.add("targetTaskName", "t.target_task_name", "目标环节", String.class);
        criteriaMetadata.add("operatorName", "t.operator_name", "操作人", String.class);
        criteriaMetadata.add("opinionText", "t.opinion_text", "操作意见", String.class);
        criteriaMetadata.add("operateTime", "t.operate_time", "操作时间", Date.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("listFlowInefficientDetailQuery", queryParams, QueryItem.class, context.getPagingInfo());
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
        if (!queryParams.containsKey("operationTypes")) {
            queryParams.put("operationTypes", Lists.newArrayList(WorkFlowOperation.ROLLBACK, WorkFlowOperation.TRANSFER
                    , WorkFlowOperation.COUNTER_SIGN, WorkFlowOperation.ADD_SIGN, WorkFlowOperation.HAND_OVER, WorkFlowOperation.GOTO_TASK));
        }
        Criterion criterion = FlowReportUtils.createFlowOperationCriterion(queryParams, workFlowSettings, true);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        // queryParams.put("actionCodes", getInefficientActionCodes(queryParams));
        return queryParams;
    }

    private List<Integer> getInefficientActionCodes(Map<String, Object> params) {
        if (MapUtils.isNotEmpty(params) && params.containsKey("actionCodes")) {
            return (List<Integer>) params.get("actionCodes");
        }
        List<Integer> actionCodes = Lists.newArrayList(ActionCode.GOTO_TASK.getCode(), ActionCode.HAND_OVER.getCode(),
                ActionCode.TRANSFER.getCode(), ActionCode.COUNTER_SIGN.getCode(), ActionCode.ADD_SIGN.getCode());
        actionCodes.addAll(WorkFlowOperation.getActionCodeOfRollback());
        return actionCodes;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

}
