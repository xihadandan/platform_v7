/*
 * @(#)Jan 22, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.filemanager.web.action;

import com.wellsoft.pt.dms.config.support.DmsFileManagerConfiguration;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.support.ListViewSelection;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.core.web.action.OpenViewAction;
import com.wellsoft.pt.dms.ext.filemanager.service.FileManagerListViewActionService;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dms.model.DmsFileDyformListViewRowData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * Jan 22, 2018.1	zhulh		Jan 22, 2018		Create
 * </pre>
 * @date Jan 22, 2018
 */
@Action("文件管理_数据列表")
public class FileManagerListViewAction extends OpenViewAction {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5366923572096199891L;

    @Autowired
    private FileManagerListViewActionService fileManagerListViewActionService;

    @ListViewActionConfig(name = "表格套打", id = FileManagerActions.LIST_VIEW_ACTION__AS_PRINTS, executeJsModule = "DmsListViewTablePrintAction", confirmMsg = "确认要套打吗?")
    @ResponseBody
    public ActionResult actionPerformed(@RequestBody ListViewSelection listViewSelection, HttpServletRequest request) {

        ActionResult actionResult = createActionResult();
        return actionResult;
    }

    /**
     * @param pkValue
     * @param request
     * @param response
     * @param model
     * @return
     */
    @ListViewActionConfig(name = "新建文档", id = FileManagerActions.LIST_VIEW_ACTION_CREATE_DOCUMENT, executeJsModule = "DmsFileManagerListViewCreateDocumentAction")
    public String gotoCreateDocument(String pkValue, HttpServletRequest request, HttpServletResponse response,
                                     Model model) {
        return open(pkValue, request, response, model);
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ListViewActionConfig(name = "通过数据UUID删除", id = FileManagerActions.LIST_VIEW_ACTION_LOGICAL_DELETE_BY_DATA_UUID, confirmMsg = "确认要删除吗?")
    @ResponseBody
    public ActionResult logicDeleteByDataUuid(@RequestBody ListViewSelection listViewSelection) {
        List<DmsFileDyformListViewRowData> dmsDyformListViewDatas = listViewSelection
                .getSelection(DmsFileDyformListViewRowData.class);
        fillFolderUuid(dmsDyformListViewDatas);
        fileManagerListViewActionService.logicDeleteByFolderUuidAndDataUuid(dmsDyformListViewDatas);

        ActionResult actionResult = createActionResult();
        actionResult.setRefresh(true);
        actionResult.setMsg("删除成功！");
        return actionResult;
    }

    /**
     * @param pkValue
     * @param request
     * @param response
     * @param model
     * @return
     */
    @ListViewActionConfig(name = "通过数据UUID恢复", id = FileManagerActions.LIST_VIEW_ACTION_RESTORE_BY_DATA_UUID, confirmMsg = "确认要还原吗?")
    @ResponseBody
    public ActionResult restoreByDataUuid(@RequestBody ListViewSelection listViewSelection) {
        List<DmsFileDyformListViewRowData> dmsDyformListViewDatas = listViewSelection
                .getSelection(DmsFileDyformListViewRowData.class);
        fillFolderUuid(dmsDyformListViewDatas);
        fileManagerListViewActionService.restoreByFolderUuidAndDataUuid(dmsDyformListViewDatas);

        ActionResult actionResult = createActionResult();
        actionResult.setRefresh(true);
        actionResult.setMsg("恢复成功！");
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ListViewActionConfig(name = "通过数据UUID彻底删除", id = FileManagerActions.LIST_VIEW_ACTION_PHYSICAL_DELETE_BY_DATA_UUID, confirmMsg = "确认要彻底删除吗?")
    @ResponseBody
    public ActionResult physicalDeleteByDataUuid(@RequestBody ListViewSelection listViewSelection) {
        List<DmsFileDyformListViewRowData> dmsDyformListViewDatas = listViewSelection
                .getSelection(DmsFileDyformListViewRowData.class);
        fillFolderUuid(dmsDyformListViewDatas);
        fileManagerListViewActionService.physicalDeleteByFolderUuidAndDataUuid(dmsDyformListViewDatas);

        ActionResult actionResult = createActionResult();
        actionResult.setRefresh(true);
        actionResult.setMsg("彻底删除成功！");
        return actionResult;
    }

    /**
     * @param dmsDyformListViewDatas
     */
    private void fillFolderUuid(List<DmsFileDyformListViewRowData> dmsDyformListViewDatas) {
        DmsFileManagerConfiguration dmsFileManagerConfiguration = (DmsFileManagerConfiguration) getActionContext()
                .getConfiguration();
        String folderUuid = dmsFileManagerConfiguration.getBelongToFolderUuid();
        for (DmsFileDyformListViewRowData rowData : dmsDyformListViewDatas) {
            if (StringUtils.isBlank(rowData.getFolderUuid())) {
                rowData.setFolderUuid(folderUuid);
            }
        }
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ListViewActionConfig(name = "删除", id = FileManagerActions.LIST_VIEW_ACTION_LOGICAL_DELETE, confirmMsg = "确认要删除吗?")
    @ResponseBody
    public ActionResult logicDelete(@RequestBody ListViewSelection listViewSelection) {
        List<DmsFile> dmsFiles = listViewSelection.getSelection(DmsFile.class);
        fileManagerListViewActionService.logicalDelete(dmsFiles);

        ActionResult actionResult = createActionResult();
        actionResult.setRefresh(true);
        actionResult.setMsg("删除成功！");
        return actionResult;
    }

    /**
     * @param pkValue
     * @param request
     * @param response
     * @param model
     * @return
     */
    @ListViewActionConfig(name = "恢复", id = FileManagerActions.LIST_VIEW_ACTION_RESTORE, confirmMsg = "确认要恢复吗?")
    @ResponseBody
    public ActionResult restore(@RequestBody ListViewSelection listViewSelection) {
        List<DmsFile> dmsFiles = listViewSelection.getSelection(DmsFile.class);
        fileManagerListViewActionService.restore(dmsFiles);

        ActionResult actionResult = createActionResult();
        actionResult.setRefresh(true);
        actionResult.setMsg("恢复成功！");
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ListViewActionConfig(name = "彻底删除", id = FileManagerActions.LIST_VIEW_ACTION_PHYSICAL_DELETE, confirmMsg = "确认要彻底删除吗?")
    @ResponseBody
    public ActionResult physicalDelete(@RequestBody ListViewSelection listViewSelection) {
        List<DmsFile> dmsFiles = listViewSelection.getSelection(DmsFile.class);
        fileManagerListViewActionService.physicalDelete(dmsFiles);

        ActionResult actionResult = createActionResult();
        actionResult.setRefresh(true);
        actionResult.setMsg("彻底删除成功！");
        return actionResult;
    }

}
