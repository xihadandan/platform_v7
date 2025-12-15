/*
 * @(#)Dec 26, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.wellsoft.pt.bpm.engine.support.ManagementType;
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
public class WorkFlowMonitorManagementDataStoreQuery extends WorkFlowManagementDataStoreQuery {

    /**
     * (non-Javadoc)
     */
    @Override
    public String getQueryName() {
        return "工作流程_监控(管理)";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.store.WorkFlowManagementDataStoreQuery#getManagementTypes()
     */
    @Override
    protected List<Integer> getManagementTypes() {
        List<Integer> types = new ArrayList<Integer>();
        types.add(ManagementType.MONITOR);
        return types;
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
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.AbstractQueryInterface#getOrder()
     */
    @Override
    public int getOrder() {
        return 120;
    }

}
