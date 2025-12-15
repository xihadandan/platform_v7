/*
 * @(#)2014-10-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegation.service.TaskDelegationTakeBackService;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.TodoSubmitTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
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
 * 2014-10-2.1	zhulh		2014-10-2		Create
 * </pre>
 * @date 2014-10-2
 */
@Service
@Transactional
public class TodoSubmitTaskActionExecutorImpl extends TaskActionExecutor implements TodoSubmitTaskActionExecutor {

    @Autowired
    private TaskDelegationTakeBackService taskDelegationTakeBackService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.executor.TaskExecutor#execute(com.wellsoft.pt.bpm.engine.executor.Param)
     */
    @Override
    public void execute(Param param) {
        TaskInstance taskInstance = param.getTaskInstance();
        TaskData taskData = param.getTaskData();
        String userId = taskData.getUserId();
        String taskInstUuid = taskInstance.getUuid();
        Integer transferCode = taskData.getTransferCode(taskInstUuid);
        if (transferCode == null) {
            // 设置流转代码为提交
            taskData.setTransferCode(taskInstUuid, TransferCode.Submit.getCode());
            // 设置操作代码为提交
            taskData.setActionCode(taskInstUuid, ActionCode.SUBMIT.getCode());
        }

        //任务提交，处理委托出去的任务
        taskDelegationTakeBackService.taskSubmitToTakeBack(taskInstUuid, userId);

        Token token = new Token(taskInstance, taskData);
        /*modified by huanglinchuan 2014.10.27 begin*/
        // 记录操作
        if (param.isLog()) {
            TaskIdentity taskIdentity = param.getTaskIdentity();
            String identityJson = null;
            String taskIdentityUuid = null;
            if (taskIdentity != null) {
                identityJson = JsonUtils.object2Json(taskIdentity);
                taskIdentityUuid = taskIdentity.getUuid();
            }
            FlowInstance flowInstance = taskInstance.getFlowInstance();
            Integer actionCode = taskData.getActionCode(taskInstUuid);
            String taskOperationUuid = null;
            if (actionCode == null) {
                taskOperationUuid = taskOperationService.saveTaskOperation(
                        WorkFlowOperation.getName(WorkFlowOperation.SUBMIT), ActionCode.SUBMIT.getCode(),
                        WorkFlowOperation.SUBMIT, null, null, null, userId, null, null, taskIdentityUuid, identityJson,
                        taskInstance, flowInstance, taskData);
            } else {
                taskOperationUuid = taskOperationService.saveTaskOperation(null, null, null, null, null, null, userId,
                        null, null, taskIdentityUuid, identityJson, taskInstance, flowInstance, taskData);
            }
            taskData.setOperationUuid(taskInstUuid, taskOperationUuid);
        }
        /*modified by huanglinchuan 2014.10.27 begin*/

        // 正常提交
        token.signal();

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());
    }

}
