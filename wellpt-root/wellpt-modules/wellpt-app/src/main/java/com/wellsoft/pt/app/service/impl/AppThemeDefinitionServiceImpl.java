/*
 * @(#)2/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppThemeDefinitionDao;
import com.wellsoft.pt.app.entity.AppThemeDefinitionEntity;
import com.wellsoft.pt.app.service.AppThemeDefinitionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
 * 2/27/23.1	zhulh		2/27/23		Create
 * </pre>
 * @date 2/27/23
 */
@Service
public class AppThemeDefinitionServiceImpl extends AbstractJpaServiceImpl<AppThemeDefinitionEntity, AppThemeDefinitionDao, String>
        implements AppThemeDefinitionService {

    @Autowired
    private AppThemeDefinitionDao themeDefinitionDao;

    /**
     * 根据主题ID获取数量
     *
     * @param id
     * @return
     */
    @Override
    public Long countById(String id) {
        return themeDefinitionDao.countById(id);
    }

    /**
     * 根据应用于列出主题
     *
     * @param applyTo
     * @return
     */
    @Override
    public List<AppThemeDefinitionEntity> listByApplyTo(String applyTo) {
        AppThemeDefinitionEntity entity = new AppThemeDefinitionEntity();
        entity.setApplyTo(applyTo);
        return themeDefinitionDao.listByEntity(entity);
    }

    /**
     * 根据主题ID列表获取主题
     *
     * @param ids
     * @return
     */
    @Override
    public List<AppThemeDefinitionEntity> listEnabledByIds(List<String> ids) {
        String hql = "from AppThemeDefinitionEntity t where t.id in(:ids) and t.enabled = :enabled";
        Map<String, Object> values = Maps.newHashMap();
        values.put("ids", ids);
        values.put("enabled", true);
        return themeDefinitionDao.listByHQL(hql, values);
    }

    /**
     * 根据主题定义JSON获取主题定义
     *
     * @param definitionJsonUuid
     * @return
     */
    @Override
    public AppThemeDefinitionEntity getByDefinitionJsonUuid(String definitionJsonUuid) {
        List<AppThemeDefinitionEntity> themeDefinitionEntities = themeDefinitionDao.listByFieldEqValue("definitionJsonUuid", definitionJsonUuid);
        if (CollectionUtils.isNotEmpty(themeDefinitionEntities)) {
            return themeDefinitionEntities.get(0);
        }
        return null;
    }
}
