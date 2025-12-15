/*
 * @(#)2015年9月16日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.selective.facade;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.selective.service.SelectiveDataService;
import com.wellsoft.pt.basicdata.selective.support.DataItem;
import com.wellsoft.pt.basicdata.selective.support.SelectiveData;
import com.wellsoft.pt.basicdata.selective.support.SelectiveDataCacheUtils;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月16日.1	zhulh		2015年9月16日		Create
 * </pre>
 * @date 2015年9月16日
 */
public class SelectiveDatas {

    private static String EMPTY_STRING = "";

    public static SelectiveData get(String configKey) {
        SelectiveDataService selectiveDataService = ApplicationContextHolder.getBean(SelectiveDataService.class);
        return selectiveDataService.get(configKey);
    }

    public static Object getItems(String configKey) {
        SelectiveData selectiveData = get(configKey);
        return selectiveData == null ? Collections.emptyList() : selectiveData.getItems();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <ITEM extends Serializable> List<ITEM> getItems(String configKey, Class<ITEM> itemClass) {
        Object itemsObject = getItems(configKey);
        List<ITEM> newItems = new ArrayList<ITEM>();

        if (itemsObject.getClass().isArray()) {
            Object[] itemsArray = (Object[]) itemsObject;
            for (Object item : itemsArray) {
                if (itemClass.isAssignableFrom(item.getClass())) {
                    newItems.addAll((Collection) Arrays.asList(itemsArray));
                    break;
                }
            }
        } else if (itemsObject instanceof Collection) {
            final Collection optionCollection = (Collection) itemsObject;
            for (Object item : optionCollection) {
                if (itemClass.isAssignableFrom(item.getClass())) {
                    newItems.addAll(optionCollection);
                    break;
                }
            }
        } else if (itemsObject instanceof Map) {
        }

        return newItems;
    }

    @SuppressWarnings("rawtypes")
    public static String getLabel(String configKey, String value) {
        SelectiveData selectiveData = get(configKey);
        if (selectiveData == null) {
            return EMPTY_STRING;
        }
        Object itemsObject = selectiveData.getItems();
        if (itemsObject.getClass().isArray()) {
            Object[] itemsArray = (Object[]) itemsObject;
            return getItemLabel(selectiveData, itemsArray, value);
        } else if (itemsObject instanceof Collection) {
            final Collection optionCollection = (Collection) itemsObject;
            return getItemLabel(selectiveData, optionCollection.toArray(), value);
        } else if (itemsObject instanceof Map) {
            final Map optionMap = (Map) itemsObject;
            return object2String(optionMap.get(value));
        }

        return EMPTY_STRING;
    }

    public static String getLabel(String configKey, String value, String defaultLabel) {
        String lable = getLabel(configKey, value);
        return EMPTY_STRING.equals(lable) ? defaultLabel : lable;
    }

    /**
     * @param itemsArray
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static String getItemLabel(SelectiveData selectiveData, Object[] itemsArray, String value) {
        String itemLabel = selectiveData.getItemLabel();
        String itemValue = selectiveData.getItemValue();

        for (Object object : itemsArray) {
            // Map对象
            if (object instanceof Map) {
                Map map = (Map) object;
                Object objValue = map.get(itemValue);
                if (objValue != null && StringUtils.equals(objValue.toString(), value)) {
                    Object label = map.get(itemLabel);
                    return label == null ? EMPTY_STRING : label.toString();
                }
            } else if (object instanceof DataItem) {
                // 数据项
                DataItem item = (DataItem) object;
                String objValue = object2String(item.getValue());
                if (StringUtils.equals(objValue.toString(), value)) {
                    Object label = item.getLabel();
                    return label == null ? EMPTY_STRING : label.toString();
                }
            } else {
                // 自定义对象
                BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
                String objValue = object2String(wrapper.getPropertyValue(itemValue));
                if (StringUtils.equals(objValue.toString(), value)) {
                    Object label = wrapper.getPropertyValue(itemLabel);
                    return label == null ? EMPTY_STRING : label.toString();
                }
            }
        }

        return EMPTY_STRING;
    }

    private static String object2String(Object object) {
        return object == null ? EMPTY_STRING : object.toString();
    }

    /**
     * @param configKey
     */
    public static void clear(String configKey) {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        String cacheName = SelectiveDataCacheUtils.get(configKey);
        if (StringUtils.isBlank(cacheName)) {
            return;
        }
        Cache cache = (Cache) cacheManager.getCache(cacheName);
        String cacheKey = SelectiveDataCacheUtils.getCacheKey(configKey);
        cache.evict(cacheKey);
    }


}
