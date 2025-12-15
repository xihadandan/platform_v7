/*
 * @(#)6/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.fulltext.dto.FulltextModelDto;

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
 * 6/16/25.1	    zhulh		6/16/25		    Create
 * </pre>
 * @date 6/16/25
 */
public interface FulltextModelFacadeService extends Facade {

    /**
     * @param dto
     */
    Long saveDto(FulltextModelDto dto);

    /**
     * @param dtos
     */
    void saveAllDto(List<FulltextModelDto> dtos);

    /**
     * @param uuid
     * @return
     */
    FulltextModelDto getDto(Long uuid);

    /**
     * @param moduleId
     * @return
     */
    List<FulltextModelDto> listByModuleId(String moduleId);

    /**
     * @param categoryUuid
     * @return
     */
    List<FulltextModelDto> listByCategoryUuid(Long categoryUuid);

    /**
     * @param uuid
     */
    void deleteByUuid(Long uuid);

    /**
     * @param dataModelUuid
     * @return
     */
    List<Long> getAllCategoryUuidsByDataModelUuid(Long dataModelUuid);

    List<FulltextModelDto> listByDataModelUuid(Long dataModelUuid);

    List<Long> getAllCategoryUuidsByParentCategoryUuids(List<Long> parentCategoryUuids);

    String getDataModeUuidByDataModelId(String dataModelId);

    FulltextModelDto getByDataModelUuidAndSystem(Long dataModelUuid, String system);
}
