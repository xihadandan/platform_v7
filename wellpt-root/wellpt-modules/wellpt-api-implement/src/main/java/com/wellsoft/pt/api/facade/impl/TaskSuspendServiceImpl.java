/*
 * @(#)2014-8-17 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskSuspendRequest;
import com.wellsoft.pt.api.response.TaskSuspendResponse;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-17.1	zhulh		2014-8-17		Create
 * </pre>
 * @date 2014-8-17
 */
@Service(ApiServiceName.TASK_SUSPEND)
public class TaskSuspendServiceImpl implements WellptService<TaskSuspendRequest> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskSuspendRequest taskSuspendRequest) {
        String taskInstUuid = taskSuspendRequest.getUuid();
        taskService.suspend(taskInstUuid);
        TaskSuspendResponse response = new TaskSuspendResponse();
        return response;
    }

}
