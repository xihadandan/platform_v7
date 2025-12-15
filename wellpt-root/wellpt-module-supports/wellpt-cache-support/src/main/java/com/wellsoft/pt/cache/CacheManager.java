package com.wellsoft.pt.cache;

import com.wellsoft.context.enums.ModuleID;

public interface CacheManager extends org.springframework.cache.CacheManager {

    /**
     * @return the cacheManager
     */
    public abstract org.springframework.cache.CacheManager getCacheManager();

    /**
     * @param cacheManager 要设置的cacheManager
     */
    public abstract void setCacheManager(org.springframework.cache.CacheManager cacheManager);

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.CacheManager#getCache(java.lang.String)
     */
    public abstract com.wellsoft.pt.cache.Cache getCache(ModuleID moduleID);

    public abstract com.wellsoft.pt.cache.Cache getCache(String name);

    public abstract com.wellsoft.pt.cache.Cache getSessionCache(String name);

    public abstract com.wellsoft.pt.cache.Cache getSessionCache(ModuleID moduleID);

    public abstract void clearSessionCache(String sessionId);

    /**
     * 注册缓存名称
     *
     * @param cacheName
     */
    public void registerCacheName(String cacheName);

    /**
     * 清理所有缓存
     */
    public abstract void clearAllCache();

}