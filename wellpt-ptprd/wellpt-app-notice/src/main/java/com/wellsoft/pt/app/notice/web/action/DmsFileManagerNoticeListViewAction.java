/*
 * @(#)May 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.notice.web.action;

import com.wellsoft.pt.app.notice.support.NoticeUtils;
import com.wellsoft.pt.dms.config.support.DmsFileManagerConfiguration;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.support.ListViewSelection;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.ext.listview.web.action.ListViewStickAction;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.model.DmsDyformListViewData;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
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
 * May 22, 2017.1	zhulh		May 22, 2017		Create
 * </pre>
 * @date May 22, 2017
 */
@Action("文件管理_平台公告_视图列表")
public class DmsFileManagerNoticeListViewAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -726630173040724517L;

    private static final String STICK_ERROR_MSG = "您不是公告的发布人或公告管理员，不能置顶公告!";

    private static final String UNSTICK_ERROR_MSG = "您不是公告的发布人或公告管理员，不能取消置顶公告!";

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private SecurityApiFacade securityApiFacade;

    @Autowired
    private ListViewStickAction listViewStickAction;

    /**
     * 通过文件置顶
     *
     * @param listViewSelection
     * @return
     */
    @ListViewActionConfig(name = "通过文件置顶", id = "btn_fm_notice_list_view_stick_by_file_uuid", confirmMsg = "确认要置顶吗?")
    @ResponseBody
    public ActionResult stickByFileUuid(@RequestBody ListViewSelection listViewSelection) {
        DmsFileManagerConfiguration dmsFileManagerConfiguration = (DmsFileManagerConfiguration) getActionContext()
                .getConfiguration();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String folderUuid = dmsFileManagerConfiguration.getBelongToFolderUuid();
        List<DmsDyformListViewData> dmsDyformListViewDatas = listViewSelection
                .getSelection(DmsDyformListViewData.class);
        List<Object> stickDataList = new ArrayList<Object>();
        for (DmsDyformListViewData rowData : dmsDyformListViewDatas) {
            String fileUuid = rowData.getUuid();
            DmsFile dmsFile = dmsFileServiceFacade.getFile(fileUuid);
            if (dmsFile == null) {
                throw new RuntimeException(STICK_ERROR_MSG);
            }
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(dmsFile.getDataDefUuid(), dmsFile.getDataUuid());
            // 删除权限检查
            if (!NoticeUtils.isNoticeCreatorOrAdmin(userId, folderUuid, dyFormData)) {
                throw new RuntimeException(STICK_ERROR_MSG);
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("formUuid", dyFormData.getFormUuid());
            map.put("uuid", dyFormData.getDataUuid());
            stickDataList.add(map);
        }
        return listViewStickAction.stick(new ListViewSelection(stickDataList));
    }

    /**
     * 通过文件取消置顶
     *
     * @param listViewSelection
     * @return
     */
    @ListViewActionConfig(name = "通过文件取消置顶", id = "btn_fm_notice_list_view_unstick_by_file_uuid", confirmMsg = "确认要取消置顶吗?")
    @ResponseBody
    public ActionResult unstickByFileUuid(@RequestBody ListViewSelection listViewSelection) {
        DmsFileManagerConfiguration dmsFileManagerConfiguration = (DmsFileManagerConfiguration) getActionContext()
                .getConfiguration();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String folderUuid = dmsFileManagerConfiguration.getBelongToFolderUuid();
        List<DmsDyformListViewData> dmsDyformListViewDatas = listViewSelection
                .getSelection(DmsDyformListViewData.class);
        List<Object> unstickDataList = new ArrayList<Object>();
        for (DmsDyformListViewData rowData : dmsDyformListViewDatas) {
            String fileUuid = rowData.getUuid();
            DmsFile dmsFile = dmsFileServiceFacade.getFile(fileUuid);
            if (dmsFile == null) {
                throw new RuntimeException(STICK_ERROR_MSG);
            }
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(dmsFile.getDataDefUuid(), dmsFile.getDataUuid());
            // 删除权限检查
            if (!NoticeUtils.isNoticeCreatorOrAdmin(userId, folderUuid, dyFormData)) {
                throw new RuntimeException(UNSTICK_ERROR_MSG);
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("formUuid", dyFormData.getFormUuid());
            map.put("uuid", dyFormData.getDataUuid());
            unstickDataList.add(map);
        }
        return listViewStickAction.unstick(new ListViewSelection(unstickDataList));
    }

    /**
     * 置顶
     *
     * @param listViewSelection
     * @return
     */
    @ListViewActionConfig(name = "置顶", id = "btn_fm_notice_list_view_stick", singleSelect = true, confirmMsg = "确认要置顶吗?")
    @ResponseBody
    public ActionResult stick(@RequestBody ListViewSelection listViewSelection) {
        DmsFileManagerConfiguration dmsFileManagerConfiguration = (DmsFileManagerConfiguration) getActionContext()
                .getConfiguration();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String folderUuid = dmsFileManagerConfiguration.getBelongToFolderUuid();
        List<DmsDyformListViewData> dmsDyformListViewDatas = listViewSelection
                .getSelection(DmsDyformListViewData.class);
        for (DmsDyformListViewData rowData : dmsDyformListViewDatas) {
            String formUuid = rowData.getFormUuid();
            String dataUuid = rowData.getUuid();
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
            // 删除权限检查
            if (!NoticeUtils.isNoticeCreatorOrAdmin(userId, folderUuid, dyFormData)) {
                throw new RuntimeException(STICK_ERROR_MSG);
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
    @ListViewActionConfig(name = "取消置顶", id = "btn_fm_notice_list_view_unstick", confirmMsg = "确认要取消置顶吗?")
    @ResponseBody
    public ActionResult unstick(@RequestBody ListViewSelection listViewSelection) {
        DmsFileManagerConfiguration dmsFileManagerConfiguration = (DmsFileManagerConfiguration) getActionContext()
                .getConfiguration();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String folderUuid = dmsFileManagerConfiguration.getBelongToFolderUuid();
        List<DmsDyformListViewData> dmsDyformListViewDatas = listViewSelection
                .getSelection(DmsDyformListViewData.class);
        for (DmsDyformListViewData rowData : dmsDyformListViewDatas) {
            String formUuid = rowData.getFormUuid();
            String dataUuid = rowData.getUuid();
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
            // 删除权限检查
            if (!NoticeUtils.isNoticeCreatorOrAdmin(userId, folderUuid, dyFormData)) {
                throw new RuntimeException(UNSTICK_ERROR_MSG);
            }
        }
        return listViewStickAction.unstick(listViewSelection);
    }

}
