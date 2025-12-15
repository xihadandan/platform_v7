/*
 * @(#)2012-12-1 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-1.1	lilin		2012-12-1		Create
 * </pre>
 * @date 2012-12-1
 */
@Component
public class DefaultCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    public static final String TENANT_PREFIX = "T";
    @Autowired
    TenantFacadeService tenantService;
    private Map<String, String> idMap = new HashMap<String, String>();

    /**
     * (non-Javadoc)
     *
     * @see org.hibernate.context.spi.CurrentTenantIdentifierResolver#resolveCurrentTenantIdentifier()
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContextHolder.getTenantId();

        if (Config.COMMON_TENANT.equals(tenantId)) {
            tenantId = Config.DEFAULT_TENANT;
        }

        if (tenantId.startsWith(TENANT_PREFIX)) {
            return tenantId;
        }

        if (!idMap.containsKey(tenantId)) {
			/*TenantService tenantService = ApplicationContextHolder.getBean(TenantService.class);
			Tenant tenant = tenantService.getByAccount(tenantId);*/
            Tenant tenant = tenantService.getByAccount(tenantId);
            if (tenant != null) {
                idMap.put(tenantId, tenant.getId());
            } else {
                idMap.put(tenantId, tenantId);
            }
        }
        return idMap.get(tenantId);
    }

    /**
     * 验证是否当前session的租户id 一致
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}
