package com.wellpt.code.builder.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:数据库方言，默认情况下数据库属性类型转为对应的java类型
 *
 * @author chenq
 * @date 2018/8/2
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/2    chenq		2018/8/2		Create
 * </pre>
 */
public abstract class AbstractDialect {


    public static AbstractDialect build(String dbUrl) {
        if (dbUrl.indexOf("jdbc:oracle:thin") != -1) {
            return new OracleDialect();
        }
        if (dbUrl.indexOf("jdbc:mysql") != -1) {
            return new MySqlDialect();
        }
        throw new UnsupportedOperationException("无法识别的数据库url");
    }

    /**
     * 定义数据库方言的字段类型与java类型映射
     *
     * @return
     */
    public Map<String, Class<?>> types() {
        return Collections.unmodifiableMap(new HashMap<String, Class<?>>());
    }


    /**
     * 获取数据库类型对应的java类型名称
     *
     * @param type
     * @return
     */
    public Class<?> javaTypeByColumnType(String type) {
        return types().get(type.toUpperCase());
    }

    /**
     * 获取数据类型对应的java类型
     * 如果包为java.lang则返回空
     *
     * @param type
     * @return
     */
    public String javaCanonicalName(String type) {
        Class<?> clazz = javaTypeByColumnType(type);
        return clazz.getCanonicalName();
    }

    /**
     * 获取数据类型对应的java类型的名称
     * 如果包为java.lang则返回空
     *
     * @param type
     * @return
     */
    public String javaTypeName(String type) {
        int index = type.indexOf("(");
        if (index > 0) {
            String t = type.substring(0, index);
            type = t;
        }
        Class<?> clazz = javaTypeByColumnType(type);
        return clazz.getName();
    }

    /**
     * 获取数据类型对应的java类型的名称
     * 如果包为java.lang则返回空
     *
     * @param type
     * @return
     */
    public String javaTypeSimpleName(String type) {
        int index = type.indexOf("(");
        if (index > 0) {
            String t = type.substring(0, index);
            type = t;
        }
        Class<?> clazz = javaTypeByColumnType(type);
        return clazz.getSimpleName();
    }


}
