/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.domain.Task;
import com.wellsoft.pt.api.facade.AbstractWellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskGetRequest;
import com.wellsoft.pt.api.response.TaskGetResponse;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
@Service(ApiServiceName.TASK_GET)
@Transactional(readOnly = true)
public class TaskGetServiceImpl extends AbstractWellptService<TaskGetRequest> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskGetRequest taskGetRequest) {
        String taskInstUuid = taskGetRequest.getUuid();
        Task task = getTask(taskInstUuid);

        TaskGetResponse response = new TaskGetResponse();
        response.setData(task);
        return response;
    }

    /**
     * @param taskInstUuid
     * @return
     */
    protected Task getTask(String taskInstUuid) {
        TaskInstance taskInstance = taskService.getTask(taskInstUuid);
        FlowInstance flowInstance = taskInstance.getFlowInstance();

        // 任务UUID
        String uuid = taskInstance.getUuid();
        // 任务id
        String id = taskInstance.getId();
        // 任务名称
        String name = taskInstance.getName();
        // 任务开始时间
        Date startTime = taskInstance.getStartTime();
        // 任务所在流程实例UUID
        String flowInstUuid = flowInstance.getUuid();
        // 任务标题
        String title = flowInstance.getTitle();
        // 前办理人名称
        String preOperatorId = taskInstance.getAssignee();
        String preOperatorName = preOperatorId;
        if (StringUtils.isNotBlank(preOperatorId)) {
            preOperatorName = IdentityResolverStrategy.resolveAsName(preOperatorId);
        }
        // 当前办理人
        String todoUserName = taskInstance.getTodoUserName();
        // 表单定义UUId
        String formUuid = taskInstance.getFormUuid();
        // 表单数据UUID
        String dataUuid = taskInstance.getDataUuid();

        Task task = new Task();
        task.setUuid(uuid);
        task.setId(id);
        task.setName(name);
        task.setStartTime(startTime);
        task.setFlowInstUuid(flowInstUuid);
        task.setTitle(title);
        task.setPreOperatorName(preOperatorName);
        task.setTodoUserName(todoUserName);
        task.setFormUuid(formUuid);
        task.setDataUuid(dataUuid);
        return task;
    }

}
