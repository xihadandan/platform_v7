/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ScheduleTagGetResponse.java
 * @Package com.wellsoft.pt.api.response
 * @Description: TODO
 * @author Administrator
 * @date 2015-1-30 下午8:00:39
 * @version V1.0
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * @author Administrator
 * @ClassName: ScheduleTagGetResponse
 * @Description: TODO
 * @date 2015-1-30 下午8:00:39
 */
public class ScheduleTagGetResponse extends WellptResponse {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = -2349282214693251693L;

    /**
     * @Fields tags : 我的日历类型列表
     */
    private Object tags;

    /**
     * @return the tags
     */
    public Object getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(Object tags) {
        this.tags = tags;
    }
}
