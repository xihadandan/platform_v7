/*
 * @(#)2016-12-20 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.support;

import com.wellsoft.context.event.WellptEvent;

/**
 * Description: 安全配置更新事件
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
public class SecurityConfigUpdatedEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4157330573922584383L;

    private String configUuid;

    private String configType;

    private String userId;

    /**
     * @param source
     */
    public SecurityConfigUpdatedEvent(String configUuid, String configType, String userId) {
        super(configUuid);
        this.configUuid = configUuid;
        this.configType = configType;
        this.userId = userId;
    }

    /**
     * @param source
     */
    public SecurityConfigUpdatedEvent(Object source, String userId) {
        super(source);
        this.userId = userId;
    }

    /**
     * @return the configUuid
     */
    public String getConfigUuid() {
        return configUuid;
    }

    /**
     * @param configUuid 要设置的configUuid
     */
    public void setConfigUuid(String configUuid) {
        this.configUuid = configUuid;
    }

    /**
     * @return the configType
     */
    public String getConfigType() {
        return configType;
    }

    /**
     * @param configType 要设置的configType
     */
    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
