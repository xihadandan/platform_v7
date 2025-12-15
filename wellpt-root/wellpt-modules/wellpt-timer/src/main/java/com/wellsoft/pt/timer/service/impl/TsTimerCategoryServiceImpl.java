/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dao.TsTimerCategoryDao;
import com.wellsoft.pt.timer.entity.TsTimerCategoryEntity;
import com.wellsoft.pt.timer.service.TsTimerCategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
public class TsTimerCategoryServiceImpl extends
        AbstractJpaServiceImpl<TsTimerCategoryEntity, TsTimerCategoryDao, String> implements TsTimerCategoryService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerCategoryService#getAllBySystemUnitIdsLikeName(java.util.List, java.lang.String)
     */
    @Override
    public List<TsTimerCategoryEntity> getAllBySystemUnitIdsLikeName(List<String> systemUnitIds, String name) {
        String system = RequestSystemContextPathResolver.system();
        Map<String, Object> values = Maps.newHashMap();
        values.put("systemUnitIds", systemUnitIds);
        values.put("systemId", system);
        String hql = "from TsTimerCategoryEntity t where t.systemUnitId in(:systemUnitIds) ";
        if (StringUtils.isNotBlank(system)) {
            hql += " and (t.system = :systemId or t.id = 'flowTiming') ";
        }
        if (StringUtils.isNotBlank(name)) {
            values.put("name", "%" + name + "%");
            hql += " and t.name like :name ";
        }
        hql += " order by code asc, name asc";
        return this.listByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerCategoryService#getById(java.lang.String)
     */
    @Override
    public TsTimerCategoryEntity getById(String id) {
        TsTimerCategoryEntity entity = new TsTimerCategoryEntity();
        entity.setId(id);
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        List<TsTimerCategoryEntity> entities = this.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerCategoryService#countById(java.lang.String)
     */
    @Override
    public long countById(String id) {
        TsTimerCategoryEntity entity = new TsTimerCategoryEntity();
        entity.setId(id);
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        return this.dao.countByEntity(entity);
    }


}
