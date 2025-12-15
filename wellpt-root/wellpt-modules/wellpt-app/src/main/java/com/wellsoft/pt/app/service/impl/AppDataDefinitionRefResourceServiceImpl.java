/*
 * @(#)8/18/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppDataDefinitionRefResourceDao;
import com.wellsoft.pt.app.dto.AppDataDefinitionRefResourceDto;
import com.wellsoft.pt.app.entity.AppDataDefinitionRefResourceEntity;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.service.AppDataDefinitionRefResourceService;
import com.wellsoft.pt.app.service.AppFunctionService;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
 * 8/18/23.1	zhulh		8/18/23		Create
 * </pre>
 * @date 8/18/23
 */
@Service
public class AppDataDefinitionRefResourceServiceImpl extends AbstractJpaServiceImpl<AppDataDefinitionRefResourceEntity,
        AppDataDefinitionRefResourceDao, Long> implements AppDataDefinitionRefResourceService {
    @Autowired
    private AppFunctionService appFunctionService;

    /***
     * 保存数据定义引用资源
     *
     * @param refResourceDtos
     * @param dataDefUuid
     */
    @Override
    @Transactional
    public void saveRefResources(String dataDefUuid, List<AppDataDefinitionRefResourceDto> refResourceDtos) {
        // 删除已存在的引用关系
        this.deleteByDataDefUuid(dataDefUuid);

        // 保存功能
        Map<String, List<FunctionElement>> functionElementsMap = Maps.newHashMap();
        for (AppDataDefinitionRefResourceDto dto : refResourceDtos) {
            functionElementsMap.put(dto.getItemId(), dto.getFunctionElements());
        }
        Map<String, List<AppFunction>> widgetFunctionMap = appFunctionService.saveFunctionElements(functionElementsMap);

        // 保存引用功能
        List<AppDataDefinitionRefResourceEntity> entities = Lists.newArrayList();
        for (AppDataDefinitionRefResourceDto dto : refResourceDtos) {
            List<AppFunction> appFunctions = widgetFunctionMap.get(dto.getItemId());
            appFunctions.forEach(appFunction -> {
                AppDataDefinitionRefResourceEntity entity = new AppDataDefinitionRefResourceEntity();
                BeanUtils.copyProperties(dto, entity);
                entity.setAppFunctionUuid(appFunction.getUuid());
                entities.add(entity);
            });
        }
        this.dao.saveAll(entities);
    }

    /**
     * 根据数据定义UUID删除引用资源
     *
     * @param dataDefUuid
     */
    @Override
    public void deleteByDataDefUuid(String dataDefUuid) {
        Assert.hasLength(dataDefUuid, "数据定义UUID不能为空！");

        String hql = "delete from AppDataDefinitionRefResourceEntity t where t.dataDefUuid = :dataDefUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataDefUuid", dataDefUuid);
        this.dao.deleteByHQL(hql, params);
    }

    @Override
    public List<AppFunction> getDataRefFunctions(String dataDefUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataDefUuid", dataDefUuid);
        return appFunctionService.listByHQL("from AppFunction a where exists (" +
                "   select 1 from AppDataDefinitionRefResourceEntity t where t.appFunctionUuid = a.uuid and t.dataDefUuid=:dataDefUuid" +
                ")", params);
    }
}
