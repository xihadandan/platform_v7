/*
 * @(#)2017-12-29 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.entity.DmsObjectAssignRoleItemEntity;
import com.wellsoft.pt.dms.service.DmsObjectAssignRoleItemService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
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
 * 2017-12-29.1	zhulh		2017-12-29		Create
 * </pre>
 * @date 2017-12-29
 */
@Service
@Transactional
public class DmsObjectAssignRoleItemServiceImpl extends BaseServiceImpl implements DmsObjectAssignRoleItemService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectAssignRoleItemService#get(java.lang.String)
     */
    @Override
    public DmsObjectAssignRoleItemEntity get(String uuid) {
        return this.dao.get(DmsObjectAssignRoleItemEntity.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectAssignRoleItemService#getAll()
     */
    @Override
    public List<DmsObjectAssignRoleItemEntity> getAll() {
        return this.dao.getAll(DmsObjectAssignRoleItemEntity.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectAssignRoleItemService#findByExample(DmsObjectAssignRoleItem)
     */
    @Override
    public List<DmsObjectAssignRoleItemEntity> findByExample(DmsObjectAssignRoleItemEntity example) {
        return this.dao.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectAssignRoleItemService#save(com.wellsoft.pt.dms.entity.DmsObjectAssignRoleItem)
     */
    @Override
    public void save(DmsObjectAssignRoleItemEntity entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectAssignRoleItemService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<DmsObjectAssignRoleItemEntity> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectAssignRoleItemService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.deleteByPk(DmsObjectAssignRoleItemEntity.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsObjectAssignRoleItemService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<DmsObjectAssignRoleItemEntity> entities) {
        this.dao.deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectAssignRoleItemService#remove(DmsObjectAssignRoleItem)
     */
    @Override
    public void remove(DmsObjectAssignRoleItemEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectAssignRoleItemService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.dao.deleteAllByPk(DmsObjectAssignRoleItemEntity.class, list);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectAssignRoleItemService#removeByAssignRoleUuid(java.lang.String)
     */
    @Override
    public void removeByAssignRoleUuid(String assignRoleUuid) {
        String hql = "delete from DmsObjectAssignRoleItemEntity t where t.assignRoleUuid = :assignRoleUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("assignRoleUuid", assignRoleUuid);
        this.dao.batchExecute(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsObjectAssignRoleItemService#removeByObjectIdentityUuid(java.lang.String)
     */
    @Override
    public void removeByObjectIdentityUuid(String objectIdentityUuid) {
        String hql = "delete from DmsObjectAssignRoleItemEntity t1 where exists (select t2.uuid from DmsObjectAssignRoleEntity t2 where t1.assignRoleUuid = t2.uuid and t2.objectIdentityUuid = :objectIdentityUuid)";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("objectIdentityUuid", objectIdentityUuid);
        this.dao.batchExecute(hql, values);
    }

}
