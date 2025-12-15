package com.wellsoft.pt.app.context.support;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Description: 有序的properties
 *
 * @author wujx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月10日.1	wujx		2016年8月10日		Create
 * </pre>
 * @date 2016年8月10日
 */
public class OrderedProperties extends Properties {

    private static final long serialVersionUID = -1539156724860908611L;

    private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

    public Enumeration<Object> keys() {
        return Collections.<Object>enumeration(keys);
    }

    public Object put(Object key, Object value) {
        Object trimKey = trimIfString(key);
        Object trimValue = trimIfString(value);
        keys.add(trimKey);
        return super.put(trimKey, trimValue);
    }

    /**
     * @param object
     * @return
     */
    private Object trimIfString(Object object) {
        if (object instanceof String) {
            return StringUtils.trimToEmpty((String) object);
        }
        return object;
    }

    public Set<Object> keySet() {
        return keys;
    }

    public Set<String> stringPropertyNames() {
        Set<String> set = new LinkedHashSet<String>();

        for (Object key : this.keys) {
            set.add((String) key);
        }

        return set;
    }
}
