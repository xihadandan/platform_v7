/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.service.impl;

import com.wellsoft.pt.fulltext.dao.FulltextSettingDao;
import com.wellsoft.pt.fulltext.entity.FulltextSettingEntity;
import com.wellsoft.pt.fulltext.service.FulltextSettingService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class FulltextSettingServiceImpl extends AbstractJpaServiceImpl<FulltextSettingEntity, FulltextSettingDao, Long> implements FulltextSettingService {

    @Override
    public FulltextSettingEntity getByTypeAndSystem(String type, String system) {
        FulltextSettingEntity entity = new FulltextSettingEntity();
        entity.setType(type);
        entity.setSystem(system);
        List<FulltextSettingEntity> entities = this.dao.listByEntity(entity);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

    @Override
    public List<String> listSystem() {
        String hql = "select distinct system from FulltextSettingEntity";
        return this.dao.listCharSequenceByHQL(hql, null);
    }

}
