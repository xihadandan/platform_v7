/*
 * @(#)2014-11-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache;

import com.google.common.collect.Sets;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.pt.cache.config.CacheName;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
public class CacheManagerImpl implements CacheManager {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final ConcurrentMap<String, com.wellsoft.pt.cache.Cache> cacheMap = new ConcurrentHashMap<String, com.wellsoft.pt.cache.Cache>(
            16);
    private final ConcurrentMap<String, Map<String, com.wellsoft.pt.cache.Cache>> sessionCacheMap = new ConcurrentHashMap<String, Map<String, com.wellsoft.pt.cache.Cache>>(
            16);
    private org.springframework.cache.CacheManager cacheManager;
    private Set<String> cacheNames = Sets.newHashSet();

    public static String getSessionId() {
        HttpSession httpSession;
        HttpServletRequest request = CacheManagerImpl.getRequest();
        if (request == null || (httpSession = request.getSession()) == null) {
            return null;
        }
        return httpSession.getId();
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? null : attrs.getRequest();
    }

    /* (non-Javadoc)
     * @see com.wellsoft.pt.cache.CacheManager1#getCacheManager()
     */
    @Override
    public org.springframework.cache.CacheManager getCacheManager() {
        return cacheManager;
    }

    /* (non-Javadoc)
     * @see com.wellsoft.pt.cache.CacheManager1#setCacheManager(org.springframework.cache.CacheManager)
     */
    @Override
    public void setCacheManager(org.springframework.cache.CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /* (non-Javadoc)
     * @see com.wellsoft.pt.cache.CacheManager1#getCache(com.wellsoft.context.enums.ModuleID)
     */
    @Override
    public com.wellsoft.pt.cache.Cache getCache(ModuleID moduleID) {
        return getCache(moduleID.getName());
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.CacheManager#getCache(java.lang.String)
     */
    @Override
    public com.wellsoft.pt.cache.Cache getCache(String name) {
        if (!getCacheNames().contains(name)) {
            // throw new RuntimeException("Cache name [" + name + "] is unknow");
            logger.warn(String.format("Cache name [%s] is unknow", name));
        }
        Cache cache = cacheManager.getCache(name);
        if (!cacheMap.containsKey(name)) {
            cacheMap.put(name, new com.wellsoft.pt.cache.CacheImpl(cache));
        }
        return cacheMap.get(name);
    }

    @Override
    public com.wellsoft.pt.cache.Cache getSessionCache(ModuleID moduleID) {
        return getSessionCache(moduleID.getName());
    }

    @Override
    public com.wellsoft.pt.cache.Cache getSessionCache(String name) {
        String sessionId = CacheManagerImpl.getSessionId();
        if (StringUtils.isBlank(sessionId)) {
            return getCache(name);
        }
        // ConcurrentMap
        Map<String, com.wellsoft.pt.cache.Cache> cacheMap = sessionCacheMap.get(sessionId);
        if (null == cacheMap) {
            synchronized (sessionCacheMap) {
                cacheMap = sessionCacheMap.get(sessionId);
                if (null == cacheMap) {
                    cacheMap = new ConcurrentHashMap<String, com.wellsoft.pt.cache.Cache>(4);
                    sessionCacheMap.put(sessionId, cacheMap);
                }
            }
        }
        com.wellsoft.pt.cache.Cache sessionCache = cacheMap.get(name);
        if (null == sessionCache) {
            synchronized (cacheMap) {
                sessionCache = cacheMap.get(name);
                if (null == sessionCache) {
                    sessionCache = new SessionCacheImpl(sessionId, getCache(name));
                    cacheMap.put(name, sessionCache);
                }
            }
        }
        return sessionCache;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.CacheManager#getCacheNames()
     */
    @Override
    public Collection<String> getCacheNames() {
        if (CollectionUtils.isEmpty(cacheNames)) {
            cacheNames.addAll(CacheName.getCacheNames());
        }
        cacheNames.addAll(cacheMap.keySet());
        return cacheNames;
    }

    @Override
    public void clearSessionCache(String sessionId) {
        // ConcurrentMap
        Map<String, com.wellsoft.pt.cache.Cache> cacheMap = sessionCacheMap.remove(sessionId);
        if (cacheMap == null || cacheMap.isEmpty()) {
            return;
        }
        for (String cacheName : cacheMap.keySet()) {
            // SessionCacheImpl.clear
            SessionCacheImpl sessionCache = (SessionCacheImpl) cacheMap.remove(cacheName);
            sessionCache.clear(true);
        }
    }

    /**
     * @param cacheName
     */
    public void registerCacheName(String cacheName) {
        getCacheNames().add(cacheName);
    }

    @Override
    public void clearAllCache() {
        Collection<String> cacheNames = getCacheNames();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (null == cache) {
                continue;
            }
            cache.clear();
        }
    }

}
