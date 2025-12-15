/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ScheduleGetDayCountRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2015-1-31 上午11:11:01
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.ScheduleGetDayCountResponse;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Administrator
 * @ClassName: ScheduleGetDayCountRequest
 * @Description: TODO
 * @date 2015-1-31 上午11:11:01
 */
public class ScheduleGetDayCountRequest extends WellptRequest<ScheduleGetDayCountResponse> {

    private String strDate;
    private String endDate;

    /* (No Javadoc)
     * <p>Title: getApiServiceName</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        // TODO Auto-generated method stub
        return ApiServiceName.SCHEDULETAG_GET_DAY_COUNT;
    }

    /* (No Javadoc)
     * <p>Title: getResponseClass</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    @JsonIgnore
    public Class<ScheduleGetDayCountResponse> getResponseClass() {
        // TODO Auto-generated method stub
        return ScheduleGetDayCountResponse.class;
    }

    /**
     * @return the strDate
     */
    public String getStrDate() {
        return strDate;
    }

    /**
     * @param strDate the strDate to set
     */
    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
