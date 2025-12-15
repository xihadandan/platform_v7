/*
 * @(#)2013-5-29 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.provider;

import com.wellsoft.pt.security.superadmin.entity.DatabaseConfig;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 2013-5-29.1	zhulh		2013-5-29		Create
 * </pre>
 * @date 2013-5-29
 */
public abstract class DatabaseConfigProvider {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public boolean checkConnectionStatus(DatabaseConfig databaseConfig) {
        Connection connection = null;
        boolean result = false;
        try {
            connection = obtainConnection(databaseConfig);
            result = true;
        } catch (SQLException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
            }
        }

        return result;
    }

    protected abstract Connection obtainConnection(DatabaseConfig databaseConfig) throws SQLException;
}
