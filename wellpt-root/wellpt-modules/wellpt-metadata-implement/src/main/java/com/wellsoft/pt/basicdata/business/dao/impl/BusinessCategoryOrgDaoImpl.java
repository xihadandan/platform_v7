/*
 * @(#)2019-02-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.dao.impl;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.basicdata.business.dao.BusinessCategoryOrgDao;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryOrgEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 数据库表BUSINESS_CATEGORY_ORG的DAO接口实现类
 *
 * @author leo
 * @version 1.0
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-02-14.1    leo        2019-02-14		Create
 * </pre>
 * @date 2019-02-14
 */
@Repository
public class BusinessCategoryOrgDaoImpl extends AbstractJpaDaoImpl<BusinessCategoryOrgEntity, String> implements
        BusinessCategoryOrgDao {

    @Override
    public BusinessCategoryOrgEntity getOne(String uuid) {
        BusinessCategoryOrgEntity entity = null;
        if (IdPrefix.startsWithExternal(uuid)) {
            entity = getUniqueBy("id", uuid);
        }
        if (entity == null) {
            entity = super.getOne(uuid);
        }
        return entity;
    }

}
