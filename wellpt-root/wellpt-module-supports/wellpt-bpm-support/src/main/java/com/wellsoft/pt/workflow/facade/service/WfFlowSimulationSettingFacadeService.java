/*
 * @(#)9/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.workflow.dto.WfFlowSimulationSettingDto;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 9/29/24.1	    zhulh		9/29/24		    Create
 * </pre>
 * @date 9/29/24
 */
public interface WfFlowSimulationSettingFacadeService extends Facade {

    /**
     * @param flowDefUuid
     * @return
     */
    WfFlowSimulationSettingDto getDtoByFlowDefUuid(String flowDefUuid);

    /**
     * @param dto
     */
    void saveDto(WfFlowSimulationSettingDto dto);

}
