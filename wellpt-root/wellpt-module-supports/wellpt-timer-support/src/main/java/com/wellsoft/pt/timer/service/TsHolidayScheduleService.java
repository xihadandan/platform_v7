/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.dao.TsHolidayScheduleDao;
import com.wellsoft.pt.timer.entity.TsHolidayScheduleEntity;
import com.wellsoft.pt.timer.query.TsHolidayPerYearScheduleCountQueryItem;
import com.wellsoft.pt.timer.query.TsHolidayScheduleQueryItem;
import com.wellsoft.pt.timer.support.TsHolidayUsedChecker;

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
 * 2021年4月7日.1	zhulh		2021年4月7日		Create
 * </pre>
 * @date 2021年4月7日
 */
public interface TsHolidayScheduleService
        extends JpaService<TsHolidayScheduleEntity, TsHolidayScheduleDao, String>, TsHolidayUsedChecker {

    /**
     * @param systemUnitId
     * @return
     */
    List<TsHolidayPerYearScheduleCountQueryItem> listHolidayPerYearScheduleCountBySystemUnitId(String systemUnitId);

    /**
     * @param year
     * @return
     */
    List<TsHolidayScheduleQueryItem> listByYear(String year);

    /**
     * @param holidayUuid
     * @param year
     * @return
     */
    TsHolidayScheduleEntity getByHolidayAndYear(String holidayUuid, String year);

}
