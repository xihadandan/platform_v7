/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.preferences.facade.service.impl;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.preferences.dto.CdUserPreferencesDto;
import com.wellsoft.pt.basicdata.preferences.entity.CdUserPreferencesEntity;
import com.wellsoft.pt.basicdata.preferences.facade.service.CdUserPreferencesFacadeService;
import com.wellsoft.pt.basicdata.preferences.service.CdUserPreferencesService;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

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
 * 2020年12月8日.1	zhulh		2020年12月8日		Create
 * </pre>
 * @date 2020年12月8日
 */
@Service
public class CdUserPreferencesFacadeServiceImpl implements CdUserPreferencesFacadeService {

    @Autowired
    private CdUserPreferencesService cdUserPreferencesService;

    /**
     * (non-Javadoc)
     */
    @Override
    public String save(String moduleId, String functionId, String userId, String dataKey, String dataValue,
                       String remark) {
        CdUserPreferencesEntity entity = getEntity(moduleId, functionId, userId, dataKey);
        if (entity == null) {
            entity = new CdUserPreferencesEntity();
        }
        entity.setModuleId(moduleId);
        entity.setFunctionId(functionId);
        entity.setUserId(userId);
        entity.setDataKey(dataKey);
        entity.setDataValue(dataValue);
        entity.setRemark(remark);
        cdUserPreferencesService.save(entity);
        cacheEvictDataValue(userId);
        return entity.getUuid();
    }

    //更新主题的缓存
    public void cacheEvictDataValue(String userId) {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        Cache cache = cacheManager.getCache(CacheName.DEFAULT);
        cache.put(userId, getDataValue(ModuleID.THEME.getValue(), "", userId, ModuleID.THEME.getValue()));
    }

    @Override
    public String getThemeDataValue(String userId) {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        Cache cache = cacheManager.getCache(CacheName.DEFAULT);
        com.wellsoft.pt.cache.Cache.ValueWrapper valueWrapper = cache.get(userId);
        String value = "";
        if (valueWrapper == null) {
            value = getDataValue(ModuleID.THEME.getValue(), "", userId, ModuleID.THEME.getValue());
            cache.put(userId, value);
        } else {
            value = String.valueOf(valueWrapper.get());
        }
        return value;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.preferences.facade.service.CdUserPreferencesFacadeService#getDataValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getDataValue(String moduleId, String functionId, String userId, String dataKey) {
        CdUserPreferencesEntity entity = getEntity(moduleId, functionId, userId, dataKey);
        if (entity != null) {
            return entity.getDataValue();
        }
        return null;
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.preferences.facade.service.CdUserPreferencesFacadeService#get(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public CdUserPreferencesDto get(String moduleId, String functionId, String userId, String dataKey) {
        CdUserPreferencesDto dto = new CdUserPreferencesDto();
        CdUserPreferencesEntity entity = getEntity(moduleId, functionId, userId, dataKey);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.preferences.facade.service.CdUserPreferencesFacadeService#delete(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void delete(String moduleId, String functionId, String userId, String dataKey) {
        CdUserPreferencesEntity entity = getEntity(moduleId, functionId, userId, dataKey);
        if (entity != null) {
            cdUserPreferencesService.delete(entity);
        }
    }

    /**
     * @param moduleId
     * @param functionId
     * @param userId
     * @param dataKey
     */
    private CdUserPreferencesEntity getEntity(String moduleId, String functionId, String userId, String dataKey) {
        CdUserPreferencesEntity entity = new CdUserPreferencesEntity();
        entity.setModuleId(moduleId);
        if (StringUtils.isNotBlank(functionId)) {
            entity.setFunctionId(functionId);
        }
        entity.setUserId(userId);
        entity.setDataKey(dataKey);
        List<CdUserPreferencesEntity> entities = cdUserPreferencesService.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

}
