/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: SecurityModifyPassowrdRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-11 上午9:05:28
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.SecurityModifyPasswordResponse;

/**
 * @author Administrator
 * @ClassName: SecurityModifyPassowrdRequest
 * @Description: TODO
 * @date 2014-12-11 上午9:05:28
 */
public class SecurityModifyPasswordRequest extends WellptRequest<SecurityModifyPasswordResponse> {

    /**
     * @Fields password : 旧密码
     */
    private String oldPassword;


    /**
     * @Fields password : 新密码
     */
    private String newPassword;

    @Override
    public String getApiServiceName() {
        return ApiServiceName.SECURITY_MODIFYPASSWORD;
    }

    @Override
    public Class<SecurityModifyPasswordResponse> getResponseClass() {
        return SecurityModifyPasswordResponse.class;
    }

    /**
     * @return the oldPassword
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * @param oldPassword the oldPassword to set
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * @return the newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * @param newPassword the newPassword to set
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
