/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskTransferRequest;
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
 * 2014-8-12.1	zhulh		2014-8-12		Create
 * </pre>
 * @date 2014-8-12
 */
@Service(ApiServiceName.TASK_TRANSFER)
public class TaskTransferServiceImpl implements WellptService<TaskTransferRequest> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskTransferRequest taskTransferRequest) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        String taskInstUuid = taskTransferRequest.getUuid();
        List<String> userIds = taskTransferRequest.getTaskUsers();
        String opinionName = taskTransferRequest.getOpinionName();
        String opinionValue = taskTransferRequest.getOpinionValue();
        String opinionText = taskTransferRequest.getOpinionText();
        TaskData taskData = new TaskData();
        taskData.setUserId(userId);
        taskData.setUserName(userName);
        taskService.transfer(userId, taskInstUuid, userIds, opinionName, opinionValue, opinionText, Collections.emptyList(), taskData);
        TaskTransferResponse response = new TaskTransferResponse();
        return response;
    }

}
