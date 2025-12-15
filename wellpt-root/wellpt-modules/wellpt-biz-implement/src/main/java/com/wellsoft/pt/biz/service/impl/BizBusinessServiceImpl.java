/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.biz.dao.BizBusinessDao;
import com.wellsoft.pt.biz.entity.BizBusinessEntity;
import com.wellsoft.pt.biz.service.BizBusinessService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
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
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@Service
public class BizBusinessServiceImpl extends AbstractJpaServiceImpl<BizBusinessEntity, BizBusinessDao, String> implements BizBusinessService {

    /**
     * 根据ID获取业务
     *
     * @param id
     * @return
     */
    @Override
    public BizBusinessEntity getById(String id) {
        Assert.hasText(id, "业务ID不能为空！");

        List<BizBusinessEntity> entities = this.dao.listByFieldEqValue("id", id);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据ID获取数量
     *
     * @param id
     * @return
     */
    @Override
    public Long countById(String id) {
        Assert.hasText(id, "业务ID不能为空！");

        BizBusinessEntity entity = new BizBusinessEntity();
        entity.setId(id);
        return this.dao.countByEntity(entity);
    }

    /**
     * 根据业务分类UUID列表，获取业务
     *
     * @param categoryUuids
     * @return
     */
    @Override
    public List<BizBusinessEntity> listByCategoryUuids(List<String> categoryUuids) {
        if (CollectionUtils.isEmpty(categoryUuids)) {
            return Collections.emptyList();
        }

        List<BizBusinessEntity> bizBusinessEntities = this.dao.listByFieldInValues("categoryUuid", categoryUuids);
        return bizBusinessEntities;
    }

    /**
     * 根据业务分类UUID列表，获取数量
     *
     * @param categoryUuids
     * @return
     */
    @Override
    public Long countByCategoryUuids(List<String> categoryUuids) {
        String hql = "from BizBusinessEntity t where t.categoryUuid in (:categoryUuids)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("categoryUuids", categoryUuids);
        return this.dao.countByHQL(hql, values);
    }

    /**
     * 根据UUID列表，获取ID列表
     *
     * @param uuids
     * @return
     */
    @Override
    public List<String> listIdByUuids(List<String> uuids) {
        String hql = "select id from BizBusinessEntity t where t.uuid in (:uuids)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuids", uuids);
        return this.dao.listCharSequenceByHQL(hql, values);
    }

    /**
     * 根据业务分类UUID，获取ID列表
     *
     * @param categoryUuid
     * @return
     */
    @Override
    public List<String> listIdByCategoryUuid(String categoryUuid) {
        String hql = "select id from BizBusinessEntity t where t.categoryUuid = :categoryUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("categoryUuid", categoryUuid);
        return this.dao.listCharSequenceByHQL(hql, values);
    }

}
