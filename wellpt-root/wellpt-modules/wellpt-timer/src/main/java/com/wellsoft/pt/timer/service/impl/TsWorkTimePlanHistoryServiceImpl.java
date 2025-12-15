/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.timer.dao.TsWorkTimePlanHistoryDao;
import com.wellsoft.pt.timer.entity.TsWorkTimePlanHistoryEntity;
import com.wellsoft.pt.timer.enums.EnumWorkTimePlanStatus;
import com.wellsoft.pt.timer.service.TsWorkTimePlanHistoryService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
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
public class TsWorkTimePlanHistoryServiceImpl
        extends AbstractJpaServiceImpl<TsWorkTimePlanHistoryEntity, TsWorkTimePlanHistoryDao, String>
        implements TsWorkTimePlanHistoryService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanHistoryService#deleteByWorkTimePlanUuid(java.lang.String)
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void deleteByWorkTimePlanUuid(String workTimePlanUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("workTimePlanUuid", workTimePlanUuid);
        String hql = "delete from TsWorkTimePlanHistoryEntity t where t.workTimePlanUuid = :workTimePlanUuid";
        this.dao.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanHistoryService#getActiveByWorkTimePlanUuid(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public TsWorkTimePlanHistoryEntity getActiveByWorkTimePlanUuid(String workTimePlanUuid) {
        String hql = "from TsWorkTimePlanHistoryEntity t where t.workTimePlanUuid = :workTimePlanUuid order by t.createTime desc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("workTimePlanUuid", workTimePlanUuid);
        List<TsWorkTimePlanHistoryEntity> historyEntities = this.dao.listByHQLAndPage(hql, values,
                new PagingInfo(1, 1));
        if (CollectionUtils.isNotEmpty(historyEntities)) {
            return historyEntities.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanHistoryService#listByWorkTimePlanUuids(java.util.List)
     */
    @Override
    public List<TsWorkTimePlanHistoryEntity> listByWorkTimePlanUuids(List<String> workTimePlanUuids) {
        if (CollectionUtils.isEmpty(workTimePlanUuids)) {
            return Collections.emptyList();
        }
        return this.dao.listByFieldInValues("workTimePlanUuid", workTimePlanUuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanHistoryService#deactiveByWorkTimePlanUuid(java.lang.String, java.util.Date)
     */
    @Override
    @Transactional
    public void deactiveByWorkTimePlanUuid(String workTimePlanUuid, Date deactiveTime) {
        String hql = "update TsWorkTimePlanHistoryEntity t set t.deactiveTime = :deactiveTime, t.status = :status where t.workTimePlanUuid = :workTimePlanUuid and t.status <> :status";
        Map<String, Object> values = Maps.newHashMap();
        values.put("workTimePlanUuid", workTimePlanUuid);
        values.put("deactiveTime", deactiveTime);
        values.put("status", EnumWorkTimePlanStatus.Deactive.getValue());
        this.dao.updateByHQL(hql, values);
    }

}
