/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.acl.dao.AclObjectIdentityDao;
import com.wellsoft.pt.security.acl.entity.AclObjectIdentity;
import com.wellsoft.pt.security.acl.service.AclObjectIdentityService;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
@Service
public class AclObjectIdentityServiceImpl extends
        AbstractJpaServiceImpl<AclObjectIdentity, AclObjectIdentityDao, String> implements AclObjectIdentityService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclObjectIdentityService#findByObjectId(java.lang.String, java.lang.String)
     */
    @Override
    public AclObjectIdentity findByObjectId(String cls, String objectIdIdentity) {
        return dao.findByObjectId(cls, objectIdIdentity);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclObjectIdentityService#isExists(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isExists(String entityClassName, String objectIdIdentity) {
        return dao.isExists(entityClassName, objectIdIdentity);
    }

}
