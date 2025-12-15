/*
 * @(#)2019年6月17日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.support;

import org.springframework.security.access.SecurityConfig;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月17日.1	zhulh		2019年6月17日		Create
 * </pre>
 * @date 2019年6月17日
 */
public class SecurityResourceConfig extends SecurityConfig {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5072419777290198513L;
    private String resourceId;

    /**
     * @param resourceId
     * @param config
     */
    public SecurityResourceConfig(String resourceId, String config) {
        super(config);
        this.resourceId = resourceId;
    }

    /**
     * @return the resourceId
     */
    public String getResourceId() {
        return resourceId;
    }

}
