/*
 * @(#)2021年5月24日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.timer.dto.TsTimerCategoryDto;

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
 * 2021年5月24日.1	zhulh		2021年5月24日		Create
 * </pre>
 * @date 2021年5月24日
 */
public interface TsTimerCategoryFacadeService extends Facade {

    /**
     * @param name
     * @return
     */
    List<TsTimerCategoryDto> getAllBySystemUnitIdsLikeName(String name);

    /**
     * 根据UUID获取计时服务分类
     *
     * @param uuid
     * @return
     */
    TsTimerCategoryDto getDto(String uuid);

    /**
     * @param id
     * @return
     */
    TsTimerCategoryDto getById(String id);

    /**
     * 保存计时服务分类
     *
     * @param timerCategoryDto
     */
    String saveDto(TsTimerCategoryDto timerCategoryDto);

    /**
     * 删除计时服务分类
     *
     * @param uuids
     */
    void deleteAll(List<String> uuids);

    /**
     * @param uuids
     * @return
     */
    ResultMessage isUsedByUuids(List<String> uuids);

}
