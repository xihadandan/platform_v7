/*
 * @(#)2013-12-4 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-4.1	zhulh		2013-12-4		Create
 * </pre>
 * @date 2013-12-4
 */
public interface DataSource {
    String getDialect();

    String getDriverClass();

    void extractDriverProperties(Properties properties);

    Properties getDriverProperties();

    String getType();

    Connection getConnection() throws SQLException;
}
