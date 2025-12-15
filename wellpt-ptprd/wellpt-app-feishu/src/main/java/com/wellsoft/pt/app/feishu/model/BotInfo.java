/*
 * @(#)3/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.model;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 3/25/25.1	    zhulh		3/25/25		    Create
 * </pre>
 * @date 3/25/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BotInfo extends BaseObject {

    @JsonProperty("app_name")
    private String appName;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    /**
     * @return the appName
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @param appName 要设置的appName
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return the avatarUrl
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * @param avatarUrl 要设置的avatarUrl
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
