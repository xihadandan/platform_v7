package com.wellsoft.context.util.reflection;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.dto.DataItem;
import com.wellsoft.context.util.BUtils;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConvertUtils {

    static {
        registerDateConverter();
    }

    public static void main(String[] args) {
        List<DataItem> list = Lists.newArrayList();
        for (int i = 0; i < 10000; i++) {
            DataItem item = new DataItem();
            item.setAceId(i + "");
            item.setLabel("l" + i);
            list.add(item);
        }

        Map<Object, DataItem> map1 = Maps.newHashMap();
        long t1 = System.currentTimeMillis();
        for (DataItem dataItem : list) {
            BeanWrapper wrapper = new BeanWrapperImpl(dataItem);
            map1.put((String) wrapper.getPropertyValue("aceId"), dataItem);
        }

        long t2 = System.currentTimeMillis();
        Map<Object, DataItem> map2 = Maps.newHashMap();
        for (DataItem dataItem : list) {
            MethodAccess method = BeanUtils.getMethodAccess(dataItem);
            String key = (String) method.invoke(dataItem, "get" + BUtils.capitalize("aceId"));
            map2.put(key, dataItem);
        }
        long t3 = System.currentTimeMillis();
        System.out.println("map1=" + (t2 - t1) + ",size=" + map1.size());
        System.out.println("map2=" + (t3 - t2) + ",size=" + map2.size());
        System.out.println("-------------------,不管数量是1000，还是1万， 明显 通过 methodAccess方法，耗时减少接近一半");
    }

    /**
     * 将集合元素以指定的key属性转换为Map
     *
     * @param sources
     * @param keyField
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> convertElementToMap(final Collection<V> sources, String keyField) {
        // 每次都 new 一个 BeanWrapperImpl ，效率太低了，换 methodAccess 来实现
        Map<K, V> map = Maps.newHashMap();
        for (V e : sources) {
            if (e == null) {
                continue;
            }
            MethodAccess method = BeanUtils.getMethodAccess(e);
            K key = (K) method.invoke(e, "get" + BUtils.capitalize(keyField));
            map.put(key, e);
            // BeanWrapper wrapper = new BeanWrapperImpl(e);
            // map.put((K) wrapper.getPropertyValue(keyField), e);
        }
        return map;
    }

    /**
     * 转换字符串到相应类型.
     *
     * @param value  待转换的字符串.
     * @param toType 转换目标类型.
     */
    public static Object convertStringToObject(String value, Class<?> toType) {
        try {
            return org.apache.commons.beanutils.ConvertUtils.convert(value, toType);
        } catch (Exception e) {
            throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 定义日期Converter的格式: yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
     */
    private static void registerDateConverter() {
        DateConverter dc = new DateConverter();
        dc.setUseLocaleFormat(true);
        dc.setPatterns(new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"});
        org.apache.commons.beanutils.ConvertUtils.register(dc, Date.class);
    }
}
