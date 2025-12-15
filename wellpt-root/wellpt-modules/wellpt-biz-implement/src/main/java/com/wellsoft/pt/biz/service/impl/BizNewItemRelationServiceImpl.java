/*
 * @(#)12/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.wellsoft.pt.biz.dao.BizNewItemRelationDao;
import com.wellsoft.pt.biz.entity.BizNewItemRelationEntity;
import com.wellsoft.pt.biz.service.BizNewItemRelationService;
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
 * 12/22/22.1	zhulh		12/22/22		Create
 * </pre>
 * @date 12/22/22
 */
@Service
public class BizNewItemRelationServiceImpl extends AbstractJpaServiceImpl<BizNewItemRelationEntity, BizNewItemRelationDao, String>
        implements BizNewItemRelationService {

    /**
     * 添加发起的业务事项关系
     *
     * @param sourceItemInstUuid
     * @param targetItemInstUuid
     * @param itemFlowInstUuid
     * @param extraData
     */
    @Override
    @Transactional
    public void addRelation(String sourceItemInstUuid, String targetItemInstUuid, String itemFlowInstUuid, String extraData) {
        BizNewItemRelationEntity entity = new BizNewItemRelationEntity();
        entity.setSourceItemInstUuid(sourceItemInstUuid);
        entity.setTargetItemInstUuid(targetItemInstUuid);
        entity.setItemFlowInstUuid(itemFlowInstUuid);
        entity.setExtraData(extraData);
        this.dao.save(entity);
    }

    /**
     * 根据事项实例UUID获取发起的业务事项关系
     *
     * @param targetItemInstUuid
     * @return
     */
    @Override
    public BizNewItemRelationEntity getByTargetItemInstUuid(String targetItemInstUuid) {
        Assert.hasLength(targetItemInstUuid, "事项实例UUID不能为空！");

        BizNewItemRelationEntity entity = new BizNewItemRelationEntity();
        entity.setTargetItemInstUuid(targetItemInstUuid);
        List<BizNewItemRelationEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据源事项实例UUID获取发起的业务事项关系
     *
     * @param sourceItemInstUuid
     * @return
     */
    @Override
    public List<BizNewItemRelationEntity> listBySourceItemInstUuid(String sourceItemInstUuid) {
        Assert.hasLength(sourceItemInstUuid, "源事项实例UUID不能为空！");

        BizNewItemRelationEntity entity = new BizNewItemRelationEntity();
        entity.setSourceItemInstUuid(sourceItemInstUuid);
        return this.listByEntity(entity);
    }

}
