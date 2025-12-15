/*
 * @(#)4/11/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 业务流程_撤回操作
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 4/11/23.1	zhulh		4/11/23		Create
 * </pre>
 * @date 4/11/23
 */
@Action("业务流程_撤回操作")
public class BizProcessItemCancelAction extends ActionSupport {

    /**
     * @return
     */
    @ActionConfig(name = "撤回业务事项", id = "process_item_cancel", executeJsModule = "BizProcessItemCancelAction")
    @ResponseBody
    public ActionResult processItemCancel() {
        ActionResult actionResult = createActionResult();
        actionResult.setClose(true);
        return actionResult;
    }

    /**
     * @return
     */
    @ActionConfig(name = "撤回发起的业务事项", id = "process_item_cancel_other", executeJsModule = "BizProcessItemCancelAction")
    @ResponseBody
    public ActionResult processItemCancelOther() {
        ActionResult actionResult = createActionResult();
        actionResult.setClose(true);
        return actionResult;
    }

}
