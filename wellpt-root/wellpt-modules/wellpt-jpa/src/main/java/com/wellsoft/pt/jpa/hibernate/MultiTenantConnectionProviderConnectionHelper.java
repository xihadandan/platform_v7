/*
 * @(#)2013-2-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.datasource.XADataSource;
import com.wellsoft.pt.jpa.datasource.XADataSourceFactory;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.tool.hbm2ddl.ConnectionHelper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-17.1	zhulh		2013-2-17		Create
 * </pre>
 * @date 2013-2-17
 */
public class MultiTenantConnectionProviderConnectionHelper implements ConnectionHelper {
    private Connection connection;
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = ApplicationContextHolder.getBean(Config.TENANT_SESSION_FACTORY_BEAN_NAME,
                    SessionFactory.class);
        }
        return sessionFactory;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.hibernate.tool.hbm2ddl.ConnectionHelper#prepare(boolean)
     */
    @Override
    public void prepare(boolean needsAutoCommit) throws SQLException {
        // SessionImplementor sessionImplementor = (SessionImplementor)
        // getSessionFactory().getCurrentSession();
        // ((SessionFactoryImpl)
        // getSessionFactory()).getServiceRegistry().getService(ConnectionProvider);
        // MULTITENANTCONNECTIONPROVIDER CONNECTIONPROVIDER =
        // ((SESSIONFACTORYIMPL) GETSESSIONFACTORY())
        // .GETSERVICEREGISTRY().GETSERVICE(MULTITENANTCONNECTIONPROVIDER.CLASS);
        // CONNection =
        // connectionProvider.getConnection(TenantContextHolder.getTenantId());
        // connection =
        // sessionImplementor.getJdbcConnectionAccess().obtainConnection();
        // JdbcConnectionAccess jdbcConnectionAccess = new
        // ContextualJdbcConnectionAccess(
        // ((SessionFactoryImpl)
        // getSessionFactory()).getServiceRegistry().getService(
        // MultiTenantConnectionProvider.class));

        TenantFacadeService tenantService = ApplicationContextHolder.getBean(TenantFacadeService.class);
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        Tenant tenant = tenantService.getById(user.getTenantId());
        XADataSource xaDataSource = XADataSourceFactory.build(tenant);
        connection = xaDataSource.getConnection();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.hibernate.tool.hbm2ddl.ConnectionHelper#getConnection()
     */
    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.hibernate.tool.hbm2ddl.ConnectionHelper#release()
     */
    @Override
    public void release() throws SQLException {
        // we only release the connection
        if (connection != null) {
            new SqlExceptionHelper().logAndClearWarnings(connection);
            // SessionImplementor sessionImplementor = (SessionImplementor)
            // getSessionFactory().getCurrentSession();
            // sessionImplementor.getJdbcConnectionAccess().releaseConnection(connection);
            connection.close();
            connection = null;
        }
    }

}
