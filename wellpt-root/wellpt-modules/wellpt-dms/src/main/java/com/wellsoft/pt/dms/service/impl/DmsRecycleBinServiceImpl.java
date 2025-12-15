/*
 * @(#)2018-02-01 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.entity.DmsRecycleBinEntity;
import com.wellsoft.pt.dms.service.DmsRecycleBinService;
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
 * 2018-02-01.1	zhulh		2018-02-01		Create
 * </pre>
 * @date 2018-02-01
 */
@Service
@Transactional
public class DmsRecycleBinServiceImpl extends BaseServiceImpl implements DmsRecycleBinService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRecycleBinService#get(java.lang.String)
     */
    @Override
    public DmsRecycleBinEntity get(String uuid) {
        return this.dao.get(DmsRecycleBinEntity.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRecycleBinService#getAll()
     */
    @Override
    public List<DmsRecycleBinEntity> getAll() {
        return this.dao.getAll(DmsRecycleBinEntity.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRecycleBinService#findByExample(DmsRecycleBin)
     */
    @Override
    public List<DmsRecycleBinEntity> findByExample(DmsRecycleBinEntity example) {
        return this.dao.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRecycleBinService#save(com.wellsoft.pt.dms.entity.DmsRecycleBin)
     */
    @Override
    public void save(DmsRecycleBinEntity entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRecycleBinService#addData(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void addData(String dataType, String dataUuid, String userId) {
        DmsRecycleBinEntity recycleBinEntity = new DmsRecycleBinEntity();
        recycleBinEntity.setDataType(dataType);
        recycleBinEntity.setDataUuid(dataUuid);
        recycleBinEntity.setUserId(userId);
        this.dao.save(recycleBinEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRecycleBinService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<DmsRecycleBinEntity> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRecycleBinService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.deleteByPk(DmsRecycleBinEntity.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsRecycleBinService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<DmsRecycleBinEntity> entities) {
        this.dao.deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRecycleBinService#remove(DmsRecycleBin)
     */
    @Override
    public void remove(DmsRecycleBinEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRecycleBinService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.dao.deleteAllByPk(DmsRecycleBinEntity.class, list);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRecycleBinService#removeByDataUuid(java.lang.String)
     */
    @Override
    public void removeByDataUuid(String dataUuid) {
        String hql = "delete from DmsRecycleBinEntity t where t.dataUuid = :dataUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataUuid", dataUuid);
        this.dao.batchExecute(hql, values);
    }

}
