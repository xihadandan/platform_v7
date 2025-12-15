/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: GroupUserDeleteRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-19 下午1:14:29
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.GroupUserDeleteResponse;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Administrator
 * @ClassName: GroupUserDeleteRequest
 * @Description: TODO
 * @date 2014-12-19 下午1:14:29
 */
public class GroupUserDeleteRequest extends WellptRequest<GroupUserDeleteResponse> {
    /**
     * @Fields groupid : 群组id
     */
    private String groupid;
    /**
     * @Fields userid : 成员id
     */
    private String userid;

    /* (No Javadoc)
     * <p>Title: getApiServiceName</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        // TODO Auto-generated method stub
        return ApiServiceName.GROUPUSER_DELETE;
    }

    /* (No Javadoc)
     * <p>Title: getResponseClass</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    @JsonIgnore
    public Class<GroupUserDeleteResponse> getResponseClass() {
        // TODO Auto-generated method stub
        return GroupUserDeleteResponse.class;
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
