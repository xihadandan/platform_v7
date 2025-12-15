/*
 * @(#)2021年2月4日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.web.action;

import com.wellsoft.pt.app.workflow.facade.service.WorkFlowSimulationService;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.support.DyformDocumentData;
import com.wellsoft.pt.dms.ext.dyform.web.action.DyFormGetAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年2月4日.1	zhulh		2021年2月4日		Create
 * </pre>
 * @date 2021年2月4日
 */
@Action("流程仿真_表单单据操作")
public class WorkFlowSimulationDyFormAction extends DyFormGetAction {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8209279102251283943L;

    @Autowired
    private WorkFlowSimulationService workFlowSimulationService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.web.action.DyFormGetAction#actionPerformed(com.wellsoft.pt.dms.core.support.DyFormActionData)
     */
    @Override
    @ActionConfig(name = "加载数据", id = "btn_workflow_simulation_dyform_get")
    @ResponseBody
    public DyformDocumentData actionPerformed(@RequestBody DyFormActionData dyFormActionData) {
        String flowDefId = dyFormActionData.getExtraString("ep_flowDefId");
        String flowInstUuid = dyFormActionData.getExtraString("ep_flowInstUuid");
        String taskId = dyFormActionData.getExtraString("ep_dataInteractionTaskId");
        DyformDocumentData dyformDocumentData = super.actionPerformed(dyFormActionData);
        workFlowSimulationService.fillTaskDyformDataControlInfo(dyformDocumentData.getDyFormData(), taskId,
                flowInstUuid, flowDefId);
        return dyformDocumentData;
    }

}
