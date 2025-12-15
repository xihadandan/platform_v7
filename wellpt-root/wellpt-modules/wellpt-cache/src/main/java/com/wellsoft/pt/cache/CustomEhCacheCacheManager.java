/*
 * @(#)2013-6-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.util.Assert;

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
 * 2013-6-14.1	zhulh		2013-6-14		Create
 * </pre>
 * @date 2013-6-14
 */
public class CustomEhCacheCacheManager extends AbstractCacheManager {

    private net.sf.ehcache.CacheManager cacheManager;

    /**
     * Return the backing EhCache {@link net.sf.ehcache.CacheManager}.
     */
    public net.sf.ehcache.CacheManager getCacheManager() {
        return this.cacheManager;
    }

    /**
     * Set the backing EhCache {@link net.sf.ehcache.CacheManager}.
     */
    public void setCacheManager(net.sf.ehcache.CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    protected Collection<Cache> loadCaches() {
        Assert.notNull(this.cacheManager, "A backing EhCache CacheManager is required");
        Status status = this.cacheManager.getStatus();
        Assert.isTrue(Status.STATUS_ALIVE.equals(status),
                "An 'alive' EhCache CacheManager is required - current cache is " + status.toString());

        String[] names = this.cacheManager.getCacheNames();
        Collection<Cache> caches = new LinkedHashSet<Cache>(names.length);
        for (String name : names) {
            caches.add(new CustomEhCacheCache(this.cacheManager.getEhcache(name)));
        }
        return caches;
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = super.getCache(name);
        if (cache == null) {
            // check the EhCache cache again
            // (in case the cache was added at runtime)
            Ehcache ehcache = this.cacheManager.getEhcache(name);
            if (ehcache != null) {
                cache = new CustomEhCacheCache(ehcache);
                addCache(cache);
            }
        }
        return cache;
    }
}
