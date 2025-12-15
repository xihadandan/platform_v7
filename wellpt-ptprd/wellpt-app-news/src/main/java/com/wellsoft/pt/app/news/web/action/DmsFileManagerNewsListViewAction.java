/*
 * @(#)May 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.news.web.action;

import com.wellsoft.pt.app.news.support.NewsUtils;
import com.wellsoft.pt.dms.config.support.DmsFileManagerConfiguration;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.support.ListViewSelection;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.ext.listview.web.action.ListViewStickAction;
import com.wellsoft.pt.dms.model.DmsDyformListViewData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
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
@Action("文件管理_平台新闻_视图列表")
public class DmsFileManagerNewsListViewAction extends ActionSupport {

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
    @ListViewActionConfig(name = "置顶", id = "btn_fm_news_list_view_stick", singleSelect = true, confirmMsg = "确认要置顶吗?")
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
            if (!NewsUtils.isNewsCreatorOrAdmin(userId, folderUuid, dyFormData)) {
                throw new RuntimeException("您不是新闻的发布人或新闻管理员，不能置顶新闻!");
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
    @ListViewActionConfig(name = "取消置顶", id = "btn_fm_news_list_view_unstick", confirmMsg = "确认要取消置顶吗?")
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
            if (!NewsUtils.isNewsCreatorOrAdmin(userId, folderUuid, dyFormData)) {
                throw new RuntimeException("您不是新闻的发布人或新闻管理员，不能取消置顶新闻!");
            }
        }
        return listViewStickAction.unstick(listViewSelection);
    }

}
