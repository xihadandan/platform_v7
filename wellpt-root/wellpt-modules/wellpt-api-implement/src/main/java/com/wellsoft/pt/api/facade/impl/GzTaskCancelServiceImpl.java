/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.GzTaskCancelRequest;
import com.wellsoft.pt.api.response.GzTaskCancelResponse;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.IdentityService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowAclServiceWrapper;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service(ApiServiceName.GZ_TASK_CANCEL)
@Transactional
public class GzTaskCancelServiceImpl extends BaseServiceImpl implements WellptService<GzTaskCancelRequest> {

    @Autowired
    private com.wellsoft.pt.bpm.engine.service.TaskService taskService;

    @Autowired
    private WorkFlowAclServiceWrapper workFlowAclServiceWrapper;

    @Autowired
    private IdentityService identityService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public synchronized WellptResponse doService(GzTaskCancelRequest cancelRequest) {
        String flowInstUuid = cancelRequest.getFlowInstUuid();
        List<TaskInstance> taskInstances = taskService.getUnfinishedTasks(flowInstUuid);

        String userId = SpringSecurityUtils.getCurrentUserId();
        for (TaskInstance taskInstance : taskInstances) {
            String taskInstUuid = taskInstance.getUuid();
            identityService.removeTodoByTaskInstUuidAndUserId(taskInstUuid, userId);
        }

        GzTaskCancelResponse response = new GzTaskCancelResponse();
        return response;
    }

}
