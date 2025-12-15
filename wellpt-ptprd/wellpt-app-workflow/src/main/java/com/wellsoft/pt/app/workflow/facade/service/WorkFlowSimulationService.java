/*
 * @(#)2018年12月24日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.workflow.dto.FlowSimulationDataDto;
import com.wellsoft.pt.app.workflow.dto.FlowSimulationWorkData;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;

import java.util.List;
import java.util.Map;

/**
 * Description: 流程仿真
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年12月24日.1	zhulh		2018年12月24日		Create
 * </pre>
 * @date 2018年12月24日
 */
public interface WorkFlowSimulationService extends BaseService {

    /**
     * 获取仿真数据
     *
     * @param flowInstUuid
     * @return
     */
    FlowSimulationDataDto getSimulationData(String flowInstUuid);

    /**
     * 仿真提交数据
     *
     * @param simulationData
     */
    ResultMessage simulationSubmit(FlowSimulationDataDto simulationData);

    /**
     * 仿真完成数据
     *
     * @param simulationData
     * @return
     */
    ResultMessage simulationComplete(FlowSimulationDataDto simulationData);

    /**
     * 仿真转办
     *
     * @param simulationData
     * @return
     */
    void simulationTransfer(FlowSimulationDataDto simulationData);

    /**
     * 仿真会签
     *
     * @param simulationData
     */
    void simulationCounterSign(FlowSimulationDataDto simulationData);

    /**
     * 仿真跳转
     *
     * @param simulationData
     */
    void simulationGotoTask(FlowSimulationDataDto simulationData);

    /**
     * 填充环节表单字段控制信息
     *
     * @param dyFormData
     * @param taskId
     * @param flowInstUuid
     * @param flowDefId
     */
    void fillTaskDyformDataControlInfo(DyFormData dyFormData, String taskId, String flowInstUuid, String flowDefId);

    /**
     * @param taskId
     * @param dyFormData
     * @param simulationData
     * @return
     */
    boolean setFieldValueIfRequired(String taskId, DyFormData dyFormData, FlowSimulationDataDto simulationData);

    /**
     * @param dyFormData
     * @param taskForm
     * @return
     */
    boolean hasRequireFieldIsEmpty(DyFormData dyFormData, TaskForm taskForm);

    /**
     * 根据流程定义ID列出所有环节
     *
     * @param flowDefId
     * @return List<Map < 环节ID ， 环节名称>>
     */
    List<Map<String, Object>> listTaskByFlowDefId(String flowDefId);

    /**
     * 清理仿真数据
     */
    void cleanSimulationData();

    /**
     * 根据仿真记录获取流程数据
     *
     * @param recordUuid
     * @param taskInstUuid
     * @return
     */
    FlowSimulationWorkData getSimulationWorkDataByRecordUuidAndTaskInstUuid(Long recordUuid, String taskInstUuid);

    String simulationSaveFormData(DyFormData dyFormData);
}
