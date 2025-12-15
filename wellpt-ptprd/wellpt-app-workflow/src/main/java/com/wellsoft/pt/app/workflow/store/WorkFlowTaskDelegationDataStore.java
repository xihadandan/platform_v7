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
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.DataType;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 工作流程_委托数据
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
public class WorkFlowTaskDelegationDataStore extends AbstractDataStoreQueryInterface {

    private Object locked = new Object();
    private String projectionColumnClause;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "工作流程_委托数据";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("consignor", "t1.consignor", "委托人ID", String.class);
        criteriaMetadata.add("consignorName", "t1.consignor_name", "委托人名称", String.class);
        criteriaMetadata.add("trustee", "t1.trustee", "受托人ID", String.class);
        criteriaMetadata.add("trusteeName", "t1.trustee_name", "受托人名称", String.class);
        criteriaMetadata.add("fromTime", "t1.from_time", "开始时间", Date.class);
        criteriaMetadata.add("toTime", "t1.to_time", "结束时间", Date.class);
        criteriaMetadata.add("dueToTakeBackWork", "t1.due_to_take_back_work", "到期回收", DataType.B);
        criteriaMetadata.add("completionState", "t1.completion_state", "委托状态", DataType.B);
        criteriaMetadata.add("delegationTime", "t1.create_time", "委托时间", DataType.T);
        criteriaMetadata.add("flowInstUuid", "t1.flow_inst_uuid", "流程实例UUID", String.class);
        criteriaMetadata.add("taskInstUuid", "t1.task_inst_uuid", "环节实例UUID", DataType.S);
        criteriaMetadata.add("taskIdentityUuid", "t1.task_identity_uuid", "待办标识UUID", DataType.S);
        criteriaMetadata.add("flowTitle", "t2.title", "流程标题", DataType.S);
        criteriaMetadata.add("flowName", "t2.name", "流程名称", String.class);
        criteriaMetadata.add("flowDefId", "t2.id", "流程定义ID", String.class);
        criteriaMetadata.add("flowStartTime", "t2.start_time", "流程开始时间", DataType.T);
        criteriaMetadata.add("flowEndTime", "t2.end_time", "流程结束时间", DataType.T);
        criteriaMetadata.add("operatorName", "t4.operator_name", "操作人", String.class);
        criteriaMetadata.add("operateTaskName", "t4.operate_task_name", "操作环节名称", DataType.S);
        criteriaMetadata.add("operateTime", "t4.operate_time", "操作时间", Date.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.core.criteria.QueryContext)
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
        TaskDelegationQuery taskDelegationQuery = FlowEngine.getInstance().createQuery(TaskDelegationQuery.class);
//        if (queryParams.containsKey("showCompletionState")) {
//            taskDelegationQuery.completionState(0, 1, 2);
//        } else {
//            taskDelegationQuery.completionState(0, 1);
//        }
        taskDelegationQuery.completionState(0, 1, 2);
        taskDelegationQuery.setProperties(queryContext.getQueryParams());
        taskDelegationQuery.where(queryContext.getWhereSqlString(), queryContext.getQueryParams());
        return taskDelegationQuery;
    }

    /**
     * @param context
     */
    protected String getCriteriaMetadataProjection(QueryContext context) {
        if (StringUtils.isNotBlank(projectionColumnClause)) {
            return projectionColumnClause;
        }
        synchronized (locked) {
            if (StringUtils.isNotBlank(projectionColumnClause)) {
                return projectionColumnClause;
            }
            StringBuilder projection = new StringBuilder();
            CriteriaMetadata criteriaMetadata = context.getCriteriaMetadata();
            Iterator<String> columnIndexIt = Arrays.asList(criteriaMetadata.getColumnIndexs()).iterator();
            while (columnIndexIt.hasNext()) {
                String columnIndex = columnIndexIt.next();
                String columnName = criteriaMetadata.getMapColumnIndex(columnIndex);
                projection.append(columnName + " as " + columnIndex);
                if (columnIndexIt.hasNext()) {
                    projection.append(",");
                }
            }
            projectionColumnClause = projection.toString();
        }
        return projectionColumnClause;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#count(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return preQuery(queryContext).count();
    }

}
