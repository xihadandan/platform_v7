/*
 * @(#)2015-08-12 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.service.impl;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.workflow.gz.entity.UfWfGzWorkData;
import com.wellsoft.pt.workflow.gz.service.UfWfGzWorkDataService;
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
 * 2015-08-12.1	zhulh		2015-08-12		Create
 * </pre>
 * @date 2015-08-12
 */
@Service
@Transactional
public class UfWfGzWorkDataServiceImpl extends BaseServiceImpl implements UfWfGzWorkDataService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.UfWfGzWorkDataService#get(java.lang.String)
     */
    @Override
    public UfWfGzWorkData get(String uuid) {
        return this.dao.get(UfWfGzWorkData.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.UfWfGzWorkDataService#getAll()
     */
    @Override
    public List<UfWfGzWorkData> getAll() {
        return this.dao.getAll(UfWfGzWorkData.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.UfWfGzWorkDataService#getAllByPk(java.util.Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public List<UfWfGzWorkData> getAllByPk(Collection<String> uuids) {
        return this.dao.get(UfWfGzWorkData.class, IdEntity.UUID, uuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.UfWfGzWorkDataService#findByExample(UfWfGzWorkData)
     */
    @Override
    public List<UfWfGzWorkData> findByExample(UfWfGzWorkData example) {
        return this.dao.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.UfWfGzWorkDataService#save(com.wellsoft.pt.workflow.gz.entity.UfWfGzWorkData)
     */
    @Override
    public void save(UfWfGzWorkData entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.UfWfGzWorkDataService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<UfWfGzWorkData> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.UfWfGzWorkDataService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.deleteByPk(UfWfGzWorkData.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.UfWfGzWorkDataService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<UfWfGzWorkData> entities) {
        this.dao.deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.UfWfGzWorkDataService#remove(UfWfGzWorkData)
     */
    @Override
    public void remove(UfWfGzWorkData entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.service.UfWfGzWorkDataService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.dao.deleteAllByPk(UfWfGzWorkData.class, list);
    }

}
