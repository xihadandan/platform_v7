/*
 * @(#)7/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache.support;

import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.springframework.cache.Cache;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/29/24.1	    zhulh		7/29/24		    Create
 * </pre>
 * @date 7/29/24
 */
public class JedisAndEhCache implements Cache {
    private Cache jedisCache;

    private Cache ehCache;

    /**
     * @param jedisCache
     * @param ehCache
     */
    public JedisAndEhCache(Cache jedisCache, Cache ehCache) {
        this.jedisCache = jedisCache;
        this.ehCache = ehCache;
    }

    @Override
    public String getName() {
        return jedisCache.getName();
    }

    @Override
    public Object getNativeCache() {
        return jedisCache.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper = null;
        if (IgnoreLoginUtils.isIgnoreLogin() && RequestContextHolder.getRequestAttributes() == null) {
            valueWrapper = ehCache.get(key);
            if (valueWrapper != null) {
                return valueWrapper;
            }
            valueWrapper = jedisCache.get(key);
            if (valueWrapper != null) {
                ehCache.put(key, valueWrapper.get());
            }
        } else {
            valueWrapper = jedisCache.get(key);
        }
        return valueWrapper;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = null;
        if (IgnoreLoginUtils.isIgnoreLogin() && RequestContextHolder.getRequestAttributes() == null) {
            value = ehCache.get(key, type);
            if (value != null) {
                return value;
            }
            value = jedisCache.get(key, type);
            if (value != null) {
                ehCache.put(key, value);
            }
        } else {
            value = jedisCache.get(key, type);
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = null;
        if (IgnoreLoginUtils.isIgnoreLogin() && RequestContextHolder.getRequestAttributes() == null) {
            value = ehCache.get(key, valueLoader);
            if (value != null) {
                return value;
            }
            value = jedisCache.get(key, valueLoader);
            if (value != null) {
                ehCache.put(key, value);
            }
        } else {
            value = jedisCache.get(key, valueLoader);
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        jedisCache.put(key, value);
        ehCache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ehCache.putIfAbsent(key, value);
        return jedisCache.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        jedisCache.evict(key);
        ehCache.evict(key);
    }

    @Override
    public void clear() {
        jedisCache.clear();
        ehCache.clear();
    }

}
