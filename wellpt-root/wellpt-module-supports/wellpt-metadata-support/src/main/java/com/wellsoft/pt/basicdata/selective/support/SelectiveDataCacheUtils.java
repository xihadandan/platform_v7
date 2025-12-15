/*
 * @(#)2016年3月15日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.selective.support;

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
 * 2016年3月15日.1	zhulh		2016年3月15日		Create
 * </pre>
 * @date 2016年3月15日
 */
public class SelectiveDataCacheUtils {
    private static final Map<String, String> cacheMap = new HashMap<String, String>();
    private static String KEY_PREFIX = "SelectiveData.";

    public static boolean containsKey(String key) {
        return cacheMap.containsKey(key);
    }

    public static String get(String key) {
        return cacheMap.get(key);
    }

    public static void put(String key, String value) {
        cacheMap.put(key, value);
    }

    public static String getCacheKey(String key) {
        return KEY_PREFIX + key;
    }

}
