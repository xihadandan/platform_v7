/*
 * @(#)Dec 26, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.pt.bpm.engine.support.ManagementType;
import com.wellsoft.pt.security.acl.support.AclPermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 工作流程_监控(管理 + 运行时)
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
public class WorkFlowMonitorAllDataStoreQuery extends WorkFlowManagementDataStoreQuery {

    /**
     * @return
     */
    @Override
    public String getQueryName() {
        return "工作流程_监控(管理 + 运行时)";
    }

    /**
     * @return
     */
    @Override
    protected List<Integer> getManagementTypes() {
        List<Integer> types = new ArrayList<Integer>();
        types.add(ManagementType.MONITOR);
        return types;
    }

    /**
     * @return
     */
    @Override
    protected List<Permission> getRuntimePermissions() {
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(AclPermission.MONITOR);
        return permissions;
    }

    //    @Override
//    protected List<String> getSids() {
//        List<String> sids = new ArrayList<String>();
//        // 用户相关的组织ID
////        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
//        String userId = SpringSecurityUtils.getCurrentUserId();
////        Set<String> orgIds = orgApiFacade.getUserOrgIds(userId);
//        Set<String> orgIds = workflowOrgService.getUserRelatedIds(userId);
//        sids.addAll(orgIds);
//        sids.add(userId);
//        return sids;
//    }

    /**
     * @return
     */
    @Override
    public int getOrder() {
        return 120;
    }

}
