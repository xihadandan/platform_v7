/*
 * @(#)2013-2-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.jdbc.support;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.SerializableClobProxy;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Clob;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 查询数据项
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-27.1	zhulh		2013-2-27		Create
 * </pre>
 * @date 2013-2-27
 */
public class QueryItem extends LinkedCaseInsensitiveMap<Object> implements BaseQueryItem {
    private static final long serialVersionUID = -7867349255204800213L;
    private static Map<String, String> keyMap = new ConcurrentHashMap<String, String>();

    // private static ImprovedNamingStrategy namingStrategy = new
    // ImprovedNamingStrategy();

    /**
     *
     */
    public QueryItem() {
        this(0);
    }

    /**
     * @param length
     */
    public QueryItem(int length) {
        super(length);
    }

    public static String getKey(String rawKey) {
        String key = keyMap.get(rawKey);
        return key != null ? key : dbNameToJavaName(rawKey, false);
    }

    private static String dbNameToJavaName(String dbName, boolean firstCharUppered) {
        String name = dbName;
        if (name == null || name.trim().length() == 0) {
            return "";
        }
        String[] parts = null;
        StringBuilder sb = new StringBuilder();
        if (name.indexOf("_") != -1) {
            parts = name.toLowerCase().split("_");
            sb.append(parts[0]);
            for (int i = 1, len = parts.length; i < len; i++) {
                sb.append(StringUtils.capitalize(parts[i]));
            }
            return sb.toString();

        }
        return name.toLowerCase();
    }

    public String getString(String key) {
        Object obj = this.get(key);
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Proxy) {
            //代理Clob的
            if (obj instanceof Proxy) {
                InvocationHandler handler = Proxy.getInvocationHandler(obj);
                if (handler instanceof SerializableClobProxy) {
                    Clob clob = ((SerializableClobProxy) handler).getWrappedClob();
                    try {
                        return IOUtils.toString(clob.getCharacterStream());
                    } catch (Exception e) {

                    }
                }
            }

        }
        return (String) this.get(key);
    }

    public Boolean getBoolean(String key) {
        Object object = this.get(key);
        if (object == null) {
            return null;
        }
        return (Boolean) object;
    }

    public Double getDouble(String key) {
        Object object = this.get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        return (Double) this.get(key);
    }

    public Integer getInt(String key) {
        Object object = this.get(key);
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }
//        return (Integer) this.get(key);
        return Integer.parseInt(this.getString(key));
    }

    public Long getLong(String key) {
        Object object = this.get(key);
        if (object == null) {
            return null;
        }
        return object instanceof Number ? ((Number) object).longValue() : (Long) this.get(key);

    }

    public Date getDate(String key) {
        return (Date) this.get(key);
    }

    /**
     * (non-Javadoc)
     *
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public Object put(String key, Object value) {
        if (!keyMap.containsKey(key)) {
            // 数据库的命名方式转化为Java属性名的命名方式
            String camelCaseKey = dbNameToJavaName(key, false);
            keyMap.put(key, camelCaseKey);
            if (!StringUtils.equals(key, camelCaseKey)) {
                keyMap.put(camelCaseKey, camelCaseKey);
            }
        }

        return super.put(keyMap.get(key), value);
    }


    public Object put(String key, Object value, boolean camelCase) {
        if (!keyMap.containsKey(key)) {
            if (camelCase) {
                String camelCaseKey = dbNameToJavaName(key, false);
                keyMap.put(key, camelCaseKey);
                if (!StringUtils.equals(key, camelCaseKey)) {
                    keyMap.put(camelCaseKey, camelCaseKey);
                }
            } else {
                keyMap.put(key, key);
            }
        }

        return super.put(keyMap.get(key), value);
    }

}
