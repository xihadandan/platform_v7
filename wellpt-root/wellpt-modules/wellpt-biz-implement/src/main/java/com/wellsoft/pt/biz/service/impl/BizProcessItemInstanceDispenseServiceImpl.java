/*
 * @(#)10/20/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.biz.dao.BizProcessItemInstanceDispenseDao;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceDispenseEntity;
import com.wellsoft.pt.biz.enums.EnumBizProcessItemDispenseState;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceDispenseService;
import com.wellsoft.pt.biz.support.ItemIncludeItem;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
 * 10/20/22.1	zhulh		10/20/22		Create
 * </pre>
 * @date 10/20/22
 */
@Service
public class BizProcessItemInstanceDispenseServiceImpl extends AbstractJpaServiceImpl<BizProcessItemInstanceDispenseEntity,
        BizProcessItemInstanceDispenseDao, String> implements BizProcessItemInstanceDispenseService {

    /**
     * 按顺序保存包含事项
     *
     * @param parentItemInstUuid
     * @param includeItems
     * @return
     */
    @Override
    @Transactional
    public List<BizProcessItemInstanceDispenseEntity> saveAllWithOrder(String parentItemInstUuid, List<ItemIncludeItem> includeItems) {
        List<BizProcessItemInstanceDispenseEntity> entities = Lists.newArrayList();
        for (int index = 0; index < includeItems.size(); index++) {
            ItemIncludeItem includeItem = includeItems.get(index);
            BizProcessItemInstanceDispenseEntity entity = new BizProcessItemInstanceDispenseEntity();
            entity.setParentItemInstUuid(parentItemInstUuid);
            entity.setItemName(includeItem.getItemName());
            entity.setItemCode(includeItem.getItemCode());
            entity.setSortOrder(index);
            entity.setCompletionState(EnumBizProcessItemDispenseState.Ready.getValue());
            entities.add(entity);
        }
        this.dao.saveAll(entities);
        return entities;
    }

    /**
     * 根据业务事项实例UUID获取分发对象
     *
     * @param itemInstUuid
     * @return
     */
    @Override
    public BizProcessItemInstanceDispenseEntity getByItemInstUuid(String itemInstUuid) {
        Assert.hasText(itemInstUuid, "业务事项实例UUID不能为空！");

        BizProcessItemInstanceDispenseEntity entity = new BizProcessItemInstanceDispenseEntity();
        entity.setItemInstUuid(itemInstUuid);
        List<BizProcessItemInstanceDispenseEntity> entities = this.dao.listByEntity(entity);

        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据上级业务事项实例UUID、序号获取分发对象
     *
     * @param parentItemInstUuid
     * @param sortOrder
     * @return
     */
    @Override
    public BizProcessItemInstanceDispenseEntity getByParentInstUuidAndSortOrder(String parentItemInstUuid, Integer sortOrder) {
        Assert.hasText(parentItemInstUuid, "上级业务事项实例UUID不能为空！");
        Assert.notNull(sortOrder, "排序号不能为空！");

        BizProcessItemInstanceDispenseEntity entity = new BizProcessItemInstanceDispenseEntity();
        entity.setParentItemInstUuid(parentItemInstUuid);
        entity.setSortOrder(sortOrder);
        List<BizProcessItemInstanceDispenseEntity> entities = this.dao.listByEntity(entity);

        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据上级业务事项实例UUID获取数量
     *
     * @param parentItemInstUuid
     * @return
     */
    @Override
    public Long countByParentInstUuid(String parentItemInstUuid) {
        Assert.hasText(parentItemInstUuid, "上级业务事项实例UUID不能为空！");

        BizProcessItemInstanceDispenseEntity entity = new BizProcessItemInstanceDispenseEntity();
        entity.setParentItemInstUuid(parentItemInstUuid);
        return this.dao.countByEntity(entity);
    }

}
