/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.systemtable.service;

import com.wellsoft.pt.basicdata.systemtable.dao.SystemTableRelationshipDao;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableRelationship;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月19日.1	chenqiong		2018年4月19日		Create
 * </pre>
 * @date 2018年4月19日
 */
public interface SystemTableRelationshipService extends
        JpaService<SystemTableRelationship, SystemTableRelationshipDao, String> {

    /**
     * @param uuid
     * @return
     */
    List<SystemTableRelationship> queryBySystemTableUuid(String uuid);

}
