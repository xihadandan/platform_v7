/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: OrgGroupInfoRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-11 上午9:26:37
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.OrgGroupInfoResponse;

/**
 * @author Administrator
 * @ClassName: OrgGroupInfoRequest
 * @Description: TODO
 * @date 2014-12-11 上午9:26:37
 */
public class OrgGroupInfoRequest extends WellptRequest<OrgGroupInfoResponse> {

    /**
     * @Fields groupid : group id
     */
    private String groupid;

    @Override
    public String getApiServiceName() {
        return ApiServiceName.GROUP_GETINFO;
    }

    @Override
    public Class<OrgGroupInfoResponse> getResponseClass() {
        return OrgGroupInfoResponse.class;
    }

    /**
     * @return the groupid
     */
    public String getGroupid() {
        return groupid;
    }

    /**
     * @param groupid the groupid to set
     */
    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

}
