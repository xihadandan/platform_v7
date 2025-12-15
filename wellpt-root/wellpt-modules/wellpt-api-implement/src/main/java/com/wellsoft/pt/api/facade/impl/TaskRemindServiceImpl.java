/*
 * @(#)2019年8月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskRemindRequest;
import com.wellsoft.pt.api.response.TaskRemindResponse;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月13日.1	zhulh		2019年8月13日		Create
 * </pre>
 * @date 2019年8月13日
 */
@Service(ApiServiceName.TASK_REMIND)
public class TaskRemindServiceImpl implements WellptService<TaskRemindRequest> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskRemindRequest taskRemindRequest) {
        // 任务实例UUID
        String taskInstUuid = taskRemindRequest.getUuid();
        // 办理意见名称
        String opinionName = taskRemindRequest.getOpinionName();
        // 办理意见立场
        String opinionValue = taskRemindRequest.getOpinionValue();
        // 办理意见内容
        String opinionText = taskRemindRequest.getOpinionText();
        taskService.remind(taskInstUuid, opinionName, opinionValue, opinionText, Collections.emptyList());
        return new TaskRemindResponse();
    }

}
