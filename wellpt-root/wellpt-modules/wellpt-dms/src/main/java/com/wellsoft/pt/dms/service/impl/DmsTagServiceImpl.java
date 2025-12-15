/*
 * @(#)2017-02-22 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.dao.DmsTagDao;
import com.wellsoft.pt.dms.entity.DmsTagEntity;
import com.wellsoft.pt.dms.service.DmsTagService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
 * 2017-02-22.1	zhulh		2017-02-22		Create
 * </pre>
 * @date 2017-02-22
 */
@Service
public class DmsTagServiceImpl extends AbstractJpaServiceImpl<DmsTagEntity, DmsTagDao, String> implements DmsTagService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagService#get(java.lang.String)
     */
    @Override
    public DmsTagEntity get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagService#getAll()
     */
    @Override
    public List<DmsTagEntity> getAll() {
        return this.dao.listByEntity(new DmsTagEntity());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagService#findByExample(DmsTag)
     */
    @Override
    public List<DmsTagEntity> findByExample(DmsTagEntity example) {
        return this.dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagService#findByExample(com.wellsoft.pt.dms.entity.DmsTag, java.lang.String)
     */
    @Override
    public List<DmsTagEntity> findByExample(DmsTagEntity example, String orderBy) {
        return this.dao.listByEntity(example, null, orderBy, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagService#countByExample(com.wellsoft.pt.dms.entity.DmsTagEntity)
     */
    @Override
    public Long countByExample(DmsTagEntity example) {
        return this.dao.countByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagService#save(com.wellsoft.pt.dms.entity.DmsTag)
     */
    @Override
    @Transactional
    public void save(DmsTagEntity entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagService#saveAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void saveAll(Collection<DmsTagEntity> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsTagService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<DmsTagEntity> entities) {
        this.dao.deleteByEntities(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagService#remove(DmsTag)
     */
    @Override
    @Transactional
    public void remove(DmsTagEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagService#removeAllByPk(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    @Override
    public List<DmsTagEntity> listRootTagBySystem(String system) {
        String hql = "from DmsTagEntity t where t.parentUuid is null and t.ownerId is null";
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :system";
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        return this.dao.listByHQL(hql, params);
    }

}
