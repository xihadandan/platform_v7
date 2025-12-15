/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.internal.suport.FormDataUtils;
import com.wellsoft.pt.api.request.FlowInstanceStartRequest;
import com.wellsoft.pt.api.response.FlowInstanceStartResponse;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskOperationService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;
import com.wellsoft.pt.bpm.engine.support.SubmitResult;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
 * 2014-8-11.1	zhulh		2014-8-11		Create
 * </pre>
 * @date 2014-8-11
 */
@Service(ApiServiceName.FLOW_INSTANCE_START)
@Transactional
public class FlowInstanceStartServiceImpl extends BaseServiceImpl implements WellptService<FlowInstanceStartRequest> {

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private WorkService workService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FlowInstanceStartRequest startRequest) {
        String flowDefinitionId = null;
        String startUserId = null;
        String startUserName = null;
        String toTaskId = startRequest.getToTaskId();
        if (StringUtils.isBlank(toTaskId)) {
            toTaskId = FlowService.AS_DRAFT;
        }
        List<String> toTaskUsers = new ArrayList<String>();
        String formUuid = null;
        String dataUuid = null;

        // flowService.startByFlowDefId(flowDefId, startUserId, toTaskId,
        // toTaskUsers, formUuid, dataUuid);

        flowDefinitionId = startRequest.getFlowDefinitionId();
        startUserId = SpringSecurityUtils.getCurrentUserId();
        startUserName = SpringSecurityUtils.getCurrentUserName();
        if (!FlowService.AUTO_SUBMIT.equals(toTaskId)) {
            toTaskUsers.add(startUserId);
        }

        // 获取流程定义
        FlowDefinition flowDefinition = flowService.getFlowDefinitionById(flowDefinitionId);
        if (flowDefinition == null) {
            throw new WorkFlowException("找不到ID为[" + flowDefinitionId + "]的流程定义!");
        }
        TaskData taskData = new TaskData();
        taskData.setUserId(startUserId);
        taskData.setUserName(startUserName);

        taskData.setFormUuid(flowDefinition.getFormUuid());
        formUuid = flowDefinition.getFormUuid();

        DyFormData dyFormData = FormDataUtils.merge2DyformData(startRequest.getFormData(), formUuid, dataUuid);
        dataUuid = dyFormFacade.saveFormData(dyFormData);
        dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        taskData.setDataUuid(dataUuid);
        taskData.setDyFormData(dataUuid, dyFormData);

        QueryItem data = new QueryItem();
        InteractionTaskData interactionTaskData = startRequest.getInteractionTaskData();
        // 兼容旧接口参数调用
        if (interactionTaskData == null) {
            FlowInstance flowInstance = flowService.startByFlowDefId(flowDefinitionId, startUserId, toTaskId,
                    toTaskUsers, formUuid, dataUuid);
            // 设置流程标题
            workService.setFlowInstanceTitle(flowDefinition, flowInstance, taskData, dyFormData);
            flowService.saveFlowInstance(flowInstance);
            data.put("flowInstUuid", flowInstance.getUuid());
        } else {
            WorkBean workBean = workService.newWorkById(flowDefinitionId);
            BeanUtils.copyProperties(startRequest.getInteractionTaskData(), workBean);
            workBean.setFormUuid(formUuid);
            workBean.setDataUuid(dataUuid);
            workBean.setDyFormData(dyFormData);
            ResultMessage resultMessage = null;
            if (FlowService.AS_DRAFT.equals(toTaskId)) {
                Map<String, String> result = workService.save(workBean);
                data.put("flowInstUuid", result.get("flowInstUuid"));
            } else if (FlowService.AUTO_SUBMIT.equals(toTaskId)) {
                workBean.setToTaskId(StringUtils.EMPTY);
                resultMessage = workService.submit(workBean);
                SubmitResult submitResult = (SubmitResult) resultMessage.getData();
                data.put("flowInstUuid", submitResult.getFlowInstUuid());
            } else {
                FlowInstance flowInstance = flowService.saveAsDraft(flowDefinition.getUuid(), taskData);
                taskData.setToTaskId(FlowDelegate.START_FLOW_ID, toTaskId);
                if (MapUtils.isNotEmpty(interactionTaskData.getTaskUsers())) {
                    taskData.setTaskUsers(interactionTaskData.getTaskUsers());
                }
                if (MapUtils.isNotEmpty(interactionTaskData.getTaskCopyUsers())) {
                    taskData.addTaskCopyUsers(interactionTaskData.getTaskCopyUsers());
                }
                if (MapUtils.isNotEmpty(interactionTaskData.getTaskMonitors())) {
                    taskData.addTaskMonitors(interactionTaskData.getTaskMonitors());
                }
                flowService.startFlowInstance(flowInstance.getUuid(), taskData);
                // 未完成的环节增加已办权限
                TaskInstance taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstance.getUuid());
                if (taskInstance.getEndTime() == null) {
                    // 增加已办权限
                    taskService.addDonePermission(taskData.getUserId(), taskInstance.getUuid());
                    // 记录操作日志
                    taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.SUBMIT),
                            ActionCode.SUBMIT.getCode(), WorkFlowOperation.SUBMIT, StringUtils.EMPTY,
                            StringUtils.EMPTY, StringUtils.EMPTY, taskData.getUserId(),
                            taskData.getTaskUsers(toTaskId), null, null, null, taskInstance, flowInstance, taskData);
                }
                //                resultMessage = workService.submit(workBean);
                //                SubmitResult submitResult = (SubmitResult) resultMessage.getData();
                data.put("flowInstUuid", flowInstance.getUuid());
            }
        }

        FlowInstanceStartResponse response = new FlowInstanceStartResponse();
        // data.put("flowInstUuid", flowInstance.getUuid());
        data.put("formUuid", formUuid);
        data.put("dataUuid", dataUuid);
        data.put("createTime", flowService.getFlowInstance(ObjectUtils.toString(data.get("flowInstUuid")))
                .getCreateTime());
        response.setData(data);

        return response;
    }
}
