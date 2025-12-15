/*
 * @(#)2021年7月14日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dto.WfFlowDefinitionCleanupConfigDto;
import com.wellsoft.pt.workflow.entity.WfFlowDefinitionCleanupConfigEntity;
import com.wellsoft.pt.workflow.facade.service.WfFlowDefinitionCleanupConfigFacadeService;
import com.wellsoft.pt.workflow.service.WfFlowDefinitionCleanupConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 流程定义定时清理配置对外服务实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月14日.1	zhulh		2021年7月14日		Create
 * </pre>
 * @date 2021年7月14日
 */
@Service
public class WfFlowDefinitionCleanupConfigFacadeServiceImpl extends AbstractApiFacade
        implements WfFlowDefinitionCleanupConfigFacadeService {

    @Autowired
    private WfFlowDefinitionCleanupConfigService flowDefinitionCleanupConfigService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.WfFlowDefinitionCleanupConfigFacadeService#getDto()
     */
    @Override
    public WfFlowDefinitionCleanupConfigDto getDto() {
        WfFlowDefinitionCleanupConfigDto dto = new WfFlowDefinitionCleanupConfigDto();
        WfFlowDefinitionCleanupConfigEntity entity = flowDefinitionCleanupConfigService
                .getBySystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.WfFlowDefinitionCleanupConfigFacadeService#listAllDto()
     */
    @Override
    public List<WfFlowDefinitionCleanupConfigDto> listAllDto() {
        List<WfFlowDefinitionCleanupConfigEntity> entities = flowDefinitionCleanupConfigService.listAll();
        return BeanUtils.copyCollection(entities, WfFlowDefinitionCleanupConfigDto.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.WfFlowDefinitionCleanupConfigFacadeService#saveDto(com.wellsoft.pt.workflow.dto.WfFlowDefinitionCleanupConfigDto)
     */
    @Override
    public void saveDto(WfFlowDefinitionCleanupConfigDto dto) {
        WfFlowDefinitionCleanupConfigEntity entity = null;
        if (StringUtils.isNotBlank(dto.getUuid())) {
            entity = flowDefinitionCleanupConfigService.getOne(dto.getUuid());
        } else {
            entity = new WfFlowDefinitionCleanupConfigEntity();
        }
        BeanUtils.copyProperties(dto, entity, IdEntity.BASE_FIELDS);
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        flowDefinitionCleanupConfigService.save(entity);
    }

}
