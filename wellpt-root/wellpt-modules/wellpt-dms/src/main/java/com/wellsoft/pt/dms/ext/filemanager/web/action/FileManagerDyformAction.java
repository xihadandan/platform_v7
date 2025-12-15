/*
 * @(#)Jan 22, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.filemanager.web.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.dms.config.support.DmsFileManagerConfiguration;
import com.wellsoft.pt.dms.config.support.Store;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.support.DyformDocumentData;
import com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData;
import com.wellsoft.pt.dms.core.web.ActionProxy;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.core.web.action.OpenViewAction;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.ext.dyform.web.action.DyFormActions;
import com.wellsoft.pt.dms.ext.filemanager.service.FileManagerDyformActionService;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService;
import com.wellsoft.pt.dms.file.service.DmsFileActionService;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dms.model.DmsFileAction;
import com.wellsoft.pt.dms.model.DmsFolderConfiguration;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dms.support.FileStatus;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
@Action("文件管理_表单数据")
public class FileManagerDyformAction extends OpenViewAction {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5366923572096199891L;

    @Autowired
    private FileManagerDyformActionService fileManagerDyformActionService;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private ReadMarkerService readMarkerService;

    @Autowired
    private DmsFileActionService dmsFileActionService;

    @Autowired
    private DmsFileService dmsFileService;

    @Autowired
    private DmsFileManagerService dmsFileManagerService;

    private String KEY_FILE_VIEW_MODE = "ep_file_view_mode";

    private String VALUE_FILE_VIEW_MODE = "0";

    private String VALUE_FILE_EDIT_MODE = "1";

    /**
     * @param dmsFileActions
     * @param fileAction
     * @return
     */
    private static boolean hasFolderPermission(List<DmsFileAction> dmsFileActions, String fileAction) {
        for (DmsFileAction dmsFileAction : dmsFileActions) {
            if (StringUtils.equals(fileAction, dmsFileAction.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param dmsFileActions
     * @param associatedFileAction
     * @return
     */
    private static boolean hasConfigActionPermission(List<DmsFileAction> dmsFileActions, String configFileAction) {
        for (DmsFileAction dmsFileAction : dmsFileActions) {
            // 查看属性
            if ("viewAttributes".equals(configFileAction)
                    && (StringUtils.startsWith(dmsFileAction.getId(), FileActions.VIEW_FILE_ATTRIBUTES)
                    || StringUtils.startsWith(dmsFileAction.getId(), FileActions.VIEW_FOLDER_ATTRIBUTES))) {
                return true;
            }
            // 排除 删除夹(有子夹、文件不可删除)
            if (StringUtils.startsWith(dmsFileAction.getId(), configFileAction)
                    && !dmsFileAction.getId().equals(FileActions.DELETE_FOLDER)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param pkValue
     * @param request
     * @param response
     * @param model
     * @return
     */
    @ActionConfig(name = "新建文档", id = FileManagerActions.DYFORM_ACTION_CREATE)
    public String gotoCreate(String pkValue, HttpServletRequest request, HttpServletResponse response, Model model) {
        return open(pkValue, request, response, model);
    }

    /**
     * 加载表单数据
     * 1、从文件库视图进入，fileUuid不为空
     * 2、从表单视图进入，fileUuid为空，尝试通过表单数据加载文档
     *
     * @return
     */
    @ActionConfig(name = "加载表单数据", id = FileManagerActions.DYFORM_ACTION_GET)
    @ResponseBody
    public DyformDocumentData getData(@RequestBody FileManagerDyFormActionData dyFormActionData,
                                      HttpServletRequest request) {
        // 加载指定夹的数据定义
        loadFileStoreIfRequire(dyFormActionData);

        ActionContext actionContext = getActionContext();
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String folderUuid = dyFormActionData.getFolderUuid();
        String fileUuid = dyFormActionData.getFileUuid();
        String formUuid = dyFormActionData.getFormUuid();
        String dataUuid = dyFormActionData.getDataUuid();

        DmsFileEntity dmsFileEntity = null;
        // 文档UUID不为空，加载文档对应的表单数据
        if (StringUtils.isNotBlank(fileUuid)) {
            dmsFileEntity = dmsFileService.get(fileUuid);
            formUuid = dmsFileEntity.getDataDefUuid();
            dataUuid = dmsFileEntity.getDataUuid();
        }
        // 文档为空，表单数据不为空，尝试通过表单数据加载文档
        if (dmsFileEntity == null && StringUtils.isNotBlank(folderUuid) && StringUtils.isNotBlank(dataUuid)) {
            dmsFileEntity = dmsFileService.getByFolderUuidAndDataUuid(folderUuid, dataUuid);
        }

        // 启用阅读记录时，记录阅读人员
        boolean isEnableReadRecord = actionContext.getConfiguration().isEnableReadRecord();
        String readRecordField = actionContext.getConfiguration().getReadRecordField();

        // 文件查看模式，1编辑
        String epFileViewMode = dyFormActionData.getExtraString(KEY_FILE_VIEW_MODE);

        // 夹的操作权限
        List<DmsFileAction> dmsFileActions = dmsFileActionService.getFolderActions(folderUuid);

        // 显示为文本
        boolean displayAsLabel = false;
        // 编辑权限判断
        boolean hasEditPermission = false;
        // 新建权限判断
        boolean hasCreatePermission = hasFolderPermission(dmsFileActions, FileActions.CREATE_DOCUMENT);
        List<ActionProxy> buildActions = new ArrayList<ActionProxy>();
        if (StringUtils.isBlank(dataUuid) && hasCreatePermission) {
            buildActions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_SAVE_AS_NORMAL));
            buildActions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_SAVE_AS_DRAFT));
            actionContext.removeAction(DyFormActions.ACTION_VIEW_READ_RECORD);
            actionContext.removeAction(DyFormActions.ACTION_VIEW_HISTORY_VERSION);
            actionContext.removeAction(DyFormActions.ACTION_SAVE_NEW_VERSION);
            actionContext.removeAction(DyFormActions.ACTION_SAVE_NEW_VERSION_WITH_REMARK);
            actionContext.removeAction(DyFormActions.ACTION_DELETE);
            actionContext.removeAction(DyFormActions.ACTION_DELETE_ALL_VERSION);
        } else if (dmsFileEntity != null) {
            // 编辑权限判断
            hasEditPermission = hasFolderPermission(dmsFileActions, FileActions.EDIT_FILE);
            // 夹没有编辑文档的权限时，判断对文件数据是否有编辑权限(文件编辑者字段)
            if (!hasEditPermission) {
                hasEditPermission = dmsFileService.isFileEditor(userDetails.getUserId(), dmsFileEntity.getUuid());
            }
            // 删除权限判断
            boolean hasDeletePermission = hasFolderPermission(dmsFileActions, FileActions.DELETE_FILE);
            // 进入编辑模式
            if (VALUE_FILE_EDIT_MODE.equals(epFileViewMode)) {
                if (hasEditPermission) {
                    buildActions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_SAVE_AS_NORMAL));
                }
                // 草稿状态
                if (FileStatus.DRAFT.endsWith(dmsFileEntity.getStatus())) {
                    buildActions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_SAVE_AS_DRAFT));
                }
            } else {
                // 草稿状态
                if (FileStatus.DRAFT.endsWith(dmsFileEntity.getStatus())) {
                    if (hasCreatePermission || hasEditPermission) {
                        buildActions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_SAVE_AS_NORMAL));
                        buildActions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_SAVE_AS_DRAFT));
                    }
                } else {
                    if (hasEditPermission) {
                        buildActions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_EDIT));
                    }
                    if (hasDeletePermission) {
                        buildActions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_DELETE));
                    }
                    // 添加查看阅读记录
                    if (isEnableReadRecord && StringUtils.isNotBlank(readRecordField)) {
                        buildActions.add(actionContext.buildAction(DyFormActions.ACTION_VIEW_READ_RECORD));
                    }
                    // 非草稿状态，显示为文本状态
                    displayAsLabel = true;
                }
            }
        } else {
            // 文档为空，通过夹及数据UUID，判断是否有编辑者权限
            // 编辑权限判断
            hasEditPermission = hasFolderPermission(dmsFileActions, FileActions.EDIT_FILE);
            // 夹没有编辑文档的权限时，判断对文件数据是否有编辑权限(文件编辑者字段)
            if (!hasEditPermission && StringUtils.isNotBlank(dataUuid)) {
                hasEditPermission = dmsFileService.isFileEditor(userDetails.getUserId(), folderUuid, dataUuid);
            }
            // 进入编辑模式
            if (VALUE_FILE_EDIT_MODE.equals(epFileViewMode)) {
                if (hasEditPermission) {
                    buildActions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_SAVE_AS_NORMAL));
                }
            } else {
                if (hasEditPermission) {
                    buildActions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_EDIT));
                }
                // 添加查看阅读记录
                if (isEnableReadRecord) {
                    buildActions.add(actionContext.buildAction(DyFormActions.ACTION_VIEW_READ_RECORD));
                }
                // 显示为文本
                displayAsLabel = true;
            }
        }

        DyFormData dyFormData = null;
        // 显示为文本时使用展示单据进行展示
        Store store = actionContext.getConfiguration().getStore(actionContext, request);
        if (displayAsLabel && StringUtils.isNotBlank(store.getDisplayFormUuid())) {
            dyFormData = dyFormApiFacade.getDyFormData(store.getDisplayFormUuid(), dataUuid);
        } else {
            dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
        }
        DyformDocumentData documentData = new DyformDocumentData(dyFormData, displayAsLabel);

        // 删除状态的文档要先还原才能进行操作
        if (isFileDeleted(folderUuid, dmsFileEntity, dyFormData)) {
            List<ActionProxy> actions = actionContext.getActions();
            actions.clear();
            if (hasFolderPermission(dmsFileActions, FileActions.CREATE_FILE)
                    || hasFolderPermission(dmsFileActions, FileActions.CREATE_DOCUMENT)) {
                actions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_RESTORE));
            }
            if (hasFolderPermission(dmsFileActions, FileActions.DELETE_FILE)) {
                actions.add(actionContext.buildAction(FileManagerActions.DYFORM_ACTION_PHYSICAL_DELETE));
            }
            documentData.setActions(actions);
        } else {
            List<ActionProxy> configActions = actionContext.getActions();
            // 合并页面配置的关联文件库权限的操作
            buildActions = mergeActions(configActions, buildActions, dmsFileActions, displayAsLabel);
            documentData.setActions(buildActions);
        }

        // 显示文本时判断是否可以送审批
        if (displayAsLabel && hasEditPermission && StringUtils.isNotBlank(folderUuid)) {
            DmsFolderConfiguration dmsFolderConfiguration = dmsFileManagerService.getFolderConfiguration(folderUuid);
            if (StringUtils.isNotBlank(dmsFolderConfiguration.getApproveFlowDefIds())) {
                ActionProxy sendToApproveAction = actionContext.buildAction(DyFormActions.ACTION_SEND_TO_APPROVE);
                sendToApproveAction.getProperties().put("approveFlowDefIds",
                        dmsFolderConfiguration.getApproveFlowDefIds());
                sendToApproveAction.getProperties().put("approveFlowDefNames",
                        dmsFolderConfiguration.getApproveFlowDefNames());
                if (!buildActions.contains(sendToApproveAction)) {
                    buildActions.add(sendToApproveAction);
                }
            }
        }

        if (Boolean.TRUE.equals(isEnableReadRecord) && StringUtils.isNotBlank(dataUuid)) {
            readMarkerService.markRead(dataUuid, SpringSecurityUtils.getCurrentUserId());
        }
        return documentData;
    }

    /**
     * @param folderUuid
     * @param dmsFileEntity
     * @param dyFormData
     * @return
     */
    private boolean isFileDeleted(String folderUuid, DmsFileEntity dmsFileEntity, DyFormData dyFormData) {
        if (dmsFileEntity != null && StringUtils.equals(FileStatus.DELETE, dmsFileEntity.getStatus())) {
            return true;
        }
        DmsFileManagerConfiguration dmsFileManagerConfiguration = (DmsFileManagerConfiguration) getActionContext()
                .getConfiguration();
        String fileStatusField = dmsFileManagerConfiguration.getFileStatusField(folderUuid);
        if (StringUtils.isNotBlank(fileStatusField) && dyFormData != null && dyFormData.isFieldExist(fileStatusField)) {
            String fileStatus = ObjectUtils.toString(dyFormData.getFieldValue(fileStatusField), StringUtils.EMPTY);
            return StringUtils.equals(FileStatus.DELETE, fileStatus);
        }
        return false;
    }

    /**
     * @param actions
     * @param buildInActions
     * @param dmsFileActions
     * @return
     */
    private List<ActionProxy> mergeActions(List<ActionProxy> configActions, List<ActionProxy> buildInActions,
                                           List<DmsFileAction> dmsFileActions, boolean displayAsLabel) {
        if (isOldConfigActions(configActions)) {
            return mergeOldConfigActions(configActions, buildInActions);
        } else {
            return mergeConfigActions(configActions, buildInActions, dmsFileActions, displayAsLabel);
        }
    }

    /**
     * @param configActions
     * @param buildInActions
     * @param dmsFileActions
     * @param displayAsLabel
     * @return
     */
    private List<ActionProxy> mergeConfigActions(List<ActionProxy> configActions, List<ActionProxy> buildInActions,
                                                 List<DmsFileAction> dmsFileActions, boolean displayAsLabel) {
        Set<ActionProxy> returnActionSet = Sets.newLinkedHashSet();
        Set<ActionProxy> removeActionSet = Sets.newLinkedHashSet();
        // 添加配置的操作
        returnActionSet.addAll(configActions);
        // 添加内置的操作，有配置编码与ID一样的内置操作会忽略掉，按配置去处理
        returnActionSet.addAll(buildInActions);
        for (ActionProxy configAction : returnActionSet) {
            Map<String, Object> properties = configAction.getProperties();
            // 文档状态判断
            String associatedDocEditModel = ObjectUtils.toString(properties.get("associatedDocEditModel"),
                    StringUtils.EMPTY);
            // 文件库权限判断
            String associatedFileAction = ObjectUtils.toString(properties.get("associatedFileAction"),
                    StringUtils.EMPTY);

            // 忽然掉内置的操作
            if (StringUtils.isBlank(associatedDocEditModel) && StringUtils.isBlank(associatedFileAction)) {
                continue;
            }

            // 表单在显示文本状态下去掉编辑模式的按钮
            if (displayAsLabel) {
                if (StringUtils.equals(VALUE_FILE_EDIT_MODE, associatedDocEditModel)) {
                    removeActionSet.add(configAction);
                }
            } else {
                // 表单在编辑状态下去掉查看模式的按钮
                if (StringUtils.equals(VALUE_FILE_VIEW_MODE, associatedDocEditModel)) {
                    removeActionSet.add(configAction);
                }
            }

            // 去除无文件库权限的按钮
            if (StringUtils.isNotBlank(associatedFileAction)) {
                if (!hasConfigActionPermission(dmsFileActions, associatedFileAction)) {
                    removeActionSet.add(configAction);
                }
            }
        }

        // 删除不满足条件的操作
        returnActionSet.removeAll(removeActionSet);

        return Arrays.asList(returnActionSet.toArray(new ActionProxy[0]));
    }

    /**
     * @param configActions
     * @param buildInActions
     * @return
     */
    private List<ActionProxy> mergeOldConfigActions(List<ActionProxy> configActions, List<ActionProxy> buildInActions) {
        List<ActionProxy> returnActions = Lists.newArrayList();
        returnActions.addAll(configActions);
        for (ActionProxy buidInAction : buildInActions) {
            if (!returnActions.contains(buidInAction)) {
                returnActions.add(buidInAction);
            }
        }
        List<ActionProxy> removeActions = new ArrayList<ActionProxy>();
        for (ActionProxy configAction : configActions) {
            String associatedFileAction = (String) configAction.getProperties().get("associatedFileAction");
            if (!StringUtils.equals(associatedFileAction, "1")) {
                continue;
            }
            ActionProxy buildAction = getAction(buildInActions, configAction.getId());
            if (buildAction == null) {
                removeActions.add(configAction);
            }
        }
        returnActions.removeAll(removeActions);
        return returnActions;
    }

    /**
     * @param configActions
     * @return
     */
    private boolean isOldConfigActions(List<ActionProxy> configActions) {
        for (ActionProxy configAction : configActions) {
            Map<String, Object> properties = configAction.getProperties();
            String associatedFileAction = ObjectUtils.toString(properties.get("associatedFileAction"),
                    StringUtils.EMPTY);
            if ("-1".equals(associatedFileAction) || "1".equals(associatedFileAction)
                    || "0".equals(associatedFileAction)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param buildActions
     * @param id
     * @return
     */
    private ActionProxy getAction(List<ActionProxy> buildActions, String id) {
        for (ActionProxy buildAction : buildActions) {
            if (StringUtils.equals(buildAction.getId(), id)) {
                return buildAction;
            }
        }
        return null;
    }

    /**
     * 加载指定夹的数据定义
     *
     * @param actionContext
     * @param folderUuid
     */
    private void loadFileStoreIfRequire(FileManagerDyFormActionData dyFormActionData) {
        DmsFileManagerConfiguration dmsFileManagerConfiguration = (DmsFileManagerConfiguration) getActionContext()
                .getConfiguration();
        dmsFileManagerConfiguration.loadStore(dyFormActionData.getFolderUuid());
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "套打", id = FileManagerActions.DYFORM_ACTION_AS_PRINT, executeJsModule = "DmsDyformPrintAction")
    @ResponseBody
    public ActionResult asPrint(@RequestBody FileManagerDyFormActionData dyFormActionData) {

        ActionResult actionResult = createActionResult();
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "保存为草稿", id = FileManagerActions.DYFORM_ACTION_SAVE_AS_DRAFT)
    @ResponseBody
    public ActionResult saveAsDraft(@RequestBody FileManagerDyFormActionData dyFormActionData) {
        // 加载指定夹的数据定义
        loadFileStoreIfRequire(dyFormActionData);

        DmsFile dmsFile = fileManagerDyformActionService.saveAsDraft(dyFormActionData, getActionContext());

        ActionResult actionResult = createActionResult();
        // 文件UUID
        actionResult.addAppendUrlParam("doc_id", dmsFile.getUuid());
        actionResult.addAppendUrlParam("idValue", dmsFile.getDataUuid());
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("formUuid", dyFormActionData.getDyFormData().getFormUuid());
        data.put("dataUuid", dmsFile.getDataUuid());
        actionResult.setData(data);
        actionResult.setRefresh(true);
        actionResult.setRefreshParent(true);
        actionResult.setMsg("保存成功！");
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "保存为正稿", id = FileManagerActions.DYFORM_ACTION_SAVE_AS_NORMAL, validate = true)
    @ResponseBody
    public ActionResult saveAsNormal(@RequestBody FileManagerDyFormActionData dyFormActionData) {
        // 加载指定夹的数据定义
        loadFileStoreIfRequire(dyFormActionData);

        DmsFile dmsFile = fileManagerDyformActionService.saveAsNormal(dyFormActionData, getActionContext());

        // 启用阅读记录时，标记为新的未阅
        boolean isEnableReadRecord = getActionContext().getConfiguration().isEnableReadRecord();
        if (isEnableReadRecord) {
            readMarkerService.markNew(dmsFile.getDataUuid());
        }

        String resultMsg = dyFormActionData.getExtraString("resultMsg");
        if (StringUtils.isBlank(resultMsg)) {
            resultMsg = "保存成功！";
        }
        ActionResult actionResult = createActionResult();
        // 文件UUID
        actionResult.addAppendUrlParam("doc_id", dmsFile.getUuid());
        actionResult.addAppendUrlParam("idValue", dmsFile.getDataUuid());
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("formUuid", dyFormActionData.getDyFormData().getFormUuid());
        data.put("dataUuid", dmsFile.getDataUuid());
        actionResult.setData(data);
        // actionResult.addAppendUrlParam(KEY_FILE_VIEW_MODE, "0");
        actionResult.setClose(true);
        actionResult.setRefreshParent(true);
        actionResult.setMsg(resultMsg);
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "编辑", id = FileManagerActions.DYFORM_ACTION_EDIT)
    @ResponseBody
    public ActionResult edit(@RequestBody FileManagerDyFormActionData dyFormActionData) {
        ActionResult actionResult = createActionResult();
        actionResult.addAppendUrlParam(KEY_FILE_VIEW_MODE, VALUE_FILE_EDIT_MODE);
        actionResult.setRefresh(true);
        actionResult.setShowMsg(false);
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "删除", id = FileManagerActions.DYFORM_ACTION_DELETE, confirmMsg = "确认要删除吗？")
    @ResponseBody
    public ActionResult delete(@RequestBody FileManagerDyFormActionData dyFormActionData) {
        fileManagerDyformActionService.delete(dyFormActionData);
        ActionResult actionResult = createActionResult();
        actionResult.setClose(true);
        actionResult.setRefreshParent(true);
        actionResult.setMsg("删除成功！");
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "还原", id = FileManagerActions.DYFORM_ACTION_RESTORE, confirmMsg = "确认要还原吗？")
    @ResponseBody
    public ActionResult restore(@RequestBody FileManagerDyFormActionData dyFormActionData) {
        fileManagerDyformActionService.restore(dyFormActionData);
        ActionResult actionResult = createActionResult();
        actionResult.setClose(false);
        actionResult.setRefresh(true);
        actionResult.setRefreshParent(true);
        actionResult.setMsg("还原成功！");
        return actionResult;
    }

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "彻底删除", id = FileManagerActions.DYFORM_ACTION_PHYSICAL_DELETE, confirmMsg = "确认要彻底删除吗？")
    @ResponseBody
    public ActionResult physicalDelete(@RequestBody FileManagerDyFormActionData dyFormActionData) {
        fileManagerDyformActionService.physicalDelete(dyFormActionData);
        ActionResult actionResult = createActionResult();
        actionResult.setClose(true);
        actionResult.setRefreshParent(true);
        actionResult.setMsg("彻底删除成功！");
        return actionResult;
    }

}
