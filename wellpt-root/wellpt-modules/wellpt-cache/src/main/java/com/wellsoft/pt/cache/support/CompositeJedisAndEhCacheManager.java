/*
 * @(#)7/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache.support;

import com.google.common.collect.Maps;
import com.wellsoft.pt.cache.jedis.JedisCacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.*;

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
public class CompositeJedisAndEhCacheManager extends AbstractCacheManager {
    private JedisCacheManager jedisCacheManager;

    private EhCacheCacheManager ehCacheManager;

    private Map<String, Cache> ehCacheMap = Maps.newConcurrentMap();

    public CompositeJedisAndEhCacheManager(JedisCacheManager jedisCacheManager, EhCacheCacheManager ehCacheManager) {
        this.jedisCacheManager = jedisCacheManager;
        this.ehCacheManager = ehCacheManager;
        // 本地缓存定时清理，10分钟清理一次
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ehCacheMap.entrySet().forEach(entry -> entry.getValue().clear());
            }
        }, 60 * 1000, 60 * 10 * 1000);
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return Collections.emptyList();
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = super.getCache(name);
        if (cache == null) {
            Cache ehCache = ehCacheManager.getCache(name);
            ehCacheMap.put(name, ehCache);
            cache = new JedisAndEhCache(jedisCacheManager.getCache(name), ehCache);
            this.addCache(cache);
        }
        return cache;
    }
}
