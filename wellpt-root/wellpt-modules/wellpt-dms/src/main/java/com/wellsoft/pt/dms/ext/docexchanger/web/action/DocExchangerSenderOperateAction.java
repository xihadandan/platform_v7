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
import com.wellsoft.pt.dms.service.DmsDocExcExtraSendService;
import com.wellsoft.pt.dms.service.DmsDocExchangeLogService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordDetailService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 文档交换_发件箱的操作集合
 *
 * @author chenq
 * @date
 */
@Action("文档交换")
public class DocExchangerSenderOperateAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5230478380296586020L;

    @Autowired
    private AppContextService appContextService;

    @Autowired
    private DmsDocExchangeRecordService dmsDocExchangeRecordService;

    @Autowired
    private DmsDocExcExtraSendService dmsDocExcExtraSendService;

    @Autowired
    private DmsDocExchangeRecordDetailService dmsDocExchangeRecordDetailService;

    @Autowired
    private DmsDocExchangeLogService dmsDocExchangeLogService;


    @ActionConfig(name = "补充发送", id = DocExchangerActions.ACTION_EXTRA_SEND_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerSenderOperateAction")
    @ResponseBody
    public ActionResult replenishSendDoc(@RequestBody DocExchangeActionData actionData,
                                         HttpServletRequest request, HttpServletResponse response,
                                         Model model) {
        ActionResult actionResult = createActionResult();
        try {
            dmsDocExcExtraSendService.saveExtraSendData(actionData);
        } catch (Exception e) {
            logger.error("补充发送人员异常：", e);
            actionResult.setSuccess(false);
            actionResult.setMsg("补充发送失败");
            return actionResult;
        }
        actionResult.setRefresh(true);
        actionResult.setMsg("补充发送成功");
        return actionResult;
    }

    @ActionConfig(name = "撤回", id = DocExchangerActions.ACTION_REVOKE_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerSenderOperateAction")
    @ResponseBody
    public ActionResult revokeDoc(@RequestBody DocExchangeActionData actionData,
                                  HttpServletRequest request, HttpServletResponse response,
                                  Model model) {
        ActionResult actionResult = createActionResult();
        try {
            dmsDocExchangeRecordDetailService.revokeReceiveDetail(actionData);
        } catch (Exception e) {
            logger.error("撤回发文异常：", e);
            actionResult.setSuccess(false);
            actionResult.setMsg("撤回失败");
            return actionResult;
        }
        actionResult.setRefresh(true);
        actionResult.setMsg("撤回成功");
        return actionResult;
    }


    @ActionConfig(name = "催办", id = DocExchangerActions.ACTION_URGE_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerSenderOperateAction")
    @ResponseBody
    public ActionResult urgeDoc(@RequestBody DocExchangeActionData actionData,
                                HttpServletRequest request, HttpServletResponse response,
                                Model model) {
        ActionResult actionResult = createActionResult();
        try {
            dmsDocExchangeRecordDetailService.urgeReceiveDetail(actionData);
        } catch (Exception e) {
            logger.error("催办发文异常：", e);
            actionResult.setSuccess(false);
            actionResult.setMsg("催办失败");
            return actionResult;
        }
        actionResult.setRefresh(true);
        actionResult.setMsg("催办成功");
        return actionResult;
    }

    @ActionConfig(name = "办结", id = DocExchangerActions.ACTION_FINISH_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerSenderOperateAction")
    @ResponseBody
    public ActionResult finishDoc(@RequestBody DocExchangeActionData actionData,
                                  HttpServletRequest request, HttpServletResponse response,
                                  Model model) {
        ActionResult actionResult = createActionResult();
        try {
            dmsDocExchangeRecordService.updateRecordFinished(actionData.getDocExcRecordUuid());
        } catch (Exception e) {
            logger.error("办结发文异常：", e);
            actionResult.setSuccess(false);
            actionResult.setMsg("办结失败");
            return actionResult;
        }
        actionResult.setRefresh(true);
        actionResult.setMsg("办结成功");
        return actionResult;
    }

}
