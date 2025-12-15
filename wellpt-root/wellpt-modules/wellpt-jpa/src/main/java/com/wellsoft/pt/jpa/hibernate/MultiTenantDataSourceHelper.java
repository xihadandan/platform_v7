/*
 * @(#)2013-10-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-8.1	zhulh		2013-10-8		Create
 * </pre>
 * @date 2013-10-8
 */
public class MultiTenantDataSourceHelper {

    private static Logger logger = LoggerFactory.getLogger(MultiTenantDataSourceHelper.class);

    /**
     * 如何描述该方法
     *
     * @param tenantIdentifier
     */
    public static DataSource getDataSource(String tenantIdentifier) {
        DataSource dataSource = null;
        try {
            MultiTenantConnectionProvider multiTenantConnectionProvider = ((SessionFactoryImplementor) SessionFactoryUtils
                    .getMultiTenantSessionFactory()).getServiceRegistry()
                    .getService(MultiTenantConnectionProvider.class);
            if (multiTenantConnectionProvider instanceof MultiTenantDataSourceProvider) {
                dataSource = ((MultiTenantDataSourceProvider) multiTenantConnectionProvider)
                        .getDataSource(tenantIdentifier);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return dataSource;
    }

    /**
     * 如何描述该方法
     *
     * @param tenantIdentifier
     */
    public static DataSource removeDataSource(String tenantIdentifier) {
        DataSource dataSource = null;
        try {
            MultiTenantConnectionProvider multiTenantConnectionProvider = ((SessionFactoryImplementor) SessionFactoryUtils
                    .getMultiTenantSessionFactory()).getServiceRegistry()
                    .getService(MultiTenantConnectionProvider.class);
            if (multiTenantConnectionProvider instanceof MultiTenantDataSourceProvider) {
                dataSource = ((MultiTenantDataSourceProvider) multiTenantConnectionProvider)
                        .removeDataSource(tenantIdentifier);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return dataSource;
    }
}
