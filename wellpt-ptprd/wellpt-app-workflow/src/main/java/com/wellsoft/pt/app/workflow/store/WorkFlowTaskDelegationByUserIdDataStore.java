/*
 * @(#)2017年4月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.query.api.TaskDelegationQuery;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 工作流程_委托数据__指定用户
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年4月18日.1	zhulh		2017年4月18日		Create
 * </pre>
 * @date 2017年4月18日
 */
@Component
public class WorkFlowTaskDelegationByUserIdDataStore extends WorkFlowTaskDelegationDataStore {
    private Object locked = new Object();
    private String projectionColumnClause;

    /**
     * (non-Javadoc)
     */
    @Override
    public String getQueryName() {
        return "工作流程_委托数据__指定用户";
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {

        TaskDelegationQuery taskDelegationQuery = preQuery(queryContext);
        taskDelegationQuery.order(queryContext.getOrderString());
        taskDelegationQuery.setFirstResult(queryContext.getPagingInfo().getFirst());
        taskDelegationQuery.setMaxResults(queryContext.getPagingInfo().getPageSize());
        return taskDelegationQuery.list(QueryItem.class);
    }

    /**
     * @param queryContext
     * @return
     */
    protected TaskDelegationQuery preQuery(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        String handoverUserId = (String) queryContext.getQueryParams().get("handoverUserId");
        TaskDelegationQuery taskDelegationQuery = FlowEngine.getInstance().createQuery(TaskDelegationQuery.class);
        if (queryParams.containsKey("showCompletionState")) {
            taskDelegationQuery.completionState(0, 1, 2);
        } else {
            taskDelegationQuery.completionState(0, 1);
        }
        taskDelegationQuery.setProperties(queryContext.getQueryParams());
        String whereSql = queryContext.getWhereSqlString() + " and (t1.consignor ='" + handoverUserId
                + "' or t1.trustee='" + handoverUserId + "') ";
        taskDelegationQuery.where(whereSql, queryContext.getQueryParams());
        return taskDelegationQuery;
    }

}
