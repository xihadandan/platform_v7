package com.wellsoft.pt.dyform.implement.definition.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存管理器
 *
 * @author hunt
 */
public class LocalCacheManager {
    private static ConcurrentHashMap<String, CachedObject> localCache = new ConcurrentHashMap<String, CachedObject>();

    public static Object getValue(String key) {
        CachedObject cachedObject = localCache.get(key);
        if (cachedObject == null) {//本地缓存不存在该key
            return null;
        } else {
            if (cachedObject.doIsTimeout()) {//超时
                return null;
            } else {
                return cachedObject.getValue();
            }
        }
    }


    /**
     * @param key
     * @param value   值为null时表示清空缓存
     * @param timeOut
     */
    public static void setValue(String key, Object value, long timeOut) {
        CachedObject cachedObject = new CachedObject();
        cachedObject.setValue(value);
        cachedObject.doBindCreateTimeAsNow();
        cachedObject.setTimeout(timeOut);
        localCache.put(key, cachedObject);
    }

    public static void evict(String key) {
        localCache.remove(key);
    }


}
