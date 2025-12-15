/*
 * @(#)2013-10-12 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.systemtable.support;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-12.1	zhouyq		2013-10-12		Create
 * </pre>
 * @date 2013-10-12
 */
public class CollectionUtil {

    public static List<String> getDiffentElement(List<String> minList, List<String> maxList) {
        List<String> newList = new ArrayList<String>();
        for (String max : maxList) {
            if (!(minList.contains(max))) {
                newList.add(max);
            }
        }
        return newList;
    }

    /**
     * 获取两个集合的不同元素
     *
     * @param collmax
     * @param collmin
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Collection getDiffent(Collection collmax, Collection collmin) {
        //使用LinkeList防止差异过大时,元素拷贝
        Collection csReturn = new LinkedList();
        Collection max = collmax;
        Collection min = collmin;
        //先比较大小,这样会减少后续map的if判断次数
        if (collmax.size() < collmin.size()) {
            max = collmin;
            min = collmax;
        }
        //直接指定大小,防止再散列
        Map<Object, Integer> map = new HashMap<Object, Integer>(max.size());
        for (Object object : max) {
            map.put(object, 1);
        }
        for (Object object : min) {
            if (map.get(object) == null) {
                csReturn.add(object);
            } else {
                map.put(object, 2);
            }
        }
        for (Map.Entry<Object, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                csReturn.add(entry.getKey());
            }
        }
        return csReturn;
    }

    /**
     * 获取两个集合的不同元素,去除重复
     *
     * @param collmax
     * @param collmin
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Collection getDiffentNoDuplicate(Collection collmax, Collection collmin) {
        return new HashSet(getDiffent(collmax, collmin));
    }
}
