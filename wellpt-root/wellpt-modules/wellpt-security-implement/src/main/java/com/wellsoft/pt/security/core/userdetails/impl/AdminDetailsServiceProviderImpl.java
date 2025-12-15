/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails.impl;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.security.core.authentication.encoding.PasswordAlgorithm;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 租户登录服务提供类
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
@Transactional
public class AdminDetailsServiceProviderImpl extends UserDetailsServiceProviderImpl {
    @Autowired
    private TenantFacadeService tenantService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.core.userdetails.impl.UserDetailsServiceProviderImpl#getLoginType()
     */
    @Override
    public String getLoginType() {
        return LoginType.ADMIN;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.support.UserDetailsServiceProvider#getUserDetails(java.lang.String)
     */
    @Override
    public UserDetails getUserDetails(String username) {
        // 验证租户是否存在且可用
        Tenant tenant = tenantService.getByAccount(username);
        if (tenant == null || !Tenant.STATUS_ENABLED.equals(tenant.getStatus())) {
            throw new RuntimeException("租户[" + username + "]不存在");
        }

        return super.checkAndGetUserDetails(username, tenant, PasswordAlgorithm.Plaintext.getValue());
    }
}
