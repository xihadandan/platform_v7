/*
 * @(#)4/24/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dto.WfFlowSettingDto;
import com.wellsoft.pt.workflow.entity.WfFlowSettingEntity;
import com.wellsoft.pt.workflow.facade.service.WfFlowSettingFacadeService;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 4/24/24.1	zhulh		4/24/24		Create
 * </pre>
 * @date 4/24/24
 */
@Service
public class WfFlowSettingFacadeServiceImpl extends AbstractApiFacade implements WfFlowSettingFacadeService {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public void initBySystemAndTenant(String system, String tenant) {
        long count = flowSettingService.countBySystemAndTenant(system, tenant);
        if (count > 0) {
            return;
        }

        List<WfFlowSettingEntity> flowSettingEntities = flowSettingService.listBySystemAndTenant(null, tenant);
        List<WfFlowSettingEntity> entities = Lists.newArrayList();
        for (WfFlowSettingEntity flowSettingEntity : flowSettingEntities) {
            WfFlowSettingEntity entity = new WfFlowSettingEntity();
            BeanUtils.copyProperties(flowSettingEntity, entity, JpaEntity.BASE_FIELDS);
            entity.setSystem(system);
            entity.setTenant(tenant);
            entities.add(entity);
        }
        flowSettingService.saveAll(entities);
    }

    /**
     * 根据UUID获取流程设置
     *
     * @param uuid
     * @return
     */
    @Override
    public WfFlowSettingDto getDto(Long uuid) {
        WfFlowSettingDto dto = new WfFlowSettingDto();
        WfFlowSettingEntity entity = flowSettingService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * @param attrKey
     * @param system
     * @param tenant
     * @return
     */
    @Override
    public WfFlowSettingDto getDtoByAttrKeyAndSystemAndTenant(String attrKey, String system, String tenant) {
        WfFlowSettingDto dto = new WfFlowSettingDto();
        WfFlowSettingEntity entity = flowSettingService.getByAttrKeyAndSystemAndTenant(attrKey, system, tenant);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * @param system
     * @param tenant
     * @return
     */
    @Override
    public List<WfFlowSettingDto> listBySystemAndTenant(String system, String tenant) {
        List<WfFlowSettingEntity> flowSettingEntities = flowSettingService.listBySystemAndTenant(system, tenant);
        return BeanUtils.copyCollection(flowSettingEntities, WfFlowSettingDto.class);
    }

    /**
     * 保存流程设置
     *
     * @param dto
     */
    @Override
    public void saveDto(WfFlowSettingDto dto) {
        WfFlowSettingEntity entity = null;
        if (dto.getUuid() != null) {
            entity = this.flowSettingService.getOne(dto.getUuid());
        } else {
            entity = this.flowSettingService.getByAttrKeyAndSystemAndTenant(dto.getAttrKey(),
                    RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId());
            if (entity == null) {
                entity = new WfFlowSettingEntity();
            }
            dto.setSystem(RequestSystemContextPathResolver.system());
            dto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        BeanUtils.copyProperties(dto, entity, JpaEntity.BASE_FIELDS);
        flowSettingService.save(entity);
        flowSettingService.clearFlowSettingsCache();
    }

    /**
     * 批量保存流程设置
     *
     * @param dtos
     */
    @Override
    @Transactional
    public void saveDtos(List<WfFlowSettingDto> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return;
        }
        dtos.forEach(dto -> saveDto(dto));
        flowSettingService.clearFlowSettingsCache();
    }

}
