/*
 * @(#)2015年7月24日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.facade.impl;

import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.workflow.gz.facade.GzWorkDataSyncAspectService;
import com.wellsoft.pt.workflow.gz.support.WfGzDataConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年7月24日.1	zhulh		2015年7月24日		Create
 * </pre>
 * @date 2015年7月24日
 */
@Service
@Transactional(readOnly = true)
public class GzWorkDataSyncAspectServiceImpl extends BaseServiceImpl implements GzWorkDataSyncAspectService {

    @Autowired
    private TaskService taskService;

    /**
     * @param taskInstance
     */
    @Override
    public void checked(String taskInstUuid) {
        if (StringUtils.isBlank(taskInstUuid)) {
            return;
        }
        TaskInstance taskInstance = taskService.getTask(taskInstUuid);
        if (taskInstance == null) {
            return;
        }
        String flowDefId = taskInstance.getFlowInstance().getId();
        if (WfGzDataConstant.FLOW_DEF_ID.equals(flowDefId)) {
            throw new WorkFlowException("该工作为您另一岗位的工作，您无权限在现登录的岗位进行操作!");
        }
    }
}
