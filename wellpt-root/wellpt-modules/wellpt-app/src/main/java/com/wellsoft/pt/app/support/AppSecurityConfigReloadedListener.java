/*
 * @(#)2016-12-20 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.wellsoft.pt.jpa.event.WellptEventListener;
import com.wellsoft.pt.security.audit.support.SecurityConfigReloadedEvent;
import org.springframework.stereotype.Component;

/**
 * Description: 安全配置变更清理APP缓存事件监听
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-12-20.1	zhulh		2016-12-20		Create
 * </pre>
 * @date 2016-12-20
 */
@Component
public class AppSecurityConfigReloadedListener extends
        WellptEventListener<SecurityConfigReloadedEvent> {

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(SecurityConfigReloadedEvent event) {
        AppCacheUtils.clear();
    }

}
