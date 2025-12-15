/*
 * @(#)11/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.dao.DmsLibrarySettingDao;
import com.wellsoft.pt.dms.entity.DmsLibrarySettingEntity;
import com.wellsoft.pt.dms.service.DmsLibrarySettingService;
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
 * 11/21/25.1	    zhulh		11/21/25		    Create
 * </pre>
 * @date 11/21/25
 */
@Service
public class DmsLibrarySettingServiceImpl extends AbstractJpaServiceImpl<DmsLibrarySettingEntity, DmsLibrarySettingDao, Long> implements DmsLibrarySettingService {

    @Override
    public DmsLibrarySettingEntity getBySystem(String system) {
        DmsLibrarySettingEntity entity = new DmsLibrarySettingEntity();
        entity.setSystem(system);
        List<DmsLibrarySettingEntity> list = this.dao.listByEntity(entity);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

}
