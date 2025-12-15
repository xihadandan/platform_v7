/*
 * @(#)10/20/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizProcessItemInstanceDispenseDao;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceDispenseEntity;
import com.wellsoft.pt.biz.support.ItemIncludeItem;
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
 * 10/20/22.1	zhulh		10/20/22		Create
 * </pre>
 * @date 10/20/22
 */
public interface BizProcessItemInstanceDispenseService extends JpaService<BizProcessItemInstanceDispenseEntity,
        BizProcessItemInstanceDispenseDao, String> {

    /**
     * 按顺序保存包含事项
     *
     * @param parentItemInstUuid
     * @param includeItems
     * @return
     */
    List<BizProcessItemInstanceDispenseEntity> saveAllWithOrder(String parentItemInstUuid, List<ItemIncludeItem> includeItems);

    /**
     * 根据业务事项实例UUID获取分发对象
     *
     * @param itemInstUuid
     * @return
     */
    BizProcessItemInstanceDispenseEntity getByItemInstUuid(String itemInstUuid);

    /**
     * 根据上级业务事项实例UUID、序号获取分发对象
     *
     * @param parentItemInstUuid
     * @param sortOrder
     * @return
     */
    BizProcessItemInstanceDispenseEntity getByParentInstUuidAndSortOrder(String parentItemInstUuid, Integer sortOrder);

    /**
     * 根据上级业务事项实例UUID获取数量
     *
     * @param parentItemInstUuid
     * @return
     */
    Long countByParentInstUuid(String parentItemInstUuid);
}
