/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.GzFlowInstanceStartRequest;
import com.wellsoft.pt.api.request.GzTaskCopyToRequest;
import com.wellsoft.pt.api.response.GzFlowInstanceStartResponse;
import com.wellsoft.pt.api.response.GzTaskCopyToResponse;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.support.WorkFlowAclServiceWrapper;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
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
@Service(ApiServiceName.GZ_TASK_COPY_TO)
@Transactional
public class GzTaskCopyToServiceImpl extends BaseServiceImpl implements WellptService<GzTaskCopyToRequest> {

    @Autowired
    private com.wellsoft.pt.bpm.engine.service.TaskService taskService;

    @Autowired
    private WorkFlowAclServiceWrapper workFlowAclServiceWrapper;

    @Autowired
    private OrgApiFacade orgApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public synchronized WellptResponse doService(GzTaskCopyToRequest copyToRequest) {
        String flowInstUuid = copyToRequest.getFlowInstUuid();
        String userId = SpringSecurityUtils.getCurrentUserId();

        GzTaskCopyToResponse response = new GzTaskCopyToResponse();
        // 添加抄送权限
        if (StringUtils.isNotBlank(flowInstUuid)) {
            List<TaskInstance> taskInstances = taskService.getUnfinishedTasks(flowInstUuid);
            for (TaskInstance taskInstance : taskInstances) {
                String taskInstUuid = taskInstance.getUuid();
                taskService.addUnreadPermission(userId, taskInstUuid);
            }
            QueryItem data = new QueryItem();
            data.put("flowInstUuid", flowInstUuid);
            response.setData(data);
        } else {
            GzFlowInstanceStartServiceImpl service = ApplicationContextHolder
                    .getBean(GzFlowInstanceStartServiceImpl.class);
            GzFlowInstanceStartRequest startRequest = new GzFlowInstanceStartRequest();
            BeanUtils.copyProperties(copyToRequest, startRequest);
            GzFlowInstanceStartResponse startResponse = (GzFlowInstanceStartResponse) service.doService(startRequest);
            response.setData(startResponse.getData());
            flowInstUuid = ((QueryItem) startResponse.getData()).getString("flowInstUuid");
            List<TaskInstance> taskInstances = taskService.getUnfinishedTasks(flowInstUuid);
            for (TaskInstance taskInstance : taskInstances) {
                String taskInstUuid = taskInstance.getUuid();
                taskService.removeTodoPermission(userId, taskInstUuid);
                taskService.addUnreadPermission(userId, taskInstUuid);
            }
        }

        return response;
    }
}
