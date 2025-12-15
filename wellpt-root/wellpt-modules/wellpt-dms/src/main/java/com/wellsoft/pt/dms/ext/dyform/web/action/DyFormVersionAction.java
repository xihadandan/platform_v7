/*
 * @(#)Feb 16, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.support.DyformDocumentData;
import com.wellsoft.pt.dms.core.web.ActionProxy;
import com.wellsoft.pt.dms.core.web.View;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.core.web.action.OpenViewActionSupport;
import com.wellsoft.pt.dms.core.web.view.DyFormView;
import com.wellsoft.pt.dms.entity.DmsDataVersion;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormVersionActionService;
import com.wellsoft.pt.dms.ext.support.ActionDataCustomizer;
import com.wellsoft.pt.dms.facade.api.DmsDataVersionFacade;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class DyFormVersionAction extends OpenViewActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3149186709712822464L;

    // 版本格式
    private static final DecimalFormat versionFormat = new DecimalFormat("0.0");

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private DyFormVersionActionService dyFormVersionActionService;

    @Autowired
    private DmsDataVersionFacade dmsDataVersionFacade;

    /**
     * 格式化版本显示
     *
     * @param versions
     */
    private static List<DmsDataVersion> formatVersion(List<DmsDataVersion> versions) {
        List<DmsDataVersion> dataVersions = BeanUtils.convertCollection(versions, DmsDataVersion.class);
        for (DmsDataVersion version : dataVersions) {
            version.setVersion(versionFormat.format(Double.valueOf(version.getVersion())));
        }
        return dataVersions;
    }

    @ActionConfig(name = "保存新版本", id = DyFormActions.ACTION_SAVE_NEW_VERSION)
    @ResponseBody
    public ActionResult saveNewVersion(@RequestBody DyFormActionData dyFormActionData) {
        return saveNewVersionWithRemark(dyFormActionData);
    }

    @ActionConfig(name = "保存新版本并验证", id = DyFormActions.ACTION_SAVE_NEW_VERSION_WIDTH_VALIDATE, validate = true)
    @ResponseBody
    public ActionResult saveNewVersionWidthValidate(@RequestBody DyFormActionData dyFormActionData) {
        return saveNewVersionWithRemark(dyFormActionData);
    }

    @ActionConfig(name = "保存新版本加备注", id = DyFormActions.ACTION_SAVE_NEW_VERSION_WITH_REMARK, executeJsModule = "DmsDyformSaveNewVersionWithRemarkAction")
    @ResponseBody
    public ActionResult saveNewVersionWithRemark(@RequestBody DyFormActionData dyFormActionData) {
        String title = getActionContext().getDocumentTitle(dyFormActionData.getDyFormData());
        String remark = dyFormActionData.getExtraString("remark");
        DyFormData dyFormData = dyFormVersionActionService.saveNewVersion(title, remark, dyFormActionData);
        String targetFormUuid = dyFormData.getFormUuid();
        String targetDataUuid = dyFormApiFacade.saveFormData(dyFormData);

        ActionResult actionResult = createActionResult();
        actionResult.addAppendUrlParam("idValue", targetDataUuid);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("formUuid", targetFormUuid);
        data.put("dataUuid", targetDataUuid);
        actionResult.setTriggerEvents(ActionDataCustomizer.getTriggerEvents(dyFormActionData));
        actionResult.setClose(ActionDataCustomizer.isClose(dyFormActionData));
        actionResult.setRefresh(true);
        actionResult.setRefreshParent(true);
        actionResult.setMsg(ActionDataCustomizer.getSuccessMsg(dyFormActionData, "保存成功！"));
        return actionResult;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.action.Action#actionPerformed(com.wellsoft.pt.dms.core.action.ActionEvent)
     */
    @ActionConfig(name = "查看历史版本", id = DyFormActions.ACTION_VIEW_HISTORY_VERSION, executeJsModule = "DmsDyformViewHistoryVersionAction")
    @ResponseBody
    public ActionResult viewHistoryVersion(@RequestBody DyFormActionData dyFormActionData) {
        List<DmsDataVersion> versions = dyFormVersionActionService.getAllVersion(dyFormActionData.getFormUuid(),
                dyFormActionData.getDataUuid());

        Collections.sort(versions, IdEntityComparators.CREATE_TIME_DESC);

        List<DmsDataVersion> returnVersions = formatVersion(versions);

        ActionResult actionResult = createActionResult();
        actionResult.setData(returnVersions);
        return actionResult;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.action.Action#actionPerformed(com.wellsoft.pt.dms.core.action.ActionEvent)
     */
    @ActionConfig(name = "打开查看版本", id = "btn_dyform_open_version")
    public String openVersion(@RequestParam("v_id") String versionUuid, HttpServletRequest request,
                              HttpServletResponse response, Model model) {
        DmsDataVersion dataVersion = dmsDataVersionFacade.getVersion(versionUuid);
        View view = new DyFormView(dataVersion.getDataDefUuid(), dataVersion.getDataUuid());
        try {
            ActionContext actionContext = getActionContext();
            List<ActionProxy> actions = actionContext.getActions();
            actionContext.setActions(actions);

            view.loadActionData(model, actionContext, request, response);

            mergeOutputModel(model, actionContext, request, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return view.getViewName();
    }

    @ActionConfig(name = "获取版本数据", id = "btn_dyform_get_version_data")
    @ResponseBody
    public DyformDocumentData getVersionData(@RequestBody DyFormActionData dyFormActionData, HttpServletRequest request) {
        String formUuid = dyFormActionData.getFormUuid();
        String dataUuid = dyFormActionData.getDataUuid();
        DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
        return new DyformDocumentData(dyFormData, true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.action.Action#actionPerformed(com.wellsoft.pt.dms.core.action.ActionEvent)
     */
    @ActionConfig(name = "删除所有版本", id = DyFormActions.ACTION_DELETE_ALL_VERSION, confirmMsg = "确认要删除所有版本吗?")
    @ResponseBody
    public ActionResult deleteAllVersion(@RequestBody DyFormActionData dyFormActionData) {
        dyFormVersionActionService.deleteAllVersion(dyFormActionData.getFormUuid(), dyFormActionData.getDataUuid());

        ActionResult actionResult = createActionResult();
        actionResult.setRefreshParent(true);
        actionResult.setClose(true);
        actionResult.setMsg("删除成功!");
        return actionResult;
    }

}
