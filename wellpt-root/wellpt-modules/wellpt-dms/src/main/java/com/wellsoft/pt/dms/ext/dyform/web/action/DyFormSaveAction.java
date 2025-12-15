/*
 * @(#)Feb 16, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService;
import com.wellsoft.pt.dms.ext.support.ActionDataCustomizer;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
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
public class DyFormSaveAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8633711363007286173L;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private DyFormActionService dyFormActionService;

    @ActionConfig(name = "保存", id = DyFormActions.ACTION_SAVE)
    @ResponseBody
    public ActionResult actionPerformed(@RequestBody DyFormActionData dyFormActionData) {
        // 代理对象操作表单
        // UfLmCmClothing proxy =
        // dyFormActionData.getDyFormDataProxy(UfLmCmClothing.class);
        // Integer sortNum = proxy.getSortNum();
        // if (sortNum == null) {
        // sortNum = 0;
        // }
        // sortNum++;
        // proxy.setName(proxy.getName() + "_proxy");
        // proxy.setSortNum(sortNum);
        // String dataUuid = dyFormActionData.saveDyFormDataProxy(proxy);

        boolean isVersioning = getActionContext().getConfiguration().isEnableVersioning();
        String dataUuid = null;
        if (isVersioning) {
            dataUuid = dyFormActionService.saveVersion(getActionContext(), dyFormActionData);
        } else {
            dataUuid = dyFormActionService.save(dyFormActionData.getDyFormData());
        }

        ActionResult actionResult = createActionResult();
        actionResult.addAppendUrlParam("idValue", dataUuid);
        actionResult.addAppendUrlParam(DyFormGetAction.KEY_VIEW_MODE, "0");
        if (StringUtils.isNotBlank(dyFormActionData.getExtraString(DyFormGetAction.KEY_VIEW_MODE))) {
            actionResult.addAppendUrlParam(DyFormGetAction.KEY_VIEW_MODE,
                    dyFormActionData.getExtraString(DyFormGetAction.KEY_VIEW_MODE));
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("formUuid", dyFormActionData.getDyFormData().getFormUuid());
        data.put("dataUuid", dataUuid);
        actionResult.setData(data);
        actionResult.setTriggerEvents(ActionDataCustomizer.getTriggerEvents(dyFormActionData));
        actionResult.setClose(ActionDataCustomizer.isClose(dyFormActionData));
        actionResult.setRefresh(true);
        actionResult.setRefreshParent(ActionDataCustomizer.isRefreshParent(dyFormActionData, true));
        actionResult.setMsg(ActionDataCustomizer.getSuccessMsg(dyFormActionData, "保存成功！"));
        actionResult.setExecuteJsModule(ActionDataCustomizer.getExecuteJsModule(dyFormActionData, StringUtils.EMPTY));
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "保存并验证", id = DyFormActions.ACTION_SAVE_WIDTH_VALIDATE, validate = true)
    @ResponseBody
    public ActionResult saveWithValidate(@RequestBody DyFormActionData dyFormActionData) {
        return actionPerformed(dyFormActionData);
    }

}
