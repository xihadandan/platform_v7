/*
 * @(#)Feb 16, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
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
public class DyFormCloseAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6185570346483889901L;

    /**
     * @return
     */
    @ActionConfig(name = "关闭", id = DyFormActions.ACTION_CLOSE, executeJsModule = "DmsDyformCloseAction")
    @ResponseBody
    public ActionResult actionPerformed() {
        ActionResult actionResult = createActionResult();
        actionResult.setClose(true);
        return actionResult;
    }

}
