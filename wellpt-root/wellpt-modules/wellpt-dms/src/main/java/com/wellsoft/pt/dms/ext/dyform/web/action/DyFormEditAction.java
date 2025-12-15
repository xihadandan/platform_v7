/*
 * @(#)2018年6月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData;
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
 * 2018年6月27日.1	zhulh		2018年6月27日		Create
 * </pre>
 * @date 2018年6月27日
 */
@Action("表单单据操作")
public class DyFormEditAction extends DyFormGetAction {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2786634957089901249L;

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "编辑", id = DyFormActions.ACTION_EDIT)
    @ResponseBody
    public ActionResult edit(@RequestBody FileManagerDyFormActionData dyFormActionData) {
        ActionResult actionResult = createActionResult();
        actionResult.addAppendUrlParam(KEY_VIEW_MODE, VALUE_EDIT_MODE);
        actionResult.setRefresh(true);
        actionResult.setShowMsg(false);
        return actionResult;
    }

}
