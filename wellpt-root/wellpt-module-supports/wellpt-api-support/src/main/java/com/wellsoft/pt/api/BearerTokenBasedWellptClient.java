/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpPost;


/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public class BearerTokenBasedWellptClient extends DefaultWellptClient {

    private String accessToken;

    /**
     * @param serverUrl
     * @param tenantId
     * @param userId
     * @param accessToken
     */
    public BearerTokenBasedWellptClient(String serverUrl, String tenantId, String userId, String accessToken) {
        super(serverUrl, tenantId, userId, StringUtils.EMPTY);
        this.accessToken = accessToken;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.DefaultWellptClient#prePost(org.apache.http.client.methods.HttpPost)
     */
    @Override
    protected void prePost(HttpPost post) {
        post.addHeader("Authorization", "Bearer " + accessToken);
    }

}
