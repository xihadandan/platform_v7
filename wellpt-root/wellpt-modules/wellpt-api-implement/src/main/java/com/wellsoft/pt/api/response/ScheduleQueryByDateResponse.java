/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ScheduleQueryByDateResponse.java
 * @Package com.wellsoft.pt.api.response
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-19 下午1:19:30
 * @version V1.0
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * @author Administrator
 * @ClassName: ScheduleQueryByDateResponse
 * @Description: TODO
 * @date 2014-12-19 下午1:19:30
 */
public class ScheduleQueryByDateResponse extends WellptResponse {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 3123319479811090386L;

    private Object schedule;

    /**
     * @return the schedule
     */
    public Object getSchedule() {
        return schedule;
    }

    /**
     * @param schedule the schedule to set
     */
    public void setSchedule(Object schedule) {
        this.schedule = schedule;
    }
}
