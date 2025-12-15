/*
 * @(#)3/19/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.support.response;

import com.wellsoft.pt.app.feishu.support.FeishuResponse;
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
 * 3/19/25.1	    zhulh		3/19/25		    Create
 * </pre>
 * @date 3/19/25
 */
public class TenantAccessTokenResponse extends FeishuResponse {

    private int expire;

    @JsonProperty("tenant_access_token")
    private String tenantAccessToken;

    /**
     * @return the expire
     */
    public int getExpire() {
        return expire;
    }

    /**
     * @param expire 要设置的expire
     */
    public void setExpire(int expire) {
        this.expire = expire;
    }

    /**
     * @return the tenantAccessToken
     */
    public String getTenantAccessToken() {
        return tenantAccessToken;
    }

    /**
     * @param tenantAccessToken 要设置的tenantAccessToken
     */
    public void setTenantAccessToken(String tenantAccessToken) {
        this.tenantAccessToken = tenantAccessToken;
    }
}
