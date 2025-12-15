/*
 * @(#)Dec 26, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.pt.bpm.engine.query.api.TaskQuery;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description: 工作流待办
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 26, 2016.1	zhulh		Dec 26, 2016		Create
 * </pre>
 * @date Dec 26, 2016
 */
@Component
public class WorkFlowAttentionDataStore extends WorkFlowDataStoreQuery {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "工作流程_关注";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.store.WorkFlowDataStoreQuery#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = super.initCriteriaMetadata(context);
        criteriaMetadata.add("attentionTime", "t4.attention_time", "关注时间", Date.class);
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
        sb.append("	select t.create_time        as attention_time, t.object_id_identity as task_inst_uuid");
        sb.append("		from acl_task_entry t ");
        sb.append("		where t.attention_auth = 1 and t.sid = :userId");
        sb.append("	) t4");
        sb.append("	on t4.task_inst_uuid = t1.uuid");
        taskQuery.join(sb.toString());
        return taskQuery;
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
        permissions.add(AclPermission.ATTENTION);
        return permissions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.AbstractQueryInterface#getOrder()
     */
    @Override
    public int getOrder() {
        return 50;
    }

}
