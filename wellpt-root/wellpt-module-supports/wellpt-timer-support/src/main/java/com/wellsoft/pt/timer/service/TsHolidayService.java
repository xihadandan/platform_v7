/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.dao.TsHolidayDao;
import com.wellsoft.pt.timer.entity.TsHolidayEntity;

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
public interface TsHolidayService extends JpaService<TsHolidayEntity, TsHolidayDao, String> {

    /**
     * @param systemUnitIds
     * @param name
     * @return
     */
    List<TsHolidayEntity> getAllBySystemUnitIdsLikeName(List<String> systemUnitIds, String name);

    /**
     * @param systemUnitIds
     * @param keyword
     * @param tags
     * @return
     */
    List<TsHolidayEntity> getAllBySystemUnitIdsLikeFields(List<String> systemUnitIds, String keyword, String tags);

    /**
     * @param id
     * @return
     */
    long countById(String id);

    /**
     * @param system
     * @param tenant
     * @return
     */
    long countBySystemAndTenant(String system, String tenant);
}
