/*
 * @(#)7/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FowOperationReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.TableCriteria;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
 * 7/18/25.1	    zhulh		7/18/25		    Create
 * </pre>
 * @date 7/18/25
 */
@Service
@Transactional(readOnly = true)
public class FowOperationReportServiceImpl implements FowOperationReportService {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private NativeDao nativeDao;

    @Override
    public long queryInefficientCount(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowOperationCriterion(params, workFlowSettings, params.containsKey("operationTypes"));//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("actionCodes", getInefficientActionCodes(params));
        return nativeDao.countByNamedQuery("getInefficientCountQuery", params);
    }

//    @Override
//    public QueryItem getMaxInefficientCountByDefinition(Map<String, Object> params) {
//        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
//        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
//        Criterion criterion = FlowReportUtils.createFlowOperationCriterion(params, workFlowSettings, true);//createCriterion(params);
//        String whereSql = criterion.toSqlString(criteria);
//        whereSql = StringUtils.replace(whereSql, "this_.", "");
//        Map<String, Object> queryParams = criteria.getQueryParams();
//        params.putAll(queryParams);
//        params.put("flowInstWhereSql", whereSql);
//        params.put("actionCodes", getInefficientActionCodes(params));
//        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowInefficientCountQuery", params, new PagingInfo(1, 1));
//        QueryItem queryItem = CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0) : null;
//        return queryItem;
//    }

    public static List<Integer> getInefficientActionCodes(Map<String, Object> params) {
        if (MapUtils.isNotEmpty(params) && params.containsKey("actionCodes")) {
            return (List<Integer>) params.get("actionCodes");
        }
        if (params.containsKey("operationTypes")) {
            List<String> operationTypes = (List<String>) params.get("operationTypes");
            List<Integer> actionCodes = Lists.newArrayList();
            if (operationTypes.contains(WorkFlowOperation.ROLLBACK)) {
                actionCodes.addAll(WorkFlowOperation.getActionCodeOfRollback());
            }
            if (operationTypes.contains(WorkFlowOperation.TRANSFER)) {
                actionCodes.add(ActionCode.TRANSFER.getCode());
            }
            if (operationTypes.contains(WorkFlowOperation.COUNTER_SIGN)) {
                actionCodes.add(ActionCode.COUNTER_SIGN.getCode());
            }
            if (operationTypes.contains(WorkFlowOperation.ADD_SIGN)) {
                actionCodes.add(ActionCode.ADD_SIGN.getCode());
            }
            if (operationTypes.contains(WorkFlowOperation.HAND_OVER)) {
                actionCodes.add(ActionCode.HAND_OVER.getCode());
            }
            if (operationTypes.contains(WorkFlowOperation.GOTO_TASK)) {
                actionCodes.add(ActionCode.GOTO_TASK.getCode());
            }
            if (CollectionUtils.isEmpty(actionCodes)) {
                actionCodes.add(-1);
            }
            return actionCodes;
        }

        List<Integer> actionCodes = Lists.newArrayList(ActionCode.GOTO_TASK.getCode(), ActionCode.HAND_OVER.getCode(),
                ActionCode.TRANSFER.getCode(), ActionCode.COUNTER_SIGN.getCode(), ActionCode.ADD_SIGN.getCode());
        actionCodes.addAll(WorkFlowOperation.getActionCodeOfRollback());
        return actionCodes;
    }

    @Override
    public List<QueryItem> listInefficientPercent(Map<String, Object> params, PagingInfo pagingInfo) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowOperationCriterion(params, workFlowSettings, false);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("actionCodes", getInefficientActionCodes(params));
        return flowInstanceService.listQueryItemByNameSQLQuery("listFlowInefficientPercentQuery", params, pagingInfo);
    }

    @Override
    public long queryInefficientTaskCount(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        TableCriteria criteria = new TableCriteria(this.nativeDao, "wf_flow_instance");
        Criterion criterion = FlowReportUtils.createFlowOperationCriterion(params, workFlowSettings, true);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        whereSql = StringUtils.replace(whereSql, "this_.", "");
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("flowInstWhereSql", whereSql);
        params.put("actionCodes", getInefficientActionCodes(params));
        List<QueryItem> queryItems = flowInstanceService.listQueryItemByNameSQLQuery("listFlowInefficientTaskCountQuery", params, null);
        return queryItems.stream().mapToLong(item -> item.getLong("count")).sum();
    }

}
