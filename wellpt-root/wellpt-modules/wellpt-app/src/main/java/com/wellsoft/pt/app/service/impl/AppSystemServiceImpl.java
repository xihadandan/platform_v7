/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.wellsoft.pt.app.dao.AppSystemDao;
import com.wellsoft.pt.app.entity.AppSystem;
import com.wellsoft.pt.app.service.AppSystemService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
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
public class AppSystemServiceImpl extends
        AbstractJpaServiceImpl<AppSystem, AppSystemDao, String> implements
        AppSystemService {

    @Override
    public AppSystem get(String uuid) {
        return this.dao.getOne(uuid);
    }

    @Override
    public AppSystem getById(String id) {
        List<AppSystem> appSystems = this.dao.listByFieldEqValue("id", id);
        return CollectionUtils.isNotEmpty(appSystems) ? appSystems.get(0) : null;
    }

    @Override
    public List<AppSystem> getAll() {
        return listAll();
    }

    @Override
    public List<AppSystem> findByExample(AppSystem example) {
        return this.dao.listByEntity(example);
    }

    @Override
    @Transactional
    public void save(AppSystem entity) {
        this.dao.save(entity);
    }

    @Override
    @Transactional
    public void saveAll(Collection<AppSystem> entities) {
        this.dao.saveAll(entities);
    }

    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    @Override
    @Transactional
    public void removeAll(Collection<AppSystem> entities) {
        deleteByEntities(entities);
    }

    @Override
    @Transactional
    public void remove(AppSystem entity) {
        this.dao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    @Override
    public List<AppSystem> getByIds(String[] ids) {
        return this.dao.listByFieldInValues("id", Arrays.asList(ids));
    }

    @Override
    public List<AppSystem> listBySystemUnitId(String systemUnitId) {
        return this.dao.listByFieldEqValue("systemUnitId", systemUnitId);
    }

    @Override
    public long countBySystemUnitId(String systemUnitId) {
        Map<String, Object> params = new HashMap<>();
        params.put("systemUnitId", systemUnitId);
        long count = this.dao.countByHQL("select count(uuid) from AppSystem where systemUnitId=:systemUnitId", params);
        return count;
    }
}
