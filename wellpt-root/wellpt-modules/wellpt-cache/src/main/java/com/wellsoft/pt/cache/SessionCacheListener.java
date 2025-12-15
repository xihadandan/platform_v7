/*
 * @(#)2018年11月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache;

import com.wellsoft.context.util.ApplicationContextHolder;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Description:
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年11月12日.1	zhongzh		2018年11月12日		Create
 * </pre>
 * @date 2018年11月12日
 */
public class SessionCacheListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession httpSession = event.getSession();
        if (httpSession == null || StringUtils.isBlank(httpSession.getId())) {
            return;
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession httpSession = event.getSession();
        if (httpSession == null || StringUtils.isBlank(httpSession.getId())) {
            return;
        }
        String sessionId = httpSession.getId();
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        cacheManager.clearSessionCache(sessionId);
    }
}
