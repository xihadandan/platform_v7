/*
 * @(#)2017年12月28日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.collection;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.BUtils;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年12月28日.1	zyguo		2017年12月28日		Create
 * </pre>
 * @date 2017年12月28日
 */
public class ListUtils {
    // private static Logger log = LoggerFactory.getLogger(ListUtils.class);

    public static <T> String list2StringsByField(List<T> list, String fieldName) {
        return list2StringsByField(list, fieldName, ";");
    }

    /**
     * 取出list的所有指定的字段值, 并拼接在一起返回
     *
     * @param list
     * @param fieldName 指定的字段名
     * @param separator 分隔符
     * @return
     */
    public static <T> String list2StringsByField(List<T> list, String fieldName, String separator) {
        List<String> values = pickField(list, fieldName);
        if (BUtils.isNotEmpty(values)) {
            return StringUtils.join(values, separator);
        }
        return null;
    }

    // 按指定的字段转成map
    public static <T, R> Map<R, T> list2map(List<T> list, String fieldName) {
        if (list == null || list.isEmpty()) {
            return Maps.newHashMap();
        }
        final MethodAccess access = BeanUtils.getMethodAccess(list.get(0));
        final String method = "get" + StringUtils.capitalize(fieldName);
        return Maps.uniqueIndex(list.iterator(), new Function<T, R>() {
            @SuppressWarnings("unchecked")
            @Override
            public R apply(T obj) {
                return (R) access.invoke(obj, method);
            }
        });
    }

    // 从LIST中，挑出指定的字段，以list方式返回
    public static <T, R> List<R> pickField(List<T> list, String fieldName) {
        if (BUtils.isNotEmpty(list)) {
            final MethodAccess access = BeanUtils.getMethodAccess(list.get(0));
            final String method = "get" + StringUtils.capitalize(fieldName);
            return Lists.transform(list, new Function<T, R>() {
                @SuppressWarnings("unchecked")
                @Override
                public R apply(T obj) {
                    return (R) access.invoke(obj, method);
                }
            });
        }
        return Lists.newArrayList();
    }

    // 将LIST,按指定的字段进行分组
    @SuppressWarnings("unchecked")
    public static <T, R> Map<R, List<T>> list2group(List<T> list, String fieldName) {
        Map<R, List<T>> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(list)) {
            MethodAccess access = BeanUtils.getMethodAccess(list.get(0));
            String method = "get" + StringUtils.capitalize(fieldName);
            for (T item : list) {
                R group = (R) access.invoke(item, method);
                if (!map.containsKey(group)) {
                    map.put(group, new ArrayList<T>());
                }
                map.get(group).add(item);
            }
        }
        return map;
    }

    // 将LIST按指定字段进行排序, 支持升序和降序，默认升序, 目前只支持
    public static <T, R extends Comparable<R>> void sort(List<T> list, String fieldName, final boolean isAsc) {
        if (CollectionUtils.isNotEmpty(list)) {
            final MethodAccess access = BeanUtils.getMethodAccess(list.get(0));
            final String method = "get" + StringUtils.capitalize(fieldName);
            Comparator<T> comparator = new Comparator<T>() {
                @SuppressWarnings("unchecked")
                @Override
                public int compare(T o1, T o2) {
                    R v1 = (R) access.invoke(o1, method);
                    R v2 = (R) access.invoke(o2, method);
                    if (isAsc) {
                        return v1.compareTo(v2);
                    }
                    return v2.compareTo(v1);
                }
            };
            Collections.sort(list, comparator);
        }

    }

    /**
     * 依次按指定大小处理子列表
     *
     * @param list
     * @param subListSize
     * @param consumer
     */
    public static <T> void handleSubList(List<T> list, int subListSize, Consumer<List<T>> consumer) {
        int listSize = CollectionUtils.size(list);
        if (listSize > subListSize) {
            int fromIndex = 0;
            int toIndex = 0;
            while (toIndex < listSize) {
                toIndex = fromIndex + subListSize;
                if (toIndex > listSize) {
                    toIndex = listSize;
                }
                consumer.accept(list.subList(fromIndex, toIndex));
                fromIndex = toIndex;
            }
        } else {
            consumer.accept(list);
        }
    }

}
