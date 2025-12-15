/*
 * @(#)7/12/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.service;

import com.wellsoft.pt.basicdata.serialnumber.dao.SnSerialNumberDefinitionDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberDefinitionEntity;
import com.wellsoft.pt.basicdata.serialnumber.query.SnSerialNumberDefinitionUsedQueryItem;
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
 * 7/12/22.1	zhulh		7/12/22		Create
 * </pre>
 * @date 7/12/22
 */
public interface SnSerialNumberDefinitionService extends JpaService<SnSerialNumberDefinitionEntity, SnSerialNumberDefinitionDao, String> {
    /**
     * 删除没用的流水号定义
     *
     * @param uuid
     * @return
     */
    int deleteWhenNotUsed(String uuid);

    /**
     * 根据流水号分类UUID获取流水号定义列表
     *
     * @param categoryUuid
     * @return
     */
    List<SnSerialNumberDefinitionEntity> listByCategoryUuid(String categoryUuid);

    /**
     * 通过流水号ID获取流水号定义
     *
     * @param id
     * @return
     */
    SnSerialNumberDefinitionEntity getById(String id);

    /**
     * 根据流水号分类UUID或流水号定义ID获取流水号定义列表
     *
     * @param categoryUuidOrIds
     * @return
     */
    List<SnSerialNumberDefinitionEntity> listByCategoryUuidOrId(List<String> categoryUuidOrIds);

    /**
     * 获取UUID列表，获取被引用的流水号
     *
     * @param uuids
     * @return
     */
    List<SnSerialNumberDefinitionUsedQueryItem> listUsedQueryItemByUuids(List<String> uuids);

    /**
     * @param system
     * @return
     */
    List<SnSerialNumberDefinitionEntity> listBySystem(String system);
}
