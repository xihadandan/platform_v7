/*
 * @(#)2019年3月22日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.web.action;

import com.wellsoft.pt.app.workflow.dto.UndertakeSituationActionData;
import com.wellsoft.pt.app.workflow.facade.service.WorkFlowUndertakeSituationService;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import org.apache.commons.lang.StringUtils;
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
 * 2019年3月22日.1	zhulh		2019年3月22日		Create
 * </pre>
 * @date 2019年3月22日
 */
@Action("分支流、子流程承办信息操作")
public class WorkFlowUndertakeSituationAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7125998299593767649L;

    @Autowired
    private WorkFlowUndertakeSituationService workFlowUndertakeSituationService;

    @ActionConfig(name = "添加承办", id = "btn_wf_undertake_situation_add_subflow")
    @ResponseBody
    public ActionResult actionPerformed(@RequestBody UndertakeSituationActionData undertakeSituationActionData) {
        String dataSource = undertakeSituationActionData.getDataSource();
        // 分支流
        if (StringUtils.equals(FlowConstant.UNDERTAKE_SITUATION_DATA_SOURCE.BRANCH_TASK, dataSource)) {
            workFlowUndertakeSituationService.startBranchTask(undertakeSituationActionData);
        } else {
            // 子流程
            workFlowUndertakeSituationService.startSubFlow(undertakeSituationActionData);
        }
        ActionResult actionResult = createActionResult();
        actionResult.setClose(true);
        actionResult.setRefresh(false);
        actionResult.setRefreshParent(true);
        actionResult.setMsg("操作成功！");
        return actionResult;
    }

}
