/*
 * @(#)2019年8月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskGotoTaskRequest;
import com.wellsoft.pt.api.response.TaskGotoTaskResponse;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
@Service(ApiServiceName.TASK_GOTO_TASK)
public class TaskGotoTaskServiceImpl implements WellptService<TaskGotoTaskRequest> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskGotoTaskRequest taskGotoTaskRequest) {
        // 任务实例UUID
        String taskInstUuid = taskGotoTaskRequest.getUuid();
        // 跳转环节ID
        String gotoTaskId = taskGotoTaskRequest.getGotoTaskId();
        // 跳转环节办理人
        Map<String, List<String>> taskUsers = taskGotoTaskRequest.getTaskUsers();
        Map<String, List<String>> copyUsers = taskGotoTaskRequest.getTaskCopyUsers();
        Map<String, List<String>> monitorUsers = taskGotoTaskRequest.getTaskMonitorUsers();

        // 办理意见名称
        String opinionName = taskGotoTaskRequest.getOpinionName();
        // 办理意见立场
        String opinionValue = taskGotoTaskRequest.getOpinionValue();
        // 办理意见内容
        String opinionText = taskGotoTaskRequest.getOpinionText();

        TaskInstance taskInstance = taskService.getTask(taskInstUuid);
        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        String key = taskInstUuid + userId;
        TaskData taskData = new TaskData();
        taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.GOTO_TASK));
        taskData.setActionType(key, WorkFlowOperation.GOTO_TASK);
        taskData.setOpinionLabel(key, opinionName);
        taskData.setOpinionValue(key, opinionValue);
        taskData.setOpinionText(key, opinionText);
        taskData.setGotoTask(taskInstance.getId(), true);
        if (MapUtils.isNotEmpty(taskUsers)) {
            taskData.setTaskUsers(taskUsers);
        }
        if (MapUtils.isNotEmpty(copyUsers)) {
            taskData.addTaskCopyUsers(copyUsers);
        }
        if (MapUtils.isNotEmpty(monitorUsers)) {
            taskData.addTaskMonitors(monitorUsers);
        }
        taskData.setUserId(userId);
        taskData.setUserName(userName);
        taskService.gotoTask(taskInstUuid, gotoTaskId, taskData);
        return new TaskGotoTaskResponse();
    }

}
