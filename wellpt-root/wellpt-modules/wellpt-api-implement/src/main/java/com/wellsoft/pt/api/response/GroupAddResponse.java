/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: GroupAddResponse.java
 * @Package com.wellsoft.pt.api.response
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-19 下午1:06:53
 * @version V1.0
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * @author Administrator
 * @ClassName: GroupAddResponse
 * @Description: TODO
 * @date 2014-12-19 下午1:06:53
 */
public class GroupAddResponse extends WellptResponse {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 4222129325099285021L;

    private Object group;

    /**
     * @return the group
     */
    public Object getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(Object group) {
        this.group = group;
    }

}
