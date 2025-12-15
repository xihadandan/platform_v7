/*
 * @(#)2017-12-27 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.dms.dao.DmsObjectIdentityDao;
import com.wellsoft.pt.dms.entity.DmsObjectIdentityEntity;
import com.wellsoft.pt.dms.service.DmsObjectIdentityService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

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
 * 2017-12-27.1	zhulh		2017-12-27		Create
 * </pre>
 * @date 2017-12-27
 */
@Service
public class DmsObjectIdentityServiceImpl extends AbstractJpaServiceImpl<DmsObjectIdentityEntity, DmsObjectIdentityDao, String> implements DmsObjectIdentityService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectIdentityService#get(java.lang.String)
     */
    @Override
    public DmsObjectIdentityEntity get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectIdentityService#findByExample(DmsObjectIdentity)
     */
    @Override
    public List<DmsObjectIdentityEntity> findByExample(DmsObjectIdentityEntity example) {
        return this.dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectIdentityService#save(com.wellsoft.pt.dms.entity.DmsObjectIdentity)
     */
    @Override
    @Transactional
    public void save(DmsObjectIdentityEntity entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectIdentityService#saveAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void saveAll(Collection<DmsObjectIdentityEntity> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectIdentityService#getOrCreate(com.wellsoft.pt.core.entity.IdEntity)
     */
    @Override
    @Transactional
    public DmsObjectIdentityEntity getOrCreate(IdEntity idEntity) {
        DmsObjectIdentityEntity example = new DmsObjectIdentityEntity();
        example.setObjectIdIdentity(idEntity.getUuid());
        example.setObjectType(ClassUtils.getUserClass(idEntity).getCanonicalName());
        List<DmsObjectIdentityEntity> objectIdentityEntities = this.dao.listByEntity(example);
        if (objectIdentityEntities.size() > 0) {
            return objectIdentityEntities.get(0);
        }
        this.dao.save(example);
        return example;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectIdentityService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsObjectIdentityService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<DmsObjectIdentityEntity> entities) {
        this.dao.deleteByEntities(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectIdentityService#remove(DmsObjectIdentity)
     */
    @Override
    @Transactional
    public void remove(DmsObjectIdentityEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectIdentityService#removeAllByPk(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    /**
     * 根据业务主体标识删除记录
     *
     * @param objectIdIdentity
     */
    @Override
    @Transactional
    public void removeByObjectIdIdentity(String objectIdIdentity) {
        Assert.hasLength(objectIdIdentity, "数据对象标识不能为空！");

        String hql = "delete from DmsObjectIdentityEntity t where t.objectIdIdentity = :objectIdIdentity";
        Map<String, Object> params = Maps.newHashMap();
        params.put("objectIdIdentity", objectIdIdentity);
        this.dao.deleteByHQL(hql, params);
    }

}
