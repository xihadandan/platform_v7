/*
 * @(#)7/11/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.serialnumber.dao.SnSerialNumberCategoryDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberCategoryEntity;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberCategoryService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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
 * 7/11/22.1	zhulh		7/11/22		Create
 * </pre>
 * @date 7/11/22
 */
@Service
public class SnSerialNumberCategoryServiceImpl extends AbstractJpaServiceImpl<SnSerialNumberCategoryEntity, SnSerialNumberCategoryDao, String>
        implements SnSerialNumberCategoryService {
    /**
     * 流水号分类按系统单位及名称查询
     *
     * @param name
     * @return
     */
    @Override
    public List<SnSerialNumberCategoryEntity> getAllBySystemUnitIdsLikeName(String name) {
        Map<String, Object> values = new HashMap<String, Object>();
        List<String> systemUnitIds = new ArrayList<String>();
        String system = RequestSystemContextPathResolver.system();
        systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
        systemUnitIds.add(SpringSecurityUtils.getCurrentUserUnitId());
        values.put("systemUnitIds", systemUnitIds);
        values.put("system", system);
        values.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        String hql = "from SnSerialNumberCategoryEntity t where t.systemUnitId in(:systemUnitIds) and t.tenant = :tenant ";
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :system ";
        }
        if (StringUtils.isNotBlank(name)) {
            values.put("name", "%" + name + "%");
            hql += " and t.name like :name ";
        }
        hql += " order by code asc ";
        List<SnSerialNumberCategoryEntity> categories = listByHQL(hql, values);
        return categories;
    }

    /**
     * @param uuid
     * @return
     */
    @Override
    public int deleteWhenNotUsed(String uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        int row = this.dao.deleteBySQL(
                "delete from sn_serial_number_category c where uuid=:uuid and not exists (select 1 from sn_serial_number_definition d where d.category_uuid=c.uuid) ",
                params);
        return row > 0 ? 1 : -1;
    }

    @Override
    public List<SnSerialNumberCategoryEntity> listBySystem(String system) {
        String hql = "from SnSerialNumberCategoryEntity t where 1 = 1 ";
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :system ";
        }
        hql += " order by t.code asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        return this.dao.listByHQL(hql, params);
    }

}
