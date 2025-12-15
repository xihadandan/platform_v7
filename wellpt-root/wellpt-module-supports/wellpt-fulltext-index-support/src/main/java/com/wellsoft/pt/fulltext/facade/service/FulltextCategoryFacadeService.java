/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.fulltext.dto.FulltextCategoryDto;

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
public interface FulltextCategoryFacadeService extends Facade {

    /**
     * @param dto
     */
    Long saveDto(FulltextCategoryDto dto);

    /**
     * @param uuid
     * @return
     */
    FulltextCategoryDto getDto(Long uuid);

    /**
     * @param moduleId
     * @return
     */
    List<TreeNode> listAsTreeByModuleId(String moduleId);

    /**
     * @param systemId
     * @return
     */
    List<FulltextCategoryDto> listBySystem(String systemId);

    void deleteByUuid(Long uuid);

    List<Long> listUuidBySystem(String systemId);
}
