/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.dao.TsHolidayInstanceDao;
import com.wellsoft.pt.timer.entity.TsHolidayInstanceEntity;

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
public interface TsHolidayInstanceService extends JpaService<TsHolidayInstanceEntity, TsHolidayInstanceDao, String> {

    /**
     * @param holidayUuid
     * @param year
     * @return
     */
    TsHolidayInstanceEntity getByHolidayUuidAndYear(String holidayUuid, String year);

    /**
     * @param uuids
     * @param year
     */
    void deleteAllExcludeUuidsInYear(List<String> uuids, String year);

}
