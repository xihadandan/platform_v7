/*
 * @(#)11/17/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.wellsoft.pt.biz.dao.BizBusinessIntegrationDao;
import com.wellsoft.pt.biz.entity.BizBusinessIntegrationEntity;
import com.wellsoft.pt.biz.service.BizBusinessIntegrationService;
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
 * 11/17/22.1	zhulh		11/17/22		Create
 * </pre>
 * @date 11/17/22
 */
@Service
public class BizBusinessIntegrationServiceImpl extends AbstractJpaServiceImpl<BizBusinessIntegrationEntity, BizBusinessIntegrationDao, String>
        implements BizBusinessIntegrationService {

    /**
     * 是否已存在业务集成
     *
     * @param type
     * @param itemInstUuid
     * @return
     */
    @Override
    public boolean isExistsByTypeAndItemInstUuid(String type, String itemInstUuid) {
        Assert.hasText(type, "业务集成类型不能为空！");
        Assert.hasText(itemInstUuid, "事项实例UUID不能为空！");

        BizBusinessIntegrationEntity entity = new BizBusinessIntegrationEntity();
        entity.setType(type);
        entity.setItemInstUuid(itemInstUuid);
        return this.dao.countByEntity(entity) > 0;
    }

    /**
     * @param type
     * @param itemInstUuid
     * @return
     */
    @Override
    public BizBusinessIntegrationEntity getByTypeAndItemInstUuid(String type, String itemInstUuid) {
        Assert.hasText(type, "业务集成类型不能为空！");
        Assert.hasText(itemInstUuid, "事项实例UUID不能为空！");

        BizBusinessIntegrationEntity entity = new BizBusinessIntegrationEntity();
        entity.setType(type);
        entity.setItemInstUuid(itemInstUuid);
        List<BizBusinessIntegrationEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }

        return null;
    }

    /**
     * 保存业务集成
     *
     * @param itemInstUuid
     * @param type
     * @param bizInstUuid
     */
    @Override
    @Transactional
    public void saveBusinessIntegration(String itemInstUuid, String type, String bizInstUuid) {
        Assert.hasText(type, "业务集成类型不能为空！");
        Assert.hasText(itemInstUuid, "事项实例UUID不能为空！");
        Assert.hasText(bizInstUuid, "业务实例UUID不能为空！");

        BizBusinessIntegrationEntity entity = new BizBusinessIntegrationEntity();
        entity.setItemInstUuid(itemInstUuid);
        entity.setType(type);
        entity.setBizInstUuid(bizInstUuid);
        this.dao.save(entity);
    }

    @Override
    public BizBusinessIntegrationEntity getByTypeAndBizInstUuid(String type, String bizInstUuid) {
        Assert.hasText(type, "业务集成类型不能为空！");
        Assert.hasText(bizInstUuid, "业务实例UUID不能为空！");

        BizBusinessIntegrationEntity entity = new BizBusinessIntegrationEntity();
        entity.setType(type);
        entity.setBizInstUuid(bizInstUuid);
        List<BizBusinessIntegrationEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }

        return null;
    }

    /**
     * 根据事项实例UUID获取业务集成信息
     *
     * @param itemInstUuid
     * @return
     */
    @Override
    public List<BizBusinessIntegrationEntity> listByItemInstUuid(String itemInstUuid) {
        Assert.hasText(itemInstUuid, "事项实例UUID不能为空！");

        BizBusinessIntegrationEntity entity = new BizBusinessIntegrationEntity();
        entity.setItemInstUuid(itemInstUuid);
        return this.dao.listByEntity(entity);
    }

    /**
     * 根据事项实例UUID列表获取业务集成信息
     *
     * @param itemInstUuids
     * @return
     */
    @Override
    public List<BizBusinessIntegrationEntity> listByItemInstUuids(List<String> itemInstUuids) {
        Assert.notEmpty(itemInstUuids, "事项实例UUID列表不能为空！");

        return this.dao.listByFieldInValues("itemInstUuid", itemInstUuids);
    }
}
