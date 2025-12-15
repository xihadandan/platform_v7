/*
 * @(#)2015-1-22 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
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
 * 2015-1-22.1	zhulh		2015-1-22		Create
 * </pre>
 * @date 2015-1-22
 */
public class TaskDoneQueryRequest extends TaskQueryRequest {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.request.TaskQueryRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return ApiServiceName.TASK_DONE_QUERY;
    }

}
