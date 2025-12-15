/*
 * @(#)2013-12-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.biz.impl;

import com.wellsoft.context.util.encode.Md5PasswordEncoderUtils;
import com.wellsoft.pt.jpa.hibernate.MultiTenantDataSourceHelper;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryUtils;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.support.SQLServer2008TenantDatabaseBuilder;
import com.wellsoft.pt.jpa.support.TenantDatabaseBuilder;
import com.wellsoft.pt.mt.biz.TenantBusinessSerivce;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.service.TenantService;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
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
 * 2013-12-6.1	zhulh		2013-12-6		Create
 * </pre>
 * @date 2013-12-6
 */
@Service
@Transactional
public class TenantBusinessSerivceImpl extends BaseServiceImpl implements TenantBusinessSerivce {

    @Autowired
    private UserService userService;

    @Autowired
    private TenantService tenantService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.biz.TenantBusinessSerivce#createTenantDatabase(com.wellsoft.pt.mt.entity.Tenant)
     */
    @Override
    public void createTenantDatabase(Tenant tenant) {
        // 创建租户库
        TenantDatabaseBuilder builder = new SQLServer2008TenantDatabaseBuilder();
        builder.build(tenant);

        if (!checkDatasourceConnectionStatus(tenant.getUuid())) {
            throw new RuntimeException("数据库连接失败!");
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.biz.TenantBusinessSerivce#createTenantAdminUser(com.wellsoft.pt.mt.entity.Tenant)
     */
    @Override
    public void createTenantAdminUser(Tenant tenant) {
        try {
            IgnoreLoginUtils.login(tenant.getId(), tenant.getId());
            // 创建租户管理员
            User example = new User();
            example.setIssys(true);
            List<User> users = userService.findByExample(example);
            if (users.isEmpty()) {
                String loginName = tenant.getAccount();
                String password = tenant.getPassword();
                User user = new User();
                user.setEnabled(true);
                user.setIssys(true);
                user.setLoginName(loginName);
                user.setUserName(loginName);
                user.setSex("1");
                user.setPassword(Md5PasswordEncoderUtils.encodePassword(password, loginName));
                userService.createUser(user);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#checkDatasourceConnectionStatus(java.lang.String)
     */
    @Override
    public boolean checkDatasourceConnectionStatus(String uuid) {
        Tenant tenant = tenantService.get(uuid);
        // 删除数据源
        MultiTenantDataSourceHelper.removeDataSource(tenant.getId());
        boolean result = false;
        JdbcConnectionAccess jdbcConnectionAccess = null;
        Connection connection = null;
        try {
            IgnoreLoginUtils.login(tenant.getId(), tenant.getId());
            jdbcConnectionAccess = ((SessionImplementor) SessionFactoryUtils.getMultiTenantSessionFactory()
                    .getCurrentSession()).getJdbcConnectionAccess();
            connection = jdbcConnectionAccess.obtainConnection();

            result = true;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            IgnoreLoginUtils.logout();
            try {
                if (jdbcConnectionAccess != null && connection != null) {
                    jdbcConnectionAccess.releaseConnection(connection);
                }
            } catch (SQLException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return result;
    }
}
