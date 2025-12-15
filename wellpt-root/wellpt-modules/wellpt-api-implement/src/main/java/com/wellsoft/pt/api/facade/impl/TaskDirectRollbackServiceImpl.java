/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskDirectRollbackRequest;
import com.wellsoft.pt.api.response.TaskRollbackResponse;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
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
 * 2014-8-12.1	zhulh		2014-8-12		Create
 * </pre>
 * @date 2014-8-12
 */
@Service(ApiServiceName.TASK_DIRECT_ROLL_BACK)
public class TaskDirectRollbackServiceImpl implements WellptService<TaskDirectRollbackRequest> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskDirectRollbackRequest directRollbackRequest) {
        // 办理意见名称
        String opinionName = directRollbackRequest.getOpinionName();
        // 办理意见立场
        String opinionValue = directRollbackRequest.getOpinionValue();
        // 办理意见内容
        String opinionText = directRollbackRequest.getOpinionText();

        String taskInstUuid = directRollbackRequest.getUuid();
        TaskInstance taskInstance = taskService.getTask(taskInstUuid);

        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        String key = taskInstUuid + userId;
        // 设置任务数据
        TaskData taskData = new TaskData();
        taskData.setOpinionLabel(key, opinionName);
        taskData.setOpinionValue(key, opinionValue);
        taskData.setOpinionText(key, opinionText);
        // 当前用户Id
        taskData.setUserId(userId);
        // 当前用户名
        taskData.setUserName(userName);
        // 表单定义UUID
        taskData.setFormUuid(taskInstance.getFormUuid());
        // 表单数据UUID
        taskData.setDataUuid(taskInstance.getDataUuid());

        // 是否退回到前环节
        taskData.setRollbackToPreTask(taskInstUuid, true);

        taskService.rollback(taskInstUuid, taskData);

        TaskRollbackResponse response = new TaskRollbackResponse();
        return response;
    }

}
