/*
 * @(#)2018年6月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
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
 * 2018年6月9日.1	zhulh		2018年6月9日		Create
 * </pre>
 * @date 2018年6月9日
 */
@Action("表单单据操作")
public class DyformWorkFlowApproveAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7492087694738934799L;

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "送审批", id = DyFormActions.ACTION_SEND_TO_APPROVE, executeJsModule = "DmsDyformWorkFlowApproveAction")
    @ResponseBody
    public ActionResult actionPerformed(@RequestBody DyFormActionData dyFormActionData) {
        return null;
    }

}
