/*
 * @(#)2017-02-22 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.entity.DmsTagDataEntity;
import com.wellsoft.pt.dms.model.DmsTagData;
import com.wellsoft.pt.dms.service.DmsTagDataService;
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
 * 2017-02-22.1	zhulh		2017-02-22		Create
 * </pre>
 * @date 2017-02-22
 */
@Service
@Transactional
public class DmsTagDataServiceImpl extends BaseServiceImpl implements DmsTagDataService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#get(java.lang.String)
     */
    @Override
    public DmsTagDataEntity get(String uuid) {
        return this.dao.get(DmsTagDataEntity.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#getAll()
     */
    @Override
    public List<DmsTagDataEntity> getAll() {
        return this.dao.getAll(DmsTagDataEntity.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#findByExample(DmsTagData)
     */
    @Override
    public List<DmsTagDataEntity> findByExample(DmsTagDataEntity example) {
        return this.dao.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#findByDataUuids(java.util.List)
     */
    @Override
    public List<DmsTagData> findByDataUuids(List<String> dataUuids) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataUuids", dataUuids);
        return this.nativeDao.namedQuery("findByDataUuids", values, DmsTagData.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#countByExample(com.wellsoft.pt.dms.entity.DmsTagData)
     */
    @Override
    public Long countByExample(DmsTagDataEntity example) {
        return this.dao.countByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#save(com.wellsoft.pt.dms.entity.DmsTagData)
     */
    @Override
    public void save(DmsTagDataEntity entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<DmsTagDataEntity> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.deleteByPk(DmsTagDataEntity.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsTagDataService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<DmsTagDataEntity> entities) {
        this.dao.deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#remove(DmsTagData)
     */
    @Override
    public void remove(DmsTagDataEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.dao.deleteAllByPk(DmsTagDataEntity.class, list);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#removeByDataUuidAndTagUuid(java.lang.String, java.lang.String)
     */
    @Override
    public void removeByDataUuidAndTagUuid(String dataUuid, String tagUuid) {
        String hql = "delete from DmsTagDataEntity t where t.dataUuid = :dataUuid and t.tagUuid = :tagUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataUuid", dataUuid);
        values.put("tagUuid", tagUuid);
        this.dao.batchExecute(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#removeAllByDataUuid(java.util.List)
     */
    @Override
    public void removeAllByDataUuid(List<String> dataUuids) {
        String hql = "delete from DmsTagDataEntity t where t.dataUuid = :dataUuid";
        for (String dataUuid : dataUuids) {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("dataUuid", dataUuid);
            this.dao.batchExecute(hql, values);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsTagDataService#removeByTagUuid(java.lang.String)
     */
    @Override
    public void removeByTagUuid(String tagUuid) {
        String hql = "delete from DmsTagDataEntity t where t.tagUuid = :tagUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("tagUuid", tagUuid);
        this.dao.batchExecute(hql, values);
    }

}
