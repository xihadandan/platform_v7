/*
 * @(#)2016年3月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dbmigrate;

import com.wellsoft.context.config.Config;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.io.*;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月1日.1	zhongzh		2016年3月1日		Create
 * </pre>
 * @date 2016年3月1日
 */
public class MigratorUtils {
    private static Logger logger = LoggerFactory.getLogger(MigratorUtils.class);

    private MigratorUtils() {

    }

    public static DataSource getDataSource(String driver, String url, Properties conProps, String... orig) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DriverDataSource ds = new DriverDataSource(classLoader, driver, url, conProps, orig);
        return ds;
    }

    public static Properties getJdbcProperties(String dbaUser, String dbaPassword) {
        Properties dbaProp = new Properties();
        dbaProp.setProperty(AbstractFlywayMigrator.propUsername, dbaUser);
        if (StringUtils.isNotBlank(dbaPassword)) {
            dbaProp.setProperty(AbstractFlywayMigrator.propPassword, dbaPassword);
        }
        return dbaProp;
    }

    public static Properties getJdbcProperties(String prifix) {
        Properties prop;
        String configJdbcFile = Config.CLASS_DIR + File.separator + Config.SYSTEM_JDBC_CONFIG;
        FileInputStream jdbcFis = null;
        try {
            prop = new Properties();
            Properties configJdbc = new Properties();
            jdbcFis = new FileInputStream(configJdbcFile);
            configJdbc.load(jdbcFis);
            Enumeration<?> jdbcKeys = configJdbc.keys();
            while (jdbcKeys.hasMoreElements()) {
                String key = jdbcKeys.nextElement().toString();
                if (prifix == null) {
                    prop.setProperty(key, configJdbc.getProperty(key));
                } else if (key.startsWith(prifix)) {
                    prop.setProperty(key.replace(prifix, ""), configJdbc.getProperty(key));
                }
            }
        } catch (Exception ex) {
            prop = new Properties();
            logger.info("error getJdbcProperties", ex);
        } finally {
            IOUtils.closeQuietly(jdbcFis);
        }
        return prop;
    }

    public static DataSource getDataSource(Properties conProps) {
        String url = conProps.getProperty(IMigrateCommon.JDBC_URL);
        String driver = conProps.getProperty(IMigrateCommon.JDBC_DRIVER);
        String user = conProps.getProperty(IMigrateCommon.JDBC_USERNAME);
        String password = conProps.getProperty(IMigrateCommon.JDBC_PASSWORD);
        if (StringUtils.isBlank(url) || StringUtils.isBlank(user) || StringUtils.isBlank(password)) {
            return null;
        } else if (StringUtils.isNotBlank(password)) {
            conProps.put(AbstractFlywayMigrator.propPassword, password);
        }
        conProps.put(AbstractFlywayMigrator.propUrl, url);
        conProps.put(AbstractFlywayMigrator.propUsername, user);
        DataSource ds;
        try {
            ds = MigratorUtils.getDataSource(driver, url, conProps);
        } catch (Exception t) {
            ds = null;
            logger.info("error getDataSource", t);
        }
        return ds;
    }

    public static String getFileString(String pathname) {
        String result = null;
        InputStream inputStream = null;
        try {
            File file = new File(pathname);
            if (file == null || file.exists() == false) {
                logger.trace("no File[{0}] not exist.", pathname);
                return result;
            }
            inputStream = new FileInputStream(file);
            result = IOUtils.toString(inputStream, "UTF-8");
        } catch (Exception t) {
            logger.error("no File[" + pathname + "] found.", t);
            // ignore exception
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return result;
    }

    public static Collection<IMigrateModule> sortedModules(Collection<IMigrateModule> modules) {
        Comparator<IMigrateModule> comparable = new Comparator<IMigrateModule>() {

            private int getOrder(Object obj) {
                if (obj instanceof Ordered) {
                    return ((Ordered) obj).getOrder();
                }
                if (obj != null) {
                    Class<?> clazz = (obj instanceof Class ? (Class<?>) obj : obj.getClass());
                    Order order = AnnotationUtils.findAnnotation(clazz, Order.class);
                    if (order != null) {
                        return order.value();
                    }
                }
                return WellOrdered.LOWEST_PRECEDENCE;
            }

            @Override
            public int compare(IMigrateModule o1, IMigrateModule o2) {
                return getOrder(o1) - getOrder(o2);
            }

        };
        IMigrateModule[] mods = new IMigrateModule[modules.size()];
        mods = modules.toArray(mods);
        Arrays.sort(mods, comparable);
        return Arrays.asList(mods);
    }

    public static String getCharset(File file) throws IOException {
        BufferedInputStream bin = null;
        try {
            bin = new BufferedInputStream(new FileInputStream(file));
            int p = (bin.read() << 8) + bin.read();
            String code = null;
            switch (p) {
                case 0xefbb:
                    code = "UTF-8";
                    break;
                case 0xfffe:
                    code = "Unicode";
                    break;
                case 0xfeff:
                    code = "UTF-16BE";
                    break;
                default:
                    code = "GBK";
            }
            return code;
        } finally {
            IOUtils.closeQuietly(bin);
        }
    }

    public static void main(String[] args) {

    }

}
