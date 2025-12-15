/*
 * @(#)2017年12月27日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.collection;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 将list转成Map对象,
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年12月27日.1	zyguo		2017年12月27日		Create
 * </pre>
 * @date 2017年12月27日
 */
public abstract class List2Map<T extends Serializable> {
    public Map<String, T> convert(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<String, T>();
        }
        Map<String, T> map = new HashMap<String, T>((int) ((float) list.size() / 0.75F + 1.0F));
        for (T obj : list) {
            if (obj != null) {
                String key = this.getMapKey(obj);
                map.put(key, obj);
            }
        }
        return map;
    }

    protected abstract String getMapKey(T obj);
}
