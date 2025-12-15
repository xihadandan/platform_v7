/*
 * @(#)6/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.facade.service.impl;

import com.wellsoft.context.jdbc.entity.SysEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.context.util.reflection.ServiceInvokeUtils;
import com.wellsoft.pt.fulltext.dto.FulltextModelDto;
import com.wellsoft.pt.fulltext.entity.FulltextModelEntity;
import com.wellsoft.pt.fulltext.facade.service.FulltextModelFacadeService;
import com.wellsoft.pt.fulltext.service.FulltextCategoryService;
import com.wellsoft.pt.fulltext.service.FulltextModelService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/16/25.1	    zhulh		6/16/25		    Create
 * </pre>
 * @date 6/16/25
 */
@Service
public class FulltextModelFacadeServiceImpl extends AbstractApiFacade implements FulltextModelFacadeService {

    @Autowired
    private FulltextModelService fulltextModelService;

    @Autowired
    private FulltextCategoryService fulltextCategoryService;

    @Override
    public Long saveDto(FulltextModelDto dto) {
        FulltextModelEntity entity = null;
        if (dto.getUuid() == null) {
            entity = new FulltextModelEntity();
        } else {
            entity = fulltextModelService.getOne(dto.getUuid());
        }
        BeanUtils.copyProperties(dto, entity, SysEntity.BASE_FIELDS);
        entity.setSystem(RequestSystemContextPathResolver.system());
        entity.setTenant(SpringSecurityUtils.getCurrentTenantId());

        fulltextModelService.save(entity);
        return entity.getUuid();
    }

    @Override
    @Transactional
    public void saveAllDto(List<FulltextModelDto> dtos) {
        dtos.forEach(dto -> {
            this.saveDto(dto);
        });
    }

    @Override
    public FulltextModelDto getDto(Long uuid) {
        FulltextModelDto dto = new FulltextModelDto();
        FulltextModelEntity entity = fulltextModelService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    @Override
    public List<FulltextModelDto> listByModuleId(String moduleId) {
        if (StringUtils.isBlank(moduleId)) {
            return Collections.emptyList();
        }

        List<FulltextModelEntity> entities = fulltextModelService.listByModuleId(moduleId);
        return BeanUtils.copyCollection(entities, FulltextModelDto.class);
    }

    @Override
    public List<FulltextModelDto> listByCategoryUuid(Long categoryUuid) {
        if (categoryUuid == null) {
            return Collections.emptyList();
        }

        List<FulltextModelEntity> entities = fulltextModelService.listByCategoryUuid(categoryUuid);
        return BeanUtils.copyCollection(entities, FulltextModelDto.class);
    }

    @Override
    public void deleteByUuid(Long uuid) {
        fulltextModelService.delete(uuid);
    }

    @Override
    public List<Long> getAllCategoryUuidsByDataModelUuid(Long dataModelUuid) {
        List<FulltextModelEntity> fulltextModelEntities = fulltextModelService.listByDataModelUuid(dataModelUuid);
        List<Long> categoryUuids = fulltextModelEntities.stream().map(FulltextModelEntity::getCategoryUuid).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(categoryUuids)) {
            List<Long> childrenUuids = fulltextCategoryService.listUuidByParentUuids(categoryUuids);
            while (CollectionUtils.isNotEmpty(childrenUuids)) {
                categoryUuids.addAll(childrenUuids);
                childrenUuids = fulltextCategoryService.listUuidByParentUuids(childrenUuids);
            }
        }
        return categoryUuids;
    }

    @Override
    public List<FulltextModelDto> listByDataModelUuid(Long dataModelUuid) {
        List<FulltextModelEntity> fulltextModelEntities = fulltextModelService.listByDataModelUuid(dataModelUuid);
        return BeanUtils.copyCollection(fulltextModelEntities, FulltextModelDto.class);
    }

    @Override
    public List<Long> getAllCategoryUuidsByParentCategoryUuids(List<Long> parentCategoryUuids) {
        if (CollectionUtils.isEmpty(parentCategoryUuids)) {
            return Collections.emptyList();
        }

        List<Long> categoryUuids = Lists.newArrayList(parentCategoryUuids.iterator());
        List<Long> childrenUuids = fulltextCategoryService.listUuidByParentUuids(categoryUuids);
        while (CollectionUtils.isNotEmpty(childrenUuids)) {
            categoryUuids.addAll(childrenUuids);
            childrenUuids = fulltextCategoryService.listUuidByParentUuids(childrenUuids);
        }
        return categoryUuids;
    }

    @Override
    public String getDataModeUuidByDataModelId(String dataModelId) {
        Object dataModelEntity = ServiceInvokeUtils.invoke("dataModelService.getById", new Class[]{String.class}, new Object[]{dataModelId});
        return dataModelEntity == null ? null : (ReflectionUtils.invokeGetterMethod(dataModelEntity, "uuid") + StringUtils.EMPTY);
    }

    @Override
    public FulltextModelDto getByDataModelUuidAndSystem(Long dataModelUuid, String system) {
        FulltextModelDto dto = new FulltextModelDto();
        FulltextModelEntity entity = fulltextModelService.getByDataModelUuidAndSystem(dataModelUuid, system);
        if (entity == null && StringUtils.isNotBlank(system)) {
            entity = fulltextModelService.getByDataModelUuidAndSystem(dataModelUuid, null);
        }
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

}
