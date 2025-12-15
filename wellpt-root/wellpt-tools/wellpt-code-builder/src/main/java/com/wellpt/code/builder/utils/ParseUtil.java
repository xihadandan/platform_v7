package com.wellpt.code.builder.utils;

import com.wellpt.code.builder.support.Context;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ParseUtil {

    /**
     * 将字符串首字母小写
     *
     * @param str
     * @return
     */
    public static String toLowerFirst(String str) {
        char first = str.charAt(0);
        if (Character.isLetter(first)) {
            if (str.length() > 1) {
                return Character.toLowerCase(str.charAt(0)) + str.substring(1);
            }
            if (str.length() == 0) {
                return Character.toLowerCase(str.charAt(0)) + "";
            }
        }
        return str;
    }

    /**
     * 表名转为类名
     *
     * @param tableName
     * @return
     */
    public static String tableNameToClass(String tableName) {
        if (tableName == null) {
            throw new RuntimeException("tableNameToClass error: tableName is null");
        }
        StringBuilder builder = new StringBuilder();
        String[] ts = tableName.split("_");
        for (String str : ts) {
            char[] chars = str.toLowerCase().toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            builder.append(chars);
        }
        return builder.toString();
    }

    /**
     * 判断chs中的index字符是否为单词的首字母
     *
     * @param chs
     * @param index
     * @return
     */
    private static boolean isWordTitle(char[] chs, int index) {
        if (chs == null || index > chs.length) {
            throw new RuntimeException("");
        }
        // 首字母默认为单词的开头
        if (index == 0)
            return true;

        // 小写字母不是单词开头
        if (Character.isLowerCase(chs[index])) {
            return false;
        } else {
            // 如果前一个字母为小写则当前字母为单词的首字母
            if (Character.isLowerCase(chs[index - 1])) {
                return true;
            }
            return false;
        }

    }

    /**
     * 类名转为表名
     *
     * @param className
     * @return
     */
    public static String classNameToTable(String className) {
        if (className == null) {
            throw new RuntimeException("classNameToTable error: className is null");
        }
        StringBuilder builder = new StringBuilder();
        char[] chars = className.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (isWordTitle(chars, i)) {
                builder.append("_").append(chars[i]);
            } else {
                builder.append(chars[i]);
            }
        }
        builder.deleteCharAt(0);
        return builder.toString().toLowerCase();
    }

    /**
     * 表名转为类名
     *
     * @param columnName
     * @return
     */
    public static String propName(String columnName) {
        return propName(columnName, true);
    }

    /**
     * 表名转为类名
     *
     * @param columnName
     * @return
     */
    public static String propName(String columnName, boolean isLowerFirst) {
        if (columnName == null) {
            throw new RuntimeException("propName error: columnName is null");
        }
        StringBuilder builder = new StringBuilder();
        String[] ts = columnName.split("_");
        for (String str : ts) {
            char[] chars = str.toLowerCase().toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            builder.append(chars);
        }
        if (!isLowerFirst)
            return builder.toString();

        if (builder.length() > 1) {
            return Character.toLowerCase(builder.charAt(0)) + builder.substring(1);
        } else {
            return Character.toLowerCase(builder.charAt(0)) + "";
        }
    }

    /**
     * 获取时间
     *
     * @return
     */
    public static String createDate(String format) {
        SimpleDateFormat tDateFormat = new SimpleDateFormat(format);
        return tDateFormat.format(new Date());
    }

    /**
     * 初始化freemarker配置
     *
     * @param context
     * @return
     */
    public static Configuration initConfig(Context context) {
        try {
            Configuration cfg = new Configuration();
            System.out.println(context.isDefault());
            if (!context.isDefault()) {
                cfg.setDirectoryForTemplateLoading(new File(context.getTemplateDir()));
            } else {
                cfg.setClassForTemplateLoading(Context.class, "com/wellpt/code/builder/ft");
            }

            cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(" templateDir is not exist！");
        }
    }

    /**
     * 获取所有属性，包含父类属性值
     *
     * @param clazz
     * @return
     */
    public static Field[] getAllFileds(Class<?> clazz) {
        if (clazz == null) {
            return new Field[0];
        }
        Field[] files = clazz.getDeclaredFields();
        Class<?> faClazz = clazz.getSuperclass();

        Field[] files2 = getAllFileds(faClazz);

        Field[] result = Arrays.copyOf(files, files.length + files2.length);
        System.arraycopy(files2, 0, result, files.length, files2.length);
        return result;
    }

}
