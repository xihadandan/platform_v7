/*
 * @(#)Jan 6, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.google.common.collect.Lists;
import com.wellsoft.pt.bpm.engine.support.ManagementType;
import com.wellsoft.pt.security.acl.support.AclPermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 工作流程_查询(管理 + 运行时)
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
@Component
public class WorkFlowViewerAllDataStoreQuery extends WorkFlowManagementDataStoreQuery {

    /**
     * @return
     */
    @Override
    public String getQueryName() {
        return "工作流程_查询(管理 + 运行时)";
    }

    /**
     * @return
     */
    @Override
    protected List<Integer> getManagementTypes() {
        List<Integer> types = new ArrayList<Integer>();
        types.add(ManagementType.SUPERVISE);
        types.add(ManagementType.MONITOR);
        types.add(ManagementType.READ);
        return types;
    }

    @Override
    protected List<Permission> getRuntimePermissions() {
        List<Permission> permissions = Lists.newArrayList();
        permissions.add(AclPermission.READ);
        permissions.add(AclPermission.TODO);
        permissions.add(AclPermission.DONE);
        permissions.add(AclPermission.UNREAD);
        permissions.add(AclPermission.FLAG_READ);
        permissions.add(AclPermission.ATTENTION);
        permissions.add(AclPermission.SUPERVISE);
        permissions.add(AclPermission.MONITOR);
        return permissions;
    }

    /**
     * @return
     */
    @Override
    public int getOrder() {
        return 140;
    }

}
