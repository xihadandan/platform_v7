/*
 * @(#)2019年8月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskAttentionRequest;
import com.wellsoft.pt.api.response.TaskAttentionResponse;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
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
 * 2019年8月13日.1	zhulh		2019年8月13日		Create
 * </pre>
 * @date 2019年8月13日
 */
@Service(ApiServiceName.TASK_ATTENTION)
public class TaskAttentionServiceImpl extends BaseServiceImpl implements WellptService<TaskAttentionRequest> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskAttentionRequest taskCopyToRequest) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String taskInstUuid = taskCopyToRequest.getUuid();
        taskService.attention(userId, taskInstUuid);
        return new TaskAttentionResponse();
    }

}
