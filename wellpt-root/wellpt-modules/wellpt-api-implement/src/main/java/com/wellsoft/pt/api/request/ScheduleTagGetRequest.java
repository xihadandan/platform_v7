/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ScheduleTagGetRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2015-1-30 下午7:59:50
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.ScheduleTagGetResponse;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Administrator
 * @ClassName: ScheduleTagGetRequest
 * @Description: TODO
 * @date 2015-1-30 下午7:59:50
 */
public class ScheduleTagGetRequest extends WellptRequest<ScheduleTagGetResponse> {

    /* (No Javadoc)
     * <p>Title: getApiServiceName</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        // TODO Auto-generated method stub
        return ApiServiceName.SCHEDULETAG_GET;
    }

    /* (No Javadoc)
     * <p>Title: getResponseClass</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    @JsonIgnore
    public Class<ScheduleTagGetResponse> getResponseClass() {
        // TODO Auto-generated method stub
        return ScheduleTagGetResponse.class;
    }

}
