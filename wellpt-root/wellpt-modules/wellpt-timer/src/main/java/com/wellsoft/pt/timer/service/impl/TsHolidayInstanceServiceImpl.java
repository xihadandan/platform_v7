/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.timer.dao.TsHolidayInstanceDao;
import com.wellsoft.pt.timer.entity.TsHolidayInstanceEntity;
import com.wellsoft.pt.timer.service.TsHolidayInstanceService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class TsHolidayInstanceServiceImpl
        extends AbstractJpaServiceImpl<TsHolidayInstanceEntity, TsHolidayInstanceDao, String>
        implements TsHolidayInstanceService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsHolidayInstanceService#getByHolidayUuidAndYear(java.lang.String, java.lang.String)
     */
    @Override
    public TsHolidayInstanceEntity getByHolidayUuidAndYear(String holidayUuid, String year) {
        TsHolidayInstanceEntity entity = new TsHolidayInstanceEntity();
        entity.setHolidayUuid(holidayUuid);
        entity.setYear(year);
        List<TsHolidayInstanceEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsHolidayInstanceService#deleteAllExcludeUuidsInYear(java.util.List, java.lang.String)
     */
    @Override
    @Transactional
    public void deleteAllExcludeUuidsInYear(List<String> uuids, String year) {
        List<String> notDeleteUuids = Lists.newArrayList(uuids);
        notDeleteUuids.add("-1");
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuids", notDeleteUuids);
        values.put("year", year);
        String sql = "delete from TsHolidayInstanceEntity t where t.uuid not in(:uuids) and t.year = :year";
        this.dao.updateByHQL(sql, values);
    }

}
