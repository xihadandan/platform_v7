/*
 * @(#)Feb 19, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.listview.web.action;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.support.ListViewSelection;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormVersionActionService;
import com.wellsoft.pt.dms.ext.support.ListViewRowData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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
 * Feb 19, 2017.1	zhulh		Feb 19, 2017		Create
 * </pre>
 * @date Feb 19, 2017
 */
@Action("视图列表")
public class ListViewDeleteAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2235364431236199641L;

    @Autowired
    private DyFormActionService dyFormActionService;
    @Autowired
    private DyFormVersionActionService dyFormVersionActionService;

    @ListViewActionConfig(name = "逻辑删除", id = ListViewActions.ACTION_LOGIC_DELETE, confirmMsg = "确认要删除吗?")
    @ResponseBody
    public ActionResult logicDelete(@RequestBody ListViewSelection listViewSelection) {
        List<ListViewRowData> queryItems = listViewSelection.getSelection(ListViewRowData.class);
        String globalFormUuid = getActionContext().getFormUuid();
        String statusField = listViewSelection.getExtraString("statusField");
        String statusValue = listViewSelection.getExtraString("statusValue");
        if (StringUtils.isBlank(statusField)) {
            statusField = "status";
        }
        if (StringUtils.isBlank(statusValue)) {
            statusValue = "-1";
        }
        dyFormActionService.logicDelete(queryItems, statusField, statusValue, globalFormUuid);

        ActionResult actionResult = createActionResult();
        actionResult.setRefresh(true);
        actionResult.setMsg("删除成功!");
        return actionResult;
    }

    @ListViewActionConfig(name = "删除", id = ListViewActions.ACTION_DELETE, confirmMsg = "确认要删除吗?")
    @ResponseBody
    public ActionResult actionPerformed(@RequestBody ListViewSelection listViewSelection) {
        List<ListViewRowData> queryItems = listViewSelection.getSelection(ListViewRowData.class);
        String globalFormUuid = getActionContext().getFormUuid();
        boolean isVersioning = getActionContext().getConfiguration().isEnableVersioning();
        dyFormActionService.delete(queryItems, globalFormUuid, isVersioning);

        ActionResult actionResult = createActionResult();
        // 触发的事件
        if (StringUtils.isNotBlank(listViewSelection.getExtraString("ep_triggerEvents"))) {
            actionResult.setTriggerEvents(Lists.newArrayList(StringUtils.split(
                    listViewSelection.getExtraString("ep_triggerEvents"), Separator.SEMICOLON.getValue())));
            List<String> dataUuids = Lists.newArrayList();
            for (ListViewRowData listViewRowData : queryItems) {
                dataUuids.add(listViewRowData.getUuid());
            }
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("dataUuid", StringUtils.join(dataUuids, Separator.SEMICOLON.getValue()));
            data.put("selection", listViewSelection.getSelection());
            actionResult.setData(data);
        }
        actionResult.setRefresh(true);
        actionResult.setMsg("删除成功!");
        return actionResult;
    }

    @ListViewActionConfig(name = "删除所有版本", id = ListViewActions.ACTION_DELETE_ALL_DATA_VERSION, confirmMsg = "确认要删除吗?")
    @ResponseBody
    public ActionResult deleteAllListDataVersion(@RequestBody ListViewSelection listViewSelection) {
        List<ListViewRowData> queryItems = listViewSelection.getSelection(ListViewRowData.class);
        String globalFormUuid = getActionContext().getFormUuid();
        for (ListViewRowData rowData : queryItems) {
            String formUuid = rowData.getFormUuid();
            if (StringUtils.isBlank(formUuid)) {
                formUuid = globalFormUuid;
            }
            if (StringUtils.isBlank(rowData.getUuid())) {
                ActionResult actionResult = createActionResult();
                actionResult.setRefresh(false);
                actionResult.setSuccess(false);
                actionResult.setMsg("删除失败!数据UUID为空!");
                return actionResult;
            }
            dyFormVersionActionService.deleteAllVersion(formUuid, rowData.getUuid());
        }
        ActionResult actionResult = createActionResult();
        actionResult.setRefresh(true);
        actionResult.setMsg("删除成功!");
        return actionResult;
    }
}
