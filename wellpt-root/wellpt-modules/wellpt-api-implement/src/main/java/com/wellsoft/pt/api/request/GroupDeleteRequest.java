/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: GroupDeleteRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-19 下午1:09:05
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.GroupDeleteResponse;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Administrator
 * @ClassName: GroupDeleteRequest
 * @Description: TODO
 * @date 2014-12-19 下午1:09:05
 */
public class GroupDeleteRequest extends WellptRequest<GroupDeleteResponse> {

    /**
     * @Fields id : 群组id
     */
    private String id;

    /* (No Javadoc)
     * <p>Title: getApiServiceName</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        // TODO Auto-generated method stub
        return ApiServiceName.GROUP_DELETE;
    }

    /* (No Javadoc)
     * <p>Title: getResponseClass</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    @JsonIgnore
    public Class<GroupDeleteResponse> getResponseClass() {
        // TODO Auto-generated method stub
        return GroupDeleteResponse.class;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

}
