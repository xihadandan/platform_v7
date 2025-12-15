package com.wellsoft.pt.integration.support;

import com.wellsoft.context.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-11-3.1	ruanhg		2014-11-3		Create
 * </pre>
 * @date 2014-11-3
 */
public class ConManager {

    private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();// 局部线程变量，用于保存connection
    private static Logger logger = LoggerFactory.getLogger(ConManager.class);

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        Connection conn = null;
        try {
            // 从ThreadLocal中取得Connection
            conn = ConManager.getConnection();

            // 手动控制事务提交
            ConManager.beginTransaction(conn);

            // 操作数据，方法省略。。。。。

            if (!conn.getAutoCommit()) {
                // 提交事务
                ConManager.commitTransaction(conn);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            try {
                if (!conn.getAutoCommit()) {
                    // 回滚事务
                    ConManager.rollbackTransaction(conn);
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }

        } finally {
            ConManager.closeConnection();
        }
    }

    /**
     * 获取Connection
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = connectionHolder.get();
        if (conn == null) {
            String driver = Config.getValue("multi.tenancy.driver");
            String url = Config.getValue("multi.tenancy.common.url");
            String username = Config.getValue("multi.tenancy.tenant.username");
            String password = Config.getValue("multi.tenancy.tenant.password");
            try {
                Class.forName(driver);
                conn = DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException e) {
                logger.error("程序异常！|ConnectionManager->getConnection()|" + e.getMessage());
                logger.info(e.getMessage());
            } catch (SQLException e) {
                logger.error("程序异常！|ConnectionManager->getConnection()|" + e.getMessage());
                logger.info(e.getMessage());
            }

            connectionHolder.set(conn);
        }
        return conn;
    }

    /**
     * 关闭连接和从线程变量中删除conn
     */
    public static void closeConnection() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                conn.close();
                connectionHolder.remove();
            } catch (SQLException e) {
                logger.error("程序异常！|ConnectionManager->closeConnection()|" + e.getMessage());
                logger.info(e.getMessage());
            }
        }
    }

    /**
     * 开启事务，手动开启
     *
     * @param conn
     */
    public static void beginTransaction(Connection conn) {
        try {
            if (conn != null) {
                if (conn.getAutoCommit()) {
                    conn.setAutoCommit(false);
                }
            }
        } catch (SQLException e) {
            logger.error("程序异常！|ConnectionManager->beginTransaction(Connection conn)|" + e.getMessage());
            logger.info(e.getMessage());
        }
    }

    /**
     * 提交事务
     *
     * @param conn
     */
    public static void commitTransaction(Connection conn) {
        try {
            if (conn != null) {
                if (!conn.getAutoCommit()) {
                    conn.commit();
                }
            }
        } catch (SQLException e) {
            logger.error("程序异常！|ConnectionManager->commitTransaction(Connection conn)|" + e.getMessage());
            logger.info(e.getMessage());
        }
    }

    /**
     * 回滚事务
     *
     * @param conn
     */
    public static void rollbackTransaction(Connection conn) {
        try {
            if (conn != null) {
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            logger.error("程序异常！|ConnectionManager->commitTransaction(Connection conn)|" + e.getMessage());
            logger.info(e.getMessage());
        }
    }
}
