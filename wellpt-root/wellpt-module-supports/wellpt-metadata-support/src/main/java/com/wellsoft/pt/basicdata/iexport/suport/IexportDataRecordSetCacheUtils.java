/*
 * @(#)2016年3月17日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月17日.1	zhulh		2016年3月17日		Create
 * </pre>
 * @date 2016年3月17日
 */
public class IexportDataRecordSetCacheUtils {

    private static Cache cache;

    private static Cache getCache() {
        if (cache == null) {
            CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
            cache = cacheManager.getCache(ModuleID.BASIC_DATA);
        }
        return cache;
    }

    public static Object getValue(String key) {
        return getCache().getValue(key);
    }

    public static void put(Object key, Object value) {
        getCache().put(key, value);
    }

    public static void clear() {
        getCache().clear();
    }

}
