/*
 * @(#)2017-01-06 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.bpm.engine.query.api.TaskQuery;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.datasource.DatabaseType;
import com.wellsoft.pt.security.acl.support.AclPermission;
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
public class WorkFlowCopyToDataStore extends WorkFlowDataStoreQuery {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.app.workflow.store.WorkFlowDataStoreQuery#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "工作流程_抄送我的";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.store.WorkFlowDataStoreQuery#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = super.initCriteriaMetadata(context);
        criteriaMetadata.add("copyToTime", "t4.copy_to_time", "抄送时间", Date.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.store.WorkFlowDataStoreQuery#preQuery(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    protected TaskQuery preQuery(QueryContext context) {
        TaskQuery taskQuery = super.preQuery(context);
        // 查询列
        taskQuery.projection(getCriteriaMetadataProjection(context));
        // 左关联
        StringBuilder sb = new StringBuilder();
        sb.append("left join (");
        if (DatabaseType.MySQL5.getName().equalsIgnoreCase(Config.getValue("database.type"))) {
            sb.append("	select max(t.create_time) as copy_to_time, t.flow_inst_uuid as flow_inst_uuid");
            sb.append("		from wf_task_operation t");
            sb.append("		where (t.action_code = 11 or (t.user_id is not null and t.copy_user_id is not null)) and (t.copy_user_id like concat('%', :userId, '%') or t.copy_user_id in(:userSids))");
            sb.append("		group by t.flow_inst_uuid");
        } else {
            sb.append("	select * from (");
            sb.append("	    select t.create_time as copy_to_time, t.flow_inst_uuid as flow_inst_uuid, ");
            sb.append("	        row_number() over(partition by t.flow_inst_uuid order by t.create_time desc) as row_num");
            sb.append("	    from wf_task_operation t where (t.action_code = 11 or (t.user_id is not null and t.copy_user_id is not null)) " +
                    "and (t.copy_user_id like '%' || :userId || '%' or t.copy_user_id in(:copyToUserSids))");
            sb.append(" ) where row_num = 1");
        }
        sb.append("	) t4");
        sb.append("	on t1.flow_inst_uuid = t4.flow_inst_uuid");
        taskQuery.join(sb.toString());
        Map<String, Object> queryParams = context.getQueryParams();
        queryParams.put("copyToUserSids", PermissionGranularityUtils.getCurrentUserSids());
        taskQuery.setProperties(queryParams);
        return taskQuery;
    }

//    /**
//     * (non-Javadoc)
//     *
//     * @see com.wellsoft.pt.app.workflow.store.WorkFlowDataStoreQuery#getSids()
//     */
//    @Override
//    protected List<String> getSids() {
//        List<String> sids = new ArrayList<String>();
//        sids.add(SpringSecurityUtils.getCurrentUserId());
//        return sids;
//    }

    /**
     * @return
     */
    protected List<Permission> getPermissions() {
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(AclPermission.FLAG_READ);
        permissions.add(AclPermission.UNREAD);
        return permissions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.AbstractQueryInterface#getOrder()
     */
    @Override
    public int getOrder() {
        return 80;
    }

}
