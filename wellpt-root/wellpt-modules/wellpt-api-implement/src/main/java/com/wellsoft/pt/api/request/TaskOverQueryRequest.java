/*
 * @(#)2019年8月14日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

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
 * 2019年8月14日.1	zhulh		2019年8月14日		Create
 * </pre>
 * @date 2019年8月14日
 */
public class TaskOverQueryRequest extends TaskQueryRequest {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return ApiServiceName.TASK_OVER_QUERY;
    }

}
