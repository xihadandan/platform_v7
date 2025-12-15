/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.basicdata.datadict.dao.CdDataDictionaryItemAttributeDao;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryItemAttributeEntity;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryItemAttributeService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
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
@Service
public class CdDataDictionaryItemAttributeServiceImpl extends AbstractJpaServiceImpl<CdDataDictionaryItemAttributeEntity, CdDataDictionaryItemAttributeDao, Long>
        implements CdDataDictionaryItemAttributeService {
    /**
     * 根据字典项UUID删除扩展属性
     *
     * @param itemUuids
     */
    @Override
    @Transactional
    public void deleteByItemUuids(List<Long> itemUuids) {
        Assert.notEmpty(itemUuids, "字典项UUID列表不能为空！");

        String hql = "delete from CdDataDictionaryItemAttributeEntity t where t.itemUuid in(:itemUuids)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("itemUuids", itemUuids);
        this.dao.deleteByHQL(hql, params);
    }

    /**
     * 根据字典项UUID获取扩展属性
     *
     * @param itemUuid
     */
    @Override
    public List<CdDataDictionaryItemAttributeEntity> listByItemUuid(Long itemUuid) {
        Assert.notNull(itemUuid, "字典项UUID不能为空！");

        CdDataDictionaryItemAttributeEntity entity = new CdDataDictionaryItemAttributeEntity();
        entity.setItemUuid(itemUuid);
        return this.dao.listByEntity(entity);
    }

    /**
     * 根据字典项UUID列表获取扩展属性
     *
     * @param itemUuids
     * @return
     */
    @Override
    public List<CdDataDictionaryItemAttributeEntity> listByItemUuids(List<Long> itemUuids) {
        if (CollectionUtils.isEmpty(itemUuids)) {
            return Collections.emptyList();
        }
        Map<String, Object> params = Maps.newHashMap();
        List<CdDataDictionaryItemAttributeEntity> results = Lists.newArrayList();
        ListUtils.handleSubList(itemUuids, 200, list -> {
            params.put("itemUuids", list);
            results.addAll(this.dao.listByHQL("from CdDataDictionaryItemAttributeEntity t where t.itemUuid in(:itemUuids)", params));
        });
        return results;
    }
}
