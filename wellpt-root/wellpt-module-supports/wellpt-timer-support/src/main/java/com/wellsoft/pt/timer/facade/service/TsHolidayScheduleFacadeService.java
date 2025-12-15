/*
 * @(#)2021年5月24日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.timer.dto.TsHolidayPerYearScheduleCountDto;
import com.wellsoft.pt.timer.dto.TsHolidayScheduleDto;

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
public interface TsHolidayScheduleFacadeService extends Facade {

    /**
     * @return
     */
    List<TsHolidayPerYearScheduleCountDto> listAllYear();

    /**
     * @param year
     * @return
     */
    List<TsHolidayScheduleDto> listByYear(String year);

    /**
     * @param holidayDtos
     */
    void saveAllDtos(String year, List<TsHolidayScheduleDto> holidayDtos);

    /**
     * @param uuids
     */
    void deleteAll(List<String> uuids);

    /**
     * @param holidayUuid
     * @param string
     * @return
     */
    TsHolidayScheduleDto getByHolidayAndYear(String holidayUuid, String year);

}
