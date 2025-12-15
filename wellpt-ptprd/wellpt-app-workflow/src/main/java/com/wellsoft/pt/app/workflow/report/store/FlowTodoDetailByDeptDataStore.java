/*
 * @(#)7/24/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

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
 * 7/24/25.1	    zhulh		7/24/25		    Create
 * </pre>
 * @date 7/24/25
 */
@Component
public class FlowTodoDetailByDeptDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private FlowTodoReportService flowTodoReportService;

    @Override
    public String getQueryName() {
        return "流程统计分析_待办流程分析按部门_待办详情列表";
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
        criteriaMetadata.add("isOverDue", "t.is_over_due", "是否逾期", Integer.class);
        criteriaMetadata.add("taskId", "t.task_id", "待办环节Id", String.class);
        criteriaMetadata.add("taskName", "t.task_name", "待办环节", String.class);
        criteriaMetadata.add("todoUserName", "t.todo_user_name", "待办人", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        queryParams.put("deptQuerySql", FlowReportUtils.getDeptQuerySql(context.getNativeDao(), queryParams));
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("listFlowTodoDetailByDeptQuery", queryParams, QueryItem.class, context.getPagingInfo());
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
