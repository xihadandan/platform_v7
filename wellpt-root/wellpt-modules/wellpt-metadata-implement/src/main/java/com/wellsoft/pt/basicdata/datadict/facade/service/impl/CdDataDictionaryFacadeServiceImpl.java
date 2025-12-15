/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.entity.SysEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.app.service.AppCodeI18nService;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryCategoryDto;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryDto;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.datadict.entity.*;
import com.wellsoft.pt.basicdata.datadict.enums.EnumDictionaryCategoryType;
import com.wellsoft.pt.basicdata.datadict.facade.service.CdDataDictionaryFacadeService;
import com.wellsoft.pt.basicdata.datadict.query.CdDataDictionaryUsedQueryItem;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryCategoryService;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryItemAttributeService;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryItemService;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryService;
import com.wellsoft.pt.common.i18n.entity.I18nEntity;
import com.wellsoft.pt.common.i18n.service.DataI18nService;
import com.wellsoft.pt.common.translate.service.TranslateService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
@Service
public class CdDataDictionaryFacadeServiceImpl extends AbstractApiFacade implements CdDataDictionaryFacadeService {

    @Autowired
    private CdDataDictionaryCategoryService dataDictionaryCategoryService;

    @Autowired
    private CdDataDictionaryService dataDictionaryService;

    @Autowired
    private CdDataDictionaryItemService dataDictionaryItemService;

    @Autowired
    private CdDataDictionaryItemAttributeService dataDictionaryItemAttributeService;

    @Autowired
    private DataI18nService dataI18nService;

    @Autowired
    private AppCodeI18nService appCodeI18nService;

    @Autowired
    private TranslateService translateService;

    /**
     * 保存字典分类
     *
     * @param categoryDto
     * @return
     */
    @Override
    public Long saveDataDictionaryCategory(CdDataDictionaryCategoryDto categoryDto) {
        CdDataDictionaryCategoryEntity entity = null;
        if (categoryDto.getUuid() != null) {
            entity = dataDictionaryCategoryService.getOne(categoryDto.getUuid());
        } else {
            entity = new CdDataDictionaryCategoryEntity();
        }
        BeanUtils.copyProperties(categoryDto, entity, JpaEntity.BASE_FIELDS);
        dataDictionaryCategoryService.save(entity);
        return entity.getUuid();
    }

    /**
     * 根据字典分类UUID删除字典分类
     *
     * @param categoryUuid
     */
    @Override
    public void deleteDataDictionaryCategoryByUuid(Long categoryUuid) {
        dataDictionaryCategoryService.deleteByUuid(categoryUuid);
    }

    /**
     * 根据模块ID获取字典分类
     *
     * @param moduleId
     * @return
     */
    @Override
    public List<CdDataDictionaryCategoryDto> listDataDictionaryCategoryByModuleId(String moduleId) {
        List<CdDataDictionaryCategoryEntity> entities = dataDictionaryCategoryService.listByModuleId(moduleId);
        return BeanUtils.copyCollection(entities, CdDataDictionaryCategoryDto.class);
    }

    /**
     * 保存数据字典
     *
     * @param dataDictionaryDto
     * @return
     */
    @Override
    @Transactional
    public Long saveDataDictionary(CdDataDictionaryDto dataDictionaryDto) {
        CdDataDictionaryEntity entity = null;
        if (dataDictionaryDto.getUuid() != null) {
            entity = dataDictionaryService.getOne(dataDictionaryDto.getUuid());
        } else {
            // 字典编码唯一性判断
            if (this.dataDictionaryService.countByCode(dataDictionaryDto.getCode()) > 0) {
                throw new RuntimeException(String.format("已经存在字典编码为[%s]的字典!", dataDictionaryDto.getCode()));
            }
            entity = new CdDataDictionaryEntity();
        }
        BeanUtils.copyProperties(dataDictionaryDto, entity, JpaEntity.BASE_FIELDS);
        dataDictionaryService.save(entity);

        // 字典子项
        List<CdDataDictionaryItemDto> itemDtos = Lists.newArrayList();
        // 树形结构转为列表
        traverseAsList(dataDictionaryDto.getItems(), itemDtos);

        List<CdDataDictionaryItemEntity> itemEntities = dataDictionaryItemService.listByDataDictUuid(entity.getUuid());
        // 删除的子项
        List<CdDataDictionaryItemEntity> deletedItemEnties = Lists.newArrayList();
        itemEntities.forEach(itemEntitiy -> {
            CdDataDictionaryItemDto itemDto = itemDtos.stream().filter(item -> itemEntitiy.getUuid().equals(item.getUuid())).findFirst().orElse(null);
            if (itemDto == null) {
                deletedItemEnties.add(itemEntitiy);
            }
        });
        // 删除前端删除的子项
        if (CollectionUtils.isNotEmpty(deletedItemEnties)) {
            // 删除扩展属性
            List<Long> deletedItemUuids = deletedItemEnties.stream().map(deletedEntity -> deletedEntity.getUuid()).collect(Collectors.toList());
            dataDictionaryItemAttributeService.deleteByItemUuids(deletedItemUuids);
            dataDictionaryItemService.deleteByEntities(deletedItemEnties);
            dataI18nService.deleteByDataUuids(deletedItemUuids, CdDataDictI18nEntity.class);
            itemEntities.removeAll(deletedItemEnties);
        }

        // 遍历并保存
        Map<Long, CdDataDictionaryItemEntity> itemEntityMap = ConvertUtils.convertElementToMap(itemEntities, "uuid");
        traverseAndSave(null, dataDictionaryDto.getItems(), itemEntityMap, entity.getUuid(), 0);

        return entity.getUuid();
    }

    private void traverseAsList(List<CdDataDictionaryItemDto> items, List<CdDataDictionaryItemDto> itemDtos) {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }

        items.forEach(item -> {
            itemDtos.add(item);

            traverseAsList(item.getChildren(), itemDtos);
        });
    }

    private void traverseAndSave(CdDataDictionaryItemDto parentItemDto, List<CdDataDictionaryItemDto> itemDtos, Map<Long, CdDataDictionaryItemEntity> itemEntityMap,
                                 Long dataDictUuid, int level) {
        if (CollectionUtils.isEmpty(itemDtos)) {
            return;
        }

        int index = level * 10;
        for (CdDataDictionaryItemDto itemDto : itemDtos) {
            CdDataDictionaryItemEntity itemEntity = null;
            Map<String, String> attrs = itemDto.getAttrs();
            // 子项扩展属性
            List<CdDataDictionaryItemAttributeEntity> attributeEntities = Lists.newArrayList();
            List<CdDataDictionaryItemAttributeEntity> deletedAttributeEntities = Lists.newArrayList();
            if (itemEntityMap.get(itemDto.getUuid()) != null) {
                itemEntity = itemEntityMap.get(itemDto.getUuid());
                attributeEntities = dataDictionaryItemAttributeService.listByItemUuid(itemDto.getUuid());
                attributeEntities.forEach(attributeEntity -> {
                    if (!attrs.containsKey(attributeEntity.getAttrKey())) {
                        deletedAttributeEntities.add(attributeEntity);
                    }
                });
            } else {
                itemEntity = new CdDataDictionaryItemEntity();
            }

            // 保存子项
            if (parentItemDto == null) {
                itemDto.setParentUuid(null);
            }
            BeanUtils.copyProperties(itemDto, itemEntity, SysEntity.BASE_FIELDS);
            itemEntity.setSortOrder(index++);
            itemEntity.setDataDictUuid(dataDictUuid);
            dataDictionaryItemService.save(itemEntity);
            if (CollectionUtils.isNotEmpty(itemDto.getI18ns())) {
                for (CdDataDictI18nEntity i : itemDto.getI18ns()) {
                    i.setDataUuid(itemEntity.getUuid());
                    i.setUuid(null);
                }
                dataI18nService.saveAll(itemEntity.getUuid(), itemDto.getI18ns(), CdDataDictI18nEntity.class);
            }


            // 删除的子欺扩展属性
            if (CollectionUtils.isNotEmpty(deletedAttributeEntities)) {
                dataDictionaryItemAttributeService.deleteByEntities(deletedAttributeEntities);
                attributeEntities.removeAll(deletedAttributeEntities);
            }
            // 保存子项扩展属性
            saveDataDictionaryItemAttribute(itemEntity, attrs, attributeEntities);

            List<CdDataDictionaryItemDto> children = itemDto.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                // 设置上级UUID
                Long parentUuid = itemEntity.getUuid();
                children.forEach(child -> child.setParentUuid(parentUuid));

                traverseAndSave(itemDto, children, itemEntityMap, dataDictUuid, level + 1);
            }
        }
    }

    /**
     * 保存子项扩展属性
     *
     * @param itemEntity
     * @param attrs
     * @param attributeEntities
     */
    private void saveDataDictionaryItemAttribute(CdDataDictionaryItemEntity itemEntity, Map<String, String> attrs, List<CdDataDictionaryItemAttributeEntity> attributeEntities) {
        Map<String, CdDataDictionaryItemAttributeEntity> attributeEntityMap = ConvertUtils.convertElementToMap(attributeEntities, "attrKey");
        List<CdDataDictionaryItemAttributeEntity> entities = Lists.newArrayList();
        for (Map.Entry<String, String> entry : attrs.entrySet()) {
            String attrKey = entry.getKey();
            String attrVal = entry.getValue();
            CdDataDictionaryItemAttributeEntity entity = null;
            if (attributeEntityMap.containsKey(attrKey)) {
                entity = attributeEntityMap.get(attrKey);
                entity.setAttrVal(attrVal);
            } else {
                entity = new CdDataDictionaryItemAttributeEntity();
                entity.setAttrKey(attrKey);
                entity.setAttrVal(attrVal);
            }
            entity.setItemUuid(itemEntity.getUuid());
            entities.add(entity);
        }
        dataDictionaryItemAttributeService.saveAll(entities);
    }

    /**
     * 根据字典UUID获取字典数据
     *
     * @param uuid
     * @return
     */
    @Override
    public CdDataDictionaryDto getDataDictionaryByUuid(Long uuid) {
        CdDataDictionaryDto dto = new CdDataDictionaryDto();
        CdDataDictionaryEntity entity = dataDictionaryService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);

            // 获取字典项列表树
            List<CdDataDictionaryItemDto> itemTreeDtos = listDictionaryItemTree(entity.getUuid(), null);
            dto.setItems(itemTreeDtos);
        }
        return dto;
    }

    /**
     * 根据字典编码获取字典数据
     *
     * @param code
     * @return
     */
    @Override
    public CdDataDictionaryDto getDataDictionaryByCode(String code) {
        return this.getLocaleDataDictionaryByCode(code, null);
    }

    @Override
    public CdDataDictionaryDto getDataDictionaryByCode(String code, boolean fetchAllItems) {
        return this.getLocaleDataDictionaryByCode(code, null, fetchAllItems);
    }

    @Override
    public CdDataDictionaryDto getLocaleDataDictionaryByCode(String code, String locale) {
        return this.getLocaleDataDictionaryByCode(code, locale, true);
    }

    public CdDataDictionaryDto getLocaleDataDictionaryByCode(String code, String locale, boolean fetchAllItems) {
        CdDataDictionaryDto dto = new CdDataDictionaryDto();
        CdDataDictionaryEntity entity = dataDictionaryService.getByCode(code);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
            // 获取字典项列表树
            if (fetchAllItems) {
                List<CdDataDictionaryItemDto> itemTreeDtos = listDictionaryItemTree(entity.getUuid(), locale);
                dto.setItems(itemTreeDtos);
            } else {
                List<CdDataDictionaryItemDto> itemTreeDtos = listDictionaryItem(entity.getUuid(), locale);
                dto.setItems(itemTreeDtos);
            }
        }
        return dto;
    }

    private List<CdDataDictionaryItemDto> listDictionaryItem(Long dictionaryUuid, String locale) {
        List<CdDataDictionaryItemEntity> itemEntities = dataDictionaryItemService.listRootByDataDictUuid(dictionaryUuid);
        List<CdDataDictionaryItemDto> itemDtos = convert2ItemDtos(itemEntities, locale);
        markParentItemFlag(itemDtos);
        return itemDtos;
    }

    private List<CdDataDictionaryItemDto> convert2ItemDtos(List<CdDataDictionaryItemEntity> itemEntities, String locale) {
        List<Long> itemUuids = itemEntities.stream().map(itemEntity -> itemEntity.getUuid()).collect(Collectors.toList());
        List<CdDataDictionaryItemAttributeEntity> itemAttributeEntities = Lists.newArrayListWithCapacity(0);
        Map<Long, List<CdDataDictI18nEntity>> map = Maps.newHashMap();
        Map<Long, String> localeNames = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(itemUuids)) {
            itemAttributeEntities = dataDictionaryItemAttributeService.listByItemUuids(itemUuids);
            List<CdDataDictI18nEntity> i18nEntities = dataI18nService.listByDataUuids(CdDataDictI18nEntity.class, itemUuids, locale);
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                for (CdDataDictI18nEntity i18nEntity : i18nEntities) {
                    if (!map.containsKey(i18nEntity.getDataUuid())) {
                        map.put(i18nEntity.getDataUuid(), Lists.newArrayList());
                    }
                    map.get(i18nEntity.getDataUuid()).add(i18nEntity);
                    if (StringUtils.isNotBlank(locale) && i18nEntity.getLocale().equals(locale)) {
                        localeNames.put(i18nEntity.getDataUuid(), i18nEntity.getContent());
                    }
                }
            }
        }

        List<CdDataDictionaryItemDto> itemDtos = BeanUtils.copyCollection(itemEntities, CdDataDictionaryItemDto.class);
        if (!map.isEmpty()) {
            for (CdDataDictionaryItemDto dto : itemDtos) {
                if (map.containsKey(dto.getUuid())) {
                    dto.setI18ns(map.get(dto.getUuid()));
                    if (StringUtils.isNotBlank(locale) && localeNames.containsKey(dto.getUuid())) {
                        dto.setLabel(localeNames.get(dto.getUuid()));
                    }
                }
            }
        }


        // 设置扩展属性
        Map<Long, List<CdDataDictionaryItemAttributeEntity>> itemAttributesMap = ListUtils.list2group(itemAttributeEntities, "itemUuid");
        itemDtos.forEach(item -> {
            List<CdDataDictionaryItemAttributeEntity> attributeEntities = itemAttributesMap.get(item.getUuid());
            if (CollectionUtils.isNotEmpty(attributeEntities)) {
                attributeEntities.forEach(attributeEntity -> item.getAttrs().put(attributeEntity.getAttrKey(), attributeEntity.getAttrVal()));
            }
        });
        return itemDtos;
    }

    /**
     * 获取字典项列表树
     *
     * @param dictionaryUuid
     * @return
     */
    private List<CdDataDictionaryItemDto> listDictionaryItemTree(Long dictionaryUuid, String locale) {
        List<CdDataDictionaryItemEntity> itemEntities = dataDictionaryItemService.listByDataDictUuid(dictionaryUuid);
        List<CdDataDictionaryItemDto> itemDtos = convert2ItemDtos(itemEntities, locale);
        // 构建树形结构
        List<CdDataDictionaryItemDto> itemTreeDtos = buildDataDictionaryItemTree(itemDtos);
        return itemTreeDtos;
    }

    /**
     * 构建树形结构
     *
     * @param itemDtos
     * @return
     */
    private List<CdDataDictionaryItemDto> buildDataDictionaryItemTree(List<CdDataDictionaryItemDto> itemDtos) {
        Map<Long, CdDataDictionaryItemDto> itemDtoMap = ConvertUtils.convertElementToMap(itemDtos, "uuid");
        itemDtos.forEach(item -> {
//            // 设置扩展属性
//            List<CdDataDictionaryItemAttributeEntity> attributeEntities = itemAttributesMap.get(item.getUuid());
//            if (CollectionUtils.isNotEmpty(attributeEntities)) {
//                attributeEntities.forEach(attributeEntity -> item.getAttrs().put(attributeEntity.getAttrKey(), attributeEntity.getAttrVal()));
//            }

            // 设置子节点
            Long parentUuid = item.getParentUuid();
            if (parentUuid != null) {
                CdDataDictionaryItemDto parentItem = itemDtoMap.get(parentUuid);
                if (parentItem != null) {
                    List<CdDataDictionaryItemDto> children = parentItem.getChildren();
                    if (children == null) {
                        children = Lists.newArrayList();
                        parentItem.setChildren(children);
                    }
                    children.add(item);
                }
            }
        });
        List<CdDataDictionaryItemDto> treeDtos = itemDtos.stream().filter(item -> item.getParentUuid() == null).collect(Collectors.toList());
        return treeDtos;
    }


    /**
     * 根据字典UUID列表删除字典数据
     *
     * @param uuids
     */
    @Override
    @Transactional
    public void deleteDataDictionaryByUuids(List<Long> uuids) {
        // 判断字典是否已经被使用
        List<CdDataDictionaryUsedQueryItem> usedQueryItems = dataDictionaryService.listUsedQueryItemByUuids(uuids);
        if (CollectionUtils.isNotEmpty(usedQueryItems)) {
            List<String> msgs = Lists.newArrayList();
            usedQueryItems.forEach(item -> {
                String itemName = AppFunctionType.AppPageDefinition.equals(item.getFunctionType()) ?
                        "页面" + item.getFunctionName() : "表单" + item.getFunctionName() + "\\" + item.getFunctionItemName();
                msgs.add(String.format("字典[%s]被[%s]使用，不能删除", item.getName(), itemName));
            });
            throw new BusinessException(StringUtils.join(msgs, "、"));
        }

        // 删除子项
        dataDictionaryItemService.deleteByDataDictUuids(uuids);
        // 删除字典
        List<CdDataDictionaryEntity> entities = dataDictionaryService.listByUuids(uuids);
        dataDictionaryService.deleteByEntities(entities);
    }

    /**
     * 根据模块ID获取字典数量
     *
     * @param moduleId
     * @return
     */
    @Override
    public Long countDataDictionaryByModuleId(String moduleId) {
        if (StringUtils.isBlank(moduleId)) {
            return 0l;
        }
        return dataDictionaryService.countByModuleId(moduleId);
    }

    /**
     * 根据字典分类类型获取字典分类
     *
     * @param categoryType
     * @return
     */
    @Override
    public List<CdDataDictionaryCategoryDto> listDataDictionaryCategoryByType(EnumDictionaryCategoryType categoryType) {
        List<CdDataDictionaryCategoryEntity> entities = dataDictionaryCategoryService.listByType(categoryType);
        return BeanUtils.copyCollection(entities, CdDataDictionaryCategoryDto.class);
    }

    /**
     * 根据字典编码获取字典项
     *
     * @param dictionaryCode
     * @return
     */
    @Override
    public List<CdDataDictionaryItemDto> listItemByDictionaryCode(String dictionaryCode) {
        if (StringUtils.isBlank(dictionaryCode)) {
            return Collections.emptyList();
        }
        CdDataDictionaryEntity dictionaryEntity = dataDictionaryService.getByCode(dictionaryCode);
        if (dictionaryEntity == null) {
            return Collections.emptyList();
        }
        return listDictionaryItemTree(dictionaryEntity.getUuid(), LocaleContextHolder.getLocale().toString());
    }

    @Override
    public List<CdDataDictionaryItemDto> listItemByDictionaryCode(String dictionaryCode, boolean fetchAllItems) {
        if (StringUtils.isBlank(dictionaryCode)) {
            return Collections.emptyList();
        }
        CdDataDictionaryEntity dictionaryEntity = dataDictionaryService.getByCode(dictionaryCode);
        if (dictionaryEntity == null) {
            return Collections.emptyList();
        }
        if (fetchAllItems) {
            return listDictionaryItemTree(dictionaryEntity.getUuid(), LocaleContextHolder.getLocale().toString());
        } else {
            return listDictionaryItem(dictionaryEntity.getUuid(), LocaleContextHolder.getLocale().toString());
        }
    }

    @Override
    public List<CdDataDictionaryItemDto> listItemByParentItemUuid(Long parentItemUuid) {
        List<CdDataDictionaryItemEntity> itemEntities = dataDictionaryItemService.listByParentUuid(parentItemUuid);
        List<CdDataDictionaryItemDto> itemDtos = convert2ItemDtos(itemEntities, LocaleContextHolder.getLocale().toString());
        markParentItemFlag(itemDtos);
        return itemDtos;
    }

    private void markParentItemFlag(List<CdDataDictionaryItemDto> itemDtos) {
        if (CollectionUtils.isEmpty(itemDtos)) {
            return;
        }

        List<Long> parentItemUuids = itemDtos.stream().map(CdDataDictionaryItemDto::getUuid).collect(Collectors.toList());
        List<CdDataDictionaryItemEntity> childItems = dataDictionaryItemService.listByParentUuids(parentItemUuids);
        if (CollectionUtils.isNotEmpty(childItems)) {
            itemDtos.forEach(itemDto -> {
                itemDto.setParent(childItems.stream().filter(item -> itemDto.getUuid().equals(item.getParentUuid())).findFirst().isPresent());
            });
        }
    }

    @Override
    public List<CdDataDictionaryItemDto> listLocaleItemByDictionaryCode(String dictionaryCode) {
        if (StringUtils.isBlank(dictionaryCode)) {
            return Collections.emptyList();
        }
        CdDataDictionaryEntity dictionaryEntity = dataDictionaryService.getByCode(dictionaryCode);
        if (dictionaryEntity == null) {
            return Collections.emptyList();
        }
        return listDictionaryItemTree(dictionaryEntity.getUuid(), !LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString()) ? LocaleContextHolder.getLocale().toString() : null);
    }


    /**
     * 根据字典编码及字典项值获取字典项
     *
     * @param dictionaryCode
     * @param itemValue
     * @return
     */
    @Override
    public CdDataDictionaryItemDto getItemByDictionaryCodeAndItemValue(String dictionaryCode, String itemValue) {
        CdDataDictionaryItemDto dto = new CdDataDictionaryItemDto();
        CdDataDictionaryEntity dictionaryEntity = dataDictionaryService.getByCode(dictionaryCode);
        if (dictionaryEntity == null) {
            return dto;
        }

        CdDataDictionaryItemEntity itemEntity = dataDictionaryItemService.getByValueAndDataDictUuid(itemValue, dictionaryEntity.getUuid());
        if (itemEntity != null) {
            BeanUtils.copyProperties(itemEntity, dto);

            // 扩展属性
            List<CdDataDictionaryItemAttributeEntity> attributeEntities = dataDictionaryItemAttributeService.listByItemUuid(itemEntity.getUuid());
            attributeEntities.forEach(attribute -> dto.getAttrs().put(attribute.getAttrKey(), attribute.getAttrVal()));
        }
        return dto;
    }

    /**
     * 根据字典项UUID获取字典项
     *
     * @param itemUuid
     * @return
     */
    @Override
    public CdDataDictionaryItemDto getItemByItemUuid(Long itemUuid) {
        CdDataDictionaryItemDto dto = new CdDataDictionaryItemDto();
        CdDataDictionaryItemEntity itemEntity = dataDictionaryItemService.getOne(itemUuid);
        if (itemEntity != null) {
            BeanUtils.copyProperties(itemEntity, dto);

            // 扩展属性
            List<CdDataDictionaryItemAttributeEntity> attributeEntities = dataDictionaryItemAttributeService.listByItemUuid(itemEntity.getUuid());
            attributeEntities.forEach(attribute -> dto.getAttrs().put(attribute.getAttrKey(), attribute.getAttrVal()));
        }
        return dto;
    }

    /**
     * 保存字典项
     *
     * @param dictionaryItemDto
     * @return
     */
    @Override
    @Transactional
    public Long saveItem(CdDataDictionaryItemDto dictionaryItemDto) {
        Long uuid = dictionaryItemDto.getUuid();
        CdDataDictionaryItemEntity itemEntity = null;
        if (uuid != null) {
            itemEntity = this.dataDictionaryItemService.getOne(uuid);
        } else {
            itemEntity = new CdDataDictionaryItemEntity();
        }
        BeanUtils.copyProperties(dictionaryItemDto, itemEntity, JpaEntity.BASE_FIELDS);
        this.dataDictionaryItemService.save(itemEntity);

        // 保存扩展属性
        Map<String, String> attrs = dictionaryItemDto.getAttrs();
        if (MapUtils.isNotEmpty(attrs)) {
            List<CdDataDictionaryItemAttributeEntity> attributeEntities = this.dataDictionaryItemAttributeService.listByItemUuid(itemEntity.getUuid());
            List<CdDataDictionaryItemAttributeEntity> deletedAttributeEntities = Lists.newArrayList();
            attributeEntities.forEach(attributeEntity -> {
                if (!attrs.containsKey(attributeEntity.getAttrKey())) {
                    deletedAttributeEntities.add(attributeEntity);
                }
            });
            // 删除的子欺扩展属性
            if (CollectionUtils.isNotEmpty(deletedAttributeEntities)) {
                this.dataDictionaryItemAttributeService.deleteByEntities(deletedAttributeEntities);
                attributeEntities.removeAll(deletedAttributeEntities);
            }
            // 保存子项扩展属性
            saveDataDictionaryItemAttribute(itemEntity, attrs, attributeEntities);
        }

        // 保存子项
        List<CdDataDictionaryItemDto> children = dictionaryItemDto.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            for (CdDataDictionaryItemDto child : children) {
                this.saveItem(child);
            }
        }

        return itemEntity.getUuid();
    }

    /**
     * 更新字典项排序
     *
     * @param sortOrderMap
     */
    @Override
    public void updateItemSortOrder(Map<Long, Integer> sortOrderMap) {
        if (MapUtils.isEmpty(sortOrderMap)) {
            return;
        }

        for (Map.Entry<Long, Integer> entry : sortOrderMap.entrySet()) {
            this.dataDictionaryItemService.updateSortOrderByUuid(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 根据字典项UUID删除字典项
     *
     * @param itemUuid
     */
    @Override
    @Transactional
    public void deleteItemByItemUuid(Long itemUuid) {
        CdDataDictionaryItemEntity itemEntity = this.dataDictionaryItemService.getOne(itemUuid);
        if (itemEntity == null) {
            return;
        }

        // 删除子项
        List<CdDataDictionaryItemEntity> children = this.dataDictionaryItemService.listByParentUuid(itemUuid);
        for (CdDataDictionaryItemEntity child : children) {
            deleteItemByItemUuid(child.getUuid());
        }

        // 删除扩展属性
        this.dataDictionaryItemAttributeService.deleteByItemUuids(Lists.newArrayList(itemUuid));

        // 删除字典项
        this.dataDictionaryItemService.delete(itemEntity);
    }

    /**
     * 获取数据字典树
     *
     * @return
     */
    @Override
    public TreeNode getAllDataDictionaryAsCategoryTree() {
        return dataDictionaryService.getAllAsCategoryTree();
    }

    @Override
    public TreeNode listItemAsTreeByDictionaryUuid(Long uuid) {
        return dataDictionaryService.listItemAsTreeByUuid(uuid);
    }

    @Override
    public TreeNode listItemAsTreeByDictionaryCode(String code) {
        return dataDictionaryService.listItemAsTreeByCode(code);
    }

    @Override
    @Transactional
    public void translateAllDataDic(Long uuid, Boolean onlyTranslateEmpty) {
        List<CdDataDictionaryItemEntity> itemEntities = dataDictionaryItemService.listByDataDictUuid(uuid);
        List<I18nEntity> i18ns = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(itemEntities)) {
            Map<Long, CdDataDictionaryItemEntity> itemMap = Maps.newHashMap();
            Set<String> allLabels = Sets.newHashSet();
            for (CdDataDictionaryItemEntity item : itemEntities) {
                itemMap.put(item.getUuid(), item);
                allLabels.add(item.getLabel());
            }
            Set<String> locales = appCodeI18nService.getAllLocaleString();
            Set<Long> uuids = itemMap.keySet();

            for (String locale : locales) {
                if (locale.equalsIgnoreCase(Locale.SIMPLIFIED_CHINESE.toString())) {
                    continue;
                }
                if (BooleanUtils.isTrue(onlyTranslateEmpty)) {
                    List<CdDataDictI18nEntity> i18nEntities = dataI18nService.listByDataUuids(CdDataDictI18nEntity.class, uuids, locale);
                    Set<Long> lackUuids = Sets.newHashSet(uuids);
                    Set<String> labels = Sets.newHashSet(allLabels);
                    if (CollectionUtils.isNotEmpty(i18nEntities)) {
                        for (CdDataDictI18nEntity e : i18nEntities) {
                            if (lackUuids.contains(e.getDataUuid())) {
                                lackUuids.remove(e.getDataUuid());
                                labels.remove(itemMap.get(e.getDataUuid()).getLabel());
                            }
                        }
                    }
                    if (!lackUuids.isEmpty()) {
                        Map<String, String> result = translateService.translate(labels, "zh", locale.split(Separator.UNDERLINE.getValue())[0]);
                        for (Long id : lackUuids) {
                            CdDataDictionaryItemEntity entity = itemMap.get(id);
                            if (result.containsKey(entity.getLabel().trim())) {
                                i18ns.add(new CdDataDictI18nEntity(entity.getUuid(), null, "label", locale, result.get(entity.getLabel().trim())));
                            }
                        }
                    }
                } else {
                    Map<String, String> result = translateService.translate(allLabels, "zh", locale.split(Separator.UNDERLINE.getValue())[0]);
                    for (CdDataDictionaryItemEntity entity : itemEntities) {
                        dataI18nService.deleteByDataUuid(entity.getUuid(), CdDataDictI18nEntity.class);
                        if (result.containsKey(entity.getLabel().trim())) {
                            i18ns.add(new CdDataDictI18nEntity(entity.getUuid(), null, "label", locale, result.get(entity.getLabel().trim())));
                        }
                    }
                }
            }
            if (!i18ns.isEmpty()) {
                dataI18nService.saveAll(i18ns);
            }
        }
    }

    @Override
    public void batchSaveDataDicItems(Long uuid, List<CdDataDictionaryItemDto> items) {
        dataDictionaryService.batchSaveDataDicItems(uuid, items);
    }

}
