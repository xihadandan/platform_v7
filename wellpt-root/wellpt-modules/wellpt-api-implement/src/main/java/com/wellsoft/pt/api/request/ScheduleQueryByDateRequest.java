/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ScheduleQueryByDateRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-19 下午1:19:05
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.ScheduleQueryByDateResponse;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Administrator
 * @ClassName: ScheduleQueryByDateRequest
 * @Description: TODO
 * @date 2014-12-19 下午1:19:05
 */
public class ScheduleQueryByDateRequest extends WellptRequest<ScheduleQueryByDateResponse> {

    private String startDate;
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
        return ApiServiceName.SCHEDULE_QUERYBYDATE;
    }

    /* (No Javadoc)
     * <p>Title: getResponseClass</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    @JsonIgnore
    public Class<ScheduleQueryByDateResponse> getResponseClass() {
        // TODO Auto-generated method stub
        return ScheduleQueryByDateResponse.class;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
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
