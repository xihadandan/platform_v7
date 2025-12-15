/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dao.TsTimerConfigDao;
import com.wellsoft.pt.timer.entity.TsTimerConfigEntity;
import com.wellsoft.pt.timer.service.TsTimerConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
public class TsTimerConfigServiceImpl extends AbstractJpaServiceImpl<TsTimerConfigEntity, TsTimerConfigDao, String>
        implements TsTimerConfigService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerConfigService#getById(java.lang.String)
     */
    @Override
    public TsTimerConfigEntity getById(String timerId) {
        return this.dao.getOneByFieldEq("id", timerId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerConfigService#listBySystemUnitIds(java.util.List)
     */
    @Override
    public List<TsTimerConfigEntity> listBySystemUnitIds(List<String> systemUnitIds) {
        if (CollectionUtils.isEmpty(systemUnitIds)) {
            return Collections.emptyList();
        }
        return this.dao.listByFieldInValues("systemUnitId", systemUnitIds);
    }

    /**
     * @param system
     * @param tenant
     * @return
     */
    @Override
    public List<TsTimerConfigEntity> listBySystemAndTenant(String system, String tenant, String excludedCategoryId) {
        String hql = "from TsTimerConfigEntity t where 1 = 1 ";
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :system and t.tenant = :tenant ";
            params.put("system", system);
            params.put("tenant", tenant);
        }
        if (StringUtils.isNotBlank(excludedCategoryId)) {
            hql += " and (t.categoryUuid is null or t.categoryUuid not in(select c.uuid from TsTimerCategoryEntity c where c.id = :excludedCategoryId))";
            params.put("excludedCategoryId", excludedCategoryId);
        }
        return this.dao.listByHQL(hql, params);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.support.TsTimerCategoryUsedChecker#isTimerCategoryUsed(java.lang.String)
     */
    @Override
    public boolean isTimerCategoryUsed(String categoryUuid) {
        TsTimerConfigEntity entity = new TsTimerConfigEntity();
        entity.setCategoryUuid(categoryUuid);
        return this.dao.countByEntity(entity) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerConfigService#countById(java.lang.String)
     */
    @Override
    public long countById(String id) {
        TsTimerConfigEntity entity = new TsTimerConfigEntity();
        entity.setId(id);
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        return this.dao.countByEntity(entity);
    }

}
