package com.wellsoft.pt.cg.core.dialect;

import java.sql.Blob;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库方言，默认情况下数据库属性类型转为对应的java类型
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-7.9	lmw		2015-7-9		Create
 * </pre>
 * @date 2015-7-9
 */
public class OracleDialect implements Dialect {
    private static final Map<String, Class<?>> types = new HashMap<String, Class<?>>();

    {
        types.put("VARCHAR2", String.class);
        types.put("VARCHAR", String.class);
        types.put("NUMBER", Integer.class);
        types.put("TIMESTAMP", Date.class);
        types.put("TINYINT", Byte.class);
        types.put("SMALLINT", Short.class);
        types.put("INGEGER", Integer.class);
        types.put("FLOAT", Float.class);
        types.put("BIGINT", Long.class);
        types.put("DOUBLE", Double.class);
        types.put("CHAR", String.class);
        types.put("BIT", Boolean.class);
        types.put("DATE", Date.class);
        types.put("CLOB", String.class);
        types.put("TIME", Date.class);
        types.put("BLOB", Blob.class);
    }

    public static Map<String, Class<?>> getTypes() {
        return Collections.unmodifiableMap(types);
    }

    @Override
    public Class<?> javaTypeByColumnType(String type) {
        return types.get(type.toUpperCase());
    }

    @Override
    public String javaCanonicalName(String type) {
        Class<?> clazz = javaTypeByColumnType(type);
        return clazz.getCanonicalName();
    }

    @Override
    public String javaTypeName(String type) {
        int index = type.indexOf("(");
        if (index > 0) {
            String t = type.substring(0, index);
            type = t;
        }
        Class<?> clazz = javaTypeByColumnType(type);
        return clazz.getName();
    }

    @Override
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
