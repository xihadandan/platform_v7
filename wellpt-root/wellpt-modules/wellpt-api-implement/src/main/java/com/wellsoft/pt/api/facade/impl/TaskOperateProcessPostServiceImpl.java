/*
 * @(#)2014-9-25 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskOperateProcessPostRequest;
import com.wellsoft.pt.api.response.TaskOperateProcessPostResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-25.1	Asus		2014-9-25		Create
 * </pre>
 * @date 2014-9-25
 */
@Service(ApiServiceName.TASK_OPERATE_PROCESS_POST)
@Transactional
public class TaskOperateProcessPostServiceImpl extends BaseServiceImpl implements WellptService<TaskOperateProcessPostRequest> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskOperateProcessPostRequest postRequest) {
        TaskOperateProcessPostResponse response = new TaskOperateProcessPostResponse();
        return response;
    }

}
