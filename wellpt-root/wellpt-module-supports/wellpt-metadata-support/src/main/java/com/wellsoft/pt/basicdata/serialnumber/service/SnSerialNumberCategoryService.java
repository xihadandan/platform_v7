/*
 * @(#)7/11/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.service;

import com.wellsoft.pt.basicdata.serialnumber.dao.SnSerialNumberCategoryDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberCategoryEntity;
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
 * 7/11/22.1	zhulh		7/11/22		Create
 * </pre>
 * @date 7/11/22
 */
public interface SnSerialNumberCategoryService extends JpaService<SnSerialNumberCategoryEntity, SnSerialNumberCategoryDao, String> {
    /**
     * 流水号分类按系统单位及名称查询
     *
     * @param name
     * @return
     */
    List<SnSerialNumberCategoryEntity> getAllBySystemUnitIdsLikeName(String name);

    /**
     * @param uuid
     * @return
     */
    int deleteWhenNotUsed(String uuid);

    /**
     * @param system
     * @return
     */
    List<SnSerialNumberCategoryEntity> listBySystem(String system);
    
}
