/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.internal.suport.FormDataUtils;
import com.wellsoft.pt.api.request.TaskSubmitRequest;
import com.wellsoft.pt.api.response.TaskSubmitResponse;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2014-8-12.1	zhulh		2014-8-12		Create
 * </pre>
 * @date 2014-8-12
 */
@Service(ApiServiceName.TASK_SUBMIT)
@Transactional
public class TaskSubmitServiceImpl extends BaseServiceImpl implements WellptService<TaskSubmitRequest> {

    @Autowired
    private com.wellsoft.pt.bpm.engine.service.TaskService taskService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskSubmitRequest submitRequest) {
        // 任务实例UUID
        String taskInstUuid = submitRequest.getUuid();
        // 目标环节ID
        String toTaskId = submitRequest.getToTaskId();
        // 目标环节办理人
        Map<String, List<String>> taskUsers = submitRequest.getTaskUsers();
        // 目标环节抄送人
        Map<String, List<String>> taskCopyUsers = submitRequest.getTaskCopyUsers();
        // 目标环节督办人
        Map<String, List<String>> taskMonitorUsers = submitRequest.getTaskMonitorUsers();

        // 办理意见立场
        String opinionName = submitRequest.getOpinionName();
        // 办理意见立场
        String opinionValue = submitRequest.getOpinionValue();
        // 办理意见内容
        String opinionText = submitRequest.getOpinionText();

        TaskInstance taskInstance = taskService.getTask(taskInstUuid);
        String formUuid = taskInstance.getFormUuid();
        String dataUuid = taskInstance.getDataUuid();
        String fromTaskId = taskInstance.getId();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        String key = taskInstUuid + userId;
        TaskData taskData = new TaskData();
        taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.SUBMIT));
        taskData.setActionType(key, WorkFlowOperation.SUBMIT);
        taskData.setOpinionLabel(key, opinionName);
        taskData.setOpinionValue(key, opinionValue);
        taskData.setOpinionText(key, opinionText);
        taskData.setToTaskId(fromTaskId, toTaskId);
        if (MapUtils.isNotEmpty(taskUsers)) {
            taskData.setTaskUsers(taskUsers);
        }
        if (MapUtils.isNotEmpty(taskCopyUsers)) {
            taskData.addTaskCopyUsers(taskCopyUsers);
        }
        if (MapUtils.isNotEmpty(taskMonitorUsers)) {
            taskData.addTaskMonitors(taskMonitorUsers);
        }
        // if (taskUsers != null) {
        // for (String taskId : taskUsers.keySet()) {
        // taskData.setSpecifyTaskUser(taskId, true);
        // }
        // }
        taskData.setUserId(userId);
        taskData.setUserName(userName);

        DyFormData dyFormData = FormDataUtils.merge2DyformData(submitRequest.getFormData(), formUuid, dataUuid);
        dataUuid = dyFormFacade.saveFormData(dyFormData);
        taskData.setFormUuid(formUuid);
        taskData.setDataUuid(dataUuid);
        taskData.setDyFormData(dataUuid, dyFormData);

        taskData.setDaemon(true);
        // 接口调用
        // taskData.setApiInvoke(true);
        taskService.submit(taskInstUuid, taskData);

        TaskSubmitResponse response = new TaskSubmitResponse();
        response.setData(taskData.getSubmitResult());
        return response;
    }
}
