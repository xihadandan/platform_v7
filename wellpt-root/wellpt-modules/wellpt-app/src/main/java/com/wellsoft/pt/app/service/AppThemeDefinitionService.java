/*
 * @(#)2/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppThemeDefinitionDao;
import com.wellsoft.pt.app.entity.AppThemeDefinitionEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 2/27/23.1	zhulh		2/27/23		Create
 * </pre>
 * @date 2/27/23
 */
public interface AppThemeDefinitionService extends JpaService<AppThemeDefinitionEntity, AppThemeDefinitionDao, String> {
    /**
     * 根据主题ID获取数量
     *
     * @param id
     * @return
     */
    Long countById(String id);

    /**
     * 根据应用于列出主题
     *
     * @param applyTo
     * @return
     */
    List<AppThemeDefinitionEntity> listByApplyTo(String applyTo);

    /**
     * 根据主题ID列表获取启用的主题
     *
     * @param ids
     * @return
     */
    List<AppThemeDefinitionEntity> listEnabledByIds(List<String> ids);

    /**
     * 根据主题定义JSON获取主题定义
     *
     * @param definitionJsonUuid
     * @return
     */
    AppThemeDefinitionEntity getByDefinitionJsonUuid(String definitionJsonUuid);
}
