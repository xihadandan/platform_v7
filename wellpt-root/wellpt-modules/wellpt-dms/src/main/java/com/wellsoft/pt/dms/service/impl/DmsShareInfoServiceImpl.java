/*
 * @(#)2018-02-02 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.entity.DmsShareInfoEntity;
import com.wellsoft.pt.dms.service.DmsShareInfoService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-02-02.1	zhulh		2018-02-02		Create
 * </pre>
 * @date 2018-02-02
 */
@Service
@Transactional
public class DmsShareInfoServiceImpl extends BaseServiceImpl implements DmsShareInfoService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsShareInfoService#get(java.lang.String)
     */
    @Override
    public DmsShareInfoEntity get(String uuid) {
        return this.dao.get(DmsShareInfoEntity.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsShareInfoService#getAll()
     */
    @Override
    public List<DmsShareInfoEntity> getAll() {
        return this.dao.getAll(DmsShareInfoEntity.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsShareInfoService#findByExample(DmsShareInfo)
     */
    @Override
    public List<DmsShareInfoEntity> findByExample(DmsShareInfoEntity example) {
        return this.dao.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsShareInfoService#save(com.wellsoft.pt.dms.entity.DmsShareInfo)
     */
    @Override
    public void save(DmsShareInfoEntity entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsShareInfoService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<DmsShareInfoEntity> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsShareInfoService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.deleteByPk(DmsShareInfoEntity.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsShareInfoService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<DmsShareInfoEntity> entities) {
        this.dao.deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsShareInfoService#remove(DmsShareInfo)
     */
    @Override
    public void remove(DmsShareInfoEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsShareInfoService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.dao.deleteAllByPk(DmsShareInfoEntity.class, list);
    }

}
