/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.fulltext.dao.FulltextModelDao;
import com.wellsoft.pt.fulltext.entity.FulltextCategoryEntity;
import com.wellsoft.pt.fulltext.entity.FulltextModelEntity;
import com.wellsoft.pt.fulltext.service.FulltextCategoryService;
import com.wellsoft.pt.fulltext.service.FulltextModelService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/13/25.1	    zhulh		6/13/25		    Create
 * </pre>
 * @date 6/13/25
 */
@Service
public class FulltextModelServiceImpl extends AbstractJpaServiceImpl<FulltextModelEntity, FulltextModelDao, Long> implements FulltextModelService {

    @Autowired
    private FulltextCategoryService fulltextCategoryService;

    @Override
    public List<FulltextModelEntity> listByModuleId(String moduleId) {
        String hql = "from FulltextModelEntity where categoryUuid in (select uuid from FulltextCategoryEntity where moduleId = :moduleId)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("moduleId", moduleId);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<FulltextModelEntity> listByCategoryUuid(Long categoryUuid) {
        FulltextModelEntity entity = new FulltextModelEntity();
        entity.setCategoryUuid(categoryUuid);
        return this.dao.listByEntity(entity);
    }

    @Override
    public List<FulltextModelEntity> listByDataModelUuid(Long dataModelUuid) {
        FulltextModelEntity entity = new FulltextModelEntity();
        entity.setDataModelUuid(dataModelUuid);
        return this.dao.listByEntity(entity);
    }

    @Override
    public List<FulltextModelEntity> listBySystem(String system) {
        List<Long> categoryUuids = fulltextCategoryService.listBySystem(system).stream().map(FulltextCategoryEntity::getUuid).collect(Collectors.toList());
        return this.dao.listByFieldInValues("categoryUuid", categoryUuids);
    }

    @Override
    @Transactional
    public void deleteByCategoryUuids(List<Long> categoryUuids) {
        if (CollectionUtils.isEmpty(categoryUuids)) {
            return;
        }

        String hql = "delete from FulltextModelEntity where categoryUuid in (:categoryUuids)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("categoryUuids", categoryUuids);
        this.dao.deleteByHQL(hql, params);
    }

    @Override
    public FulltextModelEntity getByDataModelUuidAndSystem(Long dataModelUuid, String system) {
        FulltextModelEntity entity = new FulltextModelEntity();
        entity.setDataModelUuid(dataModelUuid);
        entity.setSystem(system);
        List<FulltextModelEntity> entities = this.dao.listByEntity(entity);
        return CollectionUtils.isEmpty(entities) ? null : entities.get(0);
    }

}
