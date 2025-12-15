/*
 * @(#)10/12/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.workflow.dto.FlowSimulationDataDto;
import com.wellsoft.pt.app.workflow.facade.service.WorkFlowSimulationService;
import com.wellsoft.pt.app.workflow.facade.service.impl.WorkFlowSimulationServiceImpl.SimulationContextHolder;
import com.wellsoft.pt.app.workflow.support.FlowSimulationRecorder;
import com.wellsoft.pt.app.workflow.support.FlowSimulationRuntimeParams;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.InternalTaskListener;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.SuspensionState;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.service.IdentityService;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordItemEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 10/12/24.1	    zhulh		10/12/24		    Create
 * </pre>
 * @date 10/12/24
 */
@Component
public class FlowSimulationTaskListener extends InternalTaskListener {

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private WorkFlowSimulationService workFlowSimulationService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Override
    public String getName() {
        return "流程仿真_环节监听器";
    }

    @Override
    public void onCreated(Event event) throws WorkFlowException {
        FlowSimulationDataDto simulationDataDto = SimulationContextHolder.getFlowSimulationData();
        FlowSimulationDataDto.SimulationParams simulationParams = simulationDataDto.getSimulationParams();
        FlowSimulationRuntimeParams runtimeParams = simulationDataDto.getRuntimeParams();
        TaskData taskData = event.getTaskData();
        String taskId = event.getTaskId();
        DyFormData dyFormData = event.getDyFormData();

        // 仿真流程标记为不激活状态
        TaskInstance taskInstance = taskData.getToken().getTask();
        if (taskInstance != null) {
            taskInstance.setSuspensionState(SuspensionState.LOGIC_DELETED.getState());
            taskInstanceService.save(taskInstance);
        }

        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(event.getFormUuid(), event.getDataUuid());
        }
        boolean startFlow = taskData.getStartNewFlow(event.getFlowInstUuid());
        if (!startFlow) {
            boolean updateFieldValue = workFlowSimulationService.setFieldValueIfRequired(taskId, dyFormData, simulationDataDto);
            if (updateFieldValue) {
                taskData.addUpdatedDyFormData(dyFormData.getDataUuid(), dyFormData);
            }
        }
        String preTaskInstUuid = taskData.getPreTaskInstUuid(taskId);
        WfFlowSimulationRecordItemEntity itemEntity = new WfFlowSimulationRecordItemEntity();
        itemEntity.setFlowInstUuid(event.getFlowInstUuid());
        itemEntity.setTaskInstUuid(event.getTaskInstUuid());
        itemEntity.setTaskName(event.getTaskName());
        itemEntity.setTaskId(taskId);
        itemEntity.setPreTaskInstUuid(preTaskInstUuid);

        FlowDelegate flowDelegate = taskData.getToken().getFlowDelegate();
        TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
        Map<String, Object> details = Maps.newHashMap();
        FlowSimulationDataDto.SimulationTask simulationTask = getSimulationTask(taskId, simulationParams);
        details.put("startFlow", startFlow);
        details.put("taskType", taskElement.getType());
        details.put("formUuid", dyFormData.getFormUuid());
        details.put("dataUuid", dyFormData.getDataUuid());
        details.put("formDatas", dyFormData.getFormDatas());
        details.put("isSetUser", taskElement.getIsSetUser());
        details.put("users", taskElement.getUsers());
        details.put("decisionMakers", taskElement.getDecisionMakers());
        if (StringUtils.isNotBlank(simulationParams.getTaskUserId())) {
            details.put("simulationDefaultTaskUserId", simulationParams.getTaskUserId());
            String simulationDefaultTaskUserName = IdentityResolverStrategy.resolveAsNames(Arrays.asList(StringUtils.split(simulationParams.getTaskUserId(), Separator.SEMICOLON.getValue())));
            details.put("simulationDefaultTaskUserName", simulationDefaultTaskUserName);
        }
        if (simulationTask != null) {
            details.put("simulationTaskUserId", simulationTask.getSimulationTaskUserId());
            details.put("simulationTaskUserName", simulationTask.getSimulationTaskUserName());
            details.put("simulationDecisionMakerId", simulationTask.getSimulationDecisionMakerId());
            details.put("simulationDecisionMakerName", simulationTask.getSimulationDecisionMakerName());
        }
        details.put("isSetCopyUser", taskElement.getIsSetCopyUser());
        details.put("copyUsers", taskElement.getCopyUsers());
        details.put("copyUserCondition", taskElement.getCopyUserCondition());
        details.put("isSetMonitor", taskElement.getIsSetMonitor());
        details.put("monitors", taskElement.getMonitors());
        if (startFlow) {
            details.put("taskUserIds", SpringSecurityUtils.getCurrentUserId());
            details.put("taskUserNames", SpringSecurityUtils.getCurrentUserName());
        } else {
            String taskUserIds = taskData.getToken().getTask().getOwner();
            if (taskData.isByOrder(taskId)) {
                List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuid(event.getTaskInstUuid());
                List<String> userIds = taskIdentities.stream().sorted((o1, o2) -> o1.getSortOrder().compareTo(o2.getSortOrder()))
                        .map(o -> o.getUserId()).collect(Collectors.toList());
                details.put("taskUserIds", userIds);
                details.put("taskUserNames", IdentityResolverStrategy.resolveAsNames(userIds));
            } else if (StringUtils.isNotBlank(taskUserIds)) {
                details.put("taskUserIds", taskUserIds);
                details.put("taskUserNames", IdentityResolverStrategy.resolveAsNames(Arrays.asList(StringUtils.split(taskUserIds, Separator.SEMICOLON.getValue()))));
            }
        }
        details.put("taskDecisionMakerNames", StringUtils.join(IdentityResolverStrategy.resolveAsOrgNames(taskData.getTaskDecisionMakerSids(taskId)), Separator.SEMICOLON.getValue()));
        details.put("simulationTaskUserFlag", runtimeParams.getSimulationTaskUserFlag(taskId));
        details.put("simulationDefaultTaskUserFlag", runtimeParams.getSimulationDefaultTaskUserFlag(taskId));
        details.put("simulationDecisionMakerFlag", runtimeParams.getSimulationDecisionMakerFlag(taskId));
        details.put("taskCopyUserNames", StringUtils.join(IdentityResolverStrategy.resolveAsOrgNames(taskData.getTaskCopyUserSids(taskId)), Separator.SEMICOLON.getValue()));
        List<FlowUserSid> taskMonitorIds = (List) taskData.get("resolvedTaskMonitorIds_" + taskId);
        if (CollectionUtils.isEmpty(taskMonitorIds)) {
            taskMonitorIds = Lists.newArrayListWithCapacity(0);
        }
        details.put("taskMonitorUserNames", StringUtils.join(IdentityResolverStrategy.resolveAsOrgNames(taskMonitorIds), Separator.SEMICOLON.getValue()));
        details.put("taskUserNoFound", runtimeParams.getTaskUserNoFound(taskId));
        details.put("taskDecisionMakerNoFound", runtimeParams.getTaskDecisionMakerNoFound(taskId));
        details.put("taskCopyUserNoFound", runtimeParams.getTaskCopyUserNoFound(taskId));
        details.put("taskSuperviseUserNoFound", runtimeParams.getTaskSuperviseUserNoFound(taskId));
        details.put("chooseOneUser", runtimeParams.getChooseOneUser(taskId));
        details.put("chooseMultiUser", runtimeParams.getChooseMultiUser(taskId));
        details.put("randomUserCount", runtimeParams.getRandomUserCount(taskId));
        List<String> candidateUserIds = runtimeParams.getCandidateUserIds(taskId);
        if (CollectionUtils.isNotEmpty(candidateUserIds)) {
            details.put("candidateUserNames", IdentityResolverStrategy.resolveAsNames(candidateUserIds));
        }
        itemEntity.setDetails(JsonUtils.object2Json(details));
        FlowSimulationRecorder recorder = SimulationContextHolder.getFlowSimulationRecorder();
        recorder.addItem(itemEntity);
    }

    /**
     * @param taskId
     * @param simulationParams
     * @return
     */
    private FlowSimulationDataDto.SimulationTask getSimulationTask(String taskId, FlowSimulationDataDto.SimulationParams simulationParams) {
        List<FlowSimulationDataDto.SimulationTask> tasks = simulationParams.getTasks();
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }
        return tasks.stream().filter(task -> StringUtils.equals(task.getId(), taskId)).findFirst().orElse(null);
    }

    @Override
    public void onCompleted(Event event) throws WorkFlowException {
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
