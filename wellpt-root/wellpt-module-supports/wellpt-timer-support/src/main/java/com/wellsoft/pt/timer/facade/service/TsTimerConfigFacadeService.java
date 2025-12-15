/*
 * @(#)2021年6月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service;

import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.timer.dto.TsTimerConfigDto;
import com.wellsoft.pt.timer.entity.TsTimerConfigEntity;

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
 * 2021年6月7日.1	zhulh		2021年6月7日		Create
 * </pre>
 * @date 2021年6月7日
 */
public interface TsTimerConfigFacadeService {

    /**
     * @param systemUnitIds
     * @return
     */
    List<TsTimerConfigEntity> listBySystemUnitIds(List<String> systemUnitIds);

    /**
     * @param system
     * @param tenant
     * @param excludedCategoryId
     * @return
     */
    List<TsTimerConfigEntity> listBySystemAndTenant(String system, String tenant, String excludedCategoryId);

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    TsTimerConfigDto getDto(String uuid);

    /**
     * 根据计时器配置ID获取计时器
     *
     * @param id
     * @return
     */
    TsTimerConfigDto getDtoById(String id);

    /**
     * @param timerConfigDto
     */
    String saveDto(TsTimerConfigDto timerConfigDto);

    /**
     * @param uuids
     */
    void deleteAll(List<String> uuids);

    /**
     * @param uuids
     * @return
     */
    ResultMessage isUsedByUuids(List<String> uuids);

}
