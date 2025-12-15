/*
 * @(#)2019年6月10日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppPageResourceDao;
import com.wellsoft.pt.app.dto.AppPageResourceDto;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.entity.AppPageResourceEntity;
import com.wellsoft.pt.app.service.AppFunctionService;
import com.wellsoft.pt.app.service.AppPageResourceService;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月10日.1	zhulh		2019年6月10日		Create
 * </pre>
 * @date 2019年6月10日
 */
@Service
public class AppPageResourceServiceImpl extends
        AbstractJpaServiceImpl<AppPageResourceEntity, AppPageResourceDao, String> implements AppPageResourceService {

    private static final String REMOVE_BY_APP_PAGE_UUID = "delete from AppPageResourceEntity t where t.appPageUuid = :appPageUuid";

    private static final String REMOVE_BY_APP_FUNCTION_OF_PAGE_UUID = "delete from AppFunction t1 where t1.type in(:functionTypes) and exists (select t2.uuid from AppPageResourceEntity t2 where t2.appPageUuid = :appPageUuid and t1.uuid = t2.appFunctionUuid)";

    private static final String REMOVE_BY_APP_PAGE_UUID_AND_CONFIG_TYPE = "delete from AppPageResourceEntity t where t.appPageUuid = :appPageUuid and t.configType = :configType";

    private static final String GET_PROTECTED_UUIDS_BY_APP_PAGE_UUID_AND_CONFIG_TYPE = "select t.uuid from AppPageResourceEntity t where t.appPageUuid = :appPageUuid and t.configType = :configType and t.isProtected = :isProtected";

    @Autowired
    private AppFunctionService appFunctionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageResourceService#listByEntity(com.wellsoft.pt.app.entity.AppPageResourceEntity)
     */
    @Override
    public List<AppPageResourceEntity> listByEntity(AppPageResourceEntity entity) {
        return this.dao.listByEntity(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageResourceService#removeByAppPageUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByAppPageUuid(String appPageUuid) {
        Map<String, Object> values = Maps.newHashMap();
        List<String> functionTypes = Lists.newArrayList();
        functionTypes.add(AppFunctionType.AppWidgetDefinition);
        functionTypes.add(AppFunctionType.AppWidgetFunctionElement);
        values.put("functionTypes", functionTypes);
        values.put("appPageUuid", appPageUuid);
        // 删除页面资源
        this.dao.deleteByHQL(REMOVE_BY_APP_FUNCTION_OF_PAGE_UUID, values);
        // 删除组件元素功能
        this.dao.deleteByHQL(REMOVE_BY_APP_PAGE_UUID, values);
        this.dao.getSession().flush();
        this.dao.getSession().clear();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageResourceService#removeByAppPageUuidAndConfigType(java.lang.String, java.lang.String)
     */
    @Override
    public void removeByAppPageUuidAndConfigType(String appPageUuid, String configType) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("appPageUuid", appPageUuid);
        values.put("configType", configType);
        this.dao.deleteByHQL(REMOVE_BY_APP_PAGE_UUID_AND_CONFIG_TYPE, values);
        this.dao.getSession().flush();
        this.dao.getSession().clear();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageResourceService#getProtectedUuidsByAppPageUuidAndConfigType(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getProtectedUuidsByAppPageUuidAndConfigType(String appPageUuid, String configType) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("appPageUuid", appPageUuid);
        values.put("configType", configType);
        values.put("isProtected", true);
        return this.dao.listCharSequenceByHQL(GET_PROTECTED_UUIDS_BY_APP_PAGE_UUID_AND_CONFIG_TYPE, values);
    }

    @Override
    public List<AppPageResourceEntity> listByAppPageUuid(String uuid) {
        return this.dao.listByFieldEqValue("appPageUuid", uuid);
    }

    @Override
    public List<AppPageResourceDto> getAppPageResourcesAndFunction(String appPageUuid) {
        List<AppPageResourceEntity> pageResourceEntities = listByAppPageUuid(appPageUuid);
        List<AppPageResourceDto> dtoList = Lists.newArrayListWithCapacity(pageResourceEntities.size());
        if (pageResourceEntities != null) {
            List<String> appFunUuids = Lists.newArrayList();
            Map<String, AppPageResourceDto> functionResourceMap = Maps.newHashMap();
            for (AppPageResourceEntity resource : pageResourceEntities) {
                AppPageResourceDto dto = new AppPageResourceDto();
                BeanUtils.copyProperties(resource, dto);
                if (dto.getAppFunctionUuid() != null) {
                    appFunUuids.add(dto.getAppFunctionUuid());
                    functionResourceMap.put(dto.getAppFunctionUuid(), dto);
                }
                dtoList.add(dto);
            }
            if (!appFunUuids.isEmpty()) {
                List<AppFunction> appFunctions = appFunctionService.listByUuids(appFunUuids);
                if (CollectionUtils.isNotEmpty(appFunctions)) {
                    for (AppFunction af : appFunctions) {
                        if (functionResourceMap.containsKey(af.getUuid())) {
                            functionResourceMap.get(af.getUuid()).setAppFunction(af);
                        }
                    }
                }
            }

        }
        return dtoList;
    }

    @Override
    public List<AppPageResourceEntity> getAppPageResourcesByPageUuid(List<String> appPageUuids) {
        if (CollectionUtils.isEmpty(appPageUuids)) {
            return Collections.EMPTY_LIST;
        }
        return dao.listByFieldInValues("appPageUuid", appPageUuids);
    }

    @Override
    public List<String> getProtectedIdsByAppPageUuidAndConfigType(String appPageUuid, String configType) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("appPageUuid", appPageUuid);
        values.put("configType", configType);
        values.put("isProtected", true);
        return this.dao.listCharSequenceByHQL("select t.id from AppPageResourceEntity t where t.appPageUuid = :appPageUuid and t.configType = :configType and t.isProtected = :isProtected", values);
    }

    @Override
    public AppPageResourceEntity getByIdAndAppPageUuid(String id, String appPageUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("appPageUuid", appPageUuid);
        values.put("id", id);
        List<AppPageResourceEntity> resourceEntities = this.dao.listByHQL("from AppPageResourceEntity t where t.appPageUuid = :appPageUuid and t.id=:id", values);
        return CollectionUtils.isNotEmpty(resourceEntities) ? resourceEntities.get(0) : null;
    }


}
