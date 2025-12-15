/*
 * @(#)4/28/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.basicdata.material.dto.CdMaterialDefinitionDto;

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
 * 4/28/23.1	zhulh		4/28/23		Create
 * </pre>
 * @date 4/28/23
 */
public interface CdMaterialDefinitionFacadeService extends Facade, Select2QueryApi {

    /**
     * 获取材料定义
     *
     * @param uuid
     */
    CdMaterialDefinitionDto getDto(Long uuid);


    /**
     * 获取材料定义格式
     *
     * @param codes
     * @return
     */
    List<String> listFormatByCodes(List<String> codes);

    /**
     * 保存材料定义
     *
     * @param dto
     */
    void saveDto(CdMaterialDefinitionDto dto);

    /**
     * 根据UUID，删除材料定义
     *
     * @param uuid
     */
    void deleteByUuid(Long uuid);

}
