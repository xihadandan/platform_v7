/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.superadmin.dao.SuperAdminDao;
import com.wellsoft.pt.security.superadmin.entity.SuperAdmin;
import com.wellsoft.pt.security.superadmin.service.SuperAdminService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

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
 * 2012-12-26.1	zhulh		2012-12-26		Create
 * </pre>
 * @date 2012-12-26
 */
@Service
public class SuperAdminServiceImpl extends AbstractJpaServiceImpl<SuperAdmin, SuperAdminDao, String> implements
        SuperAdminService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.service.SuperAdminService#getByLoginName(java.lang.String)
     */
    @Override
    public SuperAdmin getByLoginName(String username) {
        List<SuperAdmin> superAdmins = dao.listByFieldEqValue("loginName", username);
        return CollectionUtils.isNotEmpty(superAdmins) ? superAdmins.get(0) : null;
    }

}
