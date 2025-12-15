/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.wellsoft.pt.biz.dao.BizTagDao;
import com.wellsoft.pt.biz.entity.BizTagEntity;
import com.wellsoft.pt.biz.service.BizTagService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
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
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@Service
public class BizTagServiceImpl extends AbstractJpaServiceImpl<BizTagEntity, BizTagDao, String> implements BizTagService {
    /**
     * 根据ID列表获取业务标签
     *
     * @param ids
     * @return
     */
    @Override
    public List<BizTagEntity> listByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return this.dao.listByFieldInValues("id", ids);
    }

    /**
     * 根据ID获取数量
     *
     * @param id
     * @return
     */
    @Override
    public Long countById(String id) {
        Assert.hasText(id, "业务标签ID不能为空！");

        BizTagEntity entity = new BizTagEntity();
        entity.setId(id);
        return this.dao.countByEntity(entity);
    }

    @Override
    public long countByEntity(BizTagEntity entity) {
        return this.dao.countByEntity(entity);
    }

}
