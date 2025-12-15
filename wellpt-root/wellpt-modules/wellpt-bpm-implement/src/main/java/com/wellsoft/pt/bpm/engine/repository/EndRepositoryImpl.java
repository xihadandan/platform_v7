/*
 * @(#)2012-11-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.repository;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.dao.FlowInstanceDao;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowState;
import com.wellsoft.pt.bpm.engine.util.ReservedFieldUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.workflow.enums.WorkFlowFieldMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
 * 2012-11-23.1	zhulh		2012-11-23		Create
 * </pre>
 * @date 2012-11-23
 */
@Service
@Transactional
public class EndRepositoryImpl implements EndRepository {
    @Autowired
    private FlowInstanceDao flowInstanceDao;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.repository.Repository#storeEnter(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void storeEnter(Node node, ExecutionContext executionContext) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.repository.Repository#store(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void store(Node node, ExecutionContext executionContext) {
        // 当前流程实例结束，保存信息
        FlowInstance flowInstance = executionContext.getToken().getFlowInstance();
        flowInstance.setIsActive(false);
        // 设置流程实例的当前环节为空
        // flowInstance.setCurrentTaskInstance(null);
        // 设置流程实例持续时间
        Date startTime = flowInstance.getStartTime();
        Date endTime = Calendar.getInstance().getTime();
        flowInstance.setDuration(endTime.getTime() - startTime.getTime());
        flowInstance.setEndTime(endTime);

        TaskData taskData = executionContext.getToken().getTaskData();
        // 更新预留字段
        ReservedFieldUtils.setReservedFields(flowInstance, taskData);

        // 同步流程状态的表单映射字段值
        List<String> mappingFieldNames = new ArrayList<String>();
        List<Object> mappingFieldValues = new ArrayList<Object>();
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_TASK.getValue());
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_TASK_ID.getValue());
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_FLOW_STATE_CODE.getValue());
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_FLOW_STATE_NAME.getValue());
        mappingFieldValues.add(executionContext.getToken().getFlowDelegate().getFlowStateName(WorkFlowState.Over));
        mappingFieldValues.add(FlowConstant.END_FLOW_ID);
        mappingFieldValues.add(WorkFlowState.Over);
        mappingFieldValues.add(executionContext.getToken().getFlowDelegate().getFlowStateName(WorkFlowState.Over));
        syncFlowStateFieldMapping(mappingFieldNames, mappingFieldValues, flowInstance, taskData);

        flowInstanceDao.save(flowInstance);
    }

    /**
     * 如何描述该方法
     *
     * @param mappingNames
     * @param mappingValues
     * @param taskInstance
     */
    private void syncFlowStateFieldMapping(List<String> mappingNames, List<Object> mappingValues,
                                           FlowInstance flowInstance, TaskData taskData) {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        String formUuid = flowInstance.getFormUuid();
        String dataUuid = flowInstance.getDataUuid();
        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        }
        // 同步表单流程号映射字段值
        boolean hasUpdateMappingField = false;
        for (int index = 0; index < mappingNames.size(); index++) {
            String mappingName = mappingNames.get(index);
            Object mappingValue = mappingValues.get(index);
            if (dyFormData != null && dyFormData.hasFieldMappingName(mappingName)) {
                dyFormData.setFieldValueByMappingName(mappingName, mappingValue);
                hasUpdateMappingField = true;
            }
        }
        if (hasUpdateMappingField) {
//			dyFormData.doForceCover();
//			String updatedDataUuid = dyFormFacade.saveFormData(dyFormData);
            taskData.setDataUuid(dataUuid);
            taskData.setDyFormData(dataUuid, dyFormData);
            taskData.addUpdatedDyFormData(dataUuid, dyFormData);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.repository.Repository#storeLeave(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void storeLeave(Node node, ExecutionContext executionContext) {
    }

}
