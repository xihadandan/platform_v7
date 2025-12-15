/*
 * @(#)2014-11-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache.config;

import com.wellsoft.context.enums.ModuleID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class CacheName {
    public static final String DEFAULT = "sc";// 平台默认的缓存指向
    public static final String SC = "sc";
    public static final String USER = "user";
    public static final String USER_PROFILE = "user_profile";
    private static List<String> cacheNames = new ArrayList<String>();

    static {
        cacheNames.add(DEFAULT);
        cacheNames.add(SC);
        cacheNames.add(USER);
        cacheNames.add(USER_PROFILE);

        ModuleID[] values = ModuleID.values();
        for (ModuleID moduleID : values) {
            cacheNames.add(moduleID.getName());
        }
    }

    public static List<String> getCacheNames() {
        return Collections.unmodifiableList(cacheNames);
    }
}
