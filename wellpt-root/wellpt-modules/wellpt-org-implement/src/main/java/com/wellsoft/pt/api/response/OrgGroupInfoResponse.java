/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: OrgGroupInfoResponse.java
 * @Package com.wellsoft.pt.api.response
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-11 上午9:22:34
 * @version V1.0
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * @author Administrator
 * @ClassName: OrgGroupInfoResponse
 * @Description: TODO
 * @date 2014-12-11 上午9:22:34
 */
public class OrgGroupInfoResponse extends WellptResponse {
    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = -7861960716010437019L;
    /**
     * @Fields group : 群组对象
     */
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
