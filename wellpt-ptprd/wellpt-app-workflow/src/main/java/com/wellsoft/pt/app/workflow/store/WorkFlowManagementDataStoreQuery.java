/*
 * @(#)Jan 6, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.query.api.TaskQuery;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSuspensionState;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.acls.model.Permission;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 流程查询
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 6, 2017.1	zhulh		Jan 6, 2017		Create
 * </pre>
 * @date Jan 6, 2017
 */
public abstract class WorkFlowManagementDataStoreQuery extends WorkFlowDataStoreQuery {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.store.WorkFlowDataStoreQuery#preQuery(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    protected TaskQuery preQuery(QueryContext context) {
        List<String> sids = getSids();
        // 查询参数
        Map<String, Object> queryParams = context.getQueryParams();
        if (queryParams == null) {
            queryParams = Maps.newHashMap();
        }
        Set<String> orgIds = Sets.newHashSet();
        orgIds.addAll(sids);
        // 管理类型
        List<Integer> managementTypes = getManagementTypes();
        queryParams.put("orgIds", orgIds);
        queryParams.put("managementTypes", managementTypes);
        // queryParams.put("suspensionState", WorkFlowSuspensionState.Normal);
        queryParams.put("suspensionStates", Lists.newArrayList(WorkFlowSuspensionState.Normal, WorkFlowSuspensionState.LOGIC_SUSPEND));
        String runtimePermissionSql = StringUtils.EMPTY;
        List<Permission> runtimePermissions = getRuntimePermissions();
        if (CollectionUtils.isNotEmpty(runtimePermissions)) {
            runtimePermissionSql = getRuntimePermissionSql(runtimePermissions);
        }
        String whereSql = null;
        if (StringUtils.isNotBlank(runtimePermissionSql)) {
            whereSql = "(t1.suspension_state in (:suspensionStates) and (" + runtimePermissionSql
                    + " or exists (select 1 from wf_flow_management wfm where t3.uuid = wfm.flow_def_uuid and wfm.type in (:managementTypes) and wfm.org_id in (:orgIds)))";
        } else {
            whereSql = "(t1.suspension_state in (:suspensionStates) and exists (select 1 from wf_flow_management wfm where t3.uuid = wfm.flow_def_uuid and wfm.type in (:managementTypes) and wfm.org_id in (:orgIds))";
        }
        if (StringUtils.isNotBlank(context.getWhereSqlString())) {
            whereSql += " and " + context.getWhereSqlString();
        }
        whereSql += ")";

        // 添加接口参数查询
        addInterfaceParamQuery(queryParams, context);
        TaskQuery taskQuery = FlowEngine.getInstance().createQuery(TaskQuery.class);
        if (StringUtils.isNotBlank((String) queryParams.get("joinTableClause"))) {
            taskQuery.projection(getCriteriaMetadataProjection(context));
        }
        // 查询监控数据
        taskQuery.setProperties(queryParams);
        taskQuery.where(whereSql, queryParams);
        return taskQuery;
    }

    /**
     * @param permissions
     * @return
     */
    private String getRuntimePermissionSql(List<Permission> permissions) {
        AclTaskService aclTaskService = ApplicationContextHolder.getBean(AclTaskService.class);
        String authWhere = aclTaskService.getAuthWhere(permissions);
        return "exists (select 1 from acl_task_entry a1 where t1.uuid = a1.object_id_identity " + authWhere + " and a1.sid in (:orgIds))";
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    protected abstract List<Integer> getManagementTypes();

    /**
     * @return
     */
    protected List<Permission> getRuntimePermissions() {
        return Collections.emptyList();
    }

}
