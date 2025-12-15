/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: OrgUserInfoRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-11 上午9:26:59
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.OrgUserInfoResponse;

/**
 * @author Administrator
 * @ClassName: OrgUserInfoRequest
 * @Description: TODO
 * @date 2014-12-11 上午9:26:59
 */
public class OrgUserInfoRequest extends WellptRequest<OrgUserInfoResponse> {
    /**
     * @Fields userid : user id
     */
    private String userid;

    @Override
    public String getApiServiceName() {
        return ApiServiceName.USER_GETINFO;
    }

    @Override
    public Class<OrgUserInfoResponse> getResponseClass() {
        return OrgUserInfoResponse.class;
    }

    /**
     * @return the userid
     */
    public String getUserid() {
        return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

}
