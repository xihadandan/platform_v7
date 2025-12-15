/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.service;

import com.wellsoft.pt.fulltext.dao.FulltextCategoryDao;
import com.wellsoft.pt.fulltext.entity.FulltextCategoryEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface FulltextCategoryService extends JpaService<FulltextCategoryEntity, FulltextCategoryDao, Long> {

    /**
     * @param moduleId
     * @return
     */
    List<FulltextCategoryEntity> listByModuleId(String moduleId);

    /**
     * @param system
     * @return
     */
    List<FulltextCategoryEntity> listBySystem(String system);

    /**
     * @param categoryUuids
     * @return
     */
    List<Long> listUuidByParentUuids(List<Long> categoryUuids);
}
