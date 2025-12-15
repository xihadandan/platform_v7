/*
 * @(#)6/27/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.query.FlowInstanceCountByCategoryQueryItem;
import com.wellsoft.pt.app.workflow.report.query.FlowInstanceCountQueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowInstanceReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.NamedQueryCriteria;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/27/25.1	    zhulh		6/27/25		    Create
 * </pre>
 * @date 6/27/25
 */
@Service
@Transactional(readOnly = true)
public class FlowInstanceReportServiceImpl implements FlowInstanceReportService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private NativeDao nativeDao;

    @Override
    public List<FlowInstanceCountQueryItem> queryFlowInstanceCount(Map<String, Object> params) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "flowInstanceCountQuery");
        Criterion criterion = FlowReportUtils.createFlowInstanceCountCriterion(params, workFlowSettings);//createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("whereSql", whereSql);
        return flowInstanceService.listItemByNameSQLQuery("flowInstanceCountQuery", FlowInstanceCountQueryItem.class, params, new PagingInfo(0, Integer.MAX_VALUE));
    }

    @Override
    public List<FlowInstanceCountByCategoryQueryItem> queryFlowInstanceCountByCategory(Map<String, Object> params) {
        NamedQueryCriteria criteria = new NamedQueryCriteria(nativeDao, "flowInstanceCountByCategoryQuery");
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        Criterion criterion = FlowReportUtils.createFlowInstanceCountCriterion(params, workFlowSettings);// createCriterion(params);
        String whereSql = criterion.toSqlString(criteria);
        Map<String, Object> queryParams = criteria.getQueryParams();
        params.putAll(queryParams);
        params.put("whereSql", whereSql);
        return flowInstanceService.listItemByNameSQLQuery("flowInstanceCountByCategoryQuery", FlowInstanceCountByCategoryQueryItem.class, params, new PagingInfo(0, Integer.MAX_VALUE));
    }

    @Override
    public List<QueryItem> namedQuery(String queryName, Map<String, Object> queryParams, PagingInfo pagingInfo) {
        return this.nativeDao.namedQuery(queryName, queryParams, QueryItem.class, pagingInfo);
    }

    @Override
    public long queryFlowInstanceStartCount(Map<String, Object> params) {
        List<FlowInstanceCountQueryItem> queryItems = queryFlowInstanceCount(params);
        String flowState = Objects.toString(params.get("flowState"), StringUtils.EMPTY);
        long startCount = queryItems.stream().mapToLong(item -> {
            if (StringUtils.equals(flowState, "completed")) {
                if (BooleanUtils.isTrue(item.getCompleted())) {
                    return item.getCount();
                } else {
                    return 0L;
                }
            }
            if (StringUtils.equals(flowState, "uncompleted")) {
                if (BooleanUtils.isNotTrue(item.getCompleted())) {
                    return item.getCount();
                } else {
                    return 0L;
                }
            }
            return item.getCount();
        }).sum();
        return startCount;
    }


}
