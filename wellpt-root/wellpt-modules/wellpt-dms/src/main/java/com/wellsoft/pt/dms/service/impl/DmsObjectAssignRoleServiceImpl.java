/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.dao.DmsObjectAssignRoleDao;
import com.wellsoft.pt.dms.entity.DmsObjectAssignRoleEntity;
import com.wellsoft.pt.dms.service.DmsObjectAssignRoleService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
@Service
public class DmsObjectAssignRoleServiceImpl extends AbstractJpaServiceImpl<DmsObjectAssignRoleEntity, DmsObjectAssignRoleDao, String> implements DmsObjectAssignRoleService {

    /**
     * @param uuid
     * @return
     */
    @Override
    public DmsObjectAssignRoleEntity get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * @param example
     * @return
     */
    @Override
    public List<DmsObjectAssignRoleEntity> findByExample(DmsObjectAssignRoleEntity example) {
        return this.dao.listByEntity(example);
    }

    /**
     * @param entity
     */
    @Override
    @Transactional
    public void save(DmsObjectAssignRoleEntity entity) {
        this.dao.save(entity);
    }

    @Override
    @Transactional
    public void saveAll(Collection<DmsObjectAssignRoleEntity> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * @param uuid
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    /**
     * @param entity
     */
    @Override
    @Transactional
    public void remove(DmsObjectAssignRoleEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * @param uuids
     */
    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    /**
     * @param objectIdentityUuid
     */
    @Override
    @Transactional
    public void removeByObjectIdentityUuid(String objectIdentityUuid) {
        String hql = "delete from DmsObjectAssignRoleEntity t where t.objectIdentityUuid = :objectIdentityUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("objectIdentityUuid", objectIdentityUuid);
        this.dao.deleteByHQL(hql, values);
    }

    @Override
    public List<DmsObjectAssignRoleEntity> listByRoleUuids(List<String> roleUuids) {
        if (CollectionUtils.isEmpty(roleUuids)) {
            return Collections.emptyList();
        }

        String hql = "from DmsObjectAssignRoleEntity t where t.roleUuid in (:roleUuids)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("roleUuids", roleUuids);
        return this.dao.listByHQL(hql, values);
    }

}
