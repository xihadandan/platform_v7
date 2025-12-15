/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dao.TsHolidayDao;
import com.wellsoft.pt.timer.entity.TsHolidayEntity;
import com.wellsoft.pt.timer.service.TsHolidayService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Iterator;
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
public class TsHolidayServiceImpl extends AbstractJpaServiceImpl<TsHolidayEntity, TsHolidayDao, String>
        implements TsHolidayService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsHolidayService#getAllBySystemUnitIdsLikeName(java.util.List, java.lang.String)
     */
    @Override
    public List<TsHolidayEntity> getAllBySystemUnitIdsLikeName(List<String> systemUnitIds, String name) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("systemUnitIds", systemUnitIds);
        String hql = "from TsHolidayEntity t where t.systemUnitId in(:systemUnitIds) ";
        if (StringUtils.isNotBlank(name)) {
            values.put("name", "%" + name + "%");
            hql += " and t.name like :name ";
        }
        hql += " order by holidayDate asc ";
        return this.listByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsHolidayService#getAllBySystemUnitIdsLikeFields(java.util.List, java.lang.String, java.lang.String)
     */
    @Override
    public List<TsHolidayEntity> getAllBySystemUnitIdsLikeFields(List<String> systemUnitIds, String keyword,
                                                                 String tags) {
        Map<String, Object> values = Maps.newHashMap();
        String system = RequestSystemContextPathResolver.system();
        values.put("systemId", system);
        values.put("systemUnitIds", systemUnitIds);
        String hql = "from TsHolidayEntity t where t.systemUnitId in(:systemUnitIds) ";
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :systemId ";
        }
        if (StringUtils.isNotBlank(keyword)) {
            values.put("keyword", "%" + keyword + "%");
            hql += " and (t.name like :keyword or t.holidayDate like :keyword)";
        }
        if (StringUtils.isNotBlank(tags)) {
            Iterator<String> tagIt = Lists.newArrayList(StringUtils.split(tags, Separator.SEMICOLON.getValue()))
                    .iterator();
            int index = 0;
            List<String> tagConditins = Lists.newArrayList();
            while (tagIt.hasNext()) {
                String tag = tagIt.next();
                values.put("tag" + index, "%" + tag + "%");
                tagConditins.add("t.tag like :tag" + index);
                index++;
            }
            hql += " and (" + StringUtils.join(tagConditins, " or ") + ")";
        }
        hql += " order by holidayDate asc ";
        return this.listByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsHolidayService#countById(java.lang.String)
     */
    @Override
    public long countById(String id) {
        TsHolidayEntity entity = new TsHolidayEntity();
        entity.setId(id);
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        return this.dao.countByEntity(entity);
    }

    /**
     * @param system
     * @param tenant
     * @return
     */
    @Override
    public long countBySystemAndTenant(String system, String tenant) {
        Assert.hasLength(system, "归属系统不能为空！");
        Assert.hasLength(tenant, "归属租户不能为空！");

        TsHolidayEntity entity = new TsHolidayEntity();
        entity.setSystem(system);
        entity.setTenant(tenant);
        return this.dao.countByEntity(entity);
    }

}
