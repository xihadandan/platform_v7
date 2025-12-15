/*
 * @(#)3/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.license.support.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 3/29/24.1	zhulh		3/29/24		Create
 * </pre>
 * @date 3/29/24
 */
@ConfigurationProperties(prefix = "license")
@EnableConfigurationProperties
public class LicenseProperties {

    private String serviceUrl;

    private String devProfile = "dev";

    /**
     * @return the serviceUrl
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * @param serviceUrl 要设置的serviceUrl
     */
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    /**
     * @return the devProfile
     */
    public String getDevProfile() {
        return devProfile;
    }

    /**
     * @param devProfile 要设置的devProfile
     */
    public void setDevProfile(String devProfile) {
        this.devProfile = devProfile;
    }
}
