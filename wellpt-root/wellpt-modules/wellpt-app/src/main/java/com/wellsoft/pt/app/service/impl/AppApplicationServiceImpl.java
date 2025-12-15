/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.dao.AppApplicationDao;
import com.wellsoft.pt.app.entity.AppApplication;
import com.wellsoft.pt.app.service.AppApplicationService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * Description: 如何描述该类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	t		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
@Service
public class AppApplicationServiceImpl extends
        AbstractJpaServiceImpl<AppApplication, AppApplicationDao, String>
        implements AppApplicationService {

    @Override
    public AppApplication get(String uuid) {
        return this.dao.getOne(uuid);
    }

    @Override
    public List<AppApplication> getAll() {
        return listAll();
    }

    @Override
    public List<AppApplication> findByExample(AppApplication example) {
        return this.dao.listByEntity(example);
    }

    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    @Override
    @Transactional
    public void removeAll(Collection<AppApplication> entities) {
        deleteByEntities(entities);
    }

    @Override
    @Transactional
    public void remove(AppApplication entity) {
        this.dao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        deleteByUuids(Lists.newArrayList(uuids));
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppApplicationService#getByIds(java.lang.String[])
     */
    @Override
    public List<AppApplication> getByIds(String[] ids) {
        return this.dao.listByFieldInValues("id", Arrays.asList(ids));
    }

    @Override
    public long countBySystemUnitId(String systemUnitId) {
        Map<String, Object> params = new HashMap<>();
        params.put("systemUnitId", systemUnitId);
        long count = this.dao.countBySQL("select count(*) from app_application t1 left join app_product_integration t2 on t1.uuid = t2.data_uuid and t2.data_type = 3 where t1.system_unit_id=:systemUnitId", params);
        return count;
    }
}
