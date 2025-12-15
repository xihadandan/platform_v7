/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.docexchanger.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordEntity;
import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;
import com.wellsoft.pt.dms.enums.DocExchangeTypeEnum;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 文档交换_新增ACTION
 *
 * @author chenq
 * @date
 */
@Action("文档交换")
public class DocExchangerSaveAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5230478380296586020L;

    @Autowired
    DmsDocExchangeRecordService dmsDocExchangeRecordService;


    @ActionConfig(name = "保存", id = DocExchangerActions.ACTION_SAVE_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerSaveAction")
    @ResponseBody
    public ActionResult save(@RequestBody DyFormActionData dyFormActionData) {
        return process(dyFormActionData, false, "保存成功");
    }


    @ActionConfig(name = "保存并验证", id = DocExchangerActions.ACTION_SAVE_VALIDATE_DOC_EXCHANGER, validate = true, executeJsModule = "DmsDocExchangerSaveAction")
    @ResponseBody
    public ActionResult saveAfterValidate(@RequestBody DyFormActionData dyFormActionData) {
        return process(dyFormActionData, false, "保存成功");
    }


    @ActionConfig(name = "发送", id = DocExchangerActions.ACTION_SEND_DOC_EXCHANGER, validate = true, executeJsModule = "DmsDocExchangerSaveAction")
    @ResponseBody
    public ActionResult send(@RequestBody DyFormActionData dyFormActionData) {
        return process(dyFormActionData, true, "发送成功");
    }


    private ActionResult process(DyFormActionData dyFormActionData, boolean isSend,
                                 String actionMsg) {
        DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.saveDocExchangeRecordWithDyformData(
                dyFormActionData, isSend);
        ActionResult actionResult = createActionResult();
        actionResult.addAppendUrlParam("idValue",
                recordEntity.getExchangeType() == DocExchangeTypeEnum.DYFORM.ordinal() ?
                        recordEntity.getDataUuid() : recordEntity.getUuid());
        /*Map<String, Object> data = new HashMap<String, Object>();
        data.put("formUuid", dyFormActionData.getDyFormData().getFormUuid());
        data.put("dataUuid", recordEntity.getDataUuid());
        actionResult.setData(data);*/
        actionResult.setRefresh(true);
        actionResult.setRefreshParent(true);
        actionResult.setClose(isSend);
        actionResult.setMsg(
                recordEntity.getRecordStatus() == DocExchangeRecordStatusEnum.WAIT_APPROVAL.ordinal() ? "送审批成功" : actionMsg);
        actionResult.addAppendUrlParam("docExchangeRecordUuid", recordEntity.getUuid());
        return actionResult;
    }

}
