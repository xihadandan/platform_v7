/*
 * @(#)2019年8月9日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.api.authentication;

import com.wellsoft.pt.api.request.ApiRequest;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月9日.1	zhulh		2019年8月9日		Create
 * </pre>
 * @date 2019年8月9日
 */
public class AuthenticationApiRequest extends ApiRequest {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8933233256994339849L;

    private String username;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username 要设置的username
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
