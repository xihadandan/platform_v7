/*
 * @(#)2019年8月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskHandOverRequest;
import com.wellsoft.pt.api.response.TaskTransferResponse;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
@Service(ApiServiceName.TASK_HAND_OVER)
public class TaskHandOverServiceImpl implements WellptService<TaskHandOverRequest> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskHandOverRequest taskHandOverRequest) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        String taskInstUuid = taskHandOverRequest.getUuid();
        List<String> userIds = taskHandOverRequest.getTaskUsers();
        String opinionName = taskHandOverRequest.getOpinionName();
        String opinionValue = taskHandOverRequest.getOpinionValue();
        String opinionText = taskHandOverRequest.getOpinionText();
        TaskData taskData = new TaskData();
        taskData.setUserId(userId);
        taskData.setUserName(userName);
        taskService.handOver(userId, taskInstUuid, userIds, opinionName, opinionValue, opinionText, Collections.emptyList(), true);
        return new TaskTransferResponse();
    }

}
