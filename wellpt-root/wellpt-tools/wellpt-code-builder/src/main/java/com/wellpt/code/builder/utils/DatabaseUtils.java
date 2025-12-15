package com.wellpt.code.builder.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/7/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/31    chenq		2018/7/31		Create
 * </pre>
 */
public class DatabaseUtils {


    private static Map<String, Connection> CONNECTION_CACHE = new ConcurrentHashMap<String, Connection>();


    public static Connection getConnection(String url, String user, String pwd) throws Exception {
        if (CONNECTION_CACHE.containsKey(url + user)) {
            return CONNECTION_CACHE.get(url + pwd);
        }
        Class.forName(DatabaseUtils.driverAdpater(url));//加载驱动
        Connection connection = DriverManager.getConnection(url, user, pwd);
        CONNECTION_CACHE.put(url + user, connection);
        return connection;
    }


    public static String driverAdpater(String url) {
        if (url.indexOf("jdbc:oracle:thin") != -1) {
            return DriverEnum.ORACLE.getDriver();
        }
        if (url.indexOf("jdbc:mysql") != -1) {
            return DriverEnum.MYSQL.getDriver();
        }
        return null;
    }

    public enum DriverEnum {
        ORACLE("oracle.jdbc.driver.OracleDriver"), MYSQL("com.mysql.jdbc.Driver");

        private String driver;

        DriverEnum(String driver) {
            this.driver = driver;
        }

        public String getDriver() {
            return driver;
        }

    }


}
