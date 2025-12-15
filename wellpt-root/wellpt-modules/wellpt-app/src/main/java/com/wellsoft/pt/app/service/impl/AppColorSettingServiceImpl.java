/*
 * @(#)2022-04-15 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.wellsoft.pt.app.dao.AppColorSettingDao;
import com.wellsoft.pt.app.dto.AppColorSettingClassifyDto;
import com.wellsoft.pt.app.dto.AppColorSettingClassifyItemDto;
import com.wellsoft.pt.app.entity.AppColorSettingEntity;
import com.wellsoft.pt.app.service.AppColorSettingService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表APP_COLOR_SETTING的service服务接口实现类
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-04-15.1	shenhb		2022-04-15		Create
 * </pre>
 * @date 2022-04-15
 */
@Service
public class AppColorSettingServiceImpl extends AbstractJpaServiceImpl<AppColorSettingEntity, AppColorSettingDao, String> implements AppColorSettingService {

    @Transactional
    @Override
    public void saveBean(AppColorSettingClassifyDto appColorSettingSaveDto) {
        String moduleCode = appColorSettingSaveDto.getModuleCode();
        String type = appColorSettingSaveDto.getType();

        // 先删除
        this.deleteBean(moduleCode, type);

        //
        List<AppColorSettingEntity> entityList = new ArrayList<>();
        List<AppColorSettingClassifyItemDto> valueList = appColorSettingSaveDto.getValueList();
        if (CollectionUtils.isNotEmpty(valueList)) {
            for (AppColorSettingClassifyItemDto itemDto : valueList) {
                AppColorSettingEntity settingEntity = new AppColorSettingEntity();
                settingEntity.setModuleCode(moduleCode);
                settingEntity.setType(type);
                settingEntity.setId(itemDto.getId());
                settingEntity.setName(itemDto.getName());
                settingEntity.setColor(itemDto.getColor());
                entityList.add(settingEntity);
            }
        }

        this.saveAll(entityList);
    }

    @Override
    public List<AppColorSettingClassifyDto> getAllBean() {
        Map<String, AppColorSettingClassifyDto> map = new HashMap<>();

        List<AppColorSettingEntity> appColorSettingEntities = this.listAll();
        for (AppColorSettingEntity appColorSettingEntity : appColorSettingEntities) {
            String key = appColorSettingEntity.getModuleCode() + "_" + appColorSettingEntity.getType();
            if (!map.containsKey(key)) {
                map.put(key, new AppColorSettingClassifyDto());
            }
            AppColorSettingClassifyDto value = map.get(key);
            value.setModuleCode(appColorSettingEntity.getModuleCode());
            value.setType(appColorSettingEntity.getType());
            List<AppColorSettingClassifyItemDto> valueList = value.getValueList();
            valueList.add(new AppColorSettingClassifyItemDto(appColorSettingEntity.getId(), appColorSettingEntity.getName(), appColorSettingEntity.getColor()));
            value.setValueList(valueList);
        }

        return new ArrayList<>(map.values());
    }

    @Override
    public AppColorSettingClassifyDto getBean(String moduleCode, String type) {
        AppColorSettingClassifyDto result = new AppColorSettingClassifyDto();
        if (StringUtils.isNotBlank(moduleCode) && StringUtils.isNotBlank(type)) {

            result.setModuleCode(moduleCode);
            result.setType(type);

            AppColorSettingEntity entity = new AppColorSettingEntity();
            entity.setModuleCode(moduleCode);
            entity.setType(type);
            List<AppColorSettingEntity> appColorSettingEntityList = this.listByEntity(entity);
            List<AppColorSettingClassifyItemDto> classifyItemDtoList = new ArrayList<>();
            for (AppColorSettingEntity appColorSettingEntity : appColorSettingEntityList) {
                AppColorSettingClassifyItemDto itemDto = new AppColorSettingClassifyItemDto();
                itemDto.setColor(appColorSettingEntity.getColor());
                itemDto.setId(appColorSettingEntity.getId());
                itemDto.setName(appColorSettingEntity.getName());
                classifyItemDtoList.add(itemDto);
            }

            result.setValueList(classifyItemDtoList);
        }

        return result;
    }

    @Transactional
    @Override
    public void deleteBean(String moduleCode, String type) {
        if (StringUtils.isNotBlank(moduleCode) && StringUtils.isNotBlank(type)) {
            AppColorSettingEntity entity = new AppColorSettingEntity();
            entity.setModuleCode(moduleCode);
            entity.setType(type);

            List<AppColorSettingEntity> appColorSettingEntityList = this.listByEntity(entity);
            for (AppColorSettingEntity appColorSettingEntity : appColorSettingEntityList) {
                this.delete(appColorSettingEntity);
            }

        }
    }


}
