/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.tree.TreeUtils;
import com.wellsoft.pt.basicdata.datadict.dao.CdDataDictionaryDao;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryCategoryEntity;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryEntity;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryItemEntity;
import com.wellsoft.pt.basicdata.datadict.query.CdDataDictionaryUsedQueryItem;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryCategoryService;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryItemService;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryService;
import com.wellsoft.pt.common.i18n.service.DataI18nService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
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
public class CdDataDictionaryServiceImpl extends AbstractJpaServiceImpl<CdDataDictionaryEntity, CdDataDictionaryDao, Long>
        implements CdDataDictionaryService {

    @Autowired
    private CdDataDictionaryCategoryService dataDictionaryCategoryService;

    @Autowired
    private CdDataDictionaryItemService dataDictionaryItemService;


    @Autowired
    private DataI18nService dataI18nService;

    /**
     * 根据字典分类UUID获取总数
     *
     * @param categoryUuid
     * @return
     */
    @Override
    public Long countByCategoryUuid(Long categoryUuid) {
        Assert.notNull(categoryUuid, "字典分类UUID不能为空！");

        CdDataDictionaryEntity entity = new CdDataDictionaryEntity();
        entity.setCategoryUuid(categoryUuid);
        return this.dao.countByEntity(entity);
    }

    /**
     * 根据字典编码获取总数
     *
     * @param code
     * @return
     */
    @Override
    public Long countByCode(String code) {
        Assert.hasLength(code, "字典编码不能为空！");

        CdDataDictionaryEntity entity = new CdDataDictionaryEntity();
        entity.setCode(code);
        return this.dao.countByEntity(entity);
    }

    /**
     * 根据模块ID获取数据
     *
     * @param moduleId
     * @return
     */
    @Override
    public Long countByModuleId(String moduleId) {
        Assert.hasLength(moduleId, "模块ID不能为空！");

        CdDataDictionaryEntity entity = new CdDataDictionaryEntity();
        entity.setModuleId(moduleId);
        return this.dao.countByEntity(entity);
    }

    /**
     * 根据字典编码获取字典
     *
     * @param code
     * @return
     */
    @Override
    public CdDataDictionaryEntity getByCode(String code) {
        Assert.hasLength(code, "字典编码不能为空！");

        CdDataDictionaryEntity entity = new CdDataDictionaryEntity();
        entity.setCode(code);
        List<CdDataDictionaryEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 获取数据字典树
     *
     * @return
     */
    @Override
    public TreeNode getAllAsCategoryTree() {
        TreeNode rootNode = new TreeNode();
        rootNode.setId("-1");
        rootNode.setName("数据字典");
        List<CdDataDictionaryCategoryEntity> categoryEntities = dataDictionaryCategoryService.listAllByTypeAsc();
        List<CdDataDictionaryEntity> entities = this.listAll();
        Map<Long, List<CdDataDictionaryEntity>> entityMap = ListUtils.list2group(entities, "categoryUuid");
        categoryEntities.forEach(category -> {
            TreeNode childNode = new TreeNode();
            childNode.setId(category.getUuid().toString());
            childNode.setName(category.getName());
            childNode.setNocheck(true);
            if (entityMap.containsKey(category.getUuid())) {
                addChildren(childNode, entityMap.remove(category.getUuid()));
            }
            rootNode.getChildren().add(childNode);
        });
        // 添加分类不存在的数据字典
        List<CdDataDictionaryEntity> addToRoot = Lists.newLinkedList();
        for (Map.Entry<Long, List<CdDataDictionaryEntity>> entry : entityMap.entrySet()) {
            addToRoot.addAll(entry.getValue());
            addChildren(rootNode, entry.getValue());
        }
        // 添加无分类的数据字典
        addChildren(rootNode, entities.stream().filter(entity -> entity.getCategoryUuid() == null && !addToRoot.contains(entity)).collect(Collectors.toList()));
        return rootNode;
    }

    /**
     * 检测字典被使用
     *
     * @param uuids
     * @return
     */
    @Override
    public List<CdDataDictionaryUsedQueryItem> listUsedQueryItemByUuids(List<Long> uuids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuids", uuids);
        params.put("whereSql", "function_type is not null");
        return this.dao.listItemByNameSQLQuery("cdDataDictionaryUsedQuery", CdDataDictionaryUsedQueryItem.class, params, new PagingInfo(1, Integer.MAX_VALUE));
    }

    private void addChildren(TreeNode treeNode, List<CdDataDictionaryEntity> entities) {
        entities.forEach(entity -> {
            TreeNode childNode = new TreeNode();
            childNode.setId(entity.getCode());
            childNode.setName(entity.getName());
            childNode.setData(entity);
            childNode.setNocheck(false);
            treeNode.getChildren().add(childNode);
        });
    }

    @Override
    public TreeNode listItemAsTreeByUuid(Long uuid) {
        CdDataDictionaryEntity dataDictionaryEntity = this.getOne(uuid);
        return listItemAsTree(dataDictionaryEntity);
    }

    @Override
    public TreeNode listItemAsTreeByCode(String code) {
        CdDataDictionaryEntity dataDictionaryEntity = this.getByCode(code);
        return listItemAsTree(dataDictionaryEntity);
    }

    @Override
    @Transactional
    public void batchSaveDataDicItems(Long uuid, List<CdDataDictionaryItemDto> items) {
        if (CollectionUtils.isNotEmpty(items)) {
            int seq = 0;
            for (CdDataDictionaryItemDto dto : items) {
                CdDataDictionaryItemEntity item = new CdDataDictionaryItemEntity();
                item.setLabel(dto.getLabel());
                item.setValue(dto.getValue());
                item.setDataDictUuid(uuid);
                item.setSortOrder(++seq);
                dto.setParentUuid(uuid);
                dataDictionaryItemService.save(item);
                dto.setUuid(item.getUuid());
                dto.setDataDictUuid(uuid);
                cascadeSaveChildDataDicItems(dto);
            }
        }
    }

    private void cascadeSaveChildDataDicItems(CdDataDictionaryItemDto parent) {
        if (CollectionUtils.isNotEmpty(parent.getChildren())) {
            int seq = 0;
            for (CdDataDictionaryItemDto c : parent.getChildren()) {
                CdDataDictionaryItemEntity item = new CdDataDictionaryItemEntity();
                item.setLabel(c.getLabel());
                item.setValue(c.getValue());
                item.setDataDictUuid(parent.getDataDictUuid());
                item.setParentUuid(parent.getUuid());
                item.setSortOrder(++seq);
                dataDictionaryItemService.save(item);
                c.setUuid(item.getUuid());
                c.setDataDictUuid(parent.getDataDictUuid());
                cascadeSaveChildDataDicItems(c);
            }
        }
    }

    private TreeNode listItemAsTree(CdDataDictionaryEntity dataDictionaryEntity) {
        List<CdDataDictionaryItemEntity> itemEntities = dataDictionaryItemService.listByDataDictUuid(dataDictionaryEntity.getUuid());
        TreeNode treeNode = TreeUtils.buildTree(itemEntities, "uuid", "parentUuid", itemEntity -> {
            TreeNode node = new TreeNode();
            node.setName(itemEntity.getLabel());
            node.setId(itemEntity.getUuid() + StringUtils.EMPTY);
            node.setData(itemEntity.getValue());
            return node;
        });
        treeNode.setId(TreeNode.ROOT_ID);
        treeNode.setName(dataDictionaryEntity.getName());
        return treeNode;
    }

}
