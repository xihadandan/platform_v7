/*
 * @(#)2019年9月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.dao.impl.DmsDataPermissionDefinitionDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDataPermissionDefinitionEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 数据权限定义实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年9月29日.1	zhulh		2019年9月29日		Create
 * </pre>
 * @date 2019年9月29日
 */
public interface DmsDataPermissionDefinitionService extends
        JpaService<DmsDataPermissionDefinitionEntity, DmsDataPermissionDefinitionDaoImpl, String> {

    /**
     * 根据ID获取数据定义
     *
     * @param id
     * @return
     */
    DmsDataPermissionDefinitionEntity getById(String id);

}
