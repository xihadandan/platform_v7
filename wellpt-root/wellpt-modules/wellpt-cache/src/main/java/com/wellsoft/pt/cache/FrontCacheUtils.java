/*
 * @(#)2019年9月21日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.cache.config.CacheName;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 前端js按模块缓存时间戳，每一次机器重启（更新）都重置所有模块时间戳
 * 在jsp中，使用${frontCache.moduleName}
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年9月21日.1	zhongzh		2019年9月21日		Create
 * </pre>
 * @date 2019年9月21日
 */
public abstract class FrontCacheUtils {

    /**
     * 如何描述DEFAULT_MODULE
     */
    public static final String DEFAULT_MODULE = "default";

    /**
     * 是否支持多模块
     */
    private static final boolean supportMultiModule = StringUtils.equalsIgnoreCase(
            Config.getValue("app.frontcache.supportMultiModule"), "true");

    private static final Map<String/*module*/, Long/*timestamp*/> pFrontCache = new HashMap<String, Long>();
    public static final Map<String/*module*/, Long/*timestamp*/> frontCache = Collections
            .unmodifiableMap(pFrontCache);

    static {
        // 每一次重启都重置一下前端缓存
        FrontCacheUtils.evictAll();
    }

    /**
     * 如何描述该方法
     *
     * @return <module, timestamp>
     */
    public static final Map<String, Long> getFrontCache() {
        return frontCache;
    }

    /**
     * 重置所有模块的时间戳
     */
    public static final void evictAll() {
        Long currentTimeMillis = System.currentTimeMillis();
        if (true == supportMultiModule) {
            for (String module : CacheName.getCacheNames()) {
                pFrontCache.put(module.toLowerCase(), currentTimeMillis);
            }
            pFrontCache.put("dyform", currentTimeMillis);
            pFrontCache.put("workflow", currentTimeMillis);
        }
        pFrontCache.put(DEFAULT_MODULE, currentTimeMillis);
    }

    /**
     * 重置指定模块的时间戳
     *
     * @param module
     * @return
     */
    public static final Long evict(String module) {
        if (module == null || false == supportMultiModule) {
            return pFrontCache.put(DEFAULT_MODULE, System.currentTimeMillis());
        }
        module = module.toLowerCase();
        if (pFrontCache.containsKey(module)) {
            return pFrontCache.put(module, System.currentTimeMillis());
        }
        throw new RuntimeException("不支持的前端缓存模块[" + module + "]");
    }

    /**
     * 获取前端指定模块的时间戳
     *
     * @param module
     * @return
     */
    public static final Long get(String module) {
        if (module == null || false == supportMultiModule) {
            return pFrontCache.get(DEFAULT_MODULE);
        }
        return frontCache.get(module.toLowerCase());
    }

    /**
     * 覆盖（更新）前端缓存
     *
     * @param request
     * @param response
     */
    public static final void overrideCookie(HttpServletRequest request,
                                            HttpServletResponse response) {
        String contextPath = request.getContextPath();
        try {
            Cookie frontCache = new Cookie("frontCache",
                    URLEncoder.encode(JsonUtils.object2Json(FrontCacheUtils.getFrontCache()),
                            "utf-8"));
            frontCache.setPath(contextPath.length() > 0 ? contextPath : "/");
            frontCache.setMaxAge(60 * 60 * 24 * 365);// 365天
            response.addCookie(frontCache);
        } catch (Exception e) {

        }

    }
}
