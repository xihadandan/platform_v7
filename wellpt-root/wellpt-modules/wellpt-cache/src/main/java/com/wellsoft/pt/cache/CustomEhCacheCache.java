/*
 * @(#)2013-6-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache;

import com.wellsoft.pt.security.util.TenantContextHolder;
import net.sf.ehcache.Ehcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.ehcache.EhCacheCache;

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
public class CustomEhCacheCache extends EhCacheCache {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param ehcache
     */
    public CustomEhCacheCache(Ehcache ehcache) {
        super(ehcache);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.ehcache.EhCacheCache#get(java.lang.Object)
     */
    @Override
    public ValueWrapper get(Object key) {
        logger.debug("get by key " + TenantContextHolder.getTenantId() + key);
        return super.get(TenantContextHolder.getTenantId() + key);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.ehcache.EhCacheCache#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(Object key, Object value) {
        logger.debug("put by key " + TenantContextHolder.getTenantId() + key + ", value " + value);
        super.put(TenantContextHolder.getTenantId() + key, value);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.ehcache.EhCacheCache#evict(java.lang.Object)
     */
    @Override
    public void evict(Object key) {
        logger.debug("evict by key " + TenantContextHolder.getTenantId() + key);
        super.evict(TenantContextHolder.getTenantId() + key);
    }

}
