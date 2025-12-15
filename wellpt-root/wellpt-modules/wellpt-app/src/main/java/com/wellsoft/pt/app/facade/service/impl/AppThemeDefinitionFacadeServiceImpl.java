/*
 * @(#)2/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.app.dto.AppThemeCheckUpdateDto;
import com.wellsoft.pt.app.dto.AppThemeDefinitionDto;
import com.wellsoft.pt.app.entity.AppThemeDefinitionEntity;
import com.wellsoft.pt.app.entity.AppThemeDefinitionHisEntity;
import com.wellsoft.pt.app.entity.AppThemeDefinitionJsonEntity;
import com.wellsoft.pt.app.facade.service.AppThemeDefinitionFacadeService;
import com.wellsoft.pt.app.service.AppThemeDefinitionHisService;
import com.wellsoft.pt.app.service.AppThemeDefinitionJsonService;
import com.wellsoft.pt.app.service.AppThemeDefinitionService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description: 应用主题门面服务实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2/27/23.1	zhulh		2/27/23		Create
 * </pre>
 * @date 2/27/23
 */
@Service
public class AppThemeDefinitionFacadeServiceImpl extends AbstractApiFacade implements AppThemeDefinitionFacadeService {
    @Autowired
    private AppThemeDefinitionService themeDefinitionService;

    @Autowired
    private AppThemeDefinitionJsonService themeDefinitionJsonService;

    @Autowired
    private AppThemeDefinitionHisService themeDefinitionHisService;

    /**
     * 根据UUID获取主题定义
     *
     * @param uuid
     * @return
     */
    @Override
    public AppThemeDefinitionDto getDto(String uuid) {
        AppThemeDefinitionDto dto = new AppThemeDefinitionDto();
        AppThemeDefinitionEntity entity = themeDefinitionService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 根据UUID获取主题配置
     *
     * @param uuid
     * @return
     */
    @Override
    public String getDefinitionJsonByUuid(String uuid) {
        AppThemeDefinitionEntity definitionEntity = themeDefinitionService.getOne(uuid);
        String definitionJsonUuid = definitionEntity.getDefinitionJsonUuid();
        if (StringUtils.isBlank(definitionJsonUuid)) {
            return StringUtils.EMPTY;
        }

        AppThemeDefinitionJsonEntity definitionJsonEntity = themeDefinitionJsonService.getOne(definitionJsonUuid);
        if (definitionJsonEntity == null) {
            return StringUtils.EMPTY;
        }
        return definitionJsonEntity.getDefinitionJson();
    }

    /**
     * 保存主题定义
     *
     * @param themeDefinitionDto
     */
    @Override
    public void saveDto(AppThemeDefinitionDto themeDefinitionDto) {
        AppThemeDefinitionEntity entity = new AppThemeDefinitionEntity();
        if (StringUtils.isNotBlank(themeDefinitionDto.getUuid())) {
            entity = themeDefinitionService.getOne(themeDefinitionDto.getUuid());
        } else {
            // ID唯一性判断
            if (themeDefinitionService.countById(themeDefinitionDto.getId()) > 0) {
                throw new RuntimeException("已经存在ID为[" + themeDefinitionDto.getId() + "]的主题定义!");
            }
        }
        BeanUtils.copyProperties(themeDefinitionDto, entity, IdEntity.BASE_FIELDS);
        themeDefinitionService.save(entity);
    }

    /**
     * 保存主题定义JSON
     *
     * @param uuid
     * @param definitionJson
     */
    @Override
    @Transactional
    public void saveDefinitionJson(String uuid, String definitionJson) {
        AppThemeDefinitionEntity definitionEntity = themeDefinitionService.getOne(uuid);
        AppThemeDefinitionJsonEntity definitionJsonEntity = null;
        AppThemeDefinitionHisEntity definitionHisEntity = new AppThemeDefinitionHisEntity();
        String definitionJsonUuid = definitionEntity.getDefinitionJsonUuid();
        if (StringUtils.isBlank(definitionJsonUuid)) {
            definitionJsonEntity = new AppThemeDefinitionJsonEntity();
        } else {
            definitionJsonEntity = themeDefinitionJsonService.getOne(definitionJsonUuid);
        }
        definitionJsonEntity.setDefinitionJson(definitionJson);
        themeDefinitionJsonService.save(definitionJsonEntity);

        // 设置主题定义对应的定义JSON UUID
        if (StringUtils.isBlank(definitionJsonUuid)) {
            definitionEntity.setDefinitionJsonUuid(definitionJsonEntity.getUuid());
        }
        themeDefinitionService.save(definitionEntity);

        // 主题定义历史记录
        BeanUtils.copyProperties(definitionEntity, definitionHisEntity, IdEntity.BASE_FIELDS);
        definitionHisEntity.setThemeDefUuid(definitionEntity.getUuid());
        definitionHisEntity.setDefinitionJson(definitionJsonEntity.getDefinitionJson());
        themeDefinitionHisService.save(definitionHisEntity);
    }

    /**
     * 删除主题定义
     *
     * @param uuids
     */
    @Override
    @Transactional
    public void deleteAll(List<String> uuids) {
        List<AppThemeDefinitionEntity> themeDefinitionEntities = themeDefinitionService.listByUuids(uuids);
        themeDefinitionEntities.forEach(entity ->
                themeDefinitionJsonService.delete(entity.getDefinitionJsonUuid()));
        themeDefinitionService.deleteByUuids(uuids);
    }

    /**
     * 根据应用于列出主题
     *
     * @param applyTo
     * @return
     */
    @Override
    public List<AppThemeDefinitionDto> listByApplyTo(String applyTo) {
        if (StringUtils.isBlank(applyTo)) {
            return Collections.emptyList();
        }
        List<AppThemeDefinitionEntity> definitionEntities = themeDefinitionService.listByApplyTo(applyTo);
        return BeanUtils.copyCollection(definitionEntities, AppThemeDefinitionDto.class);
    }

    /**
     * 获取更新的主题
     *
     * @param checkUpdateDtos
     * @return
     */
    @Override
    public List<AppThemeDefinitionDto> listByApplyToWithUpdatedTheme(String applyTo, List<AppThemeCheckUpdateDto> checkUpdateDtos) {
        List<AppThemeDefinitionEntity> definitionEntities = themeDefinitionService.listByApplyTo(applyTo);

        List<AppThemeDefinitionEntity> retDefinitionEntities = Lists.newArrayList();
        List<String> definitionJsonUuids = Lists.newArrayList();
        Map<String, AppThemeCheckUpdateDto> checkUpdateDtoMap = ConvertUtils.convertElementToMap(checkUpdateDtos, "id");
        for (AppThemeDefinitionEntity entity : definitionEntities) {
            AppThemeCheckUpdateDto checkUpdateDto = checkUpdateDtoMap.get(entity.getId());
            if (checkUpdateDto == null || checkUpdateDto.getRecVer() < entity.getRecVer()) {
                if (StringUtils.isNotBlank(entity.getDefinitionJsonUuid())) {
                    retDefinitionEntities.add(entity);
                    definitionJsonUuids.add(entity.getDefinitionJsonUuid());
                }
            }
        }

        if (CollectionUtils.isEmpty(definitionJsonUuids)) {
            return Collections.emptyList();
        }

        List<AppThemeDefinitionDto> dtos = Lists.newArrayList();
        Map<String, AppThemeDefinitionEntity> retDefinitionEntityMap = ConvertUtils.convertElementToMap(retDefinitionEntities, "definitionJsonUuid");
        List<AppThemeDefinitionJsonEntity> themeDefinitionJsonEntities = themeDefinitionJsonService.listByUuids(definitionJsonUuids);
        for (AppThemeDefinitionJsonEntity themeDefinitionJsonEntity : themeDefinitionJsonEntities) {
            AppThemeDefinitionEntity entity = retDefinitionEntityMap.get(themeDefinitionJsonEntity.getUuid());
            AppThemeDefinitionDto dto = new AppThemeDefinitionDto();
            BeanUtils.copyProperties(entity, dto);
            dto.setDefinitionJson(themeDefinitionJsonEntity.getDefinitionJson());
            dtos.add(dto);
        }
        return dtos;
    }

}
