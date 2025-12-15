/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.datadict.dao.CdDataDictionaryDao;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryEntity;
import com.wellsoft.pt.basicdata.datadict.query.CdDataDictionaryUsedQueryItem;
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
public interface CdDataDictionaryService extends JpaService<CdDataDictionaryEntity, CdDataDictionaryDao, Long> {
    /**
     * 根据字典分类UUID获取总数
     *
     * @param categoryUuid
     * @return
     */
    Long countByCategoryUuid(Long categoryUuid);

    /**
     * 根据字典编码获取总数
     *
     * @param code
     * @return
     */
    Long countByCode(String code);

    /**
     * 根据模块ID获取数据
     *
     * @param moduleId
     * @return
     */
    Long countByModuleId(String moduleId);

    /**
     * 根据字典编码获取字典
     *
     * @param code
     * @return
     */
    CdDataDictionaryEntity getByCode(String code);

    /**
     * 获取数据字典树
     *
     * @return
     */
    TreeNode getAllAsCategoryTree();

    /**
     * 检测字典被使用
     *
     * @param uuids
     * @return
     */
    List<CdDataDictionaryUsedQueryItem> listUsedQueryItemByUuids(List<Long> uuids);

    /**
     * 获取数据字典项树
     *
     * @param uuid
     * @return
     */
    TreeNode listItemAsTreeByUuid(Long uuid);

    /**
     * 获取数据字典项树
     *
     * @param code
     * @return
     */
    TreeNode listItemAsTreeByCode(String code);

    void batchSaveDataDicItems(Long uuid, List<CdDataDictionaryItemDto> items);
}
