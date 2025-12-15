/*
 * @(#)2017-12-29 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.entity.DmsRoleActionEntity;
import com.wellsoft.pt.dms.service.DmsRoleActionService;
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
public class DmsRoleActionServiceImpl extends BaseServiceImpl implements DmsRoleActionService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleActionService#get(java.lang.String)
     */
    @Override
    public DmsRoleActionEntity get(String uuid) {
        return this.dao.get(DmsRoleActionEntity.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleActionService#getAll()
     */
    @Override
    public List<DmsRoleActionEntity> getAll() {
        return this.dao.getAll(DmsRoleActionEntity.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleActionService#findByExample(DmsRoleAction)
     */
    @Override
    public List<DmsRoleActionEntity> findByExample(DmsRoleActionEntity example) {
        return this.dao.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleActionService#save(com.wellsoft.pt.dms.entity.DmsRoleAction)
     */
    @Override
    public void save(DmsRoleActionEntity entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleActionService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<DmsRoleActionEntity> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleActionService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.deleteByPk(DmsRoleActionEntity.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsRoleActionService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<DmsRoleActionEntity> entities) {
        this.dao.deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleActionService#remove(DmsRoleAction)
     */
    @Override
    public void remove(DmsRoleActionEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleActionService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.dao.deleteAllByPk(DmsRoleActionEntity.class, list);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleActionService#removeByRoleUuid(java.lang.String)
     */
    @Override
    public void removeByRoleUuid(String roleUuid) {
        String hql = "delete from DmsRoleActionEntity t where t.roleUuid = :roleUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("roleUuid", roleUuid);
        this.dao.batchExecute(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleActionService#getActionByRoleUuids(java.util.List)
     */
    @Override
    public List<String> getActionByRoleUuids(List<String> roleUuids) {
        if (roleUuids.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("roleUuids", roleUuids);
        String hql = "select t.action from DmsRoleActionEntity t where t.roleUuid in(:roleUuids)";
        return this.dao.query(hql, values, String.class);
    }

}
