/*
 * @(#)2020年6月4日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.util;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.audit.support.SecurityConfigUpdatedEvent;
import org.springframework.core.NamedThreadLocal;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年6月4日.1	zhongzh		2020年6月4日		Create
 * </pre>
 * @date 2020年6月4日
 */
public class SecurityConfigUtils {

    private static final ThreadLocal<Boolean> enable = new NamedThreadLocal<Boolean>(
            "Enable Security Config Updated Event");

    /**
     * 启用安全配置更新标识，包含权限更新等
     *
     * @param
     * @return void
     **/
    public static void enableSecurityConfigUpdatedEvent() {
        enable.set(true);
    }

    /**
     * 禁用安全配置更新标识，包含权限更新等
     *
     * @param
     * @return void
     **/
    public static void disableSecurityConfigUpdatedEvent() {
        enable.set(false);
    }

    /**
     * 删除安全配置更新标识
     *
     * @param
     * @return void
     **/
    public static void removeSecurityConfigUpdatedEvent() {
        enable.remove();
    }

    /**
     * 发布执行安全配置更新，包含权限更新等
     * 安全配置更新标识不存在或者安全配置更新标识为启用，则执行更新
     *
     * @param source
     * @return void
     **/
    public static void publishSecurityConfigUpdatedEvent(Object source) {
        if (enable.get() == null || Boolean.TRUE.equals(enable.get())) {
            ApplicationContextHolder.getApplicationContext().publishEvent(
                    new SecurityConfigUpdatedEvent(source, SpringSecurityUtils.getCurrentUserId()));
        }
    }

    /**
     * 发布执行安全配置更新，包含权限更新等
     * 安全配置更新标识不存在或者安全配置更新标识为启用，则执行更新
     *
     * @param configUuid
     * @param configType
     * @return void
     **/
    public static void publishSecurityConfigUpdatedEvent(String configUuid, String configType) {
        if (enable.get() == null || Boolean.TRUE.equals(enable.get())) {
            ApplicationContextHolder.getApplicationContext().publishEvent(
                    new SecurityConfigUpdatedEvent(configUuid, configType, SpringSecurityUtils.getCurrentUserId()));
        }
    }

}
