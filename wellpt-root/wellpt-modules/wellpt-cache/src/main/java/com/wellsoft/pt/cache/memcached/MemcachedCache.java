/*
 * @(#)2014-11-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache.memcached;

import com.wellsoft.context.util.encode.Md5PasswordEncoderUtils;
import net.spy.memcached.KeyUtil;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-11-13.1	zhulh		2014-11-13		Create
 * </pre>
 * @date 2014-11-13
 */
public class MemcachedCache implements Cache {
    // 默认过期时间10天
    private static final int expire = 10 * 24 * 60 * 60;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String name;
    private String namePrefix;
    private MemcachedClient memcachedClient;
    private Cache localCache;
    private Set<String> cacheKeys = new HashSet<String>();

    public MemcachedCache(String name, MemcachedClient memcachedClient, Cache localCache) {
        this.name = name;
        this.namePrefix = name + ".";
        this.memcachedClient = memcachedClient;
        this.localCache = localCache;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#getNativeCache()
     */
    @Override
    public Object getNativeCache() {
        return this.memcachedClient;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#get(java.lang.Object)
     */
    @Override
    public ValueWrapper get(Object key) {
        // 先从本地缓存取数据，如果存在直接返回，不存在从memcached取
        Object value = null;
        if (localCache != null) {
            value = localCache.get(key);
        }
        if (value instanceof ValueWrapper) {
            return (ValueWrapper) value;
        }

        String cacheKey = getCacheKey(key);
        try {
            value = memcachedClient.get(cacheKey);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        if (value != null && localCache != null) {
            localCache.put(key, value);
        }
        return value != null ? new SimpleValueWrapper(value) : null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T) localCache.get(key);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        if (get(key) != null) {
            return (T) get(key);
        } else {
            try {
                T v = valueLoader.call();
                put(key, v);
                return (T) get(key);
            } catch (Exception e) {

            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(Object key, Object value) {
        if (localCache != null) {
            localCache.put(key, value);
        }

        String cacheKey = getCacheKey(key);
        memcachedClient.set(cacheKey, expire, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if (get(key) != null) {
            put(key, value);
            return null;
        }
        return get(key);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#evict(java.lang.Object)
     */
    @Override
    public void evict(Object key) {
        if (localCache != null) {
            localCache.evict(key);
        }

        String cacheKey = getCacheKey(key);
        memcachedClient.delete(cacheKey);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#clear()
     */
    @Override
    public void clear() {
        if (localCache != null) {
            localCache.clear();
        }
        for (String cacheKey : cacheKeys) {
            memcachedClient.delete(cacheKey);
        }
    }

    /**
     * @param key
     * @return
     */
    private String getCacheKey(Object key) {
        String cacheKey = namePrefix + key;
        byte[] keyBytes = KeyUtil.getKeyBytes(cacheKey);
        if (keyBytes.length > MemcachedClientIF.MAX_KEY_LENGTH) {
            cacheKey = Md5PasswordEncoderUtils.encodePassword(cacheKey, null);
        }
        cacheKeys.add(cacheKey);
        return cacheKey;
    }

}
