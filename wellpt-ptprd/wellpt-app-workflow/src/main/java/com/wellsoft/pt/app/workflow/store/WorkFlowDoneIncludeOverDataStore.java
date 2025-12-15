/*
 * @(#)2017-01-06 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 工作流已办(包含办结)
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
public class WorkFlowDoneIncludeOverDataStore extends WorkFlowDoneDataStore {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.app.workflow.store.WorkFlowDataStoreQuery#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "工作流程_已办(包含办结)";
    }

    @Override
    protected String getWhereSql(QueryContext context) {
        StringBuilder whereSql = new StringBuilder(context.getWhereSqlString());
        // whereSql.append(" and (t1.end_time is null and t2.is_active = :isActive and t2.end_time is null)");
        whereSql.append(" and ((t1.end_time is null and t2.end_time is null) or (t2.is_active = 0 and t2.end_time is not null))");
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
     * @see com.wellsoft.pt.jpa.criteria.AbstractQueryInterface#getOrder()
     */
    @Override
    public int getOrder() {
        return 20;
    }

}
