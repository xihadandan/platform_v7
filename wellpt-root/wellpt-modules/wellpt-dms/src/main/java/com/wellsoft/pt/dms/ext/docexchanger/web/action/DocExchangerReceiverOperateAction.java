/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.docexchanger.web.action;

import com.wellsoft.pt.app.facade.service.AppContextService;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.DocExchangeActionData;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.facade.service.DocExchangerFacadeService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description: 文档交换_收件箱的操作集合
 *
 * @author chenq
 * @date
 */
@Action("文档交换")
public class DocExchangerReceiverOperateAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5230478380296586020L;

    @Autowired
    private AppContextService appContextService;

    @Autowired
    private DmsDocExchangeRecordService dmsDocExchangeRecordService;

    @Autowired
    private DocExchangerFacadeService docExchangerFacadeService;


    @ActionConfig(name = "退回", id = DocExchangerActions.ACTION_RETURN_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerReceiverOperateAction")
    @ResponseBody
    public ActionResult returnDoc(@RequestBody List<DocExchangeActionData> actionDataList,
                                  HttpServletRequest request,
                                  HttpServletResponse response, Model model) {
        ActionResult actionResult = createActionResult();
        try {
            for (DocExchangeActionData data : actionDataList) {
                docExchangerFacadeService.signDocExchangeRecord(data.getDocExcRecordUuid(), true,
                        data.getRemark());
            }
        } catch (Exception e) {
            logger.error("退回异常：", e);
            actionResult.setSuccess(false);
            actionResult.setMsg("退回失败");
            return actionResult;
        }
        actionResult.setRefresh(true);
        actionResult.setMsg("退回成功");
        return actionResult;
    }


    @ActionConfig(name = "转发", id = DocExchangerActions.ACTION_FORWARD_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerReceiverOperateAction")
    @ResponseBody
    public ActionResult forwardDoc(@RequestBody DocExchangeActionData actionData,
                                   HttpServletRequest request,
                                   HttpServletResponse response, Model model) {
        ActionResult actionResult = createActionResult();
        try {
            dmsDocExchangeRecordService.forwardDocExchange(actionData);
        } catch (Exception e) {
            logger.error("转发异常：", e);
            actionResult.setSuccess(false);
            actionResult.setMsg("转发失败");
            return actionResult;
        }
        actionResult.setRefresh(true);
        actionResult.setMsg("转发成功");
        return actionResult;
    }

    @ActionConfig(name = "反馈意见", id = DocExchangerActions.ACTION_FEEDBACK_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerReceiverOperateAction")
    @ResponseBody
    public ActionResult feedbackDoc(@RequestBody DocExchangeActionData actionData,
                                    HttpServletRequest request,
                                    HttpServletResponse response, Model model) {
        ActionResult actionResult = createActionResult();
        try {
            dmsDocExchangeRecordService.feedbackDocExchange(actionData);
        } catch (Exception e) {
            logger.error("反馈意见异常：", e);
            actionResult.setSuccess(false);
            actionResult.setMsg("反馈意见失败");
            return actionResult;
        }
        actionResult.setRefresh(true);
        actionResult.setMsg("反馈意见成功");
        return actionResult;
    }

}
