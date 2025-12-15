/*
 * @(#)2/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.app.dto.AppThemeCheckUpdateDto;
import com.wellsoft.pt.app.dto.AppThemeDefinitionDto;

import java.util.List;

/**
 * Description: 应用主题门面服务接口类
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
public interface AppThemeDefinitionFacadeService extends Facade {
    /**
     * 根据UUID获取主题定义
     *
     * @param uuid
     * @return
     */
    AppThemeDefinitionDto getDto(String uuid);


    /**
     * 根据UUID获取主题定义
     *
     * @param uuid
     * @return
     */
    String getDefinitionJsonByUuid(String uuid);

    /**
     * 保存主题定义
     *
     * @param themeDefinitionDto
     */
    void saveDto(AppThemeDefinitionDto themeDefinitionDto);

    /**
     * 保存主题定义JSON
     *
     * @param uuid
     * @param definitionJson
     */
    void saveDefinitionJson(String uuid, String definitionJson);

    /**
     * 删除主题定义
     *
     * @param uuids
     */
    void deleteAll(List<String> uuids);

    /**
     * 根据应用于列出主题
     *
     * @param applyTo
     * @return
     */
    List<AppThemeDefinitionDto> listByApplyTo(String applyTo);

    /**
     * 获取更新的主题
     *
     * @param applyTo
     * @param checkUpdateDtos
     * @return
     */
    List<AppThemeDefinitionDto> listByApplyToWithUpdatedTheme(String applyTo, List<AppThemeCheckUpdateDto> checkUpdateDtos);
}
