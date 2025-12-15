/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: GroupAddRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-19 下午1:06:10
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.GroupAddResponse;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Administrator
 * @ClassName: GroupAddRequest
 * @Description: TODO
 * @date 2014-12-19 下午1:06:10
 */
public class GroupAddRequest extends WellptRequest<GroupAddResponse> {
    /**
     * @Fields name : 群名
     */
    private String name;
    /**
     * @Fields members : 群成员
     */
    private String members;

    /* (No Javadoc)
     * <p>Title: getApiServiceName</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        // TODO Auto-generated method stub
        return ApiServiceName.GROUP_ADD;
    }

    /* (No Javadoc)
     * <p>Title: getResponseClass</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    @JsonIgnore
    public Class<GroupAddResponse> getResponseClass() {
        // TODO Auto-generated method stub
        return GroupAddResponse.class;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the members
     */
    public String getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(String members) {
        this.members = members;
    }

}
