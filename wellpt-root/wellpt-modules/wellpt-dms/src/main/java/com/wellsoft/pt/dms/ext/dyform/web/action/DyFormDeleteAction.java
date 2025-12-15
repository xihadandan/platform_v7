/*
 * @(#)Feb 16, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService;
import com.wellsoft.pt.dms.ext.support.ActionDataCustomizer;
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
 * Feb 16, 2017.1	zhulh		Feb 16, 2017		Create
 * </pre>
 * @date Feb 16, 2017
 */
@Action("表单单据操作")
public class DyFormDeleteAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3149186709712822464L;

    @Autowired
    private DyFormActionService dyFormActionService;


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.action.Action#actionPerformed(com.wellsoft.pt.dms.core.action.ActionEvent)
     */
    @ActionConfig(name = "逻辑删除", id = DyFormActions.ACTION_LOGIC_DELETE, confirmMsg = "确认要删除吗?")
    @ResponseBody
    public ActionResult logicDelete(@RequestBody DyFormActionData dyFormActionData) {
        String statusField = dyFormActionData.getExtraString("statusField");
        String statusValue = dyFormActionData.getExtraString("statusValue");
        if (StringUtils.isBlank(statusField)) {
            statusField = "status";
        }
        if (StringUtils.isBlank(statusValue)) {
            statusValue = "-1";
        }
        dyFormActionService.logicDelete(dyFormActionData, statusField, statusValue);

        ActionResult actionResult = createActionResult();
        actionResult.setRefreshParent(true);
        actionResult.setClose(true);
        actionResult.setMsg(ActionDataCustomizer.getSuccessMsg(dyFormActionData, "删除成功!"));
        return actionResult;
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.action.Action#actionPerformed(com.wellsoft.pt.dms.core.action.ActionEvent)
     */
    @ActionConfig(name = "删除", id = DyFormActions.ACTION_DELETE, confirmMsg = "确认要删除吗?")
    @ResponseBody
    public ActionResult actionPerformed(@RequestBody DyFormActionData dyFormActionData) {
        boolean isVersioning = getActionContext().getConfiguration().isEnableVersioning();
        dyFormActionService.delete(dyFormActionData.getFormUuid(), dyFormActionData.getDataUuid(), isVersioning);

        ActionResult actionResult = createActionResult();
        actionResult.setRefreshParent(true);
        actionResult.setClose(true);
        actionResult.setMsg(ActionDataCustomizer.getSuccessMsg(dyFormActionData, "删除成功!"));
        return actionResult;
    }

}
