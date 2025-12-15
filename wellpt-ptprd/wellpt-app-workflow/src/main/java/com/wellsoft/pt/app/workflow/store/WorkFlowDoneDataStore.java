/*
 * @(#)2017-01-06 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.bpm.engine.query.api.TaskQuery;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.datasource.DatabaseType;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 工作流已办
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-06.1	zhulh		2017-01-06		Create
 * </pre>
 * @date 2017-01-06
 */
@Component
public class WorkFlowDoneDataStore extends WorkFlowDataStoreQuery {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.app.workflow.store.WorkFlowDataStoreQuery#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "工作流程_已办";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = super.initCriteriaMetadata(context);
        criteriaMetadata.add("doneOperateTime", "t4.done_operate_time", "办理时间", Date.class);
        criteriaMetadata.add("delegationDoneActionCode", "t6.delegation_done_action_code", "委托办理操作标识", Integer.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.store.WorkFlowDataStoreQuery#preQuery(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    protected TaskQuery preQuery(QueryContext context) {
        TaskQuery taskQuery = super.preQuery(context);
        Map<String, Object> queryParams = context.getQueryParams();
        // queryParams.put("isActive", true);

        String whereSql = getWhereSql(context);

        taskQuery.setProperties(queryParams);
        taskQuery.where(whereSql, queryParams);

        // 查询列
        taskQuery.projection(getCriteriaMetadataProjection(context));
        // 左关联
        StringBuilder sb = new StringBuilder();
        // 办理时间
        sb.append(" left join (");
        if (DatabaseType.MySQL5.getName().equalsIgnoreCase(Config.getValue("database.type"))) {
            sb.append("	select max(t.create_time) as done_operate_time, t.flow_inst_uuid as flow_inst_uuid");
            sb.append("		from wf_task_operation t");
            sb.append("		where t.assignee = :currentUserId and t.action_code not in(9, 10, 11, 12)");
            sb.append("		group by t.flow_inst_uuid");
        } else {
            sb.append("	select * from (");
            sb.append("	    select t.create_time as done_operate_time, t.flow_inst_uuid as flow_inst_uuid, ");
            sb.append("	        row_number() over(partition by t.flow_inst_uuid, t.assignee order by t.create_time desc) as row_num");
            sb.append("	    from wf_task_operation t where t.assignee = :currentUserId and t.action_code not in(9, 10, 11, 12)");
            sb.append(" ) where row_num = 1");
        }
        sb.append("	) t4");
        sb.append("	on t1.flow_inst_uuid = t4.flow_inst_uuid");
        // 委托办理标识
        sb.append(" left join (");
        sb.append("	select distinct t.action_code as delegation_done_action_code, t.flow_inst_uuid as flow_inst_uuid");
        sb.append("	    from wf_task_operation t where  t.assignee = :currentUserId and t.action_code = 27");
        sb.append("	) t6");
        sb.append("	on t1.flow_inst_uuid = t6.flow_inst_uuid");
        taskQuery.join(sb.toString());
        return taskQuery;
    }

    protected String getWhereSql(QueryContext context) {
        StringBuilder whereSql = new StringBuilder(context.getWhereSqlString());
        // whereSql.append(" and (t1.end_time is null and t2.is_active = :isActive and t2.end_time is null)");
        whereSql.append(" and (t1.end_time is null and t2.end_time is null)");
        return whereSql.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.store.WorkFlowDataStoreQuery#getSids()
     */
    @Override
    protected List<String> getSids() {
        List<String> sids = new ArrayList<String>();
        sids.add(SpringSecurityUtils.getCurrentUserId());
        return sids;
    }

    /**
     * @return
     */
    protected List<Permission> getPermissions() {
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(AclPermission.DONE);
        return permissions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.store.WorkFlowDataStoreQuery#query(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        List<QueryItem> queryItems = super.query(context);
//        // 处理办理环节、办理时间
//        if (CollectionUtils.isNotEmpty(queryItems)) {
//            List<String> flowInstUuids = new ArrayList<String>();
//            for (QueryItem queryItem : queryItems) {
//                String flowInstUuid = queryItem.getString(QueryItem.getKey("flowInstUuid"));
//                if (StringUtils.isNotBlank(flowInstUuid)) {
//                    flowInstUuids.add(flowInstUuid);
//                }
//            }
//            if (CollectionUtils.isNotEmpty(flowInstUuids)) {
//                TaskOperationQuery taskOperationQuery = FlowEngine.getInstance().createQuery(TaskOperationQuery.class);
//                taskOperationQuery.operatorId(SpringSecurityUtils.getCurrentUserId());
//                taskOperationQuery.flowInstUuids(flowInstUuids);
//                List<TaskOperationQueryItem> doneItems = taskOperationQuery.list();
//                Map<String, TaskOperationQueryItem> doneMap = new HashMap<String, TaskOperationQueryItem>();
//                for (TaskOperationQueryItem doneItem : doneItems) {
//                    String flowInstUuid = doneItem.getFlowInstUuid();
//                    if (StringUtils.isNotBlank(flowInstUuid) && !doneMap.containsKey(flowInstUuid)) {
//                        doneMap.put(flowInstUuid, doneItem);
//                    }
//                }
//                for (QueryItem queryItem : queryItems) {
//                    String flowInstUuid = queryItem.getString(QueryItem.getKey("flowInstUuid"));
//                    if (doneMap.containsKey(flowInstUuid)) {
//                        TaskOperationQueryItem doneItem = doneMap.get(flowInstUuid);
//                        // 办理环节
//                        queryItem.put("doneTaskName", doneItem.getTaskName());
//                        // 办理时间
//                        queryItem.put("doneCreateTime", doneItem.getCreateTime());
//                    }
//                }
//            }
//        }
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.AbstractQueryInterface#getOrder()
     */
    @Override
    public int getOrder() {
        return 20;
    }

}
