/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.service;

import com.wellsoft.pt.basicdata.datadict.dao.CdDataDictionaryItemAttributeDao;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryItemAttributeEntity;
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
public interface CdDataDictionaryItemAttributeService extends JpaService<CdDataDictionaryItemAttributeEntity, CdDataDictionaryItemAttributeDao, Long> {
    /**
     * 根据字典项UUID删除扩展属性
     *
     * @param itemUuids
     */
    void deleteByItemUuids(List<Long> itemUuids);

    /**
     * 根据字典项UUID获取扩展属性
     *
     * @param itemUuid
     */
    List<CdDataDictionaryItemAttributeEntity> listByItemUuid(Long itemUuid);

    /**
     * 根据字典项UUID列表获取扩展属性
     *
     * @param itemUuids
     * @return
     */
    List<CdDataDictionaryItemAttributeEntity> listByItemUuids(List<Long> itemUuids);
}
