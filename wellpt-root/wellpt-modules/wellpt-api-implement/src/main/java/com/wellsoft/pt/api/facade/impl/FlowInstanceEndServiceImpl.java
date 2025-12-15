/*
 * @(#)2014-8-17 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.FlowInstanceEndRequest;
import com.wellsoft.pt.api.response.FlowInstanceEndResponse;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
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
 * 2014-8-17.1	zhulh		2014-8-17		Create
 * </pre>
 * @date 2014-8-17
 */
@Service(ApiServiceName.FLOW_INSTANCE_END)
public class FlowInstanceEndServiceImpl implements WellptService<FlowInstanceEndRequest> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FlowInstanceEndRequest endRequest) {
        String flowInstUuid = endRequest.getUuid();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        TaskData taskData = new TaskData();
        taskData.setUserId(userId);
        taskData.setUserName(userName);
        List<TaskInstance> taskInstances = taskService.getUnfinishedTasks(flowInstUuid);
        if (CollectionUtils.isEmpty(taskInstances)) {
            throw new RuntimeException("流程没有未办结环节实例，不能送结束！");
        }
        for (TaskInstance taskInstance : taskInstances) {
            String key = taskInstance.getUuid() + userId;
            taskData.setAction(key, "送结束");
            taskService.gotoTask(taskInstance.getUuid(), FlowDelegate.END_FLOW_ID, taskData);
        }
        FlowInstanceEndResponse response = new FlowInstanceEndResponse();
        return response;
    }

}
