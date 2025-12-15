/*
 * @(#)2014-8-14 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.support;

import com.wellsoft.context.config.Config;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-14.1	wubin		2014-8-14		Create
 * </pre>
 * @date 2014-8-14
 */
public class JdbcConnection {
    private Logger logger = LoggerFactory.getLogger(JdbcConnection.class);

    /**
     * 3
     * 通过properties配置文件的方式灵活配置连接参数，properties中的属性名固化
     *
     * @return 数据库连接
     */
    public Connection openConnection() {

        String url = "";
        String user = "";
        String password = "";
        Properties props = new Properties();
        try {
            props.load(this.getClass().getClassLoader().getResourceAsStream(Config.SYSTEM_CONFIG));
            url = props.getProperty("multi.tenancy.tenant.url");
            if (StringUtils.isBlank(url)) {
                props.load(this.getClass().getClassLoader().getResourceAsStream(Config.SYSTEM_JDBC_CONFIG));
                url = props.getProperty("multi.tenancy.tenant.url");
            }
            user = props.getProperty("multi.tenancy.tenant.username");
            password = props.getProperty("multi.tenancy.tenant.password");

            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * 创建连接
     *
     * @return
     */
    public Connection createConnection(String databaseType, String userName, String passWord, String url) {
        try {
            if (databaseType.equals("1")) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } else if (databaseType.equals("2")) {

            } else if (databaseType.equals("3")) {
                Class.forName("com.mysql.jdbc.Driver");
            }
            Connection connection = DriverManager.getConnection(url, userName, passWord);
            return connection;
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 释放连接
     */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
