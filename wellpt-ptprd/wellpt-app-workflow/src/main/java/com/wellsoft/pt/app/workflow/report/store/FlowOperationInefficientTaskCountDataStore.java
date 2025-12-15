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
public class FlowOperationInefficientTaskCountDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String getQueryName() {
        return "流程统计分析_流程行为分析_流程低效操作环节信息";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("taskId", "t.task_id", "环节ID", String.class);
        criteriaMetadata.add("taskName", "t.task_name", "环节名称", String.class);
        criteriaMetadata.add("count", "t.count", "操作次数", Long.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        return context.getNativeDao().namedQuery("listFlowInefficientTaskCountQuery", queryParams, QueryItem.class, context.getPagingInfo());
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
        Criterion criterion = FlowReportUtils.createFlowOperationCriterion(queryParams, workFlowSettings, true);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        queryParams.put("actionCodes", getInefficientActionCodes(queryParams));
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
