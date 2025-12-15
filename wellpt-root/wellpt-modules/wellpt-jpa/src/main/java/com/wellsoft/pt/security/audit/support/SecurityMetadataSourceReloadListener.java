/*
 * @(#)2016-12-20 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.support;

import com.google.common.base.Throwables;
import com.wellsoft.context.config.Config;
import com.wellsoft.pt.jpa.event.WellptEventListener;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Description: 安全配置变更重新加载监听器
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
public class SecurityMetadataSourceReloadListener extends WellptEventListener<SecurityConfigUpdatedEvent> {

    @Autowired
    private SecurityMetadataSourceService securityMetadataSourceService;

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    @Async
    public synchronized void onApplicationEvent(SecurityConfigUpdatedEvent event) {
        String configUuid = event.getConfigUuid();
        String configType = event.getConfigType();
        try {
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, event.getUserId());
            if (StringUtils.isBlank(configUuid) || StringUtils.isBlank(configType)) {
                securityMetadataSourceService.loadSecurityMetadataSource();
            } else {
                securityMetadataSourceService.loadSecurityMetadataSource(configUuid, configType);
            }
        } catch (Exception e) {
            logger.error("权限重载监听异常：{}", Throwables.getStackTraceAsString(e));
        } finally {
            IgnoreLoginUtils.logout();
        }

    }

}
