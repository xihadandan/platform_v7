/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dao.TsHolidayScheduleDao;
import com.wellsoft.pt.timer.entity.TsHolidayScheduleEntity;
import com.wellsoft.pt.timer.query.TsHolidayPerYearScheduleCountQueryItem;
import com.wellsoft.pt.timer.query.TsHolidayScheduleQueryItem;
import com.wellsoft.pt.timer.service.TsHolidayScheduleService;
import org.apache.commons.collections.CollectionUtils;
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
 * 2021年4月7日.1	zhulh		2021年4月7日		Create
 * </pre>
 * @date 2021年4月7日
 */
@Service
public class TsHolidayScheduleServiceImpl
        extends AbstractJpaServiceImpl<TsHolidayScheduleEntity, TsHolidayScheduleDao, String>
        implements TsHolidayScheduleService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsHolidayScheduleService#listHolidayPerYearScheduleCountBySystemUnitId(java.lang.String)
     */
    @Override
    public List<TsHolidayPerYearScheduleCountQueryItem> listHolidayPerYearScheduleCountBySystemUnitId(
            String systemUnitId) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("systemUnitId", systemUnitId);
        values.put("systemId", RequestSystemContextPathResolver.system());
        values.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        return this.dao.listItemByNameSQLQuery("holidayPerYearScheduleCountQuery",
                TsHolidayPerYearScheduleCountQueryItem.class, values, new PagingInfo(1, Integer.MAX_VALUE));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsHolidayScheduleService#listByYear(java.lang.String)
     */
    @Override
    public List<TsHolidayScheduleQueryItem> listByYear(String year) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("year", year);
        values.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        values.put("systemId", RequestSystemContextPathResolver.system());
        values.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        return this.dao.listItemByNameSQLQuery("listByYearQuery", TsHolidayScheduleQueryItem.class, values,
                new PagingInfo(1, Integer.MAX_VALUE));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.support.TsHolidayUsedChecker#isHolidayUsed(java.lang.String)
     */
    @Override
    public boolean isHolidayUsed(String holidayUuid) {
        TsHolidayScheduleEntity entity = new TsHolidayScheduleEntity();
        entity.setHolidayUuid(holidayUuid);
        return this.dao.countByEntity(entity) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsHolidayScheduleService#getByHolidayAndYear(java.lang.String, java.lang.String)
     */
    @Override
    public TsHolidayScheduleEntity getByHolidayAndYear(String holidayUuid, String year) {
        TsHolidayScheduleEntity entity = new TsHolidayScheduleEntity();
        entity.setHolidayUuid(holidayUuid);
        entity.setYear(year);
        List<TsHolidayScheduleEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

}
