package com.wellsoft.pt.cg.utils;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/1
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/1    chenq		2018/8/1		Create
 * </pre>
 */
public class CodePropsUtils {

    public final static Properties PROPERTIES = new Properties();

    public static void loadProperties(java.util.Properties properties) {
        try {
            Field[] propFields = Properties.class.getDeclaredFields();
            for (Field f : propFields) {
                String value = properties.getProperty(f.getName());
                if (StringUtils.isNotBlank(value)) {
                    f.setAccessible(true);
                    f.set(PROPERTIES, value);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static class Properties {
        public static String dbUrl;

        public static String dbUser;

        public static String dbPassword;

        public static String tables;

        public static String author;

        public static String outputDir;

        public static String javaPackage;


    }
}
