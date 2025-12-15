/*
 * @(#)2014-11-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache.memcached;

import net.spy.memcached.MemcachedClient;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.LinkedHashSet;

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
public class MemcachedCacheManager extends AbstractCacheManager {

    private MemcachedClient memcachedClient;

    private boolean cacheInLocal = true;

    private org.springframework.cache.CacheManager localCacheManager;

    /**
     * @return the memcachedClient
     */
    public MemcachedClient getMemcachedClient() {
        return memcachedClient;
    }

    /**
     * @param memcachedClient 要设置的memcachedClient
     */
    public void setMemcachedClient(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    /**
     * @return the cacheInLocal
     */
    public boolean isCacheInLocal() {
        return cacheInLocal;
    }

    /**
     * @param cacheInLocal 要设置的cacheInLocal
     */
    public void setCacheInLocal(boolean cacheInLocal) {
        this.cacheInLocal = cacheInLocal;
    }

    /**
     * @return the localCacheManager
     */
    public org.springframework.cache.CacheManager getLocalCacheManager() {
        return localCacheManager;
    }

    /**
     * @param localCacheManager 要设置的localCacheManager
     */
    public void setLocalCacheManager(org.springframework.cache.CacheManager localCacheManager) {
        this.localCacheManager = localCacheManager;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.CacheManager#getCache(java.lang.String)
     */
    @Override
    public Cache getCache(String name) {
        Cache cache = super.getCache(name);
        if (cache == null) {
            cache = new MemcachedCache(name, this.memcachedClient, getLocalCache(name));
            super.addCache(cache);
        }
        return cache;
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    private Cache getLocalCache(String name) {
        if (cacheInLocal && localCacheManager != null) {
            return localCacheManager.getCache(name);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.support.AbstractCacheManager#loadCaches()
     */
    @Override
    protected Collection<? extends Cache> loadCaches() {
        return new LinkedHashSet<Cache>(0);
    }

}
