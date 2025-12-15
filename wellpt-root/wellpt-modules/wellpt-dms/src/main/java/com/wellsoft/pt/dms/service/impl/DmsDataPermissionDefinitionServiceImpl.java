/*
 * @(#)2019年9月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.dao.impl.DmsDataPermissionDefinitionDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDataPermissionDefinitionEntity;
import com.wellsoft.pt.dms.service.DmsDataPermissionDefinitionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
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
@Service
public class DmsDataPermissionDefinitionServiceImpl extends
        AbstractJpaServiceImpl<DmsDataPermissionDefinitionEntity, DmsDataPermissionDefinitionDaoImpl, String> implements
        DmsDataPermissionDefinitionService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataPermissionDefinitionService#getById(java.lang.String)
     */
    @Override
    public DmsDataPermissionDefinitionEntity getById(String id) {
        return this.dao.getUniqueBy("id", id);
    }

}
