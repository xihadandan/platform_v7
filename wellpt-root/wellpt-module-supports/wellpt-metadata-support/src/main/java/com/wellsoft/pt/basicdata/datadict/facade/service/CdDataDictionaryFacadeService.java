/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryCategoryDto;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryDto;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.datadict.enums.EnumDictionaryCategoryType;

import java.util.List;
import java.util.Map;

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
public interface CdDataDictionaryFacadeService extends Facade {
    /**
     * 保存字典分类
     *
     * @param categoryDto
     * @return
     */
    Long saveDataDictionaryCategory(CdDataDictionaryCategoryDto categoryDto);

    /**
     * 根据字典分类UUID删除字典分类
     *
     * @param categoryUuid
     */
    void deleteDataDictionaryCategoryByUuid(Long categoryUuid);

    /**
     * 根据模块ID获取字典分类
     *
     * @param moduleId
     * @return
     */
    List<CdDataDictionaryCategoryDto> listDataDictionaryCategoryByModuleId(String moduleId);

    /**
     * 保存数据字典
     *
     * @param dataDictionaryDto
     * @return
     */
    Long saveDataDictionary(CdDataDictionaryDto dataDictionaryDto);

    /**
     * 根据字典UUID获取字典数据
     *
     * @param uuid
     * @return
     */
    CdDataDictionaryDto getDataDictionaryByUuid(Long uuid);

    /**
     * 根据字典编码获取字典数据
     *
     * @param code
     * @return
     */
    CdDataDictionaryDto getDataDictionaryByCode(String code);

    CdDataDictionaryDto getDataDictionaryByCode(String code, boolean fetchAllItems);

    CdDataDictionaryDto getLocaleDataDictionaryByCode(String code, String locale);

    /**
     * 根据字典UUID列表删除字典数据
     *
     * @param uuids
     */
    void deleteDataDictionaryByUuids(List<Long> uuids);

    /**
     * 根据模块ID获取字典数量
     *
     * @param moduleId
     * @return
     */
    Long countDataDictionaryByModuleId(String moduleId);

    /**
     * 根据字典分类类型获取字典分类
     *
     * @param categoryType
     * @return
     */
    List<CdDataDictionaryCategoryDto> listDataDictionaryCategoryByType(EnumDictionaryCategoryType categoryType);

    /**
     * 根据字典编码获取字典项
     *
     * @param dictionaryCode
     * @return
     */
    List<CdDataDictionaryItemDto> listItemByDictionaryCode(String dictionaryCode);

    /**
     * 根据字典编码获取字典项
     *
     * @param dictionaryCode
     * @param fetchAllItems
     * @return
     */
    List<CdDataDictionaryItemDto> listItemByDictionaryCode(String dictionaryCode, boolean fetchAllItems);

    /**
     * 根据字典项上级UUID获取子级字典项
     *
     * @param parentItemUuid
     * @return
     */
    List<CdDataDictionaryItemDto> listItemByParentItemUuid(Long parentItemUuid);

    List<CdDataDictionaryItemDto> listLocaleItemByDictionaryCode(String dictionaryCode);


    /**
     * 根据字典编码及字典项值获取字典项
     *
     * @param dictionaryCode
     * @param itemValue
     * @return
     */
    CdDataDictionaryItemDto getItemByDictionaryCodeAndItemValue(String dictionaryCode, String itemValue);

    /**
     * 根据字典项UUID获取字典项
     *
     * @param itemUuid
     * @return
     */
    CdDataDictionaryItemDto getItemByItemUuid(Long itemUuid);

    /**
     * 保存字典项
     *
     * @param dictionaryItemDto
     * @return
     */
    Long saveItem(CdDataDictionaryItemDto dictionaryItemDto);

    /**
     * 更新字典项排序
     *
     * @param sortOrderMap
     */
    void updateItemSortOrder(Map<Long, Integer> sortOrderMap);

    /**
     * 根据字典项UUID删除字典项
     *
     * @param itemUuid
     */
    void deleteItemByItemUuid(Long itemUuid);

    /**
     * 获取数据字典树
     *
     * @return
     */
    TreeNode getAllDataDictionaryAsCategoryTree();

    /**
     * 获取数据字典项树
     *
     * @param uuid
     * @return
     */
    TreeNode listItemAsTreeByDictionaryUuid(Long uuid);

    /**
     * 获取数据字典项树
     *
     * @param code
     * @return
     */
    TreeNode listItemAsTreeByDictionaryCode(String code);

    void translateAllDataDic(Long uuid, Boolean onlyTranslateEmpty);

    void batchSaveDataDicItems(Long uuid, List<CdDataDictionaryItemDto> items);
}
