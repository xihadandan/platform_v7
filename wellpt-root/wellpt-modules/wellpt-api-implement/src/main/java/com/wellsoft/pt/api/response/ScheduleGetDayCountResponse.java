/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ScheduleGetDayCountResponse.java
 * @Package com.wellsoft.pt.api.response
 * @Description: TODO
 * @author Administrator
 * @date 2015-1-31 上午11:09:10
 * @version V1.0
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * @author Administrator
 * @ClassName: ScheduleGetDayCountResponse
 * @Description: TODO
 * @date 2015-1-31 上午11:09:10
 */
public class ScheduleGetDayCountResponse extends WellptResponse {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = -6365970527488546708L;

    /**
     * @Fields data : TODO
     */
    private Object data;

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }

}
