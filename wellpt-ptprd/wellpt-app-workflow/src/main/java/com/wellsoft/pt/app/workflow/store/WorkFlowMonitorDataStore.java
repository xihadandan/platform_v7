/*
 * @(#)Dec 26, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.pt.security.acl.support.AclPermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
public class WorkFlowMonitorDataStore extends WorkFlowDataStoreQuery {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "工作流程_监控(运行时)";
    }

    /**
     * @return
     */
    protected List<Permission> getPermissions() {
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(AclPermission.MONITOR);
        return permissions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.AbstractQueryInterface#getOrder()
     */
    @Override
    public int getOrder() {
        return 110;
    }

}
