/*
 * @(#)11/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.dao.DmsLibraryCategoryDao;
import com.wellsoft.pt.dms.entity.DmsLibraryCategoryEntity;
import com.wellsoft.pt.dms.service.DmsLibraryCategoryService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
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
 * 11/20/25.1	    zhulh		11/20/25		    Create
 * </pre>
 * @date 11/20/25
 */
@Service
public class DmsLibraryCategoryServiceImpl extends AbstractJpaServiceImpl<DmsLibraryCategoryEntity, DmsLibraryCategoryDao, Long> implements DmsLibraryCategoryService {

    @Override
    public List<DmsLibraryCategoryEntity> listBySystem(String system) {
        DmsLibraryCategoryEntity entity = new DmsLibraryCategoryEntity();
        entity.setSystem(system);
        return this.dao.listByEntity(entity);
    }

}
