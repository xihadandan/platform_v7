/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.service;

import com.wellsoft.pt.basicdata.datadict.dao.CdDataDictionaryCategoryDao;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryCategoryEntity;
import com.wellsoft.pt.basicdata.datadict.enums.EnumDictionaryCategoryType;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 8/9/23.1	zhulh		8/9/23		Create
 * </pre>
 * @date 8/9/23
 */
public interface CdDataDictionaryCategoryService extends JpaService<CdDataDictionaryCategoryEntity, CdDataDictionaryCategoryDao, Long> {
    /**
     * 根据模块ID获取字典分类
     *
     * @param moduleId
     * @return
     */
    List<CdDataDictionaryCategoryEntity> listByModuleId(String moduleId);

    /**
     * 根据字典分类UUID删除字典分类
     *
     * @param uuid
     */
    void deleteByUuid(Long uuid);

    /**
     * 获取模块ID为空的字典分类
     *
     * @return
     */
    List<CdDataDictionaryCategoryEntity> listWithoutModuleId();

    /**
     * 获取所有字典分类
     *
     * @return
     */
    List<CdDataDictionaryCategoryEntity> listAllBySortOrderAsc();

    /**
     * 根据字典分类类型获取字典分类
     *
     * @param type
     * @return
     */
    List<CdDataDictionaryCategoryEntity> listByType(EnumDictionaryCategoryType type);

    /**
     * 按类型升序获取所有字典分类
     *
     * @return
     */
    List<CdDataDictionaryCategoryEntity> listAllByTypeAsc();
}
