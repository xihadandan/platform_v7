/*
 * @(#)2018年11月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年11月12日.1	zhongzh		2018年11月12日		Create
 * </pre>
 * @date 2018年11月12日
 */
public class SessionCacheImpl extends CacheImpl {

    private static final String DOT = ".";

    private String sessionId;

    public SessionCacheImpl(String sessionId, Cache cache) {
        super(cache);
        this.sessionId = sessionCacheKy(sessionId);
    }

    public static String sessionCacheKy(String session) {
        return "S" + DOT + session;
    }

    @Override
    public Object getValue(Object key) {
        return super.getValue(wrapperKey(key));
    }

    @Override
    public ValueWrapper get(Object key) {
        return super.get(wrapperKey(key));
    }

    @Override
    public void put(Object key, Object value) {
        Object cacheKey = wrapperKey(key);
        Set<Object> cacheKeySet = (Set<Object>) super.getValue(sessionId);
        if (cacheKeySet == null) {
            cacheKeySet = Collections.newSetFromMap(new ConcurrentHashMap<Object, Boolean>());
        }
        if (false == cacheKeySet.contains(cacheKey)) {
            cacheKeySet.add(cacheKey);
            super.put(sessionId, cacheKeySet);
        }
        super.put(cacheKey, value);
    }

    @Override
    public void evict(Object key) {
        Object cacheKey = wrapperKey(key);
        Set<Object> cacheKeySet = (Set<Object>) super.getValue(sessionId);
        if (cacheKeySet != null && cacheKeySet.contains(cacheKey)) {
            cacheKeySet.remove(cacheKey);
            super.put(sessionId, cacheKeySet);
        }
        super.evict(cacheKey);
    }

    @Override
    public void clear() {
        this.clear(false);
    }

    public void clear(boolean release) {
        Set<Object> cacheKeySet = (Set<Object>) super.getValue(sessionId);
        if (cacheKeySet == null || cacheKeySet.isEmpty()) {
            return;
        }
        for (Object cacheKey : cacheKeySet) {
            super.evict(cacheKey);
            if (release && super.get(cacheKey) == null) {
                // 释放内存
                cacheKeySet.remove(cacheKey);
            }
        }
        super.evict(sessionId);
        if (release && super.getValue(sessionId) == null) {
            // 释放内存
            this.sessionId = null;
        }
    }

    /**
     * @param key
     * @return
     */
    private final Object wrapperKey(Object key) {
        return sessionId + DOT + key;
    }
}
