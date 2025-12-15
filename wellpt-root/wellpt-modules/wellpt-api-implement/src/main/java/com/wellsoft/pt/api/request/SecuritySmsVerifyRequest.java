/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: SmsVerifyRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-10 下午4:54:24
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.SecuritySmsVerifyResponse;

/**
 * @author Administrator
 * @ClassName: SmsVerifyRequest
 * @Description: TODO
 * @date 2014-12-10 下午4:54:24
 */
public class SecuritySmsVerifyRequest extends WellptRequest<SecuritySmsVerifyResponse> {
    /**
     * @Fields smsverifycode : 短信验证码
     */
    private String smsverifycode;

    @Override
    public String getApiServiceName() {
        return ApiServiceName.SECURITY_SMSVERIFY;
    }

    @Override
    public Class<SecuritySmsVerifyResponse> getResponseClass() {
        return SecuritySmsVerifyResponse.class;
    }

    /**
     * @return the smsverifycode
     */
    public String getSmsverifycode() {
        return smsverifycode;
    }

    /**
     * @param smsverifycode the smsverifycode to set
     */
    public void setSmsverifycode(String smsverifycode) {
        this.smsverifycode = smsverifycode;
    }
}
