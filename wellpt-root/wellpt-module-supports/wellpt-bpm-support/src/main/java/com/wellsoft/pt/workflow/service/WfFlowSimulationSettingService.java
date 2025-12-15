/*
 * @(#)9/23/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.WfFlowSimulationSettingDao;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationSettingEntity;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 9/23/24.1	    zhulh		9/23/24		    Create
 * </pre>
 * @date 9/23/24
 */
public interface WfFlowSimulationSettingService extends JpaService<WfFlowSimulationSettingEntity, WfFlowSimulationSettingDao, Long> {

    /**
     * 根据流程定义UUID获取仿真设置
     *
     * @param flowDefUuid
     * @return
     */
    WfFlowSimulationSettingEntity getByFlowDefUuid(String flowDefUuid);
    
}
