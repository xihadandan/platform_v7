/*
 * @(#)7/11/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberCategoryDto;

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
 * 7/11/22.1	zhulh		7/11/22		Create
 * </pre>
 * @date 7/11/22
 */
public interface SnSerialNumberCategoryFacadeService extends Facade, Select2QueryApi {
    /**
     * 获取流水号分类
     *
     * @param uuid
     * @return
     */
    SnSerialNumberCategoryDto getDto(String uuid);

    /**
     * 流水号分类按系统单位及名称查询
     *
     * @param name
     * @return
     */
    List<SnSerialNumberCategoryDto> getAllBySystemUnitIdsLikeName(String name);

    /**
     * 保存流水号分类
     *
     * @param dto
     */
    void saveDto(SnSerialNumberCategoryDto dto);

    /**
     * 删除没用的流水号分类
     *
     * @param uuid
     * @return
     */
    int deleteWhenNotUsed(String uuid);


    /**
     * 按编号升序获取全部流水号分类
     *
     * @return
     */
    List<SnSerialNumberCategoryDto> listAllByCodeAsc();

    /**
     * @param system
     * @return
     */
    List<SnSerialNumberCategoryDto> listBySystem(String system);
}
