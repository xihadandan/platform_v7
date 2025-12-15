/*
 * @(#)May 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.notice.web.action;

import com.wellsoft.pt.app.notice.support.NoticeUtils;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.support.ListViewSelection;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.ext.listview.web.action.ListViewStickAction;
import com.wellsoft.pt.dms.ext.support.ListViewRowData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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
 * May 22, 2017.1	zhulh		May 22, 2017		Create
 * </pre>
 * @date May 22, 2017
 */
@Action("平台公告_视图列表")
public class NoticeListViewAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -726630173040724517L;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private SecurityApiFacade securityApiFacade;

    @Autowired
    private ListViewStickAction listViewStickAction;

    /**
     * 置顶
     *
     * @param listViewSelection
     * @return
     */
    @ListViewActionConfig(name = "置顶", confirmMsg = "确认要置顶吗?")
    @ResponseBody
    public ActionResult stick(@RequestBody ListViewSelection listViewSelection) {
        List<ListViewRowData> queryItems = listViewSelection.getSelection(ListViewRowData.class);
        String formUuid = getActionContext().getFormUuid();
        for (ListViewRowData rowData : queryItems) {
            String dataUuid = rowData.getUuid();
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
            // 删除权限检查
            if (!NoticeUtils.isNoticeCreatorOrAdmin(dyFormData)) {
                throw new RuntimeException("您不是公告的发布人或公告管理员，不能置顶公告!");
            }
        }
        return listViewStickAction.stick(listViewSelection);
    }

    /**
     * 取消置顶
     *
     * @param listViewSelection
     * @return
     */
    @ListViewActionConfig(name = "取消置顶", confirmMsg = "确认要取消置顶吗?")
    @ResponseBody
    public ActionResult unstick(@RequestBody ListViewSelection listViewSelection) {
        List<ListViewRowData> queryItems = listViewSelection.getSelection(ListViewRowData.class);
        String formUuid = getActionContext().getFormUuid();
        for (ListViewRowData rowData : queryItems) {
            String dataUuid = rowData.getUuid();
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
            // 删除权限检查
            if (!NoticeUtils.isNoticeCreatorOrAdmin(dyFormData)) {
                throw new RuntimeException("您不是公告的发布人或公告管理员，不能取消置顶公告!");
            }
        }
        return listViewStickAction.unstick(listViewSelection);
    }

    /**
     * @param listViewSelection
     * @return
     */
    @ListViewActionConfig(name = "逻辑删除", confirmMsg = "确认要删除吗?")
    @ResponseBody
    public ActionResult logicDelete(@RequestBody ListViewSelection listViewSelection) {
        List<ListViewRowData> queryItems = listViewSelection.getSelection(ListViewRowData.class);
        String formUuid = getActionContext().getFormUuid();
        for (ListViewRowData rowData : queryItems) {
            String dataUuid = rowData.getUuid();
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
            // 删除权限检查
            if (!NoticeUtils.isNoticeCreatorOrAdmin(dyFormData)) {
                throw new RuntimeException("您不是公告的发布人或公告管理员，不能删除公告!");
            }
            if (dyFormData.isFieldExist("publish_status")) {
                dyFormData.setFieldValue("publish_status", "-1");
            }
            dyFormApiFacade.saveFormData(dyFormData);
        }

        ActionResult actionResult = createActionResult();
        actionResult.setMsg("删除成功!");
        actionResult.setRefresh(true);
        actionResult.addTriggerEvent("AppNotice.Change");
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ListViewActionConfig(name = "逻辑删除_还原", confirmMsg = "确认要还原吗?")
    @ResponseBody
    public ActionResult restore(@RequestBody ListViewSelection listViewSelection) {
        List<ListViewRowData> queryItems = listViewSelection.getSelection(ListViewRowData.class);
        String formUuid = getActionContext().getFormUuid();
        for (ListViewRowData rowData : queryItems) {
            String dataUuid = rowData.getUuid();
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
            if (dyFormData.isFieldExist("publish_status")) {
                dyFormData.setFieldValue("publish_status", "1");
            }
            dyFormApiFacade.saveFormData(dyFormData);
        }

        ActionResult actionResult = createActionResult();
        actionResult.setMsg("还原成功!");
        actionResult.setRefresh(true);
        actionResult.addTriggerEvent("AppNotice.Change");
        return actionResult;
    }

}
