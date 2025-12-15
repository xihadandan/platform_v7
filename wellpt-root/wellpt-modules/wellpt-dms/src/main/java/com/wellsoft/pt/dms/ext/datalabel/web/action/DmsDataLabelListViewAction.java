/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.datalabel.web.action;

import com.wellsoft.pt.app.facade.service.AppContextService;
import com.wellsoft.pt.dms.bean.DmsDataLabelDto;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.service.DmsDataLabelService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * Description: 数据标签_标签动作
 *
 * @author chenq
 * @date
 */
@Action("数据标签")
public class DmsDataLabelListViewAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5230478380296586020L;

    @Autowired
    private AppContextService appContextService;

    @Autowired
    private DmsDataLabelService dmsDataLabelService;


    @ActionConfig(name = "新建标签", id = DmsDataLabelActions.ACTION_ADD_LABEL, executeJsModule = "DmsDataLabelListViewAction")
    @ResponseBody
    public ActionResult addLabel(@RequestBody DmsDataLabelDto actionData,
                                 HttpServletRequest request, HttpServletResponse response) {
        return saveOrUpdataLabel(actionData);
    }


    @ActionConfig(name = "编辑标签", id = DmsDataLabelActions.ACTION_EDIT_LABEL, executeJsModule = "DmsDataLabelListViewAction")
    @ResponseBody
    public ActionResult editLabel(@RequestBody DmsDataLabelDto actionData,
                                  HttpServletRequest request, HttpServletResponse response) {
        return saveOrUpdataLabel(actionData);
    }

    private ActionResult saveOrUpdataLabel(DmsDataLabelDto actionData) {
        ActionResult actionResult = createActionResult();
        try {
            dmsDataLabelService.saveDmsDataLabel(actionData);
        } catch (Exception e) {
            logger.error("标签操作异常：", e);
            actionResult.setSuccess(false);
            actionResult.setMsg(StringUtils.isNotBlank(actionData.getUuid()) ? "编辑标签失败" : "新建标签失败");
            return actionResult;
        }
        actionResult.setRefresh(true);
        actionResult.setMsg(StringUtils.isNotBlank(actionData.getUuid()) ? "编辑标签成功" : "新建标签成功");
        return actionResult;
    }


    @ActionConfig(name = "删除标签", id = DmsDataLabelActions.ACTION_DEKETE_LABEL, executeJsModule = "DmsDataLabelListViewAction")
    @ResponseBody
    public ActionResult deleteLabels(@RequestBody ArrayList<String> uuids,
                                     HttpServletRequest request, HttpServletResponse response) {
        ActionResult actionResult = createActionResult();
        try {
            dmsDataLabelService.deleteLabelsAndRelaData(uuids);
        } catch (Exception e) {
            logger.error("标签操作异常：", e);
            actionResult.setSuccess(false);
            actionResult.setMsg("删除标签失败");
            return actionResult;
        }
        actionResult.setRefresh(true);
        actionResult.setMsg("删除标签成功");
        return actionResult;
    }


}
