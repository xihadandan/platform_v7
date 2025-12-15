/*
 * @(#)2021年2月2日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.exception;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;

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
 * 2021年2月2日.1	zhulh		2021年2月2日		Create
 * </pre>
 * @date 2021年2月2日
 */
public class FlowSimulationDataInteractionException extends WorkFlowException {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8265950720594013770L;

    private String taskName;

    private String taskId;

    private String canEditForm;

    private String formUuid;

    private String dataUuid;

    private Map<String, List<Map<String, Object>>> formDatas;

    private TaskForm taskForm;

    /**
     * @param taskName
     * @param taskId
     * @param canEditForm
     * @param formUuid
     * @param dataUuid
     * @param formDatas
     * @param taskForm
     */
    public FlowSimulationDataInteractionException(String taskName, String taskId, String canEditForm, String formUuid, String dataUuid,
                                                  Map<String, List<Map<String, Object>>> formDatas, TaskForm taskForm) {
        this.taskName = taskName;
        this.taskId = taskId;
        this.canEditForm = canEditForm;
        this.formUuid = formUuid;
        this.dataUuid = dataUuid;
        this.formDatas = formDatas;
        this.taskForm = taskForm;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.exception.WorkFlowException#getData()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object getData() {
        Map<String, Object> data = (Map<String, Object>) super.getData();
        // 标记流程仿真
        data.put("simulation", true);
        data.put("taskName", taskName);
        data.put("taskId", taskId);
        data.put("canEditForm", canEditForm);
        data.put("formUuid", formUuid);
        data.put("dataUuid", dataUuid);
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        data.put("formDefinition", dyFormFacade.getFormDefinition(formUuid).getDefinitionJson());
        // 交互的环节
        data.put("dataInteractionTaskId", taskId);
        if (formDatas != null) {
            data.put("formDatas", formDatas);
        }
        if (taskForm != null) {
            data.put("taskForm", taskForm);
        }
        return data;
    }

}
