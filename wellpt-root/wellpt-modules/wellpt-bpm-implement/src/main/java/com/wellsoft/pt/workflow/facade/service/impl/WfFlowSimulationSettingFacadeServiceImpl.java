/*
 * @(#)9/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.dto.WfFlowSimulationSettingDto;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationSettingEntity;
import com.wellsoft.pt.workflow.facade.service.WfFlowSimulationSettingFacadeService;
import com.wellsoft.pt.workflow.service.WfFlowSimulationSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class WfFlowSimulationSettingFacadeServiceImpl extends AbstractApiFacade implements WfFlowSimulationSettingFacadeService {

    @Autowired
    private WfFlowSimulationSettingService flowSimulationSettingService;

    /**
     * @param flowDefUuid
     * @return
     */
    @Override
    public WfFlowSimulationSettingDto getDtoByFlowDefUuid(String flowDefUuid) {
        WfFlowSimulationSettingDto dto = new WfFlowSimulationSettingDto();
        WfFlowSimulationSettingEntity entity = flowSimulationSettingService.getByFlowDefUuid(flowDefUuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * @param dto
     */
    @Override
    public void saveDto(WfFlowSimulationSettingDto dto) {
        WfFlowSimulationSettingEntity entity = flowSimulationSettingService.getByFlowDefUuid(dto.getFlowDefUuid());
        if (entity == null) {
            entity = new WfFlowSimulationSettingEntity();
        }
        BeanUtils.copyProperties(dto, entity, Entity.BASE_FIELDS);
        flowSimulationSettingService.save(entity);
    }

}
