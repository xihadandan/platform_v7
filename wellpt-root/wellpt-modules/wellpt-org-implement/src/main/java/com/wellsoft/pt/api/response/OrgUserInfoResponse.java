/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: OrgUserInfoResponse.java
 * @Package com.wellsoft.pt.api.response
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-11 上午9:24:18
 * @version V1.0
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * @author Administrator
 * @ClassName: OrgUserInfoResponse
 * @Description: TODO
 * @date 2014-12-11 上午9:24:18
 */
public class OrgUserInfoResponse extends WellptResponse {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = -2632279560559698567L;
    /**
     * @Fields user : 用户对象信息
     */
    private Object user;

    /**
     * @return the user
     */
    public Object getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(Object user) {
        this.user = user;
    }

}
