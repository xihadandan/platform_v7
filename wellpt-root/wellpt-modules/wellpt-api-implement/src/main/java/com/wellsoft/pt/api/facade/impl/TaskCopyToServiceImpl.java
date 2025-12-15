/*
 * @(#)2019年8月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskCopyToRequest;
import com.wellsoft.pt.api.response.TaskCopyToResponse;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowAclRole;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service(ApiServiceName.TASK_COPY_TO)
public class TaskCopyToServiceImpl implements WellptService<TaskCopyToRequest> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskCopyToRequest taskCopyToRequest) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String taskInstUuid = taskCopyToRequest.getUuid();
        List<String> copyToUserIds = taskCopyToRequest.getTaskUsers();
        taskService.copyTo(userId, taskInstUuid, copyToUserIds, WorkFlowAclRole.TODO);
        return new TaskCopyToResponse();
    }

}
