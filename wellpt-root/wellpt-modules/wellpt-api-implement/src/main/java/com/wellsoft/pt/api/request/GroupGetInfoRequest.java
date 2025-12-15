/*
 * @(#)2019年8月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月19日.1	zhulh		2019年8月19日		Create
 * </pre>
 * @date 2019年8月19日
 */
public class GroupGetInfoRequest extends WellptRequest<WellptResponse> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return ApiServiceName.GROUP_GETINFO;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<WellptResponse> getResponseClass() {
        return WellptResponse.class;
    }

}
