/*
 * @(#)2014-10-16 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlow;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.exception.GotoTaskNotFoundException;
import com.wellsoft.pt.bpm.engine.executor.*;
import com.wellsoft.pt.bpm.engine.executor.param.GotoTaskParam;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.util.ReservedFieldUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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
 * 2014-10-16.1	zhulh		2014-10-16		Create
 * </pre>
 * @date 2014-10-16
 */
@Service
@Transactional
public class GotoTaskTaskActionExecutorImpl extends TaskActionExecutor implements GotoTaskTaskActionExecutor {

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskSubFlowService taskSubFlowService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.executor.TaskExecutor#execute(com.wellsoft.pt.bpm.engine.executor.Param)
     */
    @Override
    public void execute(Param param) {
        GotoTaskParam gotoTaskParam = (GotoTaskParam) param;
        TaskInstance taskInstance = gotoTaskParam.getTaskInstance();
        TaskData taskData = gotoTaskParam.getTaskData();
        String fromTaskId = taskInstance.getId();
        String taskInstUuid = taskInstance.getUuid();
        String userId = taskData.getUserId();
        String key = taskInstUuid + userId;
        // 流转代码
        if (taskData.getTransferCode(taskInstUuid) == null) {
            taskData.setTransferCode(taskInstUuid, TransferCode.GotoTask.getCode());
        }
        // 是否允许跳转结束的流程
//		Boolean allowGotoOverTask = (Boolean) taskData.get("allowGotoOverTask");
        // 办结流程可跳转
//		if (!Boolean.TRUE.equals(allowGotoOverTask) && taskInstance.getEndTime() != null) {
//			List<AclSid> getAclSid = aclService.getSid(TaskInstance.class, taskInstUuid, AclPermission.TODO);
//			if (getAclSid.isEmpty()) {
//				throw new WorkFlowException("工作已结束或已被处理，无法进行操作!");
//			}
//		}

        String gotoTaskId = gotoTaskParam.getGotoTaskId();
        if (StringUtils.isBlank(gotoTaskId)) {
            Map<String, Object> gotoTasks = taskService.getToTasks(taskInstUuid);
            throw new GotoTaskNotFoundException(gotoTasks);
        }
        taskData.setToTaskId(fromTaskId, gotoTaskId);
        // 表单定义UUID
        String formUuid = taskInstance.getFormUuid();
        // 表单数据UUID
        String dataUuid = taskInstance.getDataUuid();
        taskData.setFormUuid(formUuid);
        taskData.setDataUuid(dataUuid);
        if (StringUtils.isNotBlank(formUuid) && StringUtils.isNotBlank(dataUuid)) {
            DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            taskData.setDyFormData(dataUuid, dyFormData);
            ReservedFieldUtils.setReservedFields(dyFormData, taskData);
        }

        // 记录待办记录本身
        List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuid(taskInstUuid);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (gotoTaskParam.isRequiredGotoTaskPermission()) {
            for (TaskIdentity taskIdentity : taskIdentities) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("uuid", taskIdentity.getUuid());
                map.put("suspensionState", taskIdentity.getSuspensionState());
                map.put("gotoTaskId", gotoTaskId);
                list.add(map);
            }
        }
        String identityJson = JsonUtils.object2Json(list);

        // 1、删除环节办理人及对应的待办权限
        identityService.removeTodoByTaskInstUuid(taskInstUuid);
        // 送结束，更新任务办理人信息
        if (FlowDelegate.END_FLOW_ID.equals(gotoTaskId)) {
            identityService.updateTaskIdentity(taskInstance);
        }
        String action = StringUtils.isBlank(taskData.getAction(key)) ? WorkFlowOperation
                .getName(WorkFlowOperation.GOTO_TASK) : taskData.getAction(key);
        Integer actionCode = taskData.getActionCode(taskInstUuid) == null ? ActionCode.GOTO_TASK.getCode() : taskData
                .getActionCode(taskInstUuid);
        String actionType = StringUtils.isBlank(taskData.getActionType(key)) ? WorkFlowOperation.GOTO_TASK : taskData
                .getActionType(key);
        List<String> actionObjects = (List<String>) taskData.get(key + "_actionObjects");
        // add by wujx 2016-01-16 begin
        if (WorkFlowOperation.GOTO_TASK.equals(actionType)) {
            taskData.setGotoTask(fromTaskId, true);
        }
        // add by wujx 2016-01-16 begin
        if (gotoTaskParam.isLog()) {
            String operationUuid = taskOperationService.saveTaskOperation(action, actionCode, actionType, null, null,
                    null, userId, actionObjects, null, null, identityJson, taskInstance,
                    taskInstance.getFlowInstance(), taskData);
            taskData.setOperationUuid(taskInstance.getUuid(), operationUuid);
        }

        // 2、提交任务
        Param sourceParam = new Param(taskInstance, taskData, null, false);
        TaskExecutor taskExecutor = TaskExecutorFactory.getTaskActionExecutor(ActionCode.SUBMIT);
        taskExecutor.execute(sourceParam);

        // 办结流程跳转，激活流程实例
        if (taskInstance.getEndTime() != null) {
            FlowInstance flowInstance = taskInstance.getFlowInstance();
            if (flowInstance.getEndTime() != null &&
                    CollectionUtils.isNotEmpty(taskService.getUnfinishedTasks(flowInstance.getUuid()))) {
                flowInstance.setEndTime(null);
                flowInstance.setIsActive(true);
                flowService.saveFlowInstance(flowInstance);

                // 恢复子流程状态
                List<TaskSubFlow> alltaskSubFlows = taskSubFlowService.getByFlowInstUuid(flowInstance.getUuid());
                for (TaskSubFlow taskSubFlow : alltaskSubFlows) {
                    taskSubFlow.setCompleted(false);
                    taskSubFlow.setCompletionState(TaskSubFlow.STATUS_NORMAL);
                    taskSubFlowService.save(taskSubFlow);
                }
            }
        }

        // 创建流程数据快照
        createFlowInstanceSnapshot(taskData, taskInstance, taskInstance.getFlowInstance());
    }
}
