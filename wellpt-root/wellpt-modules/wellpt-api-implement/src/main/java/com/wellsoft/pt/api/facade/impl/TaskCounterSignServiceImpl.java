/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskCounterSignRequest;
import com.wellsoft.pt.api.response.TaskCounterSignResponse;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service(ApiServiceName.TASK_COUNTER_SIGN)
@Transactional
public class TaskCounterSignServiceImpl extends BaseServiceImpl implements WellptService<TaskCounterSignRequest> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskCounterSignRequest counterSignRequest) {
        String taskInstUuid = counterSignRequest.getUuid();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        List<String> todoUsers = counterSignRequest.getTaskUsers();
        String opinionName = counterSignRequest.getOpinionName();
        String opinionValue = counterSignRequest.getOpinionValue();
        String opinionText = counterSignRequest.getOpinionText();
        TaskData taskData = new TaskData();
        taskData.setUserId(userId);
        taskData.setUserName(userName);
        taskService.counterSign(userId, taskInstUuid, todoUsers, opinionName, opinionValue, opinionText, Collections.emptyList(),
                taskData);

        TaskCounterSignResponse response = new TaskCounterSignResponse();
        return response;
    }

}
