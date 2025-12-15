/*
 * @(#)2014-7-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.dao.impl;

import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import com.wellsoft.pt.jpa.support.BeanPropertyResultTransformer;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-7.1	zhulh		2014-7-7		Create
 * </pre>
 * @date 2014-7-7
 */
public class BaseDaoImpl {
    private static final Map<Class<?>, ResultTransformer> itemTransformerMap = new HashMap<Class<?>, ResultTransformer>();
    private static final Map<String, ResultTransformer> entityTransformerMap = new HashMap<String, ResultTransformer>();

    protected static Logger logger = LoggerFactory.getLogger(BaseDaoImpl.class);

    static {
        itemTransformerMap.put(String.class, null);
        itemTransformerMap.put(Boolean.class, null);
        itemTransformerMap.put(Character.class, null);
        itemTransformerMap.put(Byte.class, null);
        itemTransformerMap.put(Short.class, null);
        itemTransformerMap.put(Integer.class, null);
        itemTransformerMap.put(Long.class, null);
        itemTransformerMap.put(Float.class, null);
        itemTransformerMap.put(Double.class, null);
    }

    protected static String getDynamicNamedQueryString(SessionFactory sessionFactory, String queryName,
                                                       Map<String, Object> values) {

        return NamedQueryScriptLoader.generateDynamicNamedQueryString(sessionFactory, queryName, values);

    }

    protected ResultTransformer getTtemTransformerMap(Class<?> itemClass) {
        if (!itemTransformerMap.containsKey(itemClass)) {
            itemTransformerMap.put(itemClass, new BeanPropertyResultTransformer(itemClass));
        }
        return itemTransformerMap.get(itemClass);
    }

    protected ResultTransformer getEntityTransformerMap(Class<?> entityClass, String queryLang) {
        String key = entityClass.getCanonicalName() + "_" + StringUtils.substringBefore(queryLang, "from");
        if (!entityTransformerMap.containsKey(key)) {
            entityTransformerMap.put(key, new AliasToBeanResultTransformer(entityClass));
        }
        return entityTransformerMap.get(key);
    }
}
