/*
 * @(#)2017-02-13 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.dao.DmsDataManagementDefinitionDao;
import com.wellsoft.pt.dms.entity.DmsDataManagementDefinition;
import com.wellsoft.pt.dms.service.DmsDataManagementDefinitionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
 * 2017-02-13.1	zhulh		2017-02-13		Create
 * </pre>
 * @date 2017-02-13
 */
@Service
public class DmsDataManagementDefinitionServiceImpl extends
        AbstractJpaServiceImpl<DmsDataManagementDefinition, DmsDataManagementDefinitionDao, String> implements
        DmsDataManagementDefinitionService {
    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataManagementDefinitionService#get(java.lang.String)
     */
    @Override
    public DmsDataManagementDefinition get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataManagementDefinitionService#getAll()
     */
    @Override
    public List<DmsDataManagementDefinition> getAll() {
        return this.dao.listByEntity(new DmsDataManagementDefinition());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataManagementDefinitionService#findByExample(DmsDataManagementDefinition)
     */
    @Override
    public List<DmsDataManagementDefinition> findByExample(DmsDataManagementDefinition example) {
        return this.dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataManagementDefinitionService#save(com.wellsoft.pt.dms.entity.DmsDataManagementDefinition)
     */
    @Override
    public void save(DmsDataManagementDefinition entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataManagementDefinitionService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<DmsDataManagementDefinition> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataManagementDefinitionService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsDataManagementDefinitionService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<DmsDataManagementDefinition> entities) {
        this.dao.deleteByEntities(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataManagementDefinitionService#remove(DmsDataManagementDefinition)
     */
    @Override
    public void remove(DmsDataManagementDefinition entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataManagementDefinitionService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

}
