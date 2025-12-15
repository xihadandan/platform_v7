/*
 * @(#)12/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizNewItemRelationDao;
import com.wellsoft.pt.biz.entity.BizNewItemRelationEntity;
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
 * 12/22/22.1	zhulh		12/22/22		Create
 * </pre>
 * @date 12/22/22
 */
public interface BizNewItemRelationService extends JpaService<BizNewItemRelationEntity, BizNewItemRelationDao, String> {
    /**
     * 添加发起的业务事项关系
     *
     * @param sourceItemInstUuid
     * @param targetItemInstUuid
     * @param itemFlowInstUuid
     * @param extraData
     */
    void addRelation(String sourceItemInstUuid, String targetItemInstUuid, String itemFlowInstUuid, String extraData);

    /**
     * 根据事项实例UUID获取发起的业务事项关系
     *
     * @param targetItemInstUuid
     * @return
     */
    BizNewItemRelationEntity getByTargetItemInstUuid(String targetItemInstUuid);

    /**
     * 根据源事项实例UUID获取发起的业务事项关系
     *
     * @param sourceItemInstUuid
     * @return
     */
    List<BizNewItemRelationEntity> listBySourceItemInstUuid(String sourceItemInstUuid);
}
