/*
 * @(#)2017年12月27日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 将LIST数据 分组归类
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
public abstract class List2GroupMap<T extends Serializable> {
    // 将list分组
    public Map<String, List<T>> convert(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<String, List<T>>();
        }
        Map<String, List<T>> map = new HashMap<String, List<T>>((int) ((float) list.size() / 0.75F + 1.0F));
        for (T obj : list) {
            if (obj != null) {
                String groupUuid = this.getGroupUuid(obj);
                if (StringUtils.isNotBlank(groupUuid)) {
                    if (!map.containsKey(groupUuid)) {
                        map.put(groupUuid, new ArrayList<T>());
                    }
                    map.get(groupUuid).add(obj);
                }
            }
        }
        return map;
    }

    protected abstract String getGroupUuid(T obj);
}
