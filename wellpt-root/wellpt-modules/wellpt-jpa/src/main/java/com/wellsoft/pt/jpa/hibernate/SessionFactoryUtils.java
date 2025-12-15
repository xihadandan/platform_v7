/*
 * @(#)2013-3-21 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.config.service.SystemParamsFacadeService;
import com.wellsoft.context.util.ApplicationContextHolder;
import org.hibernate.SessionFactory;

/**
 * Description: 获取多租户的SessionFactory
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-21.1	zhulh		2013-3-21		Create
 * </pre>
 * @date 2013-3-21
 */
public abstract class SessionFactoryUtils {

    public static final String KEY_ORG_SESSION_FACTORY_BEAN_NAME = "multi.tenancy.org.session_factory.bean_name";
    public static final String ORG_DEFAULT_VALUE = "orgSessionFactory";

    public static final String KEY_CIS_SESSION_FACTORY_BEAN_NAME = "multi.tenancy.cis.session_factory.bean_name";
    public static final String CIS_DEFAULT_VALUE = "cisSessionFactory";

    public static final String getCommonSessionFactoryBeanName() {
        return Config.COMMON_SESSION_FACTORY_BEAN_NAME;
    }

    public static final SessionFactory getCommonSessionFactory() {
        return ApplicationContextHolder.getBean(Config.COMMON_SESSION_FACTORY_BEAN_NAME, SessionFactory.class);
    }

    public static final String getTenantSessionFactoryBeanName() {
        return Config.TENANT_SESSION_FACTORY_BEAN_NAME;
    }

    public static final SessionFactory getMultiTenantSessionFactory() {
        return ApplicationContextHolder.getBean(Config.TENANT_SESSION_FACTORY_BEAN_NAME, SessionFactory.class);
    }

    public static final SessionFactory getOrgSessionFactory() {
        String sessionFactoryId = ApplicationContextHolder.getBean(SystemParamsFacadeService.class).getValue(
                KEY_ORG_SESSION_FACTORY_BEAN_NAME, ORG_DEFAULT_VALUE);
        if (ApplicationContextHolder.containsBean(sessionFactoryId)) {
            return ApplicationContextHolder.getBean(sessionFactoryId, SessionFactory.class);
        }
        return getMultiTenantSessionFactory();
    }

    public static final SessionFactory getCisSessionFactory() {
        String sessionFactoryId = ApplicationContextHolder.getBean(SystemParamsFacadeService.class).getValue(
                KEY_CIS_SESSION_FACTORY_BEAN_NAME, CIS_DEFAULT_VALUE);
        if (ApplicationContextHolder.containsBean(sessionFactoryId)) {
            return ApplicationContextHolder.getBean(sessionFactoryId, SessionFactory.class);
        }
        return getMultiTenantSessionFactory();
    }

}
