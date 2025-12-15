/*
 * @(#)2018年6月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.listview.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.support.ListViewSelection;
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
@Action("视图列表")
public class ListViewWorkFlowApproveAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 969076888066085470L;

    /**
     * @param listViewSelection
     * @return
     */
    @ListViewActionConfig(name = "送审批", id = ListViewActions.ACTION_SEND_TO_APPROVE, singleSelect = false, promptMsg = "请选择数据", singleSelectPromptMsg = "请单选数据", executeJsModule = "DmsListViewWorkFlowApproveAction")
    @ResponseBody
    public ActionResult actionPerformed(@RequestBody ListViewSelection listViewSelection) {
        return null;
    }

}
