/*
 * @(#)2021年2月2日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.listener;

import com.wellsoft.pt.app.workflow.dto.FlowSimulationDataDto;
import com.wellsoft.pt.app.workflow.dto.FlowSimulationDataDto.SimulationParams;
import com.wellsoft.pt.app.workflow.exception.FlowSimulationDataInteractionException;
import com.wellsoft.pt.app.workflow.facade.service.WorkFlowSimulationService;
import com.wellsoft.pt.app.workflow.facade.service.impl.WorkFlowSimulationServiceImpl.SimulationContextHolder;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.impl.DirectionListenerAdapter;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年2月2日.1	zhulh		2021年2月2日		Create
 * </pre>
 * @date 2021年2月2日
 */
@Component
public class FlowSimulationDirectionListener extends DirectionListenerAdapter {

    @Autowired
    private WorkFlowSimulationService workFlowSimulationService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.DirectionListenerAdapter#transition(com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void transition(Event event) throws WorkFlowException {
        FlowSimulationDataDto simulationDataDto = SimulationContextHolder.getFlowSimulationData();
        if (simulationDataDto == null) {
            return;
        }
        SimulationParams simulationParams = simulationDataDto.getSimulationParams();
        if (simulationParams == null) {
            return;
        }

        String toTaskId = event.getNextTaskId();
        FlowDelegate flowDelegate = event.getTaskData().getToken().getFlowDelegate();
        String interactionMode = simulationParams.getInteractionMode();
        DyFormData dyFormData = event.getDyFormData();
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(event.getFormUuid(), event.getDataUuid());
        }
        // 表单数据交互方式，requiredField按必填字段
        if (SimulationParams.INTERACTION_MODE_REQUIREDFIELD.equals(interactionMode)) {
            TaskForm taskForm = flowDelegate.getTaskForm(toTaskId);
            Set<String> userInteractedTasks = simulationDataDto.getUserInteractedTasks();
            if (workFlowSimulationService.hasRequireFieldIsEmpty(dyFormData, taskForm)
                    && (CollectionUtils.isEmpty(userInteractedTasks) || !userInteractedTasks.contains(toTaskId))) {
                TaskElement taskElement = flowDelegate.getFlow().getTask(toTaskId);
                String canEditForm = taskElement.getCanEditForm();
                if (StringUtils.isBlank(canEditForm) && flowDelegate.isFirstTaskNode(toTaskId)) {
                    canEditForm = "1";
                }
                throw new FlowSimulationDataInteractionException(taskElement.getName(), toTaskId, canEditForm, event.getFormUuid(),
                        event.getDataUuid(), dyFormData.getFormDatas(), taskForm);
            }
        } else {
            // task按环节
            List<String> interactionTasks = simulationParams.getInteractionTasks();
            if (CollectionUtils.isEmpty(interactionTasks)) {
                return;
            }
            Set<String> userInteractedTasks = simulationDataDto.getUserInteractedTasks();
            if (CollectionUtils.isNotEmpty(userInteractedTasks) && userInteractedTasks.contains(toTaskId)) {
                return;
            }
            if (interactionTasks.contains(toTaskId)) {
                TaskElement taskElement = flowDelegate.getFlow().getTask(toTaskId);
                String canEditForm = taskElement.getCanEditForm();
                if (StringUtils.isBlank(canEditForm) && flowDelegate.isFirstTaskNode(toTaskId)) {
                    canEditForm = "1";
                }
                TaskForm taskForm = flowDelegate.getTaskForm(toTaskId);
                throw new FlowSimulationDataInteractionException(taskElement.getName(), toTaskId, canEditForm, event.getFormUuid(),
                        event.getDataUuid(), dyFormData.getFormDatas(), taskForm);
            }
        }
    }

}
