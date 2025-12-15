/*
 * @(#)2015年11月27日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import com.wellsoft.context.config.Config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年11月27日.1	zhulh		2015年11月27日		Create
 * </pre>
 * @date 2015年11月27日
 */
public class SessionFactoryBasedMultiTenancyListener implements ServletContextListener {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        if (!Config.MULTI_TENANCY_STRATEGY_SESSION_FACTORY.equals(Config.getValue(Config.KEY_MULTI_TENANCY_STRATEGY))) {
            return;
        }

        SessionFactoryRegistrar.registerActiveTenants();

		/*TenantFacadeService tenantService = ApplicationContextHolder.getBean(TenantFacadeService.class);
		List<Tenant> tenants = tenantService.getActiveTenants();
		for (Tenant t : tenants) {
			SessionFactoryRegistrar.register(t);
		}*/
    }

}
