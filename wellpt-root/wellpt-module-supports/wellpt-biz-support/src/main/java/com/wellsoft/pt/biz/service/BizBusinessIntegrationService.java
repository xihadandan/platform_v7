/*
 * @(#)11/17/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizBusinessIntegrationDao;
import com.wellsoft.pt.biz.entity.BizBusinessIntegrationEntity;
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
 * 11/17/22.1	zhulh		11/17/22		Create
 * </pre>
 * @date 11/17/22
 */
public interface BizBusinessIntegrationService extends JpaService<BizBusinessIntegrationEntity, BizBusinessIntegrationDao, String> {

    /**
     * 是否已存在业务集成
     *
     * @param type
     * @param itemInstUuid
     * @return
     */
    boolean isExistsByTypeAndItemInstUuid(String type, String itemInstUuid);

    /**
     * 根据业务集成类型、事项实例UUID获取业务集成信息
     *
     * @param type
     * @param itemInstUuid
     * @return
     */
    BizBusinessIntegrationEntity getByTypeAndItemInstUuid(String type, String itemInstUuid);

    /**
     * 保存业务集成
     *
     * @param itemInstUuid
     * @param type
     * @param bizInstUuid
     */
    void saveBusinessIntegration(String itemInstUuid, String type, String bizInstUuid);

    /**
     * 根据业务集成类型、业务实例UUID获取业务集成信息
     *
     * @param type
     * @param bizInstUuid
     * @return
     */
    BizBusinessIntegrationEntity getByTypeAndBizInstUuid(String type, String bizInstUuid);

    /**
     * 根据事项实例UUID获取业务集成信息
     *
     * @param itemInstUuid
     * @return
     */
    List<BizBusinessIntegrationEntity> listByItemInstUuid(String itemInstUuid);

    /**
     * 根据事项实例UUID列表获取业务集成信息
     *
     * @param itemInstUuids
     * @return
     */
    List<BizBusinessIntegrationEntity> listByItemInstUuids(List<String> itemInstUuids);

}
