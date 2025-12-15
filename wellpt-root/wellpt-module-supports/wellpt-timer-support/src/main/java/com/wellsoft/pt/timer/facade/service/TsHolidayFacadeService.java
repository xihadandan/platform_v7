/*
 * @(#)2021年5月24日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.timer.dto.TsHolidayDto;

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
public interface TsHolidayFacadeService extends Facade {

    /**
     * 节假日管理按系统单位及名称查询
     *
     * @param name
     * @return
     */
    List<TsHolidayDto> getAllBySystemUnitIdsLikeName(String name);

    /**
     * 节假日管理按系统单位及名称、日期、标签查询
     *
     * @param keyword
     * @param tags
     * @return
     */
    List<TsHolidayDto> getAllBySystemUnitIdsLikeFields(String keyword, String tags);

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    TsHolidayDto getDto(String uuid);

    /**
     * @param uuid
     * @param year
     * @return
     */
    String getHolidayInstanceDate(String uuid, String year);

    /**
     * 如何描述该方法
     *
     * @param holidayDto
     */
    void saveDto(TsHolidayDto holidayDto);

    /**
     * 如何描述该方法
     *
     * @param uuids
     */
    void deleteAll(List<String> uuids);

    /**
     * @param uuids
     * @return
     */
    ResultMessage isUsedByUuids(List<String> uuids);

    /**
     * 初始系统内置节假日
     *
     * @param system
     * @param tenant
     */
    void initBySystemAndTenant(String system, String tenant);
}
