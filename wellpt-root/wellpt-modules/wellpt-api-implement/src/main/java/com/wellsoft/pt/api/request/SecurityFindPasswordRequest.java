/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: SecurityFindPasswordRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-11 上午9:11:08
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.SecurityFindPasswordResponse;

/**
 * @author Administrator
 * @ClassName: SecurityFindPasswordRequest
 * @Description: TODO
 * @date 2014-12-11 上午9:11:08
 */
public class SecurityFindPasswordRequest extends WellptRequest<SecurityFindPasswordResponse> {
    @Override
    public String getApiServiceName() {
        return ApiServiceName.SECURITY_FINDPASSWORD;
    }

    @Override
    public Class<SecurityFindPasswordResponse> getResponseClass() {
        return SecurityFindPasswordResponse.class;
    }
}
