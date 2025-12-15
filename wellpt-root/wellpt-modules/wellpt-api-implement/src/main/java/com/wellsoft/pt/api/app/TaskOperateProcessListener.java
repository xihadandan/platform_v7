/*
 * @(#)2014-9-23 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.app;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.exception.WorkFlowException;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.TaskOperateProcessPostRequest;
import com.wellsoft.pt.api.response.TaskOperateProcessPostResponse;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.impl.TaskListenerAdapter;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.service.TaskActivityService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-23.1	Asus		2014-9-23		Create
 * </pre>
 * @date 2014-9-23
 */
@Component
public class TaskOperateProcessListener extends TaskListenerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private TaskActivityService taskActivityService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TaskListenerAdapter#getName()
     */
    @Override
    public String getName() {
        return "流程办理过程信息推送Restful Webservice";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TaskListenerAdapter#onCreated(com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void onCreated(Event event) throws WorkFlowException {
        // 流程实例UUID
        String flowInstUuid = event.getFlowInstUuid();
        // 环节ID
        String taskId = event.getTaskId();
        // 操作类型
        String actionType = event.getActionType();
        if (!(WorkFlowOperation.ROLLBACK.equals(actionType) || WorkFlowOperation.DIRECT_ROLLBACK.equals(actionType)
                || WorkFlowOperation.CANCEL.equals(actionType) || WorkFlowOperation.TRANSFER_SUBMIT.equals(actionType) || WorkFlowOperation.GOTO_TASK
                .equals(actionType))) {
            return;
        }
        // 获取当前流程的任务活动数，如果小于2则忽略掉
        Long activityCount = taskActivityService.countByFlowInstUuid(flowInstUuid);

        if (activityCount == null || activityCount < 2) {
            return;
        }
        // 如果不是流程的第一个结点则忽略掉
        boolean isStartedTaskInstance = taskActivityService.isStartedTaskInstance(flowInstUuid, taskId);
        if (!isStartedTaskInstance) {
            return;
        }
        // 2退回审核
        pushWebserviceData(event, true, false);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TaskListenerAdapter#onCompleted(com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void onCompleted(Event event) throws WorkFlowException {
        if (StringUtils.isBlank(event.getActionType())) {
            return;
        }
        if (!WorkFlowOperation.SUBMIT.equals(event.getActionType())) {
            return;
        }
        // 流程实例UUID
        String flowInstUuid = event.getFlowInstUuid();
        // 环节ID
        String taskId = event.getTaskId();
        // 如果是第一个环节完成任务则忽略掉
        if (taskActivityService.isStartedTaskInstance(flowInstUuid, taskId)) {
            // 1 提交审核
            pushWebserviceData(event, false, true);
        } else {
            // 3 审核通过
            pushWebserviceData(event, false, false);
        }

    }

    /**
     * 如何描述该方法
     *
     * @param event
     */
    private void pushWebserviceData(Event event, boolean isReturnToStartedTask, boolean completedSubmitStartedTask) {
        String baseAddress = "http://pm-test.leedarson.com/index/index/lcpservice";
        baseAddress = Config.getValue("crm.lcp.webservice.address", baseAddress);

        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        String loginName = SpringSecurityUtils.getCurrentLoginName();
        String password = "0";
        WellptClient wellptClient = new DefaultWellptClient(baseAddress, tenantId, loginName, password, true);
        wellptClient.addRequestParam("systemId", "LCP");

        TaskOperateProcessPostRequest request = getPostData(event, isReturnToStartedTask);
        request.setCompletedSubmitStartedTask(completedSubmitStartedTask);
        TaskOperateProcessPostResponse response = wellptClient.execute(request);

        if (response == null || !"0".equals(response.getCode()) || !response.isSuccess()) {
            logger.error(JsonUtils.object2Json(response));

            throw new WorkFlowException("接口[" + baseAddress + "]调用异常，返回信息：" + wellptClient.getResponseBody());
        }
    }

    /**
     * 如何描述该方法
     *
     * @param event
     * @return
     */
    private TaskOperateProcessPostRequest getPostData(Event event, boolean isReturnToStartedTask) {
        // 流程实例UUID
        String flowInstUuid = event.getFlowInstUuid();
        // 流程定义ID
        String flowDefinitonId = event.getFlowId();
        // 流程名称
        String flowName = event.getFlowName();
        // 环节实例UUID
        String taskInstUuid = event.getTaskInstUuid();
        // 环节ID
        String taskId = event.getTaskId();
        // 环节名称
        String taskName = event.getTaskName();
        // 办理人ID
        String operatorId = SpringSecurityUtils.getCurrentUserId();
        // 办理人名称
        String operatorName = SpringSecurityUtils.getCurrentUserName();
        // 操作动作名称
        String action = event.getAction();
        // 操作动作类型
        String actionType = event.getActionType();
        // 退回到的环节ID
        String rollbackToTaskId = null;
        // 是否退回到流程实例的开始环节
        Boolean rollbackToStartedTask = Boolean.FALSE;
        if (WorkFlowOperation.ROLLBACK.equals(actionType) || WorkFlowOperation.DIRECT_ROLLBACK.equals(actionType)) {
            rollbackToTaskId = event.getTaskData().getRollbackToTaskId(taskInstUuid);
            if (StringUtils.isNotBlank(rollbackToTaskId)) {
                rollbackToStartedTask = taskActivityService.isStartedTaskInstance(flowInstUuid, rollbackToTaskId);
            } else {
                rollbackToStartedTask = false;
            }
        }
        // 是否返回到流程实例的开始环节
        Boolean returnToStartedTask = isReturnToStartedTask;
        // 办理时间
        Date operateTime = Calendar.getInstance().getTime();
        // 办理意见立场名称
        String opinionName = event.getTaskOpinionName();
        // 办理意见立场值
        String opinionValue = event.getTaskOpinionValue();
        // 办理意见内容
        String opinionText = event.getTaskOpinionText();
        // 下一办理环节ID，多个以;隔开
        String nextTaskId = event.getNextTaskId();

        // 表单数据
        Map<String, Object> formData = getFormData(event);

        TaskOperateProcessPostRequest request = new TaskOperateProcessPostRequest();
        request.setFlowInstUuid(flowInstUuid);
        request.setFlowDefinitonId(flowDefinitonId);
        request.setFlowName(flowName);
        request.setTaskInstUuid(taskInstUuid);
        request.setTaskId(taskId);
        request.setTaskName(taskName);
        request.setOperatorId(operatorId);
        request.setOperatorName(operatorName);
        request.setAction(action);
        request.setActionType(actionType);
        request.setRollbackToTaskId(rollbackToTaskId);
        request.setRollbackToStartedTask(rollbackToStartedTask);
        request.setReturnToStartedTask(returnToStartedTask);
        request.setOperateTime(operateTime);
        request.setOpinionName(opinionName);
        request.setOpinionValue(opinionValue);
        request.setOpinionText(opinionText);
        request.setFlowCompleted(FlowDelegate.END_FLOW_ID.equals(nextTaskId));

        request.setFormData(formData);

        return request;
    }

    private Map<String, Object> getFormData(Event event) {
        DyFormData dyFormData = event.getDyFormData();
        if (dyFormData == null) {
            String formUuid = event.getFormUuid();
            String dataUuid = event.getDataUuid();
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        }

        Map<String, Object> formData = new HashMap<String, Object>();
        // 主表数据
        Map<String, Object> mainform = dyFormData.getFormDataOfMainform();
        for (String key : mainform.keySet()) {
            formData.put(key, mainform.get(key));
        }

        // 从表数据
        // TODO

        return formData;
    }

}
