/*
 * @(#)2014-11-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cache.jedis.JedisCache;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.security.util.TenantContextHolder;

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
public class CacheImpl implements Cache {

    /**
     * 如何描述DIT
     */
    private static final String DOT = ".";

    private org.springframework.cache.Cache cache;

    /**
     * @param cache
     */
    public CacheImpl(org.springframework.cache.Cache cache) {
        super();
        this.cache = cache;
    }

    /**
     * @param key
     * @return
     */
    private static final Object wrapperKey(Object key) {
        String tenantId = TenantContextHolder.getTenantId();
        if (!tenantId.startsWith("T")) {
            TenantFacadeService tenantService = ApplicationContextHolder.getBean(
                    TenantFacadeService.class);
            Tenant t = tenantService.getByAccount(tenantId);
            tenantId = t == null ? "T001" : t.getId();
        }
        return key.toString().startsWith(tenantId) ? key : tenantId + Separator.COLON.getValue() + key;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#getName()
     */
    @Override
    public String getName() {
        return cache.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#getNativeCache()
     */
    @Override
    public Object getNativeCache() {
        return cache.getNativeCache();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#get(java.lang.Object)
     */
    @Override
    public ValueWrapper get(Object key) {
        return cache.get(wrapperKey(key));
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return cache.get(wrapperKey(key), type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return cache.get(wrapperKey(key), valueLoader);
    }

    /* (non-Javadoc)
     * @see com.wellsoft.pt.cache.CacheS#getValue(java.lang.Object)
     */
    @Override
    public Object getValue(Object key) {
        ValueWrapper value = cache.get(wrapperKey(key));
        return (value == null ? value : value.get());
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(Object key, Object value) {
        if (value != null) {
            cache.put(wrapperKey(key), value);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return cache.putIfAbsent(wrapperKey(key), value);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#evict(java.lang.Object)
     */
    @Override
    public void evict(Object key) {
        cache.evict(wrapperKey(key));
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.cache.Cache#clear()
     */
    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Boolean exists(Object key) {
        if (this.cache instanceof JedisCache) {
            return ((JedisCache) cache).exists(wrapperKey(key));
        }
        return cache.get(wrapperKey(key)) != null;
    }
}
