/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ScheduleDeleteRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-19 下午1:16:21
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.ScheduleDeleteResponse;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Administrator
 * @ClassName: ScheduleDeleteRequest
 * @Description: TODO
 * @date 2014-12-19 下午1:16:21
 */
public class ScheduleDeleteRequest extends WellptRequest<ScheduleDeleteResponse> {

    /**
     * @Fields id : 日历id
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
        return ApiServiceName.SCHEDULE_DELETE;
    }

    /* (No Javadoc)
     * <p>Title: getResponseClass</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    @JsonIgnore
    public Class<ScheduleDeleteResponse> getResponseClass() {
        // TODO Auto-generated method stub
        return ScheduleDeleteResponse.class;
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
