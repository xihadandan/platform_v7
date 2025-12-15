/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.listview.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.support.ListViewSelection;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.core.web.action.OpenViewAction;
import com.wellsoft.pt.dms.ext.support.ListViewRowData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 20, 2017.1	zhulh		Feb 20, 2017		Create
 * </pre>
 * @date Feb 20, 2017
 */
@Action("视图列表")
public class ListViewStickAction extends OpenViewAction {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5230478380296586020L;

    private static final String STICK_TRUE_VALUE = "1";
    private static final String STICK_FALSE_VALUE = "0";

    @Autowired
    private DyFormFacade dyFormApiFacade;

    /**
     * 置顶
     *
     * @param listViewSelection
     * @return
     */
    @ListViewActionConfig(name = "置顶", id = ListViewActions.ACTION_STICK, confirmMsg = "确认要置顶吗?")
    @ResponseBody
    public ActionResult stick(@RequestBody ListViewSelection listViewSelection) {
        boolean isStick = getActionContext().getConfiguration().isEnableStick();
        if (Boolean.FALSE.equals(isStick)) {
            throw new RuntimeException("数据管理未启用置顶，不能进行置顶操作!");
        }

        String stickStatusField = getActionContext().getConfiguration().getStickStatusField();
        String stickTimeField = getActionContext().getConfiguration().getStickTimeField();
        if (StringUtils.isBlank(stickStatusField)) {
            throw new RuntimeException("数据管理置顶未配置，不能进行置顶操作!");
        }

        List<ListViewRowData> queryItems = listViewSelection.getSelection(ListViewRowData.class);
        String formUuid = getActionContext().getFormUuid();
        for (ListViewRowData rowData : queryItems) {
            String dataUuid = rowData.getUuid();
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
            dyFormData.setFieldValue(stickStatusField, STICK_TRUE_VALUE);
            dyFormData.setFieldValue(stickTimeField, Calendar.getInstance().getTime());
            dyFormApiFacade.saveFormData(dyFormData);
        }

        ActionResult actionResult = createActionResult();
        actionResult.setRefresh(true);
        actionResult.setMsg("置顶成功!");
        return actionResult;
    }

    /**
     * 取消置顶
     *
     * @param listViewSelection
     * @return
     */
    @ListViewActionConfig(name = "取消置顶", id = ListViewActions.ACTION_UNSTICK, confirmMsg = "确认要取消置顶吗?")
    @ResponseBody
    public ActionResult unstick(@RequestBody ListViewSelection listViewSelection) {
        boolean isStick = getActionContext().getConfiguration().isEnableStick();
        if (Boolean.FALSE.equals(isStick)) {
            throw new RuntimeException("数据管理未启用置顶，不能进行取消置顶操作!");
        }

        String stickStatusField = getActionContext().getConfiguration().getStickStatusField();
        String stickTimeField = getActionContext().getConfiguration().getStickTimeField();
        if (StringUtils.isBlank(stickStatusField)) {
            throw new RuntimeException("数据管理置顶未配置，不能进行取消置顶操作!");
        }

        List<ListViewRowData> queryItems = listViewSelection.getSelection(ListViewRowData.class);
        String formUuid = getActionContext().getFormUuid();
        for (ListViewRowData rowData : queryItems) {
            String dataUuid = rowData.getUuid();
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);

            // 所选数据置顶状态判断
            Object stickFieldValue = dyFormData.getFieldValue(stickStatusField);
            if (stickFieldValue != null && !stickFieldValue.toString().equals(STICK_TRUE_VALUE)) {
                throw new RuntimeException("所选数据不处于置顶状态，不能进行取消置顶操作!");
            }

            dyFormData.setFieldValue(stickStatusField, STICK_FALSE_VALUE);
            dyFormData.setFieldValue(stickTimeField, null);
            dyFormApiFacade.saveFormData(dyFormData);
        }

        ActionResult actionResult = createActionResult();
        actionResult.setRefresh(true);
        actionResult.setMsg("取消置顶成功!");
        return actionResult;
    }
}
