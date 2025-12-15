/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.service;

import com.wellsoft.pt.basicdata.datadict.dao.CdDataDictionaryItemDao;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryItemEntity;
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
public interface CdDataDictionaryItemService extends JpaService<CdDataDictionaryItemEntity, CdDataDictionaryItemDao, Long> {
    /**
     * 根据字典UUID获取字典子项数据
     *
     * @param dataDictUuid
     * @return
     */
    List<CdDataDictionaryItemEntity> listByDataDictUuid(Long dataDictUuid);

    /**
     * 根据字典UUID列表删除字典子项数据
     *
     * @param dataDictUuids
     */
    void deleteByDataDictUuids(List<Long> dataDictUuids);

    /**
     * 根据字典值及字典数据UUID获取字典项
     *
     * @param value
     * @param dataDictUuid
     * @return
     */
    CdDataDictionaryItemEntity getByValueAndDataDictUuid(String value, Long dataDictUuid);

    /**
     * 根据上级字典项UUID获取字典子项数据
     *
     * @param parentUuid
     * @return
     */
    List<CdDataDictionaryItemEntity> listByParentUuid(Long parentUuid);

    /**
     * 根据UUID更新排序
     *
     * @param uuid
     * @param order
     */
    void updateSortOrderByUuid(Long uuid, Integer order);

    /**
     * 根据字典UUID获取字典子项数据根节点
     *
     * @param dataDictUuid
     * @return
     */
    List<CdDataDictionaryItemEntity> listRootByDataDictUuid(Long dataDictUuid);

    List<CdDataDictionaryItemEntity> listByParentUuids(List<Long> parentUuids);
}
