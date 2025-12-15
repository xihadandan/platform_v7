/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.fulltext.dao.FulltextCategoryDao;
import com.wellsoft.pt.fulltext.entity.FulltextCategoryEntity;
import com.wellsoft.pt.fulltext.service.FulltextCategoryService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

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
public class FulltextCategoryServiceImpl extends AbstractJpaServiceImpl<FulltextCategoryEntity, FulltextCategoryDao, Long> implements FulltextCategoryService {

    /**
     * @param moduleId
     * @return
     */
    @Override
    public List<FulltextCategoryEntity> listByModuleId(String moduleId) {
        FulltextCategoryEntity entity = new FulltextCategoryEntity();
        entity.setModuleId(moduleId);
        return this.dao.listByEntity(entity);
    }

    @Override
    public List<FulltextCategoryEntity> listBySystem(String system) {
        Assert.hasText(system, "system不能为空");

        String hql = "from FulltextCategoryEntity t where t.system = :system or exists(select pm.moduleId from AppProdModuleEntity pm, AppSystemInfoEntity si " +
                "where pm.prodVersionUuid = si.prodVersionUuid and t.moduleId = pm.moduleId and si.system = :system)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<Long> listUuidByParentUuids(List<Long> categoryUuids) {
        String hql = "select t.uuid from FulltextCategoryEntity t where t.parentUuid in (:categoryUuids)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("categoryUuids", categoryUuids);
        return this.dao.listNumberByHQL(hql, params);
    }

}
