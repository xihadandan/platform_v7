/*
 * @(#)Jan 6, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.pt.bpm.engine.support.ManagementType;
import com.wellsoft.pt.security.acl.support.AclPermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 工作流程_督办(管理 + 运行时)
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
public class WorkFlowSuperviseAllDataStoreQuery extends WorkFlowManagementDataStoreQuery {

    /**
     * @return
     */
    @Override
    public String getQueryName() {
        return "工作流程_督办(管理 + 运行时)";
    }

    /**
     * @return
     */
    @Override
    protected List<Integer> getManagementTypes() {
        List<Integer> types = new ArrayList<Integer>();
        types.add(ManagementType.SUPERVISE);
        return types;
    }

    /**
     * @return
     */
    @Override
    protected List<Permission> getRuntimePermissions() {
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(AclPermission.SUPERVISE);
        return permissions;
    }

    /**
     * @return
     */
    @Override
    public int getOrder() {
        return 100;
    }

}
