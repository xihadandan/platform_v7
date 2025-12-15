/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.GzTaskDeleteRequest;
import com.wellsoft.pt.api.response.GzTaskDeleteResponse;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.support.WorkFlowAclServiceWrapper;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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
@Service(ApiServiceName.GZ_TASK_DELETE)
@Transactional
public class GzTaskDeleteServiceImpl extends BaseServiceImpl implements WellptService<GzTaskDeleteRequest> {

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
    public synchronized WellptResponse doService(GzTaskDeleteRequest deleteRequest) {
        String flowInstUuid = deleteRequest.getFlowInstUuid();
        List<TaskInstance> taskInstances = taskService.getUnfinishedTasks(flowInstUuid);

        List<String> adminIds = orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        if (!adminIds.isEmpty()) {
            try {
                IgnoreLoginUtils.login(tenantId, adminIds.get(0));
                for (TaskInstance taskInstance : taskInstances) {
                    // 管理员删除
                    String taskInstUuid = taskInstance.getUuid();
                    taskService.deleteByAdmin(adminIds.get(0), taskInstUuid);
                }
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            } finally {
                IgnoreLoginUtils.logout();
            }
        } else {
            String userId = SpringSecurityUtils.getCurrentUserId();
            for (TaskInstance taskInstance : taskInstances) {
                // 添加待办权限
                String taskInstUuid = taskInstance.getUuid();
                taskService.addTodoPermission(userId, taskInstUuid);

                // 删除
                taskService.delete(userId, taskInstUuid);
            }
        }

        GzTaskDeleteResponse response = new GzTaskDeleteResponse();
        return response;
    }

}
