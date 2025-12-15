/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.datamark.web.action;

import com.wellsoft.pt.dms.bean.MarkDataDto;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.facade.service.DmsDataMarkFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 数据标记_列表操作
 *
 * @author chenq
 * @date
 */
@Action("数据标记")
public class DmsDataMarkListViewAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5230478380296586020L;


    @Autowired
    private DmsDataMarkFacadeService dmsDataMarkFacadeService;


    @ActionConfig(name = "标记数据", id = DmsDataMarkActions.ACTION_LIST_MARK_DATA, executeJsModule = "DmsDataMarkListViewAction")
    @ResponseBody
    public ActionResult markData(@RequestBody MarkDataDto actionData) {
        ActionResult markResult = mark(actionData);
        //markResult.setMsg(markResult.isSuccess() ? "收藏成功" : "收藏失败");
        return markResult;
    }

    @ActionConfig(name = "取消标记数据", id = DmsDataMarkActions.ACTION_LIST_UNMARK_DATA, executeJsModule = "DmsDataMarkListViewAction")
    @ResponseBody
    public ActionResult unmarkData(@RequestBody MarkDataDto actionData) {
        ActionResult markResult = mark(actionData);
        //markResult.setMsg(markResult.isSuccess() ? "取消收藏成功" : "取消收藏失败");
        return markResult;
    }


    private ActionResult mark(MarkDataDto actionData) {
        ActionResult actionResult = createActionResult();
        try {
            dmsDataMarkFacadeService.saveOrDeleteMark(actionData);
        } catch (Exception e) {
            logger.error("数据标记操作异常：", e);
            actionResult.setSuccess(false);
            return actionResult;
        }
        actionResult.setRefresh(true);
        return actionResult;
    }


}
